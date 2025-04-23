package com.example.myapplication.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentLocationSearchBinding;


public class LocationSearchFragment extends Fragment {
    private FragmentLocationSearchBinding binding;
    MapUserFragment mapUserFragment;
    LocationSearchListFragment locationSearchListFragment;
    View view;
    public LocationSearchFragment(MapUserFragment context){
        this.mapUserFragment=context;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=FragmentLocationSearchBinding.inflate(getLayoutInflater());
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = binding.getRoot();
        locationSearchListFragment=new LocationSearchListFragment(this,mapUserFragment);
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.locationList_page_fragment, locationSearchListFragment)
                .commit();
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        binding.locationsearchDestinationEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                locationSearchListFragment.onDestinationChange(String.valueOf(s));
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()==0){
                    //若输入为空，搜索列表置为空
                    locationSearchListFragment.setLocationListEmpty();
                }


            }
        });
    }
}