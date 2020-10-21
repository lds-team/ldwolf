package bingo.com.service;

import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import bingo.com.base.BaseLongTaskService;

public class HandleAnalyzeService extends BaseLongTaskService {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
