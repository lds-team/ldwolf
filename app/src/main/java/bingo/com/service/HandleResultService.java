package bingo.com.service;

import android.app.IntentService;
import android.content.Intent;

public class HandleResultService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public HandleResultService() {
        super("HandleResultService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
