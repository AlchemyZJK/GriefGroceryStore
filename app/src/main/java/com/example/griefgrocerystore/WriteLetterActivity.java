package com.example.griefgrocerystore;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class WriteLetterActivity extends AppCompatActivity {
    private ImageButton imageButtonBack, imageButtonSend;
    private EditText editTextContent;
    private String writeContent;
    private String date;

    private String flag, objectId;
    private Long senderId, letterId, receiverId, maxLetterId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_letter);

        // 获取从MenuActivity、MailboxFragment和UserFragment传递的数据
        Intent mIntent = getIntent();
        Bundle mBundle = mIntent.getExtras();
        assert mBundle != null;
        flag = mBundle.getString("Flag");
        objectId = mBundle.getString("ObjectID");
        senderId = mBundle.getLong("SenderID");
        letterId = mBundle.getLong("LetterID");
        receiverId = mBundle.getLong("ReceiverID");

        // 获取当前日期
        Calendar today = Calendar.getInstance();
        int year = today.get(Calendar.YEAR);
        int month = today.get(Calendar.MONTH) + 1;  // 注意此处月份从0开始！！！
        int day = today.get(Calendar.DATE);
        date = year + "-" + month + "-" + day;

        editTextContent = findViewById(R.id.editText_write_content);

        imageButtonBack = findViewById(R.id.button_write_back);
        imageButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(WriteLetterActivity.this, MenuActivity.class);
                startActivity(mIntent);
            }
        });

        imageButtonSend = findViewById(R.id.button_write_send);
        imageButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (flag) {
                    case "ToStore":
                        SendToStore();
                        break;
                    case "FromMailbox":
                        SendFromMailbox();
                        break;
                    case "FromUserMy":
                        SendFromUserMy();
                        break;
                    case "FromUserOthers":
                        SendFromUserOthers();
                        break;
                }
            }
        });
    }

    // 处理写信给杂货店的事件
    private void SendToStore() {
        writeContent = editTextContent.getText().toString();

        // 查询数据库中目前最大的Letter_ID
        BmobQuery<Letter> maxLetterIdQuery = new BmobQuery<>();
        maxLetterIdQuery.order("-Letter_ID");
        maxLetterIdQuery.findObjects(new FindListener<Letter>() {
            @Override
            public void done(List<Letter> list, BmobException e) {
                if(e == null) {
                    maxLetterId = list.get(0).getLetter_ID();

                    // 装备信件
                    Letter letter = new Letter();
                    letter.setLetter_ID(maxLetterId + 1);
                    letter.setUser_ID(senderId);
                    letter.setLetter_Date(date);
                    letter.setLetter_Content(writeContent);
                    letter.setLetter_Reply((long) 0);
                    letter.setLetter_Answer((long) 0);
                    letter.setLetter_Read(false);
                    letter.setLetter_contact((long) 0);
                    // 往数据库中插入新的信件
                    letter.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if(e == null) {
                                Toast.makeText(WriteLetterActivity.this, "寄信成功", Toast.LENGTH_SHORT).show();
                                editTextContent.setText("");  // 清空信件输入文本
                            }
                            else {
                                Toast.makeText(WriteLetterActivity.this, "寄信失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else
                    Toast.makeText(WriteLetterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 处理从信箱中挑一封信回信的事件
    private void SendFromMailbox() {
        writeContent = editTextContent.getText().toString();

        // 查询数据库中目前最大的Letter_ID
        BmobQuery<Letter> maxLetterIdQuery = new BmobQuery<>();
        maxLetterIdQuery.order("-Letter_ID");
        maxLetterIdQuery.findObjects(new FindListener<Letter>() {
            @Override
            public void done(List<Letter> list, BmobException e) {
                if(e == null) {
                    maxLetterId = list.get(0).getLetter_ID();

                    // 更新数据库中被回信件的信息
                    Letter updateLetter = new Letter();
                    updateLetter.setLetter_Answer(maxLetterId + 1);
                    updateLetter.setLetter_contact(senderId);
                    updateLetter.update(objectId, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e == null) {
                                Log.i("UpDateSuccess", "done: update letter status(SendFromMailbox).");

                                // 装备信件
                                Letter letter = new Letter();
                                letter.setLetter_ID(maxLetterId + 1);
                                letter.setUser_ID(senderId);
                                letter.setLetter_Date(date);
                                letter.setLetter_Content(writeContent);
                                letter.setLetter_Reply(letterId);
                                letter.setLetter_Answer((long) 0);
                                letter.setLetter_Read(false);
                                letter.setLetter_contact(receiverId);
                                // 往数据库中插入新的信件
                                letter.save(new SaveListener<String>() {
                                    @Override
                                    public void done(String s, BmobException e) {
                                        if(e == null) {
                                            Toast.makeText(WriteLetterActivity.this, "寄信成功", Toast.LENGTH_SHORT).show();
                                            editTextContent.setText("");
                                        }
                                        else
                                            Toast.makeText(WriteLetterActivity.this, "寄信失败", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            else
                                Toast.makeText(WriteLetterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else
                    Toast.makeText(WriteLetterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 处理从用户“我的”中挑一封信回信的事件
    private void SendFromUserMy() {
        writeContent = editTextContent.getText().toString();

        // 查询数据库中目前最大的Letter_ID
        BmobQuery<Letter> maxLetterIdQuery = new BmobQuery<>();
        maxLetterIdQuery.order("-Letter_ID");
        maxLetterIdQuery.findObjects(new FindListener<Letter>() {
            @Override
            public void done(List<Letter> list, BmobException e) {
                if(e == null) {
                    maxLetterId = list.get(0).getLetter_ID();

                    // 查询我写的那封信的信件信息
                    BmobQuery<Letter> query = new BmobQuery<>();
                    query.addWhereEqualTo("Letter_ID", letterId);
                    query.findObjects(new FindListener<Letter>() {
                        @Override
                        public void done(List<Letter> list, BmobException e) {
                            if(e == null) {
                                Letter original = list.get(0);

                                // 装备信件
                                Letter letter = new Letter();
                                letter.setLetter_ID(maxLetterId + 1);
                                letter.setUser_ID(senderId);
                                letter.setLetter_Date(date);
                                letter.setLetter_Content(writeContent);
                                letter.setLetter_Reply(original.getLetter_Reply());
                                letter.setLetter_Answer(original.getLetter_Answer());
                                letter.setLetter_Read(false);
                                letter.setLetter_contact(original.getLetter_contact());
                                // 往数据库中插入新的信件
                                letter.save(new SaveListener<String>() {
                                    @Override
                                    public void done(String s, BmobException e) {
                                        if(e == null) {
                                            Toast.makeText(WriteLetterActivity.this, "寄信成功", Toast.LENGTH_SHORT).show();
                                            editTextContent.setText("");
                                        }
                                        else
                                            Toast.makeText(WriteLetterActivity.this, "寄信失败", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            else
                                Toast.makeText(WriteLetterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else
                    Toast.makeText(WriteLetterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 处理从用户“陌生人的”中挑一封信回信的事件
    private void SendFromUserOthers() {
        writeContent = editTextContent.getText().toString();

        // 查询数据库中目前最大的Letter_ID
        final BmobQuery<Letter> maxLetterIdQuery = new BmobQuery<>();
        maxLetterIdQuery.order("-Letter_ID");
        maxLetterIdQuery.findObjects(new FindListener<Letter>() {
            @Override
            public void done(List<Letter> list, BmobException e) {
                if(e == null) {
                    maxLetterId = list.get(0).getLetter_ID();

                    // 更新数据库中被回信件的信息
                    Letter updateLetter = new Letter();
                    updateLetter.setLetter_Answer(maxLetterId + 1);
                    updateLetter.setLetter_contact(senderId);
                    updateLetter.update(objectId, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e == null) {
                                Log.i("UpDateSuccess", "done: update letter status(SendFromMailbox).");

                                // 装备信件
                                Letter letter = new Letter();
                                letter.setLetter_ID(maxLetterId + 1);
                                letter.setUser_ID(senderId);
                                letter.setLetter_Date(date);
                                letter.setLetter_Content(writeContent);
                                letter.setLetter_Reply(letterId);
                                letter.setLetter_Answer((long) 0);
                                letter.setLetter_Read(false);
                                letter.setLetter_contact(receiverId);
                                // 往数据库中插入新的信件
                                letter.save(new SaveListener<String>() {
                                    @Override
                                    public void done(String s, BmobException e) {
                                        if(e == null) {
                                            Toast.makeText(WriteLetterActivity.this, "寄信成功", Toast.LENGTH_SHORT).show();
                                            editTextContent.setText("");
                                        }
                                        else
                                            Toast.makeText(WriteLetterActivity.this, "寄信失败", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            else
                                Toast.makeText(WriteLetterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else
                    Toast.makeText(WriteLetterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
