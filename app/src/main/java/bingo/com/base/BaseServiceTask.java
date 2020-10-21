package bingo.com.base;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;

import bingo.com.utils.Action;

public abstract class BaseServiceTask extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public BaseServiceTask(String name) {
        super(name);
    }

    protected void requestUpdate() {
        Intent intent = new Intent(Action.ACTION_REQUEST_UPDATE);
        sendBroadcast(intent);
    }

    protected SharedPreferences getSaved() {
        return getSharedPreferences(BaseActivity.PREF_NAME, MODE_PRIVATE);
    }
}
