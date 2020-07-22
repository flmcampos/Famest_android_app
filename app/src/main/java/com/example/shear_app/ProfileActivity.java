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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import java.util.Set;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class  ProfileActivity extends AppCompatActivity {

    private EditText mEditTextNome;
    private EditText mEditTextIdade;
    private EditText mEditTextAltura;
    private EditText mEditTextPeso;
    private EditText mEditTextNumeroSapato;
    private CheckBox checkHomem;
    private CheckBox checkMulher;

    public static String gender;
    public static String Nome;
    public static String Idade;
    public static String Altura;
    public static String Peso;
    public static String Numero_Sapato;

    public static Boolean perfil = false;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        checkHomem = (CheckBox) findViewById(R.id.checkBoxHomem);
        checkMulher = (CheckBox) findViewById(R.id.checkBoxMulher);
        mEditTextNome = (EditText) findViewById(R.id.editTextNome);
        mEditTextIdade = (EditText) findViewById((R.id.editTextIdade));
        mEditTextAltura = (EditText) findViewById(R.id.editTextAltura);
        mEditTextPeso = (EditText) findViewById(R.id.editTextPeso);
        mEditTextNumeroSapato = (EditText) findViewById(R.id.editTextNumeroSapato);
    }

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        switch (view.getId()) {
            case R.id.checkBoxHomem:
                if (checked) {
                    checkMulher.setChecked(false);
                    gender = checkHomem.getText().toString();
                } else {
                    checkHomem.setChecked(true);
                    gender = checkHomem.getText().toString();
                }
                break;
            case R.id.checkBoxMulher:
                if (checked) {
                    checkHomem.setChecked(false);
                    gender = checkMulher.getText().toString();
                } else {
                    checkMulher.setChecked(true);
                    gender = checkMulher.getText().toString();
                }
                break;
        }
    }

    public void guardarInfo(View view) {

        Nome = mEditTextNome.getText().toString();
        Idade = mEditTextIdade.getText().toString();
        Altura = mEditTextAltura.getText().toString();
        Peso = mEditTextPeso.getText().toString();
        Numero_Sapato = mEditTextNumeroSapato.getText().toString();

        if (Nome.length()>0 && Idade.length()>0 && Altura.length()>0 && Peso.length()>0 && Numero_Sapato.length()>0 && gender.length()>0) {

            //Intent intent = new Intent(this, MainActivity.class);

            Context context = getApplicationContext();
            CharSequence text = "Perfil Guardado";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            perfil = true;

            Intent openMainActivity = new Intent(this, MainActivity.class);
            openMainActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivityIfNeeded(openMainActivity, 0);
            //startActivity(intent);

        } else {
            Toast.makeText(this, "Preencha os restantes campos", Toast.LENGTH_SHORT).show();
        }
    }
}
