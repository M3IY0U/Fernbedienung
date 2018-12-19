package com.sundeep.timo.fernbedienung;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class HttpRequestAsync extends AsyncTask<String, Void, Void> {
    @Override
    protected Void doInBackground(String... strings) {
        HttpRequest req = new HttpRequest(Data.getInstance().getIp(), "8080", 25000, false);
        if (strings[0].equals("scanChannels=")) {
            JSONObject response;
            try {
                response = req.sendHttp("scanChannels=");
                JSONArray channels = response.getJSONArray("channels");
                ArrayList<Channel> channelArrayList = new ArrayList<>();
                for (int i = 0; i < channels.length(); ++i) {
                    JSONObject jsonChannel = channels.getJSONObject(i);
                    Channel channel = new Channel();
                    channel.setChannel(jsonChannel.getString("channel"));
                    channel.setFrequency(jsonChannel.getInt("frequency"));
                    channel.setProgram(jsonChannel.getString("program"));
                    channel.setProvider(jsonChannel.getString("provider"));
                    channel.setQuality(jsonChannel.getInt("quality"));
                    boolean dupe = false;
                    for (int j = 0; j < channelArrayList.size(); ++j) {
                        if (channel.getProgram().equals(channelArrayList.get(j).getProgram())) {
                            dupe = true;
                            if (channelArrayList.get(j).getQuality() < channel.getQuality()) {
                                channelArrayList.set(j, channel);
                            }
                        }
                    }
                    if (!dupe)
                        channelArrayList.add(channel);
                }
                Data.getInstance().setChannels(channelArrayList);
                Data.getInstance().setCurrentChannelIndex(0);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                req.sendHttp(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
