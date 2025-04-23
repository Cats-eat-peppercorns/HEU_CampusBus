package com.example.myapplication.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.ui.AppBarConfiguration;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.myapplication.Activity.LoginActivity;
import com.example.myapplication.Activity.MainActivity;
import com.example.myapplication.Activity.UserRegisterActivity;
import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentUserBinding;
import com.example.myapplication.javabean.PictureSaveSetting;
import com.example.myapplication.javabean.ServerSetting;
import com.example.myapplication.Activity.PersonaInformationActivity;
import com.example.myapplication.Activity.Setting_Activity;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class UserFragment extends Fragment {
    View view;
    Intent intentToPersonal;
    Intent intentToSetting;
    private FragmentUserBinding binding;
    private AppBarConfiguration mAppBarConfiguration;
    //头像存储路径
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentUserBinding.inflate(getLayoutInflater());
        view=binding.getRoot();
        intentToPersonal=new Intent(getActivity(), PersonaInformationActivity.class);
        intentToSetting=new Intent(getActivity(), Setting_Activity.class);
        userInformationSet();
        buttonListener();
        return view;
    }
    public void userInformationSet(){
        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        binding.usernameTv.setText(sharedPreferences.getString("userName",""));
        binding.accountTv.setText(sharedPreferences.getString("account",""));
        binding.avatarIv.setImageBitmap(openImage(PictureSaveSetting.avatarPath+"avatar.png"));
        binding.avatarIv.setOnClickListener(view1 -> {
            startActivity(intentToPersonal);
        });
    }
    public void buttonListener(){
        binding.modifyInformationIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intentToPersonal);
            }
        });
        binding.settingBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intentToSetting);
            }
        });
        binding.exitaccountBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exit_request();
            }
        });
    }
    public void exit_request(){
        String url = ServerSetting.ServerPublicIpAndPort+"exitAccount/";
        OkHttpClient okHttpClient = new OkHttpClient();
        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        String sessionId=sharedPreferences.getString("sessionId","");
        String account=sharedPreferences.getString("account","");
        RequestBody requestBody = new FormBody.Builder()
                .add("account",account)
                .build();
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .addHeader("Cookie",sessionId)
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }
            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                SharedPreferences preferences =getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.apply();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(requireContext(), LoginActivity.class);
                        intent.putExtra("status", "exitaccount");
                        startActivity(intent);
                        if(getActivity() != null) {
                            getActivity().finish(); // 销毁当前 Activity
                        }
                    }
                });

            }
        });
    }
    public Bitmap openImage(String path){
        Bitmap bitmap=null;
        try {
            BufferedInputStream bis=new BufferedInputStream(new FileInputStream(path));
            bitmap=BitmapFactory.decodeStream(bis);
            bis.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return bitmap;
    }
}