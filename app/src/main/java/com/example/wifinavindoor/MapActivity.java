package com.example.wifinavindoor;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

//import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private MapView mapView;
    private GoogleMap googleMap;
    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationClient;
    private DatabaseReference locationsRef;
    private ValueEventListener locationsValueEventListener;
    private Marker selectedLocationMarker;
    private Polyline polyline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Initialize the map view
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationsRef = FirebaseDatabase.getInstance().getReference().child("locations");

        // Set click listener for the stop navigation button
        Button stopNavigationButton = findViewById(R.id.stopNavigationButton);
        stopNavigationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopNavigation();
                navigateToDestinationActivity();
            }
        });
    }

    private void navigateToDestinationActivity() {
        Intent intent = new Intent(MapActivity.this, NavigateToDestinationActivity.class);
        startActivity(intent);
        finish(); // Optionally, you can call finish() to close the MapActivity if you don't need it anymore
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }

        // Set click listener for map markers
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.equals(selectedLocationMarker)) {
                    Toast.makeText(MapActivity.this, "Selected location clicked", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });

        // Retrieve and display all saved locations from Firebase
        retrieveLocations();
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Permission already granted, show current location on map
            showCurrentLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, show current location on map
                showCurrentLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                    // Add a marker for the current location
                    googleMap.addMarker(new MarkerOptions().position(latLng).title("Current Location"));
                }
            }
        });
    }


    private void retrieveLocations() {
        locationsValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                googleMap.clear();

                for (DataSnapshot locationSnapshot : dataSnapshot.getChildren()) {
                    LocationData locationData = locationSnapshot.getValue(LocationData.class);
                    if (locationData != null) {
                        LatLng latLng = new LatLng(locationData.getLatitude(), locationData.getLongitude());
                        googleMap.addMarker(new MarkerOptions().position(latLng).title(locationData.getName()));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MapActivity.this, "Failed to retrieve locations", Toast.LENGTH_SHORT).show();
            }
        };

        locationsRef.addValueEventListener(locationsValueEventListener);
    }

    private void startNavigation(LatLng destinationLatLng) {
        if (currentLocation == null) {
            Toast.makeText(this, "Current location not available", Toast.LENGTH_SHORT).show();
            return;
        }

        // Draw polyline
        PolylineOptions polylineOptions = new PolylineOptions()
                .add(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
                .add(destinationLatLng)
                .color(Color.BLUE)
                .width(5);
        polyline = googleMap.addPolyline(polylineOptions);

        // Calculate and display distance and direction
        float[] distanceResults = new float[1];
        Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(),
                destinationLatLng.latitude, destinationLatLng.longitude, distanceResults);
        float distance = distanceResults[0];

        float bearing = currentLocation.bearingTo(createLocationFromLatLng(destinationLatLng));
        String direction = getDirectionString(bearing);

        Toast.makeText(this, "Distance: " + distance + " meters, Direction: " + direction, Toast.LENGTH_SHORT).show();
    }

    private void stopNavigation() {
        if (polyline != null) {
            polyline.remove();
            polyline = null;
        }
    }

    private Location createLocationFromLatLng(LatLng latLng) {
        Location location = new Location("");
        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);
        return location;
    }

    private String getDirectionString(float bearing) {
        String[] directions = {"N", "NE", "E", "SE", "S", "SW", "W", "NW", "N"};
        int index = (int) ((bearing / 45) + 0.5);
        return directions[index];
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (locationsRef != null && locationsValueEventListener != null) {
            locationsRef.removeEventListener(locationsValueEventListener);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
