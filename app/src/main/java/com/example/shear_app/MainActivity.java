package com.example.shear_app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    int c =0;
    private Button calibration;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calibration = findViewById(R.id.goToStart);

        if (ProfileActivity.perfil && SettingsActivity.BTdir && SettingsActivity.BTesq) {
            calibration.setEnabled(true);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ProfileActivity.perfil && SettingsActivity.BTdir && SettingsActivity.BTesq && c==0) {
            calibration.setEnabled(true);
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Início de sessão");
            alertDialog.setMessage("O utilizador pode agora começar a sessão. Se desejar corrigir os dados inseridos " +
                    "ou os endereços Bluetooth guardados, poderá com o mesmo procedimento utilizado anteriormente");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            alertDialog.show();
            c=1;
        }
    }

    public void Instructions(View view){
        Intent i = new Intent(this, InstructionsActivity.class);
        startActivity(i);
    }


    public void Settings(View view) {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
    }


    public void Profile(View view) {
        Intent i = new Intent(this, ProfileActivity.class);
        startActivity(i);
    }

    public void reading(View view) {

        Bundle bn = getIntent().getExtras();
        bn.getString("esq");
        bn.getString("dir");
        Intent i = new Intent(this, CalibrationActivity.class);
        i.putExtras(bn);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            finishAffinity();
        }
    }
}