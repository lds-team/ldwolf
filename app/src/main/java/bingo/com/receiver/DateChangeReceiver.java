package bingo.com.receiver;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import java.util.Calendar;

import bingo.com.base.BaseReceiver;
import bingo.com.controller.InsertController;
import bingo.com.pref.ConfigPreference;
import bingo.com.utils.Utils;

public class DateChangeReceiver extends BaseReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String messageDe = ConfigPreference.formatMessageSaved(getSaved(context), "de");

        if (!TextUtils.isEmpty(messageDe))
        {
            InsertController.insertKeepMessageDelo(Utils.getDateFrom(String.valueOf(Calendar.getInstance().getTimeInMillis())), "de " + messageDe, null, getSaved(context));
        }


        String messageLo = ConfigPreference.formatMessageSaved(getSaved(context), "lo");

        if (!TextUtils.isEmpty(messageLo))
        {
            InsertController.insertKeepMessageDelo(Utils.getDateFrom(String.valueOf(Calendar.getInstance().getTimeInMillis())), "lo " + messageLo, null, getSaved(context));
        }

    }
}
