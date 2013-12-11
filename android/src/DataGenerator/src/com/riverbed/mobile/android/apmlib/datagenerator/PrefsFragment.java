package com.riverbed.mobile.android.apmlib.datagenerator;

import android.os.Bundle;
import android.preference.PreferenceFragment;

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
public class PrefsFragment extends PreferenceFragment
{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
    }
}
