package com.example.wifinavindoor;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class NavigationActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private MapView mapView;
    private GoogleMap googleMap;
    private LocationManager locationManager;
    private Marker currentLocationMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        // Initialize the map view
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        // Set click listener for the stop navigation button
        Button stopNavigationButton = findViewById(R.id.stopNavigationButton);
        stopNavigationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopNavigation();
            }
        });

        // Initialize location manager
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        // Request location permission
        requestLocationPermission();

        // Configure map settings
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setScrollGesturesEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(true);
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            startLocationUpdates();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                // Location permission denied
            }
        }
    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);
        }
    }

    private void stopLocationUpdates() {
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (currentLocationMarker == null) {
            currentLocationMarker = googleMap.addMarker(new MarkerOptions().position(latLng).title("Current Location"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
        } else {
            currentLocationMarker.setPosition(latLng);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        stopLocationUpdates();
    }

    @Override
    public void onProviderEnabled(String provider) {
        // Not used
    }

    @Override
    public void onProviderDisabled(String provider) {
        // Not used
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Not used
    }

    private void stopNavigation() {
        // TODO: Implement stop navigation logic
    }
}


