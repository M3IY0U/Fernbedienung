package com.sundeep.timo.fernbedienung;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Time;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    SeekBar volumeBar;
    TextView volumeDisplay;
    static TextView currentChannel;
    ImageButton muteButton;
    ImageButton pauseButton;
    static ImageButton favButton;
    ImageButton pwrbtn;
    HttpRequestAsync reqA;
    Time time = new Time(0);
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        volumeDisplay = findViewById(R.id.volumeDisplay);
        muteButton = findViewById(R.id.muteButton);
        currentChannel = findViewById(R.id.currentChannel);
        favButton = findViewById(R.id.favButton);
        pwrbtn = findViewById(R.id.powerButton);
        pauseButton = findViewById(R.id.pauseButton);
        updateVolumeText();
        updateChannelText();
        updateFavButton();
        volumeBar = findViewById(R.id.volumeBar);
        volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(!checkIp()){
                    Toast toast = Toast.makeText(getApplicationContext(), "Keine gültige IP eingetragen!", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                Data.getInstance().setVolume(progress);
                Data.getInstance().setMuted(false);
                muteButton.setImageResource(R.drawable.ic_volume_up_black_24dp);
                updateVolumeText();
                reqA = new HttpRequestAsync();
                reqA.execute("volume=" + Integer.toString(Data.getInstance().getVolume()));
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
        updateVolumeText();
        updateFavButton();
        updateChannelText();
        updatePowerButton();
        if(!checkIp()) {
            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setTitle("Bitte tragen sie eine gültige IP ein");
            final EditText input = new EditText(this);
            b.setView(input);
            input.setInputType(InputType.TYPE_CLASS_PHONE);
            input.setText(Data.getInstance().getIp());
            b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Data.getInstance().setIp(input.getText().toString());
                    Data.getInstance().save(getApplicationContext());
                }
            });
            b.setNegativeButton("CANCEL", null);
            b.show();
        }
        volumeBar.setProgress(Data.getInstance().getVolume());
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

    public void toggleMute(View v) {
        if(!checkIp()){
            Toast toast = Toast.makeText(getApplicationContext(), "Keine gültige IP eingetragen!", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        Data.getInstance().toggleMute();
        reqA = new HttpRequestAsync();
        if (Data.getInstance().isMuted()) {
            muteButton.setImageResource(R.drawable.ic_volume_off_black_24dp);
            reqA.execute("volume=0");
        } else {
            muteButton.setImageResource(R.drawable.ic_volume_up_black_24dp);
            reqA.execute("volume=" + Integer.toString(Data.getInstance().getPreMuteVolume()));
        }
        updateVolumeText();
    }


    public void killTv(MenuItem m) {
        if(!checkIp()){
            Toast toast = Toast.makeText(getApplicationContext(), "Keine gültige IP eingetragen!", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        reqA = new HttpRequestAsync();
        reqA.execute("powerOff=");
    }

    public void toggleDebug(MenuItem m) {
        if(!checkIp()){
            Toast toast = Toast.makeText(getApplicationContext(), "Keine gültige IP eingetragen!", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        reqA = new HttpRequestAsync();
        if (m.isChecked()) {
            reqA.execute("debug=0");
            m.setChecked(false);
        } else {
            reqA.execute("debug=1");
            m.setChecked(true);
        }
    }


    public void togglePause(View v) {
        if(!checkIp()){
            Toast toast = Toast.makeText(getApplicationContext(), "Keine gültige IP eingetragen!", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        reqA = new HttpRequestAsync();
        CharSequence text;
        if (!Data.getInstance().isPaused()) {
            time.setTime(SystemClock.elapsedRealtime());
            pauseButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);
            text = "Programm wurde pausiert! ";
            reqA.execute("timeShiftPause=");

        } else {
            pauseButton.setImageResource(R.drawable.ic_pause_black_24dp);
            text = "Programm wird fortgesetzt! ";
            reqA.execute("timeShiftPlay=" + Math.round(SystemClock.elapsedRealtime() - time.getTime())/1000);
        }
        Data.getInstance().togglePause();
        Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
        toast.show();
    }


    public void enterIp(MenuItem m) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Bitte tragen sie eine gültige IP ein");
        final EditText input = new EditText(this);
        b.setView(input);
        input.setInputType(InputType.TYPE_CLASS_PHONE);
        input.setText(Data.getInstance().getIp());
        b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Data.getInstance().setIp(input.getText().toString());
            }
        });
        b.setNegativeButton("CANCEL", null);
        b.show();
    }

    public void togglePower(View v) {
        if(!checkIp()){
            Toast toast = Toast.makeText(getApplicationContext(), "Keine gültige IP eingetragen!", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        String command = "standby=1";
        if (Data.getInstance().isOn()) {
            command = "standby=0";
        }
        updatePowerButton();
        reqA = new HttpRequestAsync();
        reqA.execute(command);
        Data.getInstance().setOn(!Data.getInstance().isOn());
    }

    public void channelSearch(MenuItem m) {
        if(!checkIp()){
            Toast toast = Toast.makeText(getApplicationContext(), "Keine gültige IP eingetragen!", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        reqA = new HttpRequestAsync();
        reqA.execute("scanChannels=");
        Data.getInstance().save(this);
    }

    public void toggleFavorite(View v){
        if(Data.getInstance().getChannels().size()==0){
            Toast toast = Toast.makeText(this, "Es wurde noch kein erfolgreicher Channel Scan durchgeführt", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        ImageButton btn = findViewById(R.id.favButton);
        Data.getInstance().getCurrentChannel().setFavorited(!Data.getInstance().getCurrentChannel().isFavorite());
        if(Data.getInstance().getCurrentChannel().isFavorite()){
            btn.setColorFilter(Color.YELLOW);
        }
        else {
            btn.clearColorFilter();
        }
        updateFavButton();
    }


    public void togglePip(View v){
        if(!checkIp()){
            Toast toast = Toast.makeText(getApplicationContext(), "Keine gültige IP eingetragen!", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        if(Data.getInstance().getChannels().size()==0){
            Toast toast = Toast.makeText(this, "Es wurde noch kein erfolgreicher Channel Scan durchgeführt", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        reqA = new HttpRequestAsync();
        if(Data.getInstance().getPictureInPictureChannel()==null){
            Data.getInstance().setPictureInPictureChannel(Data.getInstance().getCurrentChannel());
            reqA.execute("showPip=1");
            reqA = new HttpRequestAsync();
            reqA.execute("channelPip="+Data.getInstance().getCurrentChannel().getChannel());
            reqA =  new HttpRequestAsync();
            if(!Data.getInstance().getRatio().equals("16:9")){
                reqA.execute("zoomPip=1");
            }else{
                reqA.execute("zoomPip=0");
            }
        }else{
            Data.getInstance().setPictureInPictureChannel(null);
            reqA.execute("showPip=0");
        }
    }


    public void nextChannel(View view) {
        if(!checkIp()){
            Toast toast = Toast.makeText(getApplicationContext(), "Keine gültige IP eingetragen!", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        if (Data.getInstance().getChannels().size()==0 || Data.getInstance().getChannels() == null) {
            Toast toast = Toast.makeText(getApplicationContext(), "Es wurde noch kein erfolgreicher Channelscan durchgeführt!", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        if (Data.getInstance().getNextChannel() != null) {
            reqA = new HttpRequestAsync();
            reqA.execute("channelMain=" + Data.getInstance().getNextChannel().getChannel());
            updateChannelText();
            updateFavButton();
        }
    }

    public void prevChannel(View view) {
        if(!checkIp()){
            Toast toast = Toast.makeText(getApplicationContext(), "Keine gültige IP eingetragen!", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        if (Data.getInstance().getChannels().size()==0 || Data.getInstance().getChannels() == null) {
            Toast toast = Toast.makeText(getApplicationContext(), "Es wurde noch kein erfolgreicher Channelscan durchgeführt!", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        if (Data.getInstance().getPreviousChannel() != null) {
            reqA = new HttpRequestAsync();
            reqA.execute("channelMain=" + Data.getInstance().getPreviousChannel().getChannel());
            updateChannelText();
            updateFavButton();
        }
    }

    public void startChannelList(View v) {
        if (Data.getInstance().getChannels().size()==0 || Data.getInstance().getChannels() == null) {
            Toast toast = Toast.makeText(getApplicationContext(), "Es wurde noch kein erfolgreicher Channelscan durchgeführt!", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        Intent i = new Intent(
                this,
                ChannelListActivity.class
        );
        startActivity(i);
    }

    public void ratioCinema(View v) {
        if(!checkIp()){
            Toast toast = Toast.makeText(getApplicationContext(), "Keine gültige IP eingetragen!", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        if (!Data.getInstance().getRatio().equals("2.35:1")) {
            reqA = new HttpRequestAsync();
            Data.getInstance().setRatio("2.35:1");
            reqA.execute("zoomMain=1");
        }
    }

    public void ratio4to3(View v) {
        if(!checkIp()){
            Toast toast = Toast.makeText(getApplicationContext(), "Keine gültige IP eingetragen!", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        if (!Data.getInstance().getRatio().equals("4:3")) {
            reqA = new HttpRequestAsync();
            Data.getInstance().setRatio("4:3");
            reqA.execute("zoomMain=1");
        }
    }

    public void ratio16to9(View v) {
        if(!checkIp()){
            Toast toast = Toast.makeText(getApplicationContext(), "Keine gültige IP eingetragen!", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        if (!Data.getInstance().getRatio().equals("16:9")) {
            reqA = new HttpRequestAsync();
            Data.getInstance().setRatio("16:9");
            reqA.execute("zoomMain=0");
        }
    }


    public void reset(MenuItem m) {
        Data.getInstance().reset();
        Data.getInstance().save(this);
        finish();
        startActivity(getIntent());
    }

    private void updatePowerButton(){
        if(Data.getInstance().isOn()){
            pwrbtn.setColorFilter(Color.GREEN);
        }else {
            pwrbtn.clearColorFilter();
        }
    }

    private void updateVolumeText() {
        volumeDisplay.setText(Integer.toString(Data.getInstance().getVolume()));
    }

    public static void updateChannelText() {
        if (Data.getInstance().getCurrentChannel() != null) {
            currentChannel.setText(Data.getInstance().getCurrentChannel().getProgram());
        } else {
            currentChannel.setText(R.string.no_channel);
        }
    }
    public static void updateFavButton(){
        if(Data.getInstance().getCurrentChannel()!=null){
            if(Data.getInstance().getCurrentChannel().isFavorite()){
                favButton.setColorFilter(Color.YELLOW);
            }else{
                favButton.clearColorFilter();
            }
        }else{
            favButton.clearColorFilter();
        }

    }

    public static boolean checkIp(){
        if(Data.getInstance().getIp().isEmpty() || Data.getInstance().getIp().equals("") || Data.getInstance().getIp() == null)
            return false;

        final String IPADDRESS_PATTERN =
                "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
        Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
        Matcher matcher;
        matcher = pattern.matcher(Data.getInstance().getIp());
        return matcher.matches();
    }

}
