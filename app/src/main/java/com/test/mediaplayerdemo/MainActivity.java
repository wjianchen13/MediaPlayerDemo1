package com.test.mediaplayerdemo;

import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.danikula.videocache.HttpProxyCacheServer;
import com.test.cache.HttpProxyCacheUtil;

public class MainActivity extends AppCompatActivity  {

    private Button btn_play;
    private Button  btn_pause, btn_replay, btn_stop;
    private Button btn_play2;
    private Button btn_pause2;

    private Button btn_play3, btn_pause3;
    private Button btn_play4, btn_pause4;
    private MediaPlayer mediaPlayer;
    private TextView tvTest;

    private TextView tvTest3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mediaPlayer = new MediaPlayer();
        // 设置音频流的类型
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        btn_play = (Button) findViewById(R.id.btn_play);
        btn_pause = (Button) findViewById(R.id.btn_pause);
        btn_replay = (Button) findViewById(R.id.btn_replay);
        btn_stop = (Button) findViewById(R.id.btn_stop);

        btn_play2 = findViewById(R.id.btn_play2);
        btn_pause2 = findViewById(R.id.btn_pause2);
        tvTest = findViewById(R.id.tv_test);

        btn_play3 = findViewById(R.id.btn_play3);
        btn_pause3 = findViewById(R.id.btn_pause3);
        tvTest3 = findViewById(R.id.tv_test3);
        btn_play4 = findViewById(R.id.btn_play4);
        btn_pause4 = findViewById(R.id.btn_pause4);

        btn_play.setOnClickListener(click);
        btn_pause.setOnClickListener(click);
        btn_replay.setOnClickListener(click);
        btn_stop.setOnClickListener(click);
        btn_play2.setOnClickListener(click);
        btn_pause2.setOnClickListener(click);
        btn_play3.setOnClickListener(click);
        btn_pause3.setOnClickListener(click);
        btn_play4.setOnClickListener(click);
        btn_pause4.setOnClickListener(click);
    }

    String mUrl = "http://tapi.95xiu.com/song.mp3";
    String mUrl1 = "https://www.twle.cn/static/i/song.mp3";

    private View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_play:
//                    play("https://tapi.95xiu.com/111.wma");
//                    playmp3();
                    AudioManager1.getInstance(MainActivity.this).playAssertAudio("vivo.m4a");
                    break;
                case R.id.btn_pause:
//                    pause();
//                    play("https://www.twle.cn/static/i/song.mp3");
//                    stopmp3();
                    //                    playmp3();
                    AudioManager1.getInstance(MainActivity.this).stopmp3();
                    break;
                case R.id.btn_replay:
//                    replay();
                    playwma();
                    break;
                case R.id.btn_stop:
//                    stop();
                    stopmp3();
                    break;

                case R.id.btn_play2:
                    play2(mUrl, AudioBean.START);
                    break;
                case R.id.btn_pause2:
                    pause2(mUrl, AudioBean.STOP);
                    break;

                case R.id.btn_play3:
                    play3(mUrl);
                    break;
                case R.id.btn_pause3:
                    pause3(mUrl);
                    break;
                case R.id.btn_play4:
                    play3(mUrl1);
                    break;
                case R.id.btn_pause4:
                    pause3(mUrl1);
                    break;

                default:
                    break;
            }
        }
    };

    /**
     * 播放音乐
     */
    protected void playmp3() {
        if(mediaPlayer != null) {
            try {
                mediaPlayer.reset();
                // 设置指定的流媒体地址
                AssetFileDescriptor fd = getAssets().openFd("333.mp3");
                mediaPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
                // 通过异步的方式装载媒体资源
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        // 装载完毕 开始播放流媒体
                        if(mediaPlayer != null)
                            mediaPlayer.start();
                        Toast.makeText(MainActivity.this, "开始播放", Toast.LENGTH_SHORT).show();
                        // 避免重复播放，把播放按钮设置为不可用
//                        btn_play.setEnabled(false);
                    }
                });
                // 设置循环播放
                // mediaPlayer.setLooping(true);
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        // 在播放完毕被回调
//                        btn_play.setEnabled(true);
                    }
                });

                mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

                    // https://blog.51cto.com/readingcoding/2392981
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        // 如果发生错误，重新播放
//                        replay();
                        Toast.makeText(MainActivity.this, "播放失败", Toast.LENGTH_SHORT).show();
                        btn_stop.setText("播放失败");
                        return false;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "播放失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 停止播放
     */
    protected void stopmp3() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            Toast.makeText(this, "停止播放", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 播放音乐
     */
    protected void playwma() {
        if(mediaPlayer != null) {
            try {
                mediaPlayer.reset();
                // 设置指定的流媒体地址
                AssetFileDescriptor fd = getAssets().openFd("111.wma");
                mediaPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
                // 通过异步的方式装载媒体资源
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        // 装载完毕 开始播放流媒体
                        if(mediaPlayer != null)
                            mediaPlayer.start();
                        Toast.makeText(MainActivity.this, "开始播放", Toast.LENGTH_SHORT).show();
                        // 避免重复播放，把播放按钮设置为不可用
//                        btn_play.setEnabled(false);
                    }
                });
                // 设置循环播放
                // mediaPlayer.setLooping(true);
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        // 在播放完毕被回调
//                        btn_play.setEnabled(true);
                    }
                });

                mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

                    // https://blog.51cto.com/readingcoding/2392981
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        // 如果发生错误，重新播放
//                        replay();
                        Toast.makeText(MainActivity.this, "播放失败", Toast.LENGTH_SHORT).show();
                        btn_stop.setText("播放失败");
                        return false;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "播放失败", Toast.LENGTH_SHORT).show();
            }
        }
    }


























    private void play3(String url) {
        AudioManager1.getInstance(this).playAudio(url);
    }

    private void pause3(String url) {
        AudioManager1.getInstance(this).stop(url);
    }













    /**
     * 是否正在准备
     */
    private boolean isPreparing = false;
    private int status = 1; // 1停止， 2 运行

    private AudioBean preBean = new AudioBean(); // 当前准备

    private AudioBean curBean = new AudioBean(); // 最后的一个操作，如果是prepareing的时候，所有操作都保存在这里


    private void play2(final String url, final int status) {
        if(isPreparing) {
            setCurdata(url, status, true);
            return ;
        }
        if(mediaPlayer != null) {
            try {
                mediaPlayer.reset();
                HttpProxyCacheServer proxy = HttpProxyCacheUtil.getAudioProxy(this);
                mediaPlayer.setDataSource(proxy.getProxyUrl(url));
                isPreparing = true;
                setPredata(url, status, true);
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        System.out.println("===============> onPrepared: sleep");
                        try { // 模仿网络加载，延时3s
                            Thread.sleep(3000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        System.out.println("===============> onPrepared: ");
                        isPreparing = false;
                        if(preBean != null && curBean != null && curBean.isNeed()) { // 需要处理
                            System.out.println("===============> mediaplayer isNedd true");
                            // 判断url是否一样，
                            if(preBean.getUrl().equals(curBean.getUrl())) {
                                if(curBean.getStatus() == AudioBean.START) { // 如果同一个url并且需要播放，就直接start，比如prepare的时候，点击了暂停，在点击开始，同一个音频
                                    start();
                                } else { // 同一个url，如果是停止，就直接不处理，后面把isNeed设置成false

                                }
                            } else { // 如果不同url，说明点击了另外的音频，
                                if(curBean.getStatus() == AudioBean.START) { // 重新play新的url
                                    play2(url, status);
                                } else { // 如果停止的话无需处理

                                }
                            }
                            setCurdata("", AudioBean.STOP, false);
                        } else { // 如果最后一次操作不需要处理，则走正常流程
                            System.out.println("===============> mediaplayer isNedd false");
                            start();
                        }
                    }
                });
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) { // 这里播放完成需要回调给ui，初始化成未播放的状态
                        System.out.println("===============> mediaplayer onCompletion");
                    }
                });

                mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

                    // https://blog.51cto.com/readingcoding/2392981
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        System.out.println("===============> mediaplayer onError");
                        return false;
                    }
                });
            } catch (Exception e) {
                System.out.println("===============> mediaplayer Exception:" + e.toString());
                e.printStackTrace();
            }
        }
    }

    private void pause2(String url, int status) {
        System.out.println("===============> pause2 url: " + url + "   status: " + status);
        if(isPreparing) { // 如果mediaplayer当前正在准备数据，则只保留数据
            setCurdata(url, status, true);
        } else {
            System.out.println("===============> pause2 mediaplayer isPlaying: " + mediaPlayer.isPlaying());
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                System.out.println("===============> pause2 mediaplayer stop");
            }
        }
    }

    private void start() {
        if (mediaPlayer != null)
            mediaPlayer.start();
    }

    private void setPredata(String url, int status, boolean isNeed) {
        if(preBean != null) {
            preBean.setUrl(url);
            preBean.setStatus(status);
            preBean.setNeed(isNeed);
        }
    }

    private void setCurdata(String url, int status, boolean isNeed) {
        if(curBean != null) {
            curBean.setUrl(url);
            curBean.setStatus(status);
            curBean.setNeed(isNeed);
        }
    }


























    /**
     * 播放音乐
     */
    protected void play(String url) {
        if(mediaPlayer != null) {
            try {
                mediaPlayer.reset();
                // 设置指定的流媒体地址
//                HttpProxyCacheServer proxy = HttpProxyCacheUtil.getAudioProxy(this);
                int b = 2;
                // https://tapi.95xiu.com/111.wma
//                mediaPlayer.setDataSource(proxy.getProxyUrl("https://www.twle.cn/static/i/song.mp3"));
                mediaPlayer.setDataSource("http://tapi.95xiu.com/song.mp3");
                // 通过异步的方式装载媒体资源
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        // 装载完毕 开始播放流媒体
                        if(mediaPlayer != null)
                            mediaPlayer.start();
                        Toast.makeText(MainActivity.this, "开始播放", Toast.LENGTH_SHORT).show();
                        // 避免重复播放，把播放按钮设置为不可用
//                        btn_play.setEnabled(false);
                    }
                });
                // 设置循环播放
                // mediaPlayer.setLooping(true);
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        // 在播放完毕被回调
//                        btn_play.setEnabled(true);
                    }
                });

                mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

                    // https://blog.51cto.com/readingcoding/2392981
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        // 如果发生错误，重新播放
//                        replay();
                        Toast.makeText(MainActivity.this, "播放失败", Toast.LENGTH_SHORT).show();
                        btn_stop.setText("播放失败");
                        return false;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "播放失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 暂停
     */
    protected void pause() {
        if (btn_pause.getText().toString().trim().equals("继续")) {
            btn_pause.setText("暂停");
            if(mediaPlayer != null)
                mediaPlayer.start();
            Toast.makeText(this, "继续播放", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            btn_pause.setText("继续");
            Toast.makeText(this, "暂停播放", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 重新播放
     */
    protected void replay() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(0);
            Toast.makeText(this, "重新播放", Toast.LENGTH_SHORT).show();
            btn_pause.setText("暂停");
            return;
        }
//        play();
    }

    /**
     * 停止播放
     */
    protected void stop() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
//            btn_play.setEnabled(true);
            Toast.makeText(this, "停止播放", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onDestroy() {
        // 在activity结束的时候回收资源
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }
}

