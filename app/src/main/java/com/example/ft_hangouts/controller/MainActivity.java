package com.example.ft_hangouts.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ft_hangouts.R;
import com.example.ft_hangouts.model.Contact;
import com.example.ft_hangouts.model.ContactHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "DESBARRES";
    private static final String SHARED_PREF_NAME = "ft_hangouts";
    private static final String SHARED_PREF_COLOR = "COLOR_TOOLBAR";
    private static final String SHARED_PREF_PROGRESS = "PROGRESS_SEEKBAR";
    private LinearLayout mListContacts;
    private TextView mFirstname;
    private TextView mLastname;
    private TextView mIdContact;
    private FloatingActionButton mGoMessagesButton;
    private Button mButtonCall;
    private Button mButtonMessage;
    private SeekBar mSeekColor;

    private ContactHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new ContactHelper(MainActivity.this);

        mGoMessagesButton = findViewById(R.id.go_messages_button);
        mGoMessagesButton.setOnClickListener(this);
        mListContacts = findViewById(R.id.list_contacts);
        mSeekColor = findViewById(R.id.seekbar_color);

        refreshContact();
        if (!checkIfAlreadyhavePermission()) {
            requestForSpecificPermission();
        }

        String color_toolbar = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)
                .getString(SHARED_PREF_COLOR, "#0000AB");
        int progress = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)
                .getInt(SHARED_PREF_PROGRESS, 171);
        mSeekColor.setProgress(progress);
        setToolbar(color_toolbar);
        mSeekColor.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                mGoMessagesButton.setVisibility(View.VISIBLE);
                Log.d(TAG, "onStopTrackingTouch: " + convertColor(seekBar.getProgress()));
                Log.d(TAG, "onStopTrackingTouch: " + seekBar.getProgress());
                getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)
                        .edit()
                        .putString(SHARED_PREF_COLOR, convertColor(seekBar.getProgress()))
                        .apply();
                getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)
                        .edit()
                        .putInt(SHARED_PREF_PROGRESS, seekBar.getProgress())
                        .apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mGoMessagesButton.setVisibility(View.GONE);
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Log.d(TAG, "onProgressChanged: " + color);
                setToolbar(convertColor(seekBar.getProgress()));
            }
        });
    }

    private String convertColor(int progress) {
        double part = (progress + 1.0) / 256.0;
        int redColor = (int) (255.0 * part) - 765;
        int greenColor = ((int) (255.0 * (part % 4.0)) - 255) % (255 * 2);
        int blueColor = (int) (255.0 * (part % 1.000000000001));

        if (redColor < 0) {
            redColor = 0;
        } else if (redColor > 255) {
            redColor = 255;
        }
        if (part % 2.0 > 1) {
            blueColor = 255 - blueColor;
        }
        if (greenColor < 0) {
            greenColor = 0;
        } else if (greenColor > 255) {
            greenColor = 255;
        } else if ((part % 4.0) >= 3.0) {
            greenColor = 255 - greenColor;
        }
        return (String.format("#%02X%02X%02X", redColor, greenColor, blueColor));
    }

    private void setToolbar(String color_str) {
        int color = Color.parseColor(color_str);

        mSeekColor.getThumb().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        ColorDrawable colorDrawable = new ColorDrawable(color);
        getSupportActionBar()
                .setBackgroundDrawable(colorDrawable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mListContacts.removeAllViews();
        refreshContact();
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
            View mOriginalContact = LayoutInflater.from(this).inflate(R.layout.preview_contact, mListContacts, false);
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
                return;
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