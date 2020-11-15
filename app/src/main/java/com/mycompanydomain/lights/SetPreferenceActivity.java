package com.mycompanydomain.lights;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.mycompanydomain.lights.PrefsFragment;

public class SetPreferenceActivity extends Activity {
//public class SetPreferenceActivity extends ActionBarActivity {
//public class SetPreferenceActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		getFragmentManager().beginTransaction().replace(android.R.id.content,
                new PrefsFragment()).commit();
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_settings, menu);
//        return true;

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_settings, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_default) {
//        PreferenceManager.setDefaultValues(this, R.xml.preferences, true);
            SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            mySharedPreferences.edit().clear().apply();
            PreferenceManager.setDefaultValues(this, R.xml.preferences, true);

            String sPrefServerIP = mySharedPreferences.getString("pref_et_serverIP", "not initialised");
            String sPrefServerPort = mySharedPreferences.getString("pref_et_serverPort", "not initialised");

            String prefList = mySharedPreferences.getString("pref_l_fadeSpeed", "no selection");
//        sendOnSocket(prefList);

            getFragmentManager().beginTransaction().replace(android.R.id.content,
                    new PrefsFragment()).commit();

            return true;
        }

        if (id == R.id.action_save) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
