package com.example.myapplication.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R;
import com.example.myapplication.javabean.HEU_line;
import com.example.myapplication.javabean.ServerSetting;
import com.example.myapplication.javabean.django_url;
import com.example.myapplication.Activity.DriverRegisterActivity;
import com.example.myapplication.Activity.Driver_main_interface;
import com.example.myapplication.Activity.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Map_Driver_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Map_Driver_Fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    //静态不可修改常量
    private final Handler handler = new Handler(); //消息处理器

    //private final BitmapDescriptor driver_picture = BitmapDescriptorFactory.fromResource(R.drawable.bus_station);
    private ArrayList<MarkerOptions> workmates_List = new ArrayList<>(); //同事坐标列表
    protected static CameraPosition cameraPosition;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View rootView;
    private Button start_button;
    private int ONorOFF=-1;
    private TextureMapView textureMapView;
    //com.amap.api.maps.TextureMapView mMapView = null;
    private AMap aMap;
    private String driver_account;
    private String all_latitude,all_longitude;
    public static final LatLng HEU = new LatLng(126.681946,45.775789);
    private Button show_others_button;
    private int SHOWorNOT_SHOW=-1;

    private boolean show_mapText,show_mapBuild;
    public Map_Driver_Fragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Map_Driver_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Map_Driver_Fragment newInstance(String param1, String param2) {
        Map_Driver_Fragment fragment = new Map_Driver_Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        AMapLocationClient.updatePrivacyShow(getActivity(), true, true);
        AMapLocationClient.updatePrivacyAgree(getActivity(), true);
        //ONorOFF=-1;
        // 如果 rootView 为空，说明尚未创建过视图
        if(rootView==null)
        {
            rootView=inflater.inflate(R.layout.fragment_map__driver_, container, false);
        }
        //获取地图控件引用
        start_button=rootView.findViewById(R.id.start_button);
        show_others_button=rootView.findViewById(R.id.show_others_button);
        textureMapView = (TextureMapView) rootView.findViewById(R.id.map);
        //mMapView = rootView.findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        SharedPreferences sharedCheckbox= requireActivity().getSharedPreferences("user_checkBox", Context.MODE_PRIVATE);
        show_mapText=sharedCheckbox.getBoolean("Show_map_text", false);
        show_mapBuild= sharedCheckbox.getBoolean("Show_map_build", false);

        if(textureMapView != null) {
            textureMapView.onCreate(savedInstanceState);
            if(aMap==null) {
                aMap = textureMapView.getMap();
            }
            if (getCameraPosition() == null) {
                aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(HEU, 10, 0, 0)));
            }else {
                aMap.moveCamera(CameraUpdateFactory.newCameraPosition(getCameraPosition()));
            }
        }
        SharedPreferences sharedPreferences= requireContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        driver_account=sharedPreferences.getString("driver_account","");
        Get_bus_location();
        print_station(); //绘制站点
        print_line(); //绘制线路
        return rootView;
    }

    private void print_line() {
        HEU_line line=new HEU_line();
        List<LatLng> line_first=line.Get_line_first();
        Polyline polyline = aMap.addPolyline(new PolylineOptions()
                .addAll(line_first)
                .width(10)
                .color(Color.argb(255, 36, 164, 255)));
    }

    private void print_station() {
        HEU_line station=new HEU_line();
        ArrayList<MarkerOptions> stations=station.Get_station_first();
        aMap.addMarkers(stations,false);
    }


    /**
     * 方法必须重写
     */
    @Override
    public void onResume() {
        super.onResume();
        textureMapView.onResume();
        int backgroundResource_start_button_gray = R.drawable.background_dirver_4_gray;
        int backgroundResource_start_button = R.drawable.background_driver_4;
        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ONorOFF=-ONorOFF;
                On_the_job_information(); //发送在岗信息
                startLocationUpdates(); //发送坐标信息
                if(ONorOFF==1)
                    start_button.setBackground(ContextCompat.getDrawable(requireContext(), backgroundResource_start_button_gray));
                else
                    start_button.setBackground(ContextCompat.getDrawable(requireContext(), backgroundResource_start_button));
                //Toast.makeText(getContext(), "点了", Toast.LENGTH_SHORT).show();
            }
        });
        int backgroundResource_show_others_gray = R.drawable.background_dirver_5_gray;
        int backgroundResource_show_others = R.drawable.background_driver_5;
        show_others_button.setOnClickListener(new View.OnClickListener() {  //通过按钮来定时获取其他司机位置
            @Override
            public void onClick(View view) {
                SHOWorNOT_SHOW=-SHOWorNOT_SHOW;
                Show_workmates();
                if(SHOWorNOT_SHOW==1)
                    show_others_button.setBackground(ContextCompat.getDrawable(requireContext(), backgroundResource_show_others_gray));
                else
                    show_others_button.setBackground(ContextCompat.getDrawable(requireContext(), backgroundResource_show_others));

            }
        });
    }

    private void Show_workmates() {
        if(SHOWorNOT_SHOW==1)
        {
            handler.postDelayed(Show_workmatesRunnable,5000); //开启同事位置获取
        }else if(SHOWorNOT_SHOW==-1)
        {
            handler.removeCallbacks(Show_workmatesRunnable);
            clean_last_workmates();
        }
    }

    public void On_the_job_information() {
        String url = ServerSetting.ServerPublicIpAndPort+"driver_on_the_job/";
        OkHttpClient okHttpClient = new OkHttpClient();  //构建一个网络类型的实例
        RequestBody requestBody = new FormBody.Builder()
                .add("driver_account", driver_account)
                .add("ONorOFF", String.valueOf(ONorOFF))
                .build();
        Request request = new Request.Builder() //构建一个具体的网络请求对象，具体的请求url，请求头，请求体等
                .url(url)
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request); //将具体的网络请求与执行请求的实体进行绑定
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(requireActivity(), "发送失败：请检查网络", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull okhttp3.Response response) throws IOException {
                if (!response.isSuccessful()) {
                    // 请求不成功，处理错误响应
                    final String errorMessage = response.body().string();
                    // 切换到主线程更新 UI
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(requireActivity(), "发送失败：" + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }


//    private void On_the_job_information() {  //处理在岗信息
//        String url= django_url.url+"driver_on_the_job/";
//        RequestQueue requestQueue = Volley.newRequestQueue(requireActivity());
//        StringRequest request = new StringRequest(1, url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                //Toast.makeText(getActivity(), "上岗成功", Toast.LENGTH_SHORT).show();
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> map = new HashMap<>();
//                map.put("driver_account", driver_account);
//                map.put("ONorOFF", String.valueOf(ONorOFF));
//                return map;
//            }
//        };
//        requestQueue.add(request);
//    }

    private void startLocationUpdates() {
        if(ONorOFF==1)
        {
            handler.postDelayed(locationRunnable,3000);
        }else if(ONorOFF==-1)
        {
            handler.removeCallbacks(locationRunnable);
        }
    }

    private final Runnable Show_workmatesRunnable = new Runnable() {
        @Override
        public void run() {
            get_new_workmates();  //获取同事位置
        }
    };
    private void print_new_workmates() {
        aMap.addMarkers(workmates_List,false);
    }

    private void clean_last_workmates() {
        workmates_List.clear(); //清除原来列表
        for (Marker marker : aMap.getMapScreenMarkers()) {
            if (marker!=null && marker.getTitle()!=null && marker.getTitle().equals("workers")) {
                marker.remove();
            }
        }
    }

    public void get_new_workmates() {
        String url = ServerSetting.ServerPublicIpAndPort+"driver_get_workmates/";
        OkHttpClient okHttpClient = new OkHttpClient();  //构建一个网络类型的实例
        RequestBody requestBody = new FormBody.Builder()
                .add("driver_account", driver_account)
                .build();
        Request request = new Request.Builder() //构建一个具体的网络请求对象，具体的请求url，请求头，请求体等
                .url(url)
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request); //将具体的网络请求与执行请求的实体进行绑定
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(requireActivity(), "获取失败：请检查网络", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull okhttp3.Response response) throws IOException {
                if (!response.isSuccessful()&&response.body() != null) {
                    // 请求不成功，处理错误响应
                    final String errorMessage = response.body().string();
                    // 切换到主线程更新 UI
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(requireActivity(), "获取失败：" + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    String workmates_name,workmates_account,workmates_latitude,workmates_longitude;
                    BitmapDescriptor driver_picture = BitmapDescriptorFactory.fromResource(R.drawable.driver_mates);
                    try {
                        if (response.body() != null){
                            clean_last_workmates(); //清除之前的同事位置
                            JSONArray jsonArray =new JSONArray(response.body().string());   //将string类型的response转换为JSONObject类型的object
                            for(int i=0;i<jsonArray.length();i++)
                            {
                                JSONObject object = jsonArray.getJSONObject(i);
                                workmates_name=object.getString("name");
                                workmates_account=object.getString("account");
                                workmates_latitude=object.getString("latitude");
                                workmates_longitude=object.getString("longitude");
                                double latitude=Double.parseDouble(workmates_latitude);
                                double longitude=Double.parseDouble(workmates_longitude);
                                //Log.e("TAG?!?!?!?!", "run: "+ latitude+longitude);
                                workmates_List.add(new MarkerOptions().position(new LatLng(latitude,longitude)).title("workers").icon(driver_picture).snippet(workmates_name));
                            }
                            if(SHOWorNOT_SHOW==1) //若还处于展示阶段
                            {
                                requireActivity().runOnUiThread(new Runnable() { //在主线程处理绘制任务
                                    @Override
                                    public void run() {
                                        print_new_workmates(); //绘制新的同事位置
                                    }
                                });
                            }
                            handler.postDelayed(Show_workmatesRunnable, 5000);
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    //Toast.makeText(getActivity(), "查找成功", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


//    private void get_new_workmates() {
//        String url= django_url.url+"driver_get_workmates/";
//        RequestQueue requestQueue = Volley.newRequestQueue(requireActivity());
//        StringRequest request = new StringRequest(1, url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {  //处理获取到的同事信息
//                String workmates_name,workmates_account,workmates_latitude,workmates_longitude;
//                BitmapDescriptor driver_picture = BitmapDescriptorFactory.fromResource(R.drawable.driver_mates);
//                try {
//                    clean_last_workmates(); //清除之前的同事位置
//                    JSONArray jsonArray =new JSONArray(response);   //将string类型的response转换为JSONObject类型的object
//                    for(int i=0;i<jsonArray.length();i++)
//                    {
//                        JSONObject object = jsonArray.getJSONObject(i);
//                        workmates_name=object.getString("name");
//                        workmates_account=object.getString("account");
//                        workmates_latitude=object.getString("latitude");
//                        workmates_longitude=object.getString("longitude");
//                        double latitude=Double.parseDouble(workmates_latitude);
//                        double longitude=Double.parseDouble(workmates_longitude);
//                        //Log.e("TAG?!?!?!?!", "run: "+ latitude+longitude);
//                        workmates_List.add(new MarkerOptions().position(new LatLng(latitude,longitude)).title("workers").icon(driver_picture).snippet(workmates_name));
//                    }
//                    if(SHOWorNOT_SHOW==1) //若还处于展示阶段
//                        print_new_workmates(); //绘制新的同事位置
//                    handler.postDelayed(Show_workmatesRunnable, 5000);
//                    //Log.e("TAG?!?!?!?!", "run: "+ workmates_List.size());
//                } catch (JSONException e) {
//                    throw new RuntimeException(e);
//                }
//                //Toast.makeText(getActivity(), "查找成功", Toast.LENGTH_SHORT).show();
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                String errorMessage = error.getMessage();  //处理返回的错误信息
//                if (errorMessage == null) {
//                    NetworkResponse networkResponse = error.networkResponse;
//                    if (networkResponse != null) {
//                        // 获取网络响应的字节数组
//                        byte[] responseData = networkResponse.data;
//                        if (responseData != null) {
//                            String responseString = new String(responseData);
//                            //Toast.makeText(getContext(), "定位失败"+responseString, Toast.LENGTH_SHORT).show();
//                            // 处理响应字符串
//                        }
//                    }
//                }
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> map = new HashMap<>();
//                map.put("driver_account", driver_account);
//                return map;
//            }
//        };
//        requestQueue.add(request);
//    }




    // 位置更新的任务
    private final Runnable locationRunnable = new Runnable() {
        @Override
        public void run() {
            // 在这里执行你需要定时执行的代码，比如发送位置信息给服务器
            driver_locationSend(all_latitude,all_longitude);
            //Toast.makeText(getContext(), "点了", Toast.LENGTH_SHORT).show();
            // 再次将任务推送到消息队列，实现循环执行
            handler.postDelayed(this, 3000);
        }
    };

    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        textureMapView.onPause();
    }
    /**
     * 方法必须重写
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        textureMapView.onSaveInstanceState(outState);
    }
    @Override
    public void onDestroyView() { //这个方法一旦调用就会在该资源释放时崩溃,调查发现是textureMapView.onDestroy();
        //的调用和安卓Heap Pointer Tagging特性冲突，第三方库可能不支持这个特性，所以我在AndroidManifest中屏蔽
        try {
            if (textureMapView != null) {
                textureMapView.onDestroy();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroyView();
    }
    /**
     * 方法必须重写
     */
    @Override
    public void onDestroy() {
        handler.removeCallbacks(locationRunnable);//释放时要强制停止位置发送
        handler.removeCallbacks(Show_workmatesRunnable);//停止位置获取
        ONorOFF=-1;
        On_the_job_information(); //将在岗信息换为下岗
        setCameraPosition(aMap.getCameraPosition());
        super.onDestroy();
//        try { //在 Fragment 被销毁时getActivity().getSupportFragmentManager().isDestroyed()会导致空指针异常
//            if (getActivity() != null) {
//                if (!getActivity().getSupportFragmentManager().isDestroyed()) {
//                    Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
//                    childFragmentManager.setAccessible(true);
//                    childFragmentManager.set(this, null);
//                }
//            }
//        } catch (NoSuchFieldException | IllegalAccessException e) {
//            e.printStackTrace();
//        }
    }

    CameraPosition getCameraPosition() {
        return cameraPosition;
    }

    void setCameraPosition(CameraPosition cameraPosition) {
        Map_Driver_Fragment.cameraPosition = cameraPosition;
    }
    private void Get_bus_location() {   //获取定位
        if (aMap == null) {
            aMap = textureMapView.getMap();
        }
        aMap.showBuildings(show_mapBuild); //显示楼房
        aMap.showMapText(show_mapText); //显示地图文字

        MyLocationStyle myLocationStyle;  //设置定位模式
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.interval(1000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE); //设置为方向跟随手机方向
        myLocationStyle.showMyLocation(true); //设置为显示蓝点，为false时不显示蓝点
        //将设置应用到地图中
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        //aMap.getUiSettings().setMyLocationButtonEnabled(true);设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        aMap.setOnMyLocationChangeListener(onMyLocationChangeListener);//启动定位监听器
    }



    public AMap.OnMyLocationChangeListener onMyLocationChangeListener = new AMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            if (location != null) {   //检查是否为空，确保获得了有效信息
                double latitude = location.getLatitude();//纬度
                double longitude = location.getLongitude();//经度
                all_latitude=String.valueOf(latitude);
                all_longitude=String.valueOf(longitude);
            }
        }
    };


    public void driver_locationSend(String latitude, String longitude) {
        String url = ServerSetting.ServerPublicIpAndPort+"driver_locationSend/";
        OkHttpClient okHttpClient = new OkHttpClient();  //构建一个网络类型的实例
        RequestBody requestBody = new FormBody.Builder()
                .add("driver_account", driver_account)
                .add("latitude", latitude)
                .add("longitude", longitude)
                .build();
        Request request = new Request.Builder() //构建一个具体的网络请求对象，具体的请求url，请求头，请求体等
                .url(url)
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request); //将具体的网络请求与执行请求的实体进行绑定
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(requireActivity(), "发送失败：请检查网络", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull okhttp3.Response response) throws IOException {
                if (!response.isSuccessful()) {
                    // 请求不成功，处理错误响应
                    final String errorMessage = response.body().string();
                    // 切换到主线程更新 UI
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(requireActivity(), "发送失败：" + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

//    private void driver_locationSend1(String latitude, String longitude) {
//        String url= django_url.url+"driver_locationSend/";
//        RequestQueue requestQueue = Volley.newRequestQueue(requireActivity());
//        StringRequest request = new StringRequest(1, url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                //Toast.makeText(requireActivity(), latitude + " " + longitude, Toast.LENGTH_SHORT).show();
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                String errorMessage = error.getMessage();  //处理返回的错误信息
//                if (errorMessage == null) {
//                    NetworkResponse networkResponse = error.networkResponse;
//                    if (networkResponse != null) {
//                        // 获取网络响应的字节数组
//                        byte[] responseData = networkResponse.data;
//                        if (responseData != null) {
//                            String responseString = new String(responseData);
//                            //Toast.makeText(getContext(), "定位失败"+responseString, Toast.LENGTH_SHORT).show();
//                            // 处理响应字符串
//                        }
//                    }
//                }
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> map = new HashMap<>();
//                map.put("driver_account", driver_account);
//                map.put("latitude", latitude);
//                map.put("longitude", longitude);
//                return map;
//            }
//        };
//        requestQueue.add(request);
//    }


}