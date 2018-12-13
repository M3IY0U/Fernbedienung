package com.sundeep.timo.fernbedienung;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

public class ChannelAdapter extends RecyclerView.Adapter<ChannelAdapter.ChannelViewHolder> {
    private ArrayList<Channel> dataSet;
    private HttpRequest req;

    public ChannelAdapter(ArrayList<Channel> dataSet) {
        this.dataSet = dataSet;
    }

    @NonNull
    @Override
    public ChannelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_channel, parent, false);

        return new ChannelViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChannelViewHolder holder, int position) {
        final Channel channel = dataSet.get(position);
        holder.channelName.setText(channel.getProgram());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                req = new HttpRequest(Data.getInstance().getIp(),"8080",25000,true);
                try {
                    Data.getInstance().setCurrentChannelIndex(Data.getInstance().getChannels().indexOf(channel));
                    req.execute("channelMain=" + channel.getChannel());
                    MainActivity.updateChannelText();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        if(channel.isFavorited()) {
            holder.favIcon.setColorFilter(Color.argb(255, 255, 255,255));
        } else {
            holder.favIcon.clearColorFilter();
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class ChannelViewHolder extends RecyclerView.ViewHolder {
        public ImageView favIcon;
        public TextView channelName;
        public ChannelViewHolder(View itemView) {
            super(itemView);
            favIcon = (ImageView)itemView.findViewById(R.id.recyclerview_item_channel_fav_icon);
            channelName = (TextView)itemView.findViewById(R.id.recyclerview_item_channel_name);
        }
    }
}