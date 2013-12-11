/*
===================
Copyright (c) 2013 Riverbed Technology, Inc.

MAITI is licensed under the terms and conditions of the MIT License as set forth in LICENSE.TXT, which accompanies the software.  MAITI is distributed “AS IS” as set forth in the MIT License.
===================
*/
package com.riverbed.mobile.android.apmlib.objects;

public class SettingsObject {
	
	private boolean enabled;
	private String dataCollectorHost;
	private int dataCollectorPort;

	private boolean useHttps;
	private boolean recordSerial;
	private boolean recordMemory;
	private boolean recordConn;

	private static final String strHTTP = "http://";
	private static final String strHTTPS = "https://";
	
	private static final String URL_PATH = "/beacon.gif";
	

	private static final boolean DEFAULT_IS_ENABLED = true;
	public static final String DEFAULT_HOST = "mobile.collect-opnet.com";
	private static final boolean DEFAULT_USE_HTTPS = false;
	private static final int DEFAULT_PORT = 80;
    private static final int DEFAULT_PORT_SSL = 443;
	private static final boolean DEFAULT_RECORD_SERIAL = true;
	private static final boolean DEFAULT_RECORD_MEMORY = true;
	private static final boolean DEFAULT_RECORD_CONNECTION = true;
	
	public SettingsObject()
	{
		enabled = DEFAULT_IS_ENABLED;
		dataCollectorHost = DEFAULT_HOST;
		dataCollectorPort = DEFAULT_PORT;
		useHttps = DEFAULT_USE_HTTPS;
		recordSerial = DEFAULT_RECORD_SERIAL;
		recordMemory = DEFAULT_RECORD_MEMORY;
		recordConn = DEFAULT_RECORD_CONNECTION;
	}




	/**
	 * Determines whether the performance monitoring library is activated or not.   Default value is true
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setDataCollector(String hostname) {
		this.setDataCollector(hostname, DEFAULT_USE_HTTPS, DEFAULT_PORT);
	}
	
	public void setDataCollector(String hostname, boolean useHttps) {
        if (useHttps)
            this.setDataCollector(hostname, useHttps, DEFAULT_PORT_SSL);
        else
		    this.setDataCollector(hostname, useHttps, DEFAULT_PORT);
	}

	/**
	 * Sets the default hostname/URL for sending the Application Performance data.  
	 * 
	 * @param hostname  host or IP address of the collector.  Default is eue.collect-opnet.com
	 * @param useHttps  Use http or https to transmit the data.  Default is HTTP
	 * @param port		If your collector uses a different port, enter it here.
	 */
	public void setDataCollector(String hostname, boolean useHttps, int port) {
		this.dataCollectorHost = hostname;
		this.useHttps = useHttps;
		this.dataCollectorPort = port;
	}
	
	
	/**
	 * Determines whether to record a user's unique device serial number with the performance data.  
	 * Default is true.  
	 * This requires the permission READ_PHONE_STATE.
	 * @param recordSerial
	 */
	public void setRecordSerial(boolean recordSerial) {
		this.recordSerial = recordSerial;
	}
	
	/**
	 * Determines whether to record memory statistics (e.g., free memory, total memory).  
	 * Default is true.
	 * @param recordMemory
	 */
	public void setRecordMemory(boolean recordMemory) {
		this.recordMemory = recordMemory;
	}
	
	/**
	 * Determines whether to gather statistics about the user's connection type (e.g., WIFI, 3G, etc).  
	 * Default is true.  
	 * This requires the permission: ACCESS_NETWORK_STATE 
	 * @param recordConn
	 */
	public void setRecordConn(boolean recordConn) {
		this.recordConn = recordConn;
	}
	
	public String getDataCollectorFullURL()
	{
		if (this.isUseHttps())
			return strHTTPS + dataCollectorHost + ":" + dataCollectorPort + URL_PATH;
		else
			return strHTTP + dataCollectorHost + ":" + dataCollectorPort + URL_PATH;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
//	public String getDataCollector() {
//		return dataCollector;
//	}



	public boolean isUseHttps() {
		return useHttps;
	}



	public boolean isRecordSerial() {
		return recordSerial;
	}



	public boolean isRecordMemory() {
		return recordMemory;
	}



	public boolean isRecordConn() {
		return recordConn;
	}


	
}
