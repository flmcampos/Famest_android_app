package com.example.shear_app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class CalibrationActivity extends AppCompatActivity {

    TextView calibrationTimer;

    long startTimeC = 0, timeInMillisecondsC = 15;

    public static BluetoothConnectionActivity BTConnectionCL;
    public static BluetoothConnectionActivity BTConnectionCR;

    Button btnStartC;

    private Button read;

    Boolean data_dir_esqC = false;

    private int RHalC, RMet1C, RMet2C, RMet3C, RMidC, RCal1C, RCal2C;
    private int LHalC, LMet1C, LMet2C, LMet3C, LMidC, LCal1C, LCal2C;

    private int SumC = 0, length = 0;

    private int RSumC, LSumC;

    private int peso_calculado;

    Handler customHandlerC = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calibration);

        btnStartC = findViewById(R.id.goToStartCalibration);

        Bundle bn = getIntent().getExtras();
        String mDeviceAddressLeftC = bn.getString("esq");
        String mDeviceAddressRightC = bn.getString("dir");

        //Create connection for device
        BTConnectionCL = new BluetoothConnectionActivity(this, mDeviceAddressLeftC, mHandlerEsqC);
        BTConnectionCL.execute();

        //Create connection for device
        BTConnectionCR = new BluetoothConnectionActivity(this, mDeviceAddressRightC, mHandlerDirC);
        BTConnectionCR.execute();

        read = findViewById(R.id.goToReaderActivity);

        calibrationTimer = (TextView) findViewById(R.id.calibrationTimer);
    }

    private final Handler mHandlerDirC = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (data_dir_esqC) {
                String sC = (String) msg.obj;
                Log.d("TAG", "Mensagem lida R: " + sC);

                String delimiter = "\\|";
                String[] arrofsC = sC.split(delimiter);

                RHalC = Integer.parseInt(arrofsC[1]);
                RMet1C = Integer.parseInt(arrofsC[2]);
                RMet2C = Integer.parseInt(arrofsC[3]);
                RMet3C = Integer.parseInt(arrofsC[4]);
                RMidC = Integer.parseInt(arrofsC[5]);
                RCal1C = Integer.parseInt(arrofsC[6]);
                RCal2C = Integer.parseInt(arrofsC[7]);

                RSumC = (RHalC) + (RMet1C) + (RMet2C) + (RMet3C) + (RMidC) + (RCal1C) + (RCal2C);

                SumC =+ RSumC;

                length =+1;
            }
        }
    };

    private final Handler mHandlerEsqC = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (data_dir_esqC) {
                String sC = (String) msg.obj;
                Log.d("TAG", "Mensagem lida R: " + sC);

                String delimiter = "\\|";
                String[] arrofsC = sC.split(delimiter);

                LHalC = Integer.parseInt(arrofsC[1]);
                LMet1C = Integer.parseInt(arrofsC[2]);
                LMet2C = Integer.parseInt(arrofsC[3]);
                LMet3C = Integer.parseInt(arrofsC[4]);
                LMidC = Integer.parseInt(arrofsC[5]);
                LCal1C = Integer.parseInt(arrofsC[6]);
                LCal2C = Integer.parseInt(arrofsC[7]);

                LSumC = (LHalC) + (LMet1C) + (LMet2C) + (LMet3C) + (LMidC) + (LCal1C) + (LCal2C);

                SumC =+ LSumC;

                length =+1;
            }
        }
    };

    public void Start_ReadC(View view) {

        data_dir_esqC = true;
        btnStartC.setEnabled(false);

        if (btnStartC.getText().equals("Start")) {
            startTimeC = SystemClock.elapsedRealtime();
            customHandlerC.postDelayed(updateTimerThread, 0);
        } else {
            calibrationTimer.setVisibility(View.VISIBLE);
        }
    }

    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            customHandlerC.postDelayed(this, 1000- (SystemClock.elapsedRealtime() - startTimeC)%1000);
            timeInMillisecondsC =- (SystemClock.elapsedRealtime() - startTimeC)/1000;
            calibrationTimer.setText("" + timeInMillisecondsC);

            if (timeInMillisecondsC <= 0) {
                calibrationTimer.setText("Calibração realizada");
                data_dir_esqC = false;
                read.setEnabled(true);
                peso_calculado = SumC / length;

                BTConnectionCL.disconnect();
                BTConnectionCR.disconnect();
            }
        }
    };

    /*public static String getDateFromMillisC(long d) {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return df.format(d);
    }*/

    public void Reader(View view){
        Intent i = new Intent(this, ReaderActivity.class);
        startActivity(i);
    }
}
