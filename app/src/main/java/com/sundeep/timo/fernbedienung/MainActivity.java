package com.sundeep.timo.fernbedienung;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    SeekBar volumeBar;
    TextView volumeDisplay;
    ImageButton muteButton;
    ImageButton pauseButton;
    HttpRequest req;

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

        req = new HttpRequest("172.16.206.140","8080",1000,true);
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

    public void startChannelList(View v){
        Intent i = new Intent(
                this,
                ChannelListActivity.class
        );
        startActivity(i);
    }

    public void toggleRatios(View v){
        LinearLayout ratios = findViewById(R.id.ratioButtons);
        ImageButton lister = findViewById(R.id.ratioLister);

        if(ratios.getVisibility()==View.GONE){
            for (int i = 1;i<46;i++){
                lister.setRotation(i);
            }
            expand(ratios,1);
        }else {
            for (int i = 45;i>0;i--){
                lister.setRotation(i);
            }
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
