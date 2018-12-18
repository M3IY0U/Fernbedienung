package com.sundeep.timo.fernbedienung;

public class Channel {
    private int frequency;
    private String channel;
    private int quality;
    private String program;
    private boolean favorited;


    public Channel(){

    }

/*
    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        Channel test = (Channel)obj;
        return test.getChannel().equals(this.channel);
    }
*/

    public boolean isFavorite() {
        return favorited;
    }

    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public int getQuality() {
        return quality;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    private String provider;
}
