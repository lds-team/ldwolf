package bingo.com.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.SharedPreferences;

public abstract class BaseReceiver extends BroadcastReceiver {

    public SharedPreferences getSaved(Context context) {
        return context.getSharedPreferences(BaseActivity.PREF_NAME, Context.MODE_PRIVATE);
    }
}
