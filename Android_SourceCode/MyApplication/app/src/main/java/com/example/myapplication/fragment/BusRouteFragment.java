package com.example.myapplication.fragment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentBusRouteBinding;
import com.example.myapplication.javabean.LocationDBOpenHelper;
import com.example.myapplication.javabean.MyLocation;

import java.util.Objects;

public class BusRouteFragment extends Fragment {
    //此Fragment是传入的destination非站点时的情况
    //destination作为目的地，而终点站由destination搜索获得
    MyLocation userLocation;
    MyLocation destination;
    MyLocation terminus;
    MyLocation startStation;
    MapUserFragment mapUserFragment;
    SQLiteDatabase db;
    String userToStartDistance;
    private MyLocation[] station = new MyLocation[1];
    private FragmentBusRouteBinding binding;
    private Handler handler;
    public  BusRouteFragment(MapUserFragment context,MyLocation userLocation,MyLocation destination){
        this.mapUserFragment=context;
        this.userLocation=userLocation;
        this.destination=destination;
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

        binding=FragmentBusRouteBinding.inflate(getLayoutInflater());
        view=binding.getRoot();
        binding.userlocationRouteshowTv.setText(userLocation.getLocationName());
        binding.destinationRouteshowTv.setText(destination.getLocationName());
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
                binding.userlocationRouteshowTv.setText(userLocation.getLocationName());

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
        binding.startstationRouteshowTv.setText(startStation.getLocationName());

        //最近下车点由用户选择的目的地查询获得
        String query1 = "SELECT * FROM location WHERE locationId =";
        String queryOff=query1+destination.getNearestOffId();
        terminus=getNearestStation(queryOff);
        binding.terminusRouteshowTv.setText(terminus.getLocationName());
        int distanceToStart = (int) AMapUtils.calculateLineDistance(
                userLocation.getLatLng(), startStation.getLatLng());
        int distanceToTerminus = (int)AMapUtils.calculateLineDistance(
                terminus.getLatLng(), startStation.getLatLng());
        int distanceToDestination = (int)AMapUtils.calculateLineDistance(
                terminus.getLatLng(), destination.getLatLng());
        binding.distanceUserToStartTv.setText(String.format("步行约%s米，预估%s分钟", distanceToStart,1+distanceToStart/80));
        binding.distanceStartToTerminusTv.setText(String.format("行驶距离约%s米，预估xx分钟",distanceToTerminus));
        binding.distanceTerminusToDestinationTv.setText(String.format("步行约%s米，预估%s分钟", distanceToDestination,1+distanceToDestination/80));
        //查看最近车辆的监听
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
                            .replace(R.id.main_bs_page_fragment,new NearestBusShowFragment(mapUserFragment,userLocation,startStation
                                    ,terminus,destination))
                            .addToBackStack("nearestBusShowFragment")
                            .commit();
            }
        });
    }
}