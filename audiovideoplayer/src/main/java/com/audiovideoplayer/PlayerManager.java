package com.audiovideoplayer;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;

import com.audiovideoplayer.interfaces.PlayerManagerCallback;
import com.audiovideoplayer.interfaces.ServiceCallbacks;
import com.audiovideoplayer.pojo.PlayList;
import com.audiovideoplayer.service.NotificationService;
import com.audiovideoplayer.utils.AppConstants;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;

public class PlayerManager implements ServiceCallbacks {

    private final DefaultTrackSelector trackSelector;
    private Context mContext;
    private boolean bound;
    private int whatIsPlaying;
    private SimpleExoPlayer player;
    private DefaultDataSourceFactory dataSourceFactory;
    private PlayerManagerCallback playerManagerCallback;
    private Handler mHandler = new Handler();
    private boolean isPauseByAnotherApp;
    private ArrayList<PlayList> arrayList;
    private NotificationService myService;
    /**
     * this service connection is used to get the callbacks from music service.
     * to take actions from player over toolbar and locked screen.
     */
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            NotificationService.LocalBinder binder = (NotificationService.LocalBinder) service;
            myService = binder.getService();
            bound = true;
            setCallback(myService);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bound = false;
        }
    };

    /**
     * set callback for player events
     * @param myService playerManagerManager
     */
    private void setCallback(NotificationService myService) {
        myService.setCallbacks(this);
    }

    /**
     * Audio Focus Change Listener
     */
    private AudioManager.OnAudioFocusChangeListener focusChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {
                public void onAudioFocusChange(int focusChange) {
                    AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
                    switch (focusChange) {

                        case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK):
                            break;
                        case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT):
                            if (player != null) {
                                isPauseByAnotherApp = true;
                                player.setPlayWhenReady(false);
                                isPauseByAnotherApp = true;
                                playerManagerCallback.audioPaused();
                                myService.updatePlayIcon();
                            }
                            break;
                        case (AudioManager.AUDIOFOCUS_LOSS):
                            if (player != null) {
                                isPauseByAnotherApp = true;
                                player.setPlayWhenReady(false);
                                playerManagerCallback.audioPaused();
                                myService.updatePlayIcon();
                            }
                            break;

                        case (AudioManager.AUDIOFOCUS_GAIN):
                            if (player != null && isPauseByAnotherApp) {
                                  player.setPlayWhenReady(true);
                                playerManagerCallback.audioPlaying();
                                myService.updatePauseIcon();
                            }
                            break;
                    }
                }
            };


    /**
     * Initialize Player Manager
     * @param context context
     */
    public PlayerManager(Context context) {
        this.mContext = context;
        arrayList = new ArrayList<>();
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        AdaptiveTrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        dataSourceFactory = new DefaultDataSourceFactory(mContext, Util.getUserAgent(mContext, "com.audiovideoplayer.app"), bandwidthMeter);
    }

    /**
     * get Audio Acces for the application
     * @return boolean
     */
    private boolean getAudioAccess() {
        AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        assert am != null;
        int result = am.requestAudioFocus(focusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }


    /**
     * Play Audio songs
     */
    public void playAudio() {
        if (getAudioAccess()) {
            releaseExoplayer();
            initExoPlayer();
        }
    }

    /**
     * Update timer of the player
     */
    private Runnable mUpdateTimeTask = new Runnable() {
        @SuppressLint("DefaultLocale")
        public void run() {
            if (player != null) {
                if(whatIsPlaying== AppConstants.AUDIO) {
                    playerManagerCallback.setAudioTimers(player.getCurrentPosition());
                }else if(whatIsPlaying==AppConstants.VIDEO)
                {
                    playerManagerCallback.setVideoTimer(player.getCurrentPosition());
                }
                mHandler.postDelayed(this, 100);
            }
        }
    };

    /**
     * Init Exoplayer for playing audio
     */
    private void initExoPlayer() {
        player = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector, new DefaultLoadControl());
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        whatIsPlaying=AppConstants.AUDIO;
        MediaSource mediaSource[] = new MediaSource[arrayList.size()];
        for (int i = 0; i < arrayList.size(); i++) {
            MediaSource source = new ExtractorMediaSource(Uri.parse(arrayList.get(i).getMediaUrl()), dataSourceFactory, extractorsFactory, null, null);
            mediaSource[i] = source;
        }
        ConcatenatingMediaSource source = new ConcatenatingMediaSource(mediaSource);
        player.prepare(source);
        player.setPlayWhenReady(true);
        mHandler.postDelayed(mUpdateTimeTask, 0);
        player.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                switch (playbackState) {
                    case SimpleExoPlayer.STATE_READY:
                        playerManagerCallback.audioIsReady(player.getDuration());
                        break;
                    case SimpleExoPlayer.STATE_BUFFERING:
                        playerManagerCallback.audioBuffering();
                        break;
                    case SimpleExoPlayer.STATE_ENDED:
                        player.seekTo(0);
                        player.setPlayWhenReady(false);
                        playerManagerCallback.audioEnded();
                        break;
                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {
                switch (repeatMode) {
                    case Player.REPEAT_MODE_OFF:
                        playerManagerCallback.repeatOff();
                        break;
                    case Player.REPEAT_MODE_ONE:
                        playerManagerCallback.repeatOn();
                        break;
                }

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
                playerManagerCallback.shuffleMode(shuffleModeEnabled);
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                playerManagerCallback.onError();
            }

            @Override
            public void onPositionDiscontinuity(int reason) {
                PlayList playList= arrayList.get(player.getCurrentWindowIndex());
                if(myService!=null) {
                    myService.loadNotification(playList.getTitle(), playList.getAlbum(), playList.getImagUrl());
                }
                playerManagerCallback.trackChanged(playList.getTitle(),playList.getAlbum(),playList.getArtist(),playList.getImagUrl());
            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        });

    }

    /**
     * Stop Audio
     */
    public void stopPlayer() {
        if (bound && player != null) {
            player.release();
            myService.setCallbacks(null);
            mContext.unbindService(serviceConnection);
            mContext.stopService(new Intent(mContext, NotificationService.class));
            bound = false;
        }
    }


    /**
     * Release exoplayer
     */
    public void releaseExoplayer() {
        if (player != null) {
            stopPlayer();
            player.release();
            player = null;
            whatIsPlaying = 0;
        }
    }

    /**
     * set Listener for player event callbacks
     * @param playerManagerCallback playerCallbacks
     */
    public void setListener(PlayerManagerCallback playerManagerCallback) {
        this.playerManagerCallback = playerManagerCallback;
    }

    /**
     * Add to playlist song element
     * @param title title
     * @param album album
     * @param artist artist
     * @param mediaUrl mediaUrl
     * @param image image
     */
    public void addToPlayList(String title, String album, String artist, String mediaUrl, String image) {
        arrayList.add(new PlayList(title, artist, album, mediaUrl, image));
    }

    /**
     * Reset ArrayList
     */
    public void resetPlayer() {
        arrayList.clear();
    }

    /**
     * Set Seek
     * @param progress progress
     */
    public void setSeekPlayer(int progress) {
        player.seekTo(progress);
    }

    /**
     * Set Repeat Mode
     */
    public void setRepeatMode() {
        if(player.getRepeatMode()==Player.REPEAT_MODE_ONE) {
            player.setRepeatMode(Player.REPEAT_MODE_OFF);
        }else
        {
            player.setRepeatMode(Player.REPEAT_MODE_ONE);
        }
    }


    /**
     * Play next song in playlist
     */
    public void playNextSong() {
        if(player!=null && arrayList.size()>0) {
            if (player.getCurrentWindowIndex() < arrayList.size() && player.getNextWindowIndex() >= 0) {
                player.seekTo(player.getCurrentWindowIndex() + 1, 0);
                PlayList playList = arrayList.get(player.getCurrentWindowIndex());
                if(myService!=null) {
                    myService.loadNotification(playList.getTitle(), playList.getAlbum(), playList.getImagUrl());
                }
            }
        }
    }

    /**
     * play Previous song in playlist
     */
    public void playPrevSong() {
        if(player.getCurrentWindowIndex()>0 && player.getPreviousWindowIndex()>=0)
        {
            player.seekTo(player.getCurrentWindowIndex() - 1,0);
            PlayList playList=arrayList.get(player.getCurrentWindowIndex());
            myService.loadNotification(playList.getTitle(),playList.getAlbum(),playList.getImagUrl());
        }
    }

    /**
     * set Shuffle mode on
     */
    public void setShuffleMode() {
        if(player.getShuffleModeEnabled())
        {
            player.setShuffleModeEnabled(false);
        }else {
            player.setShuffleModeEnabled(true);
        }
    }

    /**
     * Pause and Play Audio
     */
    public void pausePlayAudio() {
        if(player.getPlayWhenReady())
        {
            player.setPlayWhenReady(false);
            playerManagerCallback.audioPaused();
        }else
        {
            player.setPlayWhenReady(true);
            playerManagerCallback.audioPlaying();
        }
    }

    @Override
    public void updatePlayer(String action) {
        switch (action) {
            case AppConstants.SERVICE.PLAY_ACTION:
                if (player != null) {
                    if (player.getPlayWhenReady()) {
                        player.setPlayWhenReady(false);
                        playerManagerCallback.audioPaused();
                    } else {
                        player.setPlayWhenReady(true);
                        playerManagerCallback.audioPlaying();
                    }
                }
                break;
            case AppConstants.SERVICE.STOP_SERVICE:
                if (player != null) {
                    player.setPlayWhenReady(false);
                    playerManagerCallback.audioStopped();
                    releaseExoplayer();
                }
                break;
            case AppConstants.SERVICE.PREV_ACTION:
                if (player != null) {
                    playPrevSong();

                }
                break;
            case AppConstants.SERVICE.NEXT_ACTION:
                if (player != null) {
                   playNextSong();
                }
                break;
        }
    }

    /**
     * Start notification
     * @param title
     * @param album
     * @param artist
     * @param image
     */
    public void startNotificationService(String title, String album, String artist, String image) {
        Intent intent = new Intent(mContext, NotificationService.class);
        intent.putExtra("SongName", title);
        intent.putExtra("ArtistName", album);
        intent.putExtra("SongCover", image);
        intent.setAction(AppConstants.SERVICE.START_FOREGROUND_ACTION);
        mContext.startService(intent);
        mContext.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public void stopService() {
        updatePlayer(AppConstants.SERVICE.STOP_SERVICE);
    }


    /**
     * Initialize video player
     * @param mediaUrl
     */
    public void initVideoPlayer(String mediaUrl) {
        if (getAudioAccess()) {
            releaseExoplayer();
            whatIsPlaying=AppConstants.VIDEO;
            player = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector);
            MediaSource videoSource = new ExtractorMediaSource(Uri.parse(mediaUrl), dataSourceFactory, new DefaultExtractorsFactory(), null, null);
            player.prepare(videoSource);
            player.setPlayWhenReady(true);
            mHandler.postDelayed(mUpdateTimeTask, 0);
            playerManagerCallback.setPlayer(player);
            player.addListener(new Player.EventListener() {
                @Override
                public void onTimelineChanged(Timeline timeline, Object manifest) {

                }

                @Override
                public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

                }

                @Override
                public void onLoadingChanged(boolean isLoading) {

                }

                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    switch (playbackState) {
                        case SimpleExoPlayer.STATE_READY:
                            playerManagerCallback.videoIsPlaying(player.getDuration());
                            break;
                        case SimpleExoPlayer.STATE_BUFFERING:
                            playerManagerCallback.videoIsBuffering();
                            break;
                        case SimpleExoPlayer.STATE_ENDED:
                            break;
                    }
                }

                @Override
                public void onRepeatModeChanged(int repeatMode) {

                }

                @Override
                public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

                }

                @Override
                public void onPlayerError(ExoPlaybackException error) {

                }

                @Override
                public void onPositionDiscontinuity(int reason) {

                }

                @Override
                public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

                }

                @Override
                public void onSeekProcessed() {

                }
            });
        }
    }

    /**
     * Pause and play video
     */
    public void pausePlayVideo() {
        if(player.getPlayWhenReady())
        {
            player.setPlayWhenReady(false);
            playerManagerCallback.videoPaused();
        }else
        {
            player.setPlayWhenReady(true);
            playerManagerCallback.videoPlaying();
        }
    }
}
