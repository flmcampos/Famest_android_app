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

    private float  RTempC, RHumC, LTempC, LHumC;

    private int RSumC, LSumC;

    private int peso_calculado;

    public List<CalibrationClass> PeDireitoC = new ArrayList<>();
    public List<CalibrationClass> PeEsquerdoC = new ArrayList<>();

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
                RTempC = Float.parseFloat(arrofsC[8]);
                RHumC = Float.parseFloat(arrofsC[9]);


                RSumC = (RHalC) + (RMet1C) + (RMet2C) + (RMet3C) + (RMidC) + (RCal1C) + (RCal2C);

                CalibrationClass valC = new CalibrationClass();

                valC.HalC_data = RHalC;
                valC.Met1C_data = RMet1C;
                valC.Met2C_data = RMet2C;
                valC.Met3C_data = RMet3C;
                valC.MidC_data = RMidC;
                valC.Cal1C_data = RCal1C;
                valC.Cal2C_data = RCal2C;
                valC.TempC_data = RTempC;
                valC.HumidC_data = RHumC;

                PeDireitoC.add(valC);
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
                LTempC = Float.parseFloat(arrofsC[8]);
                LHumC = Float.parseFloat(arrofsC[9]);


                LSumC = (LHalC) + (LMet1C) + (LMet2C) + (LMet3C) + (LMidC) + (LCal1C) + (LCal2C);

                CalibrationClass valC = new CalibrationClass();

                valC.HalC_data = LHalC;
                valC.Met1C_data = LMet1C;
                valC.Met2C_data = LMet2C;
                valC.Met3C_data = LMet3C;
                valC.MidC_data = LMidC;
                valC.Cal1C_data = LCal1C;
                valC.Cal2C_data = LCal2C;
                valC.TempC_data = LTempC;
                valC.HumidC_data = LHumC;

                PeEsquerdoC.add(valC);
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
            timeInMillisecondsC = 15 - (SystemClock.elapsedRealtime() - startTimeC)/1000;
            calibrationTimer.setText("" + timeInMillisecondsC);

            if (timeInMillisecondsC <= 0) {
                calibrationTimer.setText("Calibração realizada");
                data_dir_esqC = false;
                read.setEnabled(true);
                //peso_calculado = (int)(RSumC + LSumC) * (int) ;

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
