package com.example.myapplication.javabean;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.Activity.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.fragment.RouteSearchFragment;

import java.util.List;
import java.util.Objects;

public class RouteSearchAdapter extends ArrayAdapter<MyLocation> {
    private List<MyLocation> searchList;
    RouteSearchFragment fragment;
    MainActivity context;
    ViewHolder viewHolder;
    boolean isStationDestination;
    public RouteSearchAdapter(@NonNull Context context, int resource,@NonNull List<MyLocation> objects,RouteSearchFragment fragment) {
        super(context, resource,objects);
        this.fragment=fragment;
        this.context=(MainActivity) context;
    }
    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.location_itemr,parent,false);
        viewHolder = new ViewHolder(convertView);
        convertView.setTag(viewHolder);
        MyLocation myLocation =getItem(position);//得到当前项的 Fruit 实例
        // 设置要显示的图片和文字
        viewHolder.locationName.setText(myLocation.getLocationName());
        /*viewHolder.locationDistance.setText(myLocation.getLocationDistance());*/
        //设置内容文本组件的点击事件
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Objects.equals(myLocation.getLocationId(), fragment.getHasSelectLocationId())){
                    Toast.makeText(context,"起始点和目的地不能相同",Toast.LENGTH_SHORT).show();
                }else{
                    if(fragment.getLocationEtType()){
                        fragment.setIsUserAutoLocation(myLocation);
                    }
                    fragment.setIsSelectBoolean(fragment.getLocationEtType());
                    fragment.setEditViewFocus(fragment.getLocationEtType());
                    fragment.onDestinationSelect(myLocation,fragment.getLocationEtType());
                }
            }
        });
        return convertView;
    }
    private final class ViewHolder {
        TextView locationSite;
        TextView locationName;
        TextView locationDistance;
        ViewHolder(View view) {
            locationName =view.findViewById(R.id.location_name);
            locationSite =view.findViewById(R.id.location_site);
            locationDistance=view.findViewById(R.id.location_distance);
        }
    }
}