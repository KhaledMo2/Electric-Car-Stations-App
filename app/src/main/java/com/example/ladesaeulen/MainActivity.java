package com.example.ladesaeulen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.slider.Slider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,OnMyLocationButtonClickListener, OnMyLocationClickListener{
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static GoogleMap mMap;
    private static Location loc;

    final int MY_PERMISSIONS_STORAGE_INTENRET = 1;

    private RequestService mService = new RequestService();

    private RequestService.RequestServiceBinder binder;
    boolean mBound = false;

    private static String filePath = "NZSE";
    private static String csvFile; // liefert die Downloadfunktion
    static ArrayList<ChargingStation> stations= new ArrayList<ChargingStation>();
    static ArrayList<MapMarkers> markers=new ArrayList<MapMarkers>();

    private static StationManager manager = new StationManager ();
    private static MarkerManager markerManager= new MarkerManager ();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        BottomNavigationView  navigationView = findViewById(R.id.bottom_navigation);
        navigationView.setSelectedItemId(R.id.home);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        return true;
                    case R.id.search:
                        startActivity(new Intent(getApplicationContext(),SearchActivity.class));
                        overridePendingTransition(0,0);

                        return true;
                }
                return false;

            }
        });
        Button nearby = findViewById(R.id.nearby);
        nearby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nearby.getText() =="Hide Markers") {
                    mMap.clear();
                    nearby.setText("Get nearby Stations");
                }
                else {


                    ArrayList<MapMarkers> x = new ArrayList<MapMarkers>();
                    x = markerManager.getnearby_list();
                    for (int i = 0; i < x.size(); i++) {
                        mMap.addMarker(new MarkerOptions().position(x.get(i).getLocation()).title(x.get(i).getTitel()));

                    }
                    nearby.setText("Hide Markers");



                }

            }
        });

        Slider s = findViewById(R.id.continuousSlider);
        s.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {


            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                markerManager.setNearby_storage(s.getValue());
                manager.setNearby_station_storage(s.getValue());

            }

            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                Integer slide_value = Math.round(value);
            }
        });


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }



        // Bind to RequestService
        Intent myIntent = new Intent(this, RequestService.class);
        // startService(myIntent); oder alternativ:
        bindService(myIntent, mConnection, Context.BIND_AUTO_CREATE);

        // Permission grant/gewähren
        String[] permissions =
                {Manifest.permission.INTERNET,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(this, permissions,
                MY_PERMISSIONS_STORAGE_INTENRET);

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        enableMyLocation();
        String //url = "https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Energie/Unternehmen_Institutionen/E_Mobilitaet/Ladesaeulenkarte_DatenbankauszugCSV.csv;?__blob=publicationFile&v=25";
                url = "https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Energie/Unternehmen_Institutionen/E_Mobilitaet/Ladesaeulenregister_CSV.csv?__blob=publicationFile&v=37";
        // **********************
        String fPath;
        String fileName;
        String ladestationen = "ladestationen.txt"; // Name der lokalen Datei
        //


  float zoomLevel = (float) 10.0;
        LatLng myhome = new LatLng (49.855714, 8.637577);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myhome, zoomLevel));


    }

    private void enableMyLocation() {
        // [START maps_check_location_permission]
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
            }
        } else {
            // Permission to access the location is missing. Show rationale and request permission
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        }
        // [END maps_check_location_perm
    }


    /* Creates the menu items */
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }




    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    //--------------------------    Serviceverbindung einrichten
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // an den RequestService binden,
            // service-Objekt casting auf IBinder und LocalService instance erhalten
            binder = (RequestService.RequestServiceBinder) service;
            mService = binder.getService();
            // callback setzen
            mService.setCallback(getHandler());
            String url = "https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Energie/Unternehmen_Institutionen/E_Mobilitaet/Ladesaeulenregister_CSV.csv?__blob=publicationFile&v=37";
            String ladestationen = "ladestationen.txt"; // Name der lokalen Datei
            if (mService != null&&csvFile==null)
            {
                binder.runURLDownload("download", url, filePath, ladestationen);
            }

            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
    //--------------------------

    //--------------------------Handler einrichten:
    // Callbacks vereinbaren für Service Binding,
    // weiterleiten an bindService() für Ergebnismitteilung
    private Handler getHandler() {
        final Handler callbackHandler = new Handler() {
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                csvFile = (String) bundle.get(RequestService.FILEPATH);
                // Datei einlesen
                MainActivity.csvRead();
                String uniqueId = (String) bundle.get(RequestService.UNIQUEID);
                String note = (String) bundle.get(RequestService.NOTIFICATION);
                Toast.makeText(MainActivity.this, uniqueId + " file: " + csvFile + " Bytes: " + note, Toast.LENGTH_LONG).show();
            }// handleMessage
        };
        return callbackHandler;
    }

    //--------------------------


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_STORAGE_INTENRET: {
                // wenn die Anfrage gecancelled wird, sind die Ergnisfelder leer.
                if (grantResults.length > 0) {
                    for (int grant = 0; grant < grantResults.length; grant++) {
                        if (grantResults[grant] == PackageManager.PERMISSION_GRANTED)
                            // Permissions wurden gewährt
                            System.out.println(permissions[grant] + " vorhanden");
                            // Berechtigungen stehen zu Verfügung, Zugriffe ausführen ..
                        else
                            System.out.println(permissions[grant] + "  n i c h t  vorhanden");
                        // Permissions werden abgelehnt,
                        // spezifische Zugriffe werden nicht ausgeführt
                    }
                }
                return;
            }
            // ... u.U. Prüfung anderer/weiterer Permissions
        }
    }

    public static void csvRead() {
        String csvline = ""; // eingelesene csvZeile
        String delimiter = ";"; // Trennzeichen
        File myFile; // Fileobjekt
        LatLng location = new LatLng(0,0);
        final int provider  = 0;
        final int strasse  = 1; // Position in der csvZeile
        final int postanzahl  = 4;
        final int ort      = 5;
        final int latValue = 8;
        final int lonValue = 9;
        final int moduleType = 12;
        final int connectionNember = 11;
        double curLat = 49.855714;
        double curLon =8.637577;
        String strase ="";
        // ... weitere Positionen
        float lat, lon; // Breiten- und Längenangabe
        try {
            // lokale Datei ansprechen
            myFile = new File(csvFile);
//                    Environment.getExternalStorageDirectory().getPath() + );
            // new File(getApplicationContext().getExternalFilesDir("NZSE").getPath() + "/" + csvFile);
            FileInputStream fIn = new FileInputStream(myFile);
            InputStreamReader isr = new InputStreamReader(fIn, "Windows-1252");// StandardCharsets.UTF_16);
            BufferedReader myReader = new BufferedReader(isr);

            System.out.println("csv Einlesen starten ");
            System.out.println("... die ersten Zeilen überlesen");
            for (int n = 0; n < 20; n++) {
                myReader.readLine();
            } // for

            int zeilen = 0;
            String[] ladestation = null;
            String select = "--";
            while ((csvline = myReader.readLine()) != null) // Dateiende?
            {
                ChargingStation s = new ChargingStation();
                MapMarkers m = new MapMarkers();
                zeilen++;
                ladestation = csvline.split(delimiter);

                if (ladestation.length>=10){
                    if (isNumeric (ladestation[ort]))
                    {

                       ladestation[latValue + 1] = ladestation[latValue + 1].replace(",", ".");
                        ladestation[lonValue + 1] = ladestation[lonValue + 1].replace(",", ".");
                        lat = Float.parseFloat(ladestation[latValue + 1]);
                        lon = Float.parseFloat(ladestation[lonValue + 1]);
                        s.setModule_type(ladestation[moduleType+1]);
                        s.setNumber(ladestation[connectionNember+1]);

                    }


                    else {



                        System.out.println("    str = " + ladestation[strasse]);
                        System.out.println("    ort = " + ladestation[ort]);
                        System.out.println("    latValue = " + ladestation[latValue]);
                        System.out.println("    lonValue = " + ladestation[lonValue]);
                        System.out.println("    lonValue = " + ladestation[moduleType]);
                        ladestation[latValue] = ladestation[latValue].replace(".", "");
                        ladestation[latValue] = ladestation[latValue].replace(",", ".");
                        ladestation[lonValue] = ladestation[lonValue].replace(",", ".");
                        lat = Float.parseFloat(ladestation[latValue]);

                        if (Objects.equals(ladestation[lonValue], "25.01.2022")) {
                            System.out.println("    lonValue = " + ladestation[lonValue-1]);
                            lon = Float.parseFloat(ladestation[lonValue-1]);
                            System.out.println("    lonValue = " + ladestation[lonValue]);

                        }
                        else
                        {
                           // System.out.println("    lonValue = " + ladestation[lonValue]);
                            lon = Float.parseFloat(ladestation[lonValue]);

                        }
                        s.setModule_type(ladestation[moduleType]);
                        s.setNumber(ladestation[connectionNember]);

                    }

                    s.setOperator(ladestation[provider]);
                    s.setPostal_code(ladestation[postanzahl]);
                    s.setArea(ladestation[ort]);
                    s.setLocation(ladestation[strasse]);
                    s.setdistance(lat,lon,curLat,curLon);

                    stations.add(s);
                    location = new LatLng(lat, lon);
                    m.setLocation(location);
                    m.setTitel(ladestation[0]);
                    m.setdistance(lat,lon,curLat,curLon);
                    markers.add(m);
                   // mMap.addMarker(new MarkerOptions().position(location).title(ladestation[0]));
                }

            }//while

        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } // try-catch

        manager.setStation_list(stations);
        markerManager.setMarker_list(markers);


    } // csvRead

    @Override
    public void finish() {
        System.out.println("******  bye bye");
        unbindService(mConnection);
        super.finish();
    }

    public static boolean isNumeric(String string) {
        int intValue;

        System.out.println(String.format("Parsing string: \"%s\"", string));

        if(string == null || string.equals("")) {
            System.out.println("String cannot be parsed, it is null or empty.");
            return false;
        }

        try {
            intValue = Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e) {
            System.out.println("Input String cannot be parsed to Integer.");
        }
        return false;
    }

}