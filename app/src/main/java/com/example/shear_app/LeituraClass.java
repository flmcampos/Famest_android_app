package com.example.shear_app;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class LeituraClass {
    public int Hal_data;
    public int Met1_data;
    public int Met2_data;
    public int Met3_data;
    public int Mid_data;
    public int Cal1_data;
    public int Cal2_data;
    public int Temp_data;
    public int Humid_data;
    public long readingDate;

    @NonNull
    @Override
    public String toString() {
        return getMSDateFromMillis(readingDate) + "," +Hal_data + "," + Met1_data+ "," +Met2_data+ ","
                +Met3_data+ "," + Mid_data+ "," +Cal1_data+ "," +Cal2_data+ ","+ Temp_data + "," + Humid_data + System.getProperty("line.separator");
    }

    private static String getMSDateFromMillis(long d) {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss.SSS");
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return df.format(d);
    }
}