/*
===================
Copyright (c) 2013 Riverbed Technology, Inc.

MAITI is licensed under the terms and conditions of the MIT License as set forth in LICENSE.TXT, which accompanies the software.  MAITI is distributed “AS IS” as set forth in the MIT License.
===================
*/
package com.riverbed.mobile.android.apmlib.sample;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.riverbed.mobile.android.apmlib.UserExperience;
import com.riverbed.mobile.android.apmlib.objects.TransactionId;

public class WebViewSample extends Activity
{
	WebView webSample;
	UserExperience mAppPerfMonitor;
	private TransactionId parentID;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webviewsample);
		


		mAppPerfMonitor = ((SampleApplication) getApplication()).getAppPerformanceMonitor();

		webSample = (WebView)findViewById(R.id.webSample);
		
		webSample.setWebViewClient(new myWebClient());
		webSample.loadUrl("http://www.google.com");
	}
	
	public class myWebClient extends WebViewClient
    {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
        	

        	parentID = mAppPerfMonitor.transactionStart("Webview Open");
    		mAppPerfMonitor.setTransactionEvent("webview loading", parentID);

    		mAppPerfMonitor.setTransactionUserData(parentID, "test");

    		TransactionId subTransId = mAppPerfMonitor.transactionStart("SubTransaction1", parentID);
    		mAppPerfMonitor.transactionEnd(subTransId);
    		
            super.onPageStarted(view, url, favicon);
        }
 
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            view.loadUrl(url);
            return true;
 
        }
        
        @Override
        public void onPageFinished(WebView view, String url) {
        	try {
				mAppPerfMonitor.transactionEnd(parentID);
			} catch (Exception e) {
				e.printStackTrace();
			}
        	super.onPageFinished(view, url);
        }
    }
}
