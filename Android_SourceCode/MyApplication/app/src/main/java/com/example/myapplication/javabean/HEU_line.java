//TODO:站点坐标信息及线路信息应该放在数据库中并在登录时获取学校及线路以更新，但是因为目前只有哈工程为服务范围，暂且先存在bean类中吧
package com.example.myapplication.javabean;

import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.example.myapplication.R;

import java.util.ArrayList;

public class HEU_line {
    private final ArrayList<LatLng> latLngs_First = new ArrayList<LatLng>();
    private final ArrayList<MarkerOptions> station_First=new ArrayList<>();

    BitmapDescriptor bus_station_picture = BitmapDescriptorFactory.fromResource(R.drawable.bus_station);
    public HEU_line(){
        latLngs_First.add(new LatLng(45.780913, 126.686798)); //动力楼右
        latLngs_First.add(new LatLng(45.780812, 126.68691)); //动力楼右下
        latLngs_First.add(new LatLng(45.779914, 126.687372)); //船楼
        latLngs_First.add(new LatLng(45.77585, 126.688929)); //图书馆
        latLngs_First.add(new LatLng(45.772966, 126.689189)); //1号楼右下
        latLngs_First.add(new LatLng(45.772898, 126.687344)); //1号楼左下
        latLngs_First.add(new LatLng(45.772296, 126.687274)); //20公寓左上
        latLngs_First.add(new LatLng(45.771581, 126.687339)); //20公寓左下
        latLngs_First.add(new LatLng(45.771248, 126.687323)); //17公寓右下
        latLngs_First.add(new LatLng(45.771271, 126.687129)); //
        latLngs_First.add(new LatLng(45.771278, 126.686706)); //
        latLngs_First.add(new LatLng(45.771102, 126.684807)); //16公寓左下
        latLngs_First.add(new LatLng(45.770915, 126.680274)); //继续教育右下
        latLngs_First.add(new LatLng(45.771701, 126.680193)); //15公寓背后
        latLngs_First.add(new LatLng(45.771877, 126.680215)); //15公寓左上
        latLngs_First.add(new LatLng(45.772797, 126.680054)); //后勤后面
        latLngs_First.add(new LatLng(45.772913, 126.680016)); //
        latLngs_First.add(new LatLng(45.773243, 126.679995)); //
        latLngs_First.add(new LatLng(45.773302, 126.679673)); //
        latLngs_First.add(new LatLng(45.773774, 126.679603)); //理学楼右下
        latLngs_First.add(new LatLng(45.774324, 126.679571)); //理学楼右上
        latLngs_First.add(new LatLng(45.774395, 126.681121)); //
        latLngs_First.add(new LatLng(45.774612, 126.681094)); //21a左下
        latLngs_First.add(new LatLng(45.777587, 126.680864)); //北体左边
        latLngs_First.add(new LatLng(45.777545, 126.679716)); //13公寓左上
        latLngs_First.add(new LatLng(45.779921, 126.679528)); //31号楼右边
        latLngs_First.add(new LatLng(45.777545, 126.679716)); //13公寓左上
        latLngs_First.add(new LatLng(45.777313, 126.679727)); //13公寓左边
        latLngs_First.add(new LatLng(45.777239, 126.677811)); //体育馆正门

        //站点
        station_First.add(new MarkerOptions().position(new LatLng(45.77111,126.684804)).title("16公寓站").icon(bus_station_picture));
        station_First.add(new MarkerOptions().position(new LatLng(45.770959,126.681725)).title("校医院站").icon(bus_station_picture));
        station_First.add(new MarkerOptions().position(new LatLng(45.774045,126.67959)).title("理学楼站").icon(bus_station_picture));
        station_First.add(new MarkerOptions().position(new LatLng(45.77626,126.680974)).title("21b").icon(bus_station_picture));
        station_First.add(new MarkerOptions().position(new LatLng(45.777574,126.680872)).title("北体站").icon(bus_station_picture));
        station_First.add(new MarkerOptions().position(new LatLng(45.778838,126.6796)).title("41号楼").icon(bus_station_picture));
        station_First.add(new MarkerOptions().position(new LatLng(45.779915,126.679537)).title("31号楼").icon(bus_station_picture));
        station_First.add(new MarkerOptions().position(new LatLng(45.777218,126.677857)).title("体育馆").icon(bus_station_picture));
        station_First.add(new MarkerOptions().position(new LatLng(45.77158,126.687339)).title("20公寓站").icon(bus_station_picture));
        station_First.add(new MarkerOptions().position(new LatLng(45.774131,126.689091)).title("材化楼").icon(bus_station_picture));
        station_First.add(new MarkerOptions().position(new LatLng(45.77579,126.688943)).title("图书馆").icon(bus_station_picture));
        station_First.add(new MarkerOptions().position(new LatLng(45.777672,126.688251)).title("61号楼").icon(bus_station_picture));
        station_First.add(new MarkerOptions().position(new LatLng(45.779782,126.687409)).title("船海水声楼").icon(bus_station_picture));
        station_First.add(new MarkerOptions().position(new LatLng(45.78091,126.6868)).title("能动楼").icon(bus_station_picture));
    }
    public ArrayList<LatLng> Get_line_first(){
        return this.latLngs_First;
    }
    public ArrayList<MarkerOptions> Get_station_first(){
        return this.station_First;
    }
}
