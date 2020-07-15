package com.example.shear_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void Settings(View view) {
        Intent i = new Intent(this,SettingsActivity.class);
        startActivity(i);
    }


    public void Profile(View view) {
        Intent i = new Intent(this, ProfileActivity.class);
        startActivity(i);
    }

    public void reading(View view) {
        Bundle bn = getIntent().getExtras();
        String mDeviceAddressLeft = bn.getString("esq");
        String mDeviceAddressRight = bn.getString("dir");

        Bundle b = new Bundle();
        b.putString("esq", mDeviceAddressLeft);
        b.putString("dir", mDeviceAddressRight);
        Intent i = new Intent(this, ReaderActivity.class);
        i.putExtras(bn);
        startActivity(i);
    }
}
