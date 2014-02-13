/*
===================
Copyright (c) 2013 Riverbed Technology, Inc.

MAITI is licensed under the terms and conditions of the MIT License as set forth in LICENSE.TXT, which accompanies the software.  MAITI is distributed “AS IS” as set forth in the MIT License.
===================
*/
package com.riverbed.mobile.android.apmlib;

import java.util.Vector;

import android.util.Log;

import com.riverbed.mobile.android.apmlib.objects.TransactionId;

class MaitiTransaction
{
	enum TransactionType
	{
		IntervalTransaction { String getName() { return "interval"; } },
		NotificationTransaction { String getName() { return "notification"; } }
		;
		abstract String getName();
	}
	
	private static final int MAX_TAG_LENGTH = 128;
	private static final int MAX_DATA_LENGTH = 16384;

    private String customerId;
    private String appId;

	private TransactionType type;
	private String networkType;
	private String devName;
	private String devId;
	private long freeMemory;
	private long totalMemory;
	private TransactionId transactionId;
	private String sessionId;
	private long eueMonVersion;
	private String agentType;
	private String transactionName;
	private String osVersion;
	private String errorMessage = null;
	private String packageId;
	private String codeVersion;
	
	private UserData userTag1;
	private UserData userTag2;
	private UserData userTag3;
	private UserData userData;

	//private long intervalTransactionStart;
	//private long notificationStart;
    private long timestampStartTime;
	private Vector<MaitiEvent> vec_AppEventDO;

	private long intervalDuration = 0;
	private TransactionId transactionParentId;
	
	void setParentID(TransactionId parentID)
	{
		transactionParentId = parentID;
	}
	
	TransactionId getParentID()
	{
		if(transactionParentId != null && transactionParentId.length() > 0)
		return transactionParentId;
		else return null;
	}
	
	void setTransactionType(TransactionType type)
	{
		this.type = type;
	}
	TransactionType getTransactionType()
	{
		return this.type;
	}
	
	String getPackageId()
	{
		return packageId;
	}
	void setPackageId(String packageId)
	{
		this.packageId = packageId;
	}

    String getCustomerId() {
        return customerId;
    }

    void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    String getAppId() {
        return appId;
    }

    void setAppId(String appId) {
        this.appId = appId;
    }

    void setIntervalDuration(long intervalDuration)
	{
		this.intervalDuration = intervalDuration;
	}
	
	long get_AppPerfIntervalDuration()
	{
		return intervalDuration;
	}

	
	void setAppPerfEvents(MaitiEvent appEventDO)
	{
		if(vec_AppEventDO == null)
		{
			vec_AppEventDO = new Vector<MaitiEvent>();
		}
		vec_AppEventDO.add(appEventDO);
	}
	
	Vector<MaitiEvent> getAppPerfEvents()
	{
		if(vec_AppEventDO != null && vec_AppEventDO.size() > 0)
			return vec_AppEventDO;
		return null;
	}
	

	void setTimestampStartTime(long epochMs)
	{
        timestampStartTime = epochMs;
	}

	long getTimestampStartTime()
	{
		return timestampStartTime;
	}

	
	long getTimestampEndTime()
	{
		return timestampStartTime + intervalDuration;
	}
	
	void setUserTag1(String usrtg)
	{
		userTag1 = new UserData(usrtg, MAX_TAG_LENGTH);
	}
	
	String getUserTag1()
	{
		if(userTag1 != null && userTag1.getString() != null && userTag1.getString().length() > 0)
			return userTag1.getString();
		return null;
	}
	
	void setUserTag2(String usrtg)
	{
		userTag2 = new UserData(usrtg, MAX_TAG_LENGTH);
	}
	
	String getUserTag2()
	{
		if(userTag2 != null && userTag2.getString() != null && userTag2.getString().length() > 0)
			return userTag2.getString();
		return null;
	}
	
	void setUserTag3(String usrtg)
	{
		userTag3 = new UserData(usrtg, MAX_TAG_LENGTH);
	}
	
	String getUserTag3()
	{
		if(userTag3 != null && userTag3.getString() != null && userTag3.getString().length() > 0)
			return userTag3.getString();
		return null;
	}
	
	void setUserData(String usrtg)
	{
		userData = new UserData(usrtg, MAX_DATA_LENGTH);
	}
	
	String getUserData()
	{
		if(userData != null && userData.getString() != null && userData.getString().length() > 0)
			return userData.getString();
		return null;
	}
	
	void setErrorMessage(String errorMessage)
	{
		this.errorMessage = errorMessage;
	}
	
	String getErrorMessage()
	{
		return errorMessage;
	}
	
	boolean hasErrorMessage()
	{
		return (errorMessage != null);
	}
	
	void setTransactionName(String transName)
	{
		transactionName = transName;
	}
	
	String getTransactionName()
	{
		if(transactionName != null && transactionName.length() > 0)
		return transactionName;
		else return null;
	}
	
	void setVersion(long version)
	{
		eueMonVersion = version;
	}
	
	long getVersion()
	{
		return eueMonVersion;
	}
	
	void setAgentType(String agentType)
	{
		this.agentType = agentType;
	}
	
	String getAgentType()
	{
		if(agentType != null && agentType.length() > 0)
		return agentType;
		else return null;
	}
	
	void setSessionID(String sessionID)
	{
		sessionId = sessionID;
	}
	
	String getSessionID()
	{
		if(sessionId != null && sessionId.length() > 0)
		return sessionId;
		else return null;
	}
	
	void setTransactionID(TransactionId monitoringID)
	{
		transactionId = monitoringID;
	}
	
	TransactionId getTransactionId()
	{
		if(transactionId != null && transactionId.length() > 0)
		return transactionId;
		else return null;
	}
	
	void setTotalMemory(long totalMem)
	{
		totalMemory = totalMem;
	}
	
	long getTotalMemory()
	{
		return totalMemory;
	}
	
	void setFreeMemory(long availMem)
	{
		freeMemory = availMem;
	}
	
	long getFreeMemory()
	{
		return freeMemory;
	}
	
	void setDeviceID(String devID)
	{
		devId = devID;
	}
	
	String getDeviceID()
	{
		if(devId != null && devId.length() > 0)
		return devId;
		else return null;
	}
	
	void setNetworkType(String netType)
	{
		networkType = netType;
	}
	
	String getNetworkType()
	{
		if(networkType != null && networkType.length() > 0)
		return networkType;
		else return null;
	}
	
	void setDevName(String devName)
	{
		this.devName = devName;
	}
	
	String getOsVersion()
	{
		return osVersion;
	}
	void setOsVersion(String osVersion)
	{
		this.osVersion = osVersion;
	}
	
	void setCodeVersion(String codeVersion)
	{
		this.codeVersion = codeVersion;
	}
	
	String getCodeVersion()
	{
		return codeVersion;
	}
	
	String getDevName()
	{
		return devName;
	}
	
	private class UserData
	{
		private String userData = null;
		
		public UserData(String data, int maxLength)
		{
			if(data != null && !data.equalsIgnoreCase("") && data.length() > 0) {
				
				if(data.length() > maxLength)
				{
					this.userData = data.substring(0, maxLength);
					Log.d(UserExperience.class.getSimpleName(), "Userdata exceeds " + maxLength + " characters.  Output will be truncated.");
				}
				else
				{
					this.userData = data;
				}
			}

		}
		
		public String getString()
		{
			return userData;
		}
	}
}
