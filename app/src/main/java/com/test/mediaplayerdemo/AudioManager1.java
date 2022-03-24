package com.test.mediaplayerdemo;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.danikula.videocache.HttpProxyCacheServer;
import com.test.cache.HttpProxyCacheUtil;


public class AudioManager1 implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener{

    private static AudioManager1 INSTANCE;

    private MediaPlayer mMediaPlayer;

    private Context mContext;

    /**
     * 是否正在准备
     */
    private boolean isPreparing = false;

    /**
     * 当前准备
     */
    private AudioBean preBean;

    private Handler mHandler;

    /**
     * 最后的一个操作，如果是prepareing的时候，所有操作都保存在这里
     */
    private AudioBean curBean;

    private AudioManager1(Context context){
        this.mContext = context;
        initMediaPlayer();
        mHandler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                preparing();
            }
        };
    }

    public static AudioManager1 getInstance(Context context){
        if (INSTANCE == null) {
            synchronized(AudioManager1.class) {
                if (INSTANCE == null){
                    INSTANCE = new AudioManager1(context);
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 播放音频
     * @param name
     */
    public void playAssertAudio(String name) {
        if(mMediaPlayer != null && mContext != null) {
            try {
                mMediaPlayer.reset();
                AssetFileDescriptor fd = mContext.getAssets().openFd(name);
                mMediaPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
                mMediaPlayer.prepareAsync();
                mMediaPlayer.setOnPreparedListener(this);
                mMediaPlayer.setOnCompletionListener(this);

                mMediaPlayer.setOnErrorListener(this);
            } catch (Exception e) {
                System.out.println("===============> mediaplayer play Exception:" + e.toString());
                e.printStackTrace();
            }
        }
    }

    /**
     * 停止播放
     */
    protected void stopmp3() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            Toast.makeText(mContext, "停止播放", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 播放音频
     * @param url
     */
    public void playAudio(final String url) {
        System.out.println("===========================================================> playAudio: ");
        if(isPreparing) {
            setCurdata(url, AudioBean.START, true);
            return ;
        }
        if(mMediaPlayer != null && mContext != null) {
            try {
                setPredata(url, AudioBean.START, true);

                mMediaPlayer.reset();
                HttpProxyCacheServer proxy = HttpProxyCacheUtil.getAudioProxy(mContext);
                mMediaPlayer.setDataSource(proxy.getProxyUrl(url));
                isPreparing = true;
                mMediaPlayer.prepareAsync();
                mMediaPlayer.setOnPreparedListener(this);
                mMediaPlayer.setOnCompletionListener(this);

                mMediaPlayer.setOnErrorListener(this);
            } catch (Exception e) {
                System.out.println("===============> mediaplayer play Exception:" + e.toString());
                e.printStackTrace();
            }
        }
    }

    /**
     * 释放MediaPlayer
     */
    public void clear() {
        if (mMediaPlayer != null) {
            if(mMediaPlayer.isPlaying())
                mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    /**
     * 停止播放
     */
    public void stop(String url) {
        System.out.println("===========================================================> stop: ");
        System.out.println("===============> pause2 url: " + url);
        if(isPreparing) { // 如果mediaplayer当前正在准备数据，则只保留数据
            setCurdata(url, AudioBean.STOP, true);
        } else {
            System.out.println("===============> pause2 mediaplayer isPlaying: " + mMediaPlayer.isPlaying());
            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
                System.out.println("===============> pause2 mediaplayer stop");
            }
        }
    }

    /**
     * 准备播放
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        System.out.println("===============> onPrepared: sleep");
        mHandler.sendEmptyMessageDelayed(1, 0); // 模拟mediaplayer准备数据中间时长，延时
    }

    private void preparing() {
        System.out.println("===========================================================> preparing: ");
        isPreparing = false;
        if(preBean != null && curBean != null && curBean.isNeed()) { // 需要处理
            System.out.println("===============> mediaplayer isNedd true");
            // 判断url是否一样，
            if(preBean.getUrl().equals(curBean.getUrl())) { // 比如点击暂停，又点击开始
                System.out.println("===============> url 相等");
                if(curBean.getStatus() == AudioBean.START && curBean.isNeed()) { // 如果同一个url并且需要播放，就直接start，比如prepare的时候，点击了暂停，在点击开始，同一个音频
                    System.out.println("===============> 开始播放");
                    start();
                } else { // 同一个url，如果是停止，就直接不处理，后面把isNeed设置成false
                    System.out.println("===============> 不需要播放");
                }
            } else { // 如果不同url，说明点击了另外的音频，
                System.out.println("===============> url 不相等");
                if(curBean.getStatus() == AudioBean.START && curBean.isNeed()) { // 重新play新的url
                    System.out.println("===============> 重新play新的url");
                    playAudio(curBean.getUrl());
                } else { // 如果停止的话无需处理
                    System.out.println("===============> 不需要播放");
                }
            }
            setCurdata("", AudioBean.STOP, false);
        } else { // 如果最后一次操作不需要处理，则走正常流程
            System.out.println("===============> mediaplayer isNedd false");
            start();
        }
    }

    /**
     * 完成
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        System.out.println("===============> mediaplayer onCompletion");
    }

    /**
     * 播放错误
     */
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        System.out.println("===============> mediaplayer onError");
        return false;
    }

    /**
     * init
     */
    private void initMediaPlayer() {
        if(mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(android.media.AudioManager.STREAM_MUSIC);
    }

    /**
     * 开始播放
     */
    private void start() {
        if (mMediaPlayer != null)
            mMediaPlayer.start();
    }

    /**
     * 保存prepare前的操作
     */
    private void setPredata(String url, int status, boolean isNeed) {
        if(preBean == null) {
            preBean = new AudioBean();
        }
        if(preBean != null) {
            preBean.setUrl(url);
            preBean.setStatus(status);
            preBean.setNeed(isNeed);
        }
    }

    /**
     * 保存prepare中的操作
     */
    private void setCurdata(String url, int status, boolean isNeed) {
        if(curBean == null) {
            curBean = new AudioBean();
        }
        if(curBean != null) {
            curBean.setUrl(url);
            curBean.setStatus(status);
            curBean.setNeed(isNeed);
        }
    }


}
