package com.example.griefgrocerystore;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class UserFragment extends Fragment {
    private LetterAdapter mLetterAdapter;
    private LinkedList<Letter> mData;

    private Long userId;
    private String userSex;

    private RadioGroup userChoice;
    private RadioButton userChoiceMy, userChoiceOthers;

    public UserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null) {
            userId = savedInstanceState.getLong("UserID");
            userSex = savedInstanceState.getString("UserSex");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        // 从MenuActivity获取用户信息
        if(isAdded() && getArguments() != null) {
            userId = getArguments().getLong("UserID");
            userSex = getArguments().getString("UserSex");
        }

        // 初始化Bmob
        Bmob.initialize(getContext(), "aa37bf8d62705b5775afc5cc5d4be1e2");

        // 显示用户头像
        ImageView userImgMale = view.findViewById(R.id.user_image_male);
        ImageView userImgFemale = view.findViewById(R.id.user_image_female);
        if(userSex.equals("m")) {
            userImgMale.setVisibility(View.VISIBLE);
            userImgFemale.setVisibility(View.INVISIBLE);
        }
        else {
            userImgMale.setVisibility(View.INVISIBLE);
            userImgFemale.setVisibility(View.VISIBLE);
        }

        userChoice = view.findViewById(R.id.user_choice_radioGroup);
        userChoiceMy = view.findViewById(R.id.choice_my_letter);
        userChoiceOthers = view.findViewById(R.id.choice_others_letter);
        userChoice.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.choice_my_letter)
                    getLetterMy();  // 我写的信
                else
                    getLetterOthers();  // 别人写给我的信
            }
        });

        // get the listView
        ListView mListView = view.findViewById(R.id.list_user);
        mData = new LinkedList<>();
        mLetterAdapter = new LetterAdapter(mData, getActivity());
        mListView.setAdapter(mLetterAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 获取选中item的对象
                Letter clickedLetter = mData.get(position);

                // 标记该信已读
                clickedLetter.setLetter_Read(true);
                Letter updateLetter = new Letter();
                updateLetter.setLetter_Read(true);
                updateLetter.update(clickedLetter.getObjectId(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if(e == null) {
                            Log.i("Update_Status", "done: update the letter read status(UserFragment).");
                        }
                        else {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                // 传递数据给ShowLetterActivity
                Intent mIntent = new Intent(getActivity(), ShowLetterActivity.class);
                Bundle bundle = new Bundle();
                if(userChoiceMy.isChecked()) {
                    bundle.putString("Flag", "FromUserMy");
                }
                else {
                    bundle.putString("Flag", "FromUserOthers");
                }
                bundle.putString("LetterContent", clickedLetter.getLetter_Content());
                bundle.putString("ObjectID", clickedLetter.getObjectId());
                bundle.putLong("SenderID", userId);
                bundle.putLong("LetterID", clickedLetter.getLetter_ID());
                if(userChoiceMy.isChecked()) {
                    bundle.putLong("ReceiverID", clickedLetter.getLetter_contact());
                }
                else {
                    bundle.putLong("ReceiverID", clickedLetter.getUser_ID());
                }
                mIntent.putExtras(bundle);
                startActivity(mIntent);
            }
        });

        userChoiceMy.setChecked(true);  // 设置“我的”预选中

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    // 查询我写的信
    private void getLetterMy() {
        mData.clear();
        BmobQuery<Letter> query = new BmobQuery<>();
        query.addWhereEqualTo("User_ID", userId);
        query.findObjects(new FindListener<Letter>() {
            @Override
            public void done(List<Letter> list, BmobException e) {
                if(e == null) {
                    mData.addAll(list);
                    mLetterAdapter.notifyDataSetChanged();
                }
                else {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // 查询写给我的信
    private void getLetterOthers() {
        mData.clear();
        BmobQuery<Letter> query = new BmobQuery<>();
        query.addWhereEqualTo("Letter_contact", userId);
        query.findObjects(new FindListener<Letter>() {
            @Override
            public void done(List<Letter> list, BmobException e) {
                if(e == null) {
                    mData.addAll(list);
                    mLetterAdapter.notifyDataSetChanged();
                }
                else {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putLong("UserID", userId);
        savedInstanceState.putString("UserSex", userSex);
    }
}
