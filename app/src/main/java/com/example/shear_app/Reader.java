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

    public static BluetoothConnection BTConnectionL;
    public static BluetoothConnection BTConnectionR;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.chat_bt);

        Bundle bn = getIntent().getExtras();
        String mDeviceAddressLeft = bn.getString("esq");
        String mDeviceAddressRight = bn.getString("dir");

        //Create connection for device
        BTConnectionL = new BluetoothConnection(this, mDeviceAddressLeft, mHandlerEsq);
        BTConnectionL.execute();

        //Create connection for device
        BTConnectionR = new BluetoothConnection(this, mDeviceAddressRight, mHandlerDir);
        BTConnectionR.execute();

        messageTextR = (TextView)findViewById(R.id.Data_collected2);
        messageTextL = (TextView)findViewById(R.id.Data_collected);
    }


    private final Handler mHandlerDir = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            String s = (String)msg.obj;
            Log.d("TAG", "Mensagem lida R: " + s);

            messageTextR.setText(s);
        }
    };

    private final Handler mHandlerEsq = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            String s = (String)msg.obj;
            Log.d("TAG", "Mensagem lida L: " + s);

            messageTextL.setText(s);
        }
    };

}
