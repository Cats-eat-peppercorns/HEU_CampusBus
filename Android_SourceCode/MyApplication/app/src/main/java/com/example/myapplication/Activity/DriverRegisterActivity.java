package com.example.myapplication.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R;
import com.example.myapplication.databinding.ActivityDriverRegisterBinding;
import com.example.myapplication.javabean.ServerSetting;
import com.example.myapplication.javabean.django_url;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class DriverRegisterActivity extends AppCompatActivity {
    private ActivityDriverRegisterBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityDriverRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerSubmit();
    }

    private void registerSubmit() {
        binding.submitDriverRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int name_length=binding.nameDriverregisterEt.getText().length();
                int account_length=binding.accountDriverregisterEt.getText().length();
                int password_length=binding.passwordDriverregisterEt.getText().length();
                int school_length=binding.schoolDriverregisterEt.getText().length();
                int line_length=binding.lineDriverregisterEt.getText().length();
                int phone_length=binding.phoneDriverregisterEt.getText().length();
                if(name_length>0&&name_length<=20)
                {
                    if(account_length>0&&account_length<=16)
                    {
                        if(password_length>0&&password_length<=16)
                        {
                            if(school_length>0&&school_length<=100)
                            {
                                if(line_length>0&&line_length<=100)
                                {
                                    if(phone_length>0&&phone_length<=11)
                                    {
                                        String nameSubmit = binding.nameDriverregisterEt.getText().toString();
                                        String accountSubmit = binding.accountDriverregisterEt.getText().toString();
                                        String passwordSubmit = binding.passwordDriverregisterEt.getText().toString();
                                        String schoolSubmit=binding.schoolDriverregisterEt.getText().toString();
                                        String lineSubmit=binding.lineDriverregisterEt.getText().toString();
                                        String phoneSubmit=binding.phoneDriverregisterEt.getText().toString();
                                        //Log.e("TAG?!?!?!?!", "run: ");
                                        register_request(nameSubmit, accountSubmit, passwordSubmit,schoolSubmit,lineSubmit,phoneSubmit);
                                    }else{
                                        Toast.makeText(DriverRegisterActivity.this, "请正确填写电话", Toast.LENGTH_SHORT).show();
                                    }
                                }else{
                                    Toast.makeText(DriverRegisterActivity.this, "请正确填写线路", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(DriverRegisterActivity.this, "请正确填写学校", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(DriverRegisterActivity.this, "请正确填写密码", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(DriverRegisterActivity.this, "请正确填写账号", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(DriverRegisterActivity.this, "请正确填写姓名", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void register_request(String name, String account, String password, String school, String line, String phone) {
        String url = ServerSetting.ServerPublicIpAndPort+"driver_register/";
        OkHttpClient okHttpClient = new OkHttpClient();  //构建一个网络类型的实例
        RequestBody requestBody = new FormBody.Builder()
                .add("name",name)
                .add("account",account)
                .add("password", password)
                .add("school", school)
                .add("line", line)
                .add("phone", phone)
                .build();
        Request request = new Request.Builder() //构建一个具体的网络请求对象，具体的请求url，请求头，请求体等
                .url(url)
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request); //将具体的网络请求与执行请求的实体进行绑定
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(DriverRegisterActivity.this, "注册失败：请检查网络", Toast.LENGTH_SHORT).show();
                    }
                });
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
                            Toast.makeText(DriverRegisterActivity.this, "注册失败：" + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(DriverRegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Log.e("TAG?!?!?!?!", "run:222222 ");
                    Intent intent = new Intent(DriverRegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    DriverRegisterActivity.this.finish();
                }
            }
        });
    }

}