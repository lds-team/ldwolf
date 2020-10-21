package bingo.com.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import bingo.com.BuildConfig;
import bingo.com.MainActivity;
import bingo.com.R;
import bingo.com.async.CommonTask;
import bingo.com.base.BaseReceiver;
import bingo.com.callbacks.ErrorListener;
import bingo.com.controller.InsertController;
import bingo.com.enumtype.TypeMessage;
import bingo.com.helperdb.ContactDBHelper;
import bingo.com.helperdb.FollowDBHelper;
import bingo.com.helperdb.MessageDBHelper;
import bingo.com.helperdb.db.FollowDatabase;
import bingo.com.helperdb.db.LotoMsgProvider;
import bingo.com.helperdb.db.MessageDatabase;
import bingo.com.model.ChooseMessage;
import bingo.com.model.LotoMessage;
import bingo.com.model.MessageForm;
import bingo.com.model.ResultModel;
import bingo.com.model.ResultReturnModel;
import bingo.com.pref.ConfigPreference;
import bingo.com.screen.configscreen.SettingsHelper;
import bingo.com.utils.Action;
import bingo.com.utils.Constant;
import bingo.com.utils.LoaderSQLite;
import bingo.com.utils.Utils;

public class MessageReceiver extends BaseReceiver {

    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private static final String SMS_SENT = "android.provider.Telephony.SMS_SENT";
    private static final String TAG = "MessageReceiver";

    private List<LotoMessage> lsLotoMessage;

    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Intent received: " + intent.getAction());

        this.context = context;
        extractIntent(context, intent, TypeMessage.RECEIVED);
    }

    private void extractIntent(final Context context, Intent intent, final TypeMessage type) {
        Bundle bundle = intent.getExtras();
        if (bundle != null)
        {
            Object[] pdus = (Object[]) bundle.get("pdus");
            final SmsMessage[] messages = new SmsMessage[pdus.length];
            for (int i = 0; i < pdus.length; i++)
            {
                messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
            }

            //TODO case 1 message received once time.
            if (messages.length > 0)
            {
                //Case message length > 160;
                StringBuilder content = new StringBuilder();
                    for (SmsMessage message : messages)
                    {
                        content.append(message.getMessageBody());
                    }

                final String mySmsText = content.toString();
                final String phone = messages[0].getOriginatingAddress();

                final long time = messages[0].getTimestampMillis();

                if ((phone.contains("8085") || phone.contains("997")) && (phone.length() < 9))
                {
                    importKetqua(context, phone, mySmsText, time);
                }
                else
                {
                    Runnable analyze = new Runnable() {
                        @Override
                        public void run() {
                            analyzeMessage(context, mySmsText, phone,  time, type);
                        }
                    };

                    CommonTask.AsyncSingleThread.get().execute(analyze);
                }
            }
        }
    }

    private void analyzeMessage(Context context, String content, String rawPhone, long timeMessage, TypeMessage type) {
        /*long timeDe = Utils.getTimeFromConfig(SettingsHelper.getTimeReceiveDe(getSaved(context)));
        long timeLo = Utils.getTimeFromConfig(SettingsHelper.getTimeReceiveLoXien(getSaved(context)));

        long timeEnd = Math.max(timeDe, timeLo);

        if (timeEnd < timeMessage) //Not receive message over time.
            return;*/

        String phone = Utils.fixPhone(rawPhone);
        String name = ContactDBHelper.getNameKhachChuyen(phone);

        if (name == null) //This case to choose what contact in app db.
            return;

        if (!SettingsHelper.getReceiveDupplicate(getSaved(context)))
        {
            if (MessageDBHelper.getDb().hasMessageBefore(phone, Utils.convertDate(String.valueOf(timeMessage)), content))
            {
                startSend(phone, "Tin bị trùng\n" + content);
                return;
            }
        }

        /*ResultReturnModel[] result = StatisticControlImport.getResultTodayAndYesterday(context.getApplicationContext());*/
        startAnalyze(context, name, phone, content, timeMessage, type, null);

        /*TempleMessageHelper.importTempleMessage(phone, timeMessage);*/
    }

    private void startAnalyze(Context context, String name, String phone, String message, long time, TypeMessage typeMessage, ResultReturnModel[] results) {
        Log.d(TAG, "Message received: " + "from: " + phone + " body: " + message + " time: " + time);

//        LoaderSQLite.startAnalyzing(context, name, phone, message, time, type.name(), null);

        if (time == 0)
        {
            notifyError(context, message);
            time = Calendar.getInstance().getTimeInMillis();
        }

        //TODO get time here will be different with load from content provider SMS.
        //That mean if get time from here, must only to received message here.
        //Add Message To Db.
        MessageDBHelper.addMessage(new ChooseMessage(
                name,
                phone,
                String.valueOf(time),
                message,
                typeMessage.name()
        ));

        /*Parcelable[] resultPs = intent.getParcelableArrayExtra(RESULT);
        ResultReturnModel[] results = new ResultReturnModel[]{(ResultReturnModel) resultPs[0], (ResultReturnModel) resultPs[1]};*/

        boolean receiveDe = Utils.getTimeFromConfig(SettingsHelper.getTimeReceiveDe(getSaved(context), phone)) > time;
        boolean receiveLo = Utils.getTimeFromConfig(SettingsHelper.getTimeReceiveLoXien(getSaved(context), phone)) > time;

        int response = SettingsHelper.getReplyMessageConfig(getSaved(context));

        String error = InsertController.analyzeSingleMessage(
                name, phone, message, String.valueOf(time), typeMessage, ConfigPreference.getRegexCustom(context), receiveDe, receiveLo, (typeMessage == TypeMessage.SENT), null);

        Log.d("AnalyzeService", "Import Message Received: Error: " + error);

        requestUpdate();

        if (error == null)
        {
            if (!MessageDBHelper.hasResponse(phone, String.valueOf(time)) && (typeMessage == TypeMessage.RECEIVED))
            {
                responseMessage(phone, time, response, message, receiveDe, receiveLo);
                MessageDBHelper.updateResponseStatus(phone, String.valueOf(time), true);
            }
        }
        else
        {
            notifyError(context, message);
        }
    }

    private void responseMessage(String phone, long time, int type, String message, boolean receivedDe, boolean receivedLo) {
        Cursor cursor = MessageDBHelper.getDb().getMessageForResponse(phone, Utils.getCurrentDate());
        String mes = null;

        int count = cursor.getCount();
        for (int i = 0; i < count; i++)
        {
            cursor.moveToPosition(i);
            String timeInCursor = cursor.getString(cursor.getColumnIndex(MessageDatabase.TIME));

            if (timeInCursor.equals(String.valueOf(time)))
            {
                mes = "OK ".concat(receivedLo ? "" : message.matches(".*(de|bacang).*") ? "de " : "").concat("tin ").concat(String.valueOf(i + 1));

                switch (type)
                {
                    case SettingsHelper.OK_TIN_VA_NOI_DUNG:
                        mes = mes.concat("\n").concat(cursor.getString(cursor.getColumnIndex(MessageDatabase.ANALYZE_CONTENT)));
                        break;

                    case SettingsHelper.OK_TIN:
                        break;

                    case SettingsHelper.OK_TIN_DA_NHAN:
                        mes = mes + "\n" + message.trim();
                        break;

                    default:
                        mes = null;
                        break;
                }
            }
        }

        cursor.close();

        if (!TextUtils.isEmpty(mes))
        {
            mes = mes.replaceAll("<br>|</br>", " ");
            mes = mes.replaceAll("([,])\\s+([0-9])", "$1$2");
            mes = receivedLo ? mes : mes.concat("\nBỏ lô xiên vì quá giờ.");
            startSend(phone, mes);
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

    private void notifyError(Context context, String message) {

        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(Constant.NOTIFY_BUNDLE, message);
        PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, 0);

        if (message.length() > 20)
            message = message.substring(0, 20);

        Notification notification  = new Notification.Builder(context)
                .setContentTitle("Bingo Application")
                .setContentText("Error: " + message + "...")
                .setSmallIcon(R.drawable.ic_menu)
                .setContentIntent(pIntent)
                .setAutoCancel(true).build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1010, notification);
    }

    protected void requestUpdate() {
        Intent intent = new Intent(Action.ACTION_REQUEST_UPDATE);
        context.sendBroadcast(intent);
    }

    private void importKetqua(final Context context, String address, String body, long time) {
        try {
            lsLotoMessage = new ArrayList<>();

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
                // Add a new record to Database.
                new CommonTask.ThreadTask(new Runnable() {
                    @Override
                    public void run() {
                        String mBody = lsLotoMessage.get(0).Body;

                        String bodySeparated = ResultModel.buildFrom(mBody);

                        if (TextUtils.isEmpty(bodySeparated))
                        {
                            mBody = ""; //Remove when not enough prize.
                        }

                        String date = ResultModel.getDateFrom(mBody);

                        if (date == null)
                        {
                            return;
                        }

                        ContentValues values = new ContentValues();

                        values.put(LotoMsgProvider.Address, lsLotoMessage.get(0).Address);
                        values.put(LotoMsgProvider.Date, date);
                        values.put(LotoMsgProvider.Body, mBody);
                        values.put(LotoMsgProvider.Body_Separated, bodySeparated);

                        ContentResolver resolver = context.getContentResolver();

                        //IF it's new year, update old db.
                        if (isYearTrans(date, lsLotoMessage.get(0).SentDate))
                        {
                            int row = resolver.update(
                                    LoaderSQLite.getUriForQueryLoto(String.valueOf(Calendar.getInstance().get(Calendar.YEAR) - 1)),
                                    values,
                                    LotoMsgProvider.Date + "=?",
                                    new String[]{date});

                            Log.d("MessageReceiver", "Import Result: " + "updated in row: " + row + ", case YearTrans = true");
                            return;
                        }

                        Cursor c = resolver.query(LotoMsgProvider.CONTENT_URI, null,LotoMsgProvider.Date + "=?", new String[]{date}, null);

                        try {
                            if (c == null || c.getCount() == 0)
                            {
                                // Insert DB.
                                Uri uri = resolver.insert(
                                        LotoMsgProvider.CONTENT_URI, values);

                                Log.d(TAG, "Import Result: " + uri);
                            }
                            else
                            {
                                //Update DB If exist.
                                int row = resolver.update(
                                        LotoMsgProvider.CONTENT_URI, values, LotoMsgProvider.Date + "=?", new String[]{date});

                                Log.d(TAG, "Import Result: " + "updated in row: " + row + ", case YearTrans = false");
                            }
                        } finally {
                            if (c != null)
                            {
                                c.close();
                            }
                        }
                    }
                }).start();

                Thread.sleep(1500);
                requestUpdate();
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
