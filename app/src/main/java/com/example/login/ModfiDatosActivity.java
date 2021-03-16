package com.example.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class ModfiDatosActivity extends AppCompatActivity {

    private EditText textPassword,textEmail;
    private Button buttonModificar;

    private String email = "";
    private String password = "";

    FirebaseAuth Auth;
    DatabaseReference Database;

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modfi_datos);

        textPassword = (EditText) findViewById(R.id.editTextPassword);
        textEmail = (EditText) findViewById(R.id.editTextEmail);

        buttonModificar = (Button) findViewById(R.id.btnModificar);

        Auth = FirebaseAuth.getInstance();
        Database = FirebaseDatabase.getInstance().getReference();

        dialog = new ProgressDialog(this);

        buttonModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = textEmail.getText().toString();
                password = textPassword.getText().toString();

                if (!email.isEmpty() && !password.isEmpty()) {
                    if (password.length() >= 6) {
                        modificarDatos();
                    }
                    else {
                        textPassword.setError("Debe de tener minimo 6 caracteres");
                    }

                }
                else {
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

    private void modificarDatos() {
        Map<String,Object> datos = new HashMap<>();
        datos.put("email",email);
        datos.put("password",password);

        String id = Auth.getCurrentUser().getUid(); //obtener el id del usuario
        
        Database.child("Users").child(id).updateChildren(datos).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(ModfiDatosActivity.this, "Los datos se han actualizado correctamente", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ModfiDatosActivity.this,MapaActivity.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ModfiDatosActivity.this, "No se han podido actualizar los datos", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ModfiDatosActivity.this,MapaActivity.class));
                finish();
            }
        });
        //dialog.dismiss();
    }
}
