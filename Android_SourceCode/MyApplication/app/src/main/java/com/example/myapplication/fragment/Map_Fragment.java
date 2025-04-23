//package com.example.myapplication.fragment;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.os.Bundle;
//
//import androidx.annotation.NonNull;
//import androidx.core.widget.NestedScrollView;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.os.Handler;
//import android.os.Message;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.Toast;
//
//import com.amap.api.location.AMapLocationClient;
//import com.amap.api.maps.AMap;
//import com.amap.api.maps.MapView;
//import com.amap.api.maps.model.LatLng;
//import com.amap.api.maps.model.Marker;
//import com.amap.api.maps.model.MarkerOptions;
//import com.amap.api.maps.model.MyLocationStyle;
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.StringRequest;
//import com.android.volley.toolbox.Volley;
//import com.example.myapplication.R;
//import com.example.myapplication.javabean.ServerSetting;
//import com.example.myapplication.myappact.Login_Activity;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.BufferedInputStream;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.lang.ref.WeakReference;
//
//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.FormBody;
//import okhttp3.OkHttpClient;
//import okhttp3.RequestBody;
//
//
//public class Map_Fragment extends Fragment {
//    MapView mMapView = null;
//    AMap aMap;
//    Bundle bundle;
//    int loginPar=1;
//    String latitudeBusString;
//    String longitudeBusString;
//    Marker marker;
//    Button start_button;
//    Button duplicateLoginsTest_button;
//    RecyclerView recyclerView;
//    NestedScrollView nestedScrollView;
//    boolean ONorOff=false;
//    View view;
//    private Handler Myhandler;
//
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Myhandler =new MyHandler((MainActivity2) getActivity());
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        //弹窗权限确认
//        AMapLocationClient.updatePrivacyShow(getActivity(), true, true);
//        AMapLocationClient.updatePrivacyAgree(getActivity(), true);
////        view = inflater.inflate(R.layout.fragment_map, container, false);
//        start_button = view.findViewById(R.id.start_button);
//        duplicateLoginsTest_button=view.findViewById(R.id.duplicateLoginsTest_button);
//        //nestedScrollView=view.findViewById(R.id.nestedScrollView);
//        mMapView = view.findViewById(R.id.map);
//        mMapView.onCreate(savedInstanceState);
//        Location();
//        return view;
//    }
//    private void Location() {
//        aMap = mMapView.getMap();
//        MyLocationStyle myLocationStyle;
//        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
//        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
////        Log.e("1",PictureSaveSetting.avatarPath);
////        float scaleWidth = 0.17F;
////        float scaleHeight = 0.17F;
////        Matrix matrix = new Matrix();
////        matrix.postScale(scaleWidth,scaleHeight);
////        bitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,false);
////        bitmap=getRoundedBitmap(bitmap);
////        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
////        myLocationStyle.myLocationIcon(bitmapDescriptor);//设置定位蓝点的icon图标方法，需要用到BitmapDescriptor类对象作为参数。
//        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
//        //aMap.getUiSettings().setMyLocationButtonEnabled(true);设置默认定位按钮是否显示，非必需设置。
//        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER);//连续定位、蓝点不会移动到地图中心点，并且蓝点会跟随设备移动。（1秒1次定位）
//        myLocationStyle.interval(5000);//设置定位频次方法，单位：毫秒，默认值：1000毫秒，如果传小于1000的任何值将按照1000计算。该方法只会作用在会执行连续定位的工作模式上。
////        aMap.setOnMyLocationChangeListener(onMyLocationChangeListener);
//        startLocation();
//    }
//    public void locationSend(String latitude, String longitude) {
//        String url = ServerSetting.ServerPublicIpAndPort+"sendLocation/";
//        OkHttpClient okHttpClient = new OkHttpClient();
//        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
//        String sessionId=sharedPreferences.getString("sessionId","");
//        RequestBody requestBody = new FormBody.Builder()
//                .add("latitude", latitude)
//                .add("longitude", longitude)
//                .build();
//        okhttp3.Request request = new okhttp3.Request.Builder()
//                .url(url)
//                .addHeader("Cookie",sessionId)
//                .post(requestBody)
//                .build();
//        Call call = okHttpClient.newCall(request);
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Myhandler.sendEmptyMessage(0);
//            }
//            @Override
//            public void onResponse(Call call, okhttp3.Response response) throws IOException {
//                Myhandler.sendEmptyMessage(1);
//            }
//        });
//    }
//
//    public void locationGet() {
//         String url = ServerSetting.ServerPublicIpAndPort+"getLocation/";
//         RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
//         StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
//             @Override
//             public void onResponse(String response) {
//                 try {
//                     JSONObject object = new JSONObject(response);
//                     latitudeBusString = object.getString("latitude");
//                     longitudeBusString = object.getString("longitude");
//                 } catch (JSONException e) {
//                        throw new RuntimeException(e);
//                 }
//                 Toast.makeText(getActivity(), latitudeBusString + " " + longitudeBusString, Toast.LENGTH_SHORT).show();
//             }
//         }, new Response.ErrorListener() {
//             @Override
//             public void onErrorResponse(VolleyError error) {
//                 Toast.makeText(getActivity(), "获得定位失败", Toast.LENGTH_SHORT).show();
//             }
//         });
//         requestQueue.add(request);
//    }
//
//    public void  locationDraw(String latitudeBusString, String longitudeBusString) {
//        double latitude = Double.parseDouble(latitudeBusString);
//        double longitude = Double.parseDouble(longitudeBusString);
//        LatLng latLng = new LatLng(latitude, longitude);
//        marker=aMap.addMarker(new MarkerOptions().position(latLng));
//    }
//
//    public void startLocation(){
//        start_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ONorOff=!ONorOff;
//            }
//        });
//    }
//
//    public Bitmap openImage(String path){
//        Bitmap bitmap=null;
//        try {
//            BufferedInputStream bis=new BufferedInputStream(new FileInputStream(path));
//            bitmap=BitmapFactory.decodeStream(bis);
//            bis.close();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return bitmap;
//    }
//
//    public void duplicateLoginsTest(){
//        duplicateLoginsTest_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String url = ServerSetting.ServerPublicIpAndPort+"duplicateLoginsTest/";
//                OkHttpClient okHttpClient = new OkHttpClient();
//                SharedPreferences sharedPreferences=getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
//                String sessionId=sharedPreferences.getString("sessionId","");
//                RequestBody requestBody = new FormBody.Builder()
//                        .add("account", "1111")
//                        .build();
//                okhttp3.Request request = new okhttp3.Request.Builder()
//                        .url(url)
//                        .addHeader("Cookie",sessionId)
//                        .post(requestBody)
//                        .build();
//                Call call = okHttpClient.newCall(request);
//                call.enqueue(new Callback() {
//                    @Override
//                    public void onFailure(Call call, IOException e) {
//                        Myhandler.sendEmptyMessage(10);
//                    }
//                    @Override
//                    public void onResponse(Call call, okhttp3.Response response) throws IOException {
//                        String context=response.body().string();
//                        Log.e("httpResponse",context);
//                        Myhandler.sendEmptyMessage(Integer.valueOf(context));
//                    }
//                });
//            }
//        });
//    }
//    public static class MyHandler extends Handler {
//        WeakReference<MainActivity2> weakReference;
//        public MyHandler(MainActivity2 activity) {
//            weakReference = new WeakReference<MainActivity2>(activity);
//        }
//        @Override
//        public void handleMessage(@NonNull Message msg) {
//            super.handleMessage(msg);
//            MainActivity2 activity = weakReference.get();
//            switch (msg.what){
//                case 1:
//                    Toast.makeText(activity, "Ok", Toast.LENGTH_SHORT).show();
//                    break;
//                case 0:
//                    Toast.makeText(activity, "Error", Toast.LENGTH_SHORT).show();
//                    break;
//            }
//            Intent intentBackLogin=new Intent(activity, Login_Activity.class);
//            intentBackLogin.putExtra("status","duplicateLogins");
//            activity.startActivity(intentBackLogin);
//            activity.finish();
//        }
//    }
//}