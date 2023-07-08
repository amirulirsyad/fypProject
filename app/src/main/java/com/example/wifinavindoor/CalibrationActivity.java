package com.example.wifinavindoor;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class CalibrationActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibration);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            startCalibration();
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCalibration();
            } else {
                Toast.makeText(this, "Permission denied. Please enable Location permission to calibrate the location.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void startCalibration() {
        // TODO: Implement the location calibration process
        Toast.makeText(this, "Calibrating Location", Toast.LENGTH_SHORT).show();

        // Perform the necessary steps for location calibration
        // You can use various techniques like collecting Wi-Fi signals, sensors, or other methods for calibration

        // Once the calibration is completed, you can save the calibrated location for future use
        // You may consider storing it in a local database or a remote server

        // Finish the calibration activity
        finish();
    }
}
