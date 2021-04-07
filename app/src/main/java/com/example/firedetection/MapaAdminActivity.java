package com.example.firedetection;

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
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

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

import java.util.ArrayList;

public class MapaAdminActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    FirebaseAuth mAutho;
    DatabaseReference Database;
    private int flag = 0;

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

        mAutho = FirebaseAuth.getInstance();
        Database = FirebaseDatabase.getInstance().getReference();
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
        builder.setContentTitle("Noticacion");
        builder.setContentText("Esto es un prueba a ver como sale");
        builder.setColor(Color.BLUE);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setLights(Color.GREEN, 1000,1000);
        builder.setVibrate(new long[]{1000,1000,1000,1000});
        builder.setDefaults(Notification.DEFAULT_SOUND);

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
        //String id = mAutho.getCurrentUser().getUid(); //obtener el id del usuario

        /*Database.child("Listado incendios").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    TraerLatLong mp = snapshot.getValue(TraerLatLong.class);
                    Double latitud = mp.getLatitud();
                    Double longitud = mp.getLongitud();

                    LatLng incendio = new LatLng(latitud,longitud);

                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(incendio).title(""+latitud+" / "+longitud);
                    if(latitud != 0.0 && longitud != 0.0) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(incendio, 14));
                    }
                    tmpRealTimeMarkers.add(mMap.addMarker(markerOptions));

                }

                realTimeMarkers.clear();
                realTimeMarkers.addAll(tmpRealTimeMarkers);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
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

                    LatLng incendio = new LatLng(latitud,longitud);

                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(incendio).title(""+latitud+" / "+longitud);
                    if(latitud != 0.0 && longitud != 0.0) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(incendio, 12));
                    }
                    tmpRealTimeMarkers.add(mMap.addMarker(markerOptions));

                    //if(flag != 0) {
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
                                    if(flag != 0) {
                                        setPendingIntent();
                                        createNotificacion2();
                                        createNotificacion();
                                    }
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

                flag = 1;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
    }

    /*public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true); //zoom + -

        Database.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(Marker marker : realTimeMarkers) {
                    marker.remove();
                }

                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    TraerLatLong mp = snapshot.getValue(TraerLatLong.class);
                    Double latitud = mp.getLatitud();
                    Double longitud = mp.getLongitud();

                    LatLng incendio = new LatLng(latitud,longitud);

                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(incendio).title(""+latitud+" / "+longitud);
                    if(latitud != 0.0 && longitud != 0.0) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(incendio, 14));
                    }
                    tmpRealTimeMarkers.add(mMap.addMarker(markerOptions));

                }

                realTimeMarkers.clear();
                realTimeMarkers.addAll(tmpRealTimeMarkers);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
}
*/
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
