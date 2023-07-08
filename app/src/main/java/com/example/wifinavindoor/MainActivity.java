package com.example.wifinavindoor;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private static final int WIFI_PERMISSION_REQUEST_CODE = 1;

    private WifiManager wifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    public void connectToWifi(View view) {
        // Code for connect wifi goes here
        Intent intent = new Intent(MainActivity.this, WifiConnectActivity.class);
        startActivity(intent);
        Toast.makeText(this, "Connect Wifi", Toast.LENGTH_SHORT).show();
    }



    public void calibrateLocation(View view) {
        // Code for location calibration goes here
        Intent intent = new Intent(MainActivity.this, CalibrationActivity.class);
        startActivity(intent);
        Toast.makeText(this, "Calibrating Location", Toast.LENGTH_SHORT).show();
    }

    public void saveLocation(View view) {
        // Code for saving the calibrated location goes here
        Intent intent = new Intent(MainActivity.this, SaveLocationActivity.class);
        startActivity(intent);

        Toast.makeText(this, "Location Saved", Toast.LENGTH_SHORT).show();
    }

    public void navigateToDestination(View view) {
        // Code for navigation to destination goes here
        Intent intent = new Intent(MainActivity.this, NavigateToDestinationActivity.class);
        startActivity(intent);
        Toast.makeText(this, "Navigating to Destination", Toast.LENGTH_SHORT).show();
    }
}

