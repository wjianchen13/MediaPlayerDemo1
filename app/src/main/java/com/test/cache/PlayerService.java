package com.test.cache;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class PlayerService extends Service {

    private static String ACTION_START = "ACTION_START";
    private static String ACTION_PREVIOUS = "ACTION_PREVIOUS";
    private static String ACTION_NEXT = "ACTION_NEXT";
    private static String ACTION_GONE = "ACTION_GONE";
    private static String ACTION_STOP = "ACTION_STOP";

    public static void startPlayerService() {
        Intent intent = new Intent(MyApplication.getContext(), PlayerService.class);
        intent.setAction(ACTION_START);
        MyApplication.getContext().startService(intent);
    }

    public static void playerPreviousService() {
        Intent intent = new Intent(MyApplication.getContext(), PlayerService.class);
        intent.setAction(ACTION_PREVIOUS);
        MyApplication.getContext().startService(intent);
    }
    public static void playerNextService() {
        Intent intent = new Intent(MyApplication.getContext(), PlayerService.class);
        intent.setAction(ACTION_NEXT);
        MyApplication.getContext().startService(intent);
    }

    public static void stopPlayerService() {
        Intent intent = new Intent(MyApplication.getContext(), PlayerService.class);
        MyApplication.getContext().stopService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 开始前台服务
        Notifier.getInstance().init(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            if (ACTION_START.equals(intent.getAction())) {
                AudioPlayer.getInstance().play ();
            } else if (ACTION_PREVIOUS.equals(intent.getAction())) {
                AudioPlayer.getInstance().previous();
            } else if (ACTION_NEXT.equals(intent.getAction())) {
                AudioPlayer.getInstance().next();
            }
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AudioPlayer.getInstance().release();
        Notifier.getInstance().stopForeground();
    }
}
