package com.dani.firedetection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

//
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

//////
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.app.FragmentActivity;//esta
//import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.math.BigDecimal;
import java.math.MathContext;
//

public class MapaActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public FirebaseAuth mAutho;
    private double longi=0.0,lati=0.0;
    private double longiIncendio=0.0, latiIncendio=0.0;
    public int flag = 0, f_mapa = 0, f_clik = 0;
    public Marker posicion, incendio;
    public MarkerOptions markerOptions = new MarkerOptions();
    private Button btnInformacion,btnHibrido,btnTerreno;
    DatabaseReference Database;
    private String idUsuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //icono en el action bar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        /////************/////

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mAutho = FirebaseAuth.getInstance();
        Database = FirebaseDatabase.getInstance().getReference();


        btnInformacion = (Button) findViewById(R.id.btnInfo);
        btnHibrido = (Button) findViewById(R.id.btnHib);
        btnTerreno = (Button) findViewById(R.id.btnTerr);


        btnInformacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(f_clik == 0) {
                    Toast.makeText(MapaActivity.this, "Debe seleccionar un posible incendio", Toast.LENGTH_SHORT).show();
                }
                else {
                    //startActivity(new Intent(MapaActivity.this, DatosIncendioActivity.class));
                    Intent coord = new Intent(MapaActivity.this, DatosIncendioActivity.class);
                    coord.putExtra("lati",latiIncendio);
                    coord.putExtra("longi",longiIncendio);
                    startActivity(coord);
                }
            }
        });

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

        /********************************************* PERMISOS Y DE MAS  /*********************************************/
        setUpMapIfNeeded();
        //mTapTextView = (TextView) findViewById(R.id.tap_text);

        //permisos
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if(permissionCheck == PackageManager.PERMISSION_DENIED){

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) MapaActivity.this.getSystemService(Context.LOCATION_SERVICE);

// Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                //tvgps.setText(""+location.getLatitude()+" "+location.getLongitude());
                lati = location.getLatitude();
                longi = location.getLongitude();
                //mMap.clear();
                onMapReady(mMap);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

// Register the listener with the Location Manager to receive location updates
      /*  int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);*/
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

        /********************************************* FIN PERMISOS Y DE MAS  /*********************************************/
    }

    private void setUpMap()
    {
        //mMap.setOnMapClickListener(this);
       // mMap.setOnMapLongClickListener(this);
    }

    private void setUpMapIfNeeded() {
        // Hacer una comprobación nula para confirmar que ya no hemos instanciado el mapa.
        if (mMap == null) {
            // Intenta obtener el mapa desde el SupportMapFragment.
            SupportMapFragment mMapo = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
            mMapo.getMapAsync(this);
            // Comprueba si hemos tenido éxito en la obtención del mapa.

            if (mMap != null) {
                setUpMap();
            }
        }
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
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                LatLng sydney25 = new LatLng (latLng.latitude,latLng.longitude);
                latiIncendio = latLng.latitude;
                longiIncendio = latLng.longitude;

                /************Ajuste de deciamles***********/
                BigDecimal b1 = new BigDecimal(latiIncendio);
                BigDecimal b2 = new BigDecimal(longiIncendio);
                MathContext m = new MathContext(8);
                /*******************************************/

                if(f_clik !=0) {
                    incendio.remove();
                }
                incendio = mMap.addMarker(new MarkerOptions().position(sydney25).title(b1.round(m)+" / "+b2.round(m)));
                f_clik=1;
            }
        });

        if (f_mapa != 0) {
            posicion.remove();
        }

        if (f_mapa == 0) {
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);// para que nos pinte la
            // vista hibrida solo al principio
            f_mapa = 1;
        }

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(lati,longi);
        posicion = mMap.addMarker(new MarkerOptions().position(sydney).title("Usted esta aqui")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

        if (flag == 0 && lati !=0.0 && longi !=0.0) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 14));
            flag = 1;
        }
    }

    @Override public boolean onCreateOptionsMenu(Menu mimenu) {
        getMenuInflater().inflate(R.menu.menu_mapa,mimenu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem opcion_menu) {
        int id = opcion_menu.getItemId();

        if(id == R.id.elimIn){
            if(f_clik!=0) {
                incendio.remove();
                Toast.makeText(this, "Eliminado", Toast.LENGTH_SHORT).show();
                f_clik = 0;
            }
            else {
                Toast.makeText(this, "No hay un incendio selecionado", Toast.LENGTH_SHORT).show();
            }

            return true;
        }

        if(id == R.id.salir){
            final AlertDialog.Builder alerta = new AlertDialog.Builder(MapaActivity.this);
            alerta.setMessage("¿Desea cerrar sesion?")
                    .setCancelable(false)
                    .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mAutho.signOut();
                            startActivity(new Intent(MapaActivity.this,LoginActivity.class));
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

        if(id == R.id.listaIncendios){
            startActivity(new Intent(MapaActivity.this,IncendiosDetectadosActivity.class));
            return true;
        }

        if(id == R.id.modifDados){
            startActivity(new Intent(MapaActivity.this, ModfiDatosActivity.class));
            return true;
        }

        if(id == R.id.miCuenta){
            startActivity(new Intent(MapaActivity.this, MiCuentaActivity.class));
            return true;
        }

        if(id == R.id.infoMapa){
            startActivity(new Intent(MapaActivity.this, InfoMapaActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(opcion_menu);
    }
}
