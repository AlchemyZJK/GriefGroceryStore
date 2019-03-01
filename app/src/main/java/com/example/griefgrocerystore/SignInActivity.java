package com.example.griefgrocerystore;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class SignInActivity extends AppCompatActivity {
    private EditText mEditTextMailAddress, mEditTextPassword;
    private String mailAddress, password, userName, userSex;
    private boolean check;
    private Long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // 初始化Bmob
        Bmob.initialize(this, "aa37bf8d62705b5775afc5cc5d4be1e2");

        Button signInButton = findViewById(R.id.button_sign_in);
        Button signUpButton = findViewById(R.id.button_sign_up);
        mEditTextMailAddress = findViewById(R.id.editText_mail_address_in);
        mEditTextPassword = findViewById(R.id.editText_password_in);
        check = false;

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getText();
                checkPassword();
            }
        });
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转注册界面
                Intent mIntent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(mIntent);
            }
        });
    }

    // 获取输入的邮箱和密码
    private void getText() {
        mailAddress = mEditTextMailAddress.getText().toString();
        password = mEditTextPassword.getText().toString();
    }

    private void checkPassword() {
        BmobQuery<User> query = new BmobQuery<>();
        query.addWhereEqualTo("User_Mail", mailAddress);
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if(e == null) {
                    if(list == null || list.size() == 0) {
                        // 邮箱地址不存在
                        Toast.makeText(SignInActivity.this, "该邮箱未注册", Toast.LENGTH_SHORT).show();
                        check = false;
                    }
                    else {
                        // 邮箱地址存在
                        for (int i = 0; i < list.size(); i++) {
                            User user = list.get(i);
                            if (user.getUser_Pw().equals(password)) {
                                Toast.makeText(SignInActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                                check = true;
                                userId = user.getUser_ID();
                                userName = user.getUser_Name();
                                userSex = user.getUser_Sex();
                                break;
                            }
                        }
                        if (check) {
                            // 向MenuActivity传送userName, userId和userSex
                            Intent mIntent = new Intent(SignInActivity.this, MenuActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putLong("userId", userId);
                            bundle.putString("userName", userName);
                            bundle.putString("userSex", userSex);
                            mIntent.putExtras(bundle);
                            startActivity(mIntent);
                        } else {
                            mEditTextPassword.setText("");
                            Toast.makeText(SignInActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else {
                    Toast.makeText(SignInActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    check = false;
                }
            }
        });
    }
}
