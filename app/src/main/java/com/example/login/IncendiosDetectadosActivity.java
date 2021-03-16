package com.example.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class IncendiosDetectadosActivity extends AppCompatActivity {

    FirebaseAuth Auth;
    DatabaseReference Database;
    private TextView tvIncendioUno;
    public int numeroIncendios;
    private Button botonVer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incendios_detectados);

        Auth = FirebaseAuth.getInstance();
        Database = FirebaseDatabase.getInstance().getReference();

        tvIncendioUno = (TextView)findViewById(R.id.incendio1);

        botonVer = (Button)findViewById(R.id.btnVerIncendios);

        obtenerIncendios();

        botonVer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publicarIncendios();
            }
        });


        /*if(numIncendios == 0) {
            Toast.makeText(this, "Todavia no ha detectado ningun incendio", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MapaActivity.class));
            finish();
        }*/
       // Toast.makeText(this, "num:"+numeroIncendios, Toast.LENGTH_SHORT).show();

    }

    private void publicarIncendios() {

            int incendios = numeroIncendios;

            if(incendios != 0) {
                String id = Auth.getCurrentUser().getUid(); //obtener el id del usuario
                Database.child("Users").child(id).child("Incendio" + (incendios - 1)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            //numIncendios = Integer.parseInt(dataSnapshot.child("numeroincendios").getValue().toString());
                            String humo = dataSnapshot.child("humo").getValue().toString();
                            String columna = dataSnapshot.child("columna").getValue().toString();
                            String vegetacion = dataSnapshot.child("vegetacion").getValue().toString();
                            String latitud = dataSnapshot.child("latitud").getValue().toString();
                            String longitud = dataSnapshot.child("longitud").getValue().toString();


                            tvIncendioUno.setText("Humo: " + humo + System.getProperty("line.separator") + "Columna: " + columna
                                    + System.getProperty("line.separator") + "Vegetacion: " + vegetacion + System.getProperty("line.separator")
                                    + "Latitud: " + latitud + System.getProperty("line.separator") + "Longitud: " + longitud);


                        } else {
                            Toast.makeText(IncendiosDetectadosActivity.this, "No se pudo obtener los datos", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            else {
                Toast.makeText(this, "No ha detectado ningun incendio todavia", Toast.LENGTH_SHORT).show();
            }


    }

    private void obtenerIncendios() {

        String id = Auth.getCurrentUser().getUid(); //obtener el id del usuario
        Database.child("Users").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {

                    int numIncendios = Integer.parseInt(dataSnapshot.child("numeroincendios").getValue().toString());
                    //Toast.makeText(IncendiosDetectadosActivity.this, "num:"+numIncendios, Toast.LENGTH_SHORT).show();
                    //return numIncendios;
                    numeroIncendios = numIncendios;
                    //Toast.makeText(IncendiosDetectadosActivity.this, "pepeeee:"+numeroIncendios, Toast.LENGTH_SHORT).show();


                }
                else {
                    Toast.makeText(IncendiosDetectadosActivity.this, "No se pudo obtener el numero de incendios", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
