package com.example.myapplication.javabean;

import android.graphics.drawable.Drawable;

public class User {
    private String account;
    private String password;
    public User(Drawable avatar,String account,String password){
        this.account=account;
        this.password=password;
    }
    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }

}
