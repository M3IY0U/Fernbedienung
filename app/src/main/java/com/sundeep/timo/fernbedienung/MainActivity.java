package com.sundeep.timo.fernbedienung;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {
    SeekBar volumeBar;
    TextView volumeDisplay;
    ImageButton muteButton;
    ImageButton pauseButton;
    public HttpRequest req;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        volumeDisplay = (TextView)findViewById(R.id.volumeDisplay);
        muteButton = (ImageButton)findViewById(R.id.muteButton);
        pauseButton = (ImageButton) findViewById(R.id.pauseButton);
        updateVolumeText();
        volumeBar = (SeekBar)findViewById(R.id.volumeBar);
        //volumeBar.setLayoutParams(new LinearLayout.LayoutParams(this.getResources().getDisplayMetrics().widthPixels/2,25));

        req = new HttpRequest(Data.getInstance().getIp(),"8080",25000,true);
        volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Data.getInstance().setVolume(progress);
                Data.getInstance().setMuted(false);
                muteButton.setImageResource(R.drawable.ic_volume_up_black_24dp);
                updateVolumeText();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    protected void onPause() {
        Data.getInstance().save(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        Data.getInstance().restore(this);
        volumeBar.setProgress(Data.getInstance().getVolume());
        updateVolumeText();
        req.ipAddress = Data.getInstance().getIp();
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
        //    return true;
        //}

        return super.onOptionsItemSelected(item);
    }

    public void toggleMute(View v){
        Data.getInstance().toggleMute();
        if(Data.getInstance().isMuted()){
            muteButton.setImageResource(R.drawable.ic_volume_off_black_24dp);
        }else{
            muteButton.setImageResource(R.drawable.ic_volume_up_black_24dp);
        }
        updateVolumeText();
    }

    public void togglePause(View v){
        Context context = getApplicationContext();
        CharSequence text;
        if(!Data.getInstance().isPaused()){
            pauseButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);
            text = "Programm wurde pausiert!";
        }else{
            pauseButton.setImageResource(R.drawable.ic_pause_black_24dp);
            text = "Programm wird fortgesetzt!";
        }
        Data.getInstance().togglePause();
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void startSettings(MenuItem m){
        Intent i = new Intent(
                this,
                SettingsActivity.class);
        startActivity(i);

    }

    public void enterIp(MenuItem m){
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Bitte tragen sie eine gültige IP ein");
        final EditText input = new EditText(this);
        b.setView(input);
        input.setText(Data.getInstance().getIp());
        b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Data.getInstance().setIp(input.getText().toString());
                req.ipAddress = input.getText().toString();
            }
        });
        b.setNegativeButton("CANCEL",null);
        b.show();
    }

    public void togglePower(View v){
        ImageButton pwrbtn = findViewById(R.id.powerButton);
        String command= "standby=1";
        if(Data.getInstance().isOn()){
            command = "standby=0";
        }
        try {
            req.execute(command);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Data.getInstance().setOn(!Data.getInstance().isOn());
    }

    public void channelSearch(MenuItem m){
        JSONObject response = null;
        try {
            response = req.sendHttp("scanChannels=");

        JSONArray channels = response.getJSONArray("channels");
        ArrayList<Channel> channelArrayList = new ArrayList<>();
        for(int i = 0; i < channels.length(); ++i){
            JSONObject jsonChannel = channels.getJSONObject(i);
            Channel channel = new Channel();
            channel.setChannel(jsonChannel.getString("channel"));
            channel.setFrequency(jsonChannel.getInt("frequency"));
            channel.setProgram(jsonChannel.getString("program"));
            channel.setProvider(jsonChannel.getString("provider"));
            channel.setQuality(jsonChannel.getInt("quality"));
            boolean dupe = false;
            for(int j = 0; j < channelArrayList.size(); ++j){
                if(channel.getProgram()==channelArrayList.get(j).getProgram()){
                    dupe = true;
                    if(channelArrayList.get(j).getQuality()<channel.getQuality()){
                        channelArrayList.set(j,channel);
                    }
                }
            }
            if(!dupe)
                channelArrayList.add(channel);

        }

        Data.getInstance().setChannels(channelArrayList);
        Data.getInstance().setCurrentChannelIndex(0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void startChannelList(View v){
        if(Data.getInstance().getChannels()==null){
            Context context = getApplicationContext();
            Data.getInstance().togglePause();
            Toast toast = Toast.makeText(context,"Es wurde noch kein Channelscan durchgeführt!", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        Intent i = new Intent(
                this,
                ChannelListActivity.class
        );
        startActivity(i);
    }

    public void toggleRatios(View v){
        LinearLayout ratios = findViewById(R.id.ratioButtons);
        if(ratios.getVisibility()==View.GONE){
            expand(ratios,1);
        }else {
            collapse(ratios,1);
        }
    }


    private void updateVolumeText(){
        volumeDisplay.setText(Integer.toString(Data.getInstance().getVolume()));
    }


    public static void expand(final View view, int durationMultiplier) {
        if (view.getVisibility() == View.GONE) {

            view.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            final int targetWidth = view.getMeasuredWidth();
            view.getLayoutParams().width = 1;
            view.setVisibility(View.VISIBLE);
            Animation a = new Animation() {

                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    view.getLayoutParams().width = (int) (targetWidth * interpolatedTime);
                    view.requestLayout();
                }

                @Override
                public boolean willChangeBounds() {
                    return true;
                }
            };

            a.setDuration(((int) (targetWidth / view.getContext().getResources().getDisplayMetrics().density)) * durationMultiplier);
            view.startAnimation(a);
        }
    }

    public static void collapse(final View view, int durationMultiplier) {
        if (view.getVisibility() == View.VISIBLE) {


            final int initialWidth = view.getMeasuredWidth();
            Animation a = new Animation() {

                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    if (interpolatedTime == 1) {
                        view.setVisibility(View.GONE);
                    } else {
                        view.getLayoutParams().width = initialWidth - (int) (initialWidth * interpolatedTime);
                        view.requestLayout();
                    }
                }

                @Override
                public boolean willChangeBounds() {
                    return true;
                }
            };

            a.setDuration(((int) (initialWidth / view.getContext().getResources().getDisplayMetrics().density)) * durationMultiplier);
            view.startAnimation(a);
        }
    }
}
