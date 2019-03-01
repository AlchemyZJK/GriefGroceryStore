package com.example.griefgrocerystore;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CountListener;


public class FirstPageFragment extends Fragment {
    private String userName, userSex;
    private Long userId;

    private TextView unreadNum;

    public FirstPageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_first_page, container, false);

        // 初始化Bmob
        Bmob.initialize(getContext(), "aa37bf8d62705b5775afc5cc5d4be1e2");

        // 从MenuActivity中获取用户信息
        TextView userNameTextView = view.findViewById(R.id.firstpage_user_name);
        if(isAdded()) {
            assert getArguments() != null;
            userName = getArguments().getString("UserName", "陌生人");
            userId = getArguments().getLong("UserID");
            userSex = getArguments().getString("UserSex");
        }
        userNameTextView.setText(userName);

        unreadNum = view.findViewById(R.id.text_unread_letter_number);
        unreadNum.setText("0");

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Button button = Objects.requireNonNull(getActivity()).findViewById(R.id.button_go_mailbox);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转UserFragment
                MenuActivity menuActivity = (MenuActivity) getActivity();
                menuActivity.goToUser();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        getUnreadLetterNum();  // 获取写给我的没有阅读的信件数量
    }

    // 获取写给我的没有阅读的信的数量
    private void getUnreadLetterNum() {
        BmobQuery<Letter> eq1 = new BmobQuery<>();
        eq1.addWhereEqualTo("Letter_contact", userId);
        BmobQuery<Letter> eq2 = new BmobQuery<>();
        eq2.addWhereEqualTo("Letter_Read", false);
        // 封装与条件
        List<BmobQuery<Letter>> andQueries = new ArrayList<>();
        andQueries.add(eq1);
        andQueries.add(eq2);
        // 查询
        BmobQuery<Letter> query = new BmobQuery<>();
        query.and(andQueries);
        query.count(Letter.class, new CountListener() {
            @Override
            public void done(Integer integer, BmobException e) {
                if(e == null) {
                    String unread = integer.toString();
                    unreadNum.setText(unread);
                }
                else {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}