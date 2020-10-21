package bingo.com.screen.editmessagescreen;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bingo.analyze.Analyze;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import bingo.com.BuildConfig;
import bingo.com.R;
import bingo.com.async.CommonTask;
import bingo.com.async.TaskAnalyze;
import bingo.com.base.BaseFragment;
import bingo.com.controller.DeleteController;
import bingo.com.enumtype.TypeMessage;
import bingo.com.helperdb.ContactDBHelper;
import bingo.com.helperdb.MessageDBHelper;
import bingo.com.helperdb.db.ContactDatabase;
import bingo.com.helperdb.db.LotoMsgProvider;
import bingo.com.helperdb.db.MessageDatabase;
import bingo.com.model.ChooseMessage;
import bingo.com.model.LotoMessage;
import bingo.com.model.ResultModel;
import bingo.com.pref.ConfigPreference;
import bingo.com.screen.editmessagescreen.adapter.EditMessageAdapter;
import bingo.com.utils.Formats;
import bingo.com.utils.LoaderSQLite;
import bingo.com.utils.Utils;
import bingo.com.utils.Validate;
import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import butterknife.OnTextChanged;

public class EditMessageFragment extends BaseFragment<EditMessageFragment> implements View.OnClickListener {

    public static final String TAG = "EditMessageFragment";

    @Bind(R.id.editmessage_downmessage)
    RadioGroup mCheckedDownMessage;

    @Bind(R.id.editmessage_down_sent)
    RadioButton checkSent;

    @Bind(R.id.editmessage_down_inbox)
    RadioButton checkDown;

    @Bind(R.id.editmessage_spin)
    Spinner mListCustomer;

    @Bind(R.id.editmessage_list)
    RecyclerView list;

    @Bind(R.id.editmessage_reload)
    Button reload;

    @Bind(R.id.editmessage_linear)
    LinearLayout editButton;

    @Bind(R.id.editmessage_textmess)
    EditText textMessage;

    @Bind(R.id.editmessage_errnotify)
    TextView errNotify;

    TypeMessage typeMessage;

    private EditMessageAdapter adapter;

    private SimpleCursorAdapter custommerAdapter;

    private String phoneSelected;
    private String nameSelected;

    private boolean isChuNhanSo;

    private boolean isEditMode;

    private boolean hasReload;

    @Override
    public String initTag() {
        return TAG;
    }

    @Override
    public String screenNane() {
        return "Edit Message";
    }

    @Override
    public View onCreateFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        registerObs(this);
        return inflater.inflate(R.layout.fragment_editmessage, container, false);
    }

    @Override
    public void onViewFragmentCreated(View view, Bundle savedInstanceState) {
        initListCustom();
        initListMessage();

        typeMessage = TypeMessage.SMS;
    }

    private void initListCustom() {
        custommerAdapter = new SimpleCursorAdapter(
                context,
                android.R.layout.simple_spinner_dropdown_item,
                ContactDBHelper.getAlls(),
                new String[]{ContactDatabase.NAME},
                new int[]{android.R.id.text1}, 0);

        mListCustomer.setAdapter(custommerAdapter);
        mListCustomer.setDropDownVerticalOffset(120);
        mListCustomer.setSelection(0);
    }

    private void initListMessage() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        list.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(list.getContext(),
                layoutManager.getOrientation());
        list.addItemDecoration(dividerItemDecoration);

        adapter = new EditMessageAdapter(context, null, textMessage, mListCustomer, errNotify, false);
        list.setAdapter(adapter);

        requestUpdate();
    }

    private Cursor getErrorMessage() {
        return MessageDBHelper.getCursorStattisticNoAnalyze();
    }

    @OnClick(R.id.editmessage_reload)
    public void reloadDataCustomer() {

        if (phoneSelected != null)
        {
            CommonTask.AsyncSingleThread.get().execute(context, new Runnable() {
                @Override
                public void run() {
                    ConfigPreference.saveHasReloadToday(getSaved(), phoneSelected);

                    DeleteController.deleteDataUserHasAnalyze(phoneSelected, getCurrenConfigDate(), true);

                    MessageDBHelper.loadMessageSync(phoneSelected, typeMessage);

                    hasReload = true;

                    CommonTask.AsyncSingleThread.get().executeOnMain(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "Đã tải lại xong.", Toast.LENGTH_LONG).show();
                            requestUpdate();
                        }
                    });
                }
            });
        }
    }

    @OnTextChanged(R.id.editmessage_textmess)
    public void textChanged(Editable editable) {
        if (editable.toString().isEmpty())
        {
            errNotify.setText("");
            errNotify.setVisibility(View.GONE);
        }
    }

    @Override
    public EditMessageFragment instance() {
        return this;
    }

    @OnClick({R.id.editmessage_edit, R.id.editmessage_down, R.id.editmessage_down_sent, R.id.editmessage_down_inbox, R.id.editmessage_editmes, R.id.editmessage_addmes})
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.editmessage_edit:
                typeMessage = TypeMessage.SMS;
                setTypeView(true);
                break;

            case R.id.editmessage_down:
                if (mCheckedDownMessage.getCheckedRadioButtonId() == R.id.editmessage_down_sent)
                    typeMessage = TypeMessage.SENT;
                else
                    typeMessage = TypeMessage.RECEIVED;

                setTypeView(false);
                break;

            case R.id.editmessage_down_inbox:
                custommerAdapter.swapCursor(ContactDBHelper.getAllsKhachChuyenSo());
                mListCustomer.setSelection(0);
                typeMessage = TypeMessage.RECEIVED;
                break;

            case R.id.editmessage_down_sent:
                custommerAdapter.swapCursor(ContactDBHelper.getAllsChuNhanSo());
                mListCustomer.setSelection(0);
                typeMessage = TypeMessage.SENT;
                break;

            case R.id.editmessage_addmes:
                addMessage(textMessage.getText().toString());
                break;

            case R.id.editmessage_editmes:
                //TODO temporary for add result when are testing.
                /*if (textMessage.getText().toString().contains("MB "))
                {
                    importKetqua(context, "997", textMessage.getText().toString(), Long.parseLong(Utils.getTimeWithCurrentConfigDate()));
                    return;
                }*/

                updateMessage(textMessage.getText().toString());
                break;

            default:break;
        }
    }

    private void addMessage(final String mes) {
        if (TextUtils.isEmpty(mes) || phoneSelected == null)
            return;

        if (isChuNhanSo)
        {
            startingAddMessage(TypeMessage.SENT, mes);
        }
        else
        {
            startingAddMessage(TypeMessage.RECEIVED, mes);
        }

        textMessage.setText("");
    }

    private void startingAddMessage(TypeMessage type, String mes) {
        final long time = Long.parseLong(Utils.getTimeWithCurrentConfigDate());

        MessageDBHelper.addMessage(new ChooseMessage(
                nameSelected,
                phoneSelected,
                String.valueOf(time),
                mes,
                type.name()
        ));

        /*ResultReturnModel[] results = StatisticControlImport.getResultTodayAndYesterday(context.getApplicationContext());*/
        LoaderSQLite.startForceAnalyzing(context, nameSelected, phoneSelected, mes, time, type.name(), null);

        Toast.makeText(getActivity(), "Đã thêm vào tin nhắn " + type.name(), Toast.LENGTH_LONG).show();
    }

    private void updateMessage(String mes) {
        ChooseMessage itemSelect = adapter.chooseMessage;

        if (itemSelect != null && !TextUtils.isEmpty(mes))
        {
            Log.d(TAG, "updateMessage: " + itemSelect.getPhone());

            //TODO
            Analyze eObj = Validate.validate(context, mes);
            if (eObj.error)
            {
                Spanned last = Formats.getError(mes, eObj);
                if (TextUtils.isEmpty(last))
                {
                    last = Formats.getHtmlText("", mes, "#FF0000");
                }

                textMessage.setText(last);
                adapter.setErrNotify(eObj.errorNotify);

                Toast.makeText(getActivity(), "Tin lỗi.", Toast.LENGTH_LONG).show();
            }
            else
            {
                int row = MessageDBHelper.updateContent(itemSelect.getPhone(), itemSelect.getTime(), mes);

                if (row != 0 && adapter.clickable)
                {
                    adapter.setClickable(false);

                    textMessage.setText("");

                    MessageDBHelper.updateError(itemSelect.getPhone(), itemSelect.getTime(), "");

                    //Update Status Analyze for each Message.
                    //It's dupplicated validate message.
                    MessageDBHelper.updateSingleMessageAnalyze(true, itemSelect.getPhone(), itemSelect.getTime());

                    boolean checkDay = Utils.getCurrentDate().equals(Utils.convertDate(String.valueOf(Calendar.getInstance().getTimeInMillis())));

                    //Change to current time.
                    long timeCheck = checkDay && !ConfigPreference.hasReloadToday(getSaved(), itemSelect.getPhone()) ? Calendar.getInstance().getTimeInMillis() : 0/*Long.parseLong(itemSelect.getTime())*/;//Not check when pass day.
                    /*if (checkDay)
                    {`
                        MessageDBHelper.getDb().updateTimeMessage(itemSelect.getTime(), itemSelect.getPhone(), String.valueOf(newTime));
                    }*/

                    requestUpdate();

                    /*ResultReturnModel[] results = StatisticControlImport.getResultTodayAndYesterday(context.getApplicationContext());*/
                    /*LoaderSQLite.startForceAnalyzing(context, itemSelect.getName(), itemSelect.getPhone(), mes, Long.parseLong(itemSelect.getTime()), itemSelect.getType(), null);*/
                    TaskAnalyze.begin(context, itemSelect.getName(), itemSelect.getPhone(), mes, Long.parseLong(itemSelect.getTime()), timeCheck, TypeMessage.convert(itemSelect.getType()), !checkDay || itemSelect.isShowingTimeout());
                }
                else
                {
                    Toast.makeText(getActivity(), "Xảy ra lỗi khi update tin nhắn.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void setTypeView(boolean isEdit) {
        if (isEdit)
        {
            mCheckedDownMessage.setVisibility(View.GONE);
            reload.setVisibility(View.GONE);
            editButton.setVisibility(View.VISIBLE);
            textMessage.setVisibility(View.VISIBLE);

            custommerAdapter.swapCursor(ContactDBHelper.getAlls());
        }
        else
        {
            mCheckedDownMessage.setVisibility(View.VISIBLE);
            reload.setVisibility(View.VISIBLE);
            editButton.setVisibility(View.GONE);
            textMessage.setVisibility(View.GONE);

            if (checkDown.isChecked())
            {
                custommerAdapter.swapCursor(ContactDBHelper.getAllsKhachChuyenSo());
            }
            else
            {
                custommerAdapter.swapCursor(ContactDBHelper.getAllsChuNhanSo());
            }
        }

        isEditMode = isEdit;
    }

    @OnItemSelected(R.id.editmessage_spin)
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        boolean hasChange = (phoneSelected != null);

        phoneSelected = ((Cursor) mListCustomer.getSelectedItem()).getString(((Cursor) mListCustomer.getSelectedItem()).getColumnIndex(MessageDatabase.PHONE));
        nameSelected = ((Cursor) mListCustomer.getSelectedItem()).getString(((Cursor) mListCustomer.getSelectedItem()).getColumnIndex(MessageDatabase.NAME));
        isChuNhanSo = ((Cursor) mListCustomer.getSelectedItem()).getString(((Cursor) mListCustomer.getSelectedItem()).getColumnIndex(MessageDatabase.TYPE)).equals(ContactDatabase.TYPE_CHU_NHAN_SO);

        if (hasChange && isEditMode)
        {
            adapter.swapCursor(
                    ConfigPreference.hasReloadToday(getSaved(), phoneSelected),
                    MessageDBHelper.getCursorStattisticNoAnalyze(phoneSelected)
            );
        }
    }

    @Override
    public void update() {
        if (!adapter.clickable)
        {
            adapter.setClickable(true);

            //TODO
            /*String error = MessageDBHelper.getError(phoneUpdating, timeUpdating);
            if (TextUtils.isEmpty(error))
            {*/
                textMessage.setText("");
                Toast.makeText(getActivity(), "Đã Sửa tin.", Toast.LENGTH_LONG).show();
            /*}
            else
            {
                Spanned last = Formats.formatStringError(messageUpdating, error);

                textMessage.setText(last);
                Toast.makeText(getActivity(), "Tin lỗi.", Toast.LENGTH_LONG).show();
            }*/
        }

        if (hasReload)
        {
            adapter.swapCursor(
                    ConfigPreference.hasReloadToday(getSaved(), phoneSelected),
                    MessageDBHelper.getCursorStattisticNoAnalyze(phoneSelected)
            );
        }
        else
        {
            String phone = null;

            if (phoneSelected == null)
            {
                if (mListCustomer.getSelectedItem() != null)
                {
                    phone = ((Cursor) mListCustomer.getSelectedItem()).getString(((Cursor) mListCustomer.getSelectedItem()).getColumnIndex(MessageDatabase.PHONE));
                }
            }
            else
            {
                phone = phoneSelected;
            }

            adapter.swapCursor(
                    ConfigPreference.hasReloadToday(getSaved(), phone),
                    getErrorMessage()
            );
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        unRegisterObs(this);
    }

    //Remove
    private void importKetqua(final Context context, String address, String body, long time) {
        try {
            List<LotoMessage> lsLotoMessage = new ArrayList<>();

            Date dateMsg = new Date(time);
            Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            LotoMessage mLotoMessage = new LotoMessage();
            mLotoMessage.Address = address;
            mLotoMessage.Body = body;
            mLotoMessage.SentDate = dateMsg;

            if (body == null)
            {
                return;
            }

            lsLotoMessage.add(mLotoMessage);


            if (lsLotoMessage.size() > 0) {
                String mBody = lsLotoMessage.get(0).Body;
                String bodySeparated = ResultModel.buildFrom(mBody);

                if (TextUtils.isEmpty(bodySeparated))
                {
                    mBody = ""; //Remove when not enough prize.
                }

                String date = ResultModel.getDateFrom(mBody);

                if (date == null) {
                    return;
                }

                ContentValues values = new ContentValues();

                values.put(LotoMsgProvider.Address, lsLotoMessage.get(0).Address);
                values.put(LotoMsgProvider.Date, date);
                values.put(LotoMsgProvider.Body, mBody);
                values.put(LotoMsgProvider.Body_Separated, bodySeparated);

                ContentResolver resolver = context.getContentResolver();

                //IF it's new year, update old db.
                if (isYearTrans(date, lsLotoMessage.get(0).SentDate)) {
                    int row = resolver.update(
                            LoaderSQLite.getUriForQueryLoto(String.valueOf(Calendar.getInstance().get(Calendar.YEAR) - 1)),
                            values,
                            LotoMsgProvider.Date + "=?",
                            new String[]{date});

                    Log.d(TAG, "Import Result: " + "updated in row: " + row + ", case YearTrans = true");
                    return;
                }

                Cursor c = resolver.query(LotoMsgProvider.CONTENT_URI, null, LotoMsgProvider.Date + "=?", new String[]{date}, null);

                try {
                    if (c == null || c.getCount() == 0) {
                        // Insert DB.
                        Uri uri = resolver.insert(
                                LotoMsgProvider.CONTENT_URI, values);

                        Log.d(TAG, "Import Result: " + uri);
                    } else {
                        //Update DB If exist.
                        int row = resolver.update(
                                LotoMsgProvider.CONTENT_URI, values, LotoMsgProvider.Date + "=?", new String[]{date});

                        Log.d(TAG, "Import Result: " + "updated in row: " + row + ", case YearTrans = false");
                    }
                } finally {
                    if (c != null) {
                        c.close();
                    }
                }

            }

        } catch (Exception ex) {
            if (BuildConfig.DEBUG)
            {
                Log.e(TAG, ex.getMessage());
            }
        }
    }

    private boolean isYearTrans(String dateResult, Date dateMessage) {
        String[] date = dateResult.trim().split("/");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateMessage);

        return date[0] == "31" && date[1] == "12" && calendar.get(Calendar.DAY_OF_MONTH) == 1 && calendar.get(Calendar.MONTH) == 0;
    }
}
