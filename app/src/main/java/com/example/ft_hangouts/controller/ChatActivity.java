package com.example.ft_hangouts.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {
    static String TAG = "DESBARRES";

    private LinearLayout mListMessages;
    private View mOriginalMessage;
    private Button mSendButton;
    private EditText mEditContent;
    private ScrollView mScroll;

    void addMessage(int gravity, String content) {
        mOriginalMessage = LayoutInflater.from(this).inflate(R.layout.message_chat, mListMessages, false);
        TextView msgView = mOriginalMessage.findViewById(R.id.unique_message);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = gravity;
        params.bottomMargin = 12;
        msgView.setLayoutParams(params);
        msgView.setText(content);
        mListMessages.addView(mOriginalMessage);
        Log.d(TAG, "addMessage: " + mListMessages.getHeight());
        mScroll.scrollTo(0, mListMessages.getHeight() + 5000);
        // mScroll.fullScroll(View.FOCUS_DOWN);
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
        addMessage(Gravity.RIGHT, "Des barres ! \uD83E\uDEB4");
        addMessage(Gravity.LEFT, "Moi aussi ! \uD83E\uDEB4");
        addMessage(Gravity.LEFT, "😊");
    }

    @Override
    public void onClick(View view) {
        addMessage(Gravity.RIGHT, mEditContent.getText().toString());
        mEditContent.setText("");
    }
}