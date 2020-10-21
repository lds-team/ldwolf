package bingo.com.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.io.File;

import bingo.com.BingoApp;

public class InstallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action.equals(Intent.ACTION_PACKAGE_ADDED) || action.equals(Intent.ACTION_PACKAGE_CHANGED) || action.equals(Intent.ACTION_PACKAGE_INSTALL) || action.equals(Intent.ACTION_PACKAGE_REPLACED)) {
            ApplicationInfo info = null;
            try {
                info = context.getPackageManager().getApplicationInfo(intent.getDataString().split(":")[1], PackageManager.GET_META_DATA);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (info != null) {
                    File file = BingoApp.INSTALL_APK_INFO.get(info.packageName);
                    if (file != null) {
                        file.delete();
                        BingoApp.INSTALL_APK_INFO.remove(info.packageName);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
