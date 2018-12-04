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

public class MainActivity extends AppCompatActivity {
    SeekBar volumeBar;
    TextView volumeDisplay;
    ImageButton muteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        volumeDisplay = (TextView)findViewById(R.id.volumeDisplay);
        muteButton = (ImageButton)findViewById(R.id.muteButton);
        updateVolumeText();
        volumeBar = (SeekBar)findViewById(R.id.volumeBar);
        //volumeBar.setLayoutParams(new LinearLayout.LayoutParams(this.getResources().getDisplayMetrics().widthPixels/2,25));
        volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Data.getInstance().setVolume(progress);
                Data.getInstance().setMuted(false);
                muteButton.setImageResource(R.drawable.baseline_volume_up_black_48);
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void toggleMute(View v){
        Data.getInstance().toggleMute();
        if(Data.getInstance().isMuted()){
            muteButton.setImageResource(R.drawable.baseline_volume_off_black_48);
        }else{
            muteButton.setImageResource(R.drawable.baseline_volume_up_black_48);
        }
        updateVolumeText();
    }


    public void startSettings(MenuItem m){
        Intent i = new Intent(
                this,
                SettingsActivity.class);
        startActivity(i);

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
