package com.example.shear_app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    int c =0;
    private Button calibration, instructions, bt, prof_;
    boolean currentlayout = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calibration = findViewById(R.id.goToStart);
        instructions = findViewById(R.id.goToInstructions);
        bt = findViewById(R.id.goToSettings);
        prof_ = findViewById(R.id.goToProfile);

        /*calibration.setVisibility(View.INVISIBLE);
        instructions.setVisibility(View.INVISIBLE);
        bt.setVisibility(View.INVISIBLE);
        prof_.setVisibility(View.INVISIBLE);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                calibration.setVisibility(View.VISIBLE);
                instructions.setVisibility(View.VISIBLE);
                bt.setVisibility(View.VISIBLE);
                prof_.setVisibility(View.VISIBLE);
            }
        }, 5000);*/

        c =0;


        /*if (ProfileActivity.perfil && SettingsActivity.BTdir && SettingsActivity.BTesq) {
            calibration.setEnabled(true);*/
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
        setContentView(R.layout.instructions);
        currentlayout = true;
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
        Intent i = new Intent(this, MainActivity.class);
        i.putExtras(bn);
        try {
            startActivity(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (currentlayout) {
            setContentView(R.layout.activity_main);
            currentlayout = false;
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                finishAffinity();
            } else {
                super.onBackPressed();
            }
        }
    }
}