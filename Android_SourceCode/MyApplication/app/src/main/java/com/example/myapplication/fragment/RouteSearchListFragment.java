package com.example.myapplication.fragment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.myapplication.Activity.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentLocationListBinding;
import com.example.myapplication.javabean.LocationDBOpenHelper;
import com.example.myapplication.javabean.MyLocation;
import com.example.myapplication.javabean.RouteSearchAdapter;

import java.util.ArrayList;
import java.util.List;

public class RouteSearchListFragment extends Fragment {
    ListView locationList;
    SQLiteDatabase db;
    RouteSearchFragment routeSearchFragment;
    private FragmentLocationListBinding binding;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocationDBOpenHelper locationDBOpenHelper=new LocationDBOpenHelper(getContext());
        db=locationDBOpenHelper.getWritableDatabase();
        routeSearchFragment=(RouteSearchFragment) getParentFragment();
    }

    View view;
    RouteSearchAdapter adapter;
    List<MyLocation> searchList=new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_location_list, container, false);
        binding=FragmentLocationListBinding.inflate(getLayoutInflater());
        locationList=view.findViewById(R.id.location_list);
        adapter=new RouteSearchAdapter((MainActivity) getContext(),R.layout.location_itemr,searchList,routeSearchFragment);
        locationList.setAdapter(adapter);
        return view;
    }
    public void onLocationChange(String destination){
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
        adapter=new RouteSearchAdapter(getContext(),R.layout.location_itemr,searchList,routeSearchFragment);
        locationList.setAdapter(adapter);
    }
    public void setLocationListEmpty(){
        adapter=new RouteSearchAdapter(getContext(),R.layout.location_itemr, new ArrayList<>(),routeSearchFragment);
    }
}