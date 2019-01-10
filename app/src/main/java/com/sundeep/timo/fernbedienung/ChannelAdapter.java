package com.sundeep.timo.fernbedienung;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ChannelAdapter extends RecyclerView.Adapter<ChannelAdapter.ChannelViewHolder> {
    private ArrayList<Channel> dataSet;

    ChannelAdapter(ArrayList<Channel> dataSet) {
        this.dataSet = dataSet;
    }

    @NonNull
    @Override
    public ChannelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_channel, parent, false);

        return new ChannelViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChannelViewHolder holder, int position) {
        final Channel channel = dataSet.get(position);
        holder.channelName.setText(channel.getProgram());
        holder.favIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!channel.isFavorite()) {
                    holder.favIcon.setColorFilter(Color.YELLOW);
                }else {
                    holder.favIcon.clearColorFilter();
                }
                for(int i=0;i<Data.getInstance().getChannels().size();i++){
                    if(Data.getInstance().getChannels().get(i).getChannel().equals(channel.getChannel())){
                        Data.getInstance().getChannels().get(i).setFavorited(!Data.getInstance().getChannels().get(i).isFavorite());
                        notifyDataSetChanged();
                    }
                }
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Data.getInstance().setCurrentChannel(channel.getChannel());
                HttpRequestAsync d = new HttpRequestAsync();
                d.execute("channelMain=" + channel.getChannel());
                MainActivity.updateChannelText();
            }
        });
        if (channel.isFavorite()) {
            holder.favIcon.setColorFilter(Color.YELLOW);
        } else {
            holder.favIcon.clearColorFilter();
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    static class ChannelViewHolder extends RecyclerView.ViewHolder {
        ImageView favIcon;
        TextView channelName;

        ChannelViewHolder(View itemView) {
            super(itemView);
            favIcon = itemView.findViewById(R.id.recyclerview_item_channel_fav_icon);
            channelName = itemView.findViewById(R.id.recyclerview_item_channel_name);
        }
    }
}