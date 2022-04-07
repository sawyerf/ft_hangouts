package com.example.ft_hangouts.controller;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
    private EditText mEditBirthday;
    private EditText mEditAddress;
    private Button   mButtonOK;
    private Button   mButtonDelete;
    private Button   mButtonFake;

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
        mEditBirthday = findViewById(R.id.edit_birthday);
        mEditAddress = findViewById(R.id.edit_address);
        mButtonOK = findViewById(R.id.button_ok);
        mButtonDelete = findViewById(R.id.button_delete);
        mButtonFake = findViewById(R.id.button_fake);
        mButtonFake.setOnClickListener(this);
        mButtonOK.setOnClickListener(this);
        mButtonDelete.setOnClickListener(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            idContact = bundle.getString("id_contact");
            Log.d(TAG, idContact);
            if (idContact != null) {
                Contact contact = db.getContactById(idContact);

                if (contact == null) {
                    Toast.makeText(this, R.string.contact_not_found, Toast.LENGTH_SHORT).show();
                    finish();
                    return ;
                }
                mEditFirstname.setText(contact.firstname);
                mEditLastname.setText(contact.lastname);
                mEditPhone.setText(contact.phone);
            }
        } else {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null)
            {
                actionBar.setTitle(R.string.new_contact);
                mButtonDelete.setEnabled(false);
                mButtonDelete.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view == mButtonOK) {
            if (idContact == null) {
                db.addContact(mEditFirstname.getText().toString().trim(),
                        mEditLastname.getText().toString().trim(),
                        mEditPhone.getText().toString().trim()
                );
            } else {
                db.upContact(idContact,
                        mEditFirstname.getText().toString().trim(),
                        mEditLastname.getText().toString().trim(),
                        mEditPhone.getText().toString().trim()
                );
            }
        } else if (view == mButtonDelete) {
            db.delContact(idContact);
        } else if (view == mButtonFake) {
            db.addContact("Jacques", "Verges", "06" + (int)(Math.random() * 100000000));
            db.addContact("Djamila", "Bouhired", "06" + (int)(Math.random() * 100000000));
            db.addContact("Francois", "Begeaudeau", "06" + (int)(Math.random() * 100000000));
            db.addContact("Karl", "Marx", "06" + (int)(Math.random() * 100000000));
            db.addContact("Louise", "Michel", "06" + (int)(Math.random() * 100000000));
            db.addContact("Emma", "Goldman", "06" + (int)(Math.random() * 100000000));
            db.addContact("Thomas", "Sankara", "06" + (int)(Math.random() * 100000000));
        }
        finish();
    }
}