package com.example.myapplication.fragment;



import android.content.Context;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.myapplication.databinding.FragmentRouteSearchBinding;
import com.example.myapplication.javabean.MyLocation;

import java.util.Objects;

public class RouteSearchFragment extends Fragment{
    public FragmentRouteSearchBinding binding;
    MapUserFragment mapUserFragment;
    RouteSearchListFragment routeSearchListFragment=new RouteSearchListFragment();
    public RouteSearchFragment(MapUserFragment context,MyLocation searchResLocation){
        this.mapUserFragment=context;
        this.destination=searchResLocation;
        this.hasSelectLocationId=destination.getLocationId();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    View view;
    boolean locationEtType;//true代表当前输入的是userLocation
    boolean isMyLocationSelect=false;
    String hasSelectLocationId;
    MyLocation userLocation;
    MyLocation destination;
    MyLocation startStation;
    String distance;
    public interface OnIsPopBackValueChangedListener {
        void onBooleanValueChanged(boolean newValue);
    }

    private OnIsPopBackValueChangedListener mListener;
    private boolean mBooleanValue;
    // 在Fragment中修改boolean值的方法
    public void setIsPopBackValue(boolean value) {
        mBooleanValue = value;
        if (mListener != null) {
            mListener.onBooleanValueChanged(mBooleanValue);
        }
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (OnIsPopBackValueChangedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnBooleanValueChangedListener");
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentRouteSearchBinding.inflate(getLayoutInflater());
        view=binding.getRoot();
        binding.routesearchDestinationEd.setText(destination.getLocationName());
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // 在FragmentA中处理返回逻辑
                if (getChildFragmentManager().getBackStackEntryCount() > 1) {
                    getChildFragmentManager().popBackStack();
                    setIsPopBackValue(true);
                    setEditViewFocus(true);
                    setEditViewFocus(false);
                    binding.routesearchUserlocationEd.setText(userLocation.getLocationName());
                    binding.routesearchDestinationEd.setText(destination.getLocationName());
                } else {
                    mapUserFragment.requireActivity().onBackPressed();
                }
            }
        });
        if(mapUserFragment.getIsUserAutoLocation().getLocationId()==null){
            userLocation=autoGetMyLocation();
        }
        else {
            userLocation=mapUserFragment.getIsUserAutoLocation();
        }
        replaceBusRouteFragment(destination);
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        locationEtInputListener();
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        mapUserFragment.setIsUserAutoLocation(new MyLocation(null,"我的位置",
                "33.152045","106.632735", null,null,null));
    }
    public void locationEtInputListener(){
        binding.routesearchDestinationEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                locationEtType=false;
                routeSearchListFragment.onLocationChange(String.valueOf(s));
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()==0){
                    routeSearchListFragment.setLocationListEmpty();
                }

            }
        });
        binding.routesearchUserlocationEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                locationEtType=true;
                routeSearchListFragment.onLocationChange(String.valueOf(s));
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()==0){
                    isMyLocationSelect = false;
                    routeSearchListFragment.setLocationListEmpty();
                }

            }
        });
        binding.routesearchUserlocationEd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    isMyLocationSelect = false;
                    getChildFragmentManager()
                            .beginTransaction()
                            .replace(binding.routeSearchPageFragment.getId(), routeSearchListFragment)
                            .addToBackStack("routeSearchListFragment")
                            .commit();
                    setIsPopBackValue(false);

                } else {
                    /*getWindow().getDecorView().getWindowToken()用于获取当前窗口的装饰视图的窗口令牌(WindowToken)。
                    这个窗口令牌用于告诉输入法管理器在哪个窗口中隐藏键盘。
                    0是隐藏虚拟键盘的标志。在这里，0表示不显示任何特殊标志，只是隐藏键盘。*/
                    /*getContext()方法用于获取当前View的上下文(Context)对象。
                    getSystemService(Context.INPUT_METHOD_SERVICE)用于获取系统的输入法管理器(InputMethodManager)实例。
                    通过将其强制转换为InputMethodManager类型，可以使用输入法管理器的方法来控制虚拟键盘。*/
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(binding.routesearchUserlocationEd.getWindowToken(), 0);
                }
            }
        });
        binding.routesearchDestinationEd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    getChildFragmentManager()
                            .beginTransaction()
                            .replace(binding.routeSearchPageFragment.getId(), routeSearchListFragment)
                            .addToBackStack("routeSearchListFragment")
                            .commit();
                    setIsPopBackValue(false);

                }else {
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(binding.routesearchDestinationEd.getWindowToken(), 0);
                }
            }
        });
    }
    public void onDestinationSelect(MyLocation myLocation,boolean locationEtType) {
        Log.e("isTake",myLocation.getIsTake());
        if(locationEtType){
            this.userLocation=myLocation;
            binding.routesearchUserlocationEd.setText(myLocation.getLocationName());//输入框显示修改

        }
        else {
            this.destination=myLocation;
            binding.routesearchDestinationEd.setText(myLocation.getLocationName());
        }
        replaceBusRouteFragment(destination);
    }
    public boolean getLocationEtType(){
        return locationEtType;
    }
    public void setIsSelectBoolean(boolean locationEtType){
        if(locationEtType){
            isMyLocationSelect=true;
            Log.e("replace","Success");
        }

    }
    public MyLocation autoGetMyLocation(){
        return mapUserFragment.getAutoLocation();

    }

    public void replaceBusRouteFragment(MyLocation destination){
        hasSelectLocationId=destination.getLocationId();
        setIsPopBackValue(true);
        //先清空RouteSearchFragment的子fragment的返回栈
        getChildFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        Log.e("length", String.valueOf(getChildFragmentManager().getBackStackEntryCount()));
        if(Objects.equals(destination.getIsTake(), "3")){//destination不为站点
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(binding.routeSearchPageFragment.getId(), new BusRouteFragment(mapUserFragment,userLocation,destination))
                    .addToBackStack("busRouteFragment")
                    .commit();
            Log.e("length", String.valueOf(getChildFragmentManager().getBackStackEntryCount()));
        }
        else {//destination为站点
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(binding.routeSearchPageFragment.getId(), new BusRouteFragmentX(mapUserFragment,userLocation,destination))
                    .addToBackStack("busRouteFragmentX")
                    .commit();
            Log.e("length", String.valueOf(getChildFragmentManager().getBackStackEntryCount()));
        }

    }
    public void setEditViewFocus(boolean locationEtType){
        if(locationEtType){
            binding.routesearchUserlocationEd.clearFocus();
        }
        else {
            binding.routesearchDestinationEd.clearFocus();
        }
    }
    public String getHasSelectLocationId(){
        return hasSelectLocationId;
    }
    public void setIsUserAutoLocation(MyLocation userLocation){
        mapUserFragment.setIsUserAutoLocation(userLocation);
    }
}