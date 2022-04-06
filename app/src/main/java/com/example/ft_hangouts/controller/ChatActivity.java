package com.example.ft_hangouts.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
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
        if (!checkIfAlreadyhavePermission()) {
            requestForSpecificPermission();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!checkIfAlreadyhavePermission()) {
            requestForSpecificPermission();
        }
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

    private Boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS}, 101);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "You can't send message with accept permission", Toast.LENGTH_SHORT).show();
                    mSendButton.setEnabled(false);
                    mEditContent.setEnabled(false);
                } else {
                    mSendButton.setEnabled(true);
                    mEditContent.setEnabled(true);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private Boolean sendMessage(String phoneNumber, String content) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, content, null, null);
            // Toast.makeText(this, "Des Pure moment de plaisir", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.d(TAG, "sendMessage: " + e);
            // Toast.makeText(this, "Wooooa Ca fonctionne pas", Toast.LENGTH_SHORT).show();
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