package com.example.wifinavindoor;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SaveLocationActivity extends AppCompatActivity {

    private EditText buildingEditText;
    private EditText nameEditText;
    private Button calibrateButton;
    private Button saveButton;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_location);

        buildingEditText = findViewById(R.id.buildingEditText);
        nameEditText = findViewById(R.id.nameEditText);
        calibrateButton = findViewById(R.id.calibrateButton);
        saveButton = findViewById(R.id.saveButton);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        calibrateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calibrateLocation();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveLocation();
            }
        });
    }

    private void calibrateLocation() {
        // Implement your calibration logic here
        // For example, you can use sensors or algorithms to calibrate the location

        // Once the calibration is done, you can update the UI or perform any other necessary actions
        Toast.makeText(this, "Location calibrated", Toast.LENGTH_SHORT).show();
    }

    private void saveLocation() {
        String buildingName = buildingEditText.getText().toString().trim();
        String locationName = nameEditText.getText().toString().trim();

        // Check if the building name and location name are not empty
        if (TextUtils.isEmpty(buildingName) || TextUtils.isEmpty(locationName)) {
            Toast.makeText(this, "Please enter the building name and location name", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the location permission is granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Request the location permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        // Get the current location using the fusedLocationClient
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    // Create a DatabaseReference for the "locations" node in the Firebase Realtime Database
                    DatabaseReference locationsRef = FirebaseDatabase.getInstance().getReference().child("locations");

                    // Create a new child node with a unique key under "locations"
                    DatabaseReference newLocationRef = locationsRef.push();

                    // Create a HashMap to store the location data
                    Map<String, Object> locationData = new HashMap<>();
                    locationData.put("building", buildingName);
                    locationData.put("name", locationName);
                    locationData.put("latitude", latitude);
                    locationData.put("longitude", longitude);

                    // Set the location data in the new child node
                    newLocationRef.setValue(locationData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SaveLocationActivity.this, "Location saved", Toast.LENGTH_SHORT).show();
                                // Go back to MainActivity
                                finish();
                            } else {
                                Toast.makeText(SaveLocationActivity.this, "Failed to save location", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
