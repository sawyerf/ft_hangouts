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
import com.example.ft_hangouts.model.MessageHelper;

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
        Cursor cursor = dbContact.getContacts();
        fillMessages(cursor);
    }

    private void fillMessages(Cursor cursor) {
        List<Contact> contacts = dbContact.CursorToContact(cursor);
        for (Contact contact:contacts) {
        // for (int i = 0; i < 15; i++) {
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
            mFirstname.setText(contact.firstname);
            mLastname.setText(contact.lastname);
            mDate.setText("11:15");
            mPhoneNumber.setText(contact.phone);
            mNameContact.setText(contact.firstname + " " + contact.lastname);
            mPreviewMsg.setText("Des barres ! \uD83E\uDEB4");
            // Add Contact
            mListMessages.addView(mOriginalMessage);
        }
    }

    @Override
    public void onClick(View view) {
        Intent ChatActivityIntent = new Intent(MessagesActivity.this, ChatActivity.class);
        mNameContact = mOriginalMessage.findViewById(R.id.name_contact);
        mPhoneNumber = view.findViewById(R.id.phone_number);
        ChatActivityIntent.putExtra("name", mNameContact.getText().toString());
        ChatActivityIntent.putExtra("phone_number", mPhoneNumber.getText().toString());
        startActivity(ChatActivityIntent);
    }
}