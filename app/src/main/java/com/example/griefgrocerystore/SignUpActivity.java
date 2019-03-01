package com.example.griefgrocerystore;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class SignUpActivity extends AppCompatActivity {
    private Button mSignUpButton;
    private EditText mEditTextMailAddress, mEditTextPenName, mEditTextPassword, mEditTextPasswordCheck;
    private RadioGroup mRadioGroupSex;
    private String mailAddress, penName, password, passwordCheck, sex;
    private Long userId;
    private ImageView userImageMale, userImageFemale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // 初始化Bmob
        Bmob.initialize(this, "aa37bf8d62705b5775afc5cc5d4be1e2");

        mSignUpButton = findViewById(R.id.button_sign_up_back);
        mEditTextMailAddress = findViewById(R.id.editText_mail_address_up);
        mEditTextPenName = findViewById(R.id.editText_pen_name_up);
        mEditTextPassword = findViewById(R.id.editText_password_up);
        mEditTextPasswordCheck = findViewById(R.id.editText_password_check_up);
        mRadioGroupSex = findViewById(R.id.radioGroup_sex);
        userImageMale = findViewById(R.id.user_sign_up_image_male);
        userImageFemale = findViewById(R.id.user_sign_up_image_female);

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mailAddress = mEditTextMailAddress.getText().toString();
                penName = mEditTextPenName.getText().toString();
                password = mEditTextPassword.getText().toString();
                passwordCheck = mEditTextPasswordCheck.getText().toString();
                if(check()) {
                    checkMailAddress();
                }
            }
        });
        mRadioGroupSex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioButton_male:
                        sex = "m";
                        userImageMale.setVisibility(View.VISIBLE);
                        userImageFemale.setVisibility(View.INVISIBLE);
                        break;
                    case R.id.radioButton_female:
                        sex = "f";
                        userImageMale.setVisibility(View.INVISIBLE);
                        userImageFemale.setVisibility(View.VISIBLE);
                        break;
                        default:
                            sex = "N/A";
                            break;
                }
                //Toast.makeText(getApplicationContext(), sex, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 检查输入是否为空并检查两次密码输入是否一致
    private boolean check() {
        if(mailAddress.equals("")) {
            Toast.makeText(getApplicationContext(), "请输入邮箱地址", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(penName.equals("")) {
            Toast.makeText(getApplicationContext(), "请输入笔名", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(password.equals("")) {
            Toast.makeText(getApplicationContext(), "请输入密码", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(passwordCheck.equals("")) {
            Toast.makeText(getApplicationContext(), "请校验密码", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!password.equals(passwordCheck)) {
            mEditTextPassword.setText("");
            mEditTextPasswordCheck.setText("");
            Toast.makeText(getApplicationContext(), "两次密码输入不一致", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // 检验邮箱是否已注册
    private void checkMailAddress() {
        BmobQuery<User> query = new BmobQuery<>();
        query.addWhereEqualTo("User_Mail", mailAddress);
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if(e == null) {
                    if(list == null || list.size() == 0) {
                        // 若邮箱未注册则创建新用户
                        createUser();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "该邮箱已注册", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(SignUpActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // 在数据库中创建新用户
    private void createUser() {
        // 获取数据库中最大的userId
        BmobQuery<User> query = new BmobQuery<>();
        query.order("-User_ID");
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if(e == null) {
                    // 新用户userId为数据库中最大userId + 1
                    userId = list.get(0).getUser_ID() + 1;
                    // 往数据库中的User表中插入新用户
                    User newUser = new User();
                    newUser.setUser_ID(userId);
                    newUser.setUser_Name(penName);
                    newUser.setUser_Mail(mailAddress);
                    newUser.setUser_Pw(password);
                    newUser.setUser_Sex(sex);
                    newUser.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if(e == null) {
                                Toast.makeText(SignUpActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                                // 添加新用户成功跳转登陆界面
                                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                                startActivity(intent);
                            }
                            else {
                                Toast.makeText(SignUpActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(SignUpActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
