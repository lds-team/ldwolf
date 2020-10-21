package bingo.com.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

import bingo.com.MainActivity;
import bingo.com.R;
import bingo.com.base.BaseServiceTask;
import bingo.com.callbacks.ErrorListener;
import bingo.com.controller.InsertController;
import bingo.com.enumtype.TypeMessage;
import bingo.com.helperdb.FollowDBHelper;
import bingo.com.helperdb.MessageDBHelper;
import bingo.com.helperdb.db.FollowDatabase;
import bingo.com.model.MessageForm;
import bingo.com.pref.ConfigPreference;
import bingo.com.screen.configscreen.SettingsHelper;
import bingo.com.utils.Constant;
import bingo.com.utils.Utils;

//TODO : implement on Trim memory when low. That'll be notify to users.
public class AnalyzeService extends BaseServiceTask implements ErrorListener {

    public static final String NAME = "NAME";
    public static final String PHONE = "PHONE";
    public static final String MESSAGE = "MESSAGE";
    public static final String TIME = "TIME";
    public static final String TYPE = "TYPE";
    public static final String RESULT = "RESULT";
    public static final String FORCE_ADD = "FORCE_ADD";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public AnalyzeService() {
        super("AnalyzeService");
    }

    //TODO that for foreground intent service to prevent kill from system.
    /*@Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        boolean forceAdd = intent.getBooleanExtra(FORCE_ADD, false);

        if (!forceAdd)
        {
            foreGroundThis();
        }

        new CommonTask.ThreadTask(new Runnable() {
            @Override
            public void run() {
                onHandleIntent(intent);
            }
        }).start();

        return START_NOT_STICKY;
    }*/

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null)
        {
            return;
        }

        Log.d("AnalyzeService", "Received new Message and Starting Analyze. Thread: " + Thread.currentThread().getName());

        boolean forceAdd = intent.getBooleanExtra(FORCE_ADD, false);
        /*if (!forceAdd)
        {
            foreGroundThis(); //Remove when foreground on onStartCommand.
        }*/

        String name = intent.getStringExtra(NAME);
        String phone = intent.getStringExtra(PHONE);
        String message = intent.getStringExtra(MESSAGE);
        long time = intent.getLongExtra(TIME, 0);
        String type = intent.getStringExtra(TYPE);

        if (time == 0)
        {
            notifyError("System Error: " + message);
            time = Calendar.getInstance().getTimeInMillis();
        }

        //That mean if get time from here, must only to received message here.
        //Add Message To Db.
        /*MessageDBHelper.addMessage(new ChooseMessage(
                name,
                phone,
                String.valueOf(time),
                message,
                type
        ));*/

        /*Parcelable[] resultPs = intent.getParcelableArrayExtra(RESULT);
        ResultReturnModel[] results = new ResultReturnModel[]{(ResultReturnModel) resultPs[0], (ResultReturnModel) resultPs[1]};*/

        boolean receiveDe = Utils.getTimeFromConfig(SettingsHelper.getTimeReceiveDe(getSaved(), phone)) > time;
        boolean receiveLo = Utils.getTimeFromConfig(SettingsHelper.getTimeReceiveLoXien(getSaved(), phone)) > time;

        int response = SettingsHelper.getReplyMessageConfig(getSaved());

        if (forceAdd)
        {
            response = -1;
            receiveDe = true;
            receiveLo = true;
        }

        TypeMessage typeMessage = TypeMessage.valueOf(type);

        String error = InsertController.analyzeSingleMessage(
                name, phone, message, String.valueOf(time), typeMessage, ConfigPreference.getRegexCustom(getSaved()), receiveDe, receiveLo, (typeMessage == TypeMessage.SENT), this);

        Log.d("AnalyzeService", "Import Message Received: Error: " + error);

        requestUpdate();

        if (error == null)
        {
            /*if (!MessageDBHelper.hasResponse(phone, String.valueOf(time)) && (typeMessage == TypeMessage.RECEIVED))
            {
                responseMessage(phone, time, response, message);

                MessageDBHelper.updateResponseStatus(phone, String.valueOf(time), true);
            }*/
        }
        else
        {
            notifyError(message);
        }

        /*stopSelf();*/
    }

    private void updateAnalyzeContent(String phone, long time) {
        Cursor cursor = FollowDBHelper.getNumberMessageForBuild(phone, time);

        try {

            String mes = new MessageForm.Builder(
                    cursor,
                    cursor.getColumnIndex(FollowDatabase.DETAIL_PARENT_TYPE),
                    cursor.getColumnIndex(FollowDatabase.DETAIL_TYPE),
                    cursor.getColumnIndex(FollowDatabase.DETAIL_NUMBER),
                    cursor.getColumnIndex("sum"),
                    true
            ).build().getMessage();

            MessageDBHelper.updateAnalyzeContent(phone, String.valueOf(time), mes);

        } finally {
            cursor.close();
        }
    }

    private void responseMessage(String phone, long time, int type, String message) {
        switch (type)
        {
            case SettingsHelper.OK_TIN_VA_NOI_DUNG:
                Cursor cursor = FollowDBHelper.getNumberFromMessage(phone, time);
                String mes = new MessageForm.Builder(
                        cursor,
                        cursor.getColumnIndex(FollowDatabase.DETAIL_PARENT_TYPE),
                        cursor.getColumnIndex(FollowDatabase.DETAIL_TYPE),
                        cursor.getColumnIndex(FollowDatabase.DETAIL_NUMBER),
                        cursor.getColumnIndex(FollowDatabase.DETAIL_PRICE),
                        false
                ).build().getMessage();

                cursor.close();

                if (!TextUtils.isEmpty(mes))
                    startSend(phone, "OK\n" + mes);
                return;

            case SettingsHelper.OK_TIN:
                startSend(phone, "OK");
                return;

            case SettingsHelper.OK_TIN_DA_NHAN:
                startSend(phone, "OK\n" + message.trim());
                return;

            default: break;
        }
    }

    private void startSend(String phone, String message) {
        try {

            if (message.length() > 160)
            {
                SmsManager sms = SmsManager.getDefault();
                ArrayList<String> parts = sms.divideMessage(message);
                sms.sendMultipartTextMessage(phone, null, parts, null, null);
            }
            else
            {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phone, null, message, null, null);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /*private void foreGroundThis() {
        Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_menu)
                .setContentTitle("Bingo Application")
                .setContentText("Received new message...")
                .setContentIntent(pendingIntent).build();

        startForeground(1005, notification);
    }*/

    private void notifyError(String message) {

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(Constant.NOTIFY_BUNDLE, message);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        if (message.length() > 20)
            message = message.substring(0, 20);

        Notification notification  = new Notification.Builder(this)
                .setContentTitle("Bingo Application")
                .setContentText("Error: " + message + "...")
                .setSmallIcon(R.drawable.ic_menu)
                .setContentIntent(pIntent)
                .setAutoCancel(true).build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1010, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d("AnalyzeService", "Finish and Stop Analyze. Thread: " + Thread.currentThread().getName());
    }

    @Override
    public void errorNotify(boolean isError, boolean forceAdd) {
        if (!isError && forceAdd)
        {
            requestUpdate();
        }
    }
}
