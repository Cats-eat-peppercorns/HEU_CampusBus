package com.example.myapplication.javabean;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class LocationDBOpenHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "Location.db";//数据库命名
    public LocationDBOpenHelper(@Nullable Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
    /*public long addLocation(Location location) {
        SQLiteDatabase db = getWritableDatabase();//getWritableDatabase()方法创建或打开可以读/写的数据库
        ContentValues contentValues = new ContentValues();//ContentValues()方法可以操作数据库
        contentValues.put("locationName", location.getLocationName());//注册时将getAccount()的值加入account一项
        contentValues.put("password", location.getPassword());//注册时将getPassword()的值加入password一项
        long location = db.insert("users", null, contentValues);
        return location;
    }*/
    public void databaseOpen(){
        SQLiteDatabase db = getWritableDatabase();
    }
    /*public boolean login(String account, String password) {
        SQLiteDatabase db1 = getWritableDatabase();
        boolean result = false;
        Cursor users = db1.query("users", null, "account like?", new String[]{account}, null, null, null);
        if (users != null) {
            while (users.moveToNext()) {
                String password1 = users.getString(1);
                result = password1.equals(password);
                return result;
            }
        }
        return false;
    }*/
}
