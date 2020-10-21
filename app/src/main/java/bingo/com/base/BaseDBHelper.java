package bingo.com.base;

import android.content.Context;
import android.content.Intent;

import bingo.com.utils.Action;

public class BaseDBHelper {

    protected static void requestUpdateUI(Context context) {
        Intent intent = new Intent(Action.ACTION_REQUEST_UPDATE);
        context.sendBroadcast(intent);
    }
}
