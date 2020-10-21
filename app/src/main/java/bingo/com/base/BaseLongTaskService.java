package bingo.com.base;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;

import bingo.com.utils.Action;

public abstract class BaseLongTaskService extends Service {

    protected void requestUpdate() {
        Intent intent = new Intent(Action.ACTION_REQUEST_UPDATE);
        sendBroadcast(intent);
    }

    protected SharedPreferences getSaved() {
        return getSharedPreferences(BaseActivity.PREF_NAME, MODE_PRIVATE);
    }
}

