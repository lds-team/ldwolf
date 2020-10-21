package bingo.com.helperdb.db2;

import android.content.Context;

import bingo.com.base.BaseDBHelper;
import bingo.com.model.FollowModel;

public class FollowDBHelper2 extends BaseDBHelper {

    private static FollowDatabase2 database;

    public static FollowDatabase2 init(Context context) {

        synchronized (FollowDBHelper2.class)
        {
            if (database == null)
            {
                database = new FollowDatabase2(context);
            }
        }

        return database;
    }

    public static void closeDB() {

        synchronized (FollowDBHelper2.class)
        {
            if (database != null)
            {
                database.close();
            }

            database = null;
        }
    }

    public static FollowDatabase2 getDatabase() {
        return database;
    }
}
