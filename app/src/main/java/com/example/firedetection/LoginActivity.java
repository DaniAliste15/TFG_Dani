package com.example.firedetection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText textEmail;
    private EditText textPassword;
    private Button buttonLogin;
    private Button buttonOlvidar;
    private Button buttonRegistrar;

    private String email = "";
    private String password = "";
    private String email_b = "";
    private String password_b = "";

    private FirebaseAuth mAuth;
    private DatabaseReference Database;

    private ProgressDialog dialog;
    private ProgressDialog dialog1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.setTitle("Iniciar sesion");

        mAuth = FirebaseAuth.getInstance();
        Database = FirebaseDatabase.getInstance().getReference();

        textEmail = (EditText) findViewById(R.id.editTextEmail);
        textPassword = (EditText) findViewById(R.id.editTextPassword);
        buttonLogin = (Button) findViewById(R.id.btnLogin);
        buttonOlvidar = (Button) findViewById(R.id.btnOlvidar);
        buttonRegistrar = (Button) findViewById(R.id.btnRegistrar);

        dialog = new ProgressDialog(this);
        dialog1 = new ProgressDialog(this);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email_b = textEmail.getText().toString();
                email = email_b.trim();

                password_b = textPassword.getText().toString();
                password = password_b.trim();

                if(!email.isEmpty() && !password.isEmpty() && password.length() >= 6){
                    dialog.setTitle("Iniciando sesion");
                    dialog.setMessage("Espere...");
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                    loginUser();
                }
                else {
                    //Toast.makeText(LoginActivity.this, "Complete todos los campos", Toast.LENGTH_SHORT).show();

                    if(email.isEmpty()) {
                        textEmail.setError("Debe rellenar este campo");
                    }
                    if(password.isEmpty()) {
                        textPassword.setError("Debe rellenar este campo");
                    }

                    if(!password.isEmpty()) {
                        textPassword.setError("Numero insuficiente de caracteres");
                    }
                }
            }
        });

        buttonOlvidar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,ResetContraActivity.class));
            }
        });

        buttonRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegistrarActivity.class));
            }
        });
    }

    private void loginUser () {
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String id = mAuth.getCurrentUser().getUid(); //obtener el id del usuario

                    Database.child("Users").child(id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String rol = dataSnapshot.child("rol").getValue().toString();

                            if(rol.equals("usuarionormal")) {
                                startActivity(new Intent(LoginActivity.this,MapaActivity.class));
                                finish();
                            }
                            else if(rol.equals("admin")) {
                                startActivity(new Intent(LoginActivity.this,MapaAdminActivity.class));
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else {
                    final AlertDialog.Builder alerta = new AlertDialog.Builder(LoginActivity.this);
                    alerta.setMessage("Fallo al iniciar sesion.Compruebe los datos")
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
                    /*Toast.makeText(LoginActivity.this, "No se pudo iniciar sesion,compruebe los datos" +
                            "¡CUIDADO CON LOS ESPACIOS AL FINAL!", Toast.LENGTH_LONG).show();*/
                }
                dialog.dismiss();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mAuth.getCurrentUser() != null) {
            dialog1.setTitle("Entrando");
            dialog1.setMessage("Espere...");
            dialog1.setCanceledOnTouchOutside(false);
            dialog1.show();
            String id = mAuth.getCurrentUser().getUid(); //obtener el id del usuario

            Database.child("Users").child(id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String rol = dataSnapshot.child("rol").getValue().toString();

                    if(rol.equals("usuarionormal")) {
                        startActivity(new Intent(LoginActivity.this,MapaActivity.class));
                        finish();
                    }
                    else if(rol.equals("admin")) {
                        startActivity(new Intent(LoginActivity.this,MapaAdminActivity.class));
                        finish();
                    }
                    dialog1.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
