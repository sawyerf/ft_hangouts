package com.example.ft_hangouts.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MessageHelper extends SQLiteOpenHelper {
    private Context context;
    private static String DATABASE_NAME = "Message.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "messages";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_CONTENT = "content";
    private static final String COLUMN_DATE  = "date";
    private static final String COLUMN_DIRECTION = "direction";
    private static final String COLUMN_OTHER = "other";
    private static final String COLUMN_ME    = "me";

    public static final Boolean ISRECV = false;
    public static final Boolean ISSEND = true;

    public MessageHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_CONTENT + " TEXT, " +
                    COLUMN_DATE + " INTEGER, " +
                    COLUMN_DIRECTION + " BOOLEAN," +
                    COLUMN_OTHER + " TEXT, " +
                    COLUMN_ME + " TEXT);";
        db.execSQL(query);
    }

    public List<Message> CursorToMessage(Cursor cursor) {
        List<Message> messages = new ArrayList<>();

        while (cursor.moveToNext()) {
            messages.add(new Message(cursor));
        }
        return messages;
    }
    public void addMessage(String content, long date, String me, String other, Boolean direction) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_CONTENT, content);
        cv.put(COLUMN_DATE, date);
        cv.put(COLUMN_OTHER, other);
        cv.put(COLUMN_ME, me);
        cv.put(COLUMN_DIRECTION, direction);

        long result = db.insert(TABLE_NAME, null, cv);
        if (result == -1) {
            Toast.makeText(context, "Failed to add Message", Toast.LENGTH_SHORT).show();
        }
    }

    public List<Message> getMessageByPhone(String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME +
                " WHERE " + COLUMN_OTHER + "=\"" + phone + "\"" +
                " ORDER BY " + COLUMN_DATE + " ASC";

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        return CursorToMessage(cursor);
    }

    public List<Message> getLastMessage() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME +
                " WHERE " + COLUMN_ID + " IN (select max(" + COLUMN_ID + ") FROM messages group by " + COLUMN_OTHER + ")" +
                " ORDER BY " + COLUMN_DATE + " DESC";
        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        return CursorToMessage(cursor);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

}
