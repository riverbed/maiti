/*
===================
Copyright (c) 2013 Riverbed Technology, Inc.

MAITI is licensed under the terms and conditions of the MIT License as set forth in LICENSE.TXT, which accompanies the software.  MAITI is distributed “AS IS” as set forth in the MIT License.
===================
*/

package com.riverbed.mobile.android.apmlib;

import com.riverbed.mobile.android.apmlib.objects.SettingsObject;
import org.json.JSONArray;

public class StubDataHandler extends DataHandler {

	
	private JSONArray jsonArrayPosting = new JSONArray();
	private final boolean doBuffer;
	
	public StubDataHandler(String customerID, SettingsObject settings, boolean doBuffer) {
		super(customerID, settings);
		
		this.doBuffer = doBuffer;
	}

	// Override the SEND function.  Rather than sending it, just clear the buffer (as if it has been sent)
	@Override
	protected void sendJson(final JSONArray jsonArrayPosting) {

       this.transactionBuffer.clear();
	}

    JSONArray getJsonArray()
    {
        return getPostBuffer();
    }


    @Override
    synchronized void push(MaitiTransaction transaction) {

        if (doBuffer)
        {
            super.push(transaction);
        }
        else
        {
            transactionBuffer.add(transaction);
        }
    }


	
}
