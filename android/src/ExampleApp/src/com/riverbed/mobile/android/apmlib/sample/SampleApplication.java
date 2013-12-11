/*
===================
Copyright (c) 2013 Riverbed Technology, Inc.

MAITI is licensed under the terms and conditions of the MIT License as set forth in LICENSE.TXT, which accompanies the software.  MAITI is distributed “AS IS” as set forth in the MIT License.
===================
*/
package com.riverbed.mobile.android.apmlib.sample;

import com.riverbed.mobile.android.apmlib.UserExperience;
import com.riverbed.mobile.android.apmlib.PermissionsException;
import com.riverbed.mobile.android.apmlib.objects.SettingsObject;

import android.app.Application;

public class SampleApplication extends Application {

	private UserExperience apm = null;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		if (apm == null)
		{
			SettingsObject settings = new SettingsObject();
			settings.setDataCollector("mobile.collect-opnet.com", false, 80);
			
			try {
				
				apm = new UserExperience("YourCustomerID", "000000", this.getApplicationContext(), settings);
			} catch (PermissionsException e) {

				e.printStackTrace();
				throw new RuntimeException();
			}
		}
	}

	
	public UserExperience getAppPerformanceMonitor()
	{
		return apm;
	}
}
