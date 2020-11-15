package com.mycompanydomain.lights;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity
        implements AsyncResponse, TimePickerFragment.TimePickerDialogListener
{

//    public final static String EXTRA_MESSAGE = "com.mycompany.myfirstapp.MESSAGE";
    String sSend; // stores last value sent
//    String sPrefServerIP;
    ImageView ivServerLinkStatus;
    boolean bServerCheckBusy;
    WifiManager wifi;
    int iSelectedChannel;
    int[] iCurrentBrightnessPerChannel = new int[4];
    int iChannel1CurrentBrightness;
    int iChannel2CurrentBrightness;
    int iChannel3CurrentBrightness;
    int iChannel4CurrentBrightness;
    ImageButton ibMainButton;
    SeekBar sbBrightness;
    boolean bSeekbarTracking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            // Restore value of members from saved state
//            mCurrentScore = savedInstanceState.getInt(STATE_SCORE);
//            mCurrentLevel = savedInstanceState.getInt(STATE_LEVEL);
        }
        else {
            // Probably initialize members with default values for a new instance
        }

        loadPref();
        initializeVariables();
    }

    //
    @Override
    protected void onStart() {
        super.onStart();
/*
        bServerCheckBusy = true;
        ivServerLinkStatus.setImageResource(R.drawable.status_grey);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                checkServerLink();
            }
        }, 500); // this code will be executed after x milliseconds delay
*/

        if (!wifi.isWifiEnabled()){
            new AlertDialog.Builder(this)
                    .setTitle("Wifi needs to be on")
                    .setMessage("Enable Wifi to connect to lights controller?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // button press response
                            wifi.setWifiEnabled(true);
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // button press response
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
/*
        // Save the user's current game state
        savedInstanceState.putInt(STATE_SCORE, mCurrentScore);
        savedInstanceState.putInt(STATE_LEVEL, mCurrentLevel);
*/

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        // Restore state members from saved instance
//        mCurrentScore = savedInstanceState.getInt(STATE_SCORE);
//        mCurrentLevel = savedInstanceState.getInt(STATE_LEVEL);

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
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, SetPreferenceActivity.class);
            startActivityForResult(intent, 0);
            return true;
        }

        if (id == R.id.action_extra) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, TestActivity.class);
            startActivityForResult(intent, 0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        //super.onActivityResult(requestCode, resultCode, data);

        loadPref();
    }

    //
    private void loadPref(){
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
//        SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

//        String sPrefServerIP = mySharedPreferences.getString("pref_et_serverIP", "not initialised");
//        String sPrefServerPort = mySharedPreferences.getString("pref_et_serverPort", "not initialised");

//        String prefList = mySharedPreferences.getString("pref_l_fadeSpeed", "no selection");
//        sendOnSocket(prefList);
    }

    // A private method to help us initialize our variables.
    private void initializeVariables() {
        sSend = "default";

        wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);

        ivServerLinkStatus = (ImageView) findViewById(R.id.iv_statuslight);
        bServerCheckBusy = false;
        timerHandler.postDelayed(timerRunnable, 0);

        ibMainButton = (ImageButton) findViewById(R.id.imageButton1);

        bSeekbarTracking = false;
        sbBrightness = (SeekBar) findViewById(R.id.sb_brightness);
        sbBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                setChannel(4);
                bSeekbarTracking = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                bSeekbarTracking = false;
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                if (bSeekbarTracking) {
                    int progress = 100 * progressValue / seekBar.getMax();
                    setBrightness(progress);
                }
            }
        });

        SeekBar mySB2 = (SeekBar) findViewById(R.id.sb_spectrum);
        mySB2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                int sect = seekBar.getMax() / 5;
                if ((progressValue >= 0) && (progressValue <= sect)) {
                    // violet to blue
                    setChannel(1);
                    setBrightness(0);
                    setChannel(2);
                    int i = (100 - (100 * progressValue / sect));
                    setBrightness(i);
                    setChannel(3);
                    setBrightness(100);
                }
                if ((progressValue > sect) && (progressValue <= (2 * sect))) {
                    // blue to turquoise
                    setChannel(1);
                    int i = (100 * (progressValue - sect) / sect);
                    setBrightness(i);
                    setChannel(2);
                    setBrightness(0);
                    setChannel(3);
                    setBrightness(100);
                }
                if ((progressValue > (2 * sect)) && (progressValue <= (3 * sect))) {
                    // turquoise to green
                    setChannel(1);
                    setBrightness(100);
                    setChannel(2);
                    setBrightness(0);
                    setChannel(3);
                    int i = (100 - (100 * (progressValue - (2 * sect)) / sect));
                    setBrightness(i);
                }
                if ((progressValue > (3 * sect)) && (progressValue <= (4 * sect))) {
                    // green to orange
                    setChannel(1);
                    setBrightness(100);
                    setChannel(2);
                    int i = (100 * (progressValue - (3 * sect)) / sect);
                    setBrightness(i);
                    setChannel(3);
                    setBrightness(0);
                }
                if ((progressValue > (4 * sect)) && (progressValue <= (5 * sect))) {
                    // orange to red
                    setChannel(1);
                    int i = (100 - (100 * (progressValue - (4 * sect)) / sect));
                    setBrightness(i);
                    setChannel(2);
                    setBrightness(100);
                    setChannel(3);
                    setBrightness(0);
                }
            }
        });

//        myTV.setText("Covered: " + mySB1.getProgress() + "/" + mySB1.getMax());

    }

    /** Called when the user clicks the button */
    public void openTest(View view) {
        Intent intent = new Intent(this, TestActivity.class);
        startActivity(intent);
    }

    /** Called when the user clicks the button */
    public void button_ch1main(View view) {
        setChannel(4);
        int i = iCurrentBrightnessPerChannel[iSelectedChannel-1];

        if (i >= 100) {
            i = 0;
        }
        else if (i >= 50) {
            i = 100;
        }
        else {
            i = 50;
        }

        setBrightness(i);
    }

    /** Called when the user clicks the button */
    public void button_ch1sleep(View view) {
        setChannel(4);
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }

    /** Called when the user clicks the button */
    public void button_ch1button3(View view) {
        setChannel(4);
    }

    /** Called when the user clicks the button */
    public void button_ch1button4(View view) {
        setChannel(4);
    }

    /** Called when the user clicks the button */
    public void button_ch1increase(View view) {
        setChannel(4);
        int i = iCurrentBrightnessPerChannel[iSelectedChannel-1];
        if ((i>=0) && (i<=19))
            i = 20;
        else if ((i>=20) && (i<=39))
            i = 40;
        else if ((i>=40) && (i<=59))
            i = 60;
        else if ((i>=60) && (i<=79))
            i = 80;
        else if ((i>=80) && (i<=100))
            i = 100;
        setBrightness(i);
    }

    /** Called when the user clicks the button */
    public void button_ch1decrease(View view) {
        setChannel(4);
        int i = iCurrentBrightnessPerChannel[iSelectedChannel-1];
        if ((i>=0) && (i<=20))
            i = 0;
        else if ((i>=21) && (i<=40))
            i = 20;
        else if ((i>=41) && (i<=60))
            i = 40;
        else if ((i>=61) && (i<=80))
            i = 60;
        else if ((i>=81) && (i<=100))
            i = 80;
        setBrightness(i);
    }

    /** Called when the user clicks the button */
    public void button_ch1off(View view) {
        setChannel(1);
        setBrightness(0);
        setChannel(2);
        setBrightness(0);
        setChannel(3);
        setBrightness(0);
        setChannel(4);
        setBrightness(0);
    }

    //
    private void doSleepButtonAction() {
//        iCurrentBrightnessPerChannel[iSelectedChannel-1] = 10;
//        int iDuty = convertBrightness(iCurrentBrightnessPerChannel[iSelectedChannel-1]);
//        sSend = Integer.toString(iDuty);
//        sendOnSocket(sSend);

/*
        sSend = "sleep";
        sendOnSocket(sSend);
        Toast.makeText(getApplicationContext(), "lights out set", Toast.LENGTH_SHORT).show();
*/
    }

    //
    private int convertBrightness(int requestedValue) {
        return ( requestedValue * 24 / (124-requestedValue) );
    }

    //
    private void sendOnSocket(String toSend){
        sSend = toSend; // store last value sent
        new SocketOperationSend(sSend, this).execute("");
    }

    //
    private void checkServerLink(){
        ivServerLinkStatus.setImageResource(R.drawable.status_grey);
        bServerCheckBusy = true;
        SocketOperationTest asyncTask = new SocketOperationTest(this);
        asyncTask.delegate = this;
        asyncTask.execute("");
    }

    // not tested
    public void processFinish(String output){
        String compare = "online";
        if (output.equals(compare)){
            ivServerLinkStatus.setImageResource(R.drawable.status_green);
        }
        else
            ivServerLinkStatus.setImageResource(R.drawable.status_red);
    }

    //
    public void processFinish(boolean output){
        if (output) {
            ivServerLinkStatus.setImageResource(R.drawable.status_green);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ivServerLinkStatus.setImageResource(R.drawable.status_grey);
                        }
                    });
                }
            }, 100); // this code will be executed after x milliseconds delay
        }
        else {
            ivServerLinkStatus.setImageResource(R.drawable.status_red);
        }
        bServerCheckBusy = false;
    }

    // not tested
    public void processFinish(int output){
        if (output == 2) {
            ivServerLinkStatus.setImageResource(R.drawable.status_green);
        }
        else if (output == 1) {
            ivServerLinkStatus.setImageResource(R.drawable.status_yellow);
        }
        else {
            ivServerLinkStatus.setImageResource(R.drawable.status_red);
        }
    }

    //runs without a timer by reposting this handler at the end of the runnable
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (wifi.isWifiEnabled()) {
                if (!bServerCheckBusy){
                    checkServerLink();
                }
            }
            else {
                ivServerLinkStatus.setImageResource(R.drawable.status_grey_x);
            }
            timerHandler.postDelayed(this, 2000);
        }
    };

    //
    public void onTimeSet(DialogFragment dialog, int hour, int minute){
        doSleepButtonAction();
//        setBrightness(minute);

        int seconds = (minute*60) + (hour*60*60);
        minute = minute + (hour*60);
        sSend = "sleep," + 4 + "," + minute;
        sendOnSocket(sSend);
        Toast.makeText(getApplicationContext(), "lights out set", Toast.LENGTH_SHORT).show();
    }

    //
    private void setChannel(int channel) {
        if (channel != iSelectedChannel) {
            switch(channel) {
                case 1:
                    sSend = "101";
                    break;
                case 2:
                    sSend = "102";
                    break;
                case 3:
                    sSend = "103";
                    break;
                case 4:
                    sSend = "104";
                    break;
                default:
                    break;
            }
            sendOnSocket(sSend);
            iSelectedChannel = channel;
        }
    }

    //
    private void setBrightness(int brightness) {
        if (brightness < 0)
            brightness = 0;
        if (brightness > 100)
            brightness = 100;

        TextView tv;
        switch (iSelectedChannel) {
            case 4:
                tv = (TextView) findViewById(R.id.textView2);
                tv.setText(brightness + " %");
                break;
            default:
                break;
        }

        if (brightness == 100) {
            ibMainButton.setImageResource(R.mipmap.ic_action_bightness_low);
        }
        else if (brightness >= 50) {
            ibMainButton.setImageResource(R.mipmap.ic_action_brightness_high);
        }
        else {
            ibMainButton.setImageResource(R.mipmap.ic_action_brightness_medium);
        }

        int i = sbBrightness.getMax();
        i = i * brightness / 100;
        sbBrightness.setProgress(i);

        int duty = convertBrightness(brightness);
        sSend = Integer.toString(duty);
        sendOnSocket(sSend);
        iCurrentBrightnessPerChannel[iSelectedChannel-1] = brightness;
    }


}
