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
public class Thumbnail {

    private final int id;
    private final int loadDelayMs;

    private boolean isDownloaded = false;

    public Thumbnail(int id, int loadDelayMs)
    {
        this.id = id;
        this.loadDelayMs = loadDelayMs;
    }

    public String getRawThumbnailPath()
    {
        return "raw/" + id + ".jpg";
    }
    public String getName()
    {
        return "Beverage #" + id;
    }

    public int getLoadDelayMs()
    {
        return loadDelayMs;
    }

    public String getShortDescription()
    {
        return "Thumbnail Load Delay: " + loadDelayMs + "ms";
        //return LoremIpsum.getWords(3);

    }

    public String getLongDescription()
    {
        return "";//LoremIpsum.getParagraphs(4);

    }

    public int getId()
    {
        return id;
    }


    public boolean isDownloaded() {
        return isDownloaded;
    }

    public void setDownloaded(boolean downloaded) {
        isDownloaded = downloaded;
    }

}
