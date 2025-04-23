package com.example.myapplication.javabean;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.R;
import com.example.myapplication.fragment.MapUserFragment;
import com.example.myapplication.fragment.RouteSearchFragment;

import java.util.List;

public class LocationSearchAdapter extends ArrayAdapter<MyLocation> {
    private List<MyLocation> searchList;
    MapUserFragment mapUserFragment;

    public LocationSearchAdapter(@NonNull Context context, MapUserFragment mapUserFragment,int resource, @NonNull List<MyLocation> objects) {
        super(context, resource, objects);
        this.searchList = objects;
        this.mapUserFragment =mapUserFragment;
    }
    ViewHolder viewHolder;
    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.location_item,parent,false);
        viewHolder = new ViewHolder(convertView);
        convertView.setTag(viewHolder);
        MyLocation myLocation =getItem(position);//得到当前项的 Fruit 实例
        // 设置要显示的图片和文字
        viewHolder.locationName.setText(myLocation.getLocationName());
        /*viewHolder.locationDistance.setText(myLocation.getLocationDistance());*/
        //设置内容文本组件的点击事件
        viewHolder.location_icon_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mapUserFragment.locationShowInMap(myLocation);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        viewHolder.route_icon_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapUserFragment.getChildFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_bs_page_fragment,new RouteSearchFragment(mapUserFragment,myLocation))
                        .addToBackStack("routeSearchFragment")
                        .commit();
            }
        });
        return convertView;
    }
    private final class ViewHolder {
        TextView locationSite;
        TextView locationName;
        TextView locationDistance;
        ImageView location_icon_iv;
        ImageView route_icon_iv;
        ViewHolder(View view) {
            locationName =view.findViewById(R.id.location_name);
            locationSite =view.findViewById(R.id.location_site);
            locationDistance=view.findViewById(R.id.location_distance);
            location_icon_iv=view.findViewById(R.id.location_icon_iv);
            route_icon_iv=view.findViewById(R.id.route_icon_iv);
        }
    }

}
