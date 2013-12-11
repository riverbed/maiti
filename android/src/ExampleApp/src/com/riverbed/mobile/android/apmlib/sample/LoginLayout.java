/*
===================
Copyright (c) 2013 Riverbed Technology, Inc.

MAITI is licensed under the terms and conditions of the MIT License as set forth in LICENSE.TXT, which accompanies the software.  MAITI is distributed “AS IS” as set forth in the MIT License.
===================
*/
package com.riverbed.mobile.android.apmlib.sample;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.riverbed.mobile.android.apmlib.UserExperience;
import com.riverbed.mobile.android.apmlib.objects.TransactionId;

public class LoginLayout extends Activity {
    EditText un,pw;
	TextView error;
    Button ok;
    Button btn_Web;
    

    UserExperience mAppPerfMonitor;
	private TransactionId parentID;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        un=(EditText)findViewById(R.id.et_un);
        pw=(EditText)findViewById(R.id.et_pw);
        ok=(Button)findViewById(R.id.btn_login);
        btn_Web=(Button)findViewById(R.id.btn_Web);
        error=(TextView)findViewById(R.id.tv_error);
        

        
        btn_Web.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View v) 
			{
				Intent iWebViewSample = new Intent(LoginLayout.this,WebViewSample.class);
				startActivity(iWebViewSample);
			}
		});
        
        ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

        		mAppPerfMonitor = ((SampleApplication) getApplication()).getAppPerformanceMonitor();

            	JSONObject jsonobj = new JSONObject();
            	try {
					jsonobj.putOpt("uname", un.getText().toString());
	            	jsonobj.putOpt("upwd", pw.getText().toString());
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            	ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            	postParameters.add(new BasicNameValuePair("input", jsonobj.toString()));
//            	postParameters.add(new BasicNameValuePair("password", pw.getText().toString()));

            	String response = null;

        		

    			

    			mAppPerfMonitor.notification("Login Notification", null);
    			
    			parentID = mAppPerfMonitor.transactionStart("Login Sent");
        		mAppPerfMonitor.setTransactionEvent("Loading", parentID);
        		
        		mAppPerfMonitor.setTransactionUserData(parentID, "401");
        		mAppPerfMonitor.setTransactionUserTag1(parentID, "Happened");
        		try
        		{
        			response = CustomHttpClient.executeHttpPost("http://www.google.com", postParameters);
        		}
        		catch (Exception e)
        		{
        			mAppPerfMonitor.setTransactionEvent("Exception", parentID);
        			mAppPerfMonitor.setTransactionUserData(parentID, e.getMessage());
        		}
        	    
    			mAppPerfMonitor.transactionEnd(parentID);
    			
        	    //String res=response.toString();
        	    //System.out.println("response"+res);
        	    //res= res.replaceAll("\\s+","");
        	    //if(res.equals("1"))
        	    //	error.setText(res);//"Correct Username or Password");
        	    //else
        	    //	error.setText(res);//"Sorry!! Incorrect Username or Password");
 

            }
        });
    }
}

