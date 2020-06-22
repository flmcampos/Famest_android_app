package com.example.shear_app;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Reader extends Activity {

    public TextView messageTextL;
    public TextView messageTextR;

    private int LHal, LMet1, LMet2, LMet3, LMid, LCal1, LCal2 ;
    private int RHal, RMet1, RMet2, RMet3, RMid, RCal1, RCal2 ;
    private int RSum, LSum;

    private View LHal_ball, LMet1_ball, LMet2_ball, LMet3_ball, LMid_ball, LCal1_ball, LCal2_ball;
    private View RHal_ball, RMet1_ball, RMet2_ball, RMet3_ball, RMid_ball, RCal1_ball, RCal2_ball;
    private View CP_esq, CP_dir;


    public static BluetoothConnection BTConnectionL;
    public static BluetoothConnection BTConnectionR;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.chat_bt);

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


        Bundle bn = getIntent().getExtras();
        String mDeviceAddressLeft = bn.getString("esq");
        String mDeviceAddressRight = bn.getString("dir");

        //Create connection for device
        BTConnectionL = new BluetoothConnection(this, mDeviceAddressLeft, mHandlerEsq);
        BTConnectionL.execute();

        //Create connection for device
        BTConnectionR = new BluetoothConnection(this, mDeviceAddressRight, mHandlerDir);
        BTConnectionR.execute();

        messageTextR = findViewById(R.id.Data_collected2);
        messageTextL = findViewById(R.id.Data_collected);
    }


    private final Handler mHandlerDir = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            String s = (String)msg.obj;
            Log.d("TAG", "Mensagem lida R: " + s);

            String [] arrofs = s.split("|");

            for (int i=0 ; i< arrofs.length; i++) {
                if (arrofs[i] == "S") {
                    RHal = Integer.parseInt(arrofs[i+1]);
                    RMet1 = Integer.parseInt(arrofs[i+2]);
                    RMet2 = Integer.parseInt(arrofs[i+3]);
                    RMet3 = Integer.parseInt(arrofs[i+4]);
                    RMid = Integer.parseInt(arrofs[i+5]);
                    RCal1 = Integer.parseInt(arrofs[i+6]);
                    RCal2 = Integer.parseInt(arrofs[i+7]);

                }
            }
            RSum = (RHal) + (RMet1) + (RMet2)+ (RMet3)+(RMid)+(RCal1)+(RCal2);

            RHal_ball.getLayoutParams().width = (3000 + (RHal))/100;
            RHal_ball.getLayoutParams().height = (3000 + (RHal))/100;

            RMet1_ball.getLayoutParams().width = (3000 + (RMet1))/100;
            RMet1_ball.getLayoutParams().height = (3000 + (RMet1))/100;

            RMet2_ball.getLayoutParams().width = (3000 + (RMet2))/100;
            RMet2_ball.getLayoutParams().height = (3000 + (RMet2))/100;

            RMet3_ball.getLayoutParams().width = (3000 + (RMet3))/100;
            RMet3_ball.getLayoutParams().height = (3000 + (RMet3))/100;

            RMid_ball.getLayoutParams().width = (3000 + (RMid))/100;
            RMid_ball.getLayoutParams().height = (3000 + (RMid))/100;

            RCal1_ball.getLayoutParams().width = (3000 + (RCal1))/100;
            RCal1_ball.getLayoutParams().height = (3000 + (RCal1))/100;

            RCal2_ball.getLayoutParams().width = (3000 + (RCal2))/100;
            RCal2_ball.getLayoutParams().height = (3000 + (RCal2))/100;

            CP_dir.setTranslationX((getX(RHal_ball)+getX(RMet1_ball)+getX(RMet2_ball)+getX(RMet3_ball)+getX(RMid_ball)+getX(RCal1_ball)+getX(RCal2_ball))/RSum);
            CP_dir.setTranslationY((getY(RHal_ball)+getY(RMet1_ball)+getY(RMet2_ball)+getY(RMet3_ball)+getY(RMid_ball)+getY(RCal1_ball)+getY(RCal2_ball))/RSum);




            messageTextR.setText(s);
        }
    };

    private final Handler mHandlerEsq = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            String s = (String)msg.obj;
            Log.d("TAG", "Mensagem lida L: " + s);

            String [] arrofs = s.split("|");

            for (int i=0 ; i< arrofs.length; i++) {
                if (arrofs[i]=="S") {
                    LHal = Integer.parseInt(arrofs[i+1]);
                    LMet1 = Integer.parseInt(arrofs[i+2]);
                    LMet2 = Integer.parseInt(arrofs[i+3]);
                    LMet3 = Integer.parseInt(arrofs[i+4]);
                    LMid = Integer.parseInt(arrofs[i+5]);
                    LCal1 = Integer.parseInt(arrofs[i+6]);
                    LCal2 = Integer.parseInt(arrofs[i+7]);
                }
            }

            LSum = (LHal) + (LMet1) + (LMet2)+ (LMet3)+(LMid)+(LCal1)+(LCal2);


            LHal_ball.getLayoutParams().width = (3000 + (LHal))/100;
            LHal_ball.getLayoutParams().height = (3000 + (LHal))/100;

            LMet1_ball.getLayoutParams().width = (3000 + (LMet1))/100;
            LMet1_ball.getLayoutParams().height = (3000 + (LMet1))/100;

            LMet2_ball.getLayoutParams().width = (3000 + (LMet2))/100;
            LMet2_ball.getLayoutParams().height = (3000 + (LMet2))/100;

            LMet3_ball.getLayoutParams().width = (3000 + (LMet3))/100;
            LMet3_ball.getLayoutParams().height = (3000 + (LMet3))/100;

            LMid_ball.getLayoutParams().width = (3000 + (LMid))/100;
            LMid_ball.getLayoutParams().height = (3000 + (LMid))/100;

            LCal1_ball.getLayoutParams().width = (3000 + (LCal1))/100;
            LCal1_ball.getLayoutParams().height = (3000 + (LCal1))/100;

            LCal2_ball.getLayoutParams().width = (3000 + (LCal2))/100;
            LCal2_ball.getLayoutParams().height = (3000 + (LCal2))/100;

            //Calculate Center of pressure

            CP_esq.setTranslationX((getX(LHal_ball)+getX(LMet1_ball)+getX(LMet2_ball)+getX(LMet3_ball)+getX(LMid_ball)+getX(LCal1_ball)+getX(LCal2_ball))/LSum);
            CP_esq.setTranslationY((getY(LHal_ball)+getY(LMet1_ball)+getY(LMet2_ball)+getY(LMet3_ball)+getY(LMid_ball)+getY(LCal1_ball)+getY(LCal2_ball))/LSum);



            messageTextL.setText(s);
        }
    };

    private int getX (View ball) {

        int [] location = new int[2];
        ball.getLocationOnScreen(location);
        int x = location[0];

        return x;
    }

    private int getY (View ball) {

        int [] location = new int[2];
        ball.getLocationOnScreen(location);
        int y = location[1];

        return y;
    }
}
