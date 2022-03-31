package com.example.ft_hangouts.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ft_hangouts.R;
import com.example.ft_hangouts.model.ContactHelper;

public class MessagesActivity extends AppCompatActivity implements View.OnClickListener {
    private static String TAG = "DESBARRES";
    private View mOriginalMessage;
    private LinearLayout mListMessages;

    private TextView mFirstname;
    private TextView mLastname;
    private TextView mDate;
    private TextView mPreviewMsg;
    private ContactHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        mListMessages = findViewById(R.id.list_messages);
        // db = new ContactHelper(MainActivity.this);
        fillMessages();
    }

    private void fillMessages(/*Cursor cursor*/) {
        // List<Contact> contacts = db.CursorToContact(cursor);
        // for (Contact contact:contacts) {
        for (int i = 0; i < 15; i++) {
            mOriginalMessage = LayoutInflater.from(this).inflate(R.layout.preview_message, mListMessages, false);
            // mOriginalMessage.setBackgroundColor(getResources().getColor(R.color.grey_light));
            mOriginalMessage.setOnClickListener(this);
            // Set FirstName & LastName
            mFirstname  = mOriginalMessage.findViewById(R.id.firstname);
            mLastname   = mOriginalMessage.findViewById(R.id.lastname);
            mDate       = mOriginalMessage.findViewById(R.id.date);
            mPreviewMsg = mOriginalMessage.findViewById(R.id.preview_message);
            mFirstname.setText("Jacques");
            mLastname.setText("Verges");
            mDate.setText("11:15");
            mPreviewMsg.setText("Des barres ! \uD83E\uDEB4");
            // Add Contact
            mListMessages.addView(mOriginalMessage);
        }
    }

    @Override
    public void onClick(View view) {
        Intent ChatActivityIntent = new Intent(MessagesActivity.this, ChatActivity.class);
        // mIdContact = view.findViewById(R.id.id_contact);
        // EditActivityIntent.putExtra("id_contact", mIdContact.getText().toString());
        startActivity(ChatActivityIntent);
    }
}