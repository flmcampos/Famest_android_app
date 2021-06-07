package com.example.FAMEST_app;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;


import java.util.Set;


public class SettingsActivity extends Activity {

    BluetoothAdapter BTadapter;
    Set<BluetoothDevice> paired_devices;
    String[] plist;
    String[] name;
    String[] address;

    public static boolean BTesq = false;
    public static boolean BTdir = false;

    public Button b_dir;
    public Button b_esq;

    public static String mDeviceAddressLeft;
    public static String mDeviceAddressRight;



    //Apresentação da página que permite a conexão aos sistemas DAQ
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_main);
        BTadapter = BluetoothAdapter.getDefaultAdapter();

    }

    //Botão que permite a ativação da comunição BT
    public void on(View view) {
        if (BTadapter == null) {

            Toast.makeText(getApplicationContext(), "No Bluetooth communication available", Toast.LENGTH_SHORT).show();

        } else {
            if (!BTadapter.isEnabled()) {
                Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(i, 1);

            } else {
                Toast.makeText(this, "Bluetooth already turned ON", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Bloco de funções que permite:
    //1: Verificar se o BT já está ativado no smartphone
    //2: Guardar o endereço do sistema DAQ correspondente à palmilha esquerda
    //3: Guardar o endereço do sistema DAQ correspondente à palmilha direita
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), "The bluetooth is enabled", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 2){ //ESQ
            if (resultCode == RESULT_OK){
                b_esq = findViewById(R.id.button_esq);
                b_esq.setTextColor(Color.GREEN);
                mDeviceAddressLeft = data.getStringExtra(PairedListActivity.DEVICE_ADDRESS);
                BTesq = true;

            }
        } else if (requestCode == 3) { //DIR
            if (resultCode == RESULT_OK){
                b_dir = findViewById(R.id.button_dir);
                b_dir.setTextColor(Color.GREEN);
                mDeviceAddressRight = data.getStringExtra(PairedListActivity.DEVICE_ADDRESS);
                BTdir = true;
            }
        }
    }
//


    //Botão que permite acesso aos dispositivos emparelhados, guardando o item clicado como
    //Endereço BT do pé esquerdo
    public void pé_esq(View view) {
        connectToDevice("esq");
    }

    //Botão que permite acesso aos dispositivos emparelhados, guardando o item clicado como
    //Endereço BT do pé direito
    public void pé_dir(View view) {
        connectToDevice("dir");
    }

    //Botão que permite começar a atividade de calibração
    public void startProcess(View view){
        if (BTesq && BTdir) {
            Bundle bn = new Bundle();
            bn.putString("esq", mDeviceAddressLeft);
            bn.putString("dir",mDeviceAddressRight);
            Intent i = new Intent(this, MainActivity.class);
            i.putExtras(bn);
            //i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            //startActivityIfNeeded(i, 1);
            startActivity(i);
            finish();
        } else {
            Toast.makeText(this, "Save left and right adresses to establish Bluetooth connection", Toast.LENGTH_SHORT).show();
        }
    }

    //Função que permite a listagem dos componentes emparelhados e que guarda o item clicado
    private void connectToDevice(String side){
        if (BTadapter == null) {
            Toast.makeText(getApplicationContext(), "No Bluetooth communication available", Toast.LENGTH_SHORT).show();
        } else {

            if (!BTadapter.isEnabled()) {
                Toast.makeText(getApplicationContext(), "Enable bluetooth first", Toast.LENGTH_SHORT).show();
            } else {
                paired_devices = BTadapter.getBondedDevices();
                plist = new String[paired_devices.size()];
                name = new String[paired_devices.size()];
                address = new String[paired_devices.size()];
                int j = 0;
                for (BluetoothDevice device : paired_devices) {
                    name[j] = device.getName();
                    address[j] = device.getAddress();
                    plist[j] = name[j] + " | " + address[j];

                    j++;
                }
                Bundle bn = new Bundle();
                bn.putStringArray("paires", plist);
                bn.putString("side",side);
                Intent i1 = new Intent(this, PairedListActivity.class);
                i1.putExtras(bn);
                startActivityForResult(i1, side.equals("esq") ? 2 : 3);
            }
        }
    }
}