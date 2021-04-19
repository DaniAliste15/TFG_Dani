package com.dani.firedetection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MiCuentaActivity extends AppCompatActivity {

    private TextView hola,email,nIncendios;

    FirebaseAuth Auth;
    DatabaseReference Database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_cuenta);

        hola = (TextView) findViewById(R.id.hola);
        email = (TextView) findViewById(R.id.infoEmail);
        nIncendios = (TextView) findViewById(R.id.infoNincendios);

        Auth = FirebaseAuth.getInstance();
        Database = FirebaseDatabase.getInstance().getReference();
        
        obtenerInfo();
    }

    private void obtenerInfo() {
        String id = Auth.getCurrentUser().getUid(); //obtener el id del usuario
        Database.child("Users").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    String nombre = dataSnapshot.child("name").getValue().toString();
                    hola.setText("Hola "+nombre);

                    int numIncendios = Integer.parseInt(dataSnapshot.child("numeroincendios").getValue().toString());
                    nIncendios.setText("Ha detectado: "+numIncendios+" incendios");

                    String correo = dataSnapshot.child("email").getValue().toString();
                    email.setText("Su correo es: "+correo);

                }
                else {
                    Toast.makeText(MiCuentaActivity.this, "No se pudo obtener los datos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}

