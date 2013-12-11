package com.riverbed.mobile.android.apmlib.datagenerator;

import android.*;
import android.R;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

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
public class MainListFragment extends ListFragment {

    private static final String THUMBNAIL_ACTIVITY = "Thumbnail Viewer";
    private static final String BROWSER_ACTIVITY = "Web Browser";
    private static final String SETTINGS_ACTIVITY = "MAITI Settings";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String[] values = new String[] {THUMBNAIL_ACTIVITY, BROWSER_ACTIVITY, SETTINGS_ACTIVITY};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.simple_list_item_1, values);
        setListAdapter(adapter);
    }



    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        String item = (String) getListAdapter().getItem(position);

        if (PrefsActivity.isPreferencesValid(getActivity()))
        {
            if (item.equals(THUMBNAIL_ACTIVITY))
            {
                Intent intent = new Intent(getActivity(),ThumbnailActivity.class);
                getActivity().startActivity(intent);
            }
            else if (item.equals(BROWSER_ACTIVITY))
            {
                Intent intent = new Intent(getActivity(),WebViewSample.class);
                getActivity().startActivity(intent);
            }
            else if (item.equals(SETTINGS_ACTIVITY))
            {
                Intent intent = new Intent(getActivity(),PrefsActivity.class);
                getActivity().startActivity(intent);
            }


        }
        else
        {
            Intent intent = new Intent(getActivity(),PrefsActivity.class);
            getActivity().startActivity(intent);
            Toast.makeText(getActivity(), "Please set your preferences before proceeding.", Toast.LENGTH_SHORT).show();
        }


    }
}
