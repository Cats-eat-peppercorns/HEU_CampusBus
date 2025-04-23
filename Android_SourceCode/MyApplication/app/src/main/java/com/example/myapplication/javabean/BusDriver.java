package com.example.myapplication.javabean;

import com.amap.api.maps.model.LatLng;

public class BusDriver {
    private String driverId;
    private String driverName;
    private String driverAccount;
    private String driverPhone;
    private  String longitude;
    private  String latitude;
    private  LatLng latLng;
    public String getDriverId(){
        return driverId;
    }
    public String getDriverName(){
        return driverName;
    }
    public String getDriverAccount(){
        return driverAccount;
    }
    public String getDriverPhone(){
        return driverPhone;
    }
    public String getLongitude(){
        return longitude;
    }
    public String getLatitude(){
        return latitude;
    }
    public LatLng getLatLng(){
        return latLng;
    }
    public BusDriver(String driverId,String driverName,String driverAccount,String driverPhone,String latitude,String longitude){
        this.driverId=driverId;
        this.driverName=driverName;
        this.driverAccount=driverAccount;
        this.driverPhone=driverPhone;
        this.longitude=longitude;
        this.latitude=latitude;
        this.latLng=new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude));
    }
}
