package com.example.FAMEST_app;

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

public class BluetoothConnectionActivity extends AsyncTask<Void, Void, Void> {

    //Atividade executada em background e que permite a conexão a um dispositivo BT

    public static final int DONT_FINISH_TASK_WITH_ACTIVITY = 0;

    public static boolean Connected = true;
    private ProgressDialog mProgressdialog;
    private BluetoothAdapter mBluetoothadapter = null;
    private BluetoothSocket btsocket = null;
    private Activity mactivity = null;
    private String maddress = null;
    private Handler mHandler;

    private InputStream minstream;

    private static final String TAG = "Socket_Creation";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private boolean running = true;

    BluetoothConnectionActivity(Activity activity, String address, Handler handler) {
        mactivity = activity;
        maddress = address;
        mHandler = handler;


    }

    //Inicialmente é apresentada uma caixa de diálogo que informa o utilizador da tentativa de
    //estabelecimento de conexão BT
    @Override
    protected void onPreExecute() {
        mProgressdialog = ProgressDialog.show(mactivity, "Connecting", "Please wait...");
    }

    //De seguida, é executada a tentativa de comunicação com o outro dispositivo BT em background
    @Override
    protected Void doInBackground(Void... devices) {


        try {
            if (!isCancelled()) {
                if (btsocket == null || !Connected) {
                    mBluetoothadapter = BluetoothAdapter.getDefaultAdapter();
                    //Criação de uma socket com o endereço obtida da lista de dispositivos emparelhados
                    BluetoothDevice device = mBluetoothadapter.getRemoteDevice(maddress);
                    btsocket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);


                    btsocket.connect(); //Conexão à socket


                    //Get input stream from the socket
                    minstream = btsocket.getInputStream();
                }
            }
        } catch (Exception e) {
            Connected = false;

        }
        return null;
    }

    //Em caso de resultado positivo, é apresentada uma mensagem a indicar o sucesso da conexão
    //E no caso negativo a indicar a falha desta tentativa
    //Em caso positivo, é iniciada a função que permite ler os dados enviados pelo DAQ da palmilha
    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

        if (!Connected) {
            Toast.makeText(mactivity.getApplicationContext(), "Connection failed", Toast.LENGTH_SHORT).show();

            mactivity.finish(); //Encerrar esta atividade no caso de tentativa mal sucedida
        } else {
            Toast.makeText(mactivity.getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
            ListenInput.start(); //Leitura dos dados
        }
        mProgressdialog.dismiss();
    }

    //Thread que permite a leitura e envio dos dados para a atividade da sessão (ReaderActivity)
    Thread ListenInput = new Thread() {
        @Override
        public void run() {
            while (Connected) {
                if (running) {
                    try {
                        String s = read(); //Informação obtida transferida para uma String
                        sendToReadHandler(s); //Envio da String para uma função que irá apresentar esses dados no ReaderActivity (Handler)
                    } catch (Exception e) {
                        Log.i(TAG, "Not able to perform read");
                    }
                } else {
                    break;
                }
            }
        }


    };

    //Função que lê os bytes enviados pelo outro dispositivo
    // Irá converter esses dados para formato texto
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


    //Função que envia os dados já em texto para a atividade da sessão (ReaderActivity)
    public void sendToReadHandler(String s) {
        Message msg = Message.obtain();
        msg.obj = s;
        mHandler.sendMessage(msg);
    }


    //Função que termina a ligação BT existente entre o smartphone e outro dispositivo
    public void disconnect() {

        if (btsocket != null) {
            try {
                btsocket.close();
                cancel(true);
                running = false;
                mactivity.finish();
            } catch (IOException e) {
                Toast.makeText(mactivity.getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
            }
        }

        Toast.makeText(mactivity.getApplicationContext(), "Disconnected", Toast.LENGTH_SHORT).show();
    }
}
