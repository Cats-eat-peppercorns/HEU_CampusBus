package com.example.myapplication.fragment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentLocationListBinding;
import com.example.myapplication.javabean.MyLocation;
import com.example.myapplication.javabean.LocationSearchAdapter;
import com.example.myapplication.javabean.LocationDBOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class LocationSearchListFragment extends Fragment{
    ListView locationList;
    SQLiteDatabase db;
    private FragmentLocationListBinding binding;
    LocationSearchFragment locationSearchFragment;
    MapUserFragment mapUserFragment;
    public LocationSearchListFragment(LocationSearchFragment locationSearchFragment,MapUserFragment context){
        this.locationSearchFragment=locationSearchFragment;
        this.mapUserFragment=context;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocationDBOpenHelper locationDBOpenHelper=new LocationDBOpenHelper(getContext());
        db=locationDBOpenHelper.getWritableDatabase();
    }

    View view;
    LocationSearchAdapter adapter;
    List<MyLocation> searchList=new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_location_list, container, false);
        binding=FragmentLocationListBinding.inflate(getLayoutInflater());
        locationList=view.findViewById(R.id.location_list);
        /*historySearchListShow();*/
        // 获取FragmentManager

        adapter=new LocationSearchAdapter(getContext(),mapUserFragment,R.layout.location_item,searchList);
        locationList.setAdapter(adapter);
        return view;
    }
    public void onDestinationChange(String destination){
        locationShow(destination);

    }
    public void locationShow(String destination){
        String query = "SELECT * FROM location WHERE locationName LIKE '%" + destination + "%'";
        Cursor cursor = db.rawQuery(query,null);
        searchList.clear();
        cursor.moveToFirst();
        for(int i=0;i<cursor.getCount();i++){
            String locationId=cursor.getString(0);
            String locationNameQuery=cursor.getString(1);
            String longitudeQuery= String.valueOf(cursor.getDouble(2));
            String latitudeQuery= String.valueOf(cursor.getDouble(3));
            String nearestTake=String.valueOf(cursor.getString(5));
            String nearestOff=String.valueOf(cursor.getString(6));
            String isTake= String.valueOf(cursor.getInt(7));
            MyLocation myLocation =new MyLocation(locationId,locationNameQuery, longitudeQuery,latitudeQuery,nearestTake,nearestOff,isTake);
            searchList.add(myLocation);
            cursor.moveToNext();
        }
        cursor.close();
        adapter=new LocationSearchAdapter(getContext(),mapUserFragment,R.layout.location_item,searchList);
        locationList.setAdapter(adapter);
    }
//    public void historySearchListShow(){
//        MyLocation myLocation =new MyLocation("0","历史搜索地点","地点地理位置","距离","经度","纬度");
//        searchList.add(myLocation);
//    }
    public void setLocationListEmpty(){
        adapter=new LocationSearchAdapter(getContext(),mapUserFragment,R.layout.location_itemr, new ArrayList<>());
    }

}