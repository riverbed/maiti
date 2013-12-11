/*
===================
Copyright (c) 2013 Riverbed Technology, Inc.

MAITI is licensed under the terms and conditions of the MIT License as set forth in LICENSE.TXT, which accompanies the software.  MAITI is distributed “AS IS” as set forth in the MIT License.
===================
*/
package com.riverbed.mobile.android.apmlib;

class MaitiEvent {

	private String appPerfEventName;
	private String appPerfEventParent;
	private long appPerfEventDur;
	
	void set_AppPerfEventName(String eventName)
	{
		appPerfEventName = eventName;
	}
	
	String get_AppPerfEventName()
	{
		return appPerfEventName;
	}
	
	void set_AppPerfEventParent(String eventParent)
	{
		appPerfEventParent = eventParent;
	}
	
	String get_AppPerfEventParent()
	{
		return appPerfEventParent;
	}
	
	void set_AppPerfEventDuration(long eventDuration)
	{
		appPerfEventDur = eventDuration;
	}
	
	long get_AppPerfEventDuration()
	{
		return appPerfEventDur;
	}
}
