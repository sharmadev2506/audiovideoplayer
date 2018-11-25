package com.audiovideoplayer.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.session.MediaSessionCompat;

import com.audiovideoplayer.R;
import com.audiovideoplayer.interfaces.ServiceCallbacks;
import com.audiovideoplayer.utils.AppConstants;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.Objects;

public class NotificationService extends Service {
    private static final String CHANNEL_ID = "com.app";
    private static final int NOTIFICATION_ID = 101;
    private IBinder binder = new LocalBinder();
    private String coverImage;
    private boolean isPlaying;
    private NotificationManager mNotificationManager;
    private ServiceCallbacks serviceCallbacks;
    private NotificationCompat.Builder notificationBuilder;
    private MediaSessionCompat mediaSession;
    private PendingIntent previousIntent;
    private PendingIntent playIntent;
    private PendingIntent nextIntent;
    private PendingIntent closeIntent;

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaSession = new MediaSessionCompat(this, "tag");
        Intent previousIntent = new Intent(this, NotificationService.class);
        previousIntent.setAction(AppConstants.SERVICE.PREV_ACTION);
        this.previousIntent = PendingIntent.getService(this, 0, previousIntent, 0);
        Intent playIntent = new Intent(this, NotificationService.class);
        playIntent.setAction(AppConstants.SERVICE.PLAY_ACTION);
        this.playIntent = PendingIntent.getService(this, 0, playIntent, 0);
        Intent nextIntent = new Intent(this, NotificationService.class);
        nextIntent.setAction(AppConstants.SERVICE.NEXT_ACTION);
        this.nextIntent = PendingIntent.getService(this, 0, nextIntent, 0);
        Intent closeIntent = new Intent(this, NotificationService.class);
        closeIntent.setAction(AppConstants.SERVICE.STOP_SERVICE);
        this.closeIntent = PendingIntent.getService(this, 0, closeIntent, 0);
    }

    /**
     * Set Callback for notifications
     *
     * @param callbacks serviceCallback
     */
    public void setCallbacks(ServiceCallbacks callbacks) {
        serviceCallbacks = callbacks;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (intent != null)
            if (Objects.equals(intent.getAction(), AppConstants.SERVICE.START_FOREGROUND_ACTION)) {
                String songName = intent.getStringExtra("SongName");
                String artistName = intent.getStringExtra("ArtistName");
                coverImage = intent.getStringExtra("SongCover");
                loadNotification(songName, artistName, coverImage);
            } else if (Objects.equals(intent.getAction(), AppConstants.SERVICE.PREV_ACTION)) {
                if (serviceCallbacks != null)
                    serviceCallbacks.updatePlayer(AppConstants.SERVICE.PREV_ACTION);
            } else if (Objects.equals(intent.getAction(), AppConstants.SERVICE.PLAY_ACTION)) {
                if (!isPlaying) {
                    if (notificationBuilder.mActions != null) {
                        notificationBuilder.mActions.get(1).icon = R.drawable.exo_controls_play;
                        isPlaying = true;
                    }
                } else {
                    if (notificationBuilder.mActions != null) {
                        notificationBuilder.mActions.get(1).icon = R.drawable.exo_controls_pause;
                        isPlaying = false;
                    }
                }
                mNotificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
                if (serviceCallbacks != null)
                    serviceCallbacks.updatePlayer(AppConstants.SERVICE.PLAY_ACTION);
            } else if (Objects.requireNonNull(intent.getAction()).equals(AppConstants.SERVICE.NEXT_ACTION)) {
                if (serviceCallbacks != null)
                    serviceCallbacks.updatePlayer(AppConstants.SERVICE.NEXT_ACTION);
            } else if (intent.getAction().equals(AppConstants.SERVICE.STOP_SERVICE)) {
                stopForeground(true);
                stopSelf();
                if (serviceCallbacks != null)
                    serviceCallbacks.updatePlayer(AppConstants.SERVICE.STOP_SERVICE);
            }
        return START_STICKY;
    }


    /**
     * load notifications
     * @param songName song name
     * @param albumName album name
     * @param coverImage cover Image
     */
    public void loadNotification(String songName, String albumName, String coverImage) {
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "System", NotificationManager.IMPORTANCE_LOW);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(channel);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
            notificationBuilder.setVisibility(Notification.VISIBILITY_PUBLIC);
            notificationBuilder.setSmallIcon(R.drawable.ic_stat_name);
            notificationBuilder.setContentTitle(songName);
            notificationBuilder.setColorized(true);
            notificationBuilder.setContentText(albumName);
            notificationBuilder.setContentInfo("Now Playing");
            notificationBuilder.addAction(R.drawable.exo_controls_previous, "Previous", previousIntent);
            notificationBuilder.addAction(R.drawable.exo_controls_pause, "pause", playIntent);
            notificationBuilder.addAction(R.drawable.exo_controls_next, "Next", nextIntent);
            notificationBuilder.addAction(android.R.drawable.ic_menu_close_clear_cancel, "Cancel", closeIntent);
            android.support.v4.media.app.NotificationCompat.MediaStyle mediaStyle = new android.support.v4.media.app.NotificationCompat.MediaStyle();
            mediaStyle.setShowCancelButton(true);
            mediaStyle.setCancelButtonIntent(closeIntent);
            mediaStyle.setMediaSession(mediaSession.getSessionToken());
            mediaStyle.setShowActionsInCompactView(0, 1, 2);
            notificationBuilder.setStyle(mediaStyle);
            notificationBuilder.getNotification().flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
            if (this.coverImage.length() > 0) {
                Glide.with(this).load(coverImage).asBitmap().into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        notificationBuilder.setLargeIcon(resource);
                        mNotificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
                    }
                });
            } else {
                notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_stat_name));
            }
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_LOW);
        }
        startForeground(NOTIFICATION_ID, notificationBuilder.build());
    }

    /**
     * Update Play Icon
     */
    public void updatePlayIcon() {
        notificationBuilder.mActions.get(1).icon = R.drawable.exo_controls_play;
        isPlaying = false;
        mNotificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());

    }

    /**
     * Update Pause Icon
     */
    public void updatePauseIcon() {
        if (notificationBuilder.mActions != null) {
            notificationBuilder.mActions.get(1).icon = R.drawable.exo_controls_pause;
            isPlaying = false;
            mNotificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
        }
    }


    public class LocalBinder extends Binder {
        public NotificationService getService() {
            return NotificationService.this;
        }
    }

}
