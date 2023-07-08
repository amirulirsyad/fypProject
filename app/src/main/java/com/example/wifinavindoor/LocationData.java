package com.example.wifinavindoor;

public class LocationData {
    private String name;
    //private String building;
    private double latitude;
    private double longitude;

    public LocationData() {
        // Default constructor required for Firebase
    }

    public LocationData(String name, double latitude, double longitude) {
        this.name = name;
        //this.building = building;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public String getBuilding() {
//        return building;
//    }
//
//    public void setBuilding(String building) {
//        this.name = building;
//    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
