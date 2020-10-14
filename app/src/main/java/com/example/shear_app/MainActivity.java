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
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    //int aux = 0;
    int c =0;
    private Button calibration;
    boolean currentlayout = false;

    Bundle bn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calibration = findViewById(R.id.goToStart);
        c =0;
}
    //Após preencher os dados sobre BT e o perfil é apresentada uma caixa de diálogo a informar sobre
    //o começo da sessão
    @Override
    protected void onResume() {
        super.onResume();
        if (ProfileActivity.perfil && SettingsActivity.BTdir && SettingsActivity.BTesq && c==0) {
            calibration.setEnabled(true);
            /*AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Início de sessão");
            alertDialog.setMessage("O utilizador pode agora começar a sessão. Se desejar corrigir os dados inseridos " +
                    "ou os endereços Bluetooth guardados, poderá com o mesmo procedimento utilizado anteriormente");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            alertDialog.show();
            c=1;*/
        }
    }

    //Encaminhamento para uma página com instruções sobre o funcionamento da aplicação
    public void Instructions(View view){
        setContentView(R.layout.instructions);
        currentlayout = true;
    }

    //Encaminhamento para página de setup de BT
    public void Settings(View view) {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
    }

    //Encaminhamento para página de preenchimento do perfil
    public void Profile(View view) {
        Intent i = new Intent(this, ProfileActivity.class);
        startActivity(i);
    }

    //Início da atividade de calibração com os endereços BT guardados a serem usados na conexão com os DAQ
    public void reading(View view) {

        bn = getIntent().getExtras();
        bn.getString("esq");
        bn.getString("dir");
        Intent i = new Intent(this, CalibrationActivity.class);
        i.putExtras(bn);
        try {
            startActivity(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Caso a página apresentada seja a de instruções, clicando no botão de retornar volta à pagina inicial
    //Caso contrário fecha a aplicação
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