package com.example.myapplication.fragment;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentLocationShowBinding;
import com.example.myapplication.javabean.MyLocation;
import com.example.myapplication.javabean.ServerSetting;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class LocationShowFragment extends Fragment {
    public MyLocation myLocation;
    public MapUserFragment mapUserFragment;
    public LocationShowFragment(MapUserFragment mapUserFragment,MyLocation myLocation){
        this.mapUserFragment=mapUserFragment;
        this.myLocation=myLocation;
    }
    public FragmentLocationShowBinding binding;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=FragmentLocationShowBinding.inflate(getLayoutInflater());
    }
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=binding.getRoot();
        binding.locationNameShowTv.setText(myLocation.getLocationName());
        getLocationIvRequest();
        return view;
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        mapUserFragment.stopLocationShowInMap();
    }
    public void getLocationIvRequest(){
        String url = ServerSetting.ServerPublicIpAndPort+"getLocationImage/";
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("locationId",myLocation.getLocationId())
                .add("IvPath",myLocation.getLocationName())
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull okhttp3.Response response) throws IOException {
                Log.e("Response","Success");
                byte[] imageBytes = response.body().bytes();
                final Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

                // Update the UI on the main thread
                binding.locationPictureIv.post(new Runnable() {
                    @Override
                    public void run() {
                        binding.locationPictureIv.setImageBitmap(bitmap);
                    }
                });
            }
        });
    }
}