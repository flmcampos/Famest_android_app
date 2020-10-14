package com.example.shear_app;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Telephony;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

public class CalibrationActivity extends AppCompatActivity {

    TextView calibrationTimer;
    TextView resultado_peso;
    TextView aviso;

    long startTimeC_dir = 0, timeInMillisecondsC_dir = 12;
    long startTimeC_esq = 0, timeInMillisecondsC_esq = 12;

    public static BluetoothConnectionActivity BTConnectionCL;
    public static BluetoothConnectionActivity BTConnectionCR;

    Button btnStartC;

    private Button read;

    Boolean data_dirC = false;
    Boolean data_esqC = false;

    private int RHalC, RMet1C, RMet2C, RMet3C, RMidC, RCal1C, RCal2C;
    private int LHalC, LMet1C, LMet2C, LMet3C, LMidC, LCal1C, LCal2C;

    private int MaxLHal, MaxLMet1, MaxLMet2, MaxLMet3, MaxLMid, MaxLCal1, MaxLCal2;
    private int MaxRHal, MaxRMet1, MaxRMet2, MaxRMet3, MaxRMid, MaxRCal1, MaxRCal2;

    private int SumC = 0, lengthL, lengthR = 0;
    int[] listR= new int[lengthR];
    int[] listL= new int[lengthL];

    private int maxR1,maxR2,maxR3,maxL1,maxL2,maxL3= 0;

    private int RSumC, LSumC;

    private double peso_calculado;

    Handler customHandlerC_dir = new Handler();
    Handler customHandlerC_esq = new Handler();

    //Início da atividade de calibração, são chamadas as funções que permitem executar duas
    //conexões BT distintas (com DAQs da palmilha esquerda e direita)
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
        try {
            BTConnectionCL.execute();
        } catch (Exception e) {
            BTConnectionCL.disconnect();
            finish();
            e.printStackTrace();
        }
        //Create connection for device
        BTConnectionCR = new BluetoothConnectionActivity(this, mDeviceAddressRightC, mHandlerDirC);
        try {
            BTConnectionCR.execute();
        } catch (Exception e) {
            BTConnectionCL.disconnect();
            BTConnectionCR.disconnect();
            finish();
            e.printStackTrace();
        }

        MaxLHal = MaxLMet1 = MaxLMet2 = MaxLMet3 = MaxLMid = MaxLCal1 = MaxLCal2 = MaxRHal = MaxRMet1 = MaxRMet2 = MaxRMet3 = MaxRMid = MaxRCal1 = MaxRCal2 = 0;

        read = findViewById(R.id.goToReaderActivity);

        calibrationTimer = (TextView) findViewById(R.id.calibrationTimer);
        resultado_peso = (TextView) findViewById(R.id.weight_result);
        aviso = (TextView) findViewById(R.id.msg_aviso);
    }

    //função que permite receber a informação enviada pelo DAQ da palmilha direita
    private final Handler mHandlerDirC = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {


            if (data_dirC) {
                String sC = (String) msg.obj;
                Log.d("TAG", "Mensagem lida R: " + sC);

                String delimiter = "\\|";
                String[] arrofsC = sC.split(delimiter);

                if (arrofsC.length >= 8) {
                    RHalC = (int) Integer.parseInt(arrofsC[1]);
                    RMet1C = (int) Integer.parseInt(arrofsC[2]);
                    RMet2C = (int) Integer.parseInt(arrofsC[3]);
                    RMet3C = (int) Integer.parseInt(arrofsC[4]);
                    RMidC = (int) Integer.parseInt(arrofsC[5]);
                    RCal1C = (int) Integer.parseInt(arrofsC[6]);
                    RCal2C = (int) Integer.parseInt(arrofsC[7]);

                    RSumC = RHalC + RMet1C + RMet2C + RMet3C + RMidC + RCal1C + RCal2C;

                    listR = add_element(lengthR, listR, RSumC);
                    lengthR = listR.length;


                    //SumC =+ (RSumC+LSumC);

                    Log.d("TAG5", "" + Arrays.toString(listR));

                    //length =+1;
                } else {
                    Toast.makeText(CalibrationActivity.this, "Está a ocorrer um erro na ligação Bluetooth direita. Renicie app e dispositivos de aquisição", Toast.LENGTH_LONG).show();
                    BTConnectionCL.disconnect();
                    BTConnectionCR.disconnect();
                    finish();
                }
            }
        }
    };

    //função que permite receber a informação enviada pelo DAQ da palmilha esquerda
    private final Handler mHandlerEsqC = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (data_esqC) {
                String sC = (String) msg.obj;
                Log.d("TAG", "Mensagem lida L: " + sC);

                String delimiter = "\\|";
                String[] arrofsC = sC.split(delimiter);

                if (arrofsC.length >= 8) {
                    LHalC = Integer.parseInt(arrofsC[1]);
                    LMet1C = Integer.parseInt(arrofsC[2]);
                    LMet2C = Integer.parseInt(arrofsC[3]);
                    LMet3C = Integer.parseInt(arrofsC[4]);
                    LMidC = Integer.parseInt(arrofsC[5]);
                    LCal1C = Integer.parseInt(arrofsC[6]);
                    LCal2C = Integer.parseInt(arrofsC[7]);

                    LSumC = (LHalC) + (LMet1C) + (LMet2C) + (LMet3C) + (LMidC) + (LCal1C) + (LCal2C);

                    listL = add_element(lengthL, listL, LSumC);
                    lengthL = listL.length;

                    Log.d("TAG6", "" + Arrays.toString(listL));
                } else {
                    Toast.makeText(CalibrationActivity.this, "Está a ocorrer um erro na ligação Bluetooth esquerda. Renicie app e dispositivos de aquisição", Toast.LENGTH_LONG).show();
                    BTConnectionCL.disconnect();
                    BTConnectionCR.disconnect();
                    finish();
                }
            }
        }
    };



    public static int[] add_element(int n, int myarray[], int ele)
    {
        int i;

        int newArray[] = new int[n + 1];
        //copy original array into new array
        for (i = 0; i < n; i++)
        newArray[i] = myarray[i];

        //add element to the new array
        newArray[n] = ele;

        return newArray;
    }

    //Botão que irá iniciar a recolha de dados para a calibração e um relógio em contagem decrescente
    //sendo no final da contagem apresentado o resultado da calibração
    public void Start_ReadC(View view) {
        if (btnStartC.getText().equals("Direito")) {

            data_dirC = true;
            btnStartC.setEnabled(false);

            startTimeC_dir = SystemClock.elapsedRealtime();
            customHandlerC_dir.postDelayed(updateTimerThread_dir, 0);
            aviso.setText("Por favor não se mexa até a calibração terminar");

        } else if (btnStartC.getText().equals("Esquerdo")){
            data_esqC = true;
            btnStartC.setEnabled(false);

            startTimeC_esq = SystemClock.elapsedRealtime();
            customHandlerC_esq.postDelayed(updateTimerThread_esq, 0);
            aviso.setText("Por favor não se mexa até a calibração terminar");
        }
    }

    //função que permite apresentar um relógio em contagem decrescente. Quando a contagem
    //chega ao 0, é apresentado o valor da calibração e torna acessível o botão que
    //iniciará a sessão de monitorização de pressões plantares
    private Runnable updateTimerThread_dir = new Runnable() {
        public void run() {
            customHandlerC_dir.postDelayed(this, 1000- (SystemClock.elapsedRealtime() - startTimeC_dir)%1000);
            timeInMillisecondsC_dir = 12- (SystemClock.elapsedRealtime() - startTimeC_dir)/1000;

            if (timeInMillisecondsC_dir == 8) {
                data_dirC = false;
                for (int i =0; i<lengthR; i++) {
                    if (RSumC > maxR1) {
                        maxR1 = RSumC;
                    }
                }
                lengthR = 0;
                listR= new int[lengthR];
                data_dirC = true;
            }

            if (timeInMillisecondsC_dir == 4) {
                data_dirC = false;
                for (int i =0; i<lengthR; i++) {
                    if (RSumC > maxR2) {
                        maxR2 = RSumC;
                    }
                }
                lengthR = 0;
                listR= new int[lengthR];
                data_dirC = true;
            }

            if (timeInMillisecondsC_dir == 0) {
                aviso.setText("Agora apoie-se apenas no pé esquerdo");
                calibrationTimer.setText("Calibração realizada");
                data_dirC = false;

                for (int i =0; i<lengthR; i++) {
                    if (RSumC > maxR3) {
                        maxR3 = RSumC;
                    }
                }

                btnStartC.setText("Esquerdo");
                btnStartC.setEnabled(true);
                calibrationTimer.setText("" + timeInMillisecondsC_esq);



            } else if (timeInMillisecondsC_dir>0){
                calibrationTimer.setText("" + timeInMillisecondsC_dir);
            }
        }
    };

    private Runnable updateTimerThread_esq = new Runnable() {
        public void run() {
            customHandlerC_esq.postDelayed(this, 1000- (SystemClock.elapsedRealtime() - startTimeC_esq)%1000);
            timeInMillisecondsC_esq = 12- (SystemClock.elapsedRealtime() - startTimeC_esq)/1000;

            if (timeInMillisecondsC_esq == 8) {
                data_esqC = false;
                for (int i =0; i<lengthL; i++) {
                    if (LSumC > maxL1) {
                        maxL1 = LSumC;
                    }
                }
                lengthL = 0;
                listL= new int[lengthL];
                data_esqC = true;
            }

            if (timeInMillisecondsC_esq == 4) {
                data_esqC = false;
                for (int i =0; i<lengthL; i++) {
                    if (LSumC > maxL2) {
                        maxL2 = LSumC;
                    }
                }
                lengthL = 0;
                listL= new int[lengthL];
                data_esqC = true;
            }

            if (timeInMillisecondsC_esq == 0) {
                aviso.setVisibility(View.INVISIBLE);
                calibrationTimer.setText("Calibração realizada");
                data_esqC = false;
                read.setEnabled(true);

                for (int i =0; i<lengthL; i++) {
                    if (LSumC > maxL3) {
                        maxL3 = LSumC;
                    }
                }

                SumC = (maxR1 + maxR2 + maxR3+maxL1+maxL2+maxL3)/3;
                peso_calculado = (double) SumC*3*7*71.4e-3/9.8; //3.65 corresponde ao fator de calibração
                resultado_peso.setText(String.format("Peso calculado = %.4s Kgf", peso_calculado));

            } else if (timeInMillisecondsC_esq>0){
                calibrationTimer.setText("" + timeInMillisecondsC_esq);
            }
        }
    };

    //No final da calibração, este botão é ativado, encaminhando o utilizador para a atividade de
    //vizualização da sessão de monitorização de pressões plantares
    public void Reader(View view){

        BTConnectionCL.disconnect();
        BTConnectionCR.disconnect();
        Bundle bn = getIntent().getExtras();
        bn.getString("esq");
        bn.getString("dir");
        Intent i = new Intent(this, ReaderActivity.class);
        i.putExtras(bn);

        startActivity(i);

        finish();
    }
}
