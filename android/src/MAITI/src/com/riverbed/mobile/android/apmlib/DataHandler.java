package com.riverbed.mobile.android.apmlib;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.riverbed.mobile.android.apmlib.objects.SettingsObject;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

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
class DataHandler extends Handler {
    protected static final long BUFFER_TOTAL_TIME_MS = 5000;


    static final String KEY_CUSTOMER_ID = "cust_id";
    static final String KEY_APP_ID = "app_id";
    static final String KEY_ID = "id";
    static final String TRANS_TYPE_ID = "type";
    static final String KEY_PARENTID = "parent_id";
    static final String KEY_PACKAGEID = "pkg_id";
    static final String KEY_ERROR = "error";
    static final String KEY_VERSION = "ver";
    static final String KEY_AGENTTYPE = "agent_type";
    static final String KEY_NAME = "name";
    static final String KEY_SERIALNO = "ser_num";
    static final String KEY_MEMFREE = "mem_free";
    static final String KEY_MEMTOTAL = "mem_total";
    static final String KEY_CONNECTION = "conn";
    static final String KEY_MODEL = "model";
    static final String KEY_OS = "os";
    static final String KEY_SESSIONID = "sess_id";
    static final String KEY_USERTAG1 = "utag1";
    static final String KEY_USERTAG2 = "utag2";
    static final String KEY_USERTAG3 = "utag3";
    static final String KEY_USERDATA = "udata";
    static final String KEY_DURATION = "dur";
    static final String KEY_CODEVERSION = "code_ver";
    static final String KEY_BUFFEROFFSET = "offset_ms";
    static final String KEY_EVENTS = "events";

    protected Vector<MaitiTransaction> transactionBuffer = new Vector<MaitiTransaction>();;

    protected JSONArray jsonArrayPost;
    protected JSONObject jsonObjPost;
    protected JSONObject jsonObjEventPost;
    protected final SettingsObject settings;
    protected final String customerID;

    protected Poster activePoster = null;

    DataHandler(String customerID, SettingsObject settings)
    {
        super();

        this.customerID = customerID;
        this.settings = settings;
        // Allow this thread to exit when the app exits

    }


    synchronized void push(MaitiTransaction transaction)
    {
        transactionBuffer.add(transaction);

        // Start the thread only if we're sure we're going to need it
        if (activePoster == null)
        {
            activePoster = new Poster();
            postDelayed(activePoster, BUFFER_TOTAL_TIME_MS);
        }

    }

    // Poster is activated at most once every 5 seconds.
    // Poster creates the JSON array from current data in memory (syncrhonized)
    // And then sends it over the network (not synchronized)
    protected class Poster implements Runnable
    {
        @Override
        public void run() {
            activePoster = null;

            // Kick off a new thread to wrap up and post the data
            Thread t = new Thread(){
                public void run()
                {
                    Looper.prepare(); //For Preparing Message Pool for the child Thread

                    // Synchronized
                    JSONArray data = getPostBuffer();

                    // Not synchronized (can upload batches async)
                    sendJson(data);

                    Looper.loop(); //Loop in the message queue
                }
            };

            t.start();
        }
    }

    synchronized protected JSONArray getPostBuffer()
    {

        long now = UserExperience.getCurrentTime();

        MaitiTransaction transaction;
        jsonArrayPost = new JSONArray();
        for(int PostBufferSize = 0; PostBufferSize < transactionBuffer.size(); PostBufferSize++)
        {
            transaction = transactionBuffer.get(PostBufferSize);
            jsonObjPost = new JSONObject();
            try {
                jsonObjPost.put(KEY_CUSTOMER_ID, transaction.getCustomerId());
                jsonObjPost.put(KEY_APP_ID, transaction.getAppId());
                jsonObjPost.putOpt(KEY_ID, transaction.getTransactionId());
                jsonObjPost.putOpt(TRANS_TYPE_ID, transaction.getTransactionType().getName());
                jsonObjPost.putOpt(KEY_PACKAGEID, transaction.getPackageId());
                jsonObjPost.putOpt(KEY_VERSION, transaction.getVersion());
                jsonObjPost.putOpt(KEY_AGENTTYPE, transaction.getAgentType());
                jsonObjPost.putOpt(KEY_NAME, transaction.getTransactionName());
                jsonObjPost.putOpt(KEY_SERIALNO, transaction.getDeviceID());
                jsonObjPost.putOpt(KEY_MEMFREE, transaction.getFreeMemory());
                jsonObjPost.putOpt(KEY_MEMTOTAL, transaction.getTotalMemory());
                jsonObjPost.putOpt(KEY_CONNECTION, transaction.getNetworkType());
                jsonObjPost.putOpt(KEY_MODEL, transaction.getDevName());
                jsonObjPost.putOpt(KEY_OS, transaction.getOsVersion() );
                jsonObjPost.putOpt(KEY_SESSIONID, transaction.getSessionID());
                jsonObjPost.putOpt(KEY_USERTAG1, transaction.getUserTag1());
                jsonObjPost.putOpt(KEY_USERTAG2, transaction.getUserTag2());
                jsonObjPost.putOpt(KEY_USERTAG3, transaction.getUserTag3());
                jsonObjPost.putOpt(KEY_USERDATA, transaction.getUserData());
                jsonObjPost.putOpt(KEY_CODEVERSION, transaction.getCodeVersion());
                jsonObjPost.putOpt(KEY_BUFFEROFFSET, now - transaction.getTimestampEndTime() );

                if (transaction.hasErrorMessage())
                    jsonObjPost.putOpt(KEY_ERROR, transaction.getErrorMessage());

                // Add properties that are unique to the interval
                if (transaction.getTransactionType() == MaitiTransaction.TransactionType.IntervalTransaction)
                {
                    jsonObjPost.putOpt(KEY_DURATION, transaction.get_AppPerfIntervalDuration());
                    jsonObjPost.putOpt(KEY_PARENTID, transaction.getParentID());

                    // Add events if they exist
                    if(transaction.getAppPerfEvents() != null && transaction.getAppPerfEvents().size() > 0)
                    {
                        jsonObjEventPost = new JSONObject();
                        for(int eventSize = 0; eventSize < transaction.getAppPerfEvents().size(); eventSize++)
                        {
                            MaitiEvent localEventDO = new MaitiEvent();
                            localEventDO = transaction.getAppPerfEvents().get(eventSize);
                            jsonObjEventPost.putOpt(localEventDO.get_AppPerfEventName(),
                                    localEventDO.get_AppPerfEventDuration());
                        }
                        if(jsonObjEventPost != null)
                            jsonObjPost.putOpt(KEY_EVENTS, jsonObjEventPost);
                    }
                }




                jsonArrayPost.put(jsonObjPost);
            } catch (JSONException e) {

                e.printStackTrace();
            }
        }
        transactionBuffer.clear();

        return jsonArrayPost;

    }


    protected void sendJson(final JSONArray jsonArrayPosting) {


        HttpClient client = new DefaultHttpClient();

        HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
        HttpResponse response;
        try{
            Log.d(this.getClass().getSimpleName(), "is=>" + settings.getDataCollectorFullURL());
            HttpPost post = new HttpPost(settings.getDataCollectorFullURL());

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("eueMon", "mobile"));
            nameValuePairs.add(new BasicNameValuePair("ver", String.valueOf( UserExperience.APP_PERF_VERSION )));
            nameValuePairs.add(new BasicNameValuePair("jsid", customerID ));

            nameValuePairs.add(new BasicNameValuePair("payload", jsonArrayPosting.toString()));
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            response = client.execute(post);
            /*Checking response */
            if(response!=null)
            {
                response.getEntity();
               // if(respEntity != null)
               //     strResp = EntityUtils.toString(respEntity);
                Log.d(this.getClass().getSimpleName(), "MAITI json Post: "+jsonArrayPosting.toString());
            }

        }
        catch(Exception e){
            //strResp = "Exception while posting";
            e.printStackTrace();
        }

    }
}
