package com.sundeep.timo.fernbedienung;

import java.util.Comparator;

public class ChannelComparator implements Comparator<Channel> {

    public int compare(Channel c1, Channel c2){
        int w1 = 0;
        int w2 = 0;

        if(c1.isFavorite() || c1.isFavorite()){
            w1 = 1;
        }

        if(c2.isFavorite() || c2.isFavorite()){
            w2 = 1;
        }

        if (c1 == c2)
            return 0;
        else if (w1 > w2)
            return 1;
        else
            return -1;
    }
}

