package com.example.ft_hangouts.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ft_hangouts.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    static String TAG = "DESBARRES";
    private View mOriginalContact;
    private LinearLayout mListContacts;
    private TextView mFirstname;
    private TextView mLastname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        for (int i = 0; i < 25; i++) {
            mListContacts = findViewById(R.id.list_contacts);
            mOriginalContact = LayoutInflater.from(this).inflate(R.layout.main_one_contact, mListContacts, false);
            mOriginalContact.setBackgroundColor(R.color.grey_light);
            mOriginalContact.setOnClickListener(this);
            mFirstname = mOriginalContact.findViewById(R.id.firstname);
            mFirstname.setText("Jacques");
            mLastname = mOriginalContact.findViewById(R.id.lastname);
            mLastname.setText("Verges");
            mListContacts.addView(mOriginalContact);
        }
    }

    @Override
    public void onClick(View view) {
        Intent EditActivityIntent = new Intent(MainActivity.this, EditActivity.class);
        startActivity(EditActivityIntent);
        Log.d(TAG, "onClick: BG CA CLICK");
    }
}