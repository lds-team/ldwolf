package bingo.com.dialog;

import android.app.TimePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import bingo.com.R;
import bingo.com.base.baseview.BaseDialog;
import bingo.com.controller.DeleteController;
import bingo.com.controller.UpdateController;
import bingo.com.enumtype.LotoType;
import bingo.com.helperdb.CongnoDBHelper;
import bingo.com.helperdb.ContactColumnMaps;
import bingo.com.helperdb.ContactDBHelper;
import bingo.com.helperdb.MessageDBHelper;
import bingo.com.helperdb.db.ContactDatabase;
import bingo.com.screen.configscreen.SettingsHelper;
import bingo.com.utils.Utils;
import butterknife.Bind;
import butterknife.OnClick;

public class InfoContactDialog extends BaseDialog implements View.OnClickListener {

    private final int MAX_LENGTH = 3;

    @Bind(R.id.contactinfo_name)
    TextView name;

    @Bind(R.id.contactinfo_number)
    TextView number;

    @Deprecated
    @Bind(R.id.contactinfo_daudb)
    EditText daudb;

    @Deprecated
    @Bind(R.id.contactinfo_daudb_lanan)
    EditText lanan_Daudb;

    @Bind(R.id.contactinfo_ditdb)
    EditText ditdb;

    @Bind(R.id.contactinfo_ditdb_lanan)
    EditText lanan_Ditdb;

    @Deprecated
    @Bind(R.id.contactinfo_daunhat)
    EditText daunhat;

    @Deprecated
    @Bind(R.id.contactinfo_daunhat_lanan)
    EditText lanan_daunhat;

    @Deprecated
    @Bind(R.id.contactinfo_ditnhat)
    EditText ditnhat;

    @Deprecated
    @Bind(R.id.contactinfo_ditnhat_lanan)
    EditText lananDitnhat;

    @Bind(R.id.contactinfo_lo)
    EditText lo;

    @Bind(R.id.contactinfo_lo_lanan)
    EditText lanan_lo;

    @Bind(R.id.contactinfo_xien2)
    EditText xien2;

    @Bind(R.id.contactinfo_xien2_lanan)
    EditText lanan_xien2;

    @Bind(R.id.contactinfo_xien3)
    EditText xien3;

    @Bind(R.id.contactinfo_xien3_lanan)
    EditText lanan_xien3;

    @Bind(R.id.contactinfo_xien4)
    EditText xien4;

    @Bind(R.id.contactinfo_xien4_lanan)
    EditText lanan_xien4;

    @Bind(R.id.contactinfo_3cang)
    EditText bacang;

    @Bind(R.id.contactinfo_3cang_lanan)
    EditText lanan_bacang;

    @Bind(R.id.contactinfo_khach)
    RadioButton khach;

    @Bind(R.id.contactinfo_chu)
    RadioButton chu;

    //==============

    @Bind(R.id.contactinfo_gia)
    LinearLayout layoutGia;

    @Bind(R.id.contactinfo_giu)
    LinearLayout layoutGiu;

    @Bind(R.id.contactinfo_setgia)
    Button setGia;

    @Bind(R.id.contactinfo_setgiu)
    Button setGiu;

    @Bind(R.id.contactinfo_configlo)
    TextView timeLoXien;

    @Bind(R.id.contactinfo_configde)
    TextView timeDe;

    //==========

    @Bind(R.id.giutungkhach_seek1)
    EditText seekBarGiu1;

    @Bind(R.id.giutungkhach_max1)
    EditText seekBarMax1;

    @Bind(R.id.giutungkhach_seek2)
    EditText seekBarGiu2;

    @Bind(R.id.giutungkhach_max2)
    EditText seekBarMax2;

    @Bind(R.id.giutungkhach_seek3)
    EditText seekBarGiu3;

    @Bind(R.id.giutungkhach_max3)
    EditText seekBarMax3;

    @Bind(R.id.giutungkhach_seek4)
    EditText seekBarGiu4;

    @Bind(R.id.giutungkhach_max4)
    EditText seekBarMax4;

    @Bind(R.id.giutungkhach_seek5)
    EditText seekBarGiu5;

    @Bind(R.id.giutungkhach_max5)
    EditText seekBarMax5;

    @Bind(R.id.giutungkhach_seek6)
    EditText seekBarGiu6;

    @Bind(R.id.giutungkhach_max6)
    EditText seekBarMax6;

    private EditText[] arrFields;

    private Context context;

    private HashMap<Integer, Double> percentMaps;

    private String phone;
    private String nameofPhone;
    private String timeConfigDe;
    private String timeConfigLoXien;
    private boolean isAddNew;

    public InfoContactDialog(Context context, boolean notCancel, String name, String phone) {
        super(context, notCancel);
        this.context = context;

        init();

        this.phone = phone;
        this.nameofPhone = name;
        this.name.setText(name);
        this.number.setText(phone);
        khach.setChecked(true);
        isAddNew = true;

        initLayoutGiu();
    }

    public InfoContactDialog(Context context, boolean notCancel, Cursor cursor, int nameIndex, int numberIndex, int typeIndex, String... columns) {
        super(context, notCancel);
        this.context = context;

        init();

        try {
            if (cursor != null)
            {
                this.nameofPhone = cursor.getString(nameIndex);
                this.phone = cursor.getString(numberIndex);
                name.setText(nameofPhone);
                number.setText(phone);
                setTypeSelected(cursor.getString(typeIndex));

                for (int i = 0; i < columns.length; i++)
                {
                    arrFields[i].setText(cursor.getString(cursor.getColumnIndex(columns[i])));
                }
            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        initLayoutGiu();
    }

    private void init() {
        arrFields = new EditText[]{
                daudb, lanan_Daudb,
                ditdb, lanan_Ditdb,
                daunhat, lanan_daunhat,
                ditnhat, lananDitnhat,
                lo, lanan_lo,
                xien2, lanan_xien2,
                xien3, lanan_xien3,
                xien4, lanan_xien4,
                bacang, lanan_bacang
        };
    }

    private boolean initLayoutGiu() {
        if (phone != null)
        {
            setHesoGiu(phone);
        }
        else
        {
            dismiss();
        }

        timeConfigLoXien = SettingsHelper.getTimeReceiveLoXien(getSaved(), phone);
        timeLoXien.setText(timeConfigLoXien);

        timeConfigDe = SettingsHelper.getTimeReceiveDe(getSaved(), phone);
        timeDe.setText(timeConfigDe);


        EditText[] inputs = new EditText[]{seekBarGiu1, seekBarGiu2, seekBarGiu3, seekBarGiu4, seekBarGiu5, seekBarGiu6};

        for (EditText input : inputs)
        {
            input.setFilters(new InputFilter[] {new InputFilter.LengthFilter(MAX_LENGTH)});
        }

        return true;
    }

    @OnClick(R.id.contactinfo_configlo)
    public void changeTimeLoXien() {
        showPicker(true);
    }

    @OnClick(R.id.contactinfo_configde)
    public void changeTimeDe() {
        showPicker(false);
    }

    private void showPicker(final boolean isLoXien) {
        Calendar calendar = Calendar.getInstance();

        TimePickerDialog dialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                setTextTime(isLoXien, Utils.getTimeFrom(hourOfDay, minute));
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);

        dialog.show();

        calendar.clear();
    }

    private void setTextTime(boolean isLoXien, String textTime) {
        if (isLoXien)
        {
            timeLoXien.setText(textTime);
            this.timeConfigLoXien = textTime;
            SettingsHelper.saveTimeReceiveLoXien(getSaved(), phone, timeConfigLoXien);
        }
        else
        {
            timeDe.setText(textTime);
            this.timeConfigDe = textTime;
            SettingsHelper.saveTimeReceiveDe(getSaved(), phone, timeConfigDe);
        }
    }

    private void setHesoGiu(String phone) {
        if (phone != null)
        {
            percentMaps = ContactDBHelper.getHesoGiu(phone);

            seekBarGiu1.setText(String.valueOf((int) (percentMaps.get(ContactColumnMaps.GIU_DE) * 100)));
            seekBarGiu2.setText(String.valueOf((int) (percentMaps.get(ContactColumnMaps.GIU_LO) * 100)));
            seekBarGiu3.setText(String.valueOf((int) (percentMaps.get(ContactColumnMaps.GIU_XIEN2) * 100)));
            seekBarGiu4.setText(String.valueOf((int) (percentMaps.get(ContactColumnMaps.GIU_XIEN3) * 100)));
            seekBarGiu5.setText(String.valueOf((int) (percentMaps.get(ContactColumnMaps.GIU_XIEN4) * 100)));
            seekBarGiu6.setText(String.valueOf((int) (percentMaps.get(ContactColumnMaps.GIU_BACANG) * 100)));

            seekBarMax1.setText(String.valueOf(percentMaps.get(ContactColumnMaps.MAX_DE).intValue()));
            seekBarMax2.setText(String.valueOf(percentMaps.get(ContactColumnMaps.MAX_LO).intValue()));
            seekBarMax3.setText(String.valueOf(percentMaps.get(ContactColumnMaps.MAX_XIEN2).intValue()));
            seekBarMax4.setText(String.valueOf(percentMaps.get(ContactColumnMaps.MAX_XIEN3).intValue()));
            seekBarMax5.setText(String.valueOf(percentMaps.get(ContactColumnMaps.MAX_XIEN4).intValue()));
            seekBarMax6.setText(String.valueOf(percentMaps.get(ContactColumnMaps.MAX_BACANG).intValue()));
        }
    }

    private void setTypeSelected(String typeSelected) {
        if (typeSelected.equals(ContactDatabase.TYPE_CHU_NHAN_SO))
        {
            chu.setChecked(true);
            khach.setChecked(false);
        }
        else
        {
            chu.setChecked(false);
            khach.setChecked(true);
        }
    }

    private String geTypeSelected() {
        if (chu.isChecked())
            return ContactDatabase.TYPE_CHU_NHAN_SO;
        else
            return ContactDatabase.TYPE_KHACH_CHUYEN_SO;
    }

    private String[] getStringEditted() {
        String[] strings = new String[arrFields.length];

        for (int i = 0; i < arrFields.length; i++)
        {
            strings[i] = arrFields[i].getText().toString();
        }

        return strings;
    }

    @Override
    public int layout() {
        return R.layout.dialog_contactinfo;
    }

    @Override
    public void dismissed() {
        requestUpdate();
        releaseViews();
    }

    @OnClick({R.id.contactinfo_delete, R.id.contactinfo_add, R.id.giutungkhach_giubtn})
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.contactinfo_delete:
                ContactDBHelper.deleteContact(number.getText().toString());
                break;

            case R.id.contactinfo_add:
                ContactDBHelper.addContactWithOnConflict(number.getText().toString(), name.getText().toString(), geTypeSelected(), getStringEditted());

            case R.id.giutungkhach_giubtn:
                updateHesoGiu();
                break;

            default:break;
        }

        layoutGiu.postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        }, 300);
    }

    @OnClick({R.id.contactinfo_setgia, R.id.contactinfo_setgiu})
    public void changeLayoutType(View v) {
        switch (v.getId())
        {
            case R.id.contactinfo_setgia:
                layoutGiu.setVisibility(View.GONE);
                layoutGia.setVisibility(View.VISIBLE);
                setGia.setBackgroundResource(R.drawable.button_bg_active);
                setGiu.setBackgroundResource(R.drawable.button_bg_normal);
                setGia.setTextColor(Color.WHITE);
                setGiu.setTextColor(Color.BLACK);
                break;

            case R.id.contactinfo_setgiu:
                layoutGiu.setVisibility(View.VISIBLE);
                layoutGia.setVisibility(View.GONE);
                setGia.setBackgroundResource(R.drawable.button_bg_normal);
                setGiu.setBackgroundResource(R.drawable.button_bg_active);
                setGia.setTextColor(Color.BLACK);
                setGiu.setTextColor(Color.WHITE);
                break;

            default:break;
        }
    }

    private void updateHesoGiu() {
        if (!validateInput())
        {
            Utils.showAlert(contextWrap.get(), null, "Lỗi hệ số.", "Ok", null);
            return;
        }

        if (phone != null)
        {
            String giuDe = seekBarGiu1.getText().toString().isEmpty() ? "0" : seekBarGiu1.getText().toString();
            String giuLo = seekBarGiu2.getText().toString().isEmpty() ? "0" : seekBarGiu2.getText().toString();
            String giuXien2 = seekBarGiu3.getText().toString().isEmpty() ? "0" : seekBarGiu3.getText().toString();
            String giuXien3 = seekBarGiu4.getText().toString().isEmpty() ? "0" : seekBarGiu4.getText().toString();
            String giuXien4 = seekBarGiu5.getText().toString().isEmpty() ? "0" : seekBarGiu5.getText().toString();
            String giuBacang = seekBarGiu6.getText().toString().isEmpty() ? "0" : seekBarGiu6.getText().toString();

            String maxDe = seekBarMax1.getText().toString().isEmpty() ? "0" : seekBarMax1.getText().toString();
            String maxLo = seekBarMax2.getText().toString().isEmpty() ? "0" : seekBarMax2.getText().toString();
            String maxXien2 = seekBarMax3.getText().toString().isEmpty() ? "0" : seekBarMax3.getText().toString();
            String maxXien3 = seekBarMax4.getText().toString().isEmpty() ? "0" : seekBarMax4.getText().toString();
            String maxXien4 = seekBarMax5.getText().toString().isEmpty() ? "0" : seekBarMax5.getText().toString();
            String maxBacang = seekBarMax6.getText().toString().isEmpty() ? "0" : seekBarMax6.getText().toString();

            Log.d("GiuSoDialog", "updateHesoGiu: " + "Starting update he so.");

            String[] valueMaps = new String[]{
                    String.valueOf(Double.parseDouble(giuDe) / 100d),
                    String.valueOf(Double.parseDouble(giuLo) / 100d),
                    String.valueOf(Double.parseDouble(giuXien2) / 100d),
                    String.valueOf(Double.parseDouble(giuXien3) / 100d),
                    String.valueOf(Double.parseDouble(giuXien4) / 100d),
                    String.valueOf(Double.parseDouble(giuBacang) / 100d),
                    maxDe,
                    maxLo,
                    maxXien2,
                    maxXien3,
                    maxXien4,
                    maxBacang};


            ContactDBHelper.updateHesoGiu(phone,
                    new String[]{
                            ContactDatabase.GIU_DE,
                            ContactDatabase.GIU_LO,
                            ContactDatabase.GIU_XIEN2,
                            ContactDatabase.GIU_XIEN3,
                            ContactDatabase.GIU_XIEN4,
                            ContactDatabase.GIU_BACANG,
                            ContactDatabase.MAX_DE,
                            ContactDatabase.MAX_LO,
                            ContactDatabase.MAX_XIEN2,
                            ContactDatabase.MAX_XIEN3,
                            ContactDatabase.MAX_XIEN4,
                            ContactDatabase.MAX_BACANG},
                    valueMaps);

            //Disable change value before.
            /*if (!isAddNew) updateWhenChangeHesoGiu(nameofPhone, phone, valueMaps);*/

            Toast.makeText(context, "Lưu thay đổi thành công!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateWhenChangeHesoGiu(final String name, final String phone, final String[] valueMaps) {
        String[] typeChanged = putStringTypeChange(valueMaps);
        UpdateController.updateKeepValue(context, name, phone, Utils.getCurrentDate(), typeChanged);

        /*CommonTask.AsyncSingleThread.get().execute(new Runnable() {
            @Override
            public void run() {
                String[] typeChanged = putStringTypeChange(valueMaps);

                UpdateController.updateKeepValue(name, phone, Utils.getCurrentDate(), typeChanged *//*, percentMaps*//*);

                if (context instanceof BaseActivity)
                {
                    ((BaseActivity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "Giữ số thành công!", Toast.LENGTH_SHORT).show();
                            requestUpdate();
                        }
                    });
                }
            }
        }).clean();*/
    }

    private String[] putStringTypeChange(String[] valueMaps) {
        ArrayList<String> hesoChanged = new ArrayList<>();

        if (percentMaps != null)
        {
            if (percentMaps.get(ContactColumnMaps.getKeepColumn(LotoType.de)) != Double.parseDouble(valueMaps[0])
                    || percentMaps.get(ContactColumnMaps.getMaxColumn(LotoType.de)) != Double.parseDouble(valueMaps[6]))
            {
                hesoChanged.add(LotoType.de.name());
            }

            if (percentMaps.get(ContactColumnMaps.getKeepColumn(LotoType.lo)) != Double.parseDouble(valueMaps[1])
                    || percentMaps.get(ContactColumnMaps.getMaxColumn(LotoType.lo)) != Double.parseDouble(valueMaps[7]))
            {
                hesoChanged.add(LotoType.lo.name());
            }

            if (percentMaps.get(ContactColumnMaps.getKeepColumn(LotoType.xien2)) != Double.parseDouble(valueMaps[2])
                    || percentMaps.get(ContactColumnMaps.getMaxColumn(LotoType.xien2)) != Double.parseDouble(valueMaps[8]))
            {
                hesoChanged.add(LotoType.xien2.name());
            }

            if (percentMaps.get(ContactColumnMaps.getKeepColumn(LotoType.xien3)) != Double.parseDouble(valueMaps[3])
                    || percentMaps.get(ContactColumnMaps.getMaxColumn(LotoType.xien3)) != Double.parseDouble(valueMaps[9]))
            {
                hesoChanged.add(LotoType.xien3.name());
            }

            if (percentMaps.get(ContactColumnMaps.getKeepColumn(LotoType.xien4)) != Double.parseDouble(valueMaps[4])
                    || percentMaps.get(ContactColumnMaps.getMaxColumn(LotoType.xien4)) != Double.parseDouble(valueMaps[10]))
            {
                hesoChanged.add(LotoType.xien4.name());
            }

            if (percentMaps.get(ContactColumnMaps.getKeepColumn(LotoType.bacang)) != Double.parseDouble(valueMaps[5])
                    || percentMaps.get(ContactColumnMaps.getMaxColumn(LotoType.bacang)) != Double.parseDouble(valueMaps[11]))
            {
                hesoChanged.add(LotoType.bacang.name());
            }

            String[] arr = new String[hesoChanged.size()];

            return hesoChanged.toArray(arr);
        }

        return null;
    }

    private boolean validateInput() {
        EditText[] inputs = new EditText[]{seekBarGiu1, seekBarGiu2, seekBarGiu3, seekBarGiu4, seekBarGiu5, seekBarGiu6};

        for (EditText input : inputs)
        {
            if (TextUtils.isEmpty(input.getText().toString()))
            {
                continue;
            }

            if (Double.parseDouble(input.getText().toString()) > 100 || Double.parseDouble(input.getText().toString()) < 0)
            {
                return false;
            }
        }

        return true;
    }
}
