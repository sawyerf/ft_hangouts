package com.example.ft_hangouts.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.ft_hangouts.R;
import com.example.ft_hangouts.model.Message;
import com.example.ft_hangouts.model.MessageHelper;

import java.sql.Timestamp;
import java.util.List;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {
    static String TAG = "DESBARRES";

    private LinearLayout mListMessages;
    private View mOriginalMessage;
    private Button mSendButton;
    private EditText mEditContent;
    private ScrollView mScroll;

    private String nameContact;
    private String phoneNumber;

    private MessageHelper dbMessage;

    private void scrollDown() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mScroll.fullScroll(View.FOCUS_DOWN);
            }
        }, 100);
    }

    void addMessage(int gravity, String content) {
        mOriginalMessage = LayoutInflater.from(this).inflate(R.layout.message_chat, mListMessages, false);
        TextView msgView = mOriginalMessage.findViewById(R.id.unique_message);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = gravity;
        params.bottomMargin = 12;
        msgView.setLayoutParams(params);
        msgView.setText(content);
        mListMessages.addView(mOriginalMessage);
        scrollDown();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mListMessages = findViewById(R.id.list_chat);
        mSendButton = findViewById(R.id.send_button);
        mEditContent = findViewById(R.id.chat_content);
        mScroll = findViewById(R.id.scroll_chat);
        mSendButton.setOnClickListener(this);

        dbMessage = new MessageHelper(ChatActivity.this);

        Bundle bundle = getIntent().getExtras();
        nameContact = bundle.getString("name");
        phoneNumber = bundle.getString("phone_number");
        setTitle(nameContact);
        Log.d(TAG, "onCreate: " + nameContact);
        Log.d(TAG, "onCreate: " + phoneNumber);

        List<Message> listMessages = dbMessage.getMessageByPhone(phoneNumber);
        for (Message message : listMessages) {
            if (message.sendby == phoneNumber) {
                addMessage(Gravity.LEFT, message.content);
            } else {
                addMessage(Gravity.RIGHT, message.content);
            }
        }
        /*
        addMessage(Gravity.RIGHT, "Des barres ! \uD83E\uDEB4");
        addMessage(Gravity.LEFT, "Moi aussi ! \uD83E\uDEB4");
        addMessage(Gravity.LEFT, "😊");
         */
    }

    @Override
    public void onClick(View view) {
        String content = mEditContent.getText().toString();

        addMessage(Gravity.RIGHT, content);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        dbMessage.addMessage(content, timestamp.getTime(), "me", phoneNumber);
        mEditContent.setText("");
    }
}