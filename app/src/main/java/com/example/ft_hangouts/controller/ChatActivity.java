package com.example.ft_hangouts.controller;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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

    private Boolean isContact;
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
            isContact = true;
            setTitle(contact.firstname + " " + contact.lastname);
        } else {
            isContact = false;
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
        }
    }

    private void setToolbar() {
        String progress = getSharedPreferences("ft_hangouts", MODE_PRIVATE)
                .getString("COLOR_TOOLBAR", "#0000AB");
        int color = Color.parseColor(progress);

        ColorDrawable colorDrawable = new ColorDrawable(color);
        getSupportActionBar()
                .setBackgroundDrawable(colorDrawable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Contact contact = dbContact.getContactByPhone(phoneNumber);
        if (contact != null) {
            isContact = true;
            setTitle(contact.firstname + " " + contact.lastname);
        } else {
            isContact = false;
            setTitle(phoneNumber);
        }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isContact == false) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.main, menu);
            Log.d(TAG, "onCreateOptionsMenu: des barres");
        }
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_contact) {
            Intent ActivityIntent = new Intent(ChatActivity.this, EditActivity.class);
            Log.d(TAG, "onOptionsItemSelected: " + phoneNumber);
            ActivityIntent.putExtra("new_phone", phoneNumber);
            startActivity(ActivityIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        String content = mEditContent.getText().toString();

        if (!content.equals("")) {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            Log.d(TAG, "onClick: " + timestamp.toString());

            if (sendMessage(phoneNumber, content)) {
                dbMessage.addMessage(content, timestamp.getTime(), "me", phoneNumber, dbMessage.ISSEND);
                addMessage(Gravity.END, content);
                mEditContent.setText(new String[]{"Des barres ! \uD83E\uDEB4", "Moi aussi ! \uD83E\uDEB4", "😊"}[(int) (Math.random() * 3.0)]);
                // mEditContent.setText("");
            }
        }
    }
}