package com.musicandvideoplayer;

import android.app.Application;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

@ReportsCrashes(mailTo =  "dev.sharma@appinventiv.com", mode = ReportingInteractionMode.TOAST, resToastText = R.string.crash_toast_text)
public class App extends Application
{
    public static String appName;
    private static App applicationContext;

    public static App getInstance() {
        return applicationContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = this;
        appName = getResources().getString(R.string.app_name);
        ACRA.init(this);
    }

}
