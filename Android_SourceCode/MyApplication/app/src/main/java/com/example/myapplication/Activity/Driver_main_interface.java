package com.example.myapplication.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapplication.R;
import com.example.myapplication.fragment.Checking_In_Fragment;
import com.example.myapplication.fragment.Map_Driver_Fragment;
import com.example.myapplication.fragment.User_Driver_Fragment;

public class Driver_main_interface extends AppCompatActivity {

    private ImageView Map_imageview;
    private ImageView Checking_in_imageview;
    private ImageView User_imageview;

    private boolean[] navigationState = new boolean[3];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_main_interface);
        Map_imageview = findViewById(R.id.map_imageview); //地图按钮
        Checking_in_imageview = findViewById(R.id.buttonA2);
        User_imageview = findViewById(R.id.user_imageview);
        init_fragment();
        onResume();
    }

    private void init_fragment() { //初始化三个界面，下面用来显示或隐藏
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setReorderingAllowed(true);
        Checking_In_Fragment CIF=new Checking_In_Fragment();
        transaction.add(R.id.home_container, CIF, "出勤");
        User_Driver_Fragment UDF=new User_Driver_Fragment();
        transaction.add(R.id.home_container, UDF, "用户");
        Map_Driver_Fragment MDF=new Map_Driver_Fragment();
        transaction.add(R.id.home_container, MDF, "地图");
        transaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Start_map();
        Map_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Start_map();
            }
        });
        Checking_in_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Bundle bundle =new Bundle();
//
//                Checking_In_Fragment CIF=new Checking_In_Fragment();
//                CIF.setArguments(bundle);
                Replace_Fragment(2);
                if (!navigationState[1]) {
                    Map_imageview.setImageResource(R.drawable.navigation1a);
                    Checking_in_imageview.setImageResource(R.drawable.clocking_in);
                    User_imageview.setImageResource(R.drawable.navigation3a);
                    navigationState[0] = false;
                    navigationState[1] = true;
                    navigationState[2] = false;
                }
            }
        });
        User_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Bundle bundle = new Bundle();
//
//                User_Driver_Fragment UDF=new User_Driver_Fragment();
//                UDF.setArguments(bundle);
                Replace_Fragment(3);
                if (!navigationState[2]) {
                    Map_imageview.setImageResource(R.drawable.navigation1a);
                    Checking_in_imageview.setImageResource(R.drawable.clocking_in2);
                    User_imageview.setImageResource(R.drawable.navigation3);
                    navigationState[0] = false;
                    navigationState[1] = false;
                    navigationState[2] = true;
                }
            }
        });
    }

    private void Replace_Fragment(int key) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment User_Driver_Fragment=fragmentManager.findFragmentByTag("用户");
        Fragment Checking_In_Fragment=fragmentManager.findFragmentByTag("出勤");
        Fragment Map_Driver_Fragment=fragmentManager.findFragmentByTag("地图");

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setReorderingAllowed(true);
        if(Map_Driver_Fragment.isAdded()&&Checking_In_Fragment.isAdded()&&User_Driver_Fragment.isAdded())
        {
            if(key==1)
            {
                transaction.show(Map_Driver_Fragment).hide(Checking_In_Fragment).hide(User_Driver_Fragment);
            }else if(key==2)
            {
                transaction.show(Checking_In_Fragment).hide(Map_Driver_Fragment).hide(User_Driver_Fragment);
            }else if(key==3)
            {
                transaction.show(User_Driver_Fragment).hide(Map_Driver_Fragment).hide(Checking_In_Fragment);
            }
        }
        //transaction.replace(R.id.home_container,fragment);
        transaction.commit();
    }

    private void Start_map(){  //方便开始时直接进入地图界面
//        Bundle bundle =new Bundle(); //可以用bundle来向fragment中传参数 下面同理
//        Map_Driver_Fragment MDF=new Map_Driver_Fragment();
//        MDF.setArguments(bundle);
        Replace_Fragment(1);
        if (!navigationState[0]) {
            Map_imageview.setImageResource(R.drawable.navigation1);
            Checking_in_imageview.setImageResource(R.drawable.clocking_in2);
            User_imageview.setImageResource(R.drawable.navigation3a);
            navigationState[0] = true;
            navigationState[1] = false;
            navigationState[2] = false;
        }
    }

    public void recreateMap(){  //重绘地图
        Fragment MDF = getSupportFragmentManager().findFragmentByTag("地图");
        if(MDF!=null){
            Map_Driver_Fragment M_D_F = new Map_Driver_Fragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.remove(MDF);
            //Map_Driver_Fragment M_D_F = new Map_Driver_Fragment();
            fragmentTransaction.add(R.id.home_container, M_D_F, "地图");
            fragmentTransaction.hide(M_D_F);
            fragmentTransaction.commit();
        }
    }

}