package com.xiaoe.shop.wxb.business.audio.presenter;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.ArrayList;
import java.util.List;

import com.xiaoe.common.app.XiaoeApplication;
import com.xiaoe.common.entitys.AudioPlayEntity;
import com.xiaoe.network.utils.ThreadPoolUtils;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.business.audio.ui.AudioActivity;


/**
 * Created by wcy on 2017/4/18.
 */
public class AudioNotifier {
    private static final int NOTIFICATION_ID = 0x111;
    private AudioMediaPlayer playService;
    private NotificationManager notificationManager;
    private Bitmap mBitmap;
    public static AudioNotifier get() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static AudioNotifier instance = new AudioNotifier();
    }

    private AudioNotifier() {
    }

    public void init(AudioMediaPlayer playService) {
        this.playService = playService;
        notificationManager = (NotificationManager) playService.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void showPlay(AudioPlayEntity music) {
        if (music == null) {
            return;
        }
        String imgUrl = TextUtils.isEmpty(music.getImgUrlCompressed()) ? music.getImgUrl() : music.getImgUrlCompressed();
        if (TextUtils.isEmpty(imgUrl)){
            playService.startForeground(NOTIFICATION_ID, buildNotification(playService, music, true,null));
        }else {
             Glide.with(XiaoeApplication.getmContext())
                    .load(imgUrl)
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    mBitmap = resource;
                    ThreadPoolUtils.runTaskOnUIThread(() ->
                            playService.startForeground(NOTIFICATION_ID,buildNotification(playService, music, true,resource)));
                }
            });
        }
    }

    public void showPause(AudioPlayEntity music) {
        if (music == null) {
            return;
        }
        playService.stopForeground(false);
        notificationManager.notify(NOTIFICATION_ID, buildNotification(playService, music, false,mBitmap));
    }

    public void cancelAll() {
        if (notificationManager != null) {
            notificationManager.cancelAll();
        }
    }

    private Notification buildNotification(Context context, AudioPlayEntity music, boolean isPlaying,Bitmap bitmap) {
        Intent intent = new Intent(context, AudioActivity.class);
//        intent.putExtra(Extras.EXTRA_NOTIFICATION, true);
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_notification)
                .setCustomContentView(getRemoteViews(context, music, isPlaying,bitmap));
        return builder.build();
    }

    private RemoteViews getRemoteViews(Context context, AudioPlayEntity music, boolean isPlaying,Bitmap bitmap) {
        String title = music.getTitle();
//        String subtitle = FileUtils.getArtistAndAlbum(music.getArtist(), music.getAlbum());
        String subtitle = "";
//        Bitmap cover = CoverLoader.get().loadThumb(music);

//        String imgUrl = TextUtils.isEmpty(music.getImgUrlCompressed()) ? music.getImgUrl() : music.getImgUrlCompressed();
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_audio_notification);
        if (bitmap == null) {
            remoteViews.setImageViewResource(R.id.iv_icon, R.mipmap.logo);
        } else{
            remoteViews.setImageViewBitmap(R.id.iv_icon,bitmap);
        }
        remoteViews.setTextViewText(R.id.tv_title, title);
        remoteViews.setTextViewText(R.id.tv_subtitle, subtitle);

        boolean isLightNotificationTheme = isLightNotificationTheme(playService);
//
        Intent playIntent = new Intent(StatusBarReceiver.ACTION_STATUS_BAR);
        playIntent.putExtra(StatusBarReceiver.EXTRA, StatusBarReceiver.EXTRA_PLAY_PAUSE);
        PendingIntent playPendingIntent = PendingIntent.getBroadcast(context, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setImageViewResource(R.id.iv_play_pause, getPlayIconRes(isLightNotificationTheme, isPlaying));
        remoteViews.setOnClickPendingIntent(R.id.iv_play_pause, playPendingIntent);

        return remoteViews;
    }

    private int getPlayIconRes(boolean isLightNotificationTheme, boolean isPlaying) {
        if (isPlaying) {
            return isLightNotificationTheme
                    ? R.mipmap.audiodetail_stop
                    : R.mipmap.audiolist_stop;
        } else {
            return isLightNotificationTheme
                    ? R.mipmap.audiodetail_play
                    : R.mipmap.audiolist_play;
        }
    }
//
//    private int getNextIconRes(boolean isLightNotificationTheme) {
//        return isLightNotificationTheme
//                ? R.drawable.ic_status_bar_next_dark_selector
//                : R.drawable.ic_status_bar_next_light_selector;
//    }

    private boolean isLightNotificationTheme(Context context) {
        int notificationTextColor = getNotificationTextColor(context);
        return isSimilarColor(Color.BLACK, notificationTextColor);
    }

    private int getNotificationTextColor(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        Notification notification = builder.build();
        RemoteViews remoteViews = notification.contentView;
        if (remoteViews == null) {
            return Color.BLACK;
        }
        int layoutId = remoteViews.getLayoutId();
        ViewGroup notificationLayout = (ViewGroup) LayoutInflater.from(context).inflate(layoutId, null);
        TextView title = (TextView) notificationLayout.findViewById(android.R.id.title);
        if (title != null) {
            return title.getCurrentTextColor();
        } else {
            return findTextColor(notificationLayout);
        }
    }

    /**
     * 如果通过 android.R.id.title 无法获得 title ，
     * 则通过遍历 notification 布局找到 textSize 最大的 TextView ，应该就是 title 了。
     */
    private int findTextColor(ViewGroup notificationLayout) {
        List<TextView> textViewList = new ArrayList<>();
        findTextView(notificationLayout, textViewList);

        float maxTextSize = -1;
        TextView maxTextView = null;
        for (TextView textView : textViewList) {
            if (textView.getTextSize() > maxTextSize) {
                maxTextView = textView;
            }
        }

        if (maxTextView != null) {
            return maxTextView.getCurrentTextColor();
        }

        return Color.BLACK;
    }

    private void findTextView(View view, List<TextView> textViewList) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                findTextView(viewGroup.getChildAt(i), textViewList);
            }
        } else if (view instanceof TextView) {
            textViewList.add((TextView) view);
        }
    }

    private boolean isSimilarColor(int baseColor, int color) {
        int simpleBaseColor = baseColor | 0xff000000;
        int simpleColor = color | 0xff000000;
        int baseRed = Color.red(simpleBaseColor) - Color.red(simpleColor);
        int baseGreen = Color.green(simpleBaseColor) - Color.green(simpleColor);
        int baseBlue = Color.blue(simpleBaseColor) - Color.blue(simpleColor);
        double value = Math.sqrt(baseRed * baseRed + baseGreen * baseGreen + baseBlue * baseBlue);
        return value < 180.0;
    }
}
