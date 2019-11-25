package com.example.remembrall;

import android.content.Context;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Note implements Serializable {

    private long nDateTime;
    private String nTitle;
    private String nContent;

    public Note(long dateTime, String title, String content) {
        nContent = content;
        nDateTime = dateTime;
        nTitle = title;
    }

    public void setnDateTime(long nDateTime) {
        this.nDateTime = nDateTime;
    }

    public void setnTitle(String nTitle) {
        this.nTitle = nTitle;
    }

    public void setnContent(String nContent) {
        this.nContent = nContent;
    }

    public long getnDateTime() {
        return nDateTime;
    }

    public String getnTitle() {
        return nTitle;
    }

    public String getnContent() {
        return nContent;
    }

    public String getDateTimeFormatted(Context context) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy",
                context.getResources().getConfiguration().locale);
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(new Date(nDateTime));
    }
}