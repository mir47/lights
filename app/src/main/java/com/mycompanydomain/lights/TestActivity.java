package com.mycompanydomain.lights;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;


public class TestActivity extends Activity
        implements ProgramCheckResponse, BrightnessBarDialogFragment.NoticeDialogListener
{

    private TextView myTV1, myTV2;
    SeekBar mySB1;
    String sSend;
    EditText myET;
    CheckBox prefCheckBox;
    TextView prefEditText;
    TextView tvServerProgramStatus1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        initializeVariables();

//        prefCheckBox = (CheckBox)findViewById(R.id.prefCheckBox);
//        prefEditText = (TextView)findViewById(R.id.prefEditText);
        loadPref();

    }

    //
    @Override
    protected void onStart() {
        super.onStart();

        TextView tv = (TextView) findViewById(R.id.tvServerProgramStatus);
        tv.setText("checking server program ...");

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                recvServerProgramCheck();
            }
        }, 1000); // this code will be executed after x milliseconds delay
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_test, menu);
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
            intent.setClass(TestActivity.this, SetPreferenceActivity.class);
            startActivityForResult(intent, 0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        //super.onActivityResult(requestCode, resultCode, data);

        //To make it simple, always re-load Preference setting.
        loadPref();
    }

    // A private method to help us initialize our variables.
    private void initializeVariables() {
        sSend = "default";

        myET = (EditText) findViewById(R.id.etTextToSend);

        tvServerProgramStatus1 = (TextView) findViewById(R.id.tvServerProgramStatus);

        mySB1 = (SeekBar) findViewById(R.id.seekBar1);
        mySB1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                int progress = 100 * progressValue / seekBar.getMax();
                int iBrightness = convertBrightness(progress);
//                myTV1.setText(progress + ":" + iBrightness);
                myTV1.setText("Brightness:" + progress);
                myTV2.setText("Duty Cycle:" + iBrightness);
                sSend = Integer.toString(iBrightness);
                sendOnSocket(sSend);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(getApplicationContext(), "Stopped tracking seekbar", Toast.LENGTH_SHORT).show();
            }
        });

        myTV1 = (TextView) findViewById(R.id.textView6);
        myTV1.setText("Brightness:" + mySB1.getProgress());

        myTV2 = (TextView) findViewById(R.id.textView7);
        myTV2.setText("Duty Cycle:0");

    }

    //
    private void sendOnSocket(String toSend){
        sSend = toSend; // store last value sent
        new SocketOperationSend(toSend, this).execute("");
    }

    private int convertBrightness(int requestedValue) {
        return ( requestedValue * 24 / (124-requestedValue) );
    }

    /** Called when the user clicks the button */
    public void sendMessageOnSocket(View view) {
        sSend = myET.getText().toString();
        new SocketOperationSend(sSend, this).execute("");
    }

    /** Called when the user clicks the button */
    public void OpenBarDialog(View view) {
        DialogFragment newFragment = new BrightnessBarDialogFragment();
        newFragment.show(getFragmentManager(), "BrightnessBar");
    }

    /** Called when the user clicks the button */
    public void sendChannel1(View view) {
        new SocketOperationSend("101", this).execute("");
    }

    /** Called when the user clicks the button */
    public void sendChannel2(View view) {
        new SocketOperationSend("102", this).execute("");
    }

    /** Called when the user clicks the button */
    public void sendChannel3(View view) {
        new SocketOperationSend("103", this).execute("");
    }

    /** Called when the user clicks the button */
    public void sendChannel4(View view) {
        new SocketOperationSend("104", this).execute("");
    }

    /** Called when the user clicks the button */
    public void sendDuty0(View view) {
        new SocketOperationSend("0", this).execute("");
    }

    /** Called when the user clicks the button */
    public void sendDuty10(View view) {
        new SocketOperationSend("10", this).execute("");
    }

    /** Called when the user clicks the button */
    public void sendDuty25(View view) {
        new SocketOperationSend("25", this).execute("");
    }

    /** Called when the user clicks the button */
    public void sendDuty50(View view) {
        new SocketOperationSend("50", this).execute("");
        int progressValue = 50 * mySB1.getMax() / 100;
        mySB1.setProgress(progressValue);
    }

    /** Called when the user clicks the button */
    public void sendDuty75(View view) {
        new SocketOperationSend("75", this).execute("");
    }

    /** Called when the user clicks the button */
    public void sendDuty100(View view) {
        new SocketOperationSend("100", this).execute("");
    }

    /** Called when the user clicks the button */
    public void sendFadeSpeedSlow(View view) {
        new SocketOperationSend("126", this).execute("");
    }

    /** Called when the user clicks the button */
    public void sendFadeSpeedMedium(View view) {
        new SocketOperationSend("123", this).execute("");
    }

    /** Called when the user clicks the button */
    public void sendFadeSpeedFast(View view) {
        new SocketOperationSend("121", this).execute("");
    }

    /** Called when the user clicks the button */
    public void sendFadeSpeedOff(View view) {
        new SocketOperationSend("120", this).execute("");
    }

    /** Called when the user clicks the button */
    public void sendResolutionLow(View view) {
        new SocketOperationSend("131", this).execute("");
    }

    /** Called when the user clicks the button */
    public void sendResolutionMedium(View view) {
        new SocketOperationSend("132", this).execute("");
    }

    /** Called when the user clicks the button */
    public void sendResolutionHigh(View view) {
        new SocketOperationSend("134", this).execute("");
    }

    /** Called when the user clicks the button */
    public void sendResolutionVeryHigh(View view) {
        new SocketOperationSend("138", this).execute("");
    }

    //
    public void sendServerProgramCheck() {
        new SocketOperationSend("LightsServerProgramRunning?", this).execute("");
    }

    //
    public void recvServerProgramCheck() {
        SocketOperationRecv sor = new SocketOperationRecv(this);
        sor.delegate = this;
        sor.execute("");
    }

    //
    public void finish(String output){
        Log.d("Test", "test1");
        String compare = "Yes";
        Log.d("Test", "test2");
        if (output.equals(compare)){
            Log.d("Test", "test3");
            tvServerProgramStatus1.setText("On");
        }
        else {
            Log.d("Test", "test4");
            tvServerProgramStatus1.setText("Off");
        }
    }

    //
    private void loadPref(){
        SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
/*
        boolean my_checkbox_preference = mySharedPreferences.getBoolean("checkbox_preference", false);
        prefCheckBox.setChecked(my_checkbox_preference);

        String my_edittext_preference = mySharedPreferences.getString("edittext_preference", "");
        prefEditText.setText(my_edittext_preference);
*/

        String prefList = mySharedPreferences.getString("pref_l_fadeSpeed", "no selection");
        //       settingList.setText("LIST preference = " + prefList);
        new SocketOperationSend(prefList, this).execute("");

    }

    //
    public void onDialogBarChange(DialogFragment dialog, int progress) {
        int iBrightness = convertBrightness(progress);
        myTV1.setText("Brightness:" + progress);
        myTV2.setText("Duty Cycle:" + iBrightness);
        sSend = Integer.toString(iBrightness);
        sendOnSocket(sSend);
    }

}
