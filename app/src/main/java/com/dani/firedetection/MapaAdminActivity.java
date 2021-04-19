package com.dani.firedetection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;

public class MapaAdminActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    FirebaseAuth mAutho;
    DatabaseReference Database;
    private int flag,f_mapa = 0;

    private Button btnHibrido,btnTerreno;

    private PendingIntent pendingIntent;
    private final static String CHANNEL_ID = "NOTIFICACION";
    private final static int NOTIFICACION_ID = 0;

    private ArrayList<Marker> tmpRealTimeMarkers = new ArrayList<>();
    private ArrayList<Marker> realTimeMarkers = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_admin);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //icono en el action bar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        /////************/////
        //Color ActionBar
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorAdmin )));
        /////***********////

        mAutho = FirebaseAuth.getInstance();
        Database = FirebaseDatabase.getInstance().getReference();

        btnHibrido = (Button) findViewById(R.id.btnHib);
        btnTerreno = (Button) findViewById(R.id.btnTerr);

        asignaValor();

        btnHibrido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            }
        });

        btnTerreno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            }
        });
    }


    private void asignaValor() {
        flag = 1;
    }

    private void setPendingIntent() {
        Intent intent = new Intent(this,MapaAdminActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        //stackBuilder.addParentStack(NotificacionActivity.class);
        stackBuilder.addNextIntent(intent);
        pendingIntent = stackBuilder.getPendingIntent(1, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void createNotificacion2() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR_0_1) {
            CharSequence name = "Notificacion";
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,name, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private void createNotificacion() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_whatshot_black_24dp);
        builder.setContentTitle("Atencion");
        builder.setContentText("Tiene usted un posible incendio");
        builder.setColor(Color.BLUE);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setLights(Color.GREEN, 1000,1000);
        builder.setVibrate(new long[]{1000,1000,1000,1000});
        builder.setDefaults(Notification.DEFAULT_SOUND);
        builder.setAutoCancel(true);

        builder.setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        notificationManagerCompat.notify(NOTIFICACION_ID,builder.build());
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true); //zoom + -
        String id = "fR9J3z0qhwNGVRUcjU66KIYpA2F2";


        if (f_mapa == 0) {
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);// para que nos pinte la
            // vista hibrida solo al principio
            f_mapa = 1;
        }

        Database.child("Listado incendios").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(Marker marker : realTimeMarkers) {
                    marker.remove();
                }

                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    TraerLatLong mp = snapshot.getValue(TraerLatLong.class);
                    Double latitud = mp.getLatitud();
                    Double longitud = mp.getLongitud();
                    String humo = mp.getHumo();
                    String columna = mp.getColumna();
                    String vegetacion = mp.getVegetacion();

                    LatLng incendio = new LatLng(latitud,longitud);

                    /************Ajuste de deciamles***********/
                    BigDecimal b1 = new BigDecimal(latitud);
                    BigDecimal b2 = new BigDecimal(longitud);
                    MathContext m = new MathContext(6);
                    /*******************************************/

                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(incendio).title("("+""+b1.round(m)+" / "+b2.round(m)+")"
                            +"  "+humo+" - "+columna+" - "+vegetacion+" ");

                    if(latitud != 0.0 && longitud != 0.0) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(incendio, 12));
                    }
                    tmpRealTimeMarkers.add(mMap.addMarker(markerOptions));

                    if(mAutho.getCurrentUser() != null) {
                        String id = mAutho.getCurrentUser().getUid(); //obtener el id del usuario

                        Database.child("Users").child(id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String rol = dataSnapshot.child("rol").getValue().toString();

                                if(rol.equals("usuarionormal")) {
                                    //nada
                                }
                                else if(rol.equals("admin")) {
                                    if(flag != 1) {
                                        setPendingIntent();
                                        createNotificacion2();
                                        createNotificacion();
                                    }
                                    flag = 0;
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }


                }

                realTimeMarkers.clear();
                realTimeMarkers.addAll(tmpRealTimeMarkers);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override public boolean onCreateOptionsMenu(Menu mimenu) {

          /*OTRA FORMA DE HACERLO
          MenuInflater inflater = getMenuInflater();
          inflater.inflate(R.menu.menu_en_activity,mimenu);

        return super.onCreateOptionsMenu(mimenu);*/

        getMenuInflater().inflate(R.menu.menu_mapa_admin,mimenu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem opcion_menu) {
        int id = opcion_menu.getItemId();

        if(id == R.id.salir){
            final AlertDialog.Builder alerta = new AlertDialog.Builder(MapaAdminActivity.this);
            alerta.setMessage("¿Desea cerrar sesion?")
                    .setCancelable(false)
                    .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mAutho.signOut();
                            startActivity(new Intent(MapaAdminActivity.this,LoginActivity.class));
                            finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            AlertDialog titulo = alerta.create();
            titulo.setTitle("Atención");
            titulo.show();

            return true;
        }

        return super.onOptionsItemSelected(opcion_menu);
    }
}
