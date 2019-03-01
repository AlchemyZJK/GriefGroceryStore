package com.example.griefgrocerystore;

import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class MenuActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {
    private RadioGroup radioGroupMenu;
    private RadioButton radioButtonFirstPage, radioButtonUser, radioButtonMailbox;
    private ImageButton imageButtonWrite;

    private FirstPageFragment firstPage;
    private UserFragment user;
    private MailboxFragment mailbox;
    private FragmentManager mFragmentManager;

    private String userName, userSex;
    private Long userId;
    private Bundle mBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // 从SignInActivity中获取username
        Bundle bundleFromSign = getIntent().getExtras();
        assert bundleFromSign != null;
        userId = bundleFromSign.getLong("userId");
        userName = bundleFromSign.getString("userName");
        userSex = bundleFromSign.getString("userSex");

        // 装备bundle
        mBundle = new Bundle();
        mBundle.putString("UserName", userName);
        mBundle.putLong("UserID", userId);
        mBundle.putString("UserSex", userSex);

        mFragmentManager = getSupportFragmentManager();

        imageButtonWrite = findViewById(R.id.button_write);
        imageButtonWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 传递数据给跳转WriteLetterActivity
                Intent mIntent = new Intent(MenuActivity.this, WriteLetterActivity.class);
                Bundle toWriteLetterActivity = new Bundle();
                toWriteLetterActivity.putString("Flag", "ToStore");  // 给杂货店寄信
                toWriteLetterActivity.putString("ObjectID", "None");
                toWriteLetterActivity.putLong("SenderID", userId);
                toWriteLetterActivity.putLong("LetterID", 0);
                toWriteLetterActivity.putLong("ReceiverID", 0);
                mIntent.putExtras(toWriteLetterActivity);
                startActivity(mIntent);
            }
        });

        radioGroupMenu = findViewById(R.id.radioGroup_menu);
        radioGroupMenu.setOnCheckedChangeListener(this);

        // 设置First Page为默认选中
        radioButtonFirstPage = findViewById(R.id.radioButton_firstpage);
        radioButtonFirstPage.setChecked(true);
        firstPage = new FirstPageFragment();
        firstPage.setArguments(mBundle);  // 向FirstPageFragment传递信息
        radioButtonUser = findViewById(R.id.radioButton_user);
        radioButtonMailbox = findViewById(R.id.radioButton_mailbox);

        setSelfImageSize(radioButtonFirstPage);
        setSelfImageSize(radioButtonUser);
        setSelfImageSize(radioButtonMailbox);

        // get Intent from ShowLetterActivity
        Intent intent = getIntent();
        int id = intent.getIntExtra("fragment", 1);
        if(id == 3) {
            goToUser();
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
        hideAllFragment(mFragmentTransaction);
        switch (checkedId) {
            case R.id.radioButton_firstpage:
                if(firstPage == null) {
                    firstPage = new FirstPageFragment();
                    // 向FirstPageFragment传递信息
                    firstPage.setArguments(mBundle);
                    mFragmentTransaction.add(R.id.frame_layout_content, firstPage);
                }
                else {
                    mFragmentTransaction.show(firstPage);
                }
                break;
            case R.id.radioButton_user:
                if(user == null) {
                    user = new UserFragment();
                    // 向UserFragment传递信息
                    user.setArguments(mBundle);
                    mFragmentTransaction.add(R.id.frame_layout_content, user);
                }
                else {
                    mFragmentTransaction.show(user);
                }
                break;
            case R.id.radioButton_mailbox:
                if(mailbox == null) {
                    mailbox = new MailboxFragment();
                    // 向MailBoxFragment传递信息
                    mailbox.setArguments(mBundle);
                    mFragmentTransaction.add(R.id.frame_layout_content, mailbox);
                }
                else {
                    mFragmentTransaction.show(mailbox);
                }
                break;
        }
        mFragmentTransaction.commit();
    }

    // 调整ImageButton中图片的大小
    private void setSelfImageSize(@NonNull RadioButton mRadioButton) {
        Drawable[] drawables = mRadioButton.getCompoundDrawables();
        Rect r = new Rect(0, 0, drawables[1].getMinimumWidth()/8, drawables[1].getMinimumHeight()/8);
        drawables[1].setBounds(r);
        mRadioButton.setCompoundDrawables(null,drawables[1],null,null);
    }

    // 隐藏所有Fragment
    private void hideAllFragment(FragmentTransaction fragmentTransaction) {
        if(firstPage != null)
            fragmentTransaction.hide(firstPage);
        if(user != null)
            fragmentTransaction.hide(user);
        if(mailbox != null)
            fragmentTransaction.hide(mailbox);
    }

    // FirstPageFragment中点击信箱跳转MailboxFragment事件
    public void goToMailbox() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        if(mailbox == null) {
            mailbox = new MailboxFragment();
            mailbox.setArguments(mBundle);
            transaction.add(R.id.frame_layout_content, mailbox);
        }
        else {
            transaction.show(mailbox);
        }
        transaction.commit();
        radioButtonMailbox.setChecked(true);
    }

    // UserFragment中点击某封信查看内容后点击返回跳转UserFragment事件
    public void goToUser() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        if(user == null) {
            user = new UserFragment();
            user.setArguments(mBundle);
            transaction.add(R.id.frame_layout_content, user);
        }
        else {
            transaction.show(user);
        }
        transaction.commit();
        radioButtonUser.setChecked(true);
    }

}
