/*
===================
Copyright (c) 2013 Riverbed Technology, Inc.

MAITI is licensed under the terms and conditions of the MIT License as set forth in LICENSE.TXT, which accompanies the software.  MAITI is distributed “AS IS” as set forth in the MIT License.
===================
*/
package com.riverbed.mobile.android.apmlib.objects;

import android.os.Parcel;
import android.os.Parcelable;

public class TransactionId implements Parcelable {

	// Just a simple wrapper class around a string to make it harder to use the wrong arguments
	
	private String id;


	public TransactionId(String id)
	{
		this.id = id;
	}

    private TransactionId(Parcel in)
    {
         this.id = in.readString();
    }

    public static final Parcelable.Creator<TransactionId> CREATOR
            = new Parcelable.Creator<TransactionId>() {
        public TransactionId createFromParcel(Parcel in) {
            return new TransactionId(in);
        }

        public TransactionId[] newArray(int size) {
            return new TransactionId[size];
        }
    };
	
	public String toString()
	{
		return getTransactionId();
	}
	public String getTransactionId()
	{
		return id;
	}
	
	public int length()
	{
		return id.length();
	}
	
	public boolean startsWith(String prefix)
	{
		return id.startsWith(prefix);
	}
	
	public boolean equals(TransactionId id)
	{
		return this.id.equals(id.getTransactionId());
	}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
    }
}
