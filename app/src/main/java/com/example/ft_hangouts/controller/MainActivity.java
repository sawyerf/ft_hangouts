package com.example.ft_hangouts.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    private TextView mIdContact;

    private myDataBaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListContacts = findViewById(R.id.list_contacts);
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
        refreshContact();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        mListContacts.removeAllViews();
        refreshContact();
    }

    private void refreshContact() {
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
            mOriginalContact = LayoutInflater.from(this).inflate(R.layout.main_one_contact, mListContacts, false);
            mOriginalContact.setBackgroundColor(getResources().getColor(R.color.grey_light));
            mOriginalContact.setOnClickListener(this);
            // Set FirstName & LastName
            mFirstname = mOriginalContact.findViewById(R.id.firstname);
            mLastname = mOriginalContact.findViewById(R.id.lastname);
            mIdContact = mOriginalContact.findViewById(R.id.id_contact);
            mFirstname.setText(contact.firstname);
            mLastname.setText(contact.lastname);
            mIdContact.setText(contact.idContact.toString());
            // Add Contact
            mListContacts.addView(mOriginalContact);
        }
    }

    @Override
    public void onClick(View view) {
        Intent EditActivityIntent = new Intent(MainActivity.this, EditActivity.class);
        mIdContact = view.findViewById(R.id.id_contact);
        EditActivityIntent.putExtra("id_contact", mIdContact.getText().toString());
        startActivity(EditActivityIntent);
        Log.d(TAG, "onClick: BG CA CLICK");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_contact) {
            Intent EditActivityIntent = new Intent(MainActivity.this, EditActivity.class);
            startActivity(EditActivityIntent);
        }
        return super.onOptionsItemSelected(item);
    }
}