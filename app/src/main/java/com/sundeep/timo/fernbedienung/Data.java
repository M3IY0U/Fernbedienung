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


    public void save(Context context){
        SharedPreferences sP = context.getSharedPreferences(context.getString(R.string.preferences),Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sP.edit();
        editor.putInt("volume", volume);
        editor.putBoolean("on",on);
        editor.putString("ip",ip);
        editor.putInt("currentChannelIndex", currentChannelIndex);
        editor.putBoolean("muted", muted);
        editor.putString("channels",toJson(channels));
        editor.commit();
    }

    public void restore(Context context){
        SharedPreferences sP = context.getSharedPreferences(context.getString(R.string.preferences), Context.MODE_PRIVATE);
        volume = sP.getInt("volume",50);
        on = sP.getBoolean("on",false);
        ip = sP.getString("ip","");
        muted = sP.getBoolean("muted", false);
        currentChannelIndex = sP.getInt("currentChannelIndex", -1);
        channels = toArrayList(sP.getString("channels", null));
        channels = channels != null ? channels : new ArrayList<Channel>();
    }

    public void reset(){
        ip=null;
        muted = false;
        volume =0;
        paused = false;
        currentChannelIndex = -1;
        channels = null;
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


    public Channel getPreviousChannel() {
        if(currentChannelIndex < 0) {
            return null;
        }
        if(--currentChannelIndex < 0) {
            currentChannelIndex = channels.size() - 1;
        }
        return channels.get(currentChannelIndex);
    }

    public Channel getCurrentChannel() {
        if(currentChannelIndex < 0) {
            return null;
        }
        return channels.get(currentChannelIndex);
    }

    public void setCurrentChannel(String c){
        String test;
        for(int i =0;i<channels.size();i++){
            test = channels.get(i).getChannel();
            if(test.equals(c)){
                setCurrentChannelIndex(i);
                break;
            }
        }
    }


    public Channel getNextChannel() {
        if(currentChannelIndex < 0) {
            return null;
        }
        if(++currentChannelIndex >= channels.size()) {
            currentChannelIndex = 0;
        }
        return channels.get(currentChannelIndex);
    }



    public ArrayList<Channel> getChannels() {
        return channels;
    }
    public void setChannels(ArrayList<Channel> channels) {
        this.channels = channels;
    }
    @Nullable
    public Channel getPictureInPictureChannel() {
        return pictureInPictureChannel;
    }
    public void setPictureInPictureChannel(@Nullable Channel pictureInPictureChannel) {
        this.pictureInPictureChannel = pictureInPictureChannel;
    }
    public int getCurrentChannelIndex() {
        return currentChannelIndex;
    }
    public void setCurrentChannelIndex(int currentChannelIndex) {
        this.currentChannelIndex = currentChannelIndex;
    }
    public boolean isOn() {
        return on;
    }
    public void setOn(boolean on) {
        this.on = on;
    }
    public boolean isPaused() {
        return paused;
    }
    public void setPaused(boolean paused) {
        this.paused = paused;
    }
    public int getPreMuteVolume() {
        return preMuteVolume;
    }
    public int getVolume() {
        return volume;
    }
    public void setVolume(int volume) {
        this.volume = volume;
    }
    public boolean isMuted() {
        return muted;
    }
    public void setMuted(boolean muted) {
        this.muted = muted;
    }
    public static Data getInstance() {
        return ourInstance;
    }
    public String getIp() {
        return ip;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }
    public void toggleMute(){
        if(muted){
            muted= false;
            volume=preMuteVolume;
        }else {
            preMuteVolume = volume;
            muted = true;
            volume=0;
        }
    }
    public void togglePause(){
        if(paused){
            paused=false;
        }else{
            paused=true;
        }

    }
    public String getRatio() {
        return ratio;
    }
    public void setRatio(String ratio) {
        this.ratio = ratio;
    }
}
