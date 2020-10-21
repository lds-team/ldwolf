package bingo.com.screen.dbscreen;

import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import bingo.com.R;
import bingo.com.async.TaskHelper;
import bingo.com.base.BaseFragment;
import bingo.com.helperdb.CongnoDBHelper;
import bingo.com.helperdb.ContactDBHelper;
import bingo.com.helperdb.FollowDBHelper;
import bingo.com.helperdb.MessageDBHelper;
import bingo.com.helperdb.db2.FollowDBHelper2;
import bingo.com.model.ContactModel;
import bingo.com.utils.Formats;
import bingo.com.utils.LoaderSQLite;
import bingo.com.utils.Utils;
import butterknife.Bind;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class DatabaseFragment extends BaseFragment<DatabaseFragment> {

    public static final String TAG = "DatabaseFragment";

    @Bind(R.id.dbscreen_ketqua)
    TextView result;

    @Bind(R.id.dbscreen_showketqua)
    Switch aSwitch;

    private HashMap<String, ContactModel> contacts;

    @Override
    public String initTag() {
        return TAG;
    }

    @Override
    public String screenNane() {
        return "Database";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new TaskHelper.TaskLoadResult(getActivity().getApplicationContext()).execute();
        contacts = ContactDBHelper.getFullContact();
    }

    @Override
    public View onCreateFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        registerObs(this);
        return inflater.inflate(R.layout.fragment_database, container, false);
    }

    @Override
    public void onViewFragmentCreated(View view, Bundle savedInstanceState) {
        loadResult(false);
    }

    private void loadResult(boolean showNotify) {
        String[] days = Utils.getCurrentDate().split("/");

        String year = days[2];
        String date = days[0].concat("/").concat(days[1]);

        String rst = LoaderSQLite.getResultFromDate(context, date, year, false).getBody();

        if (!TextUtils.isEmpty(rst))
        {
            if (rst.contains("DB:"))
            {
                int idxDb = rst.indexOf("DB:") + 6;
                if (idxDb != -1 && rst.length() > (idxDb + 2))
                {
                    String txt1 = rst.substring(0, idxDb).replaceAll("\\n", "<br>");
                    String txt2 = rst.substring(idxDb + 2).replaceAll("\\n", "<br>");
                    String db = rst.substring(idxDb, idxDb + 2);

                    rst = Formats.getStringHtmlText("#FF0000", 1, txt1, db, txt2);
                }
            }
            result.setText(Html.fromHtml(rst));
            result.setVisibility(View.VISIBLE);
        }
        else
        {
            if (showNotify)
            {
                Utils.showAlert(context, null, "Chưa có kết quả hôm nay.", "Ok", new Runnable() {
                    @Override
                    public void run() {
                        aSwitch.setChecked(false);
                    }
                });
            }
            else
            {
                aSwitch.setChecked(false);
            }
            result.setVisibility(View.GONE);
        }
    }

    @Override
    public DatabaseFragment instance() {
        return this;
    }

    @OnClick({R.id.dbscreen_deletealldb, R.id.dbscreen_deletedb, R.id.dbscreen_deletekeepdb, R.id.dbscreen_sendmessage,
            R.id.dbscreen_calculate, R.id.dbscreen_updateresult, R.id.dbscreen_updateresult997, R.id.dbscreen_updateresultweb})
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.dbscreen_updateresult:
                sendSms("8085");
                break;

            case R.id.dbscreen_updateresult997:
                sendSms("997");
                break;

            case R.id.dbscreen_updateresultweb:
                Utils.showAlert(context, null, "Hiện tại chưa support lấy dữ liệu từ Server.", "OK", null);
                break;

            case R.id.dbscreen_deletealldb:
                eraseAlldb();
                break;

            case R.id.dbscreen_deletedb:
                deleteAlldb();
                break;

            case R.id.dbscreen_deletekeepdb:
                deleteAllKeepdb();
                break;

            case R.id.dbscreen_sendmessage:
                sendMessage();
                break;

            case R.id.dbscreen_calculate:
//                new TaskHelper.TaskLoadResultToDB(getActivity(), null).execute();
                new TaskHelper.TaskCalculateResult(getActivity(), null).execute();
                break;

            default:break;
        }
    }

    public void sendSms(String recipient) {

        try {
            String body = "xstd";

            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(recipient, null, body, null, null);
            Toast.makeText(context, "Message Sent", Toast.LENGTH_LONG).show();

        } catch (Exception ex) {
            Toast.makeText(context, ex.getMessage().toString(), Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    private void sendMessage() {
        Utils.showAlertTwoButton(context, null, "Nhắn tin chốt tiền cho khách?", "OK", "Cancel", new Runnable() {
            @Override
            public void run() {
                new TaskHelper.TaskSendMessageChotTien(context).execute();
            }
        }, null);
    }

    private void eraseAlldb() {
        Utils.showAlertTwoButton(context, null, "Xóa hết CSDL?", "OK", "Cancel", new Runnable() {
            @Override
            public void run() {
                showLoading("Deleting...");

                MessageDBHelper.getDb().eraseAlls();
                FollowDBHelper2.getDatabase().eraseAlls();
                CongnoDBHelper.eraseAlls();

                dismissLoading();
            }
        }, null);
    }

    private void deleteAlldb() {
        Utils.showAlertTwoButton(context, null, "Xóa hết CSDL Ngày?", "OK", "Cancel", new Runnable() {
            @Override
            public void run() {
                showLoading("Deleting...");

                MessageDBHelper.deleteAlls();
                FollowDBHelper2.getDatabase().deleteAlls();
                CongnoDBHelper.deleteAlls();

                dismissLoading();
            }
        }, null);
    }

    private void deleteAllKeepdb() {
        Utils.showAlertTwoButton(context, null, "Xóa hết Giữ?", "OK", "Cancel", new Runnable() {
            @Override
            public void run() {
                showLoading("Deleting...");

                FollowDBHelper2.getDatabase().deleteAllKeepValueByDate(Utils.getCurrentDate());

                dismissLoading();
            }
        }, null);
    }

    @Override
    public void update() {
        loadResult(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unRegisterObs(this);
    }

    /*@OnClick(R.id.dbscreen_showketqua)
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(TAG, "handleChecked");

        switch (buttonView.getId())
        {
            case R.id.dbscreen_showketqua:
                if (isChecked)
                {
                    loadResult(false);
                }

                toggleButton(result, isChecked);
                break;

            default:break;
        }
    }*/

    private void toggleButton(View v, boolean show) {
        v.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
