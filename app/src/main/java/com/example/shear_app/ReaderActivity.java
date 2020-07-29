package com.example.shear_app;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class ReaderActivity extends AppCompatActivity {

    Vibrator v;
    // Vibrate for 500 milliseconds

    private static final int PERMISSION_REQUEST_CODE = 1;

    TextView tvTimer;
    long startTime, timeInMilliseconds = 0;
    Handler customHandler = new Handler();
    Handler handler = new Handler();
    Button btnStart, btnStop;
    String TAG = "TAG";

    public static BluetoothConnectionActivity BTConnectionL;
    public static BluetoothConnectionActivity BTConnectionR;

    public TextView messageTextL;
    public TextView messageTextR;

    protected FrameLayout frameLayout;
    protected FrameLayout dataLayout;
    protected FrameLayout graphLayout;

    List<LeituraClass> PeDireito = new ArrayList<>();
    List<LeituraClass> PeEsquerdo = new ArrayList<>();

    private int LHal, LMet1, LMet2, LMet3, LMid, LCal1, LCal2;
    private int RHal, RMet1, RMet2, RMet3, RMid, RCal1, RCal2;

    private float LHum, LTemp, RHum, RTemp;

    private int RSum, LSum;

    private View CP_esq_ball, CP_dir_ball;

    private View LHal_ball, LMet1_ball, LMet2_ball, LMet3_ball, LMid_ball, LCal1_ball, LCal2_ball;
    private View RHal_ball, RMet1_ball, RMet2_ball, RMet3_ball, RMid_ball, RCal1_ball, RCal2_ball;

    Boolean data_dir_esq = false;

    private LineGraphSeries<DataPoint> seriesL;
    private LineGraphSeries<DataPoint> seriesR;

    public TextView messageLHal;
    public TextView messageLMet1;
    public TextView messageLMet2;
    public TextView messageLMet3;
    public TextView messageLMid;
    public TextView messageLCal1;
    public TextView messageLCal2;

    public TextView messageRHal;
    public TextView messageRMet1;
    public TextView messageRMet2;
    public TextView messageRMet3;
    public TextView messageRMid;
    public TextView messageRCal1;
    public TextView messageRCal2;

    private int MaxLHal, MaxLMet1, MaxLMet2, MaxLMet3, MaxLMid, MaxLCal1, MaxLCal2;
    private int MaxRHal, MaxRMet1, MaxRMet2, MaxRMet3, MaxRMid, MaxRCal1, MaxRCal2;

    private int auxL, auxR = 0 ;
    private int CP_Rstart, CP_Lstart, CP_Rstop, CP_Lstop;
    private boolean Rset, Lset = false;
    private int no_passos = 0;

    private TextView tapoio_esq, tapoio_dir, passos_text;

    private ProgressBar progressBar;
    private TextView Lpercentage, Rpercentage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.chat_bt);

        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        frameLayout = (FrameLayout) findViewById(R.id.map_container);
        dataLayout = (FrameLayout) findViewById(R.id.data_container);
        graphLayout = (FrameLayout) findViewById(R.id.graph_container);

        getLayoutInflater().inflate(R.layout.feet_map, frameLayout);
        getLayoutInflater().inflate(R.layout.data, dataLayout);
        getLayoutInflater().inflate(R.layout.graphs,graphLayout);

        frameLayout.setVisibility(View.VISIBLE);
        dataLayout.setVisibility(View.INVISIBLE);
        graphLayout.setVisibility(View.INVISIBLE);

        tvTimer = (TextView) findViewById(R.id.tvTimer);

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

        seriesL = new LineGraphSeries<DataPoint>();
        seriesR = new LineGraphSeries<DataPoint>();

        GraphView graphesq = (GraphView) findViewById(R.id.graph);
        GraphView graphdir = (GraphView) findViewById(R.id.graph2);

        graphesq.addSeries(seriesL);
        graphdir.addSeries(seriesR);

        seriesL.setColor(Color.GREEN);
        seriesL.setTitle("Soma do esquerdo");

        seriesR.setColor(Color.BLUE);
        seriesR.setTitle("Soma do direito");

        //customize viewportesq
        Viewport viewportesq = graphesq.getViewport();
        viewportesq.setYAxisBoundsManual(true);
        viewportesq.setXAxisBoundsManual(true);
        viewportesq.setMinY(0);
        viewportesq.setMaxY(1000);
        viewportesq.setMinX(0);
        viewportesq.setMaxX(10);

        //customize viewportdir
        Viewport viewportdir = graphdir.getViewport();
        viewportdir.setYAxisBoundsManual(true);
        viewportdir.setXAxisBoundsManual(true);
        viewportdir.setMinY(0);
        viewportdir.setMaxY(1000);
        viewportdir.setMinX(0);
        viewportdir.setMaxX(10);

        Bundle bn = getIntent().getExtras();
        String mDeviceAddressLeft = bn.getString("esq");
        String mDeviceAddressRight = bn.getString("dir");

        //Create connection for device
        BTConnectionL = new BluetoothConnectionActivity(this, mDeviceAddressLeft, mHandlerEsq);
        BTConnectionL.execute();

        //Create connection for device
        BTConnectionR = new BluetoothConnectionActivity(this, mDeviceAddressRight, mHandlerDir);
        BTConnectionR.execute();

        //messageTextR = findViewById(R.id.Data_collected_dir);
        //messageTextL = findViewById(R.id.Data_collected_esq);

        messageLHal = findViewById(R.id.LHalMax);
        messageLMet1 = findViewById(R.id.LMet1Max);
        messageLMet2 = findViewById(R.id.LMet2Max);
        messageLMet3 = findViewById(R.id.LMet3Max);
        messageLMid = findViewById(R.id.LMidMax);
        messageLCal1 = findViewById(R.id.LCal1Max);
        messageLCal2 = findViewById(R.id.LCal2Max);

        messageRHal = findViewById(R.id.RHalMax);
        messageRMet1 = findViewById(R.id.RMet1Max);
        messageRMet2 = findViewById(R.id.RMet2Max);
        messageRMet3 = findViewById(R.id.RMet3Max);
        messageRMid = findViewById(R.id.RMidMax);
        messageRCal1 = findViewById(R.id.RCal1Max);
        messageRCal2 = findViewById(R.id.RCal2Max);

        MaxLHal = MaxLMet1 = MaxLMet2 = MaxLMet3 = MaxLMid = MaxLCal1 = MaxLCal2 = MaxRHal = MaxRMet1 = MaxRMet2 = MaxRMet3 = MaxRMid = MaxRCal1 = MaxRCal2 = 0;

        tapoio_esq = findViewById(R.id.temp_apoioesq);
        tapoio_dir = findViewById(R.id.temp_apoiodir);
        passos_text = findViewById(R.id.no_de_passos);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(100);
        Lpercentage = (TextView) findViewById(R.id.left_percentage);
        Rpercentage = (TextView) findViewById(R.id.right_percentage);

    }

    public static String getDateFromMillis(long d) {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return df.format(d);
    }

    private final Handler mHandlerDir = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (data_dir_esq) {
                String s = (String) msg.obj;
                //Log.d("TAG", "Mensagem lida R: " + s);

                String delimiter = "\\|";
                String[] arrofs = s.split(delimiter);

                RHal = Integer.parseInt(arrofs[1]);
                RMet1 = Integer.parseInt(arrofs[2]);
                RMet2 = Integer.parseInt(arrofs[3]);
                RMet3 = Integer.parseInt(arrofs[4]);
                RMid = Integer.parseInt(arrofs[5]);
                RCal1 = Integer.parseInt(arrofs[6]);
                RCal2 = Integer.parseInt(arrofs[7]);
                RTemp = Float.parseFloat(arrofs[8]);
                RHum = Float.parseFloat(arrofs[9]);


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

                /*RHal_ball.getBackground().setColorFilter(Color.parseColor(DecToHexa((int) (-0.17 * RHal + 255))), PorterDuff.Mode.MULTIPLY);
                RMet1_ball.getBackground().setColorFilter(Color.parseColor(DecToHexa((int) (-0.17 * RMet1 + 255))), PorterDuff.Mode.MULTIPLY);
                RMet2_ball.getBackground().setColorFilter(Color.parseColor(DecToHexa((int) (-0.17 * RMet2 + 255))), PorterDuff.Mode.MULTIPLY);
                RMet3_ball.getBackground().setColorFilter(Color.parseColor(DecToHexa((int) (-0.17 * RMet3 + 255))), PorterDuff.Mode.MULTIPLY);
                RMid_ball.getBackground().setColorFilter(Color.parseColor(DecToHexa((int) (-0.17 * RMid + 255))), PorterDuff.Mode.MULTIPLY);
                RCal1_ball.getBackground().setColorFilter(Color.parseColor(DecToHexa((int) (-0.17 * RCal1 + 255))), PorterDuff.Mode.MULTIPLY);
                RCal2_ball.getBackground().setColorFilter(Color.parseColor(DecToHexa((int) (-0.17 * RCal2 + 255))), PorterDuff.Mode.MULTIPLY);*/

                RHal_ball.getBackground().setColorFilter(ball_color(RHal), PorterDuff.Mode.MULTIPLY);
                RMet1_ball.getBackground().setColorFilter(ball_color(RMet1), PorterDuff.Mode.MULTIPLY);
                RMet2_ball.getBackground().setColorFilter(ball_color(RMet2), PorterDuff.Mode.MULTIPLY);
                RMet3_ball.getBackground().setColorFilter(ball_color(RMet3), PorterDuff.Mode.MULTIPLY);
                RMid_ball.getBackground().setColorFilter(ball_color(RMid), PorterDuff.Mode.MULTIPLY);
                RCal1_ball.getBackground().setColorFilter(ball_color(RCal1), PorterDuff.Mode.MULTIPLY);
                RCal2_ball.getBackground().setColorFilter(ball_color(RCal2), PorterDuff.Mode.MULTIPLY);

                if (RSum != 0) {

                    //Calculate center of pressure coordinates

                    CP_dir_ball.setVisibility(View.VISIBLE);

                    CP_dir_ball.setX((RHal_ball.getX() * RHal + RMet1_ball.getX() * RMet1 + RMet2_ball.getX() * RMet2 + RMet3_ball.getX() * RMet3 + RMid_ball.getX() * RMid + RCal1_ball.getX() * RCal1 + RCal2_ball.getX() * RCal2) / RSum);
                    CP_dir_ball.setY((RHal_ball.getY() * RHal + RMet1_ball.getY() * RMet1 + RMet2_ball.getY() * RMet2 + RMet3_ball.getY() * RMet3 + RMid_ball.getY() * RMid + RCal1_ball.getY() * RCal1 + RCal2_ball.getY() * RCal2) / RSum);

                    /*progressBar.setProgress(LSum / (RSum + LSum)*100);
                    Lpercentage.setText((LSum / (RSum + LSum)*100) + "%");
                    Rpercentage.setText((RSum / (RSum + LSum)*100) + "%");*/

                } else {
                    CP_dir_ball.setVisibility(View.INVISIBLE);

                }

                if (CP_dir_ball.getVisibility() == View.VISIBLE && auxR == 0) {
                    auxR = 1;
                    CP_Rstart = (int) SystemClock.elapsedRealtime();
                } else if (CP_dir_ball.getVisibility() == View.INVISIBLE && auxR == 1) {
                    auxR = 0;
                    CP_Rstop = (int) SystemClock.elapsedRealtime();
                    Rset = true;
                    int CP_R = CP_Rstop - CP_Rstart;
                    tapoio_dir.setText("Direito: " + CP_R);
                }

                if (Rset || Lset) {
                    no_passos = no_passos + 1;
                    Rset = false;
                    Lset = false;
                    passos_text.setText("" + no_passos);
                }


                LeituraClass val = new LeituraClass();

                val.Hal_data = RHal;
                val.Met1_data = RMet1;
                val.Met2_data = RMet2;
                val.Met3_data = RMet3;
                val.Mid_data = RMid;
                val.Cal1_data = RCal1;
                val.Cal2_data = RCal2;
                val.Temp_data = RTemp;
                val.Humid_data = RHum;

                val.readingDate = SystemClock.elapsedRealtime() - startTime;

                PeDireito.add(val);

                //messageTextR.setText(s);

                seriesR.appendData(new DataPoint((double) (SystemClock.elapsedRealtime() - startTime)/1000,(double) RSum),true,10000);

                if (RHal > MaxRHal) {
                    MaxRHal = RHal;
                    messageRHal.setText(String.format("%d", MaxRHal));
                    if (MaxRHal > 300) {
                        messageRHal.setBackgroundColor(Color.parseColor("#E38282"));
                    }
                }

                if (RMet1 > MaxRMet1) {
                    MaxRMet1 = RMet1;
                    messageRMet1.setText(String.format("%d", MaxRMet1));
                    if (MaxRMet1 > 300) {
                        messageRMet1.setBackgroundColor(Color.parseColor("#E38282"));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                        } else {
                            v.vibrate(500);
                        }
                    }
                }

                if (RMet2 > MaxRMet2) {
                    MaxRMet2 = RMet2;
                    messageRMet2.setText(String.format("%d", MaxRMet2));
                    if (MaxRMet2 > 300) {
                        messageRMet2.setBackgroundColor(Color.parseColor("#E38282"));
                    }
                }

                if (RMet3 > MaxRMet3) {
                    MaxRMet3 = RMet3;
                    messageRMet3.setText(String.format("%d", MaxRMet3));
                    if (MaxRMet3 > 300) {
                        messageRMet3.setBackgroundColor(Color.parseColor("#E38282"));
                    }
                }

                if (RMid > MaxRMid) {
                    MaxRMid = RMid;
                    messageRMid.setText(String.format("%d", MaxRMid));
                    if (MaxRMid > 300) {
                        messageRMid.setBackgroundColor(Color.parseColor("#E38282"));
                    }
                }

                if (RCal1 > MaxRCal1) {
                    MaxRCal1 = RCal1;
                    messageRCal1.setText(String.format("%d", MaxRCal1));
                    if (MaxRCal1 > 300) {
                        messageRCal1.setBackgroundColor(Color.parseColor("#E38282"));
                    }
                }

                if (RCal2 > MaxRCal2) {
                    MaxRCal2 = RCal2;
                    messageRCal2.setText(String.format("%d", MaxRCal2));
                    if (MaxRCal2 > 300) {
                        messageRCal2.setBackgroundColor(Color.parseColor("#E38282"));
                    }
                }

            }
        }
    };


    private final Handler mHandlerEsq = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (data_dir_esq) {
                String s = (String) msg.obj;
                //Log.d("TAG", "Mensagem lida L: " + s);

                String delimiter = "\\|";
                String[] arrofs = s.split(delimiter);

                LHal = Integer.parseInt(arrofs[1]);
                LMet1 = Integer.parseInt(arrofs[2]);
                LMet2 = Integer.parseInt(arrofs[3]);
                LMet3 = Integer.parseInt(arrofs[4]);
                LMid = Integer.parseInt(arrofs[5]);
                LCal1 = Integer.parseInt(arrofs[6]);
                LCal2 = Integer.parseInt(arrofs[7]);
                LTemp = Float.parseFloat(arrofs[8]);
                LHum = Float.parseFloat(arrofs[9]);



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

                LHal_ball.getBackground().setColorFilter(ball_color(LHal), PorterDuff.Mode.MULTIPLY);
                LMet1_ball.getBackground().setColorFilter(ball_color(LMet1), PorterDuff.Mode.MULTIPLY);
                LMet2_ball.getBackground().setColorFilter(ball_color(LMet2), PorterDuff.Mode.MULTIPLY);
                LMet3_ball.getBackground().setColorFilter(ball_color(LMet3), PorterDuff.Mode.MULTIPLY);
                LMid_ball.getBackground().setColorFilter(ball_color(LMid), PorterDuff.Mode.MULTIPLY);
                LCal1_ball.getBackground().setColorFilter(ball_color(LCal1), PorterDuff.Mode.MULTIPLY);
                LCal2_ball.getBackground().setColorFilter(ball_color(LCal2), PorterDuff.Mode.MULTIPLY);
                //Log.d("TAG","" + Color.rgb(255,0,0));


                //Calculate Center of pressure

                if (LSum != 0) {

                    CP_esq_ball.setVisibility(View.VISIBLE);

                    CP_esq_ball.setX((LHal_ball.getX() * LHal + LMet1_ball.getX() * LMet1 + LMet2_ball.getX() * LMet2 + LMet3_ball.getX() * LMet3 + LMid_ball.getX() * LMid + LCal1_ball.getX() * LCal1 + LCal2_ball.getX() * LCal2) / LSum);
                    CP_esq_ball.setY((LHal_ball.getY() * LHal + LMet1_ball.getY() * LMet1 + LMet2_ball.getY() * LMet2 + LMet3_ball.getY() * LMet3 + LMid_ball.getY() * LMid + LCal1_ball.getY() * LCal1 + LCal2_ball.getY() * LCal2) / LSum);

                    /*progressBar.setProgress(LSum / (RSum + LSum)*100);
                    Lpercentage.setText((LSum / (RSum + LSum)*100) + "%");
                    Rpercentage.setText((RSum / (RSum + LSum)*100) + "%");*/

                } else {
                    CP_esq_ball.setVisibility(View.INVISIBLE);

                }

                if (CP_esq_ball.getVisibility() == View.VISIBLE && auxL == 0) {
                    auxL = 1;
                    CP_Lstart = (int) SystemClock.elapsedRealtime();
                } else if (CP_esq_ball.getVisibility() == View.INVISIBLE && auxL == 1) {
                    auxL = 0;
                    CP_Lstop = (int) SystemClock.elapsedRealtime();
                    Lset = true;
                    int CP_L = CP_Lstop - CP_Lstart;
                    tapoio_esq.setText("Esquerdo: " + CP_L);
                }

                if (Rset || Lset) {
                    no_passos = no_passos + 1;
                    Rset = false;
                    Lset = false;
                    passos_text.setText("" + no_passos);
                }

                LeituraClass val = new LeituraClass();

                val.Hal_data = LHal;
                val.Met1_data = LMet1;
                val.Met2_data = LMet2;
                val.Met3_data = LMet3;
                val.Mid_data = LMid;
                val.Cal1_data = LCal1;
                val.Cal2_data = LCal2;
                val.Temp_data = LTemp;
                val.Humid_data = LHum;

                val.readingDate = SystemClock.elapsedRealtime() - startTime;

                PeEsquerdo.add(val);

                //messageTextL.setText(s);

                seriesL.appendData(new DataPoint((double) (SystemClock.elapsedRealtime() - startTime)/1000, (double) LSum),true,10000);

                if (LHal > MaxLHal) {
                    MaxLHal = LHal;
                    messageLHal.setText(String.format("%d", MaxLHal));
                    if (MaxLHal > 300) {
                        messageLHal.setBackgroundColor(Color.parseColor("#E38282"));
                    }
                }

                if (LMet1 > MaxLMet1) {
                    MaxLMet1 = LMet1;
                    messageLMet1.setText(String.format("%d", MaxLMet1));
                    if (MaxLMet1 > 300) {
                        messageLMet1.setBackgroundColor(Color.parseColor("#E38282"));
                    }
                }

                if (LMet2 > MaxLMet2) {
                    MaxLMet2 = LMet2;
                    messageLMet2.setText(String.format("%d", MaxLMet2));
                    if (MaxLMet2 > 300) {
                        messageLMet2.setBackgroundColor(Color.parseColor("#E38282"));
                    }
                }

                if (LMet3 > MaxLMet3) {
                    MaxLMet3 = LMet3;
                    messageLMet3.setText(String.format("%d", MaxLMet3));
                    if (MaxLMet3 > 300) {
                        messageLMet3.setBackgroundColor(Color.parseColor("#E38282"));
                    }
                }

                if (LMid > MaxLMid) {
                    MaxLMid = LMid;
                    messageLMid.setText(String.format("%d", MaxLMid));
                    if (MaxLMid > 300) {
                        messageLMid.setBackgroundColor(Color.parseColor("#E38282"));
                    }
                }

                if (LCal1 > MaxLCal1) {
                    MaxLCal1 = LCal1;
                    messageLCal1.setText(String.format("%d", MaxLCal1));
                    if (MaxLCal1 > 300) {
                        messageLCal1.setBackgroundColor(Color.parseColor("#E38282"));
                    }
                }

                if (LCal2 > MaxLCal2) {
                    MaxLCal2 = LCal2;
                    messageLCal2.setText(String.format("%d", MaxLCal2));
                    if (MaxLCal2 > 300) {
                        messageLCal2.setBackgroundColor(Color.parseColor("#E38282"));
                    }
                }

            }
        }
    };


    int ball_color (int n) {
        int change;

        change = (int) (-0.5 * n + 255);
        int new_color;

        if (change <= 0) {
            new_color = Color.rgb(255, 0, 0);
        } else {
            new_color = Color.rgb(255, change, 0);
        }
        return new_color;
    }


        /*// char array to store hexadecimal number
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


        //Log.d("TAG","#FF" + Hexa + "00");*/



    @Override
    public void onBackPressed() {
        if (data_dir_esq) {
            Toast.makeText(this, "Parar a aquisição primeiro", Toast.LENGTH_SHORT).show();
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Encerrar atividade");
            alertDialog.setMessage("Deseja encerrar a atividade e a conexão com as palmilhas?");
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Sim",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            BTConnectionL.disconnect();
                            BTConnectionR.disconnect();
                            finish();
                        }
                    });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Não",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            alertDialog.show();
        }
    }



    public void start_map(View view) {
        dataLayout.setVisibility(View.INVISIBLE);
        graphLayout.setVisibility(View.INVISIBLE);
        frameLayout.setVisibility(View.VISIBLE);
    }

    public void Dados(View view) {
        frameLayout.setVisibility(View.INVISIBLE);
        graphLayout.setVisibility(View.INVISIBLE);
        dataLayout.setVisibility(View.VISIBLE);
    }

    public void start_graph(View view) {
        frameLayout.setVisibility(View.INVISIBLE);
        graphLayout.setVisibility(View.VISIBLE);
        dataLayout.setVisibility(View.INVISIBLE);
    }

    public void Start_Read(View view) {

        data_dir_esq = true;
        btnStart.setEnabled(false);
        btnStop.setEnabled(true);
        //thread.start();

        if (btnStart.getText().equals("Start")) {
            startTime = SystemClock.elapsedRealtime();
            customHandler.postDelayed(updateTimerThread, 0);
        } else {
            tvTimer.setVisibility(View.VISIBLE);

        }


    }

    public void Stop_read(View view) {
        data_dir_esq = false;
        //messageTextL.setText("Reading stopped");
        //messageTextR.setText("Reading stopped");

        btnStart.setEnabled(true);
        btnStop.setEnabled(false);
        btnStart.setText("Resume");
        tvTimer.setVisibility(View.INVISIBLE);

        //customHandler.removeCallbacks(updateTimerThread);

    }

    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            customHandler.postDelayed(this, 1000 - (SystemClock.elapsedRealtime() - startTime) % 1000);
            timeInMilliseconds = SystemClock.elapsedRealtime() - startTime;
            tvTimer.setText(getDateFromMillis(timeInMilliseconds));

            if (LSum != 0 || RSum != 0) {
                progressBar.setProgress(LSum / (RSum + LSum) * 100);
                Lpercentage.setText((LSum / (RSum + LSum) * 100) + "%");
                Rpercentage.setText((RSum / (RSum + LSum) * 100) + "%");
            }
        }
    };

    /*private Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (data_dir_esq) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (LSum != 0 || RSum != 0) {
                            progressBar.setProgress(LSum / (RSum + LSum) * 100);
                            Lpercentage.setText("" + (LSum / (RSum + LSum) * 100) + "%");
                            Rpercentage.setText("" + (RSum / (RSum + LSum) * 100) + "%");
                        }
                    }
                });
            }
        }
    });*/

    public void save_data(View view) {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (checkPermission())
            {
                // Code for above or equal 23 API Oriented Device
                // Your Permission granted already .Do next code
                savedata_tofile();
            } else {
                requestPermission(); // Code for permission
            }
        }
        else
        {
            // Code for Below 23 API Oriented Device
            // Do next code
            savedata_tofile();
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }

    private void savedata_tofile() {

        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM-dd,HH:mm");


        String name = ProfileActivity.Nome;
        String age = ProfileActivity.Idade;
        String gender = ProfileActivity.gender;
        String height = ProfileActivity.Altura;
        String Weight = ProfileActivity.Peso;
        String foot_size = ProfileActivity.Numero_Sapato;

        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {

            String root = Environment.getExternalStorageDirectory().toString();
            File dir = new File(root + "/FAMEST");

            if (!dir.exists()) {

                dir.mkdir();

            }
            File file = new File(dir, name + " on " + dateFormat.format(c.getTime()) +".txt");
            if (file.exists()){
                file.delete();
            } else {


                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    //ObjectOutputStream oos = new ObjectOutputStream(fos);
                    //fos = openFileOutput("example.txt", MODE_PRIVATE);
                    //fos.write(PeEsquerdo.toString().getBytes());
                    //fos.write(PeDireito.toString().getBytes());
                    //fos.write(test.getBytes());
                    fos.write(("Nome: " + name + System.getProperty("line.separator")+ "Idade: " + age + " anos" + System.getProperty("line.separator") + "Sexo: " + gender
                            + System.getProperty("line.separator")+ "Altura: " + height+ " cm" + System.getProperty("line.separator")+ "Peso: " + Weight + " Kg"+
                            System.getProperty("line.separator") + "Nº pé: " + foot_size + System.getProperty("line.separator")+ System.getProperty("line.separator")).getBytes());

                    fos.write(("Data from left foot: Time, Hal, Met1, Met2, Met3, Mid, Cal1, Cal2" + System.getProperty("line.separator")).getBytes());
                    for (int i = 0; i < PeEsquerdo.size(); i++) {
                        fos.write(PeEsquerdo.get(i).toString().getBytes());
                    }

                    fos.write((System.getProperty("line.separator")).getBytes());

                    fos.write(("Data from right foot: Time, Hal, Met1, Met2, Met3, Mid, Cal1, Cal2" + System.getProperty("line.separator")).getBytes());

                    for (int i = 0; i < PeDireito.size(); i++) {
                        fos.write(PeDireito.get(i).toString().getBytes());
                    }

                    fos.close();

                    Toast.makeText(this, "Data saved", Toast.LENGTH_SHORT).show();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error saving data", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error saving data", Toast.LENGTH_SHORT).show();

                }
            }
        } else {
            Toast.makeText(this, "Storage could not be found", Toast.LENGTH_SHORT).show();
        }
    }
}
