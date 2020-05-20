package com.example.shear_app;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Paired_list extends Activity {
    ListView lview;
    String[] paires;
    String side;
    ArrayAdapter<String> adapter;
    String item;
    String info;

    public static String DEVICE_ADDRESS = "device_address";
    public static String  DEVICE_ADDRESS_ESQ = "device_address_esq";
    public static String  DEVICE_ADDRESS_DIR = "device_address_dir";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pairing_list);
        lview = (ListView) findViewById(R.id.listviewid);
        Bundle bn = getIntent().getExtras();
        paires = bn.getStringArray("paires");
        side = bn.getString("side");
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, paires);


        lview.setAdapter(adapter);
        lview.setOnItemClickListener(myclickLis);

    }

    private AdapterView.OnItemClickListener myclickLis = new AdapterView.OnItemClickListener() {

        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            info = ((TextView) view).getText().toString();
            item = info.substring(info.length() - 17);

            Intent returnIntent = new Intent();
            returnIntent.putExtra(DEVICE_ADDRESS,item);
            setResult(Activity.RESULT_OK,returnIntent);
            finish();


            //////////////////////////teste de verificação do gitlab///////////////////////////////
            
        }
    };
}
