package com.example.myapplication.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.example.myapplication.Activity.LoginActivity;
import com.example.myapplication.Activity.MainActivity;
import com.example.myapplication.databinding.FragmentNearestBusShowBinding;
import com.example.myapplication.javabean.BusDriver;
import com.example.myapplication.javabean.MyLocation;
import com.example.myapplication.javabean.ServerSetting;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Driver;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class NearestBusShowFragment extends Fragment {
    MapUserFragment mapUserFragment;
    MyLocation userLocation;
    MyLocation destination;
    MyLocation startStation;
    MyLocation terminus;
    BusDriver nearestDriver;
    private Call call;
    private CountDownLatch latch;
    private BusDriver[] resDriver;
    public NearestBusShowFragment(MapUserFragment context,MyLocation userLocation,MyLocation startStation,MyLocation terminus,MyLocation destination) {
        this.mapUserFragment=context;
        this.userLocation=userLocation;
        this.destination=destination;
        this.startStation=startStation;
        this.terminus=terminus;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    View view;
    private FragmentNearestBusShowBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentNearestBusShowBinding.inflate(getLayoutInflater());
        view=binding.getRoot();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        ViewGroup.LayoutParams layoutParams = binding.busLine.busLineScrollview.getLayoutParams();
        layoutParams.width = screenWidth;
        binding.busLine.busLineScrollview.setLayoutParams(layoutParams);

        return view;
    }
    @Override
    public void onResume(){
        super.onResume();
        nearestDriver=getNearestBusRequest();
        call.cancel();
        busAndLocationShowInMap();
        binding.drivernameNearestbusTv.setText(nearestDriver.getDriverName());
        binding.driveridNearestbusTv.setText(nearestDriver.getDriverId());
        binding.driverphoneNearestbusTv.setText(nearestDriver.getDriverPhone());
        int distanceToNearestBus = (int) AMapUtils.calculateLineDistance(
                userLocation.getLatLng(), nearestDriver.getLatLng());
        binding.distanceNearestTv.setText(String.format(Locale.getDefault(), "车辆距离您%d米", distanceToNearestBus));
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (call != null) {
            Log.e("Destroy","call");
            call.cancel();
        }
        mapUserFragment.stopLinkBusAndStation();
    }
    public void busAndLocationShowInMap(){
        mapUserFragment.busShowInMap(nearestDriver);
        mapUserFragment.stationShowInMap(startStation);
        mapUserFragment.linkBusAndStartStation(nearestDriver.getLatLng(),startStation.getLatLng());
    }
    public BusDriver getNearestBusRequest() {
        resDriver = new BusDriver[1];
        latch = new CountDownLatch(1);

        String url = ServerSetting.ServerPublicIpAndPort + "get_nearest_bus/";
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("startStation", startStation.getLocationId())
                .build();
        Log.e("startStation",startStation.getLocationId());
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                latch.countDown();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull okhttp3.Response response) throws IOException {
                String driverId = null;
                String driverName = null;
                String driverAccount = null;
                String driverPhone = null;
                String latitude = null;
                String longitude = null;
                try {
                    JSONObject object;
                    if (response.body() != null) {
                        object = new JSONObject(response.body().string());
                        driverId = object.getString("driverId");
                        driverName = object.getString("driverName");
                        driverAccount = object.getString("driverAccount");
                        driverPhone = object.getString("driverPhone");
                        latitude = object.getString("latitude");
                        longitude = object.getString("longitude");
                    }

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                resDriver[0] = new BusDriver(driverId, driverName, driverAccount, driverPhone, latitude, longitude);
                latch.countDown();
            }
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return resDriver[0];
    }
}