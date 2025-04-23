package com.example.myapplication.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.myapplication.Activity.LoginActivity;
import com.example.myapplication.Activity.UserRegisterActivity;
import com.example.myapplication.databinding.FragmentUserLoginBinding;

public class UserLoginFragment extends Fragment {
    private FragmentUserLoginBinding binding;
    LoginActivity loginActivity;
    public UserLoginFragment(LoginActivity loginActivity) {
        // Required empty public constructor
        this.loginActivity = loginActivity;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentUserLoginBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }
    @Override
    public void onResume(){
        super.onResume();
        loginInputListener();
        loginToMainActivity();
        register();
    }
    public void loginInputListener() {
        binding.passwordEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        binding.passwordIsVisibleIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setSelected(!view.isSelected());
                if ((binding.passwordEt.getInputType() & InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) > 0) {
                    // 如果当前是可见的密码文本，切换为普通文本
                    binding.passwordEt.setInputType(InputType.TYPE_CLASS_TEXT);
                } else {
                    // 如果当前是普通文本，切换为密码文本
                    binding.passwordEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                //光标移动到文本末尾
                binding.passwordEt.setSelection(binding.passwordEt.getText().length());
            }
        });
        //删除键根据账号输入与否显示
        binding.accountEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty())
                    //当更变后的文本长度不为空时，删除键可见
                    binding.accountDeleteIv.setVisibility(View.VISIBLE);
                else
                    //否则不可见
                    binding.accountDeleteIv.setVisibility(View.INVISIBLE);
            }
        });
        //删除整行账号输入
        binding.accountDeleteIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.accountEt.setText("");//内容设为空达到删除效果
            }
        });
    }

    public void loginToMainActivity() {
        binding.loginBtn.setOnClickListener(view -> {
            if (binding.accountEt.getText().length() == 0)//账号输入为空
            {
                Toast.makeText(requireContext(), "请输入账号", Toast.LENGTH_SHORT).show();
            } else if (binding.accountEt.getText().length() != 0 && binding.passwordEt.getText().length() == 0)//密码输入为空
            {
                Toast.makeText(requireContext(), "请输入密码", Toast.LENGTH_SHORT).show();
            } else if (binding.accountEt.getText().length() != 0 && binding.passwordEt.getText().length() != 0) {
                //输入格式没有问题
                String accountLogin = binding.accountEt.getText().toString();
                String passwordLogin = binding.passwordEt.getText().toString();
                loginActivity.userLoginRequest(accountLogin, passwordLogin);

            }
        });
    }
    public void register() {
        binding.registerGoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(requireContext(), UserRegisterActivity.class));

            }
        });
    }
}