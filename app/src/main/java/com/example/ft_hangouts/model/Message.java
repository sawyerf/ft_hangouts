package com.example.ft_hangouts.model;

import android.database.Cursor;
import android.util.Log;

public class Message {
    public Integer idMessage;
    public String content;
    public Integer date;
    public String sendby;
    public String sendto;

    public Message(Cursor cursor) {
        if (cursor != null) {
            idMessage = cursor.getInt(0);
            content = cursor.getString(1);
            date = cursor.getInt(2);
            sendby = cursor.getString(3);
            Log.d("DESBARRES", "Message: " + sendby);
            sendto = cursor.getString(3);
        }
    }
}