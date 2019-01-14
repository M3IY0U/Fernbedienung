package com.sundeep.timo.fernbedienung;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Data {

    private Data() {

    }

    private String ip;
    private boolean muted = false;
    private static final Data ourInstance = new Data();
    private int volume = 0;
    private boolean on = false;
    private boolean paused = false;
    private int preMuteVolume;
    private ArrayList<Channel> channels;
    private Channel pictureInPictureChannel;
    private String ratio = "16:9";


    private int currentChannelIndex = -1;


    void save(Context context){
        SharedPreferences sP = context.getSharedPreferences(context.getString(R.string.preferences),Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sP.edit();
        editor.putInt("volume", volume);
        editor.putBoolean("on",on);
        editor.putString("ip",ip);
        editor.putInt("currentChannelIndex", currentChannelIndex);
        editor.putBoolean("muted", muted);
        editor.putString("channels",toJson(channels));
        editor.apply();
    }

    void restore(Context context){
        SharedPreferences sP = context.getSharedPreferences(context.getString(R.string.preferences), Context.MODE_PRIVATE);
        volume = sP.getInt("volume",50);
        on = sP.getBoolean("on",false);
        ip = sP.getString("ip","");
        muted = sP.getBoolean("muted", false);
        currentChannelIndex = sP.getInt("currentChannelIndex", -1);
        channels = toArrayList(sP.getString("channels", null));
        channels = channels != null ? channels : new ArrayList<Channel>();
    }

    void reset(){
        ip="";
        muted = false;
        volume =0;
        paused = false;
        currentChannelIndex = -1;
        channels.clear();
        pictureInPictureChannel = null;
    }


    private <T extends Object> T toObject(String json, Class<T> targetClass) {
        if (json == null) {
            return null;
        }
        Gson gson = new Gson();
        return gson.fromJson(json, targetClass);
    }

    private ArrayList<Channel> toArrayList(String json) {
        if(json == null) {
            return null;
        }
        Gson gson = new Gson();
        Type channelType = new TypeToken<ArrayList<Channel>>(){}.getType();

        return gson.fromJson(json, channelType);
    }

    private String toJson(Object object) {
        if(object == null) {
            return  null;
        }
        Gson gson = new Gson();
        return gson.toJson(object);
    }


     Channel getPreviousChannel() {
        if(currentChannelIndex < 0) {
            return null;
        }
        if(--currentChannelIndex < 0) {
            currentChannelIndex = channels.size() - 1;
        }
        return channels.get(currentChannelIndex);
    }

     Channel getCurrentChannel() {
        if(currentChannelIndex < 0) {
            return null;
        }
        return channels.get(currentChannelIndex);
    }

     void setCurrentChannel(String c){
        String test;
        for(int i =0;i<channels.size();i++){
            test = channels.get(i).getChannel();
            if(test.equals(c)){
                setCurrentChannelIndex(i);
                break;
            }
        }
    }


     Channel getNextChannel() {
        if(currentChannelIndex < 0) {
            return null;
        }
        if(++currentChannelIndex >= channels.size()) {
            currentChannelIndex = 0;
        }
        return channels.get(currentChannelIndex);
    }



     ArrayList<Channel> getChannels() {
        return channels;
    }
     void setChannels(ArrayList<Channel> channels) {
        this.channels = channels;
    }
    @Nullable
     Channel getPictureInPictureChannel() {
        return pictureInPictureChannel;
    }
     void setPictureInPictureChannel(@Nullable Channel pictureInPictureChannel) {
        this.pictureInPictureChannel = pictureInPictureChannel;
    }
     int getCurrentChannelIndex() {
        return currentChannelIndex;
    }
     void setCurrentChannelIndex(int currentChannelIndex) {
        this.currentChannelIndex = currentChannelIndex;
    }

     boolean isOn() {
        return on;
    }
     void setOn(boolean on) {
        this.on = on;
    }
     boolean isPaused() {
        return paused;
    }
     void setPaused(boolean paused) {
        this.paused = paused;
    }
    int getPreMuteVolume() {
        return preMuteVolume;
    }
     int getVolume() {
        return volume;
    }
     void setVolume(int volume) {
        this.volume = volume;
    }
     boolean isMuted() {
        return muted;
    }
     void setMuted(boolean muted) {
        this.muted = muted;
    }
     static Data getInstance() {
        return ourInstance;
    }
     String getIp() {
        return ip;
    }
     void setIp(String ip) {
        this.ip = ip;
    }
     void toggleMute(){
        if(muted){
            muted= false;
            volume=preMuteVolume;
        }else {
            preMuteVolume = volume;
            muted = true;
            volume=0;
        }
    }
     void togglePause(){
         paused = !paused;

    }
     String getRatio() {
        return ratio;
    }
     void setRatio(String ratio) {
        this.ratio = ratio;
    }
}
