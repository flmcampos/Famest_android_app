package com.example.shear_app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class BluetoothConnection extends AsyncTask<Void, Void, Void> {

    public static boolean Connected = true;
    private ProgressDialog mProgressdialog;
    private BluetoothAdapter mBluetoothadapter = null;
    private BluetoothSocket btsocket = null;
    private Activity mactivity = null;
    private String maddress = null;
    private Handler mHandler;

    private InputStream minstream;
    private String inmessage;

    private int mystate;


    private static final String TAG = "Socket_Creation";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    BluetoothConnection(Activity activity, String address, Handler handler) {
        mactivity = activity;
        maddress = address;
        mHandler = handler;


    }

    @Override
    protected void onPreExecute() {
        mProgressdialog = ProgressDialog.show(mactivity, "Connecting", "Please wait...");
    }

    @Override
    protected Void doInBackground(Void... devices) {
        try {
            if (btsocket == null || !Connected) {
                mBluetoothadapter = BluetoothAdapter.getDefaultAdapter();
                BluetoothDevice device = mBluetoothadapter.getRemoteDevice(maddress);
                btsocket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
                btsocket.connect();

                //Get input stream from the socket
                minstream = btsocket.getInputStream();
            }
        } catch (Exception e) {
            Connected = false;

        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

        if (!Connected) {
            Toast.makeText(mactivity.getApplicationContext(), "Connection failed", Toast.LENGTH_SHORT).show();
            mactivity.finish();
        } else {
            Toast.makeText(mactivity.getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
            ListenInput.start();
        }
        mProgressdialog.dismiss();
    }

    Thread ListenInput = new Thread() {
        @Override
        public void run() {
            while (Connected) {
                try {
                    String s = read();
                    sendToReadHandler(s);
                } catch (Exception e) {
                    Log.i(TAG, "Not able to perform read");
                }
            }
        }
    };

    private String read() {

        String s = "";
        try {


            byte[] buffer = new byte[1024];
            int bytes;

            //Read from the Input Stream

            bytes = minstream.read(buffer);
            s = new String(buffer, "ASCII");
            s = s.substring(0, bytes);
        } catch (IOException e) {
            Log.e(TAG, "Read failed");
        }
        return s;
    }

    public void sendToReadHandler(String s) {
        Message msg = Message.obtain();
        msg.obj = s;
        mHandler.sendMessage(msg);
    }

    public void disconnect() {

        if (btsocket != null) {
            try {
                btsocket.close();
            } catch (IOException e) {
                Toast.makeText(mactivity.getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
            }
        }

        Toast.makeText(mactivity.getApplicationContext(), "Disconnected", Toast.LENGTH_SHORT).show();

        mactivity.finish();
    }
}
