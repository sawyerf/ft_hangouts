package com.example.ft_hangouts.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.ft_hangouts.R;
import com.example.ft_hangouts.model.myDataBaseHelper;

public class EditActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText mEditFirstname;
    private EditText mEditLastname;
    private EditText mEditPhone;
    private EditText mEditMail;
    private EditText mEditBirthday;
    private EditText mEditAddress;
    private Button   mButtonOK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        mEditFirstname = findViewById(R.id.edit_firstname);
        mEditLastname = findViewById(R.id.edit_lastname);
        mEditPhone = findViewById(R.id.edit_phone);
        mEditMail = findViewById(R.id.edit_mail);
        mEditBirthday = findViewById(R.id.edit_birthday);
        mEditAddress = findViewById(R.id.edit_address);
        mButtonOK = findViewById(R.id.button_ok);

        mButtonOK.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        myDataBaseHelper db = new myDataBaseHelper(EditActivity.this);
        db.addContact(mEditFirstname.getText().toString().trim(),
                mEditLastname.getText().toString().trim(),
                mEditPhone.getText().toString().trim()
        );
    }
}