package com.example.wifinavindoor;

public class PinLocation {
    private String name;
    private double latitude;
    private double longitude;

    public PinLocation() {
        // Default constructor required for Firebase
    }

    public PinLocation(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
