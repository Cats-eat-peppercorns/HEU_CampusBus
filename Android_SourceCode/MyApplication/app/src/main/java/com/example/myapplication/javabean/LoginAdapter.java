package com.example.myapplication.javabean;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.myapplication.Activity.LoginActivity;
import com.example.myapplication.fragment.DriverLoginFragment;
import com.example.myapplication.fragment.UserLoginFragment;

public class LoginAdapter extends FragmentStateAdapter {
    private Context context;
    private int totalTabs;

    public LoginAdapter(@NonNull FragmentActivity fragmentActivity, Context context, int totalTabs) {
        super(fragmentActivity);
        this.context = context;
        this.totalTabs = totalTabs;
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new UserLoginFragment((LoginActivity) context);
            case 1:
                return new DriverLoginFragment((LoginActivity) context);
            default:
                return null;
        }
    }
    @Override
    public int getItemCount() {
        return totalTabs;
    }
}
