package bingo.com.dialog;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import bingo.com.R;
import bingo.com.base.baseview.BaseDialog;
import bingo.com.enumtype.LotoType;
import bingo.com.enumtype.TableType;
import bingo.com.enumtype.TypeMessage;
import bingo.com.helperdb.ContactColumnMaps;
import bingo.com.helperdb.ContactDBHelper;
import bingo.com.helperdb.FollowDBHelper;
import bingo.com.helperdb.MessageDBHelper;
import bingo.com.helperdb.db.ContactDatabase;
import bingo.com.helperdb.db.FollowDatabase;
import bingo.com.helperdb.db.MessageDatabase;
import bingo.com.helperdb.db2.FollowDBHelper2;
import bingo.com.model.ChooseMessage;
import bingo.com.model.MessageForm;
import bingo.com.utils.Keysaved;
import bingo.com.utils.LoaderSQLite;
import bingo.com.utils.Utils;
import bingo.com.utils.Validate;
import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import butterknife.OnTextChanged;

public class XuatSoDialog extends BaseDialog implements View.OnClickListener {

    public enum TYPE_TIEN_XUAT {
        XUAT_HET,
        XUAT_LON_HON,
        XUAT_PHAN_TRAM,
        XUAT_TIEN
    }

    public static int TYPE_XUAT_SO = 0;
    public static int TYPE_XUAT_TIN_NHAN = 1;

    @Bind(R.id.xuatso_relativespin)
    RelativeLayout topRelative;

    @Bind(R.id.xuatso_linearbtm)
    LinearLayout btmXuatso;

    @Bind(R.id.xuatso_xuatnhan)
    Button btmTinnhan;

    @Bind(R.id.xuatso_edittext)
    EditText editText;

    @Bind(R.id.xuatso_radiogroup)
    RadioGroup group;

    @Bind(R.id.xuatso_radiogroup2)
    RadioGroup groupType;

    @Bind(R.id.xuatso_spin)
    Spinner listCustomer;

    @Bind(R.id.xuatso_infocustomer)
    TextView customer;

    private Context context;

    private String mes = "";

    private int xuat;

    private int[] range;

    private TYPE_TIEN_XUAT type_tien_xuat;

    private Cursor cursor;

    public XuatSoDialog(Context context, Cursor cursor, boolean notCancel, int xuat, TYPE_TIEN_XUAT type_tien_xuat, int[] range) {
        super(context, notCancel);

        this.context = context;
        this.xuat = xuat;
        this.type_tien_xuat = type_tien_xuat;
        this.cursor = cursor;
        this.range = range;

        initListContact();

        setType(TYPE_XUAT_SO);
    }

    private void initListContact() {

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                context,
                android.R.layout.simple_spinner_dropdown_item,
                ContactDBHelper.getAllsChuNhanSo(),
                new String[]{ContactDatabase.NAME},
                new int[]{android.R.id.text1}, 0);

        listCustomer.setAdapter(adapter);
        listCustomer.setDropDownVerticalOffset(120);
        listCustomer.setSelection(0);
        listCustomer.setSelected(true);
    }

    @Override
    public void onShowDialog(DialogInterface dialog) {
        loadingAnim();
        mes = getMessage();
        dismissAnim();

        if (TextUtils.isEmpty(mes))
        {
            Utils.showAlert(context, null, "Không có số liệu để xuất.", "Ok", null);
            dismiss();
        }
        else
        {
            editText.setText(mes);
        }
    }

    @OnTextChanged(R.id.xuatso_edittext)
    public void afterTextChanged(Editable s) {
        mes = s.toString();
    }

    @Override
    public int layout() {
        return R.layout.dialog_dan_xuatso;
    }

    public void setType(int type) {
        if (type == TYPE_XUAT_SO)
        {
            group.check(R.id.xuatso_typexuatso);
            groupType.check(R.id.xuatso_xuatde);
            topRelative.setVisibility(View.VISIBLE);
            btmXuatso.setVisibility(View.VISIBLE);
            btmTinnhan.setVisibility(View.GONE);
        }
        else
        {
            group.check(R.id.xuatso_typetinnhan);
            groupType.check(R.id.xuatso_xuatde);
            topRelative.setVisibility(View.VISIBLE);
            btmXuatso.setVisibility(View.GONE);
            btmTinnhan.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void dismissed() {

    }

    @OnClick({R.id.xuatso_typexuatso, R.id.xuatso_typetinnhan, R.id.xuatso_xuatde, R.id.xuatso_xuatlo, R.id.xuatso_xuatxien, R.id.xuatso_xuatbacang})
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.xuatso_typexuatso:
                setType(TYPE_XUAT_SO);
                break;

            case R.id.xuatso_typetinnhan:
                setType(TYPE_XUAT_TIN_NHAN);
                break;

            /*case R.id.xuatso_xuatde:
                xuatType = LotoType.de;
                break;

            case R.id.xuatso_xuatlo:
                xuatType = LotoType.lo;
                break;

            case R.id.xuatso_xuatxien:
                xuatType = LotoType.xien;
                break;

            case R.id.xuatso_xuatbacang:
                xuatType = LotoType.bacang;
                break;*/

            default:break;
        }
    }

    @OnClick({R.id.xuatso_xuatdan, R.id.xuatso_xuatnhan})
    public void onButtonClick(View v) {
        String phone = null;
        String name = null;

        if (listCustomer.getSelectedItem() != null)
        {
            phone = ((Cursor) listCustomer.getSelectedItem()).getString(ContactColumnMaps.PHONE);
            name = ((Cursor) listCustomer.getSelectedItem()).getString(ContactColumnMaps.NAME);
        }

        if (Validate.canAnalyze(context, mes) != null)
        {
            Utils.showAlert(context, "Lỗi cú pháp", "Vui lòng chỉnh sửa lại tin nhắn!", "Ok", null);
            return;
        }

        editText.setEnabled(false);

        switch (v.getId())
        {
            case R.id.xuatso_xuatdan:
                extractClipboard(name, phone, mes);
                break;

            case R.id.xuatso_xuatnhan:
                extractMessage(name, phone, mes);
                break;

            default:break;
        }
    }

    public void importToDb(final String name, final String phone, final String mes) {

        /*final ResultReturnModel[] result = StatisticControlImport.getResultTodayAndYesterday(context);*/

        if (phone != null)
        {
            String time = Utils.getTimeWithCurrentConfigDate();

            //Import to messageDB.
            MessageDBHelper.addMessageSent(new ChooseMessage(
                    name,
                    phone,
                    time,
                    mes,
                    TypeMessage.SENT.name()
            ));

            //Import Direct To FollowDB.
            LoaderSQLite.startForceAnalyzing(context, name, phone, mes, Long.parseLong(time), TypeMessage.SENT.name(), null);

            requestUpdate();

            Toast.makeText(context, "Xuất tin nhắn thành công.", Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(context, "Lỗi! Chưa chọn chủ nhận số.", Toast.LENGTH_LONG).show();
        }
    }

    public void extractClipboard(String name, String phone, String mes) {
        importToDb(name, phone, mes);
        saveToClipboard(mes);
        dismiss();
    }

    public void extractMessage(final String name, final String phone, final String mes) {
        if (phone != null && !TextUtils.isEmpty(mes))
        {
            //Send info to sms default app.
            /*Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            sendIntent.putExtra("sms_body", mes);
            sendIntent.setData(Uri.parse("sms:" + phone));
            context.startActivity(sendIntent);*/

            //Send direct.
            Utils.showAlertTwoButton(context, "Tin nhắn gửi đến " + phone, mes, "Cancel", "Gửi", null, new Runnable() {
                @Override
                public void run() {
                    importToDb(name, phone, mes);

                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phone, null, mes, null, null);
                    dismiss();
                    Toast.makeText(context, "Message Sent", Toast.LENGTH_LONG).show();
                }
            });
        }

        if (phone == null)
        {
            Utils.showAlert(context, null, "Không có chủ nhận số", "Ok", null);
        }
    }

    private String getMessage() {
        int colType = cursor.getColumnIndex("type");
        int colNumber = cursor.getColumnIndex("number");
        int colRemain = cursor.getColumnIndex("sms");

        MessageForm from = new MessageForm.Builder(cursor,
                colType,
                colNumber,
                colRemain,
                xuat,
                type_tien_xuat,
                range
        ).build();

        return from.getMessage();
    }

    private void saveToClipboard(String text) {
        ClipboardManager manager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData data = ClipData.newPlainText(Keysaved.CLIPBOARD_BINGO, text);
        manager.setPrimaryClip(data);
    }

    @OnItemSelected(R.id.xuatso_spin)
    public void changeCustomer() {

        if (listCustomer.getSelectedItem() instanceof Cursor)
        {
            Cursor c = ((Cursor) listCustomer.getSelectedItem());

            String phone = c.getString(c.getColumnIndex(MessageDatabase.PHONE));

            String name = c.getString(c.getColumnIndex(MessageDatabase.NAME));

            customer.setText(name.concat(": ").concat(phone));

        }
    }
}
