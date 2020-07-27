package com.example.shear_app;

import androidx.annotation.NonNull;

public class CalibrationClass {
    public int HalC_data;
    public int Met1C_data;
    public int Met2C_data;
    public int Met3C_data;
    public int MidC_data;
    public int Cal1C_data;
    public int Cal2C_data;
    public float TempC_data;
    public float HumidC_data;
    public long readingDateC;

    @NonNull
    @Override
    public String toString() {
        return (float) readingDateC/1000 + "," +HalC_data + "," + Met1C_data+ "," +Met2C_data+ ","
                +Met3C_data+ "," + MidC_data+ "," +Cal1C_data+ "," +Cal2C_data+ ","+ TempC_data + "," + HumidC_data + System.getProperty("line.separator");
    }
}
