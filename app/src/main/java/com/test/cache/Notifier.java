package com.test.cache;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.test.mediaplayerdemo.R;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.NOTIFICATION_SERVICE;

public class Notifier {
    public static final String CHANNEL_ID = "channel_id_audio";
    public static final String CHANNEL_NAME = "channel_name_audio";
    public static final String CHANNEL_ID_DEFAULT = "channel_id_default";
    public static final String EXTRA_NOTIFICATION = "com.sktcm.app.doctor.utils.audio.notification_dark";
    private static final int NOTIFICATION_ID = 0x111;
    private PlayerService playerService;
    private NotificationManager notificationManager;
    private boolean isDark;
    private String packageName;

    public static Notifier getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static Notifier instance = new Notifier();
    }

    private Notifier() {
    }

    public void init(PlayerService playerService) {
        this.playerService = playerService;
        this.notificationManager = (NotificationManager) playerService.getSystemService(NOTIFICATION_SERVICE);
        // 前台服务
        this.playerService.startForeground(NOTIFICATION_ID, buildNotification(playerService, AudioPlayer.getInstance().getNowPlaying()));
        this.packageName = MyApplication.getContext().getPackageName();
        this.isDark = isDarkNotificationBar(playerService);
    }

    public void stopForeground() {
        this.playerService.stopForeground(true);
    }

    public void showPlayInfo(AlbumProgramItemBean source) {
        this.notificationManager.notify(NOTIFICATION_ID, buildNotification(playerService, source));
    }

    private Notification buildNotification(Context context, AlbumProgramItemBean source) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //适配安卓8.0的消息渠道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
            channel.enableLights(false);
            channel.enableVibration(false);
            notificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContent(getRemoteViews(playerService, source));
        return builder.build();

    }

    private RemoteViews getRemoteViews(Context context, AlbumProgramItemBean source) {
        int layoutId = isDark ? R.layout.activity_main : R.layout.activity_main;
        final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), layoutId);
//        if (source == null) {
//            remoteViews.setTextViewText(R.id.tvTitle, "资源名称");
//            remoteViews.setViewVisibility(R.id.tvSubtitle, View.GONE);
//            remoteViews.setViewVisibility(R.id.btnPlay, View.GONE);
//            remoteViews.setViewVisibility(R.id.btnNext, View.GONE);
//            remoteViews.setImageViewResource(R.id.ivIcon, R.mipmap.ic_launcher);
//        } else {
//            remoteViews.setTextViewText(R.id.tvTitle, source.getName());
//            remoteViews.setViewVisibility(R.id.btnPlay, View.VISIBLE);
//            remoteViews.setViewVisibility(R.id.btnNext, View.VISIBLE);
//            remoteViews.setImageViewResource(R.id.btnPlay, getPlayIconRes());
//            if (Variables.nowPlayingAlbumData != null && !TextUtils.isEmpty(Variables.nowPlayingAlbumData.getName())) {
//                remoteViews.setViewVisibility(R.id.tvSubtitle, View.VISIBLE);
//                remoteViews.setTextViewText(R.id.tvSubtitle, Variables.nowPlayingAlbumData.getName());
//                Glide.with(context).load(Variables.nowPlayingAlbumData.getHead()).asBitmap().placeholder(R.mipmap.ic_launcher)
//                        .error(R.drawable.icon_img_err).into(new SimpleTarget<Bitmap>(128, 128) {
//                    @Override
//                    public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
//                        remoteViews.setImageViewBitmap(R.id.ivIcon, bitmap);
//                    }
//                });
//            } else {
//                remoteViews.setViewVisibility(R.id.tvSubtitle, View.GONE);
//                remoteViews.setImageViewResource(R.id.ivIcon, R.mipmap.ic_launcher);
//            }
//
//            Intent playIntent = new Intent(NotificationReceiver.ACTION_STATUS_BAR);
//            playIntent.putExtra(NotificationReceiver.EXTRA, NotificationReceiver.EXTRA_PLAY);
//            PendingIntent playPendingIntent = PendingIntent.getBroadcast(context, 1, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//            remoteViews.setOnClickPendingIntent(R.id.btnPlay, playPendingIntent);
//
//            Intent nextIntent = new Intent(NotificationReceiver.ACTION_STATUS_BAR);
//            nextIntent.putExtra(NotificationReceiver.EXTRA, NotificationReceiver.EXTRA_NEXT);
//            PendingIntent nextPendingIntent = PendingIntent.getBroadcast(context, 2, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//            remoteViews.setOnClickPendingIntent(R.id.btnNext, nextPendingIntent);
//
//        }

        return remoteViews;
    }

    private int getPlayIconRes() {
        if (AudioPlayer.getInstance().getStatus() == ManagedMediaPlayer.Status.STARTED) {
            return getStartIcon();
        } else {
            return getPauseIcon();
        }
    }

    private int getStartIcon() {
//        return isDark ? R.drawable.selector_play : R.drawable.selector_play_light;
        return R.drawable.ic_launcher_background;
    }

    private int getPauseIcon() {
//        return isDark ? R.drawable.selector_pause : R.drawable.selector_pause_light;
        return R.drawable.ic_launcher_background;
    }

    /**********************************************************************************************/

    private static final double COLOR_THRESHOLD = 180.0;
    private String DUMMY_TITLE = "DUMMY_TITLE";
    private int titleColor = 0;

    /**
     * 判断是否Notification背景是否为黑色
     *
     * @param context
     * @return
     */
    public boolean isDarkNotificationBar(Context context) {
        return !isColorSimilar(Color.BLACK, getNotificationTitleColor(context));
    }

    /**
     * 获取Notification 标题的颜色
     *
     * @param context
     * @return
     */
    private int getNotificationTitleColor(Context context) {
        int color = 0;
        if (context instanceof AppCompatActivity) {
            color = getNotificationColorCompat(context);
        } else {
            color = getNotificationColorInternal(context);
        }
        return color;
    }

    /**
     * 判断颜色是否相似
     *
     * @param baseColor
     * @param color
     * @return
     */
    public boolean isColorSimilar(int baseColor, int color) {
        int simpleBaseColor = baseColor | 0xff000000;
        int simpleColor = color | 0xff000000;
        int baseRed = Color.red(simpleBaseColor) - Color.red(simpleColor);
        int baseGreen = Color.green(simpleBaseColor) - Color.green(simpleColor);
        int baseBlue = Color.blue(simpleBaseColor) - Color.blue(simpleColor);

        double value = Math.sqrt(baseRed * baseRed + baseGreen * baseGreen + baseBlue * baseBlue);
        return value < COLOR_THRESHOLD;

    }

    /**
     * 获取标题颜色
     *
     * @param context
     * @return
     */
    private int getNotificationColorInternal(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID_DEFAULT);
        builder.setContentTitle(DUMMY_TITLE);
        Notification notification = builder.build();
        RemoteViews contentView = notification.contentView;
        if (contentView != null) {
            ViewGroup notificationRoot = (ViewGroup) contentView.apply(context, new FrameLayout(context));
            TextView title = (TextView) notificationRoot.findViewById(android.R.id.title);
            if (title == null) {
                //如果ROM厂商更改了默认的id
                iteratorView(notificationRoot, new Filter() {
                    @Override
                    public void filter(View view) {
                        if (view instanceof TextView) {
                            TextView textView = (TextView) view;
                            if (DUMMY_TITLE.equals(textView.getText().toString())) {
                                titleColor = textView.getCurrentTextColor();
                            }
                        }
                    }
                });
                return titleColor == 0 ? Color.WHITE : titleColor;
            } else {
                return title.getCurrentTextColor();
            }
        } else {
            return Color.BLACK;
        }
    }


    private int getNotificationColorCompat(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        Notification notification = builder.build();
        int layoutId = notification.contentView.getLayoutId();
        ViewGroup notificationRoot = (ViewGroup) LayoutInflater.from(context).inflate(layoutId, null);
        TextView title = (TextView) notificationRoot.findViewById(android.R.id.title);
        if (title == null) {
            final List<TextView> textViews = new ArrayList<>();
            iteratorView(notificationRoot, new Filter() {
                @Override
                public void filter(View view) {
                    if (view instanceof TextView) {
                        textViews.add((TextView) view);
                    }
                }
            });
            float minTextSize = Integer.MIN_VALUE;
            int index = 0;
            for (int i = 0, j = textViews.size(); i < j; i++) {
                float currentSize = textViews.get(i).getTextSize();
                if (currentSize > minTextSize) {
                    minTextSize = currentSize;
                    index = i;
                }
            }
            textViews.get(index).setText(DUMMY_TITLE);
            return textViews.get(index).getCurrentTextColor();
        } else {
            return title.getCurrentTextColor();
        }
    }

    private void iteratorView(View view, Filter filter) {
        if (view == null || filter == null) {
            return;
        }
        filter.filter(view);
        if (view instanceof ViewGroup) {
            ViewGroup container = (ViewGroup) view;
            for (int i = 0, j = container.getChildCount(); i < j; i++) {
                View child = container.getChildAt(i);
                iteratorView(child, filter);
            }
        }
    }

    interface Filter {
        void filter(View view);
    }

}