package com.example.myapplication.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.Activity.Driver_information_set;
import com.example.myapplication.Activity.Driver_main_interface;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link User_Driver_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class User_Driver_Fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View rootView;
    private TextView Driver_name_text,Driver_account_text,Driver_school_text,Driver_line_text;

    private CheckBox Show_map_text,Show_map_build;
    private Button Set_map;
    private LinearLayout Change_self_information;
    public User_Driver_Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment User_Driver_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static User_Driver_Fragment newInstance(String param1, String param2) {
        User_Driver_Fragment fragment = new User_Driver_Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.e("TAG?!?!?!?!", "dty");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(rootView==null)
        {
            rootView=inflater.inflate(R.layout.fragment_user__driver_, container, false);
        }
        //Log.e("TAG?!?!?!?!", "run: ??!?!?!??!?!?");
        Change_self_information=rootView.findViewById(R.id.change_self_information);
        Driver_name_text = rootView.findViewById(R.id.driver_name_text);
        Driver_account_text = rootView.findViewById(R.id.driver_account_text);
        Driver_school_text = rootView.findViewById(R.id.driver_school_text);
        Driver_line_text = rootView.findViewById(R.id.driver_line_text);
        Show_map_text = rootView.findViewById(R.id.show_map_text);
        Show_map_build = rootView.findViewById(R.id.show_map_build);
        Set_map = rootView.findViewById(R.id.set_map);
        SharedPreferences sharedCheckbox= requireActivity().getSharedPreferences("user_checkBox", Context.MODE_PRIVATE);
        Show_map_text.setChecked(sharedCheckbox.getBoolean("Show_map_text", false));
        Show_map_build.setChecked(sharedCheckbox.getBoolean("Show_map_build", false));

        SharedPreferences sharedPreferences= requireActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        Driver_name_text.setText(sharedPreferences.getString("driver_name",""));
        Driver_account_text.setText(sharedPreferences.getString("driver_account",""));
        Driver_school_text.setText(sharedPreferences.getString("driver_school",""));
        Driver_line_text.setText(sharedPreferences.getString("driver_line",""));
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        /***选项框***/
        SharedPreferences sharedCheckbox= requireActivity().getSharedPreferences("user_checkBox", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedCheckbox.edit();
        Show_map_text.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                editor.putBoolean("Show_map_text", b);
                editor.apply();
            }
        });
        Show_map_build.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                editor.putBoolean("Show_map_build", b);
                editor.apply();
            }
        });
        Set_map.setOnClickListener(new View.OnClickListener() { //地图设置
            @Override
            public void onClick(View view) {
                ((Driver_main_interface)requireActivity()).recreateMap();
            }
        });
        Change_self_information.setOnClickListener(new View.OnClickListener() {  //个人信息设置
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(requireContext(), Driver_information_set.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onDestroy() {
        //Log.e("TAG?!?!?!?!", "dty");
        super.onDestroy();
    }
}