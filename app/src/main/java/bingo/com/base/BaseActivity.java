package bingo.com.base;

import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

import bingo.com.R;
import bingo.com.async.CommonTask;
import bingo.com.customviews.ProgressLoading;
import bingo.com.obs.FireUpdate;
import bingo.com.obs.SyncObs;
import bingo.com.utils.Action;
import bingo.com.utils.Utils;

public abstract class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";

    public static final String PREF_NAME = "BingoSaved";

    private Toolbar toolbar;

    private TextView titleScreen;

    private TextView datePublic;

    private TextView notifyNumber;

    protected ProgressDialog dialog;

    protected ProgressLoading loadNum;

    protected FragmentManager fm;

    private SharedPreferences pref;

    private FireUpdate observable;

    private EventChanged eventChanged;

    protected String currentFragmentTag;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        observable = new FireUpdate();

        fm = getFragmentManager();

        pref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage("Updating...");

        eventChanged = new EventChanged();
        IntentFilter filter = new IntentFilter(Action.ACTION_REQUEST_UPDATE);
        registerReceiver(eventChanged, filter);

        Utils.currentDate = Utils.convertDate(String.valueOf(Calendar.getInstance().getTimeInMillis()));
    }

    protected void registerObs(SyncObs obs) {
        observable.register(obs);
    }

    protected void unRegisterObs(SyncObs obs) {
        observable.unRegister(obs);
    }

    public void requestUpdate() {
        if (!dialog.isShowing())
        {
            getLoadingAnimation("Updating...").show();
        }

        observable.notifyUpdate();
        setRunTimeTask();
        setNotifyNumber(getNotifyError());
        dismissLoading();
    }

    protected abstract int getNotifyError();

    protected void setRunTimeTask() {
        if (loadNum != null)
        {
            loadNum.setNumber(CommonTask.AsyncSingleThread.get().getCacheTaskCount());
        }
    }

    protected void setNotifyNumber(int number) {
        if (notifyNumber != null)
        {
            notifyNumber.setText(String.valueOf(number));
        }
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        refresh();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        refresh();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        refresh();
    }

    private void refresh() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar == null)
        {
            throw new NullPointerException("Toolbar has null because of not found.");
        }

        titleScreen = (TextView) toolbar.findViewById(R.id.toolbar_title);
        datePublic = (TextView) toolbar.findViewById(R.id.toolbar_date);
        notifyNumber = (TextView) toolbar.findViewById(R.id.toolbar_notify);
        loadNum = (ProgressLoading) toolbar.findViewById(R.id.toolbar_loading);

        datePublic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        setSupportActionBar(toolbar);
    }

    protected void registerNotifyNumberClick(View.OnClickListener event) {
        if (notifyNumber != null)
        {
            notifyNumber.setOnClickListener(event);
        }
        else
        {
//            throw new NullPointerException("NotifyNumber is definded");
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        calendar.setTimeInMillis(Long.parseLong(Utils.getTimeWithCurrentConfigDate()));

        DatePickerDialog dialog = new DatePickerDialog(BaseActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Utils.currentDate = Utils.getDateFrom(dayOfMonth, month, year);
                setDate();

                requestUpdate();
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (Utils.isSoFarTimeWithCurrent())
                {
                    notifyOnOldestDay();
                }

            }
        });

        dialog.show();
    }

    public SharedPreferences getSaved() {
        return pref;
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle("");
        if (titleScreen != null) {
            titleScreen.setText(title);

            setDate();
        }
    }

    private void notifyOnOldestDay() {
        Utils.showAlert(this, null, "Vui lòng không sửa dữ liệu do dữ liệu đã cũ.\nSửa sẽ gây lỗi dữ liệu.", "Chỉ xem", null);
    }

    protected ProgressDialog getLoadingAnimation(String... message) {
        if (message != null && message.length != 0)
            dialog.setMessage(message[0]);

        return dialog;
    }

    protected void dismissLoading () {
        if (dialog != null && dialog.isShowing())
        {
            dialog.dismiss();
        }
    }

    protected void showBackButton(int indicator, boolean show) {
        if (indicator == 0)
            indicator = R.drawable.ic_menu;

        getSupportActionBar().setHomeAsUpIndicator(indicator);
        getSupportActionBar().setDisplayHomeAsUpEnabled(show);
    }

    public void startActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }

    protected void transition(BaseFragment target, String tag) {
        setTitle(target.screenNane());

        this.currentFragmentTag = tag;

        FragmentTransaction menuTransaction = fm.beginTransaction();

        menuTransaction.replace(R.id.content_frame, target, tag);
        menuTransaction.commit();
    }

    protected BaseFragment getExistFragment(Class clazz, String tag) {
        BaseFragment f = (BaseFragment) fm.findFragmentByTag(tag);

        if (f == null)
        {
            try {
                f = (BaseFragment) clazz.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return f;
    }

    @Deprecated
    protected <T extends BaseFragment> T getVisibleFragment() {

        return null;
    }

    private void setDate() {
        if (datePublic != null)
            datePublic.setText(Utils.currentDate);
    }

    public String getCurrentDate() {
        return Utils.currentDate;
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    protected void unBindview(ViewGroup viewGroup) {
        if (viewGroup != null) {
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                viewGroup.getChildAt(i).setOnClickListener(null);
                viewGroup.getChildAt(i).setTag(null);
                if (viewGroup.getChildAt(i) instanceof ViewGroup)
                    unBindview((ViewGroup) viewGroup.getChildAt(i));
            }

            viewGroup.destroyDrawingCache();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        fm = null;

        unregisterReceiver(eventChanged);
        eventChanged = null;
    }

    protected class EventChanged extends BaseReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Action.ACTION_REQUEST_UPDATE))
            {
                if (CommonTask.AsyncSingleThread.get().getCacheTaskCount() < 3)
                {
                    BaseActivity.this.requestUpdate();
                }
            }
        }
    }
}
