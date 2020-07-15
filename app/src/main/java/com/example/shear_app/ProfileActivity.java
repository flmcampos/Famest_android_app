package com.example.shear_app;

import android.content.Context;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import java.util.Set;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class  ProfileActivity extends AppCompatActivity {

    public EditText mEditTextNome;
    public EditText mEditTextIdade;
    public EditText mEditTextAltura;
    public EditText mEditTextPeso;
    public EditText mEditTextNumeroSapato;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
    }

    public void guardarInfo(View view) {

        Intent intent = new Intent(this, MainActivity.class);

        mEditTextNome = (EditText) findViewById(R.id.editTextNome);
        mEditTextIdade = (EditText) findViewById((R.id.editTextIdade));
        mEditTextAltura = (EditText) findViewById(R.id.editTextAltura);
        mEditTextPeso = (EditText) findViewById(R.id.editTextPeso);
        mEditTextNumeroSapato = (EditText) findViewById(R.id.editTextNumeroSapato);

        String Nome = mEditTextNome.getText().toString();
        String Idade = mEditTextIdade.getText().toString();
        String Altura = mEditTextAltura.getText().toString();
        String Peso = mEditTextPeso.getText().toString();
        String Numero_Sapato = mEditTextNumeroSapato.getText().toString();

        Context context = getApplicationContext();
        CharSequence text = "Perfil Guardado";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

        startActivity(intent);
    }
}
