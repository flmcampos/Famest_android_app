package com.example.shear_app;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ReaderActivity extends Activity {

    Chronometer cmTimer;
    Button btnStart, btnStop;
    Boolean resume = false;
    long elapsedTime;
    String TAG = "TAG";

    public static BluetoothConnectionActivity BTConnectionL;
    public static BluetoothConnectionActivity BTConnectionR;

    public TextView messageTextL;
    public TextView messageTextR;

    protected FrameLayout frameLayout;
    protected FrameLayout dataLayout;

    List<LeituraClass> PeDireito = new ArrayList<>();
    List<LeituraClass> PeEsquerdo = new ArrayList<>();

    private int LHal, LMet1, LMet2, LMet3, LMid, LCal1, LCal2;
    private int RHal, RMet1, RMet2, RMet3, RMid, RCal1, RCal2;

    private int RSum, LSum;

    private View CP_esq_ball, CP_dir_ball;

    private View LHal_ball, LMet1_ball, LMet2_ball, LMet3_ball, LMid_ball, LCal1_ball, LCal2_ball;
    private View RHal_ball, RMet1_ball, RMet2_ball, RMet3_ball, RMid_ball, RCal1_ball, RCal2_ball;

    Boolean data_dir_esq = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //timerTextView = findViewById(R.id.TimerTextView);
        //button = findViewById(R.id.Start);

        setContentView(R.layout.chat_bt);

        frameLayout = (FrameLayout) findViewById(R.id.map_container);
        dataLayout = (FrameLayout) findViewById(R.id.data_container);

        getLayoutInflater().inflate(R.layout.feet_map, frameLayout);
        getLayoutInflater().inflate(R.layout.data, dataLayout);

        frameLayout.setVisibility(View.INVISIBLE);
        dataLayout.setVisibility(View.INVISIBLE);

        cmTimer = (Chronometer) findViewById(R.id.cmTimer);

        btnStart = findViewById(R.id.Start);
        btnStop = findViewById(R.id.Stop);

        LHal_ball = findViewById(R.id.Lhal);
        LMet1_ball = findViewById(R.id.Lmet1);
        LMet2_ball = findViewById(R.id.Lmet2);
        LMet3_ball = findViewById(R.id.Lmet3);
        LMid_ball = findViewById(R.id.Lmid);
        LCal1_ball = findViewById(R.id.Lcal1);
        LCal2_ball = findViewById(R.id.Lcal2);

        RHal_ball = findViewById(R.id.Rhal);
        RMet1_ball = findViewById(R.id.Rmet1);
        RMet2_ball = findViewById(R.id.Rmet2);
        RMet3_ball = findViewById(R.id.Rmet3);
        RMid_ball = findViewById(R.id.Rmid);
        RCal1_ball = findViewById(R.id.Rcal1);
        RCal2_ball = findViewById(R.id.Rcal2);

        CP_dir_ball = findViewById(R.id.CP_dir);
        CP_esq_ball = findViewById(R.id.CP_esq);


        Bundle bn = getIntent().getExtras();
        String mDeviceAddressLeft = bn.getString("esq");
        String mDeviceAddressRight = bn.getString("dir");

        //Create connection for device
        BTConnectionL = new BluetoothConnectionActivity(this, mDeviceAddressLeft, mHandlerEsq);
        BTConnectionL.execute();

        //Create connection for device
        BTConnectionR = new BluetoothConnectionActivity(this, mDeviceAddressRight, mHandlerDir);
        BTConnectionR.execute();

        messageTextR = findViewById(R.id.Data_collected_dir);
        messageTextL = findViewById(R.id.Data_collected_esq);

        /*button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button = (Button) v;
                if (button.getText().equals("Start")) {
                    startTime = System.currentTimeMillis();
                    timerHandler.postDelayed(timerRunnable, 0);
                    button.setText("Stop");
                } else {
                    timerHandler.removeCallbacks(timerRunnable);
                    button.setText("Start");
                }
            }
        });*/

        cmTimer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                if (!resume) {
                    long minutes = ((SystemClock.elapsedRealtime() - cmTimer.getBase()) / 1000) / 60;
                    long seconds = ((SystemClock.elapsedRealtime() - cmTimer.getBase()) / 1000) % 60;
                    elapsedTime = SystemClock.elapsedRealtime();
                    Log.d(TAG, "onChronometerTick: " + minutes + " : " + seconds);
                } else {
                    long minutes = ((elapsedTime - cmTimer.getBase()) / 1000) / 60;
                    long seconds = ((elapsedTime - cmTimer.getBase()) / 1000) % 60;
                    elapsedTime = elapsedTime + 1000;
                    Log.d(TAG, "onChronometerTick: " + minutes + " : " + seconds);
                }
            }
        });

    }



    private final Handler mHandlerDir = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (data_dir_esq) {
                String s = (String) msg.obj;
                Log.d("TAG", "Mensagem lida R: " + s);

                String delimiter = "\\|";
                String[] arrofs = s.split(delimiter);

                RHal = Integer.parseInt(arrofs[1]);
                RMet1 = Integer.parseInt(arrofs[2]);
                RMet2 = Integer.parseInt(arrofs[3]);
                RMet3 = Integer.parseInt(arrofs[4]);
                RMid = Integer.parseInt(arrofs[5]);
                RCal1 = Integer.parseInt(arrofs[6]);
                RCal2 = Integer.parseInt(arrofs[7]);



                RSum = (RHal) + (RMet1) + (RMet2) + (RMet3) + (RMid) + (RCal1) + (RCal2);

                //Ball growth

                RHal_ball.getLayoutParams().width = (200 + (RHal)) / 10;
                RHal_ball.getLayoutParams().height = (200 + (RHal)) / 10;
                RHal_ball.requestLayout();

                RMet1_ball.getLayoutParams().width = (200 + (RMet1)) / 10;
                RMet1_ball.getLayoutParams().height = (200 + (RMet1)) / 10;
                RMet1_ball.requestLayout();

                RMet2_ball.getLayoutParams().width = (200 + (RMet2)) / 10;
                RMet2_ball.getLayoutParams().height = (200 + (RMet2)) / 10;
                RMet2_ball.requestLayout();

                RMet3_ball.getLayoutParams().width = (200 + (RMet3)) / 10;
                RMet3_ball.getLayoutParams().height = (200 + (RMet3)) / 10;
                RMet3_ball.requestLayout();

                RMid_ball.getLayoutParams().width = (200 + (RMid)) / 10;
                RMid_ball.getLayoutParams().height = (200 + (RMid)) / 10;
                RMid_ball.requestLayout();

                RCal1_ball.getLayoutParams().width = (200 + (RCal1)) / 10;
                RCal1_ball.getLayoutParams().height = (200 + (RCal1)) / 10;
                RCal1_ball.requestLayout();

                RCal2_ball.getLayoutParams().width = (200 + (RCal2)) / 10;
                RCal2_ball.getLayoutParams().height = (200 + (RCal2)) / 10;
                RCal2_ball.requestLayout();

                //Ball color change

                RHal_ball.getBackground().setColorFilter(Color.parseColor(DecToHexa((int) (-0.17 * RHal + 255))), PorterDuff.Mode.MULTIPLY);
                RMet1_ball.getBackground().setColorFilter(Color.parseColor(DecToHexa((int) (-0.17 * RMet1 + 255))), PorterDuff.Mode.MULTIPLY);
                RMet2_ball.getBackground().setColorFilter(Color.parseColor(DecToHexa((int) (-0.17 * RMet2 + 255))), PorterDuff.Mode.MULTIPLY);
                RMet3_ball.getBackground().setColorFilter(Color.parseColor(DecToHexa((int) (-0.17 * RMet3 + 255))), PorterDuff.Mode.MULTIPLY);
                RMid_ball.getBackground().setColorFilter(Color.parseColor(DecToHexa((int) (-0.17 * RMid + 255))), PorterDuff.Mode.MULTIPLY);
                RCal1_ball.getBackground().setColorFilter(Color.parseColor(DecToHexa((int) (-0.17 * RCal1 + 255))), PorterDuff.Mode.MULTIPLY);
                RCal2_ball.getBackground().setColorFilter(Color.parseColor(DecToHexa((int) (-0.17 * RCal2 + 255))), PorterDuff.Mode.MULTIPLY);

                if (RSum != 0) {

                    //Calculate center of pressure coordinates

                    CP_dir_ball.setVisibility(View.VISIBLE);

                    CP_dir_ball.setX((RHal_ball.getX() * RHal + RMet1_ball.getX() * RMet1 + RMet2_ball.getX() * RMet2 + RMet3_ball.getX() * RMet3 + RMid_ball.getX() * RMid + RCal1_ball.getX() * RCal1 + RCal2_ball.getX() * RCal2) / RSum);
                    CP_dir_ball.setY((RHal_ball.getY() * RHal + RMet1_ball.getY() * RMet1 + RMet2_ball.getY() * RMet2 + RMet3_ball.getY() * RMet3 + RMid_ball.getY() * RMid + RCal1_ball.getY() * RCal1 + RCal2_ball.getY() * RCal2) / RSum);

                } else {
                    CP_dir_ball.setVisibility(View.INVISIBLE);

                }

                LeituraClass val = new LeituraClass();

                val.RHal_data = RHal;
                val.RMet1_data = RMet1;
                val.RMet2_data = RMet2;
                val.RMet3_data = RMet3;
                val.RMid_data = RMid;
                val.RCal1_data = RCal1;
                val.RCal2_data = RCal2;

                val.readingDate = Calendar.getInstance().getTime();

                PeDireito.add(val);

                messageTextR.setText(s);
            }
        }
    };


    private final Handler mHandlerEsq = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (data_dir_esq) {
                String s = (String) msg.obj;
                Log.d("TAG", "Mensagem lida L: " + s);

                String delimiter = "\\|";
                String[] arrofs = s.split(delimiter);

                LHal = Integer.parseInt(arrofs[1]);
                LMet1 = Integer.parseInt(arrofs[2]);
                LMet2 = Integer.parseInt(arrofs[3]);
                LMet3 = Integer.parseInt(arrofs[4]);
                LMid = Integer.parseInt(arrofs[5]);
                LCal1 = Integer.parseInt(arrofs[6]);
                LCal2 = Integer.parseInt(arrofs[7]);


                LSum = (LHal) + (LMet1) + (LMet2) + (LMet3) + (LMid) + (LCal1) + (LCal2);

                //Log.d("Values", "Valores inteiros: " + LHal + "," + LMet2 + "," + LMet3 + "," + LMid + "," + LCal1 + "," + LCal2 + ", SOMA: " + LSum);


                //Ball growth

                LHal_ball.getLayoutParams().width = (200 + (LHal)) / 10;
                LHal_ball.getLayoutParams().height = (200 + (LHal)) / 10;
                LHal_ball.requestLayout();

                LMet1_ball.getLayoutParams().width = (200 + (LMet1)) / 10;
                LMet1_ball.getLayoutParams().height = (200 + (LMet1)) / 10;
                LMet1_ball.requestLayout();

                LMet2_ball.getLayoutParams().width = (200 + (LMet2)) / 10;
                LMet2_ball.getLayoutParams().height = (200 + (LMet2)) / 10;
                LMet2_ball.requestLayout();

                LMet3_ball.getLayoutParams().width = (200 + (LMet3)) / 10;
                LMet3_ball.getLayoutParams().height = (200 + (LMet3)) / 10;
                LMet3_ball.requestLayout();

                LMid_ball.getLayoutParams().width = (200 + (LMid)) / 10;
                LMid_ball.getLayoutParams().height = (200 + (LMid)) / 10;
                LMid_ball.requestLayout();

                LCal1_ball.getLayoutParams().width = (200 + (LCal1)) / 10;
                LCal1_ball.getLayoutParams().height = (200 + (LCal1)) / 10;
                LCal1_ball.requestLayout();

                LCal2_ball.getLayoutParams().width = (200 + (LCal2)) / 10;
                LCal2_ball.getLayoutParams().height = (200 + (LCal2)) / 10;
                LCal2_ball.requestLayout();

                //Ball color change

                /*if (-0.5 * LHal +255 <= 0) {
                    LHal_ball.getBackground().setColorFilter(Color.parseColor(DecToHexa((0))), PorterDuff.Mode.MULTIPLY);
                } else {
                    LHal_ball.getBackground().setColorFilter(Color.parseColor(DecToHexa((int) (-0.5 * LHal + 255))), PorterDuff.Mode.MULTIPLY);
                }*/
                LHal_ball.getBackground().setColorFilter(Color.parseColor(DecToHexa((int) (-0.5 * LHal + 255))), PorterDuff.Mode.MULTIPLY);
                LMet1_ball.getBackground().setColorFilter(Color.parseColor(DecToHexa((int) (-0.5 * LMet1 + 255))), PorterDuff.Mode.MULTIPLY);
                LMet2_ball.getBackground().setColorFilter(Color.parseColor(DecToHexa((int) (-0.5 * LMet2 + 255))), PorterDuff.Mode.MULTIPLY);
                LMet3_ball.getBackground().setColorFilter(Color.parseColor(DecToHexa((int) (-0.5 * LMet3 + 255))), PorterDuff.Mode.MULTIPLY);
                LMid_ball.getBackground().setColorFilter(Color.parseColor(DecToHexa((int) (-0.5 * LMid + 255))), PorterDuff.Mode.MULTIPLY);
                LCal1_ball.getBackground().setColorFilter(Color.parseColor(DecToHexa((int) (-0.5 * LCal1 + 255))), PorterDuff.Mode.MULTIPLY);
                LCal2_ball.getBackground().setColorFilter(Color.parseColor(DecToHexa((int) (-0.5 * LCal2 + 255))), PorterDuff.Mode.MULTIPLY);

                //Calculate Center of pressure

                if (LSum != 0) {

                    CP_esq_ball.setVisibility(View.VISIBLE);

                    CP_esq_ball.setX((LHal_ball.getX() * LHal + LMet1_ball.getX() * LMet1 + LMet2_ball.getX() * LMet2 + LMet3_ball.getX() * LMet3 + LMid_ball.getX() * LMid + LCal1_ball.getX() * LCal1 + LCal2_ball.getX() * LCal2) / LSum);
                    CP_esq_ball.setY((LHal_ball.getY() * LHal + LMet1_ball.getY() * LMet1 + LMet2_ball.getY() * LMet2 + LMet3_ball.getY() * LMet3 + LMid_ball.getY() * LMid + LCal1_ball.getY() * LCal1 + LCal2_ball.getY() * LCal2) / LSum);

                } else {
                    CP_esq_ball.setVisibility(View.INVISIBLE);

                }

                LeituraClass val = new LeituraClass();

                val.LHal_data = LHal;
                val.LMet1_data = LMet1;
                val.LMet2_data = LMet2;
                val.LMet3_data = LMet3;
                val.LMid_data = LMid;
                val.LCal1_data = LCal1;
                val.LCal2_data = LCal2;

                val.readingDate = Calendar.getInstance().getTime();

                PeEsquerdo.add(val);

                messageTextL.setText(s);
            }
        }
    };



        String DecToHexa(int n) {

            // char array to store hexadecimal number
            char[] hexaDeciNum = new char[100];

            // counter for hexadecimal number array
            int i = 0;
            while (n != 0) {
                // temporary variable to store remainder
                int temp = 0;

                // storing remainder in temp variable.
                temp = n % 16;

                // check if temp < 10
                if (temp < 10) {
                    hexaDeciNum[i] = (char) (temp + 48);
                    i++;
                } else {
                    hexaDeciNum[i] = (char) (temp + 55);
                    i++;
                }

                n = n / 16;
            }
            StringBuilder Hexa = new StringBuilder();

            for (int j = i - 1; j >= 0; j--) {
                Hexa.append(hexaDeciNum[j]);
            }

            return "#FF" + Hexa + "00";
        }

    @Override
    public void onBackPressed() {}

    public void start_map(View view) {
            if (dataLayout.getVisibility() != View.VISIBLE) {
                frameLayout.setVisibility(View.VISIBLE);
            } else {
                dataLayout.setVisibility(View.INVISIBLE);
                frameLayout.setVisibility(View.VISIBLE);
            }
    }

    public void Dados(View view) {
        if (frameLayout.getVisibility() != View.VISIBLE) {
            dataLayout.setVisibility(View.VISIBLE);
        } else {
            frameLayout.setVisibility(View.INVISIBLE);
            dataLayout.setVisibility(View.VISIBLE);
        }
    }

    public void Start_Read(View view) {

        data_dir_esq = true;
        btnStart.setEnabled(false);
        btnStop.setEnabled(true);

        if (!resume) {
            cmTimer.setBase(SystemClock.elapsedRealtime());
            cmTimer.start();
        } else {
            cmTimer.start();
        }

    }

    public void Stop_read(View view) {
        data_dir_esq = false;
        messageTextL.setText("Reading stopped");
        messageTextR.setText("Reading stopped");

        btnStart.setEnabled(true);
        btnStop.setEnabled(false);
        cmTimer.stop();
        cmTimer.setText("");
        resume = true;
        btnStart.setText("Resume");

    }
}
