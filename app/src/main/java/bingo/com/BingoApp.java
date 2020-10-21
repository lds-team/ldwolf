package bingo.com;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

import bingo.com.base.BaseBingoApp;
import bingo.com.helperdb.CongnoDBHelper;
import bingo.com.helperdb.ContactDBHelper;
import bingo.com.helperdb.FollowDBHelper;
import bingo.com.helperdb.MessageDBHelper;
import bingo.com.helperdb.TempleMessageHelper;
import bingo.com.helperdb.db2.FollowDBHelper2;

public class BingoApp extends BaseBingoApp {

    public static final ConcurrentHashMap<String, File> INSTALL_APK_INFO = new ConcurrentHashMap<String, File>();

    @Override
    public void onCreate() {
        super.onCreate();

        init();
    }

    private void init() {
        ContactDBHelper.init(getApplicationContext());
        MessageDBHelper.init(getApplicationContext());
//        FollowDBHelper.init(getApplicationContext());
        FollowDBHelper2.init(getApplicationContext());
        TempleMessageHelper.init(getApplicationContext());
        CongnoDBHelper.init(getApplicationContext());
    }
}
