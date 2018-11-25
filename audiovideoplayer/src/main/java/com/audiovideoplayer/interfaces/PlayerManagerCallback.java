package com.audiovideoplayer.interfaces;

import com.google.android.exoplayer2.SimpleExoPlayer;

public interface PlayerManagerCallback {
    void audioIsReady(long duration);

    void audioBuffering();

    void repeatOff();

    void repeatOn();

    void shuffleMode(boolean shuffleModeEnabled);

    void onError();

    void trackChanged(String title, String album, String artist, String imagUrl);

    void setAudioTimers(long currentPosition);

    void audioPaused();

    void audioPlaying();

    void audioStopped();

    void setPlayer(SimpleExoPlayer player);

    void videoIsPlaying(long duration);

    void videoIsBuffering();

    void setVideoTimer(long currentPosition);

    void videoPaused();

    void videoPlaying();

    void audioEnded();
}
