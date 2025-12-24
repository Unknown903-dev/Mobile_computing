package unkown903-dev.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MAPS_AUTO";

    //location permission num 101
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;
    private static final float DEFAULT_ZOOM = 15f;

    private GoogleMap mMap;

    //gets users location
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private TextView locationInfo;
    private ImageButton myLocationBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        //update text in code
        locationInfo = findViewById(R.id.location_info);
        //detect clicks
        myLocationBtn = findViewById(R.id.btn_my_location);
        //gets the device location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) mapFragment.getMapAsync(this);

        //when use taps my location button checks location permission and center map
        myLocationBtn.setOnClickListener(v -> enableLocationFeatures());
    }

    //when map finish loading
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        MapsInitializer.initialize(this, MapsInitializer.Renderer.LATEST, null);
        //show the zoom in and out button
        mMap.getUiSettings().setZoomControlsEnabled(true);
        //disable blue my location button
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        //checks if location is granted
        checkLocationPermission();
    }

    private void checkLocationPermission() {

        //checks if permission is granted if so then it gives updates on location
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            enableLocationFeatures();
        }
        //if not request users location
        else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void enableLocationFeatures() {
        //check permission granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) return;
        //Put the blue dot on user location and give updates
        mMap.setMyLocationEnabled(true);
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        //gets precise location
        LocationRequest request = new LocationRequest.Builder(
                //update every 10 seconds
                Priority.PRIORITY_HIGH_ACCURACY, 10000)
                .setWaitForAccurateLocation(true)
                .build();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult result) {
                //grabs most recent location
                Location location = result.getLastLocation();

                //if location exist get longitude and lattitude
                if (location != null) {
                    double lat = location.getLatitude();
                    double lon = location.getLongitude();

                    //show location in text view
                    Log.i(TAG, "Location update: " + lat + ", " + lon);

                    locationInfo.setText(String.format("Lat: %.5f, Lng: %.5f", lat, lon));

                    LatLng pos = new LatLng(lat, lon);
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(pos).title("You are here"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, DEFAULT_ZOOM));
                }
            }
        };

        //checks if approximate or precise location permission if not it exits
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        //update location
        fusedLocationClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper());
    }

    //pauses stops recieving updates, avoid updating map, and saves battery
    @Override
    protected void onPause() {
        super.onPause();
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    //resume and check if permission still there and start updating
    @Override
    protected void onResume() {
        super.onResume();
        if (mMap != null &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        }
    }

    //checks permissions if allow call enable location feature
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableLocationFeatures();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
