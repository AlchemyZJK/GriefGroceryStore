package com.example.griefgrocerystore;

import cn.bmob.v3.BmobObject;

public class User extends BmobObject {
    private Long User_ID;
    private String User_Name;
    private String User_Mail;
    private String User_Pw;
    private String User_Sex;

    public Long getUser_ID() {
        return User_ID;
    }

    public void setUser_ID(Long mUser_Id) {
        User_ID = mUser_Id;
    }

    public String getUser_Name() {
        return User_Name;
    }

    public void setUser_Name(String mUser_Name) {
        User_Name = mUser_Name;
    }

    public String getUser_Mail() {
        return User_Mail;
    }

    public void setUser_Mail(String mUser_Mail) {
        User_Mail = mUser_Mail;
    }

    public String getUser_Pw() {
        return User_Pw;
    }

    public void setUser_Pw(String mUser_Pw) {
        User_Pw = mUser_Pw;
    }

    public String getUser_Sex() {
        return User_Sex;
    }

    public void setUser_Sex(String mUser_Sex) {
        User_Sex = mUser_Sex;
    }

}
