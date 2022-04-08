package com.example.ft_hangouts.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ft_hangouts.R;
import com.example.ft_hangouts.model.Contact;
import com.example.ft_hangouts.model.ContactHelper;
import com.example.ft_hangouts.model.Message;
import com.example.ft_hangouts.model.MessageHelper;

import java.sql.Timestamp;
import java.util.List;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "DESBARRES";
    public static ChatActivity instance;

    private LinearLayout mListMessages;
    private View mOriginalMessage;
    private Button mSendButton;
    private EditText mEditContent;
    private ScrollView mScroll;

    private String phoneNumber;

    private MessageHelper dbMessage;
    private ContactHelper dbContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        instance = this;

        mListMessages = findViewById(R.id.list_chat);
        mSendButton = findViewById(R.id.send_button);
        mEditContent = findViewById(R.id.chat_content);
        mScroll = findViewById(R.id.scroll_chat);
        mSendButton.setOnClickListener(this);

        dbMessage = new MessageHelper(ChatActivity.this);
        dbContact = new ContactHelper(ChatActivity.this);

        Bundle bundle = getIntent().getExtras();
        phoneNumber = bundle.getString("phone_number");
        Contact contact = dbContact.getContactByPhone(phoneNumber);
        if (contact != null) {
            setTitle(contact.firstname + " " + contact.lastname);
        } else {
            setTitle(phoneNumber);
        }
        Log.d(TAG, "onCreate: " + phoneNumber);

        List<Message> listMessages = dbMessage.getMessageByPhone(phoneNumber);
        for (Message message : listMessages) {
            if (message.direction == message.ISRECV) {
                addMessage(Gravity.START, message.content);
            } else if (message.direction == message.ISSEND) {
                addMessage(Gravity.END, message.content);
            }
        }
        checkIfHavePermission();
        setToolbar();
    }

    private void checkIfHavePermission() {
        int result_SEND_SMS = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        if (result_SEND_SMS == PackageManager.PERMISSION_GRANTED) {
            mSendButton.setEnabled(true);
            mEditContent.setEnabled(true);
        } else {
            Toast.makeText(this, R.string.permission_not_granted, Toast.LENGTH_SHORT).show();
            mSendButton.setEnabled(false);
            mEditContent.setEnabled(false);
        };
    }

    private void setToolbar() {
        int progress = getSharedPreferences("ft_hangouts", MODE_PRIVATE)
                .getInt("COLOR_TOOLBAR", 5708771);
        int color = Color.parseColor(String.format("#%06X", progress));

        ColorDrawable colorDrawable = new ColorDrawable(color);
        getSupportActionBar()
                .setBackgroundDrawable(colorDrawable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkIfHavePermission();
    }

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

    public void updateMessage(String phone, String content) {
        if (phone.equals(phoneNumber)) {
            addMessage(Gravity.START, content);
        }
    }

    public static ChatActivity getInstance() {
        return instance;
    }

    private Boolean sendMessage(String phoneNumber, String content) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, content, null, null);
        } catch (Exception e) {
            Log.d(TAG, "sendMessage: " + e);
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        String content = mEditContent.getText().toString();

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Log.d(TAG, "onClick: " + timestamp.toString());

        if (sendMessage(phoneNumber, content)) {
            dbMessage.addMessage(content, timestamp.getTime(), "me", phoneNumber, dbMessage.ISSEND);
            addMessage(Gravity.END, content);
            mEditContent.setText(new String[]{"Des barres ! \uD83E\uDEB4", "Moi aussi ! \uD83E\uDEB4", "😊"}[(int) (Math.random() * 3.0)]);
        }
    }
}