package com.example.ft_hangouts.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private static String TAG = "DESBARRES";
    private View mOriginalMessage;
    private LinearLayout mListMessages;

    private TextView mFirstname;
    private TextView mLastname;
    private TextView mDate;
    private TextView mPhoneNumber;
    private TextView mPreviewMsg;
    private TextView mNameContact;
    private ContactHelper dbContact;
    private MessageHelper dbMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        mListMessages = findViewById(R.id.list_messages);
        dbContact = new ContactHelper(MessagesActivity.this);
        dbMessage = new MessageHelper(MessagesActivity.this);
        fillMessages();
    }

    private void fillMessages() {
        Contact contact;

        List<Message> messages = dbMessage.getLastMessage();
        for (Message message : messages) {
            mOriginalMessage = LayoutInflater.from(this).inflate(R.layout.preview_message, mListMessages, false);
            // mOriginalMessage.setBackgroundColor(getResources().getColor(R.color.grey_light));
            mOriginalMessage.setOnClickListener(this);
            // Set FirstName & LastName
            mFirstname   = mOriginalMessage.findViewById(R.id.firstname);
            mLastname    = mOriginalMessage.findViewById(R.id.lastname);
            mDate        = mOriginalMessage.findViewById(R.id.date);
            mPhoneNumber = mOriginalMessage.findViewById(R.id.phone_number);
            mNameContact = mOriginalMessage.findViewById(R.id.name_contact);
            mPreviewMsg  = mOriginalMessage.findViewById(R.id.preview_message);

            contact = dbContact.getContactByPhone(message.other);
            if (contact != null) {
                mFirstname.setText(contact.firstname);
                mLastname.setText(contact.lastname);
                mNameContact.setText(contact.firstname + " " + contact.lastname);
            } else {
                mFirstname.setText(message.other);
                mNameContact.setText(message.other);
            }
            Timestamp ts = new Timestamp((long)message.date);
            mDate.setText(ts.toString());
            mPhoneNumber.setText(message.other);
            mPreviewMsg.setText(message.content);
            // Add Contact
            mListMessages.addView(mOriginalMessage);
        }
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
        mListMessages.removeAllViews();
        fillMessages();
    }
}