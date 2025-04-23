package com.example.myapplication.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps.MapsInitializer;
import com.example.myapplication.MyService;
import com.example.myapplication.R;
import com.example.myapplication.fragment.MapUserFragment;
import com.example.myapplication.fragment.RouteSearchFragment;
import com.example.myapplication.fragment.UserFragment;
import com.example.myapplication.javabean.BusDriver;
import com.example.myapplication.javabean.CodeDiaLogFragment;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import com.example.myapplication.databinding.ActivityMainBinding;
import com.example.myapplication.databinding.NavHeaderMainBinding;
import com.example.myapplication.databinding.FragmentMapUserBinding;
import com.example.myapplication.javabean.PictureSaveSetting;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

public class MainActivity extends AppCompatActivity implements CodeDiaLogFragment.OnDialogDismissListener, RouteSearchFragment.OnIsPopBackValueChangedListener {
    private ActivityMainBinding binding;
    public MapUserFragment mapUserFragment;
    MenuItem code_item,list_item,user_item;
    boolean isPopBack=true;
    View headerView;
    @Override
    public void onBackPressed() {
        // 获取当前显示的子Fragment
        MapUserFragment mapUserFragment = (MapUserFragment) getSupportFragmentManager().findFragmentById(R.id.main_page_fragment);
        if (mapUserFragment != null&&isPopBack) {
            mapUserFragment.handleBackPressed();
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public void onBooleanValueChanged(boolean newValue) {
        isPopBack=newValue;
    }
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        NavigationView navView=findViewById(R.id.navView);
        headerView = navView.getHeaderView(0);
        navUISetting();//导航相关UI控件的设置
        mapUserFragment=new MapUserFragment(this);
        Intent serviceIntent = new Intent(this, MyService.class);
        startService(serviceIntent);
        MapsInitializer.updatePrivacyShow(MainActivity.this,true,true);
        MapsInitializer.updatePrivacyAgree(MainActivity.this,true);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(binding.appMain.mainPageFragment.getId(),mapUserFragment,"MapUserFragment")
                .commit();
    }
    @Override
    protected void onResume() {
        super.onResume();

        mainUIListener();//主要UI的点击事件监听
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
    }
    @Override
    public void onDismiss () {
        code_item.setCheckable(false);
    }
    public void mapSetting(){

    }
    public void navUISetting(){
        //底部导航栏图标绑定
        Menu navBottomMenu = binding.appMain.navBottomView.getMenu();
        code_item= navBottomMenu.findItem(R.id.code_it);
        list_item= navBottomMenu.findItem(R.id.home_it);
        user_item= navBottomMenu.findItem(R.id.user_it);
        ImageView avatarNavIv=headerView.findViewById(R.id.avatar_nav_iv);
        avatarNavIv.setImageBitmap(openImage(PictureSaveSetting.avatarPath + "avatar.png"));
        SharedPreferences sharedPreferences= getSharedPreferences("user", Context.MODE_PRIVATE);
        TextView userNameNavTv=headerView.findViewById(R.id.username_nav_tv);
        userNameNavTv.setText(sharedPreferences.getString("userName",""));
        TextView accountNavTv=headerView.findViewById(R.id.account_nav_tv);
        accountNavTv.setText(sharedPreferences.getString("account",""));
    }
    public void mainUIListener(){
        CodeDiaLogFragment codeDiaLogFragment = new CodeDiaLogFragment();
        codeDiaLogFragment.setOnDialogDismissListener(this);
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("Code");
        binding.appMain.navBottomView.getMenu().findItem(R.id.home_it).setCheckable(false);
        binding.appMain.navBottomView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home_it:
                        getSupportFragmentManager().beginTransaction()
                                .replace(binding.appMain.mainPageFragment.getId(),mapUserFragment)
                                .commit();
                        return true;
                    case R.id.code_it:
                        if (fragment == null || !fragment.isAdded()) {
                            item.setCheckable(true);
                            codeDiaLogFragment.show(getSupportFragmentManager(), "Code");
                        }
                        return true;
                    case R.id.user_it:
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(binding.appMain.mainPageFragment.getId(),new UserFragment())
                                .commit();
                        return true;
                }
                return false;
            }
        });
        binding.drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
            }
            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
            }
            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
            }
            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });
    }
    public void drawerLayoutSet(){
        binding.drawerLayout.openDrawer(GravityCompat.START);
    }
    /*public void locationShowInMapListener(MyLocation myLocation){
        FragmentManager fragmentManager = getSupportFragmentManager();
        MapUserFragment mapUserFragment = (MapUserFragment) fragmentManager.findFragmentByTag("MapUserFragment");
        if (mapUserFragment != null) {
            mapUserFragment.LocationShowInMap(myLocation);
        }
    }*/
    public void busShowInMapListener(BusDriver busDriver){
        FragmentManager fragmentManager = getSupportFragmentManager();
        MapUserFragment mapUserFragment = (MapUserFragment) fragmentManager.findFragmentByTag("MapUserFragment");
        if (mapUserFragment != null) {
            mapUserFragment.busShowInMap(busDriver);
        }
    }
    public Bitmap openImage (String path){
        Bitmap bitmap = null;
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(path));
            bitmap = BitmapFactory.decodeStream(bis);
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

}
