package com.musicandvideoplayer.mainscreen;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.audiovideoplayer.PlayerManager;
import com.audiovideoplayer.interfaces.PlayerManagerCallback;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.musicandvideoplayer.mainscreen.utils.Constant;
import com.musicandvideoplayer.R;
import com.musicandvideoplayer.mainscreen.utils.Utils;
import com.musicandvideoplayer.mainscreen.adapter.AudioAdapter;
import com.musicandvideoplayer.mainscreen.adapter.VideoAdapter;
import com.musicandvideoplayer.mainscreen.listener.RecyclerOnClick;
import com.musicandvideoplayer.mainscreen.pojo.AudioVideoBean;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements RecyclerOnClick, PlayerManagerCallback {

    @BindView(R.id.rvList)
    RecyclerView rvList;
    @BindView(R.id.iv_icon)
    ImageView ivIcon;
    @BindView(R.id.iv_close_audio)
    ImageView ivCloseAudio;
    @BindView(R.id.frame_icon)
    FrameLayout frameIcon;
    @BindView(R.id.tv_bottomsheet_songname)
    AppCompatTextView tvBottomsheetSongname;
    @BindView(R.id.tv_bottomsheet_artistname)
    AppCompatTextView tvBottomsheetArtistname;
    @BindView(R.id.llm_titles)
    LinearLayout llmTitles;
    @BindView(R.id.iv_play_pause_audio)
    ImageView ivPlayPauseAudio;
    @BindView(R.id.progress_audio)
    ProgressBar progressAudio;
    @BindView(R.id.frame_play)
    FrameLayout framePlay;
    @BindView(R.id.rel_top)
    RelativeLayout relTop;
    @BindView(R.id.iv_player)
    AppCompatImageView rvPlayer;
    @BindView(R.id.seekbar_audio)
    AppCompatSeekBar seekbarAudio;
    @BindView(R.id.tv_current_duration)
    AppCompatTextView tvCurrentDuration;
    @BindView(R.id.tv_total_duration)
    AppCompatTextView tvTotalDuration;
    @BindView(R.id.rel_seekbar)
    RelativeLayout relSeekbar;
    @BindView(R.id.iv_repeat)
    ImageView ivRepeat;
    @BindView(R.id.iv_prev)
    ImageView ivPrev;
    @BindView(R.id.iv_play_pause_big_audio)
    ImageView ivPlayPauseBigAudio;
    @BindView(R.id.progress_buffering)
    ProgressBar progressBuffering;
    @BindView(R.id.iv_forward)
    ImageView ivForward;
    @BindView(R.id.iv_shuffle)
    ImageView ivShuffle;
    @BindView(R.id.llm_controls)
    LinearLayout llmControls;
    @BindView(R.id.audioSheet)
    RelativeLayout audioSheet;
    @BindView(R.id.main_coordinator)
    CoordinatorLayout mainCoordinator;
    @BindView(R.id.seekbar_home)
    SeekBar seekbarHome;
    @BindView(R.id.tvPlayAudio)
    AppCompatTextView tvPlayAudio;
    @BindView(R.id.tvPlayVideo)
    AppCompatTextView tvPlayVideo;
    @BindView(R.id.llm_bottom_bar)
    LinearLayout llmBottomBar;
    @BindView(R.id.container)
    RelativeLayout container;
    @BindView(R.id.exo_video_player)
    SimpleExoPlayerView exoVideoPlayer;
    @BindView(R.id.video_progress)
    ProgressBar videoProgress;
    @BindView(R.id.iv_maximize)
    ImageView ivMaximize;
    @BindView(R.id.iv_play_video)
    ImageView ivPlayVideo;
    @BindView(R.id.tv_video_play_duration)
    AppCompatTextView tvVideoPlayDuration;
    @BindView(R.id.seekbar_video)
    AppCompatSeekBar seekbarVideo;
    @BindView(R.id.tv_video_total_duration)
    AppCompatTextView tvVideoTotalDuration;
    @BindView(R.id.rel_seekbar_video)
    RelativeLayout relSeekbarVideo;
    @BindView(R.id.frame_video)
    FrameLayout frameVideo;
    @BindView(R.id.tv_video_song_name)
    AppCompatTextView tvVideoSongName;
    @BindView(R.id.tv_video_artist_name)
    AppCompatTextView tvVideoArtistName;
    @BindView(R.id.iv_stop_video)
    ImageView ivStopVideo;
    @BindView(R.id.rel_menu)
    RelativeLayout relMenu;
    @BindView(R.id.llm_video_padding_view)
    LinearLayout llmVideoPaddingView;
  /*  @BindView(R.id.llm_main_video)
    LinearLayout llmMainVideo;*/
    @BindView(R.id.rel_bottom_sheet_video)
    RelativeLayout relBottomSheetVideo;
    private ArrayList<AudioVideoBean> audioVideoBeanArrayList;
    private int viewType = 0;
    private PlayerManager playerManager;
    private BottomSheetBehavior audioBottomSheet;
    private BottomSheetBehavior videoBottomSheet;
    private boolean isLandscape;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        ButterKnife.bind(this);
        audioVideoBeanArrayList = new ArrayList<>();
        playerManager = new PlayerManager(this);
        playerManager.setListener(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        audioBottomSheet = BottomSheetBehavior.from(audioSheet);
        videoBottomSheet = BottomSheetBehavior.from(relBottomSheetVideo);
        rvList.setLayoutManager(new LinearLayoutManager(this));
        setAudioSongs();
    }

    private void setAudioSongs() {
        viewType = 1;
        audioVideoBeanArrayList.clear();
        audioVideoBeanArrayList = Utils.getAudioSongs();
        AudioAdapter audioAdapter = new AudioAdapter(audioVideoBeanArrayList, this);
        rvList.setAdapter(audioAdapter);
    }

    private void setVideoSongs() {
        viewType = 2;
        audioVideoBeanArrayList.clear();
        audioVideoBeanArrayList = Utils.getVideoSongs();
        VideoAdapter videoAdapter = new VideoAdapter(audioVideoBeanArrayList, this);
        rvList.setAdapter(videoAdapter);
    }


    @Override
    public void onClick(int position) {
        if (viewType == 1) {
            openAudioPlayer(audioVideoBeanArrayList.get(position));
        } else if (viewType == 2) {
            openVideoPlayer(audioVideoBeanArrayList.get(position));
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void openVideoPlayer(AudioVideoBean audioVideoBean) {
        if (Utils.isInternetAvailable()) {
            relBottomSheetVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            audioBottomSheet.setPeekHeight(0);
            playerManager.stopService();
            playerManager.initVideoPlayer(audioVideoBean.getMediaUrl());
            tvVideoSongName.setText(audioVideoBean.getTitle().trim());
            tvVideoArtistName.setText(audioVideoBean.getAlbum().trim());
            ivPlayVideo.setImageResource(R.drawable.exo_controls_pause);
            videoBottomSheet.setState(BottomSheetBehavior.STATE_EXPANDED);
            videoBottomSheet.setPeekHeight(200);
            seekbarVideo.setProgress(0);
            seekbarVideo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        playerManager.setSeekPlayer(progress);
                    }

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            relMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    videoBottomSheet.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            });
            videoBottomSheet.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    switch (newState) {
                        case BottomSheetBehavior.STATE_DRAGGING:
                            if (isLandscape) {
                                videoBottomSheet.setState(BottomSheetBehavior.STATE_EXPANDED);
                            }
                            break;
                        case BottomSheetBehavior.STATE_SETTLING:
                            break;
                        case BottomSheetBehavior.STATE_EXPANDED:
                            llmBottomBar.setVisibility(View.INVISIBLE);
                            break;
                        case BottomSheetBehavior.STATE_COLLAPSED:
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                            frameVideo.getLayoutParams().height = 200;
                            exoVideoPlayer.getLayoutParams().height = 200;
                            ivPlayVideo.setVisibility(View.GONE);
                            ivMaximize.setVisibility(View.GONE);
                            animateViewVisibility(relMenu, View.VISIBLE);
                            relMenu.setVisibility(View.VISIBLE);
                            relSeekbarVideo.setVisibility(View.GONE);
                            ivPlayVideo.setVisibility(View.GONE);
                            ivMaximize.setVisibility(View.GONE);
                            break;
                        case BottomSheetBehavior.STATE_HIDDEN:
                            break;
                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                    if (slideOffset < 0.8) {
                        llmBottomBar.setVisibility(View.VISIBLE);

                    }
                    if (slideOffset > 0) {
                        exoVideoPlayer.getLayoutParams().height = 500;
                        frameVideo.getLayoutParams().height = 500;
                        animateViewVisibility(relMenu, View.GONE);
                    }

                    llmBottomBar.setAlpha(1 - slideOffset);
                }
            });

            frameVideo.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (relSeekbarVideo.getVisibility() == View.GONE) {
                        relSeekbarVideo.setVisibility(View.VISIBLE);
                        ivPlayVideo.setVisibility(View.VISIBLE);
                        ivMaximize.setVisibility(View.VISIBLE);
                    } else {
                        relSeekbarVideo.setVisibility(View.GONE);
                        ivPlayVideo.setVisibility(View.GONE);
                        ivMaximize.setVisibility(View.GONE);
                    }
                    return false;
                }
            });

        } else {
            Toast.makeText(this, getString(R.string.no_network), Toast.LENGTH_SHORT).show();
        }


    }

    private void openAudioPlayer(AudioVideoBean audioVideoBean) {
        if (Utils.isInternetAvailable()) {
            playerManager.resetPlayer();
            int index = 0;
            for (int i = 0; i < audioVideoBeanArrayList.size(); i++) {
                if (audioVideoBean.equals(audioVideoBeanArrayList.get(i))) {
                    index = i;
                    break;
                }
            }
            for (int i = index; i < audioVideoBeanArrayList.size(); i++) {
                AudioVideoBean audioVideoBean1 = audioVideoBeanArrayList.get(i);
                playerManager.addToPlayList(audioVideoBean1.getTitle(), audioVideoBean1.getAlbum(), audioVideoBean1.getArtist(),
                        Constant.AUDIO + audioVideoBean1.getMediaUrl(), Constant.AUDIO + audioVideoBean1.getImage());
            }
            playerManager.playAudio();
            playerManager.startNotificationService(audioVideoBean.getTitle(), audioVideoBean.getAlbum(), audioVideoBean.getArtist(), Constant.AUDIO + audioVideoBean.getImage());
            audioBottomSheet.setState(BottomSheetBehavior.STATE_EXPANDED);
            audioBottomSheet.setHideable(false);
            videoBottomSheet.setPeekHeight(0);
            audioBottomSheet.setPeekHeight(150);
            String url = Constant.AUDIO + audioVideoBean.getImage();
            Glide.with(this).load(url).
                    placeholder(R.drawable.ic_home)
                    .dontAnimate().diskCacheStrategy(DiskCacheStrategy.ALL).
                    into(ivIcon);
            Glide.with(this).load(url).
                    placeholder(R.drawable.ic_home)
                    .dontAnimate().diskCacheStrategy(DiskCacheStrategy.ALL).
                    into(rvPlayer);
            tvBottomsheetArtistname.setText(audioVideoBean.getAlbum());
            tvBottomsheetSongname.setText(audioVideoBean.getTitle());
            audioSheet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    audioBottomSheet.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            });
            seekbarAudio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        playerManager.setSeekPlayer(progress);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            audioBottomSheet.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    switch (newState) {
                        case BottomSheetBehavior.STATE_DRAGGING:
                            break;
                        case BottomSheetBehavior.STATE_SETTLING:
                            break;
                        case BottomSheetBehavior.STATE_EXPANDED:
                            ivIcon.setVisibility(View.INVISIBLE);
                            progressAudio.setVisibility(View.GONE);
                            ivPlayPauseAudio.setVisibility(View.INVISIBLE);
                            ivPlayPauseAudio.setEnabled(false);
                            llmBottomBar.setVisibility(View.INVISIBLE);
                            llmBottomBar.setEnabled(false);
                            break;
                        case BottomSheetBehavior.STATE_COLLAPSED:
                            ivPlayPauseAudio.setEnabled(true);
                            llmBottomBar.setEnabled(true);
                            break;
                        case BottomSheetBehavior.STATE_HIDDEN:
                            break;
                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                    llmBottomBar.setAlpha(1 - slideOffset);
                    ivIcon.setAlpha(1 - slideOffset);
                    ivCloseAudio.setAlpha(slideOffset);
                    ivPlayPauseAudio.setAlpha(1 - slideOffset);
                    progressAudio.setAlpha(1 - slideOffset);
                    if (slideOffset < 0.8) {
                        ivIcon.setVisibility(View.VISIBLE);
                        llmBottomBar.setVisibility(View.VISIBLE);
                        ivPlayPauseAudio.setVisibility(View.VISIBLE);
                    }

                }
            });

        } else {
            Toast.makeText(this, getString(R.string.no_network), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onBackPressed() {
        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            handleBackPress();
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            handleBackPress();
        }
    }

    private void handleBackPress() {
        if (audioBottomSheet.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            audioBottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else if (videoBottomSheet.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            videoBottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void audioIsReady(long duration) {
        ivPlayPauseBigAudio.setVisibility(View.VISIBLE);
        progressBuffering.setVisibility(View.GONE);
        seekbarAudio.setMax((int) duration);
        tvTotalDuration.setText(String.format("%2d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))));
    }

    @Override
    public void audioBuffering() {
        ivPlayPauseBigAudio.setVisibility(View.GONE);
        progressBuffering.setVisibility(View.VISIBLE);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void repeatOff() {
        ivRepeat.setImageTintList(ColorStateList.valueOf(Color.BLACK));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void repeatOn() {
        ivRepeat.setImageTintList(ColorStateList.valueOf(Color.MAGENTA));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void shuffleMode(boolean shuffleModeEnabled) {
        if (shuffleModeEnabled) {
            ivShuffle.setImageTintList(ColorStateList.valueOf(Color.MAGENTA));
        } else {
            ivShuffle.setImageTintList(ColorStateList.valueOf(Color.BLACK));
        }
    }

    @Override
    public void onError() {

    }

    @Override
    public void trackChanged(String title, String album, String artist, String imagUrl) {
        tvBottomsheetSongname.setText(title);
        tvBottomsheetArtistname.setText(album);
        Glide.with(this).load(imagUrl).
                placeholder(R.drawable.ic_home)
                .dontAnimate().diskCacheStrategy(DiskCacheStrategy.ALL).
                into(ivIcon);
        Glide.with(this).load(imagUrl).
                placeholder(R.drawable.ic_home)
                .dontAnimate().diskCacheStrategy(DiskCacheStrategy.ALL).
                into(rvPlayer);

    }

    @SuppressLint("DefaultLocale")
    @Override
    public void setAudioTimers(long currentPosition) {
        seekbarAudio.setProgress((int) currentPosition);
        tvCurrentDuration.setText(String.format("%2d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(currentPosition),
                TimeUnit.MILLISECONDS.toSeconds(currentPosition) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(currentPosition))));
    }

    @Override
    public void audioPaused() {
        ivPlayPauseBigAudio.setImageResource(R.drawable.exo_controls_play);
        ivPlayPauseAudio.setImageResource(R.drawable.exo_controls_play);
    }

    @Override
    public void audioPlaying() {
        ivPlayPauseBigAudio.setImageResource(R.drawable.exo_controls_pause);
        ivPlayPauseAudio.setImageResource(R.drawable.exo_controls_pause);
    }

    @Override
    public void audioStopped() {
        audioBottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
        audioBottomSheet.setPeekHeight(0);
    }

    @Override
    public void setPlayer(SimpleExoPlayer player) {
        exoVideoPlayer.setPlayer(player);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void videoIsPlaying(long duration) {
        videoProgress.setVisibility(View.GONE);
        videoProgress.setMax((int) duration);
        tvVideoTotalDuration.setText(String.format("%2d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))));
    }

    @Override
    public void videoIsBuffering() {
        videoProgress.setVisibility(View.VISIBLE);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void setVideoTimer(long currentPosition) {
        videoProgress.setProgress((int) currentPosition);
        tvVideoPlayDuration.setText(String.format("%2d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(currentPosition),
                TimeUnit.MILLISECONDS.toSeconds(currentPosition) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(currentPosition))));
    }

    @Override
    public void videoPaused() {
        ivPlayVideo.setImageResource(R.drawable.exo_controls_play);
    }

    @Override
    public void videoPlaying() {
        ivPlayVideo.setImageResource(R.drawable.exo_controls_pause);
    }

    @Override
    public void audioEnded() {

    }

    @OnClick({R.id.iv_maximize, R.id.iv_stop_video, R.id.iv_play_video, R.id.tvPlayVideo, R.id.tvPlayAudio, R.id.iv_close_audio, R.id.iv_play_pause_audio, R.id.iv_repeat, R.id.iv_prev, R.id.iv_play_pause_big_audio, R.id.iv_forward, R.id.iv_shuffle})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_close_audio:
                audioBottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
                audioBottomSheet.setPeekHeight(0);
                playerManager.releaseExoplayer();
                break;
            case R.id.iv_stop_video:
                videoBottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
                videoBottomSheet.setPeekHeight(0);
                playerManager.releaseExoplayer();
                break;
            case R.id.iv_play_pause_audio:
                playerManager.pausePlayAudio();
                break;
            case R.id.iv_repeat:
                playerManager.setRepeatMode();
                break;
            case R.id.iv_prev:
                playerManager.playPrevSong();
                break;
            case R.id.iv_play_pause_big_audio:
                playerManager.pausePlayAudio();
                break;
            case R.id.iv_forward:
                playerManager.playNextSong();
                break;
            case R.id.iv_play_video:
                playerManager.pausePlayVideo();
                break;
            case R.id.iv_shuffle:
                playerManager.setShuffleMode();
                break;
            case R.id.iv_nav_close:
                videoBottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
                videoBottomSheet.setPeekHeight(0);
                playerManager.releaseExoplayer();
                break;

            case R.id.tvPlayAudio:
                if (viewType == 2) {
                    setAudioSongs();
                }
                break;
            case R.id.tvPlayVideo:
                if (viewType == 1) {
                    setVideoSongs();
                }
                break;
            case R.id.iv_maximize:
                if (!isLandscape) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
                detectOrientation();
                break;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        switch (newConfig.orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                doLandscape();
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                doPotrait();
                break;

        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void doPotrait() {
        isLandscape = false;
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        exoVideoPlayer.getLayoutParams().height = 500;
        frameVideo.getLayoutParams().height = 500;
        llmBottomBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        playerManager.stopPlayer();
    }


    @SuppressLint("ClickableViewAccessibility")
    private void doLandscape() {
        isLandscape = true;
        llmBottomBar.setVisibility(View.GONE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        videoBottomSheet.setState(BottomSheetBehavior.STATE_EXPANDED);
        exoVideoPlayer.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
        frameVideo.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
       // llmMainVideo.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
    }

    private void detectOrientation() {
        if (Settings.System.getInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0) == 1) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                }
            }, 2000);

        }
    }

    protected void animateViewVisibility(final View view, final int visibility) {
        view.animate().cancel();
        view.animate().setListener(null);
        if (visibility == View.VISIBLE) {
            view.animate().setDuration(2000);
            view.animate().alpha(1f).start();
            view.setVisibility(View.VISIBLE);
        } else {
            view.animate().setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setVisibility(visibility);
                }
            }).alpha(0f).setDuration(2000).start();
        }
    }


}
