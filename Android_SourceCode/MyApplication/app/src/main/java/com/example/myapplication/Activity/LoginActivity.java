package com.example.myapplication.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.databinding.ActivityLoginBinding;
import com.example.myapplication.fragment.UserLoginFragment;
import com.example.myapplication.javabean.LoginAdapter;
import com.example.myapplication.javabean.PictureSaveSetting;
import com.example.myapplication.javabean.ServerSetting;
import com.example.myapplication.javabean.django_url;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    LoginAdapter adapter;
    private ActivityLoginBinding binding;
    private Handler handlerLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Dynamic_application_positioning();
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("我是学生"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("我是司机"));
        binding.tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        adapter=new LoginAdapter(this,this,binding.tabLayout.getTabCount());
        binding.viewPager.setAdapter(adapter);
        PictureSaveSetting.avatarPath=this.getFilesDir().getAbsolutePath();
        handlerLogin=new MyHandler(this);
        pathCreate();//创建头像存储文件夹
        backToLogin();//返回登录界面处理
    }
    @Override
    protected void onResume() {
        super.onResume();
        viewPaperSelectListener();
    }
    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
    private void Dynamic_application_positioning() {
        // 检查应用是否已经授权定位权限
        if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // 应用已经被授权定位权限，可以进行定位操作
            // 执行相关操作...
        } else {
            locationPermissionRequest.launch(new String[]{  //申请定位权限
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
    }
    ActivityResultLauncher<String[]> locationPermissionRequest = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                Boolean fineLocationGranted = result.getOrDefault(
                        android.Manifest.permission.ACCESS_FINE_LOCATION, false);
                Boolean coarseLocationGranted = result.getOrDefault(
                        Manifest.permission.ACCESS_COARSE_LOCATION,false);
                if (fineLocationGranted != null && fineLocationGranted) {
                    Toast.makeText(LoginActivity.this, "定位权限申请成功", Toast.LENGTH_SHORT).show();
                    // Precise location access granted.
                } else if (coarseLocationGranted != null && coarseLocationGranted) {
                    Toast.makeText(LoginActivity.this, "只能使用模糊定位，失去大部分功能，建议使用精准定位", Toast.LENGTH_SHORT).show();
                    // Only approximate location access granted.
                } else {
                    Toast.makeText(LoginActivity.this, "定位权限申请失败", Toast.LENGTH_SHORT).show();
                    // No location access granted.
                }
            }
    );
    public void viewPaperSelectListener(){
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.viewPager.setCurrentItem(tab.getPosition(), true);
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Do nothing
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Do nothing
            }
        });
        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position));
            }
        });
    }
    public void userLoginRequest(String account, String password) {
        String url = ServerSetting.ServerPublicIpAndPort+"isLogin/";
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("account",account)
                .add("password",password)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                handlerLogin.sendEmptyMessage(0);
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull okhttp3.Response response) throws IOException {
                String nameResponse = null;
                String accountResponse = null;
                String passwordResponse = null;
                String avatarResponse = null;
                try {
                    JSONObject object;
                    if (response.body() != null) {
                        object = new JSONObject(response.body().string());
                        nameResponse=object.getString("name");
                        accountResponse=object.getString("account");
                        passwordResponse=object.getString("password");
                        avatarResponse=object.getString("avatar");
                    }

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                SharedPreferences userSettings=getSharedPreferences("user",MODE_PRIVATE);
                SharedPreferences.Editor editor=userSettings.edit();
                Headers headers = response.headers();
                List<String> cookie = headers.values("Set-Cookie");
                String cookieString=cookie.toString();
                String sessionId=cookieString.substring(1, cookieString.indexOf(";"));
                editor.putString("sessionId",sessionId);
                editor.putString("userName",nameResponse);
                editor.putString("account",accountResponse);
                editor.putString("password",passwordResponse);
                editor.putBoolean("isExit",true);
                editor.apply();
                Bitmap avatarBitmap=Base64ToBitmap(avatarResponse);
                saveImage(avatarBitmap);
                Log.e("sessionId",sessionId);
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                handlerLogin.sendEmptyMessage(1);
            }
        });
    }

    public void driverLoginRequest(String account,String password) {
        String url = ServerSetting.ServerPublicIpAndPort+"driver_login/";
        OkHttpClient okHttpClient = new OkHttpClient();  //构建一个网络类型的实例
        RequestBody requestBody = new FormBody.Builder()
                .add("account",account)
                .add("password",password)
                .build();
        Request request = new Request.Builder() //构建一个具体的网络请求对象，具体的请求url，请求头，请求体等
                .url(url)
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request); //将具体的网络请求与执行请求的实体进行绑定
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                handlerLogin.sendEmptyMessage(2); //sendEmptyMessage(int what)而sendMessage(Message msg)
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull okhttp3.Response response) throws IOException {
                if (!response.isSuccessful()) {
                    // 请求不成功，处理错误响应
                    final String errorMessage = response.body().string();
                    // 切换到主线程更新 UI
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "登录失败：" + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    String nameResponse = null;
                    String accountResponse = null;
                    String passwordResponse = null;
                    String numberResponse = null;
                    String schoolResponse = null;
                    String lineResponse = null;
                    try {
                        if (response.body() != null) {
                            JSONObject object = new JSONObject(response.body().string());   //将string类型的response转换为JSONObject类型的object
                            nameResponse = object.getString("name");
                            accountResponse = object.getString("account");
                            passwordResponse = object.getString("password");
                            numberResponse = object.getString("phone");
                            schoolResponse = object.getString("school_name_new");
                            lineResponse = object.getString("line_name_new");
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    SharedPreferences userSettings = getSharedPreferences("user", MODE_PRIVATE);
                    //创建了一个名为 "user" 的 SharedPreferences 对象，然后获取其编辑器并将用户信息存储进去。
                    SharedPreferences.Editor editor = userSettings.edit();
                    //SharedPreferences.Editor是用于编辑SharedPreferences数据的接口，
                    // 通过调用edit()方法获取一个SharedPreferences.Editor实例，然后可以利用该实例进行数据的添加修改或删除操作
                    editor.putString("driver_name", nameResponse);
                    editor.putString("driver_account", accountResponse);  //数据是以键值对的方式存储的
                    editor.putString("driver_password", passwordResponse);
                    editor.putString("driver_number", numberResponse);
                    editor.putString("driver_school", schoolResponse);
                    editor.putString("driver_line", lineResponse);
                    editor.putBoolean("driver_isExit", true);
                    editor.apply();   //利用commit方法可以将更改保存到SharedPreferences中
                    Intent intent = new Intent(LoginActivity.this, Driver_main_interface.class);
                    startActivity(intent);
                    handlerLogin.sendEmptyMessage(1);
                    LoginActivity.this.finish();
                }
            }
        });
    }


    public static Bitmap Base64ToBitmap(String string) {
        Bitmap bitmap = null;
        try {
            byte[] decodedBytes = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
    public void saveImage(Bitmap bitmap) {
        FileOutputStream fos;
        try {
            fos=new FileOutputStream(PictureSaveSetting.avatarPath+"avatar.png");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    //用户头像存储路径的建立
    public void pathCreate(){
        PictureSaveSetting.avatarPath= PictureSaveSetting.avatarPath+"pictures";
        File nomediaFile = new File(PictureSaveSetting.avatarPath);
        nomediaFile.mkdirs();
    }
    public static boolean isGrantExternalRW(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);
            return false;
        }
        return true;
    }
    //返回登录界面时不同情况的处理
    public void backToLogin(){
        //获得传递来的intent
        Intent intent = getIntent();
        if(intent.getStringExtra("status")!=null){
            //根据不同的返回状态值进行后续处理
            switch (intent.getStringExtra("status")){
                //代表应用因为账号重复登录返回
                case "duplicateLogins":
                    //展示重复登陆提示框
                    AlertDialog.Builder builder1=new AlertDialog.Builder(this);
                    builder1.setMessage("检测到重复登录")
                            .create()
                            .show();
                case "exitaccount":
                    Toast.makeText(this,"退出账号成功",Toast.LENGTH_LONG).show();
            }
        }
    }
    //自定义Handler，用于网络请求回调后和主线程通信
    private static class MyHandler extends Handler {
        //弱引用持有HandlerActivity , GC回收时会被回收掉
        WeakReference<LoginActivity> weakReference;
        public MyHandler(LoginActivity activity) {
            weakReference = new WeakReference<>(activity);
        }
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            LoginActivity activity = weakReference.get();
            //根据传递来的msg的值进行不同处理
            switch (msg.what) {
                case 1:
                    Toast.makeText(activity, "登陆成功", Toast.LENGTH_SHORT).show();
                    activity.finish();
                    break;
                case 0:
                    Toast.makeText(activity, "账户不存在或密码错误", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}