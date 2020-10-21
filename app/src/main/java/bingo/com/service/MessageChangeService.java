package bingo.com.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;

import bingo.com.obs.MessageDeletedObserver;
import bingo.com.receiver.MessageReceiver;

public class MessageChangeService extends Service {

    MessageReceiver messageReceiver = new MessageReceiver();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("MessageChangeService", "onStartCommand: " + intent);

        registerReceiver(messageReceiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("MessageChangeService", "onCreate: ");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("MessageChangeService", "onUnbind: ");
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.d("MessageChangeService", "onRebind: ");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.d("MessageChangeService", "onTaskRemoved: ");
        sendBroadcast(new Intent("restartService"));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("MessageChangeService", "onBind: " + intent);
        return null;
    }

    @Override
    public void onDestroy() {
        Log.d("MessageChangeService", "onDestroy: ");
        super.onDestroy();

    }

    public class RestartService extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }
}
