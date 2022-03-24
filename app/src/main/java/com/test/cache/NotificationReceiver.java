package com.test.cache;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

public class NotificationReceiver extends BroadcastReceiver {
    public static final String ACTION_STATUS_BAR = "com.sktcm.app.doctor.utils.audio.NOTIFICATION_ACTIONS";
    public static final String EXTRA = "extra";
    public static final String EXTRA_PLAY = "play_pause";
    public static final String EXTRA_NEXT= "play_next";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || TextUtils.isEmpty(intent.getAction())) {
            return;
        }
        String extra = intent.getStringExtra(EXTRA);
        if (EXTRA_PLAY.equals(extra)) {
            if (AudioPlayer.getInstance().getStatus() == ManagedMediaPlayer.Status.STARTED) {
                AudioPlayer.getInstance().pause();
            } else if (AudioPlayer.getInstance().getStatus() == ManagedMediaPlayer.Status.PAUSED){
                AudioPlayer.getInstance().resume();
            }
        } else if (EXTRA_NEXT.equals(extra)){
            AudioPlayer.getInstance().next();
        }
    }

}

