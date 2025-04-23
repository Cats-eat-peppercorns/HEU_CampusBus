package com.example.myapplication.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;

import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.example.myapplication.Activity.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.databinding.ActivityMainBinding;
import com.example.myapplication.databinding.FragmentMapUserBinding;
import com.example.myapplication.databinding.NavHeaderMainBinding;
import com.example.myapplication.javabean.BusDriver;
import com.example.myapplication.javabean.MyLocation;
import com.example.myapplication.javabean.PictureSaveSetting;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.Objects;

public class MapUserFragment extends Fragment {
    MainActivity mainActivity;
    public MapUserFragment(MainActivity context){
        this.mainActivity=context;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    View view;
    AMap aMap;
    Marker marker;
    Marker nearestBusMaker;
    Marker startStationMaker;
    MapView mapView;
    MyLocation autoLocation;
    MyLocation userLocation;
    Polyline polyline;
    BottomSheetBehavior bottomSheetBehavior;
    private FragmentMapUserBinding binding;
    private NavHeaderMainBinding bindingNavHeader;
    private ActivityMainBinding bindingMainActivity;
    public void handleBackPressed(){
        getChildFragmentManager().popBackStack();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentMapUserBinding.inflate(getLayoutInflater());
        view=binding.getRoot();
        userLocation=new MyLocation(null,"我的位置",
                "33.152045","106.632735", null,null,null);
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet);
        /*bottomSheet=binding.bottomSheet;*/
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.main_bs_page_fragment, new MainFragment(this),"MainFragment")
                .commit();
        mapView=binding.map;
        aMap=mapView.getMap();
        LatLng centerBJPoint= new LatLng(45.776625,126.682563);
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centerBJPoint,16));
        mapView.onCreate(savedInstanceState);
        navUISetting();
        navUIListener();
        Location();
        onMyLocationChangeLister();
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        navUIListener();//主要UI的点击事件监听
    }
    private void Location() {
        MyLocationStyle myLocationStyle=new MyLocationStyle();
        //连续定位、蓝点不会移动到地图中心点，并且蓝点会跟随设备移动。（1秒1次定位）
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
        //设置定位频次方法，单位：毫秒，默认值：1000毫秒，如果传小于1000的任何值将按照1000计算。该方法只会作用在会执行连续定位的工作模式上。
        myLocationStyle.interval(5000);
        //设置定位蓝点的Style
        aMap.setMyLocationStyle(myLocationStyle);
        //设置默认定位按钮是否显示，非必需设置。
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        // 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        aMap.setMyLocationEnabled(true);
        /*        Log.e("1",PictureSaveSetting.avatarPath);
        float scaleWidth = 0.17F;
        float scaleHeight = 0.17F;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth,scaleHeight);
        bitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,false);
        bitmap=getRoundedBitmap(bitmap);
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
        myLocationStyle.myLocationIcon(bitmapDescriptor);//设置定位蓝点的icon图标方法，需要用到BitmapDescriptor类对象作为参数。*/
    }
    public void navUISetting(){
        binding.mapAvatarIv.setImageBitmap(openImage(PictureSaveSetting.avatarPath + "avatar.png"));
    }
    public Bitmap openImage (String path){
        Bitmap bitmap = null;
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(path));
            bitmap = BitmapFactory.decodeStream(bis);
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
    public void navUIListener(){
        binding.mapAvatarIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.drawerLayoutSet();
            }
        });
    }
    public void onMyLocationChangeLister(){
        aMap.setOnMyLocationChangeListener(new AMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(android.location.Location location) {
                autoLocation=new MyLocation(null,"我的位置",
                        String.valueOf(location.getLatitude()),String.valueOf(location.getLongitude()),
                        null,null,null);
            }
        });
    }
    public MyLocation getAutoLocation(){
        return autoLocation;
    }
    public void setBottomSheetBehavior(int behaviorStatus){
        switch  (behaviorStatus){
            case 0:
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
                break;
            case 1:
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED); // 折叠 BottomSheet
            case 2:
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED); // 将 BottomSheet 完全展开
        }
    }
    public void locationShowInMap(MyLocation myLocation){
        double longitude = Double.parseDouble(myLocation.getLongitude());//经度*/
        double latitude = Double.parseDouble(myLocation.getLatitude());//纬度
        LatLng latLng = new LatLng(latitude,longitude);
        marker=aMap.addMarker(new MarkerOptions().position(latLng));
        //DisplayMetrics类提供了关于显示屏幕的一些信息
        DisplayMetrics displayMetrics = new DisplayMetrics();
        //getWindowManager() 获取窗口管理器对象，它是用来管理窗口的类。
        //getDefaultDisplay() 获取默认的显示对象，表示当前设备的屏幕。
        //getMetrics() 获取屏幕显示度量，返回一个DisplayMetrics对象，其中包含了有关显示屏幕的信息，如宽度、高度、密度等。
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        //offsetY为屏幕显示高度的四分之一
        int offsetY = (int) (displayMetrics.heightPixels* 0.25);
        //screenPoint为地图中心点由地理坐标转化为屏幕度量后的点
        Point screenPoint = aMap.getProjection().toScreenLocation(latLng);
        //将地图向上移动四分之一个屏幕距离
        screenPoint.y += offsetY;
        //根据屏幕度量点来设置地图显示的中心点
        LatLng newCenterLatLng = aMap.getProjection().fromScreenLocation(screenPoint);
        aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newCenterLatLng,16));
        aMap.setMyLocationEnabled(false);
        setBottomSheetBehavior(0);
        getChildFragmentManager().beginTransaction()
                .replace(R.id.main_bs_page_fragment,new LocationShowFragment(this,myLocation))
                .addToBackStack("locationShowFragment")
                .commit();

    }
    public void stopLocationShowInMap(){
        marker.remove();
        aMap.setMyLocationEnabled(true);
    }
    public void stopLinkBusAndStation(){
        nearestBusMaker.remove();
        startStationMaker.remove();
        polyline.remove();
        aMap.setMyLocationEnabled(true);
    }
    public void busShowInMap(BusDriver busDriver){
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.draggable(true);//设置Marker可拖动
        int width = 60; // 设置宽度为100像素
        int height = 60; // 设置高度为100像素
        Bitmap originalBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.nearestbus_icon);
        Bitmap busTestBitmap = Bitmap.createScaledBitmap(originalBitmap, width, height, false);
        markerOption.icon(BitmapDescriptorFactory.fromBitmap(busTestBitmap));
        markerOption.setFlat(true);//设置marker平贴地图效果
        nearestBusMaker=aMap.addMarker(markerOption.position(busDriver.getLatLng()));
        aMap.setMyLocationEnabled(false);
    }
    public void stationShowInMap(MyLocation station){
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.draggable(true);//设置Marker可拖动
        int width = 60; // 设置宽度为100像素
        int height = 60; // 设置高度为100像素
        Bitmap originalBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.station_icon);
        Bitmap busTestBitmap = Bitmap.createScaledBitmap(originalBitmap, width, height, false);
        markerOption.icon(BitmapDescriptorFactory.fromBitmap(busTestBitmap));
        markerOption.setFlat(true);//设置marker平贴地图效果
        startStationMaker=aMap.addMarker(markerOption.position(station.getLatLng()));
    }
    public void linkBusAndStartStation(LatLng latLng1,LatLng latLng2){
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.add(latLng1, latLng2) // 添加起点和终点
                .width(10) // 设置线宽
                .color(Color.RED) // 设置线颜色
                .setDottedLine(true); // 设置为虚线
        polyline = aMap.addPolyline(polylineOptions);
    }
    public MyLocation getIsUserAutoLocation(){
        return userLocation;
    }
    public void setIsUserAutoLocation(MyLocation userLocation){
        this.userLocation=userLocation;
    }
}