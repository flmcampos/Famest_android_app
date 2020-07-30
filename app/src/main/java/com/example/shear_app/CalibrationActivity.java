package com.example.shear_app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Telephony;
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
    TextView resultado_peso;

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

    private double peso_calculado;

    Handler customHandlerC = new Handler();

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
            e.printStackTrace();
        }
        //Create connection for device
        BTConnectionCR = new BluetoothConnectionActivity(this, mDeviceAddressRightC, mHandlerDirC);
        try {
            BTConnectionCR.execute();
        } catch (Exception e) {
            BTConnectionCL.disconnect();
            BTConnectionCR.disconnect();
            e.printStackTrace();
        }

        read = findViewById(R.id.goToReaderActivity);

        calibrationTimer = (TextView) findViewById(R.id.calibrationTimer);
        resultado_peso = (TextView) findViewById(R.id.weight_result);
    }

    //função que permite receber a informação enviada pelo DAQ da palmilha direita
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

                SumC =+ (RSumC+LSumC);

                Log.d("TAG","" + LSumC + " + " + RSumC + " = " + SumC);

                length =+1;
            }
        }
    };

    //função que permite receber a informação enviada pelo DAQ da palmilha esquerda
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

                //SumC =+ LSumC;

                //length =+1;
            }
        }
    };

    //Botão que irá iniciar a recolha de dados para a calibração e um relógio em contagem decrescente
    //sendo no final da contagem apresentado o resultado da calibração
    public void Start_ReadC(View view) {

        data_dir_esqC = true;
        btnStartC.setEnabled(false);

        startTimeC = SystemClock.elapsedRealtime();
        customHandlerC.postDelayed(updateTimerThread, 0);

        calibrationTimer.setVisibility(View.VISIBLE);
    }

    //função que permite apresentar um relógio em contagem decrescente. Quando a contagem
    //chega ao 0, é apresentado o valor da calibração e torna acessível o botão que
    //iniciará a sessão de monitorização de pressões plantares
    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            customHandlerC.postDelayed(this, 1000- (SystemClock.elapsedRealtime() - startTimeC)%1000);
            timeInMillisecondsC = 15- (SystemClock.elapsedRealtime() - startTimeC)/1000;

            if (timeInMillisecondsC == 0) {
                calibrationTimer.setText("Calibração realizada");
                data_dir_esqC = false;
                read.setEnabled(true);
                peso_calculado = (double)(SumC / length)*71.4e-4;
                resultado_peso.setText(String.format("Peso calculado = %.3s Kgf", peso_calculado));

                BTConnectionCL.disconnect();
                BTConnectionCR.disconnect();

            } else if (timeInMillisecondsC>0){
                calibrationTimer.setText("" + timeInMillisecondsC);
            }
        }
    };

    //No final da calibração, este botão é ativado, encaminhando o utilizador para a atividade de
    //vizualização da sessão de monitorização de pressões plantares
    public void Reader(View view){

        Bundle bn = getIntent().getExtras();
        bn.getString("esq");
        bn.getString("dir");
        Intent i = new Intent(this, ReaderActivity.class);
        i.putExtras(bn);

        startActivity(i);
        finish();
    }
}
