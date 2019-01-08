package com.sundeep.timo.fernbedienung;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Collections;

public class ChannelListActivity extends AppCompatActivity {

    private RecyclerView channelRecyclerView;
    private RecyclerView.Adapter channelAdapter;
    private LinearLayoutManager channelLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_list);
        channelRecyclerView = findViewById(R.id.channels_recyclerview);

        channelLayoutManager = new LinearLayoutManager(this);
        channelLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        channelRecyclerView.setLayoutManager(channelLayoutManager);
        ArrayList<Channel> temp = new ArrayList<Channel>(Data.getInstance().getChannels());
        Collections.sort(temp,Collections.<Channel>reverseOrder(new com.sundeep.timo.fernbedienung.ChannelComparator()));
        channelAdapter = new ChannelAdapter(temp);
        channelRecyclerView.setAdapter(channelAdapter);
    }

    @Override
    protected void onPause() {
        Data.getInstance().save(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        Data.getInstance().restore(this);
        ArrayList<Channel> t = new ArrayList<Channel>(Data.getInstance().getChannels());
        Collections.sort(t, Collections.reverseOrder(new com.sundeep.timo.fernbedienung.ChannelComparator()));
        channelAdapter = new ChannelAdapter(t);
        channelRecyclerView.setAdapter(channelAdapter);
        super.onResume();
    }
}
