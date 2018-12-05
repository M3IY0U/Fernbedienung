package com.sundeep.timo.fernbedienung;

public class Data {

    private int volume = 0;

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    private boolean on = false;

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    private boolean paused = false;

    public int getPreMuteVolume() {
        return preMuteVolume;
    }

    private int preMuteVolume;
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

    private boolean muted = false;
    private static final Data ourInstance = new Data();

    public static Data getInstance() {
        return ourInstance;
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



    private Data() {

    }
}
