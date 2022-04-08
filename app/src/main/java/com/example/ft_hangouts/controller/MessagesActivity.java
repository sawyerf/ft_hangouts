package com.example.ft_hangouts.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ft_hangouts.R;
import com.example.ft_hangouts.model.Contact;
import com.example.ft_hangouts.model.ContactHelper;
import com.example.ft_hangouts.model.Message;
import com.example.ft_hangouts.model.MessageHelper;

import java.sql.Timestamp;
import java.util.List;

public class MessagesActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "DESBARRES";
    private static MessagesActivity instance;
    private View mOriginalMessage;
    private LinearLayout mListMessages;

    private TextView mName;
    private TextView mDate;
    private TextView mPhoneNumber;
    private TextView mPreviewMsg;
    private ContactHelper dbContact;
    private MessageHelper dbMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        instance = this;

        mListMessages = findViewById(R.id.list_messages);
        dbContact = new ContactHelper(MessagesActivity.this);
        dbMessage = new MessageHelper(MessagesActivity.this);
        fillMessages();
        setToolbar();
    }

    public void fillMessages() {
        Contact contact;

        mListMessages.removeAllViews();
        List<Message> messages = dbMessage.getLastMessage();
        for (Message message : messages) {
            mOriginalMessage = LayoutInflater.from(this).inflate(R.layout.preview_message, mListMessages, false);
            // mOriginalMessage.setBackgroundColor(getResources().getColor(R.color.grey_light));
            mOriginalMessage.setOnClickListener(this);
            // Set FirstName & LastName
            mName   = mOriginalMessage.findViewById(R.id.name);
            mDate        = mOriginalMessage.findViewById(R.id.date);
            mPhoneNumber = mOriginalMessage.findViewById(R.id.phone_number);
            mPreviewMsg  = mOriginalMessage.findViewById(R.id.preview_message);

            contact = dbContact.getContactByPhone(message.other);
            if (contact != null) {
                mName.setText(contact.firstname + " " + contact.lastname);
            } else {
                mName.setText(message.other);
            }
            Timestamp ts = new Timestamp(message.date);
            mDate.setText(ts.toString());
            mPhoneNumber.setText(message.other);
            mPreviewMsg.setText(message.content);
            // Add Contact
            mListMessages.addView(mOriginalMessage);
        }
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
    public void onClick(View view) {
        Intent ChatActivityIntent = new Intent(MessagesActivity.this, ChatActivity.class);
        mPhoneNumber = view.findViewById(R.id.phone_number);
        ChatActivityIntent.putExtra("phone_number", mPhoneNumber.getText().toString());
        startActivity(ChatActivityIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fillMessages();
    }

    public static MessagesActivity getInstance() {
        return instance;
    }
}