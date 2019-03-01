package com.example.griefgrocerystore;

import cn.bmob.v3.BmobObject;

public class Letter extends BmobObject {
    private Long Letter_ID;
    private Long User_ID;  // 写这封信的人
    private String Letter_Date;
    private String Letter_Content;
    private Long Letter_Reply;  // 是否为回信，若为回信则填写回的那封信的ID
    private Long Letter_Answer;  // 是否有回信，若有则填写回信的信件ID
    private Boolean Letter_Read;  // 是否被查看
    private Long Letter_contact;  // 信的主人在与谁来往，联系人ID

    public Long getLetter_ID() {
        return Letter_ID;
    }

    public void setLetter_ID(Long mLetter_ID) {
        Letter_ID = mLetter_ID;
    }

    public Long getUser_ID() {
        return User_ID;
    }

    public void setUser_ID(Long mUser_ID) {
        User_ID = mUser_ID;
    }

    public String getLetter_Date() {
        return Letter_Date;
    }

    public void setLetter_Date(String mLetter_Date) {
        Letter_Date = mLetter_Date;
    }

    public String getLetter_Content() {
        return Letter_Content;
    }

    public void setLetter_Content(String mLetter_Content) {
        Letter_Content = mLetter_Content;
    }


    public Long getLetter_Reply() {
        return Letter_Reply;
    }

    public void setLetter_Reply(Long mLetter_Reply) {
        Letter_Reply = mLetter_Reply;
    }

    public Long getLetter_Answer() {
        return Letter_Answer;
    }

    public void setLetter_Answer(Long mLetter_Answer) {
        Letter_Answer = mLetter_Answer;
    }

    public Boolean getLetter_Read() {
        return Letter_Read;
    }

    public void setLetter_Read(Boolean mLetter_Read) {
        Letter_Read = mLetter_Read;
    }

    public Long getLetter_contact() {
        return Letter_contact;
    }

    public void setLetter_contact(Long mLetter_contact) {
        Letter_contact = mLetter_contact;
    }

}
