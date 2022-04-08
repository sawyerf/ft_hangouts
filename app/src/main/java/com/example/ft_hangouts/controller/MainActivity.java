package com.example.ft_hangouts.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
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

        mGoMessagesButton = findViewById(R.id.go_messages_button);
        mGoMessagesButton.setOnClickListener(this);
        mListContacts = findViewById(R.id.list_contacts);
        db = new ContactHelper(MainActivity.this);
        refreshContact();
        if (!checkIfAlreadyhavePermission()) {
            requestForSpecificPermission();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mListContacts.removeAllViews();
        refreshContact();
        // if (!checkIfAlreadyhavePermission()) {
        //     requestForSpecificPermission();
        // }
    }

    private void refreshContact() {
        Cursor cursor = db.getContacts();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, R.string.no_contact, Toast.LENGTH_SHORT).show();
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
        Intent ActivityIntent = null;

        if (view == mGoMessagesButton) {
            ActivityIntent = new Intent(MainActivity.this, MessagesActivity.class);
        } else if (view.getId() == R.id.button_call) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                Button button = view.findViewById(R.id.button_call);
                ActivityIntent = new Intent(Intent.ACTION_CALL);
                ActivityIntent.setData(Uri.parse("tel:" + button.getText().toString()));
            } else {
                Toast.makeText(this, R.string.permission_call_not_granted, Toast.LENGTH_SHORT).show();
                return ;
            }
        } else if (view.getId() == R.id.button_message) {
            ActivityIntent = new Intent(MainActivity.this, ChatActivity.class);
            Button button = view.findViewById(R.id.button_message);
            // ActivityIntent.putExtra("name", button.getText().toString());
            ActivityIntent.putExtra("phone_number", button.getText().toString());
        } else if (view.getId() == R.id.preview_contact) {
            ActivityIntent = new Intent(MainActivity.this, EditActivity.class);
            mIdContact = view.findViewById(R.id.id_contact);
            ActivityIntent.putExtra("id_contact", mIdContact.getText().toString());
        }
        startActivity(ActivityIntent);
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

    private Boolean checkIfAlreadyhavePermission() {
        int result_SEND_SMS = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        int result_RECV_SMS = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);
        int result_CALL = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        return (result_SEND_SMS == PackageManager.PERMISSION_GRANTED &&
                result_RECV_SMS == PackageManager.PERMISSION_GRANTED &&
                result_CALL == PackageManager.PERMISSION_GRANTED);
    }

    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS, Manifest.permission.CALL_PHONE}, 101);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}