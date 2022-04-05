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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ft_hangouts.R;
import com.example.ft_hangouts.model.Contact;
import com.example.ft_hangouts.model.ContactHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    static String TAG = "DESBARRES";
    private View mOriginalContact;
    private LinearLayout mListContacts;
    private TextView mFirstname;
    private TextView mLastname;
    private TextView mIdContact;
    private FloatingActionButton mGoMessagesButton;
    private Button mButtonCall;
    private Button mButtonMessage;

    private ContactHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Intent EditActivityIntent = new Intent(MainActivity.this, MessagesActivity.class);
        // startActivity(EditActivityIntent);

        mGoMessagesButton = findViewById(R.id.go_messages_button);
        mGoMessagesButton.setOnClickListener(this);
        mListContacts = findViewById(R.id.list_contacts);
        db = new ContactHelper(MainActivity.this);
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
        for (Contact contact : contacts) {
            mOriginalContact = LayoutInflater.from(this).inflate(R.layout.preview_contact, mListContacts, false);
            mOriginalContact.setBackgroundColor(getResources().getColor(R.color.grey_light));
            mOriginalContact.setOnClickListener(this);
            // Get View
            mFirstname = mOriginalContact.findViewById(R.id.firstname);
            mLastname = mOriginalContact.findViewById(R.id.lastname);
            mIdContact = mOriginalContact.findViewById(R.id.id_contact);
            mButtonCall = mOriginalContact.findViewById(R.id.button_call);
            mButtonMessage = mOriginalContact.findViewById(R.id.button_message);
            // Set FirstName & LastName
            mFirstname.setText(contact.firstname);
            mLastname.setText(contact.lastname);
            mIdContact.setText(contact.idContact.toString());
            mButtonMessage.setText(contact.phone);
            mButtonCall.setText(contact.phone);
            // Set Button
            mButtonCall.setOnClickListener(this);
            mButtonMessage.setOnClickListener(this);
            // Add Contact
            mListContacts.addView(mOriginalContact);
        }
    }

    @Override
    public void onClick(View view) {
        Intent ActivityIntent;
        if (view == mGoMessagesButton) {
            ActivityIntent = new Intent(MainActivity.this, MessagesActivity.class);
            startActivity(ActivityIntent);
        } else if (view.getId() == R.id.button_call) {
            Log.d(TAG, "onClick: Call");
        } else if (view.getId() == R.id.button_message) {
            ActivityIntent = new Intent(MainActivity.this, ChatActivity.class);
            Button button = view.findViewById(R.id.button_message);
            ActivityIntent.putExtra("name", button.getText().toString());
            ActivityIntent.putExtra("phone_number", button.getText().toString());
            startActivity(ActivityIntent);
        } else if (view.getId() == R.id.preview_contact){
            ActivityIntent = new Intent(MainActivity.this, EditActivity.class);
            mIdContact = view.findViewById(R.id.id_contact);
            ActivityIntent.putExtra("id_contact", mIdContact.getText().toString());
            Log.d(TAG, "onClick: BG CA CLICK");
            startActivity(ActivityIntent);
        }
        // startActivity(ActivityIntent);
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