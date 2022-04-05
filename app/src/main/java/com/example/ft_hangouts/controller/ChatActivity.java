package com.example.ft_hangouts.controller;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
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
import android.widget.Toast;

import com.example.ft_hangouts.R;
import com.example.ft_hangouts.model.Contact;
import com.example.ft_hangouts.model.ContactHelper;
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

    private String phoneNumber;

    private MessageHelper dbMessage;
    private ContactHelper dbContact;

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
                addMessage(Gravity.LEFT, message.content);
            } else if (message.direction == message.ISSEND) {
                addMessage(Gravity.RIGHT, message.content);
            }
        }
        /*
        addMessage(Gravity.RIGHT, "Des barres ! \uD83E\uDEB4");
        addMessage(Gravity.LEFT, "Moi aussi ! \uD83E\uDEB4");
        addMessage(Gravity.LEFT, "😊");
         */
    }

    private Boolean sendMessage(String phoneNumber, String content) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, content, null, null);
            Toast.makeText(this, "Des Pure moment de plaisir", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.d(TAG, "sendMessage: " + e);
            Toast.makeText(this, "Wooooa Ca fonctionne pas", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View view) {

        String content = mEditContent.getText().toString();

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Log.d(TAG, "onClick: " + timestamp.toString());

        ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {});

        /*
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.SEND_SMS }, 100);
        }

        if (ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ChatActivity.this, new String[]{Manifest.permission.SEND_SMS}, 1);
        }
         */
        if (sendMessage(phoneNumber, content)) {
            dbMessage.addMessage(content, timestamp.getTime(), "me", phoneNumber, dbMessage.ISSEND);
            addMessage(Gravity.RIGHT, content);
            mEditContent.setText("");
        }
    }
}