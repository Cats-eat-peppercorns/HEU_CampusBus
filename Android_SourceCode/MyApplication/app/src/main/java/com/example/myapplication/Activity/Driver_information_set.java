package com.example.myapplication.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R;
import com.example.myapplication.javabean.ServerSetting;
import com.example.myapplication.javabean.django_url;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class Driver_information_set extends AppCompatActivity {
    private EditText nameRegister,accountRegister,passwordRegister,schoolRegister,lineRegister,phoneRegister;
    private Button submitButton,deleteAccountButton;
    private String True_account;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_information_set);
        nameRegister = findViewById(R.id.name_register_editText);
        accountRegister = findViewById(R.id.account_register_editText);
        passwordRegister = findViewById(R.id.password_register_editText);
        schoolRegister = findViewById(R.id.school_register_editText);
        lineRegister = findViewById(R.id.line_register_editText);
        phoneRegister = findViewById(R.id.phone_register_editText);
        submitButton = findViewById(R.id.register_submit_button);
        deleteAccountButton =findViewById(R.id.delete_account);

        SharedPreferences sharedPreferences= getSharedPreferences("user", Context.MODE_PRIVATE);
        nameRegister.setText(sharedPreferences.getString("driver_name",""));
        accountRegister.setText(sharedPreferences.getString("driver_account",""));
        passwordRegister.setText(sharedPreferences.getString("driver_password",""));
        schoolRegister.setText(sharedPreferences.getString("driver_school",""));
        lineRegister.setText(sharedPreferences.getString("driver_line",""));
        phoneRegister.setText(sharedPreferences.getString("driver_number",""));
        True_account= sharedPreferences.getString("driver_account","");
        onResume();
    }

    @Override
    protected void onResume() {
        super.onResume();
        deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Driver_information_set.this);
                builder.setTitle("确认框");
                builder.setMessage("确定要执行操作吗？");
                builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 用户选择是时的操作
                        Delete_account();
                        //Toast.makeText(Driver_information_set.this, "您选择了是", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                        // 用户选择否时的操作
                        //Toast.makeText(Driver_information_set.this, "您选择了否", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int name_length=nameRegister.getText().length();
                int account_length=accountRegister.getText().length();
                int password_length=passwordRegister.getText().length();
                int school_length=schoolRegister.getText().length();
                int line_length=lineRegister.getText().length();
                int phone_length=phoneRegister.getText().length();
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
                                        String nameSubmit = nameRegister.getText().toString();
                                        String accountSubmit = accountRegister.getText().toString();
                                        String passwordSubmit = passwordRegister.getText().toString();
                                        String schoolSubmit=schoolRegister.getText().toString();
                                        String lineSubmit=lineRegister.getText().toString();
                                        String phoneSubmit=phoneRegister.getText().toString();
                                        information_set(nameSubmit, accountSubmit, passwordSubmit,schoolSubmit,lineSubmit,phoneSubmit);
                                    }else{
                                        Toast.makeText(Driver_information_set.this, "请正确填写电话", Toast.LENGTH_SHORT).show();
                                    }
                                }else{
                                    Toast.makeText(Driver_information_set.this, "请正确填写线路", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(Driver_information_set.this, "请正确填写学校", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(Driver_information_set.this, "请正确填写密码", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(Driver_information_set.this, "请正确填写账号", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(Driver_information_set.this, "请正确填写姓名", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void Delete_account() {
        String url = ServerSetting.ServerPublicIpAndPort+"delete_driver_account/";
        OkHttpClient okHttpClient = new OkHttpClient();  //构建一个网络类型的实例
        RequestBody requestBody = new FormBody.Builder()
                .add("true_account",True_account)
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
                        Toast.makeText(Driver_information_set.this, "删除失败：请检查网络", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(Driver_information_set.this, "删除失败：" + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    String workmates_name,workmates_account,workmates_latitude,workmates_longitude;
                    BitmapDescriptor driver_picture = BitmapDescriptorFactory.fromResource(R.drawable.driver_mates);
                    if (response.body() != null){
                        SharedPreferences userSettings=getSharedPreferences("user",MODE_PRIVATE);
                        //创建了一个名为 "user" 的 SharedPreferences 对象，然后获取其编辑器并将用户信息存储进去。
                        SharedPreferences.Editor editor=userSettings.edit();
                        //SharedPreferences.Editor是用于编辑SharedPreferences数据的接口，
                        // 通过调用edit()方法获取一个SharedPreferences.Editor实例，然后可以利用该实例进行数据的添加修改或删除操作
                        editor.putString("driver_name","");
                        editor.putString("driver_account","");  //数据是以键值对的方式存储的
                        editor.putString("driver_password","");
                        editor.putString("driver_number","");
                        editor.putString("driver_school","");
                        editor.putString("driver_line","");
                        editor.putBoolean("driver_isExit",false);
                        editor.apply();   //利用commit方法可以将更改保存到SharedPreferences中
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Driver_information_set.this, "删除成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                        Intent intent = new Intent(Driver_information_set.this, LoginActivity.class);
                        startActivity(intent);
                        Driver_information_set.this.finish();
                    }
                }
            }
        });
    }

//    private void Delete_account1() {
//        String url = django_url.url+"delete_driver_account/";
//        RequestQueue requestQueue = Volley.newRequestQueue(Driver_information_set.this);
//        StringRequest request = new StringRequest(1, url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                SharedPreferences userSettings=getSharedPreferences("user",MODE_PRIVATE);
//                //创建了一个名为 "user" 的 SharedPreferences 对象，然后获取其编辑器并将用户信息存储进去。
//                SharedPreferences.Editor editor=userSettings.edit();
//                //SharedPreferences.Editor是用于编辑SharedPreferences数据的接口，
//                // 通过调用edit()方法获取一个SharedPreferences.Editor实例，然后可以利用该实例进行数据的添加修改或删除操作
//                editor.putString("driver_name","");
//                editor.putString("driver_account","");  //数据是以键值对的方式存储的
//                editor.putString("driver_password","");
//                editor.putString("driver_number","");
//                editor.putString("driver_school","");
//                editor.putString("driver_line","");
//                editor.putBoolean("driver_isExit",false);
//                editor.apply();   //利用commit方法可以将更改保存到SharedPreferences中
//                Toast.makeText(Driver_information_set.this, "删除成功", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(Driver_information_set.this, Login_Activity.class);
//                startActivity(intent);
//                Driver_information_set.this.finish();
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
//                            Toast.makeText(Driver_information_set.this, "删除失败、"+responseString, Toast.LENGTH_SHORT).show();
//                            // 处理响应字符串
//                        }
//                    }
//                }
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> map = new HashMap<>();
//                map.put("true_account",True_account);
//                return map;
//            }
//        };
//        request.setTag("volleyGet");
//        requestQueue.add(request);
//    }


    public void information_set(String name, String account, String password, String school, String line, String phone) {
        String url = ServerSetting.ServerPublicIpAndPort+"driver_information_set/";
        OkHttpClient okHttpClient = new OkHttpClient();  //构建一个网络类型的实例
        RequestBody requestBody = new FormBody.Builder()
                .add("name", name)
                .add("account", account)
                .add("password", password)
                .add("school", school)
                .add("line", line)
                .add("phone", phone)
                .add("true_account",True_account)
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
                        Toast.makeText(Driver_information_set.this, "修改失败：请检查网络", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull okhttp3.Response response) throws IOException {
                if (!response.isSuccessful() && response.body() != null) {
                    // 请求不成功，处理错误响应
                    final String errorMessage;
                    errorMessage = response.body().string();
                    // 切换到主线程更新 UI
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Driver_information_set.this, "获取失败：" + errorMessage, Toast.LENGTH_SHORT).show();
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
                        if (response.body() != null)
                        {
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
                    Intent intent = new Intent(Driver_information_set.this, Driver_main_interface.class);
                    startActivity(intent);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Driver_information_set.this, "修改成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Driver_information_set.this.finish();
                }
            }
        });
    }

//    private void register_request1(String name, String account, String password, String school, String line, String phone) {
//
//        String url = django_url.url+"driver_information_set/";
//        RequestQueue requestQueue = Volley.newRequestQueue(Driver_information_set.this);
//        StringRequest request = new StringRequest(1, url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                String nameResponse;
//                String accountResponse;
//                String passwordResponse;
//                String numberResponse;
//                String schoolResponse;
//                String lineResponse;
//                try {
//                    JSONObject object=new JSONObject(response);   //将string类型的response转换为JSONObject类型的object
//                    nameResponse=object.getString("name");
//                    accountResponse=object.getString("account");
//                    passwordResponse=object.getString("password");
//                    numberResponse =object.getString("phone");
//                    schoolResponse=object.getString("school_name_new");
//                    lineResponse=object.getString("line_name_new");
//                } catch (JSONException e) {
//                    throw new RuntimeException(e);
//                }
//                SharedPreferences userSettings=getSharedPreferences("user",MODE_PRIVATE);
//                //创建了一个名为 "user" 的 SharedPreferences 对象，然后获取其编辑器并将用户信息存储进去。
//                SharedPreferences.Editor editor=userSettings.edit();
//                //SharedPreferences.Editor是用于编辑SharedPreferences数据的接口，
//                // 通过调用edit()方法获取一个SharedPreferences.Editor实例，然后可以利用该实例进行数据的添加修改或删除操作
//                editor.putString("driver_name",nameResponse);
//                editor.putString("driver_account",accountResponse);  //数据是以键值对的方式存储的
//                editor.putString("driver_password",passwordResponse);
//                editor.putString("driver_number",numberResponse);
//                editor.putString("driver_school",schoolResponse);
//                editor.putString("driver_line",lineResponse);
//                editor.putBoolean("driver_isExit",true);
//                editor.apply();   //利用commit方法可以将更改保存到SharedPreferences中
//
//                Toast.makeText(Driver_information_set.this, "修改成功", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(Driver_information_set.this, Driver_main_interface.class);
//                startActivity(intent);
//                Driver_information_set.this.finish();
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
//                            Toast.makeText(Driver_information_set.this, "修改失败、"+responseString, Toast.LENGTH_SHORT).show();
//                            // 处理响应字符串
//                        }
//                    }
//                }
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> map = new HashMap<>();
//                map.put("name", name);
//                map.put("account", account);
//                map.put("password", password);
//                map.put("school", school);
//                map.put("line", line);
//                map.put("phone", phone);
//                map.put("true_account",True_account);
//                return map;
//            }
//        };
//        request.setTag("volleyGet");
//        requestQueue.add(request);
//    }





}