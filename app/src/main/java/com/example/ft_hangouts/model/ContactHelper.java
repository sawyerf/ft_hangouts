package com.example.ft_hangouts.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.ft_hangouts.R;

import java.util.ArrayList;
import java.util.List;

public class ContactHelper extends SQLiteOpenHelper {
    private final Context context;
    private static final String DATABASE_NAME = "Contact.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "contacts";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_FIRSTNAME = "firstname";
    private static final String COLUMN_LASTNAME = "lastname";
    private static final String COLUMN_PHONE = "phone_number";
    private static final String COLUMN_MAIL = "mail";
    private static final String COLUMN_DESC = "description";

    public ContactHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_FIRSTNAME + " TEXT, " +
                COLUMN_LASTNAME + " TEXT, " +
                COLUMN_PHONE + " TEXT UNIQUE, " +
                COLUMN_MAIL + " TEXT, " +
                COLUMN_DESC + " TEXT" + ");";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public List<Contact> CursorToContact(Cursor cursor) {
        List<Contact> contacts = new ArrayList<>();

        while (cursor.moveToNext()) {
            contacts.add(new Contact(cursor));
        }
        cursor.close();
        return contacts;
    }

    public Contact getContactById(String idContact) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME +
                " WHERE _id=?";

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, new String[]{idContact});
        }
        if (cursor.getCount() != 0) {
            cursor.moveToNext();
            return new Contact(cursor);
        }
        return null;
    }

    public Contact getContactByPhone(String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME +
                " WHERE " + COLUMN_PHONE + "=?";

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, new String[]{phone});
        }
        if (cursor.getCount() != 0) {
            cursor.moveToNext();
            return new Contact(cursor);
        }
        return null;
    }

    public List<Contact> getContacts() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME +
                " ORDER BY " + COLUMN_LASTNAME + " ASC";

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        return CursorToContact(cursor);
    }

    public Boolean addContact(String firstname, String lastname, String phone, String mail, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_FIRSTNAME, firstname);
        cv.put(COLUMN_LASTNAME, lastname);
        cv.put(COLUMN_PHONE, phone);
        cv.put(COLUMN_MAIL, mail);
        cv.put(COLUMN_DESC, description);

        long result = db.insert(TABLE_NAME, null, cv);
        if (result == -1) {
            Toast.makeText(context, R.string.fail_add_contact, Toast.LENGTH_SHORT).show();
            return false;
        } else {
            Toast.makeText(context, R.string.add_success, Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    public Boolean upContact(String id_contact, String firstname, String lastname, String phone, String mail, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_FIRSTNAME, firstname);
        cv.put(COLUMN_LASTNAME, lastname);
        cv.put(COLUMN_PHONE, phone);
        cv.put(COLUMN_MAIL, mail);
        cv.put(COLUMN_DESC, description);

        long result = db.update(TABLE_NAME, cv, "_id=?", new String[]{id_contact});
        if (result == -1) {
            Toast.makeText(context, R.string.fail_up_contact, Toast.LENGTH_SHORT).show();
            return false;
        } else {
            Toast.makeText(context, R.string.up_success, Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    public void delContact(String id_contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        long result = db.delete(TABLE_NAME, "_id=?", new String[]{id_contact});
        if (result == -1) {
            Toast.makeText(context, R.string.fail_del_contact, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, R.string.del_success, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }
}
