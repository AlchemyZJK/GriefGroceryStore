package com.example.griefgrocerystore;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class ShowLetterActivity extends AppCompatActivity {
    private ImageButton imageButtonBack;
    private ImageButton imageButtonWrite;
    private String content, flag, objectId;
    private Long senderId, letterId, receiverId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_letter);

        // 从MailboxFragment或UserFragment获取信件内容
        Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();
        assert bundle != null;
        flag = bundle.getString("Flag");
        objectId = bundle.getString("ObjectID");
        content = bundle.getString("LetterContent");
        senderId = bundle.getLong("SenderID");
        letterId = bundle.getLong("LetterID");
        receiverId = bundle.getLong("ReceiverID");
        // 显示信件内容
        TextView letterContent = findViewById(R.id.show_letter_content);
        letterContent.setText(content);

        imageButtonBack = findViewById(R.id.button_show_back);
        imageButtonWrite = findViewById(R.id.button_show_write);
        imageButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 从ShowLetterActivity返回MenuActivity的UserFragment
                Intent mIntent = new Intent(ShowLetterActivity.this, MenuActivity.class);
                mIntent.putExtra("fragment", 3);
                startActivity(mIntent);
            }
        });
        imageButtonWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 从ShowLetterActivity传递数据给WriteLetterActivity
                Intent mIntent = new Intent(ShowLetterActivity.this, WriteLetterActivity.class);
                Bundle bundleToWrite = new Bundle();
                bundleToWrite.putString("Flag", flag);
                bundleToWrite.putString("ObjectID", objectId);
                bundleToWrite.putLong("SenderID", senderId);
                bundleToWrite.putLong("LetterID", letterId);
                bundleToWrite.putLong("ReceiverID", receiverId);
                mIntent.putExtras(bundleToWrite);
                startActivity(mIntent);
            }
        });
    }
}
