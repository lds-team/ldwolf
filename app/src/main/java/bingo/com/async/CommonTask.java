package bingo.com.async;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class CommonTask {

    public static class ThreadTask extends Thread {

        public ThreadTask(Runnable target) {
            super(target);
        }

        @Override
        public synchronized void start() {
            super.start();
        }
    }

    public static class AsyncSingleThread {

        private ExecutorService eventAsync = Executors.newSingleThreadExecutor();
        private Executor eventMain = new MainThreadExecutor();
        private List<Future<?>> futures;
        private List<Runnable> cacheOnForceStop;

        private static AsyncSingleThread instance;

        private AtomicInteger cntTask = new AtomicInteger(0);

        protected AsyncSingleThread() {
            cacheOnForceStop = new ArrayList<>();
            futures = new ArrayList<>();
        }

        public void clean() {

        }

        public void stop() {
            eventAsync.shutdown();
        }

        public void forceStop() {
            List<Runnable> feature = eventAsync.shutdownNow();
            cacheOnForceStop.addAll(feature);
        }

        public boolean hasCacheTask() {
            return cacheOnForceStop.size() > 0;
        }

        public void clearCache() {
            cacheOnForceStop.clear();
        }

        public int getCacheTaskCount() {
            return cntTask.get();
        }

        public static AsyncSingleThread get() {
            synchronized (AsyncSingleThread.class)
            {
                if (instance == null)
                    instance = new AsyncSingleThread();
            }

            return instance;
        }

        public AsyncSingleThread execute(Runnable... taskAsync) {

            return execute(null, taskAsync);
        }

        public AsyncSingleThread execute(final Context context, Runnable... taskAsync) {

            final WeakReference<Context> weakContext = new WeakReference<>(context);

            if (checkAllDone())
            {
                futures.clear();
                if (context != null)
                    Toast.makeText(weakContext.get(), "Hệ thống bắt đầu chạy...", Toast.LENGTH_LONG).show();
            }
            else
            {
                if (context != null)
                    Toast.makeText(weakContext.get(), "Hệ thống đã thêm vào tác vụ chờ.", Toast.LENGTH_LONG).show();
            }

            final Runnable result = new Runnable() {
                @Override
                public void run() {
                    Log.d("AsyncSingleThread", "Finish 1 task.");
                    cntTask.decrementAndGet();
                }
            };

            cntTask.addAndGet(taskAsync.length);
            for (final Runnable task: taskAsync)
            {
                Future<?> future = eventAsync.submit(new Runnable() {
                    @Override
                    public void run() {
                        task.run();
                        result.run();
                    }
                });
                futures.add(future);
            }

            return this;
        }

        public AsyncSingleThread executeOnMain(Runnable... taskMain) {
            for (Runnable task: taskMain)
            {
                eventMain.execute(task);
            }

            return this;
        }

        public boolean checkAllDone() {
            boolean allDone = true;
            List<Future<?>> cachefutures = new ArrayList<>();
            for(Future<?> future : futures){
                allDone &= future.isDone();

                if (!allDone)
                {
                    cachefutures.add(future);
                }
            }

            this.futures.clear();
            this.futures = cachefutures;

            return allDone;
        }
    }

    private static class MainThreadExecutor implements Executor {
        private final Handler handler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable runnable) {
            handler.post(runnable);
        }
    }
}
