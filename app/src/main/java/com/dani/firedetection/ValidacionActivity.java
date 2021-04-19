package com.dani.firedetection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;
import java.util.Map;

public class ValidacionActivity extends AppCompatActivity {

    private TextView tvHumo,tvVegetacion,tvColumna,tvCoordenadas;
    public BigDecimal latiRecortada,longiRecortada;
    private Button enviarAviso;
    private String humo,vegetacion,columna;
    private Double latitud,longitud;
    public int flag,flagIn;
    private ProgressDialog dialog;


    FirebaseAuth Auth;
    DatabaseReference Database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validacion);
        enviarAviso = (Button) findViewById(R.id.enviarAviso);

        Auth = FirebaseAuth.getInstance();
        Database = FirebaseDatabase.getInstance().getReference();
        dialog = new ProgressDialog(this);

        recibirDatos();

        obtenerNumIncendios();

        enviarAviso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //habria que poner un progress dialog cuando lo subamos a firebase
                dialog.setTitle("Enviando aviso");
                dialog.setMessage("Espere...");
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                subirDatos();
            }
        });
    }

    private void obtenerNumIncendios() {
        String id = Auth.getCurrentUser().getUid(); //obtener el id del usuario
        /////OBTENER NUMERO INCENDIOS
        Database.child("Users").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    int nIncendios = Integer.parseInt(dataSnapshot.child("numeroincendios").getValue().toString());
                    flagIn = nIncendios;
                    flag = nIncendios + 1;
                    //Toast.makeText(ValidacionActivity.this, "Nincen:"+ nIncendios[0], Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(ValidacionActivity.this, "No se puedo obtener el numero de incendios", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        ////FIN OBTENER NUMERO INCENDIOS
    }

    public void subirDatos() {
        String id = Auth.getCurrentUser().getUid(); //obtener el id del usuario

        Map<String, Object> datos = new HashMap<>();
        datos.put("latitud",latitud);
        datos.put("longitud",longitud);
        datos.put("humo",humo);
        datos.put("vegetacion",vegetacion);
        datos.put("columna",columna);


        Database.child("Users").child(id).child("Incendio"+flagIn).setValue(datos).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    startActivity(new Intent(ValidacionActivity.this,EnviadoAvisoActivity.class));
                    finish();
                }
                else {
                    Toast.makeText(ValidacionActivity.this, "No se pudo subir el incendio", Toast.LENGTH_SHORT).show();
                }

            }
        });


        Database.child("Users").child(id).child("numeroincendios").setValue(flag).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task2) {
                if(task2.isSuccessful()){
                    //Toast.makeText(ValidacionActivity.this, "IncreIn:"+flag, Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(ValidacionActivity.this, "No se pudo actualizar numero incendios", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Database.child("Listado incendios").child("Incendio1").setValue(datos).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task3) {
                if(task3.isSuccessful()) {
                    //nada
                }
                else {
                    Toast.makeText(ValidacionActivity.this, "No se puedo crear en el listado", Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        dialog.dismiss();
    }



    private void recibirDatos() {
        tvHumo = (TextView) findViewById(R.id.tvHumo);
        tvVegetacion = (TextView) findViewById(R.id.tvVegetacion);
        tvColumna = (TextView) findViewById(R.id.tvColumna);
        tvCoordenadas = (TextView) findViewById(R.id.tvCoordenadas);

        Bundle extras = getIntent().getExtras();

         humo = extras.getString("humo");
         vegetacion = extras.getString("vegetacion");
         columna = extras.getString("columna");
         latitud = extras.getDouble("latitud");
         longitud = extras.getDouble("longitud");

        /************Ajuste de deciamles***********/
        BigDecimal b1 = new BigDecimal(latitud);
        BigDecimal b2 = new BigDecimal(longitud);
        MathContext m = new MathContext(8);
        /*******************************************/

        latiRecortada = b1.round(m);
        longiRecortada = b2.round(m);

        tvHumo.setText(humo);
        tvVegetacion.setText(vegetacion);
        tvColumna.setText(columna);
        tvCoordenadas.setText(latiRecortada +" / "+longiRecortada);
    }
}

