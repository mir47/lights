package com.mycompanydomain.lights;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by miro on 2015/03/15.
 */
public class SocketOperationSend extends AsyncTask<String, Void, String> {

    String sPrefServerIP = "10.0.0.11";
    String sPrefServerPort = "5011";
    String sSend;
    String toSend = "default";

    public SocketOperationSend() {
    }

    public SocketOperationSend (String a) {
        toSend = a;
    }

    public SocketOperationSend (String a, Context myCTX) {
        toSend = a;
        loadPref(myCTX);
    }

    @Override
    protected String doInBackground(String... arg0) {
        Socket socket = null;
        String sServerIP = sPrefServerIP;
        int iServerPort = Integer.parseInt(sPrefServerPort);

        try {
            InetAddress serverAddress = InetAddress.getByName(sServerIP);
            socket = new Socket(serverAddress, iServerPort);
        }
        catch (IOException e) {
            String sTemp = e.toString();
            Log.d("error", sTemp);
            e.printStackTrace();
        }
        catch (Exception e) {
            String sTemp = e.toString();
            Log.d("error", sTemp);
            e.printStackTrace();
        }

        try {
            OutputStream myOutputStream = socket.getOutputStream();
            Writer myWriter = new OutputStreamWriter(myOutputStream);
            PrintWriter out = new PrintWriter(myWriter, false);

            out.print(toSend);
            out.close();
            Log.d("Socket", "Sent");

//                out = null;
            socket = null;
        }
        catch (UnknownHostException e) {
            String sTemp = e.toString();
            Log.d("error", sTemp);
            e.printStackTrace();
        }
        catch (IOException e) {
            String sTemp = e.toString();
            Log.d("error", sTemp);
            e.printStackTrace();
        }
        catch (Exception e) {
            String sTemp = e.toString();
            Log.d("error", sTemp);
            e.printStackTrace();
        }

        return null;
    }

    // Load preferences
    private void loadPref(Context myCTX){
//        SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(myCTX);
        sPrefServerIP = mySharedPreferences.getString("pref_et_serverIP", "");
        sPrefServerPort = mySharedPreferences.getString("pref_et_serverPort", "");

//        String prefList = mySharedPreferences.getString("pref_l_fadeSpeed", "no selection");
//        new SocketOperation(prefList).execute("");
    }

}
