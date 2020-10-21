package bingo.com.dialog;

import android.content.Context;
import android.database.Cursor;
import android.text.Editable;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import bingo.com.R;
import bingo.com.base.baseview.BaseDialog;
import bingo.com.customviews.TextViewDegit;
import bingo.com.enumtype.LotoType;
import bingo.com.helperdb.CongnoDBHelper;
import bingo.com.helperdb.ContactDBHelper;
import bingo.com.helperdb.db.CongnoDatabase;
import bingo.com.helperdb.db.ContactDatabase;
import bingo.com.helperdb.db.MessageDatabase;
import bingo.com.model.CongnoModel;
import bingo.com.utils.Utils;
import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import butterknife.OnTextChanged;

public class AddChargeDialog extends BaseDialog {

    private long newCongNo = 0;

    @Bind(R.id.dialog_charge_changed)
    EditText textChanged;

    @Bind(R.id.dialog_charge_customer)
    TextView customer;

    @Bind(R.id.dialog_charge_old)
    TextViewDegit oldCongNo;

    @Bind(R.id.dialog_charge_spin)
    Spinner listCustomer;

    private Context context;

    private String phone;
    private String name;
    private boolean isChu;

    private Cursor congnoCursor;

    public AddChargeDialog(Context context, boolean notCancel, Cursor cursor) {
        super(context, notCancel);

        this.context = context;
        this.congnoCursor = cursor;

        init();
    }

    private void init() {
        initListContact();

    }

    private void initListContact() {
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                context,
                android.R.layout.simple_spinner_dropdown_item,
                ContactDBHelper.getAlls(),
                new String[]{ContactDatabase.NAME},
                new int[]{android.R.id.text1}, 0);

        listCustomer.setAdapter(adapter);
        listCustomer.setDropDownVerticalOffset(120);
        listCustomer.setSelection(0);
        listCustomer.setSelected(true);
    }

    @Override
    public int layout() {
        return R.layout.dialog_charge;
    }

    @OnItemSelected(R.id.dialog_charge_spin)
    public void changeCustomer() {

        if (listCustomer.getSelectedItem() != null && listCustomer.getSelectedItem() instanceof Cursor)
        {
            Cursor c = ((Cursor) listCustomer.getSelectedItem());

            phone = c.getString(c.getColumnIndex(MessageDatabase.PHONE));

            name = c.getString(c.getColumnIndex(MessageDatabase.NAME));

            customer.setText(name.concat(": ").concat(phone));

            setOldCongNoValue();
        }
    }

    private void setOldCongNoValue() {
        listCustomer.setEnabled(false);

        congnoCursor.moveToFirst();
        while (!congnoCursor.isAfterLast())
        {
            if (congnoCursor.getString(congnoCursor.getColumnIndex("phone")).equals(phone))
            {
                isChu = !congnoCursor.getString(congnoCursor.getColumnIndex(CongnoDatabase.DETAIL_CUSTOMER)).equals("0");
                oldCongNo.setResult(isChu ? 0 - congnoCursor.getDouble(congnoCursor.getColumnIndex("sum")) :
                        congnoCursor.getDouble(congnoCursor.getColumnIndex("sum")));
                listCustomer.setEnabled(true);
                return;
            }

            congnoCursor.moveToNext();
        }

        oldCongNo.setResult(0);
        listCustomer.setEnabled(true);
    }

    @OnTextChanged(R.id.dialog_charge_changed)
    public void textChanged(Editable editable) {
        try {
            String edit = editable.toString();

            if (edit.matches(".*[-][-]+.*"))
            {
                edit = edit.replaceAll("[-]+", "-");
                textChanged.setText(edit);
                textChanged.setSelection(edit.length());
            }
            else if (edit.matches(".*[0-9][-]+[0-9].*"))
            {
                edit = edit.replaceAll("([0-9])[-]+([0-9])", "$1$2");
                textChanged.setText(edit);
                textChanged.setSelection(edit.length());
            }

            if (edit.replaceAll("-", "").length() > 18)
            {
                String old = edit.startsWith("-") ? String.valueOf(0 - newCongNo) : String.valueOf(newCongNo);
                textChanged.setText(old);
                textChanged.setSelection(old.length());
                Utils.showAlert(context, null, "Dộ dài vượt cho phép.!", "Ok", null);
                return;
            }

            if (!TextUtils.isEmpty(edit.replaceAll("-", "")))
            {
                String parse = edit.replaceAll("-", "");
                newCongNo = Long.parseLong(parse);

                if (edit.endsWith("-"))
                {
                    boolean startNega = edit.startsWith("-");
                    newCongNo = startNega ? newCongNo : 0 - newCongNo;
                    if (!String.valueOf(newCongNo).equals(editable.toString()))
                    {
                        textChanged.setText(String.valueOf(newCongNo));
                        textChanged.setSelection(startNega ? String.valueOf(newCongNo).length() : String.valueOf(newCongNo).length() + 1);
                    }
                }
            }
            else
            {
                newCongNo = 0;
            }
        } catch (Exception e) {
            Utils.showAlert(context, null, "Lỗi nhập số!", "Ok", null);
            textChanged.setText("");
        }
    }

    @OnClick(R.id.dialog_charge_create)
    public void changeCongNo() {
        loadingAnim();
        if (phone != null)
        {
            textChanged.setEnabled(false);
            boolean negative = textChanged.getText().toString().contains("-");
            CongnoDBHelper.deleteAllCustomerCongNo(phone);
            if (newCongNo != 0)
            {
                CongnoDBHelper.insert(new CongnoModel(name, phone, LotoType.unknown, 0, 0, Utils.getCurrentDate(), String.valueOf(negative ? 0 - newCongNo : newCongNo), isChu ? "1" : "0"));
            }
            requestUpdate();
        }
        else
        {
            Utils.showAlert(context, "Lỗi", "Chưa lựa chọn khách hàng.", "Dismiss", null);
        }

        dismissAnim();
        dismiss();
    }

    @Override
    public void dismissed() {
        releaseViews();
    }
}
