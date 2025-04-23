package com.example.myapplication.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.myapplication.R;

public class PersonaInformationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_information);
        /*ImageView avatar = findViewById(R.id.avatar_show_image);*/
        /*Intent intent=getIntent();
        if(intent!=null)
        {
            Bitmap bitmap=intent.getParcelableExtra("bitmap");
            avatar.setImageBitmap(bitmap);
        }*/
        
    }

}