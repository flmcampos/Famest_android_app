package com.example.FAMEST_app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

public class CalibrationActivity extends AppCompatActivity {

    TextView calibrationTimer;
    TextView resultado_peso;
    TextView aviso;

    long startTimeC = 0, timeInMillisecondsC = 15;

    public static BluetoothConnectionActivity BTConnectionCL;
    public static BluetoothConnectionActivity BTConnectionCR;

    Button btnStartC;

    private Button read;

    Boolean data_dir_esqC = false;

    private int maxR1=0,maxR2=0,maxR3=0,maxL1=0,maxL2=0,maxL3= 0;

    private int RHalC, RMet1C, RMet2C, RMet3C, RMidC, RCal1C, RCal2C;
    private int LHalC, LMet1C, LMet2C, LMet3C, LMidC, LCal1C, LCal2C;

    private int lengthL = 0, lengthR = 0;
    private double[] SumC = new double[3];
    int[] listR= new int[lengthR];
    int[] listL= new int[lengthL];


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

        read = findViewById(R.id.goToReaderActivity);

        calibrationTimer = (TextView) findViewById(R.id.calibrationTimer);
        resultado_peso = (TextView) findViewById(R.id.weight_result);
        aviso = (TextView) findViewById(R.id.msg_aviso);
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
            if (data_dir_esqC) {
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


                } else {
                    Toast.makeText(CalibrationActivity.this, "Está a ocorrer um erro na ligação Bluetooth esquerda. Renicie app e dispositivos de aquisição", Toast.LENGTH_LONG).show();
                    BTConnectionCL.disconnect();
                    BTConnectionCR.disconnect();
                    finish();
                }


                //length =+1;

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
            timeInMillisecondsC = 12- (SystemClock.elapsedRealtime() - startTimeC)/1000;

            if (timeInMillisecondsC == 8) {
                data_dir_esqC = false;
                for (int i =0; i<lengthL; i++) {
                    if (LSumC > maxL1) {
                        maxL1 = LSumC;
                    }
                }

                for (int i =0; i<lengthR; i++) {
                    if (RSumC > maxR1) {
                        maxR1 = RSumC;
                    }
                }
                lengthR =0;
                listR = new int[lengthR];

                lengthL = 0;
                listL= new int[lengthL];
                data_dir_esqC = true;
            }

            if (timeInMillisecondsC == 4) {
                data_dir_esqC = false;
                for (int i =0; i<lengthL; i++) {
                    if (LSumC > maxL2) {
                        maxL2 = LSumC;
                    }
                }
                for (int i =0; i<lengthR; i++) {
                    if (RSumC > maxR1) {
                        maxR2 = RSumC;
                    }
                }
                lengthR =0;
                listR = new int[lengthR];

                lengthL = 0;
                listL= new int[lengthL];
                data_dir_esqC = true;
            }

            if (timeInMillisecondsC == 0) {
                aviso.setVisibility(View.INVISIBLE);
                calibrationTimer.setText("Calibração realizada");
                data_dir_esqC = false;
                read.setEnabled(true);

                for (int i =0; i<lengthL; i++) {
                    if (LSumC > maxL3) {
                        maxL3 = LSumC;
                    }
                }
                for (int i =0; i<lengthR; i++) {
                    if (RSumC > maxR1) {
                        maxR1 = RSumC;
                    }
                }

                SumC[0] = (double) (maxR1+maxL1)*2.44*7*71.4e-3/9.8;//(maxR1 + maxR2 + maxR3+maxL1+maxL2+maxL3)/3;
                SumC[1]= (double) (maxR2+maxL2)*2.44*7*71.4e-3/9.8;
                SumC[2]= (double) (maxR3+maxL3)*2.44*7*71.4e-3/9.8;
                peso_calculado = (SumC[0]+SumC[1]+SumC[2])/3; //3.65 corresponde ao fator de calibração
                resultado_peso.setText(String.format("Peso calculado = %.1f Kgf", peso_calculado));

            } else if (timeInMillisecondsC>0){
                calibrationTimer.setText("" + timeInMillisecondsC);
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

        finish();
        startActivity(i);


    }
}