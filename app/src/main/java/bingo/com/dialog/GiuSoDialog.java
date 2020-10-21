package bingo.com.dialog;

import android.content.Context;
import android.database.Cursor;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.bingo.analyze.VNCharacterUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

import bingo.com.R;
import bingo.com.async.CommonTask;
import bingo.com.base.BaseActivity;
import bingo.com.base.baseview.BaseDialog;
import bingo.com.controller.DeleteController;
import bingo.com.controller.InsertController;
import bingo.com.controller.UpdateController;
import bingo.com.enumtype.LotoType;
import bingo.com.helperdb.ContactColumnMaps;
import bingo.com.helperdb.ContactDBHelper;
import bingo.com.helperdb.db.ContactDatabase;
import bingo.com.pref.ConfigPreference;
import bingo.com.utils.Formats;
import bingo.com.utils.Utils;
import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnItemSelected;

//TODO case xien,bacang is set All percent keep?
public class GiuSoDialog extends BaseDialog {

    private final int MAX_LENGTH = 3;

    public enum TYPE_LAYOUT {
        DELO,
        XIENBACANG,
        GIUTUNGKHACH
    }

    @Bind(R.id.giuso_type_delo)
    RelativeLayout type1;

    @Bind(R.id.giuso_type_xien)
    ViewGroup type2;

    @Bind(R.id.giuso_type_giukhach)
    ViewGroup type3;

    @Bind(R.id.giutungkhach_spin)
    Spinner listCustomer2;

    @Bind(R.id.giuso_radiogroup)
    RadioGroup group;

    @Bind(R.id.suadangiuso_typede)
    RadioButton typeDelo_de;

    @Bind(R.id.giuso_seek1)
    EditText seekBarXien1;

    @Bind(R.id.giuso_seek2)
    EditText seekBarXien2;

    @Bind(R.id.giuso_seek3)
    EditText seekBarXien3;

    @Bind(R.id.giuso_seek4)
    EditText seekBarXien4;

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

    @Bind(R.id.suadangiuso_edittext)
    EditText editText;

    String oldContent;

    private TYPE_LAYOUT type = TYPE_LAYOUT.DELO;

    private Context context;

    //Only for type layout = DELO
    private String preSyntax = LotoType.de.name().concat(" ");

    private ArrayList<String> hesoChanged;

    private HashMap<Integer, Double> percentMaps;

    public GiuSoDialog(Context context, boolean notCancel, TYPE_LAYOUT type) {
        super(context, notCancel);

        this.context = context;
        this.type = type;

        setType(type);
        setMaxLength();
    }

    private void initListContact(Spinner spinner) {

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                context,
                android.R.layout.simple_spinner_dropdown_item,
                ContactDBHelper.getAllsKhachChuyenSo(),
                new String[]{ContactDatabase.NAME},
                new int[]{android.R.id.text1}, 0);

        spinner.setAdapter(adapter);
        spinner.setDropDownVerticalOffset(120);
        spinner.setSelection(0);
    }

    public void setType(TYPE_LAYOUT type) {
        this.type = type;

        if (type == TYPE_LAYOUT.DELO)
        {
            group.check(R.id.giuso_type1);

            type1.setVisibility(View.VISIBLE);
            type2.setVisibility(View.GONE);
            type3.setVisibility(View.GONE);

            typeDelo_de.setChecked(true);

            oldContent = ConfigPreference.getMessageGiuDe(getSaved());
            editText.setText(ConfigPreference.formatMessageSaved(getSaved(), preSyntax.trim()));
        }
        else if (type == TYPE_LAYOUT.XIENBACANG)
        {
            group.check(R.id.giuso_type2);

            type1.setVisibility(View.GONE);
            type2.setVisibility(View.VISIBLE);
            type3.setVisibility(View.GONE);

            setHesoGiu(ContactDBHelper.getRandomPhone(), TYPE_LAYOUT.XIENBACANG);
        }
        else
        {
            group.check(R.id.giuso_type3);

            type1.setVisibility(View.GONE);
            type2.setVisibility(View.GONE);
            type3.setVisibility(View.VISIBLE);

            initListContact(listCustomer2);

            String phone = listCustomer2.getSelectedItem() == null ? null: ((Cursor) listCustomer2.getSelectedItem()).getString(ContactColumnMaps.PHONE);

            if (phone != null)
            {
                setHesoGiu(phone, TYPE_LAYOUT.GIUTUNGKHACH);
            }
        }
    }

    private boolean setMaxLength() {
        EditText[] inputs = new EditText[]{seekBarGiu1, seekBarGiu2, seekBarGiu3, seekBarGiu4, seekBarGiu5, seekBarGiu6};

        for (EditText input : inputs)
        {
            input.setFilters(new InputFilter[] {new InputFilter.LengthFilter(MAX_LENGTH)});
        }

        return true;
    }

    @OnItemSelected({R.id.giutungkhach_spin, R.id.giuso_spin})
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String phone;

        if (type == TYPE_LAYOUT.GIUTUNGKHACH)
        {
            phone = ((Cursor) listCustomer2.getSelectedItem()).getString(ContactColumnMaps.PHONE);

            if (phone != null)
            {
                setHesoGiu(phone, TYPE_LAYOUT.GIUTUNGKHACH);
            }
        }
    }

    @SuppressWarnings("WrongConstant")
    @OnClick({R.id.giuso_type1, R.id.giuso_type2, R.id.giuso_type3, R.id.delo_suadan_btn, R.id.giutungkhach_giubtn, R.id.giuso_giubtn, R.id.suadangiuso_typede, R.id.suadangiuso_typelo, R.id.delo_xoadan})
    public void onClick(View v) {
        switch (v.getId())
        {
            //Change type Layout.
            case R.id.giuso_type1:
                setType(TYPE_LAYOUT.DELO);
                break;

            case R.id.giuso_type2:
                setType(TYPE_LAYOUT.XIENBACANG);
                break;

            case R.id.giuso_type3:
                setType(TYPE_LAYOUT.GIUTUNGKHACH);
                break;

            //Others.
            case R.id.delo_suadan_btn:
                suadan();
                break;

            case R.id.giutungkhach_giubtn:
                Toast.makeText(context, "Hệ thống đang update số liệu", Toast.LENGTH_SHORT).show();
                updateHesoGiu(TYPE_LAYOUT.GIUTUNGKHACH);
                type2.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dismiss();
                    }
                }, 300);
                break;

            case R.id.giuso_giubtn:
                Toast.makeText(context, "Hệ thống đang update số liệu", Toast.LENGTH_SHORT).show();
                updateHesoGiu(TYPE_LAYOUT.XIENBACANG);
                type3.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dismiss();
                        notifyUpdate();
                    }
                }, 300);
                break;

            //Choose pre syntax.
            case R.id.suadangiuso_typede:
                preSyntax = LotoType.de.name().concat(" ");
                oldContent = ConfigPreference.getMessageGiuDe(getSaved());
                editText.setText(ConfigPreference.formatMessageSaved(getSaved(), preSyntax.trim()));
                break;

            case R.id.suadangiuso_typelo:
                preSyntax = LotoType.lo.name().concat(" ");
                oldContent = ConfigPreference.getMessageGiuLo(getSaved());
                editText.setText(ConfigPreference.formatMessageSaved(getSaved(), preSyntax.trim()));
                break;

            //
            case R.id.delo_xoadan:
                editText.setText("");
                saveMessageKeepDeLo();

                try {
                    DeleteController.deleteKeepDeLo(new JSONArray(oldContent), preSyntax.trim(), Utils.getCurrentDate());

                    Toast.makeText(context, "Xóa thành công.", Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    Toast.makeText(context, "Lỗi khi xóa.", Toast.LENGTH_SHORT).show();
                }

                requestUpdate();
                dismiss();
                break;

            default:break;
        }
    }

    private void suadan() {
        String text = editText.getText().toString();

        JSONArray jsonArray;

        try {

            jsonArray = new JSONArray(oldContent);

        } catch (JSONException e) {
            jsonArray = new JSONArray();
        }

        if (!TextUtils.isEmpty(text))
        {
            text = VNCharacterUtils.removeAccent(text).toLowerCase();
            text = text.replaceAll("^\\W*", "");
            text = text.startsWith("de|lo") ? text : preSyntax.concat(text);

            //Import Direct To FollowDB.
            String error = InsertController.insertKeepMessageDelo(Utils.getCurrentDate(), text, jsonArray, getSaved());

            if (error != null)
            {
                Spanned last;

                if (!TextUtils.isEmpty(error))
                {
                    error = error.replace(LotoType.de.name(), "").replace(LotoType.lo.name(), "");

                    last = Formats.formatStringError(text, error);
                }
                else
                {
                    last = Formats.getHtmlText("", text, "#FF0000");
                }

                editText.setText(last);
            }
            else
            {
                requestUpdate();
                dismiss();

                Toast.makeText(context, "Giữ số thành công.", Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            DeleteController.deleteKeepDeLo(jsonArray, preSyntax.trim(), Utils.getCurrentDate());
            saveMessageKeepDeLo();
        }
    }

    private void saveMessageKeepDeLo() {

        if (preSyntax.contains(LotoType.de.name()))
        {
            ConfigPreference.saveMessageGiuDe(getSaved(), "", "");
        }
        else if (preSyntax.contains(LotoType.lo.name()))
        {
            ConfigPreference.saveMessageGiuLo(getSaved(), "", "");
        }
    }

    private void updateHesoGiu(TYPE_LAYOUT type) {
        if (type == TYPE_LAYOUT.XIENBACANG)
        {
            String giuXien2 = seekBarXien1.getText().toString().isEmpty() ? "0" : seekBarXien1.getText().toString();
            String giuXien3 = seekBarXien2.getText().toString().isEmpty() ? "0" : seekBarXien2.getText().toString();
            String giuXien4 = seekBarXien3.getText().toString().isEmpty() ? "0" : seekBarXien3.getText().toString();
            String giubacang = seekBarXien4.getText().toString().isEmpty() ? "0" : seekBarXien4.getText().toString();

            ContactDBHelper.updateHesoGiu(
                    new String[]{ContactDatabase.GIU_DIEM_XIEN2, ContactDatabase.GIU_DIEM_XIEN3, ContactDatabase.GIU_DIEM_XIEN4, ContactDatabase.GIU_DIEM_BACANG},
                    new String[]{String.valueOf(Math.round(Double.parseDouble(giuXien2))),
                            String.valueOf(Math.round(Double.parseDouble(giuXien3))),
                            String.valueOf(Math.round(Double.parseDouble(giuXien4))),
                            String.valueOf(Math.round(Double.parseDouble(giubacang)))});

            updateAllXienBacang();
        }
        else if (type == TYPE_LAYOUT.GIUTUNGKHACH)
        {
            if (listCustomer2.getSelectedItem() != null)
            {
                String phone = ((Cursor) listCustomer2.getSelectedItem()).getString(ContactColumnMaps.PHONE);
                String name = ((Cursor) listCustomer2.getSelectedItem()).getString(ContactColumnMaps.NAME);

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

                    updateWhenChangeHesoGiu(name, phone, valueMaps);
                }
            }
        }
    }

    private String[] putStringTypeChange(String[] valueMaps) {
        hesoChanged = new ArrayList<>();

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

    private void updateAllXienBacang() {
        CommonTask.AsyncSingleThread.get().execute(new Runnable() {
            @Override
            public void run() {
                UpdateController.updateKeepXienValue(Utils.getCurrentDate());

                if (context instanceof BaseActivity)
                {
                    ((BaseActivity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            notifyUpdate();
                            Toast.makeText(context, "Hệ thống update xong.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).clean();
    }

    private void setHesoGiu(String phone, TYPE_LAYOUT type) {
        if (phone != null)
        {
            percentMaps = ContactDBHelper.getHesoGiu(phone);

            if (type == TYPE_LAYOUT.XIENBACANG)
            {
                seekBarXien1.setText(String.valueOf(percentMaps.get(ContactColumnMaps.GIU_DIEM_XIEN2).intValue()));
                seekBarXien2.setText(String.valueOf(percentMaps.get(ContactColumnMaps.GIU_DIEM_XIEN3).intValue()));
                seekBarXien3.setText(String.valueOf(percentMaps.get(ContactColumnMaps.GIU_DIEM_XIEN4).intValue()));
                seekBarXien4.setText(String.valueOf(percentMaps.get(ContactColumnMaps.GIU_DIEM_BACANG).intValue()));
            }
            else if (type == TYPE_LAYOUT.GIUTUNGKHACH)
            {
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
    }

    private int toggleShow(int current) {
        if (current == View.VISIBLE)
        {
            return View.GONE;
        }
        else
        {
            return View.VISIBLE;
        }
    }

    @Override
    public int layout() {
        return R.layout.dialog_dan_giuso;
    }

    @Override
    public void dismissed() {
        /*notifyUpdate();*/
    }

    private void notifyUpdate() {
        requestUpdate();
    }
}
