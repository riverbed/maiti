/*
===================
Copyright (c) 2013 Riverbed Technology, Inc.

MAITI is licensed under the terms and conditions of the MIT License as set forth in LICENSE.TXT, which accompanies the software.  MAITI is distributed “AS IS” as set forth in the MIT License.
===================
*/
package com.riverbed.mobile.android.apmlib;

import android.Manifest.permission;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.riverbed.mobile.android.apmlib.MaitiTransaction.TransactionType;
import com.riverbed.mobile.android.apmlib.objects.SettingsObject;
import com.riverbed.mobile.android.apmlib.objects.TransactionId;

import java.util.Vector;


public class UserExperience 
{
	private enum UserTags
	{
		USERTAG1,
		USERTAG2,
		USERTAG3,
		USERDATA,
		ERROR_MESSAGE
	}
	
	// Constants
	static final int MAX_UNFINISHED_TRANSACTIONS_IN_MEMORY = 3000;

    static final String PARENT_OFFSET_KEY = "_opParOfst";


	static final int APP_PERF_VERSION = 1;
	static final String APP_PERF_AGENT_TYPE = "droid";


	
	// Private variables
	private final String customerId;
	private final String applicationId;
	private final Context appPerformerContext;
	private ConnectivityManager mConnectivity = null;
	private TelephonyManager mTelephony = null;
	private final PackageManager pm;

    private DataHandler dataTransmitter;


	private MaitiTransaction staticTransactionData;
	private Vector<MaitiTransaction> unfinishedTransactions;
	

	//private CountDownTimer cdt_AppPerfBuffer;

	
	private final SettingsObject appSettings;


	public UserExperience(String customerId, String appId, Context caller_context) throws PermissionsException
	{
		this(customerId, appId, caller_context, null);
	}

	/**
	 * 
	 * @param caller_context calling activity context
	 * @param settings object of SettingsObject class to change the default options,
	 * use null if want default options
	 * @throws PermissionsException throws PermissionsException if permissions not found in manifest file.
	 * need permission.INTERNET, permission.ACCESS_NETWORK_STATE
	 */
	public UserExperience(String customerId, String appId, Context caller_context, final SettingsObject settings) throws PermissionsException
	{
        this.customerId = customerId;
        this.applicationId = appId;

        this.appPerformerContext = caller_context;

        this.pm = appPerformerContext.getPackageManager();


        this.unfinishedTransactions = new Vector<MaitiTransaction>();
        this.staticTransactionData = new MaitiTransaction();

        if (settings == null)
            this.appSettings = new SettingsObject();
        else
            this.appSettings = settings;


        this.dataTransmitter = new DataHandler(this.customerId, this.appSettings);

        if(appSettings.isRecordMemory())
        {
            staticTransactionData.setTotalMemory(getTotalMemory());
        }

        staticTransactionData.setSessionID(Utility.generate_Random(18));
        staticTransactionData.setVersion(APP_PERF_VERSION);
        staticTransactionData.setAgentType(APP_PERF_AGENT_TYPE);
        staticTransactionData.setOsVersion(android.os.Build.VERSION.RELEASE);
        staticTransactionData.setPackageId(this.appPerformerContext.getPackageName());

        String codeVersion = "";
        try {
            codeVersion = pm.getPackageInfo(this.appPerformerContext.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {		}

        staticTransactionData.setCodeVersion(codeVersion);

        if(appSettings.isEnabled() && appSettings.isRecordConn())
        {
            if(pm.checkPermission(permission.ACCESS_NETWORK_STATE, appPerformerContext.getPackageName())
                    != PackageManager.PERMISSION_GRANTED)
            {
                throw new PermissionsException("ApplicationPerformanceMonitor: ACCESS_NETWORK_STATE Permission missing.  You must either add this permission to your AndroidManifest.xml or disable connection recording in your runtime settings.");
            }
            else
            {
                mConnectivity = (ConnectivityManager)appPerformerContext.getSystemService(Context.CONNECTIVITY_SERVICE);
                mTelephony = (TelephonyManager)appPerformerContext.getSystemService(Context.TELEPHONY_SERVICE);
            }
        }
        if(appSettings.isEnabled() && appSettings.isRecordSerial())
        {
            staticTransactionData.setDeviceID(Secure.getString(caller_context.getContentResolver(), Settings.Secure.ANDROID_ID));
        }
        if (appSettings.isEnabled())
        {
            if (pm.checkPermission(permission.INTERNET, appPerformerContext.getPackageName()) != PackageManager.PERMISSION_GRANTED)
            {
                throw new PermissionsException("ApplicationPerformanceMonitor: INTERNET Permission missing.  You must add this permission to your AndroidManifest.xml.");
            }
        }
        staticTransactionData.setDevName(getDeviceName());

	}


    /**
     * Used to mock out the data handler for unit tests
     * @param handler
     */
    void setMockDataHandler(DataHandler handler)
    {
        this.dataTransmitter = handler;
    }

    /**
     * Used by unit tests to validate
     * @return
     */
    Vector<MaitiTransaction> getUnfinishedTransactions()
    {
        return unfinishedTransactions;
    }
	
	/**
	 * Will return connection type(WIFI, Mobile)
	 * @return connection type or "No network" if network not available
	 */
	private String detect_Net_Conn_Type()
	{
		// Skip if no connection, or background data disabled
		NetworkInfo info = mConnectivity.getActiveNetworkInfo();
		if (info == null || !mConnectivity.getBackgroundDataSetting()) 
		{
		    return "No Connection";
		}

		// Only update if WiFi or 3G is connected and not roaming
		int netType = info.getType();
		if (netType == ConnectivityManager.TYPE_WIFI) {
		    return "wifi";
		} 
		else if (netType == ConnectivityManager.TYPE_MOBILE)
		{
			int[] HIGH_SPEED_CONNECTION_CONSTANTS = {13, 15}; // TelephonyManager.NETWORK_TYPE_LTE, NETWORK_TYPE_HSPAP
			int networkType = mTelephony.getNetworkType();
			
			for (int hscc : HIGH_SPEED_CONNECTION_CONSTANTS)
			{
				if (networkType == hscc)
					return "4g";
			}
			
			return "3g";
		}
		return "unknown";
	}

	/**
	 * 
	 * @return available heap memory in bytes
	 */
	private long getAvailableMemory()
	{
        long maxHeapSize = Runtime.getRuntime().maxMemory();
        long curHeapSize = Runtime.getRuntime().totalMemory();
        long memFree = Runtime.getRuntime().freeMemory();

        // Return unallocated heap + free memory available on allocated heap
        return (maxHeapSize - curHeapSize) + memFree;
		//return Debug.getNativeHeapFreeSize();
	}
	/**
	 * 
	 * @return total heap memory in bytes
	 */
	private long getTotalMemory()
	{
        return Runtime.getRuntime().maxMemory();
		//return Debug.getNativeHeapSize();
	}
	
	private String getDeviceName() 
	{
		String manufacturer = Build.MANUFACTURER;
		String model = Build.MODEL;
		if (model.startsWith(manufacturer)) 
		{
			return capitalize(model);
		}
		else 
		{
			return capitalize(manufacturer) + " " + model;
		}
	}


	private String capitalize(String s){
		if (s == null || s.length() == 0){
			return "";
		}
		char first = s.charAt(0);
		if (Character.isUpperCase(first)){
			return s;
		} 
		else{
			return Character.toUpperCase(first) + s.substring(1);
		}
	}
	
	static long getCurrentTime()
	{
		return System.currentTimeMillis();
	}
	
	private void assignCommonData(MaitiTransaction appPerfDO)
	{

		
		if(appSettings.isRecordSerial())
		{
			appPerfDO.setDeviceID(staticTransactionData.getDeviceID());
		}

        appPerfDO.setCustomerId(customerId);
        appPerfDO.setAppId(applicationId);
	    appPerfDO.setDevName(staticTransactionData.getDevName());
	    appPerfDO.setOsVersion(staticTransactionData.getOsVersion());
	    appPerfDO.setPackageId(this.appPerformerContext.getPackageName());
	    
	    if(appSettings.isRecordConn())
	    {
	    	appPerfDO.setNetworkType(detect_Net_Conn_Type());
	    }
	    
	    if(appSettings.isRecordMemory())
	    {
		    appPerfDO.setTotalMemory(staticTransactionData.getTotalMemory());
		    appPerfDO.setFreeMemory(getAvailableMemory());
	    }
		appPerfDO.setVersion(staticTransactionData.getVersion());
		appPerfDO.setAgentType(staticTransactionData.getAgentType());
		appPerfDO.setCodeVersion(staticTransactionData.getCodeVersion());
		appPerfDO.setSessionID(staticTransactionData.getSessionID());
		
	}
	
	public void setEnabled(boolean enabled)
	{
		appSettings.setEnabled(enabled);
	}
	
	/**
	 * Starts Interval Transaction
	 * @param transactionName The name of the Transaction that will be visible when analyzing performance data
	 * @return Transaction ID
	 */
	public TransactionId transactionStart(String transactionName)
	{
		return this.transactionStart(transactionName, null);
	}

	/**
	 * Starts Interval Transaction
	 * @param transactionName The name of the Transaction that will be visible when analyzing performance data
	 * @param parentTransactionId The ID of a parent transaction
	 * @return Transaction ID
	 */
	public TransactionId transactionStart(String transactionName, TransactionId parentTransactionId)
	{
		if(appSettings.isEnabled())
		{

            long now = getCurrentTime();


			MaitiTransaction appPerfDO = new MaitiTransaction();
			appPerfDO.setTransactionID(new TransactionId(Utility.generate_Random(20)));
			appPerfDO.setTransactionType(TransactionType.IntervalTransaction);
			
			if(parentTransactionId != null && parentTransactionId.length() > 0)
			{
				appPerfDO.setParentID(parentTransactionId);
			}
			appPerfDO.setTransactionName(transactionName);
			appPerfDO.setTimestampStartTime(now);

			appPerfDO.setUserTag1(null);
			appPerfDO.setUserTag2(null);
			appPerfDO.setUserTag3(null);
			appPerfDO.setUserData(null);

            // Since buffers can be sent independantly (and their timings will be variable based on network latency/bw)
            // we must set an offset from the parent so that the presentation of these events is always accurate
            MaitiTransaction parentDO = findAppById(parentTransactionId);
            if (parentDO != null)
            {
                long parentOffsetTime = now - parentDO.getTimestampStartTime();
                MaitiEvent events = new MaitiEvent();
                events.set_AppPerfEventName(PARENT_OFFSET_KEY);
                events.set_AppPerfEventDuration(parentOffsetTime);
                appPerfDO.setAppPerfEvents(events);
            }

			assignCommonData(appPerfDO);

			if (unfinishedTransactions.size() >= MAX_UNFINISHED_TRANSACTIONS_IN_MEMORY)
            {
                Log.w("MAITI", "Exceeded maximum number of incomplete transactions (" + MAX_UNFINISHED_TRANSACTIONS_IN_MEMORY + ")");
				unfinishedTransactions.remove(0);
            }
			
			unfinishedTransactions.add(appPerfDO);
			
			return appPerfDO.getTransactionId();
		}
		else return null;
	}
	
	
	/**
	 * 
	 * @param eventName Name of the event to start
	 * @param transactionId Transaction ID to which event is related
	 */
	public void setTransactionEvent(String eventName, TransactionId transactionId)
	{
		if(appSettings.isEnabled())
		{
			if(transactionId != null && transactionId.length() > 0 )
			{
				for(int i = 0; i < unfinishedTransactions.size(); i++)
				{
					if(unfinishedTransactions.get(i).getTransactionId().equals(transactionId))
					{
                        MaitiEvent maitiEvent = new MaitiEvent();
                        maitiEvent.set_AppPerfEventName(eventName);
                        maitiEvent.set_AppPerfEventDuration(
                                getCurrentTime() -
                                        unfinishedTransactions.get(i).getTimestampStartTime()
                        );
						unfinishedTransactions.get(i).setAppPerfEvents(maitiEvent);
						break;
					}
				}
			}
			else {
				//throw new Exception("Please check ParentID");
				Log.d(this.getClass().getSimpleName(), "Invalid transactionId for " + eventName + ".  Please check the transaction ID.");
			}
		}
	}

    /**
     * Notification Occurs
     * @param transactionName the name of this transaction that will be visible in the Web UI
     */
    public void notification(String transactionName) {
        notification(transactionName, null, null, null, null, null);
    }

	/**
	 * Notification Occurs
	 * @param transactionName the name of this transaction that will be visible in the Web UI
	 * @param userTag A user-defined tag that is searchable in the web UI
	 */
	public void notification(String transactionName, String userTag) {
		notification(transactionName, null, userTag, null, null, null);
	}

	public void notification(String transactionName, String error, String userTag1, 
			String userTag2, String userTag3, String userData) {

        if (appSettings.isEnabled() == false)
            return;
        
        long now = getCurrentTime();

        MaitiTransaction appPerfDO = new MaitiTransaction();
        appPerfDO.setTransactionID(new TransactionId(Utility.generate_Random(20)));
        appPerfDO.setTransactionType(TransactionType.NotificationTransaction);
        
		appPerfDO.setTimestampStartTime(now);

        appPerfDO.setTransactionName(transactionName);

        if (userTag1 != null && userTag1.equals("") == false)
            appPerfDO.setUserTag1(userTag1);
        if (userTag2 != null && userTag2.equals("") == false)
            appPerfDO.setUserTag2(userTag2);
        if (userTag3 != null && userTag3.equals("") == false)
            appPerfDO.setUserTag3(userTag3);
        if (userData != null && userData.equals("") == false)
            appPerfDO.setUserData(userData);
        if (error != null && error.equals("") == false)
            appPerfDO.setErrorMessage(error);

        assignCommonData(appPerfDO);

        unfinishedTransactions.add(appPerfDO);

        startBuffering(appPerfDO);

	}
	
	private void setTransactionUserTagData(UserTags tag, TransactionId transactionId, String userData)
	{

        if (appSettings.isEnabled() == false)
            return;

		MaitiTransaction appDO = findAppById(transactionId);

		if(appDO != null){
			switch (tag)
			{
				case USERTAG1:	appDO.setUserTag1(userData); break;
				case USERTAG2:	appDO.setUserTag2(userData); break;
				case USERTAG3:	appDO.setUserTag3(userData); break;
				case USERDATA:	appDO.setUserData(userData); break;
				case ERROR_MESSAGE: appDO.setErrorMessage(userData); break;
			}
		}
		else{
			Log.d(this.getClass().getSimpleName(), "Invalid transactionId for call to tagTransaction.");
		}
	}
	
	public void setTransactionUserData(TransactionId transactionId, String userData)
	{
		setTransactionUserTagData(UserTags.USERDATA, transactionId, userData);
	}
	public void setTransactionUserTag1(TransactionId transactionId, String userData)
	{
		setTransactionUserTagData(UserTags.USERTAG1, transactionId, userData);
	}
	public void setTransactionUserTag2(TransactionId transactionId, String userData)
	{
		setTransactionUserTagData(UserTags.USERTAG2, transactionId, userData);
	}
	public void setTransactionUserTag3(TransactionId transactionId, String userData)
	{
		setTransactionUserTagData(UserTags.USERTAG3, transactionId, userData);
	}
	public void setTransactionError(TransactionId transactionId, String errorMessage)
	{
		setTransactionUserTagData(UserTags.ERROR_MESSAGE, transactionId, errorMessage);
	}
	
	/**
	 * 
	 * @param transactionId transaction id given at the time of transactionStart()
	 */
	public void transactionEnd(TransactionId transactionId) {

        if (appSettings.isEnabled() == false)
            return;

        long curTime = getCurrentTime();
        MaitiTransaction appDO = findAppAndRemove(transactionId);

        if(appDO != null){

            appDO.setIntervalDuration(curTime - appDO.getTimestampStartTime());
            startBuffering(appDO);


        }
        else{
            Log.d(this.getClass().getSimpleName(), "Invalid transactionId for call to transactionEnd.");
        }

	}

    synchronized private MaitiTransaction findAppAndRemove(TransactionId transactionId)
    {
        int appIndex = findAppIndex(transactionId);
        if (appIndex == -1)
            return null;

        MaitiTransaction transaction = unfinishedTransactions.get(appIndex);
        unfinishedTransactions.remove(appIndex);

        return transaction;
    }

	private MaitiTransaction findAppById(TransactionId transactionId)
	{
        int appIndex = findAppIndex(transactionId);
        if (appIndex == -1)
            return null;

        return unfinishedTransactions.get(appIndex);
	}

    private int findAppIndex(TransactionId transactionId)
    {
        if(transactionId == null || transactionId.length() == 0)
            return -1;

        for(int i = 0; i < unfinishedTransactions.size(); i++)
        {
            if(unfinishedTransactions.get(i).getTransactionId().equals(transactionId))
            {
                return i;
            }
        }

        return -1;
    }
	
	protected void startBuffering(MaitiTransaction bufferingObject)
	{
        dataTransmitter.push(bufferingObject);
	}
	


}