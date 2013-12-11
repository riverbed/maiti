package com.riverbed.mobile.android.apmlib.datagenerator;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.*;
import com.riverbed.mobile.android.apmlib.UserExperience;
import com.riverbed.mobile.android.apmlib.objects.TransactionId;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

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
public class ThumbnailActivity  extends ListActivity implements View.OnClickListener {

    private final int ABSOLUTE_MAX_NUM_MAITI_IMAGES = 51;

    // 5% failures
    private final static double FAILURE_PERCENT = 0.05;

    private static final int FIRST_LISTITEM_ID = 3523520;
    private static final int FINAL_LISTITEM_ID = 3523521;

    private static final int INVALID_DELAY = -1;

    private static final String KEY_STATUS_CODE = "status_code";
    private static final String KEY_DL_DELAY = "dl_delay";
    private static final String KEY_MAITI_TRANS_ID = "maiti_trans_id";
    private static final int SUCCESS = 0;
    private static final int FAIL = 1;

    private int THUMBNAILS_PER_GRAB = 4;
    private int MAX_PAGES = ABSOLUTE_MAX_NUM_MAITI_IMAGES / THUMBNAILS_PER_GRAB;
    private int downloadedThumbs;

    private UserExperience maiti;

    private ProgressDialog dialog;
    private TextView lblCurPage;
    private ThumbnailRowAdapter adapter;
    private View configView;
    private View footerView;

    private List<Thumbnail> thumbnails = new ArrayList<Thumbnail>(ABSOLUTE_MAX_NUM_MAITI_IMAGES);

    private EditText txtThumbDelay;
    private EditText txtServerDelay;
    private Button cmdClear;
    private Spinner cmbthumbsPerPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.thumbnail_list);


        maiti = ((GeneratorApp) getApplication()).getMaiti();


        lblCurPage = (TextView) findViewById(R.id.lbl_curpage);


        adapter = new ThumbnailRowAdapter(this, R.layout.thumbnail_row,
                R.id.title, thumbnails);

        configView = getLayoutInflater().inflate(R.layout.thumbnail_config, getListView(), false);
        configView.setTag(new Integer(FIRST_LISTITEM_ID));
        getListView().addHeaderView(configView);

        footerView = getLayoutInflater().inflate(R.layout.thumbnail_moreresults, getListView(), false);
        footerView.setTag(new Integer(FINAL_LISTITEM_ID));

        getListView().addFooterView(footerView);

        setListAdapter(adapter);
        getListView().setTextFilterEnabled(false);

        txtServerDelay = (EditText) configView.findViewById(R.id.txtServerDelay);
        txtThumbDelay = (EditText) configView.findViewById(R.id.txtThumbDelay);
        cmdClear = (Button) configView.findViewById(R.id.cmdClear);
        cmbthumbsPerPage = (Spinner) configView.findViewById(R.id.cmb_thumbs_per_page);
        cmbthumbsPerPage.setAdapter(ArrayAdapter.createFromResource(this,
                R.array.thumbnails_spinner_array, android.R.layout.simple_spinner_item));
        cmbthumbsPerPage.setSelection(2);

        cmbthumbsPerPage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {

                THUMBNAILS_PER_GRAB = Integer.valueOf((String) adapterView.getItemAtPosition(pos));
                MAX_PAGES = ABSOLUTE_MAX_NUM_MAITI_IMAGES / THUMBNAILS_PER_GRAB;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // ignore
            }
        });

        cmdClear.setOnClickListener(this);


        downloadedThumbs = 0;
        lblCurPage.setText("");

        initDownload();
    }

    private void updateMaxPages()
    {

    }


    // Wh
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.cmdClear)
        {
            maiti.notification("Thumbnail Clear");

            thumbnails.clear();
            downloadedThumbs = 0;
            lblCurPage.setText("");

            footerView.setVisibility(View.VISIBLE);

            adapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onListItemClick(ListView parent, View v, int position, long id) {



        if (((Integer) v.getTag()).intValue() == FINAL_LISTITEM_ID)
        {
            // Last item is always the "more results" clicky

            if (downloadedThumbs <= ABSOLUTE_MAX_NUM_MAITI_IMAGES)
            {
                int thumbDelay = getThumbDelay();
                int serverDelay = getServerDelay();

                if (serverDelay == INVALID_DELAY)
                {
                    Toast.makeText(this, "Invalid Server Delay", Toast.LENGTH_SHORT).show();
                }
                else if (thumbDelay == INVALID_DELAY)
                {
                    Toast.makeText(this, "Invalid Thumbnail Delay", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    initDownload();
                }

            }
        }
        else
        {
            Thumbnail thumbnail = thumbnails.get(position);

            //Intent detailsActivity = new Intent(this, BookDetailsActivity.class);

            // Package up our drilldown and kick off the graph activity
            //Bundle b = new Bundle();
            //b.putSerializable(BookDetailsActivity.BOOK_NAME, book);
            //b.putParcelable(UserParamParcel.USER_PARAM_PARCEL_NAME, userParams);

            //detailsActivity.putExtras(b);

            //startActivity(detailsActivity);
        }

    }



    private void initDownload()
    {
        if ((dialog == null) || (dialog.isShowing() == false))
        {
            dialog = ProgressDialog.show(this, "",
                    "Downloading Thumbnails...", true, true);
            DownloadThread downloadThread = new DownloadThread(handler);
            downloadThread.start();
        }
    }

    private int getServerDelay()
    {
        try
        {
            int meanVal = Integer.parseInt(txtServerDelay.getText().toString());

            return getRandomCentered(meanVal);
        }
        catch (NumberFormatException e) {
            return INVALID_DELAY;
        }


    }

    private int getThumbDelay()
    {

        try
        {
            int meanVal = Integer.parseInt(txtThumbDelay.getText().toString());
            return getRandomCentered(meanVal);
        }
        catch (NumberFormatException e)
        {
            return INVALID_DELAY;
        }

    }

    // Return a random number uniformly distributed between 70% and 130% of average
    private int getRandomCentered(int average)
    {
        if (average < 0)
            return INVALID_DELAY;

        Random random = new Random();

        int minVal = (int) (((double) average) * .7);
        int maxVal = (int) (((double) average) * 1.3);

        return minVal + random.nextInt(maxVal - minVal);


    }


    private class DownloadThread extends Thread {
        Handler mHandler;

        DownloadThread(Handler h) {
            mHandler = h;
        }

        @Override
        public void run() {

            TransactionId maitiPageId = maiti.transactionStart("Thumbnail Page");
            maiti.setTransactionUserTag1(maitiPageId, "Results " + (downloadedThumbs + 1) + "-" + (downloadedThumbs + THUMBNAILS_PER_GRAB));

            Bundle b = new Bundle();
            Message msg = mHandler.obtainMessage();

            int serverDelayMs = getServerDelay();

            // "Downloading data...
            // Just simulating it with a sleep.

            TransactionId maitiXMLId = maiti.transactionStart("Download XML", maitiPageId);

            try {
                Thread.sleep(serverDelayMs);
            }   catch (InterruptedException e) {}

            maiti.transactionEnd(maitiXMLId);

            b.putParcelable(KEY_MAITI_TRANS_ID, maitiPageId);
            b.putInt(KEY_DL_DELAY, serverDelayMs);

            // Randomly signal a failure (10% of the time)
            Random r = new Random();
            if (r.nextDouble() < FAILURE_PERCENT)
                b.putInt(KEY_STATUS_CODE, FAIL);
            else
                b.putInt(KEY_STATUS_CODE, SUCCESS);


            msg.setData(b);
            mHandler.sendMessage(msg);
        }

    }



    // Define the Handler that receives messages from the thread and update the progress
    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int status_code = msg.getData().getInt(KEY_STATUS_CODE);

            int dl_delay = msg.getData().getInt(KEY_DL_DELAY);
            TransactionId maitiPageId = (TransactionId) msg.getData().getParcelable(KEY_MAITI_TRANS_ID);

            if (dialog.isShowing())
            {
                if (status_code == SUCCESS){

                    Toast.makeText(getApplicationContext(),"Thumbnails downloaded in " + dl_delay + "ms", Toast.LENGTH_LONG).show();
                    displayResults(maitiPageId);

                }
                else if (status_code == FAIL)
                {
                    maiti.setTransactionError(maitiPageId, "Download Failure");
                    maiti.transactionEnd(maitiPageId);
                    Toast.makeText(getApplicationContext(),"Download FAILED after " + dl_delay + "ms", Toast.LENGTH_LONG).show();
                }


                dialog.dismiss();
            }
        }
    };

    private void displayResults(final TransactionId maitiPageId)
    {
        // At this point we should be passing downloaded data through the bundle, but since it's all
        // fake data anyway, we'll just create it here.


        Handler thumbnailDLHandler = new Handler();

        final AtomicInteger dlCompletedCountdown = new AtomicInteger(THUMBNAILS_PER_GRAB);

        int start_index = downloadedThumbs + 1;

        for (int i = start_index; i < start_index + THUMBNAILS_PER_GRAB; i++)
        {
            if (i > ABSOLUTE_MAX_NUM_MAITI_IMAGES)
                break;

            final TransactionId maitiThumbnailId = maiti.transactionStart("Download Thumbnail", maitiPageId);

            final Thumbnail newThumb = new Thumbnail(i, getThumbDelay());

            maiti.setTransactionUserTag1(maitiThumbnailId, "Thumbnail #" + newThumb.getId());

            thumbnails.add(newThumb);

            // For each thumbnail, "download" it asyncronously.  We're really just sleeping for x milliseconds
            // Once it's downloaded, set the flag on the thumbnail that it's been downloaded so that the list will show it
            thumbnailDLHandler.postDelayed(new Runnable() {
                public void run() {
                    newThumb.setDownloaded(true);

                    maiti.transactionEnd(maitiThumbnailId);

                    // Once all thumbnails are downloaded, close the transaction
                    int itemsLeftToDl = dlCompletedCountdown.decrementAndGet();
                    if (itemsLeftToDl <= 0)
                        maiti.transactionEnd(maitiPageId);

                    if (adapter != null)
                        adapter.notifyDataSetChanged();
                }
            }, newThumb.getLoadDelayMs());

            downloadedThumbs++;
        }


        if (downloadedThumbs >= ABSOLUTE_MAX_NUM_MAITI_IMAGES)
            footerView.setVisibility(View.INVISIBLE);

        if (adapter != null)
            adapter.notifyDataSetChanged();

        lblCurPage.setText(String.format("Results %d-%d of %d", 1, downloadedThumbs, ABSOLUTE_MAX_NUM_MAITI_IMAGES));
    }

}
