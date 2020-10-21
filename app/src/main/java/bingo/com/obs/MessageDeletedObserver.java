package bingo.com.obs;

import android.content.AsyncQueryHandler;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import java.lang.ref.WeakReference;

public class MessageDeletedObserver extends ContentObserver {

    private Context context;

    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public MessageDeletedObserver(Handler handler, Context context) {
        super(handler);

        this.context = context;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);

//        Log.d("MessageDeletedObserver", "onChange message.");
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);

        Log.d("MessageDeletedObserver", "onChange: " + uri);

        final String _id =  uri.toString().substring(uri.toString().lastIndexOf("/") + 1);

        WeakReference<AsyncQueryHandler> queryHandler = new WeakReference<AsyncQueryHandler>(new AsyncQueryHandler(context.getContentResolver()) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                Log.d("MessageDeletedObserver", "AsyncQueryHandler: " + "Message hasChange: " + cursor.getCount());

                if (cursor.getCount() == 0)
                {
                    //This is delete message success.
                    /*TempleMessageHelper.deleteMessageWithId(_id);*/
                }
                else
                {

                }
            }
        });

        queryHandler.get().startQuery(0,
                null,
                uri,
                null,
                null,
                null,
                null);
    }
}
