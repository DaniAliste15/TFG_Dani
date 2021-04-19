package com.dani.firedetection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


public class RegistrarActivity extends AppCompatActivity {

    private EditText textName;
    private EditText textEmail;
    private EditText textPassword;
    private Button buttonRegistrar;
    private ProgressDialog barra;

    //variables de los datos a registrar
    private String name = "";
    private String email = "";
    private String password = ""; //firebase exige que tenga al menos 6 caracteres la pass

    private String name_b = "";
    private String email_b = "";
    private String password_b = "";
    private int nIncencidios = 0;
    private String tipousuario = "usuarionormal";

    FirebaseAuth Auth;
    DatabaseReference Database;



    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Auth = FirebaseAuth.getInstance();
        Database = FirebaseDatabase.getInstance().getReference();

        barra = new ProgressDialog(this);

        textName = (EditText) findViewById(R.id.editTextName);
        textEmail = (EditText) findViewById(R.id.editTextEmail);
        textPassword = (EditText) findViewById(R.id.editTextPassword);
        buttonRegistrar = (Button) findViewById(R.id.btnRegistrar);


        buttonRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name_b = textName.getText().toString();
                name = name_b.trim();

                email_b = textEmail.getText().toString();
                email = email_b.trim();

                password_b = textPassword.getText().toString();
                password = password_b.trim();

                if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
                    if (password.length() >= 6) {
                        barra.setTitle("Registrando");
                        barra.setMessage("Espere...");
                        barra.setCanceledOnTouchOutside(false);
                        barra.show();
                        registrerUser();
                    } else {
                        textPassword.setError("Numero insuficiente de caracteres");
                    }

                } else {

                    if(name.isEmpty()) {
                        textName.setError("Debe rellenar este campo");
                    }

                    if(email.isEmpty()) {
                        textEmail.setError("Debe rellenar este campo");
                    }

                    if(password.isEmpty()) {
                        textPassword.setError("Debe rellenar este campo");
                    }
                }
            }
        });

    }


    private void registrerUser() {
        Auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                /*if (!task.isSuccessful()) {
                    Log.d("respueta:_:", "onComplete: Failed=" + task.getException().getMessage()); //ADD THIS


                }*/ //Log para comprobar porque la tarea es falsa
                if (task.isSuccessful()) {

                    Map<String, Object> map = new HashMap<>();
                    map.put("name", name);
                    map.put("email", email);
                    map.put("password", password);
                    map.put("numeroincendios",nIncencidios);
                    map.put("rol",tipousuario);


                    String id = Auth.getCurrentUser().getUid(); //obtener el id del usuario

                    Database.child("Users").child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task2) {
                            if (task2.isSuccessful()) {
                                startActivity(new Intent(RegistrarActivity.this, MapaActivity.class));
                                finish();
                            } else {
                                Toast.makeText(RegistrarActivity.this, "No se pudo crear correctamente", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    final AlertDialog.Builder alerta = new AlertDialog.Builder(RegistrarActivity.this);
                    alerta.setMessage("Fallo al registrar usuario.Compruebe los datos")
                            .setCancelable(false)
                            .setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog titulo = alerta.create();
                    titulo.setTitle("Atención");
                    titulo.show();
                }
                barra.dismiss();
            }

        });

    }
}
