package com.example.ft_hangouts.controller;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ft_hangouts.R;
import com.example.ft_hangouts.model.Contact;
import com.example.ft_hangouts.model.ContactHelper;

public class EditActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "DESBARRES";

    private String idContact;
    private EditText mEditFirstname;
    private EditText mEditLastname;
    private EditText mEditPhone;
    private EditText mEditMail;
    private EditText mEditDesc;
    private Button mButtonOK;
    private Button mButtonDelete;
    private Button mButtonFake;

    private ContactHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        db = new ContactHelper(EditActivity.this);

        mEditFirstname = findViewById(R.id.edit_firstname);
        mEditLastname = findViewById(R.id.edit_lastname);
        mEditPhone = findViewById(R.id.edit_phone);
        mEditMail = findViewById(R.id.edit_mail);
        mEditDesc = findViewById(R.id.edit_desc);
        mButtonOK = findViewById(R.id.button_ok);
        mButtonDelete = findViewById(R.id.button_delete);
        mButtonFake = findViewById(R.id.button_fake);

        mButtonFake.setOnClickListener(this);
        mButtonOK.setOnClickListener(this);
        mButtonDelete.setOnClickListener(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            idContact = bundle.getString("id_contact");
            String phoneNumber = bundle.getString("new_phone");

            if (idContact != null) {
                Log.d(TAG, idContact);
                Contact contact = db.getContactById(idContact);

                if (contact == null) {
                    Toast.makeText(this, R.string.contact_not_found, Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                mEditFirstname.setText(contact.firstname);
                mEditLastname.setText(contact.lastname);
                mEditPhone.setText(contact.phone);
                mEditMail.setText(contact.mail);
                mEditDesc.setText(contact.description);
                mButtonFake.setEnabled(false);
                mButtonFake.setVisibility(View.GONE);
            } else if (phoneNumber != null) {
                Log.d(TAG, "onCreate: " + phoneNumber);
                mEditPhone.setText(phoneNumber);
                mButtonDelete.setEnabled(false);
                mButtonDelete.setVisibility(View.GONE);
                mButtonFake.setEnabled(false);
                mButtonFake.setVisibility(View.GONE);
            }
        } else {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(R.string.new_contact);
                mButtonDelete.setEnabled(false);
                mButtonDelete.setVisibility(View.GONE);
            }
        }
        setToolbar();
    }

    @Override
    public void onClick(View view) {
        if (view == mButtonOK) {
            if (mEditPhone.getText().toString().trim().equals("") || mEditFirstname.getText().toString().trim().equals("")) {
                Toast.makeText(this, R.string.incomplete_edit, Toast.LENGTH_SHORT).show();
                return;
            }
            if (idContact == null) {
                db.addContact(mEditFirstname.getText().toString().trim(),
                        mEditLastname.getText().toString().trim(),
                        mEditPhone.getText().toString().trim(),
                        mEditMail.getText().toString().trim(),
                        mEditDesc.getText().toString().trim()
                );
            } else {
                db.upContact(idContact,
                        mEditFirstname.getText().toString().trim(),
                        mEditLastname.getText().toString().trim(),
                        mEditPhone.getText().toString().trim(),
                        mEditMail.getText().toString().trim(),
                        mEditDesc.getText().toString().trim()
                );
            }
        } else if (view == mButtonDelete) {
            db.delContact(idContact);
        } else if (view == mButtonFake) {
            db.addContact("Jacques", "Verges", "06" + (int) (Math.random() * 100000000), "jacques.verges@mail.com", "Des barres ! \uD83E\uDEB4");
            db.addContact("Djamila", "Bouhired", "06" + (int) (Math.random() * 100000000), "djamila.bouhired@mail.com", "Des barres ! \uD83E\uDEB4");
            db.addContact("Francois", "Begeaudeau", "06" + (int) (Math.random() * 100000000), "francois.b@mail.com", "Des barres ! \uD83E\uDEB4");
            db.addContact("Karl", "Marx", "06" + (int) (Math.random() * 100000000), "karl.m@mail.com", "Des barres ! \uD83E\uDEB4");
            db.addContact("Louise", "Michel", "06" + (int) (Math.random() * 100000000), "louis.mimi@mail.com", "Des barres ! \uD83E\uDEB4");
            db.addContact("Emma", "Goldman", "06" + (int) (Math.random() * 100000000), "emma.goldwoman@mail.com", "Des barres ! \uD83E\uDEB4");
            db.addContact("Thomas", "Sankara", "06" + (int) (Math.random() * 100000000), "thomas.sanka@mail.com", "Des barres ! \uD83E\uDEB4");
        }
        finish();
    }

    private void setToolbar() {
        String progress = getSharedPreferences("ft_hangouts", MODE_PRIVATE)
                .getString("COLOR_TOOLBAR", "#FFFFFF");
        int color = Color.parseColor(progress);

        ColorDrawable colorDrawable = new ColorDrawable(color);
        getSupportActionBar()
                .setBackgroundDrawable(colorDrawable);
    }
}