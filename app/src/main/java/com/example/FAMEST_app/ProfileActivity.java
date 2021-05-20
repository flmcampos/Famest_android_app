package com.example.FAMEST_app;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;

public class  ProfileActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private File file;
    File[] list_file;
    ListView profile_view;
    ArrayAdapter<String> f_adapter;
    File dir;
    String f_del;
    File del;
    String[] profile_list;

    private Button load;

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

    protected FrameLayout frameLayout;
    protected ConstraintLayout layout;

    public static Boolean perfil = false;

    //Apresentação da página onde o utilizador irá preencher algumas informações úteis como nome, altura, etc
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        layout = (ConstraintLayout) findViewById(R.id.xmlprofile);

        checkHomem = (CheckBox) findViewById(R.id.checkBoxHomem);
        checkMulher = (CheckBox) findViewById(R.id.checkBoxMulher);
        mEditTextNome = (EditText) findViewById(R.id.editTextNome);
        mEditTextIdade = (EditText) findViewById((R.id.editTextIdade));
        mEditTextAltura = (EditText) findViewById(R.id.editTextAltura);
        mEditTextPeso = (EditText) findViewById(R.id.editTextPeso);
        mEditTextNumeroSapato = (EditText) findViewById(R.id.editTextNumeroSapato);

        load = (Button) findViewById(R.id.load);

        frameLayout = (FrameLayout) findViewById(R.id.listprofile);
        getLayoutInflater().inflate(R.layout.profile_list,frameLayout);

        profile_view = (ListView) findViewById(R.id.profiles);
        registerForContextMenu(profile_view);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        f_del=f_adapter.getItem(info.position);
        del = new File(dir, f_del);
        if (item.getItemId() == R.id.delete) {
            //Log.d("ContM", del.getName());
            del.delete();
            load.performClick();
            Toast.makeText(this, "Perfil removido", Toast.LENGTH_SHORT).show();

        }
        return super.onContextItemSelected(item);
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
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    //Função que permite aferir se o utilizador concedeu permissão para aceder à memória interna do smartphone
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) +
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
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

            fos.write((Nome+ System.getProperty("line.separator") + Idade + System.getProperty("line.separator") + gender + System.getProperty("line.separator") + Altura +  System.getProperty("line.separator") + Peso +
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

        frameLayout.setVisibility(View.VISIBLE);
        layout.setVisibility(View.INVISIBLE);


        String root = Environment.getExternalStorageDirectory().toString();
        dir = new File(root + "/FAMEST"+"/Perfis");

        //Criação de um diretório para onde será enviado o ficheiro txt
        if (!dir.exists()) {

            dir.mkdir();

        }
        list_file = dir.listFiles();
        //File [] list_file = dir.listFiles();
        profile_list = new String[list_file.length];
        int count = 0;
        for (File f : list_file) {
            String name = f.getName();
            profile_list[count]= name;
            count++;
        }
        f_adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, profile_list);

        profile_view.setAdapter(f_adapter);
        profile_view.setOnItemClickListener(myclickLis);


    }

    private final AdapterView.OnItemClickListener myclickLis = (parent, view, position, id) -> {
        frameLayout.setVisibility(View.INVISIBLE);
        layout.setVisibility(View.VISIBLE);

        String title = ((TextView) view).getText().toString();
        File read_file = new File(dir, title);


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
        String [] array_dados = text.toString().split("\n");
        mEditTextNome.setText(array_dados[0]);
        mEditTextIdade.setText(array_dados[1]);
        mEditTextAltura.setText(array_dados[3]);
        mEditTextPeso.setText(array_dados[4]);
        mEditTextNumeroSapato.setText(array_dados[5]);

        if (array_dados[2].equals("Masculino")){
            checkMulher.setChecked(false);
            checkHomem.setChecked(true);
        } else {
            checkHomem.setChecked(false);
            checkMulher.setChecked(true);
        }


    };

    private String[] remove_arr (String[] arr, int pos) {
        for (int i = pos; i < arr.length - 1; i++) {
            arr[i] = arr[i + 1];
        }
        return arr;
    }
}
