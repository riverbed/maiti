/*
===================
Copyright (c) 2013 Riverbed Technology, Inc.

MAITI is licensed under the terms and conditions of the MIT License as set forth in LICENSE.TXT, which accompanies the software.  MAITI is distributed “AS IS” as set forth in the MIT License.
===================
*/
package com.riverbed.mobile.android.apmlib.datagenerator;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import com.riverbed.mobile.android.apmlib.UserExperience;
import com.riverbed.mobile.android.apmlib.PermissionsException;
import com.riverbed.mobile.android.apmlib.objects.SettingsObject;

import android.app.Application;

public class GeneratorApp extends Application {

	private UserExperience apm = null;
	
	@Override
	public void onCreate() {
		super.onCreate();

	}

    public void updateMaiti()
    {
        if (PrefsActivity.isPreferencesValid(this))
        {

            SettingsObject settings = new SettingsObject();

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

            int port = Integer.valueOf(PrefsActivity.getPort(sharedPref));

            settings.setDataCollector(PrefsActivity.getHostname(sharedPref),
                    PrefsActivity.getUseSSL(sharedPref), port);

            settings.setEnabled(PrefsActivity.getEnabled(sharedPref));

            settings.setRecordConn(PrefsActivity.getRecordConn(sharedPref));
            settings.setRecordMemory(PrefsActivity.getRecordMemory(sharedPref));
            settings.setRecordSerial(PrefsActivity.getRecordSerial(sharedPref));

            try {

                apm = new UserExperience(PrefsActivity.getCustomerId(sharedPref), PrefsActivity.getAppId(sharedPref),
                        this.getApplicationContext(), settings);
            } catch (PermissionsException e) {

                e.printStackTrace();
                throw new RuntimeException();
            }
        }
        else
        {
            Log.w(this.getClass().getSimpleName(), "Unable to update the MAITI library due to bad preferences.");
        }
    }
	
	public UserExperience getMaiti()
	{
        if (apm == null)
        {
            updateMaiti();
        }
		return apm;
	}
}
