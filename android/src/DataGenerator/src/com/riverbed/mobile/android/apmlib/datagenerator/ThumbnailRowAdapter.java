package com.riverbed.mobile.android.apmlib.datagenerator;

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

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;



public class ThumbnailRowAdapter extends ArrayAdapter<Thumbnail> {


    private LayoutInflater mInflater;

    public ThumbnailRowAdapter(Context context, int resource,
                                int textViewResourceId, List<Thumbnail> objects) {
        super(context, resource, textViewResourceId, objects);


        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        TextView title = null;
        TextView detail = null;
        ImageView i11 = null;
        Thumbnail rowData = getItem(position);

        if (null == convertView) {
            convertView = mInflater.inflate(R.layout.thumbnail_row, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        holder = (ViewHolder) convertView.getTag();
        title = holder.gettitle();
        title.setText(rowData.getName());
        detail = holder.getdetail();
        detail.setText(rowData.getShortDescription());

        i11 = holder.getPicture();
        if (rowData.isDownloaded() == false)
        {
            RotateAnimation anim = new RotateAnimation(0f, 350f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setInterpolator(new LinearInterpolator());
            anim.setRepeatCount(Animation.INFINITE);
            anim.setDuration(700);

            i11.setImageResource(R.drawable.thumbspinner);
            i11.startAnimation(anim);
            i11.setVisibility(View.VISIBLE);
        }
        else
        {
            i11.setAnimation(null);

            try
            {
                Drawable d = Drawable.createFromStream(getContext().getAssets().open("maiti_img/" + rowData.getId() + ".jpg"), null);

                i11.setImageDrawable(d);
                i11.setBackgroundColor(Color.parseColor("#eeeeee"));
            }
            catch (IOException e)
            {
                i11.setVisibility(View.INVISIBLE);
            }
        }
//		else if (rowData.getNumImages() > 0)
//		{
//			//i11.setVisibility(View.INVISIBLE);
//			i11.setImageResource(R.drawable.img_icon);
//		}

        //i11.setImageResource(imgid[rowData.mId]);
        return convertView;
    }

    private class ViewHolder
    {
        private View mRow;
        private TextView title = null;
        private TextView detail = null;
        private ImageView hasImages = null;

        public ViewHolder(View row) {
            mRow = row;
        }

        public TextView gettitle() {
            if (null == title) {
                title = (TextView) mRow.findViewById(R.id.title);
            }
            return title;
        }

        public TextView getdetail() {
            if (null == detail) {
                detail = (TextView) mRow.findViewById(R.id.description);
            }
            return detail;
        }

        public ImageView getPicture() {
            if (null == hasImages) {
                hasImages = (ImageView) mRow.findViewById(R.id.thumb);
            }
            return hasImages;
        }
    }
}
