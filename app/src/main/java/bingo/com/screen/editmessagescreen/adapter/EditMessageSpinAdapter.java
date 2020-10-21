package bingo.com.screen.editmessagescreen.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.SimpleCursorAdapter;

import bingo.com.helperdb.db.ContactDatabase;

@Deprecated
public class EditMessageSpinAdapter extends SimpleCursorAdapter {

    public EditMessageSpinAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);

    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        super.bindView(view, context, cursor);

        view.setTag(cursor.getString(cursor.getColumnIndex(ContactDatabase.PHONE)));
    }
}
