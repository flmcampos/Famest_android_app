package com.example.shear_app;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
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
    private View CP_esq_ball, CP_dir_ball;


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

        CP_dir_ball =  findViewById(R.id.CP_dir);
        CP_esq_ball = findViewById(R.id.CP_esq);


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
                    //Sensor pressure values
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

            //Ball growth

            RHal_ball.getLayoutParams().width = (2000 + (RHal))/100;
            RHal_ball.getLayoutParams().height = (2000 + (RHal))/100;

            RMet1_ball.getLayoutParams().width = (2000 + (RMet1))/100;
            RMet1_ball.getLayoutParams().height = (2000 + (RMet1))/100;

            RMet2_ball.getLayoutParams().width = (2000 + (RMet2))/100;
            RMet2_ball.getLayoutParams().height = (2000 + (RMet2))/100;

            RMet3_ball.getLayoutParams().width = (2000 + (RMet3))/100;
            RMet3_ball.getLayoutParams().height = (2000 + (RMet3))/100;

            RMid_ball.getLayoutParams().width = (2000 + (RMid))/100;
            RMid_ball.getLayoutParams().height = (2000 + (RMid))/100;

            RCal1_ball.getLayoutParams().width = (2000 + (RCal1))/100;
            RCal1_ball.getLayoutParams().height = (2000 + (RCal1))/100;

            RCal2_ball.getLayoutParams().width = (2000 + (RCal2))/100;
            RCal2_ball.getLayoutParams().height = (2000 + (RCal2))/100;

            //Ball color change

            RHal_ball.setBackgroundColor(Color.parseColor(DecToHexa((int) (-0.17 * RHal + 255)))); //Dúvida?????????????
            RMet1_ball.setBackgroundColor(Color.parseColor(DecToHexa((int) (-0.17 * RMet1 + 255))));
            RMet2_ball.setBackgroundColor(Color.parseColor(DecToHexa((int) (-0.17 * RMet2 + 255))));
            RMet3_ball.setBackgroundColor(Color.parseColor(DecToHexa((int) (-0.17 * RMet3 + 255))));
            RMid_ball.setBackgroundColor(Color.parseColor(DecToHexa((int) (-0.17 * RMid + 255))));
            RCal1_ball.setBackgroundColor(Color.parseColor(DecToHexa((int) (-0.17 * RCal1 + 255))));
            RCal2_ball.setBackgroundColor(Color.parseColor(DecToHexa((int) (-0.17 * RCal2 + 255))));

            if (RSum != 0 ) {

                //Calculate center of pressure coordinates

                CP_dir_ball.setVisibility(View.VISIBLE);

                CP_dir_ball.setTranslationX((RHal_ball.getX()*RHal + RMet1_ball.getX()*RMet1 + RMet2_ball.getX()*RMet2 + RMet3_ball.getX()*RMet3 + RMid_ball.getX()*RMid + RCal1_ball.getX()*RCal1 + RCal2_ball.getX()*RCal2) / RSum);
                CP_dir_ball.setTranslationY((RHal_ball.getY()*RHal + RMet1_ball.getY()*RMet1 + RMet2_ball.getY()*RMet2 + RMet3_ball.getY()*RMet3 + RMid_ball.getY()*RMid + RCal1_ball.getY()*RCal1 + RCal2_ball.getY()*RCal2) / RSum);

            }


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

                    //Sensor pressure values
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

            //Ball growth

            LHal_ball.getLayoutParams().width = (2000 + (LHal))/100;
            LHal_ball.getLayoutParams().height = (2000 + (LHal))/100;

            LMet1_ball.getLayoutParams().width = (2000 + (LMet1))/100;
            LMet1_ball.getLayoutParams().height = (2000 + (LMet1))/100;

            LMet2_ball.getLayoutParams().width = (2000 + (LMet2))/100;
            LMet2_ball.getLayoutParams().height = (2000 + (LMet2))/100;

            LMet3_ball.getLayoutParams().width = (2000 + (LMet3))/100;
            LMet3_ball.getLayoutParams().height = (2000 + (LMet3))/100;

            LMid_ball.getLayoutParams().width = (2000 + (LMid))/100;
            LMid_ball.getLayoutParams().height = (2000 + (LMid))/100;

            LCal1_ball.getLayoutParams().width = (2000 + (LCal1))/100;
            LCal1_ball.getLayoutParams().height = (2000 + (LCal1))/100;

            LCal2_ball.getLayoutParams().width = (2000 + (LCal2))/100;
            LCal2_ball.getLayoutParams().height = (2000 + (LCal2))/100;

            //Ball color change

            LHal_ball.setBackgroundColor(Color.parseColor(DecToHexa((int) (-0.17 * LHal + 255)))); //Dúvida?????????????
            LMet1_ball.setBackgroundColor(Color.parseColor(DecToHexa((int) (-0.17 * LMet1 + 255))));
            LMet2_ball.setBackgroundColor(Color.parseColor(DecToHexa((int) (-0.17 * LMet2 + 255))));
            LMet3_ball.setBackgroundColor(Color.parseColor(DecToHexa((int) (-0.17 * LMet3 + 255))));
            LMid_ball.setBackgroundColor(Color.parseColor(DecToHexa((int) (-0.17 * LMid + 255))));
            LCal1_ball.setBackgroundColor(Color.parseColor(DecToHexa((int) (-0.17 * LCal1 + 255))));
            LCal2_ball.setBackgroundColor(Color.parseColor(DecToHexa((int) (-0.17 * LCal2 + 255))));

            //Calculate Center of pressure

            if (LSum !=0) {

                CP_esq_ball.setVisibility(View.VISIBLE);

                CP_esq_ball.setX((LHal_ball.getX()*LHal + LMet1_ball.getX()*LMet1 + LMet2_ball.getX()*LMet2 + LMet3_ball.getX()*LMet3 + LMid_ball.getX()*LMid + LCal1_ball.getX()*LCal1 + LCal2_ball.getX()*LCal2) / LSum);
                CP_esq_ball.setY((LHal_ball.getY()*LHal + LMet1_ball.getY()*LMet1 + LMet2_ball.getY()*LMet2 + LMet3_ball.getY()*LMet3 + LMid_ball.getY()*LMid + LCal1_ball.getY()*LCal1 + LCal2_ball.getY()*LCal2) / LSum);

            }

            messageTextL.setText(s);
        }
    };

    String DecToHexa (int n) {

        // char array to store hexadecimal number
        char[] hexaDeciNum = new char[100];

        // counter for hexadecimal number array
        int i = 0;
        while(n!=0)
        {
            // temporary variable to store remainder
            int temp  = 0;

            // storing remainder in temp variable.
            temp = n % 16;

            // check if temp < 10
            if(temp < 10)
            {
                hexaDeciNum[i] = (char)(temp + 48);
                i++;
            }
            else
            {
                hexaDeciNum[i] = (char)(temp + 55);
                i++;
            }

            n = n/16;
        }
        String Hexa = "";

        for(int j=i-1; j>=0; j--) {
            Hexa = Hexa + hexaDeciNum[j];
        }

        return "#FF" + Hexa + "00";
    }

    /*private int getX (View ball) {

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
    }*/
}
