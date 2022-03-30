package com.example.ft_hangouts.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ft_hangouts.R;
import com.example.ft_hangouts.model.Contact;
import com.example.ft_hangouts.model.myDataBaseHelper;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    static String TAG = "DESBARRES";
    private View mOriginalContact;
    private LinearLayout mListContacts;
    private TextView mFirstname;
    private TextView mLastname;

    private myDataBaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new myDataBaseHelper(MainActivity.this);
        /*
        db.addContact("Jacques", "Verges", "0611223344");
        db.addContact("Djamila", "Bouhired", "0611223344");
        db.addContact("Francois", "Begeaudeau", "0611223344");
        db.addContact("Karl", "Marx", "0611223344");
        db.addContact("Louise", "Michel", "0611223344");
        db.addContact("Emma", "Goldman", "0611223344");
        db.addContact("Thomas", "Sankara", "0611223344");
         */
        Cursor cursor = db.getContacts();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No data.", Toast.LENGTH_SHORT).show();
        } else {
            fillContacts(cursor);
        }
    }

    private void fillContacts(Cursor cursor) {
        List<Contact> contacts = db.CursorToContact(cursor);
        for (Contact contact:contacts) {
            mListContacts = findViewById(R.id.list_contacts);
            mOriginalContact = LayoutInflater.from(this).inflate(R.layout.main_one_contact, mListContacts, false);
            mOriginalContact.setBackgroundColor(getResources().getColor(R.color.grey_light));
            mOriginalContact.setOnClickListener(this);
            // Set FirstName & LastName
            mFirstname = mOriginalContact.findViewById(R.id.firstname);
            mFirstname.setText(contact.firstname);
            mLastname = mOriginalContact.findViewById(R.id.lastname);
            mLastname.setText(contact.lastname);
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