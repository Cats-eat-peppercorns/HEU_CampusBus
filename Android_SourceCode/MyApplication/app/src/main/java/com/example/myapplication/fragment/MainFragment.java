package com.example.myapplication.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentMainBinding;

public class MainFragment extends Fragment {
    private FragmentMainBinding binding;
    MapUserFragment mapUserFragment;
    public MainFragment(MapUserFragment context){
        this.mapUserFragment=context;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=FragmentMainBinding.inflate(getLayoutInflater());
    }
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=binding.getRoot();
        binding.searchBusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapUserFragment.setBottomSheetBehavior(2);
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_bs_page_fragment, new LocationSearchFragment(mapUserFragment))
                        .addToBackStack("locationSearchFragment")
                        .commit();

            }
        });
        return view;
    }
}