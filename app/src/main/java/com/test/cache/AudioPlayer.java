package com.test.cache;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.util.Log;

import com.danikula.videocache.HttpProxyCacheServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AudioPlayer implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnErrorListener {

    private static final String TAG = "AudioPlayer";

    /**
     * 播放方式
     */
    public enum PlayMode {
        /**
         * 顺序
         */
        ORDER,
        /**
         * 列表循环
         */
        LOOP,
        /**
         * 随机
         */
        RANDOM,
        /**
         * 单曲循环
         */
        REPEAT
    }

    private ManagedMediaPlayer mMediaPlayer;
    private List<AlbumProgramItemBean> mQueue;
    private int mQueueIndex;
    private PlayMode mPlayMode = PlayMode.ORDER;
    private AlbumProgramItemBean nowPlaying;
    private WifiManager.WifiLock wifiLock;
    private AudioFocusManager audioFocusManager;
    private HttpProxyCacheServer proxy;

    private static class SingletonHolder {
        private static AudioPlayer instance = new AudioPlayer();
    }

    public static AudioPlayer getInstance() {
        return SingletonHolder.instance;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        BasePlayReceiver.sendBroadcastCompletion(MyApplication.getContext());
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        start();
        BasePlayReceiver.sendBroadcastPrepared(MyApplication.getContext());
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        BasePlayReceiver.sendBroadcastBufferingUpdate(MyApplication.getContext(), percent);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e(TAG, "MediaPlayer onError what " + what + " extra " + extra);
        release();
        next();
        BasePlayReceiver.sendBroadcastError(MyApplication.getContext(), what, extra);
        return false;
    }

    public void init() {
        mMediaPlayer = new ManagedMediaPlayer();
        // 使用唤醒锁
        mMediaPlayer.setWakeMode(MyApplication.getContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnErrorListener(this);
        // 初始化wifi锁
        wifiLock = ((WifiManager) MyApplication.getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE)).createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock");
        // 初始化音频焦点管理器
        audioFocusManager = new AudioFocusManager(MyApplication.getContext());
        // 初始化AndroidVideoCache
        proxy = HttpProxyCacheUtil.getAudioProxy();
    }


    public void setPlayIndex(int index) {
        this.mQueueIndex = index;
    }

    public void setQueue(List<AlbumProgramItemBean> mQueue) {
        this.mQueue = mQueue;
    }

    public void setQueueAndIndex(List<AlbumProgramItemBean> mQueue, int mQueueIndex) {
        this.mQueue = mQueue;
        this.mQueueIndex = mQueueIndex;
    }

    private void play(AlbumProgramItemBean source) {
        if (source == null) {
            Log.e(TAG, "没有可用资源");
            return;
        }
        if (mMediaPlayer == null) {
            init();
        }
        if (getStatus() == ManagedMediaPlayer.Status.INITIALIZED) {
            Log.e(TAG, "正在准备上一个资源，请稍候");
            return;
        }
        // 更新播放器状态
        mMediaPlayer.reset();

        nowPlaying = source;
        // 更新Notification
        Notifier.getInstance().showPlayInfo(source);
        // 向MainActivity发送EventBus
//        EventBus.getDefault().post(new MainActivityEvent());
        // 发送初始化资源信息的广告
        BasePlayReceiver.sendBroadcastInitSource(MyApplication.getContext(), source);
        // 获取音频地址（音频地址一般私有）
//        Call<BaseCallBackVo<String>> call = HttpClientFactory.getAppApiClientInstance().getAlbumAddress(source.getId());
//        call.enqueue(new Callback<BaseCallBackVo<String>>() {
//            @Override
//            public void onResponse(Call<BaseCallBackVo<String>> call, Response<BaseCallBackVo<String>> response) {
//                if (response.code() == 200 && response.body() != null) {
//                    if (response.body().getStatus() == 200) {
//                        String url = response.body().getData();
//                        url = proxy.getProxyUrl(url);
//                        play(url);
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<BaseCallBackVo<String>> call, Throwable t) {
//                BasePlayReceiver.sendBroadcastPrepared(MyApplication.getContext());
//            }
//        });
    }

    private void play(String dataSource) {
//        mMediaPlayer.reset();
        try {
            mMediaPlayer.setDataSource(dataSource);
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "该资源无法播放");
            BasePlayReceiver.sendBroadcastPrepared(MyApplication.getContext());
        }
    }

    private void start() {
        // 获取音频焦点
        if (!audioFocusManager.requestAudioFocus()) {
            Log.e(TAG, "获取音频焦点失败");
        }
        mMediaPlayer.start();
        // 启用wifi锁
        wifiLock.acquire();
        // 更新notification
        Notifier.getInstance().showPlayInfo(nowPlaying);
        // 向MainActivity发送EventBus
//        EventBus.getDefault().post(new MainActivityEvent());
        // 发送播放状态的广播
        BasePlayReceiver.sendBroadcastPlayStatus(MyApplication.getContext());
    }

    public void pause() {
        if (getStatus() == ManagedMediaPlayer.Status.STARTED) {
            mMediaPlayer.pause();
            // 关闭wifi锁
            if (wifiLock.isHeld()) {
                wifiLock.release();
            }
            // 发送播放状态的广播
            BasePlayReceiver.sendBroadcastPlayStatus(MyApplication.getContext());
            // 更新notification
            Notifier.getInstance().showPlayInfo(nowPlaying);
            // 向MainActivity发送EventBus
//            EventBus.getDefault().post(new MainActivityEvent());
            // 取消音频焦点
            if (audioFocusManager != null) {
                audioFocusManager.abandonAudioFocus();
            }
        }
    }

    public void resume() {
        if (getStatus() == ManagedMediaPlayer.Status.PAUSED) {
            start();
        }
    }

    public void stop() {
        if (getStatus() == ManagedMediaPlayer.Status.STARTED
                || getStatus() == ManagedMediaPlayer.Status.PAUSED
                || getStatus() == ManagedMediaPlayer.Status.COMPLETED) {
            mMediaPlayer.stop();
            // 发送播放状态的广播
            BasePlayReceiver.sendBroadcastPlayStatus(MyApplication.getContext());
            // 更新notification
            Notifier.getInstance().showPlayInfo(nowPlaying);
            // 向MainActivity发送EventBus
//            EventBus.getDefault().post(new MainActivityEvent());
            // 取消音频焦点
            if (audioFocusManager != null) {
                audioFocusManager.abandonAudioFocus();
            }
        }
    }

    public void release() {
        if (mMediaPlayer == null) {
            return;
        }
        nowPlaying = null;
        Log.d(TAG, "release");
        mMediaPlayer.release();
        mMediaPlayer = null;
        // 取消音频焦点
        if (audioFocusManager != null) {
            audioFocusManager.abandonAudioFocus();
        }
        // 关闭wifi锁
        if (wifiLock.isHeld()) {
            wifiLock.release();
        }
        wifiLock = null;
        audioFocusManager = null;
        proxy = null;
        // 向MainActivity发送EventBus
//        EventBus.getDefault().post(new MainActivityEvent());
    }

    public void seekTo(int msec) {
        if (getStatus() == ManagedMediaPlayer.Status.STARTED
                || getStatus() == ManagedMediaPlayer.Status.PAUSED
                || getStatus() == ManagedMediaPlayer.Status.COMPLETED) {
            mMediaPlayer.seekTo(msec);
        }
    }

    /**
     * 播放
     */
    public void play() {
        AlbumProgramItemBean albumProgramItemBean = getPlaying(mQueueIndex);
        play(albumProgramItemBean);
    }

    public void next() {
        AlbumProgramItemBean albumProgramItemBean = getNextPlaying();
        play(albumProgramItemBean);
    }

    public void previous() {
        AlbumProgramItemBean albumProgramItemBean = getPreviousPlaying();
        play(albumProgramItemBean);
    }

    public AlbumProgramItemBean getNowPlaying() {
        if (nowPlaying != null) {
            return nowPlaying;
        } else {
            return getPlaying(mQueueIndex);
        }
    }

    public int getCurrentPosition() {
        if (getStatus() == ManagedMediaPlayer.Status.STARTED
                || getStatus() == ManagedMediaPlayer.Status.PAUSED) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public int getDuration() {
        if (getStatus() == ManagedMediaPlayer.Status.STARTED
                || getStatus() == ManagedMediaPlayer.Status.PAUSED) {
            return mMediaPlayer.getDuration();
        }
        return 0;
    }

    public ManagedMediaPlayer.Status getStatus() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getState();
        } else {
            return ManagedMediaPlayer.Status.STOPPED;
        }
    }

    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    public PlayMode getPlayMode() {
        return mPlayMode;
    }

    public void setPlayMode(PlayMode playMode) {
        mPlayMode = playMode;
    }

    public int getQueueIndex() {
        return mQueueIndex;
    }

    public List<AlbumProgramItemBean> getQueue() {
        return mQueue == null ? new ArrayList<AlbumProgramItemBean>() : mQueue;
    }

    private AlbumProgramItemBean getNextPlaying() {
        switch (mPlayMode) {
            case ORDER:
                mQueueIndex = mQueueIndex + 1;
                return getPlaying(mQueueIndex);
            case LOOP:
                mQueueIndex = (mQueueIndex + 1) % mQueue.size();
                return getPlaying(mQueueIndex);
            case RANDOM:
                mQueueIndex = new Random().nextInt(mQueue.size()) % mQueue.size();
                return getPlaying(mQueueIndex);
            case REPEAT:
                return getPlaying(mQueueIndex);
            default:
                break;
        }
        return null;
    }

    private AlbumProgramItemBean getPreviousPlaying() {
        switch (mPlayMode) {
            case ORDER:
                mQueueIndex = mQueueIndex - 1;
                return getPlaying(mQueueIndex);
            case LOOP:
                mQueueIndex = (mQueueIndex + mQueue.size() - 1) % mQueue.size();
                return getPlaying(mQueueIndex);
            case RANDOM:
                mQueueIndex = new Random().nextInt(mQueue.size()) % mQueue.size();
                return getPlaying(mQueueIndex);
            case REPEAT:
                return getPlaying(mQueueIndex);
            default:
                break;
        }
        return null;
    }

    private AlbumProgramItemBean getPlaying(int index) {
        if (mQueue != null && !mQueue.isEmpty() && index >= 0 && index < mQueue.size()) {
            return mQueue.get(index);
        } else {
            return null;
        }
    }

}