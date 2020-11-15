package com.mycompanydomain.lights;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by miro on 2015/03/15.
 */
public class SocketOperationTest extends AsyncTask<String, Void, Boolean> {

    String sPrefServerIP = "10.0.0.11";
    boolean bServerComms = false;
    public AsyncResponse delegate=null;

    public SocketOperationTest() {
    }

    public SocketOperationTest (Context CTX) {
        loadPref(CTX);
    }

    // Load preferences
    private void loadPref(Context CTX){
//        SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(CTX);
        sPrefServerIP = mySharedPreferences.getString("pref_et_serverIP", "");
//        sPrefServerPort = mySharedPreferences.getString("pref_et_serverPort", "");

//        String prefList = mySharedPreferences.getString("pref_l_fadeSpeed", "no selection");
//        new SocketOperation(prefList).execute("");
    }

    @Override
    protected Boolean doInBackground(String... arg0) {
        try {
            bServerComms = InetAddress.getByName(sPrefServerIP).isReachable(1500);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bServerComms;

/*
        if (bServerComms)
            return "online";
        else
            return "offline";
*/

//        return null;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        delegate.processFinish(result);
    }

}
