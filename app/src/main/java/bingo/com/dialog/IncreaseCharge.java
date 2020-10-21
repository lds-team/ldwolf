package bingo.com.dialog;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.widget.EditText;

import bingo.com.R;
import bingo.com.base.baseview.BaseDialog;
import bingo.com.enumtype.LotoType;
import bingo.com.helperdb.CongnoDBHelper;
import bingo.com.helperdb.ContactDBHelper;
import bingo.com.model.CongnoModel;
import bingo.com.utils.Utils;
import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class IncreaseCharge extends BaseDialog {

    private Context context;

    private long newCongNo = 0;

    @Bind(R.id.dialog_addcharge_changed)
    EditText textChanged;

    private String phone;
    private String name;
    private String date;
    private boolean isChu;

    public IncreaseCharge(Context context, boolean notCancel, String name, String phone, String date, boolean isChu) {
        super(context, notCancel);
        this.context = context;

        this.name = name;
        this.phone = phone;
        this.date = date;
        this.isChu = isChu;
    }

    @OnTextChanged(R.id.dialog_addcharge_changed)
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

    @OnClick(R.id.dialog_addcharge_create)
    public void changeCongNo() {
        loadingAnim();
        if (phone != null)
        {
            textChanged.setEnabled(false);
            boolean negative = textChanged.getText().toString().contains("-");
            if (newCongNo != 0)
            {
                CongnoDBHelper.insertWithOutConflict(new CongnoModel(name, phone, LotoType.unknown, 0, 0, date, String.valueOf(negative ? 0 - newCongNo : newCongNo), isChu ? "1" : "0"));
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
    public int layout() {
        return R.layout.dialog_addchargehistory;
    }

    @Override
    public void dismissed() {
        releaseViews();
    }
}
