package com.example.shear_app;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import java.util.Set;


public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;


    BluetoothAdapter BTadapter;
    Set<BluetoothDevice> paired_devices;
    String[] plist;
    String[] name;
    String[] address;

    private static boolean BTesq = false;
    private static boolean BTdir = false;

    public Button b_dir;
    public Button b_esq;

    private String mDeviceAddressLeft;
    private String mDeviceAddressRight;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BTadapter = BluetoothAdapter.getDefaultAdapter();

        Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.open_nav_drawer, R.string.close_nav_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /*public void on(View view) {
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
                mDeviceAddressLeft = data.getStringExtra(Paired_list.DEVICE_ADDRESS);
                BTesq = true;

            }
        } else if (requestCode == 3) { //DIR
            if (resultCode == RESULT_OK){
                b_dir = findViewById(R.id.button_dir);
                b_dir.setTextColor(Color.GREEN);
                mDeviceAddressRight = data.getStringExtra(Paired_list.DEVICE_ADDRESS);
                BTdir = true;

            }
        }
    }



    public void pé_esq(View view) {
        connectToDevice("esq");
    }

    public void pé_dir(View view) {
        connectToDevice("dir");
    }

    public void startProcess(View view){
        if (BTesq && BTdir) {
            Bundle bn = new Bundle();
            bn.putString("esq", mDeviceAddressLeft);
            bn.putString("dir",mDeviceAddressRight);
            Intent i = new Intent(this, Reader.class);
            i.putExtras(bn);
            startActivity(i);
        }
    }


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
                Intent i1 = new Intent(this, Paired_list.class);
                i1.putExtras(bn);
                startActivityForResult(i1, side.equals("esq") ? 2 : 3);
            }
        }
    }*/

}