package com.example.wifinavindoor;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class WifiConnectActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 2;

    private WifiManager wifiManager;
    private ListView wifiListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_connect);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        wifiListView = findViewById(R.id.wifiListView);
        Button scanButton = findViewById(R.id.scanButton);

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanWifiNetworks();
            }
        });

        wifiListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedWifiSSID = (String) parent.getItemAtPosition(position);
                connectToWifi(selectedWifiSSID);
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            autoConnectToWifi();
        }
    }

    private void autoConnectToWifi() {
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }

        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = "\"" + getDeviceSSID() + "\"";

        int networkId = wifiManager.addNetwork(wifiConfig);
        wifiManager.disconnect();
        wifiManager.enableNetwork(networkId, true);
        wifiManager.reconnect();
    }

    private String getDeviceSSID() {
        return wifiManager.getConnectionInfo().getSSID().replace("\"", "");
    }

    private void scanWifiNetworks() {
        wifiManager.startScan();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        List<ScanResult> scanResults = wifiManager.getScanResults();
        ArrayList<String> wifiNetworks = new ArrayList<>();
        for (ScanResult result : scanResults) {
            wifiNetworks.add(result.SSID);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, wifiNetworks);
        wifiListView.setAdapter(adapter);
    }

    private void connectToWifi(String wifiSSID) {
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = "\"" + wifiSSID + "\"";

        int networkId = wifiManager.addNetwork(wifiConfig);
        boolean success = wifiManager.enableNetwork(networkId, true);

        if (success) {
            Toast.makeText(this, "Connected to Wi-Fi: " + wifiSSID, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to connect to Wi-Fi: " + wifiSSID, Toast.LENGTH_SHORT).show();
        }
    }
}
