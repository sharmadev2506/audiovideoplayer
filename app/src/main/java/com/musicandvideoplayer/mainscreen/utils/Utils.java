package com.musicandvideoplayer.mainscreen.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.musicandvideoplayer.App;
import com.musicandvideoplayer.mainscreen.pojo.AudioVideoBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class Utils {

    public static ArrayList<AudioVideoBean> getAudioSongs() {
        ArrayList<AudioVideoBean> audioList = new ArrayList<>();
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset("audio.json"));
            JSONArray jsonArray = obj.getJSONArray("music");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                AudioVideoBean audioBean = new AudioVideoBean();
                audioBean.setArtist(jsonObject.getString("artist"));
                audioBean.setAlbum(jsonObject.getString("album"));
                audioBean.setTitle(jsonObject.getString("title"));
                audioBean.setImage(jsonObject.getString("image"));
                audioBean.setMediaUrl(jsonObject.getString("source"));
                audioList.add(audioBean);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return audioList;
    }

    private static String loadJSONFromAsset(String name) {
        String json = null;
        try {
            InputStream is = App.getInstance().getAssets().open(name);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public static ArrayList<AudioVideoBean> getVideoSongs() {
        ArrayList<AudioVideoBean> audioList = new ArrayList<>();
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset("video.json"));
            JSONArray jsonArray = obj.getJSONArray("videos");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                AudioVideoBean audioBean = new AudioVideoBean();
                audioBean.setArtist(jsonObject.getString("description"));
                audioBean.setAlbum(jsonObject.getString("subtitle"));
                audioBean.setTitle(jsonObject.getString("title"));
                audioBean.setImage(jsonObject.getString("thumb"));
                audioBean.setMediaUrl(jsonObject.getString("sources"));
                audioList.add(audioBean);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return audioList;
    }

    public static boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) App.getInstance().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();

    }
}
