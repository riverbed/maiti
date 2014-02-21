package com.maiti.testapp;

import android.app.Application;
import android.util.Log;

import com.riverbed.mobile.android.apmlib.PermissionsException;
import com.riverbed.mobile.android.apmlib.UserExperience;
import com.riverbed.mobile.android.apmlib.objects.SettingsObject;

/**
 * Created by Lyubov on 11.02.14.
 */
public class MAITITestApplication extends Application {

    private static final String TAG = MAITITestApplication.class.getName();
    public static final String MAITI_CUSTOMER_ID = "CA62A5DEE94918D0";
    public static final String MAITI_APP_ID = "522883";


    private UserExperience userExperience = null;
    private SettingsObject settingsObject = null;

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            settingsObject = new SettingsObject();
            userExperience = new UserExperience(MAITI_CUSTOMER_ID, MAITI_APP_ID, this, settingsObject);
        } catch (PermissionsException e) {
            Log.e(TAG, "Error initializing MAITI Lib", e);
        }

    }


    public UserExperience getUserExperience() {
        return userExperience;
    }

    public SettingsObject getMAITILibSettings() {
        return settingsObject;
    }

    public static UserExperience getUserExperience(
            final Application application) {
        return ((MAITITestApplication) application).getUserExperience();
    }

    public static SettingsObject getMAITILibSettings(
            final Application application) {
        return ((MAITITestApplication) application).getMAITILibSettings();
    }
}
