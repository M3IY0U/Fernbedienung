package com.sundeep.timo.fernbedienung;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

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

        channelAdapter = new ChannelAdapter(new ArrayList<Channel>(Data.getInstance().getChannels()));
        channelRecyclerView.setAdapter(channelAdapter);
    }


}
