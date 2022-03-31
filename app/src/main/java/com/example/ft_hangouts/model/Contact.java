package com.example.ft_hangouts.model;

import android.database.Cursor;

public class Contact {
    public Integer idContact;
    public String firstname;
    public String lastname;
    public String phone;

    public Contact(Cursor cursor) {
        if (cursor != null) {
            idContact = cursor.getInt(0);
            firstname = cursor.getString(1);
            lastname = cursor.getString(2);
            phone = cursor.getString(3);
        }
    }
}