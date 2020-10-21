package bingo.com.callbacks;

import android.database.sqlite.SQLiteDatabase;

public interface RunnableDb {
    void run(SQLiteDatabase db);
}
