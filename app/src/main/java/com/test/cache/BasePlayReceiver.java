package com.test.cache;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

public abstract class BasePlayReceiver extends BroadcastReceiver {

    public static String ACTION = "com.example.android.myapplication.PLAY_RECEIVER";
    public static String EXTRA_TYPE = "type";

    public static String TYPE_ON_INIT_SOURCE = "onInitSource";
    public static String EXTRA_SOURCE = "source";

    public static String TYPE_ON_PREPARED = "onPrepared";

    public static String TYPE_ON_COMPLETION = "onCompletion";

    public static String TYPE_ON_PLAY_STATUS = "onPlayStatus";

    public static String TYPE_ON_BUFFERING_UPDATE = "onBufferingUpdate";
    public static String EXTRA_PERCENT = "percent";

    public static String TYPE_ON_ERROR = "onError";
    public static String EXTRA_WHAT = "what";
    public static String EXTRA_EXTRA = "extra";

    public static void registerReceiver(Context context, BasePlayReceiver basePlayReceiver) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BasePlayReceiver.ACTION);
        //注册
        context.registerReceiver(basePlayReceiver, filter);
    }

    public static void unregisterReceiver(Context context, BasePlayReceiver basePlayReceiver) {
        if (basePlayReceiver != null) {
            context.unregisterReceiver(basePlayReceiver);
        }
    }

    public static void sendBroadcastInitSource(Context context, AlbumProgramItemBean song) {
        Intent intent = new Intent();
        intent.setAction(BasePlayReceiver.ACTION);
        intent.putExtra(BasePlayReceiver.EXTRA_TYPE, TYPE_ON_INIT_SOURCE);
        intent.putExtra(BasePlayReceiver.EXTRA_SOURCE, song);
        context.sendBroadcast(intent);
    }

    public static void sendBroadcastPrepared(Context context) {
        Intent intent = new Intent();
        intent.setAction(BasePlayReceiver.ACTION);
        intent.putExtra(BasePlayReceiver.EXTRA_TYPE, TYPE_ON_PREPARED);
        context.sendBroadcast(intent);
    }

    public static void sendBroadcastCompletion(Context context) {
        Intent intent = new Intent();
        intent.setAction(BasePlayReceiver.ACTION);
        intent.putExtra(BasePlayReceiver.EXTRA_TYPE, TYPE_ON_COMPLETION);
        context.sendBroadcast(intent);
    }

    public static void sendBroadcastPlayStatus(Context context) {
        Intent intent = new Intent();
        intent.setAction(BasePlayReceiver.ACTION);
        intent.putExtra(BasePlayReceiver.EXTRA_TYPE, TYPE_ON_PLAY_STATUS);
        context.sendBroadcast(intent);
    }

    public static void sendBroadcastBufferingUpdate(Context context, int percent) {
        Intent intent = new Intent();
        intent.setAction(BasePlayReceiver.ACTION);
        intent.putExtra(BasePlayReceiver.EXTRA_TYPE, TYPE_ON_BUFFERING_UPDATE);
        intent.putExtra(BasePlayReceiver.EXTRA_PERCENT, percent);
        context.sendBroadcast(intent);
    }

    public static void sendBroadcastError(Context context, int what, int extra) {
        Intent intent = new Intent();
        intent.setAction(BasePlayReceiver.ACTION);
        intent.putExtra(BasePlayReceiver.EXTRA_TYPE, TYPE_ON_ERROR);
        intent.putExtra(BasePlayReceiver.EXTRA_WHAT, what);
        intent.putExtra(BasePlayReceiver.EXTRA_EXTRA, extra);
        context.sendBroadcast(intent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!BasePlayReceiver.ACTION.equals(intent.getAction()) || intent.getExtras() == null) {
            return;
        }
        Bundle bundle = intent.getExtras();
        String type = bundle.getString(EXTRA_TYPE);
        if (TYPE_ON_INIT_SOURCE.equals(type)) {
            onInitSource((AlbumProgramItemBean) bundle.getParcelable(EXTRA_SOURCE));
        } else if (TYPE_ON_PREPARED.equals(type)) {
            onPrepared();
        } else if (TYPE_ON_COMPLETION.equals(type)) {
            onCompletion();
        } else if (TYPE_ON_PLAY_STATUS.equals(type)) {
            onPlayStatus();
        } else if (TYPE_ON_BUFFERING_UPDATE.equals(type)) {
            onBufferingUpdate(bundle.getInt(EXTRA_PERCENT));
        } else if (TYPE_ON_ERROR.equals(type)) {
            onError(bundle.getInt(EXTRA_WHAT), bundle.getInt(EXTRA_EXTRA));
        }
    }

    /**
     * 初始化信息
     *
     * @param source
     */
    protected abstract void onInitSource(AlbumProgramItemBean source);

    /**
     * 资源准备完成
     */
    protected abstract void onPrepared();

    /**
     * 资源播放完成
     */
    protected abstract void onCompletion();

    /**
     * 播放状态的改变
     */
    protected abstract void onPlayStatus();

    /**
     * 缓存进度
     *
     * @param percent
     */
    protected abstract void onBufferingUpdate(int percent);

    /**
     * 播放错误
     *
     * @param what
     * @param extra
     */
    protected abstract void onError(int what, int extra);
}