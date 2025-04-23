package com.example.myapplication.fragment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.amap.api.maps.AMapUtils;
import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentBusRoutexBinding;
import com.example.myapplication.javabean.LocationDBOpenHelper;
import com.example.myapplication.javabean.MyLocation;

import java.util.Objects;

public class BusRouteFragmentX extends Fragment {
    //此Fragment是传入的destination为站点时的情况
    //没有目的地，只有目的站点，destination作为终点站
    MyLocation userLocation;
    MyLocation terminus;
    MyLocation startStation;
    MapUserFragment mapUserFragment;
    SQLiteDatabase db;
    private FragmentBusRoutexBinding binding;
    public  BusRouteFragmentX(MapUserFragment context,MyLocation userLocation,MyLocation destination){
        this.mapUserFragment=context;
        this.userLocation=userLocation;
        this.terminus=destination;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocationDBOpenHelper locationDBOpenHelper=new LocationDBOpenHelper(getContext());
        db=locationDBOpenHelper.getWritableDatabase();
    }
    @Override
    public void onDestroy(){
        super.onDestroy();

        Toast.makeText(getContext(), "Destroy", Toast.LENGTH_SHORT).show();
    }
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding=FragmentBusRoutexBinding.inflate(getLayoutInflater());
        view=binding.getRoot();
        binding.userlocationRouteshowxTv.setText(userLocation.getLocationName());
        String query;
        if(userLocation.getLocationId()!=null){//如果用户位置不是自动获取的
            if(Objects.equals(userLocation.getIsTake(), "3")) {//且userLocation不是站点
                //由用户选择的定位点获得最近上车点
                query = "SELECT * FROM location WHERE locationId =";
                String queryTake = query + userLocation.getNearestTakeId();
                startStation = getNearestStation(queryTake);
            }
            //否则userLocation为autoLocation，startStation为userLocation
            else {
                startStation=userLocation;
                userLocation=mapUserFragment.getAutoLocation();
                binding.userlocationRouteshowxTv.setText(userLocation.getLocationName());

            }
        }else{//如果用户位置是自动获取的
            //由自动获取的位置搜索附近最近的上车点
            query = "SELECT *\n" +
                    "FROM location\n" +
                    "WHERE isTake = '1'\n" +
                    "ORDER BY ((latitude - " + userLocation.getLatitude() + ") * (latitude - " + userLocation.getLatitude() + ")" +
                    " + (longitude - " + userLocation.getLongitude() + ") * (longitude - " + userLocation.getLongitude() + "))\n" +
                    "LIMIT 1";
            startStation=getNearestStation(query);
        }
        binding.startstationRouteshowxTv.setText(startStation.getLocationName());
        //terminus自动设置无需查询
        binding.terminusRouteshowxTv.setText(terminus.getLocationName());
        int distanceToStart = (int) AMapUtils.calculateLineDistance(
                userLocation.getLatLng(), startStation.getLatLng());
        int distanceToTerminus = (int)AMapUtils.calculateLineDistance(
                terminus.getLatLng(), startStation.getLatLng());
        binding.distanceUserToStartTv.setText(String.format("步行约%s米，预估%s分钟", distanceToStart,1+distanceToStart/80));
        binding.distanceStartToTerminusTv.setText(String.format("行驶距离约%s米，预估xx分钟",distanceToTerminus));
        getNearBus();
        return view;
    }
    public MyLocation getNearestStation(String query){
        MyLocation resLocation=null;
        Cursor cursor = db.rawQuery(query,null);
        cursor.moveToFirst();
        for(int i=0;i<cursor.getCount();i++){
            String locationId=cursor.getString(0);
            String locationNameQuery=cursor.getString(1);
            String longitudeQuery= String.valueOf(cursor.getDouble(2));
            String latitudeQuery= String.valueOf(cursor.getDouble(3));
            String nearestTake=String.valueOf(cursor.getString(5));
            String nearestOff=String.valueOf(cursor.getString(6));
            String isTake= String.valueOf(cursor.getInt(7));
            resLocation=new MyLocation(locationId,locationNameQuery, longitudeQuery,latitudeQuery,nearestTake,nearestOff,isTake);
        }
        cursor.close();
        return resLocation;

    }
    public void getNearBus(){
        binding.searchNearBusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapUserFragment.setBottomSheetBehavior(0);
                mapUserFragment.getChildFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_bs_page_fragment,new NearestBusShowFragment(mapUserFragment,userLocation,startStation,terminus,null))
                        .addToBackStack("nearestBusShowFragment")
                        .commit();
            }
        });
    }
}