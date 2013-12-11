/*
===================
Copyright (c) 2013 Riverbed Technology, Inc.

MAITI is licensed under the terms and conditions of the MIT License as set forth in LICENSE.TXT, which accompanies the software.  MAITI is distributed “AS IS” as set forth in the MIT License.
===================
*/

package com.riverbed.mobile.android.apmlib;

import com.riverbed.mobile.android.apmlib.objects.SettingsObject;
import org.json.JSONArray;
import org.json.JSONObject;

import android.test.AndroidTestCase;

import com.riverbed.mobile.android.apmlib.objects.TransactionId;

import java.util.Vector;


public class BasicTest extends AndroidTestCase {

	public void testIntervalTransacionIsParsedProperly() throws Exception
	{
		final int TRANSACTION_MIN_DUR_MS = 200;
        StubDataHandler stubDataHandler = new StubDataHandler("TESTCust", new SettingsObject(), false);
        UserExperience apm = new UserExperience("TESTCust", "TestApp", this.getContext());
        apm.setMockDataHandler(stubDataHandler);
		
		TransactionId id = apm.transactionStart("Interval Test 1");

		final String userData = "{\"hello\":123}";
		apm.setTransactionUserData(id, userData);
		apm.setTransactionUserTag1(id, "tagtag1");
		apm.setTransactionUserTag2(id, "tagtag2");
		apm.setTransactionUserTag3(id, "tagtag3");
		apm.setTransactionError(id, "errerr");
		
		sleep(TRANSACTION_MIN_DUR_MS);
		apm.transactionEnd(id);
		
		
		// Check that the JsonArray is not null
        JSONArray json = stubDataHandler.getJsonArray();
		assertTrue(json != null);

		
		JSONObject transactionData = (JSONObject) json.get(0);
		
		assertTrue(transactionData.getString(DataHandler.KEY_ID).length() > 2);
		assertTrue(transactionData.getInt(DataHandler.KEY_DURATION) > TRANSACTION_MIN_DUR_MS);
		assertTrue(transactionData.getString(DataHandler.KEY_SESSIONID).length() > 2);
		assertTrue(transactionData.getString(DataHandler.KEY_NAME).equals("Interval Test 1"));
		assertTrue(transactionData.getString(DataHandler.KEY_AGENTTYPE).equals(UserExperience.APP_PERF_AGENT_TYPE));
		assertTrue(transactionData.getInt(DataHandler.KEY_VERSION) == UserExperience.APP_PERF_VERSION);

		assertTrue(transactionData.getString(DataHandler.KEY_ERROR).equals("errerr"));
		assertTrue(transactionData.getString(DataHandler.KEY_USERDATA).equals(userData));
		assertTrue(transactionData.getString(DataHandler.KEY_USERTAG1).equals("tagtag1"));
		assertTrue(transactionData.getString(DataHandler.KEY_USERTAG2).equals("tagtag2"));
		assertTrue(transactionData.getString(DataHandler.KEY_USERTAG3).equals("tagtag3"));
	}
	
	public void testNotificationTransactionIsParsedProperly() throws Exception
	{
        StubDataHandler stubDataHandler = new StubDataHandler("TESTCust", new SettingsObject(), false);
        UserExperience apm = new UserExperience("TESTCust", "TestApp", this.getContext());
        apm.setMockDataHandler(stubDataHandler);
			


		apm.notification("Notification Test 1", "tagtag1");


        // Check that the JsonArray is not null
        JSONArray json = stubDataHandler.getJsonArray();
        assertTrue(json != null);
		
		JSONObject transactionData = (JSONObject) json.get(0);
		
		assertTrue(transactionData.getString(DataHandler.KEY_ID).length() > 2);
		assertTrue(transactionData.getString(DataHandler.KEY_SESSIONID).length() > 2);
		assertTrue(transactionData.getString(DataHandler.KEY_NAME).equals("Notification Test 1"));
		assertTrue(transactionData.getString(DataHandler.KEY_AGENTTYPE).equals(UserExperience.APP_PERF_AGENT_TYPE));
		assertTrue(transactionData.getInt(DataHandler.KEY_VERSION) == UserExperience.APP_PERF_VERSION);
		

		//assertTrue(transactionData.getString(UserExperience.KEY_USERDATA).equals("datadata"));
		assertTrue(transactionData.getString(DataHandler.KEY_USERTAG1).equals("tagtag1"));
		//assertTrue(transactionData.getString(UserExperience.KEY_USERTAG2).equals("tagtag2"));
		//assertTrue(transactionData.getString(UserExperience.KEY_USERTAG3).equals("tagtag3"));
	}
	
	
	public void testBadInputs() throws Exception
	{
        StubDataHandler stubDataHandler = new StubDataHandler("TESTCust", new SettingsObject(), false);
		UserExperience apm = new UserExperience("TESTCust", "TestApp", this.getContext());
        apm.setMockDataHandler(stubDataHandler);
		
		// Try closing a transaction twice
		TransactionId id = apm.transactionStart("Interval Test 1");
		apm.transactionEnd(id);
		apm.transactionEnd(id);
		
		// Try closing a null transaction
		apm.transactionEnd(null);
		
		// Try adding to a null transaction
		apm.setTransactionEvent("Somethign", null);
		apm.setTransactionError(null, "Error");
		apm.setTransactionUserTag1(null, "Tag1");
		apm.setTransactionUserTag2(null, "Tag2");
		apm.setTransactionUserTag3(null, "Tag3");
		apm.setTransactionUserData(null, "Stuff");
		
		// Try adding to a closed transaction
		apm.setTransactionEvent("Somethign", id);
		apm.setTransactionError(id, "Error");
		apm.setTransactionUserTag1(id, "Tag1");
		apm.setTransactionUserTag2(id, "Tag2");
		apm.setTransactionUserTag3(id, "Tag3");
		apm.setTransactionUserData(id, "Stuff");
		

	}
	
	public void testMaxUnfinishedTransactionSize() throws Exception
	{
        StubDataHandler stubDataHandler = new StubDataHandler("TESTCust", new SettingsObject(), false);
        UserExperience apm = new UserExperience("TESTCust", "TestApp", this.getContext());
        apm.setMockDataHandler(stubDataHandler);
		
		TransactionId first = apm.transactionStart("First");
		
		for (int i = 0; i < UserExperience.MAX_UNFINISHED_TRANSACTIONS_IN_MEMORY - 1; i++)
		{
			apm.transactionStart("Dummy");
		}
		
		apm.transactionEnd(first);

        // Check that the JsonArray is not null
        Vector<MaitiTransaction> unfinishedTransactions = apm.getUnfinishedTransactions();
        assertTrue(unfinishedTransactions != null);

        MaitiTransaction transactionData =  unfinishedTransactions.get(unfinishedTransactions.size() - 1);

        assertEquals(unfinishedTransactions.size(), UserExperience.MAX_UNFINISHED_TRANSACTIONS_IN_MEMORY - 1);
        String transId = ((JSONObject) stubDataHandler.getJsonArray().get(0)).getString("id");
        assertEquals(transId, first.getTransactionId());



		first = apm.transactionStart("newFirst");
		for (int i = 0; i < UserExperience.MAX_UNFINISHED_TRANSACTIONS_IN_MEMORY + 1; i++)
		{
			apm.transactionStart("Dummy");
		}
		apm.transactionEnd(first);

        assertEquals(unfinishedTransactions.size(), UserExperience.MAX_UNFINISHED_TRANSACTIONS_IN_MEMORY);


        TransactionId last = apm.transactionStart("newLast");
        apm.transactionEnd(last);

        unfinishedTransactions = apm.getUnfinishedTransactions();
        //transactionData =  unfinishedTransactions.get(unfinishedTransactions.size() - 1);

        transId = ((JSONObject) stubDataHandler.getJsonArray().get(0)).getString("id");
        assertNotSame(transId, first.getTransactionId());
        assertEquals(transId, last.getTransactionId());
	}
	
	
	
	
	public static void sleep(long ms)
	{
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {	
			System.err.print(e);
		}
	}
}
