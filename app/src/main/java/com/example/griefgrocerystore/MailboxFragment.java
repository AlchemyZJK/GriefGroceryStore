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
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class MailboxFragment extends Fragment {
    private Letter letter;
    private ListView mListView;
    private LetterAdapter mLetterAdapter;
    private LinkedList<Letter> mData;

    private Long userId;

    public MailboxFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null) {
            userId = savedInstanceState.getLong("UserID");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mailbox, container, false);

        // 初始化Bmob
        Bmob.initialize(getContext(), "aa37bf8d62705b5775afc5cc5d4be1e2");

        // 从MenuActivity获取用户信息
        if(isAdded() && getArguments() != null) {
            userId = getArguments().getLong("UserID");
        }

        // get the listView
        mListView = view.findViewById(R.id.list_mailbox);
        mData = new LinkedList<>();
        mLetterAdapter = new LetterAdapter(mData, getContext());
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
                            Log.i("Update_Status", "done: update the letter read status(MailboxFragment).");
                        }
                        else {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                // 传递数据给ShowLetterActivity
                Intent mIntent = new Intent(getActivity(), ShowLetterActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("Flag", "FromMailbox");
                bundle.putString("ObjectID", clickedLetter.getObjectId());
                bundle.putString("LetterContent", clickedLetter.getLetter_Content());
                bundle.putLong("SenderID", userId);
                bundle.putLong("LetterID", clickedLetter.getLetter_ID());
                bundle.putLong("ReceiverID", clickedLetter.getUser_ID());
                mIntent.putExtras(bundle);
                startActivity(mIntent);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getLetter();  // 获取寄给杂货店的信件（前10封）
    }

    // 获取寄给杂货店的信件（前10封）
    private void getLetter() {
        // 装备查询条件
        BmobQuery<Letter> eq1 = new BmobQuery<>();
        eq1.addWhereEqualTo("Letter_Reply", 0);  // 这封信不是回信
        BmobQuery<Letter> eq2 = new BmobQuery<>();
        eq2.addWhereEqualTo("Letter_Answer", 0);  // 这封信没有回信
        BmobQuery<Letter> eq4 = new BmobQuery<>();
        eq4.addWhereEqualTo("Letter_contact", 0);  // 这封信未与他人建立联系

        final List<BmobQuery<Letter>> queries = new ArrayList<>();
        queries.add(eq1);
        queries.add(eq2);
        queries.add(eq4);

        // 查询
        BmobQuery<Letter> query = new BmobQuery<>();
        query.and(queries);
        query.findObjects(new FindListener<Letter>() {
            @Override
            public void done(List<Letter> list, BmobException e) {
                if(e == null) {
                    for(int i = 0; i < list.size(); i++) {
                        if(i >= 10)
                            break;
                        letter = list.get(i);
                        mData.add(letter);
                        mLetterAdapter.notifyDataSetChanged();  // 时时更新
                    }
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
    }
}
