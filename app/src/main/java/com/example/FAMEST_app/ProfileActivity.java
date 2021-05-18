package com.example.FAMEST_app;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class  ProfileActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private File file;

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

    //Apresentação da página onde o utilizador irá preencher algumas informações úteis como nome, altura, etc
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

    //Com esta função pretende-se seleccionar apenas uma das opções relativamente ao gênero do utilizador
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

    //Após premir este botão os dados preenchidos serão guardados em variáveis que serão depois transportadas
    //para o ficheiro txt que será gerado no final de cada sessão
    public void guardarInfo(View view) {

        Nome = mEditTextNome.getText().toString();
        Idade = mEditTextIdade.getText().toString();
        Altura = mEditTextAltura.getText().toString();
        Peso = mEditTextPeso.getText().toString();
        Numero_Sapato = mEditTextNumeroSapato.getText().toString();

        if (Nome.length()>0 && Idade.length()>0 && Altura.length()>0 && Peso.length()>0 && Numero_Sapato.length()>0 && gender.length()>0) {

            //Intent intent = new Intent(this, MainActivity.class);

            perfil = true;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkPermission()) {
                    // Code for above or equal 23 API Oriented Device
                    // Your Permission granted already .Do next code
                    gravar_dados();
                } else {
                    requestPermission(); // Code for permission
                }
            } else {
                // Code for Below 23 API Oriented Device
                // Do next code
                gravar_dados();
            }

            //startActivity(intent);

        } else {
            Toast.makeText(this, "Preencha os restantes campos", Toast.LENGTH_SHORT).show();
        }
    }

    public void loadprofile(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission()) {
                // Code for above or equal 16 API Oriented Device
                // Your Permission granted already .Do next code
                listprofile();
            } else {
                requestPermission(); // Code for permission
            }
        } else {
            // Code for Below 23 API Oriented Device
            // Do next code
            listprofile();
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    //Função que permite aferir se o utilizador concedeu permissão para aceder à memória interna do smartphone
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    //Função que transcreve para uma variável booleana a resposta do utilizador ao pedido de acesso à memória interna
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }


    /*private void requestPermission_read() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    //Função que permite aferir se o utilizador concedeu permissão para aceder à memória interna do smartphone
    private boolean checkPermission_read() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }*/



    private void gravar_dados() {

        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {

            String root = Environment.getExternalStorageDirectory().toString();
            File dir = new File(root + "/FAMEST"+"/Perfis");

            //Criação de um diretório para onde será enviado o ficheiro txt
            if (!dir.exists()) {

                dir.mkdir();

            }

            //criação do ficheiro txt
            //No nome do ficheiro será incluido o nome colocado no perfil e a data e hora de criação deste
            file = new File(dir, Nome +".txt");

            //No caso de já existir um ficheiro com o mesmo nome, este será apagado
            if (file.exists()){

                subst_perfil();


                //Bloco que garante escrever toda a informação no ficheiro txt
            } else {

                write_dados();
                Intent openMainActivity = new Intent(this, MainActivity.class);
                openMainActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivityIfNeeded(openMainActivity, 0);
            }
        } else {
            Toast.makeText(this, "Storage could not be found", Toast.LENGTH_SHORT).show();
        }

    }

    private void subst_perfil() {

        AlertDialog AD = new AlertDialog.Builder(this).create();
        AD.setTitle("Ficheiro já existe!");
        AD.setMessage("Deseja sobrepôr este perfil?");
        AD.setButton(AlertDialog.BUTTON_POSITIVE, "Sim",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        file.delete();
                        write_dados();
                        Intent openMainActivity = new Intent(ProfileActivity.this, MainActivity.class);
                        openMainActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivityIfNeeded(openMainActivity, 0);
                    }
                });
        AD.setButton(AlertDialog.BUTTON_NEGATIVE, "Não",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        AD.show();
    }

    private void write_dados() {
        try {
            FileOutputStream fos = new FileOutputStream(file);

            fos.write((Nome+ System.getProperty("line.separator") + gender + System.getProperty("line.separator") + Altura +  System.getProperty("line.separator") + Peso +
                    System.getProperty("line.separator") + Numero_Sapato).getBytes());

            fos.close();

            Toast.makeText(this, "Perfil guardado", Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro 2", Toast.LENGTH_SHORT).show();

        }
    }

    private void listprofile() {
        String root = Environment.getExternalStorageDirectory().toString();
        File dir = new File(root + "/FAMEST"+"/Perfis");

        //Criação de um diretório para onde será enviado o ficheiro txt
        if (!dir.exists()) {

            dir.mkdir();

        }


        File read_file = new File(dir, "Francisco.txt");


        //Read text from file
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(read_file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
        }
        Log.d("Res", text.toString());
    }
}
