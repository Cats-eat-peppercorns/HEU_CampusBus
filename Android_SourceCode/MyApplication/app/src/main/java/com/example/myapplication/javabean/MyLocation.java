package com.example.myapplication.javabean;

import com.amap.api.maps.model.LatLng;

public class MyLocation {
    private final String locationId;
    private final String locationName;
    private final String longitude;
    private final String latitude;
    private final String nearestTakeId;
    private final String nearestOffId;
    private final String isTake;
    private final LatLng latLng;
    public String getLocationId(){
        return locationId;
    }
    public String getLocationName(){
        return locationName;
    }
    public String getLongitude(){
        return longitude;
    }
    public String getLatitude(){
        return latitude;
    }
    public String getNearestTakeId(){
        return  nearestTakeId;
    }
    public String getNearestOffId(){
        return nearestOffId;
    }
    public String getIsTake(){
        return isTake;
    }
    public LatLng getLatLng(){
        return latLng;
    }
    public MyLocation(String locationId,String locationName, String latitude,String longitude,String nearestTakeId,String nearestOffId,String isTake){
        this.locationId=locationId;
        this.locationName=locationName;
        this.latitude=latitude;
        this.longitude=longitude;
        this.nearestTakeId=nearestTakeId;
        this.nearestOffId=nearestOffId;
        this.isTake=isTake;
        this.latLng=new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude));
    }
}
