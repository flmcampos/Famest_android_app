package com.example.FAMEST_app;

import androidx.annotation.NonNull;

//classe que possibilita a transcrição dos dados recebidos para uma linha que será depois adicionada no ficheiro txt

public class LeituraClass {
    public int Hal_data;
    public int Met1_data;
    public int Met2_data;
    public int Met3_data;
    public int Mid_data;
    public int Cal1_data;
    public int Cal2_data;
    public float Temp_data;
    public float Humid_data;
    public long readingDate;

    @NonNull
    @Override
    public String toString() {
        return (float) readingDate/1000 + ": " +Hal_data + "," + Met1_data+ "," +Met2_data+ ","
                +Met3_data+ "," + Mid_data+ "," +Cal1_data+ "," +Cal2_data+ ","+ Temp_data + "," + Humid_data + System.getProperty("line.separator");
    }

    /*private static String getMSDateFromMillis(long d) {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss.SSS");
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return df.format(d);
    }*/
}