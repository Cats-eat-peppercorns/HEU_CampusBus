package com.example.myapplication.Activity;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;


import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R;
import com.example.myapplication.databinding.ActivityUserRegisterBinding;
import com.example.myapplication.javabean.ServerSetting;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.Format;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class UserRegisterActivity extends AppCompatActivity {
    private ActivityUserRegisterBinding binding;
    Intent intent = new Intent();
    boolean isAvatarSelect =false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityUserRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        registerSubmit();
        avatarOption();
    }

    public void registerSubmit() {
        binding.submitUserregisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.nameUserregisterEt.getText().length() == 0) {
                    Toast.makeText(UserRegisterActivity.this, "名称不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    if (binding.accountUserregisterEt.getText().length() == 0) {
                        Toast.makeText(UserRegisterActivity.this, "账号不能为空", Toast.LENGTH_SHORT).show();
                    }
                    else if(binding.accountUserregisterEt.getText().length() != 0&&binding.passwordUserregisterEt.getText().length()==0){
                        Toast.makeText(UserRegisterActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        if(!isAvatarSelect){
                            Toast.makeText(UserRegisterActivity.this, "请上传头像", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            //先获得输入框内用户注册的账号与密码

                            String nameSubmit = binding.nameUserregisterEt.getText().toString();
                            String accountSubmit = binding.accountUserregisterEt.getText().toString();
                            String passwordSubmit = binding.passwordUserregisterEt.getText().toString();
                            binding.avatarUserregisterIv.setDrawingCacheEnabled(true);
                            Bitmap bitmap=binding.avatarUserregisterIv.getDrawingCache();//获得上传头像的bitmap
                            String avatarSubmit=getDiskBitmap2Base64(bitmap);//bitmap转化为base64数据流
                            register_request(nameSubmit, accountSubmit, passwordSubmit,avatarSubmit);
                            binding.avatarUserregisterIv.setDrawingCacheEnabled(false);
                        }

                    }
                }
            }
        });
    }
    private void register_request(String name, String account, String password,String avatar) {
        String url = ServerSetting.ServerPublicIpAndPort+"register/";
        OkHttpClient okHttpClient=new OkHttpClient();
        RequestBody requestBody=new FormBody.Builder()
                .add("name", name)
                .add("account", account)
                .add("password", password)
                .add("avatar",avatar)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Call call=okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "注册失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(UserRegisterActivity.this, LoginActivity.class));
                    }
                });

            }
        });
    }
    public void avatarOption(){
        binding.avatarUserregisterIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGetContent.launch("image/*");
            }
        });
    }
    private Bitmap getBitmapFromUri(Uri uri) throws FileNotFoundException {
        return BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
    }
    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
        new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri uri) {
                Bitmap bitmap = null;
                //Uri转化为Bitmap
                try {
                    bitmap = getBitmapFromUri(uri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                binding.avatarUserregisterIv.setImageBitmap(bitmap);
                binding.avatarUserregisterIv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                isAvatarSelect =true;
            }
    });
    public static String getDiskBitmap2Base64(Bitmap bitmap){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        //参数2：压缩率，40表示压缩掉60%; 如果不压缩是100，表示压缩率为0
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] bytes = bos.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }
}