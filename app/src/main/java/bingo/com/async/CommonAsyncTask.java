package bingo.com.async;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

import bingo.com.base.BaseFragment;

public class CommonAsyncTask<FM extends BaseFragment> extends AsyncTask<Runnable, Void, Void> {

    final Runnable postExecuteTask;

    ProgressDialog progress;

    WeakReference<FM> context;

    public CommonAsyncTask(FM context, Runnable postExecuteTask) {
        this.context = new WeakReference<FM>(context);
        this.postExecuteTask = postExecuteTask;

        if (progress == null) {
            progress = createProgressDialog();
        }
        progress.setMessage("Loading...");
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progress.show();
    }

    @Override
    protected Void doInBackground(Runnable... params) {
        for (Runnable param : params) {
            param.run();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if (context.get().isVisible())
        {
            if (progress != null && progress.isShowing()) {
                progress.dismiss();
            }
            if (postExecuteTask != null) {
                postExecuteTask.run();
            }
        }
    }

    private ProgressDialog createProgressDialog() {
        ProgressDialog dialog = new ProgressDialog(context.get().getActivity());
        dialog.setIndeterminate(true);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        return dialog;
    }
}