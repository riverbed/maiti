package com.riverbed.mobile.android.apmlib.datagenerator;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

/**
 * ***************************************
 * Copyright (c) 2013			*
 * by OPNET Technologies, Inc.     *
 * (A Delaware Corporation)		*
 * 7255 Woodmont Av., Suite 250  		*
 * Bethesda, MD 20814, U.S.A.       *
 * All Rights Reserved.		*
 * ***************************************
 */
public class PrefsActivity extends Activity implements View.OnClickListener {


    public static String PREF_HOST ="pref_key_host";
    public static String PREF_USE_SSL ="pref_key_use_ssl";
    public static String PREF_PORT ="pref_key_port";
    public static String PREF_CUSTOMER_ID ="pref_key_customer_id";
    public static String PREF_APP_ID ="pref_key_app_id";
    public static String PREF_ENABLED ="pref_key_enabled";
    public static String PREF_RECORD_CONN ="pref_key_record_conn";
    public static String PREF_RECORD_SERIAL ="pref_key_record_serial";
    public static String PREF_RECORD_MEMORY ="pref_key_record_memory";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.prefs);
        //getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment()).commit();

        findViewById(R.id.cmdSave).setOnClickListener(this);
        findViewById(R.id.cmdRestoreDefaults).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.cmdSave)
        {
            String prefsErrorMessage = preferencesValid(this);

            if (prefsErrorMessage.equals("") == false)
            {
                Toast.makeText(this, prefsErrorMessage, Toast.LENGTH_SHORT).show();
            }
            else
            {
                ((GeneratorApp) getApplication()).updateMaiti();
                this.finish();
            }

        }
        else if (view.getId() == R.id.cmdRestoreDefaults)
        {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            sharedPref.edit().clear().commit();
        }
    }

    public static boolean isPreferencesValid(Context context)
    {
        return preferencesValid(context).equals("");
    }

    // Returns empty string if valid.  Returns error message if invalid
    private static String preferencesValid(Context context)
    {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String host = sharedPref.getString(PREF_HOST, "");
        String appId = sharedPref.getString(PREF_APP_ID, "");
        String customerId = sharedPref.getString(PREF_CUSTOMER_ID, "");
        String port = sharedPref.getString(PREF_PORT, "");

        if (getHostname(sharedPref).equals(""))
            return "Missing Collector hostname";
        if (getAppId(sharedPref).equals(""))
            return "Missing App ID";
        if (getCustomerId(sharedPref).equals(""))
            return "Missing Customer ID";
        if (getPort(sharedPref).equals(""))
            return "Missing port";

        try
        {
            int portNum = Integer.valueOf(port);
        }
        catch (NumberFormatException e)
        {
            return "Invalid port number, must be numeric";
        }

        return "";
    }


    public static String getHostname(SharedPreferences sharedPref)
    {
        return sharedPref.getString(PREF_HOST, "mobile.collect-opnet.com");
    }
    public static String getPort(SharedPreferences sharedPref)
    {
        return sharedPref.getString(PREF_PORT, "80");
    }
    public static boolean getUseSSL(SharedPreferences sharedPref)
    {
        return sharedPref.getBoolean(PREF_USE_SSL, false);
    }
    public static String getCustomerId(SharedPreferences sharedPref)
    {
        return sharedPref.getString(PREF_CUSTOMER_ID, "");
    }
    public static String getAppId(SharedPreferences sharedPref)
    {
        return sharedPref.getString(PREF_APP_ID, "");
    }

    public static boolean getEnabled(SharedPreferences sharedPref)
    {
        return sharedPref.getBoolean(PREF_ENABLED, true);
    }
    public static boolean getRecordConn(SharedPreferences sharedPref)
    {
        return sharedPref.getBoolean(PREF_RECORD_CONN, false);
    }
    public static boolean getRecordMemory(SharedPreferences sharedPref)
    {
        return sharedPref.getBoolean(PREF_RECORD_MEMORY, false);
    }
    public static boolean getRecordSerial(SharedPreferences sharedPref)
    {
        return sharedPref.getBoolean(PREF_RECORD_SERIAL, false);
    }
}
