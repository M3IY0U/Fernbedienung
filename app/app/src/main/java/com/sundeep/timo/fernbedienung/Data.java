package com.sundeep.timo.fernbedienung;

public class Data {

    private int volume = 0;

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

    private Data() {

    }
}
