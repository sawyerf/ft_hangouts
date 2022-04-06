package com.example.ft_hangouts.model;

import android.database.Cursor;
import android.util.Log;

public class Message {
    public Integer idMessage;
    public String content;
    public long date;
    public String other;
    public String me;
    public Boolean direction;

    public static final Boolean ISRECV = false;
    public static final Boolean ISSEND = true;

    public Message(Cursor cursor) {
        if (cursor != null) {
            idMessage = cursor.getInt(0);
            content = cursor.getString(1);
            date = cursor.getLong(2);
            other = cursor.getString(4);
            me = cursor.getString(5);
            direction = cursor.getInt(3) > 0;
            // Log.d("DESBARRES", "Other: " + other);
            // Log.d("DESBARRES", "ME: " + me);
            // Log.d("DESBARRES", "direction: " + direction);
        }
    }
}