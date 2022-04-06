package com.example.ft_hangouts.message;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.example.ft_hangouts.controller.ChatActivity;
import com.example.ft_hangouts.controller.MessagesActivity;
import com.example.ft_hangouts.model.MessageHelper;

import java.sql.Timestamp;

public class MySmsReceiver extends BroadcastReceiver {
    private static final String TAG = "DESBARRES";
    public static final String pdu_type = "pdus";

    private MessageHelper dbMessage;

    private void updateActivity(String phone, String content) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        dbMessage.addMessage(content, timestamp.getTime(), "me", phone, dbMessage.ISRECV);
        if (ChatActivity.getInstance() != null) {
            ChatActivity.getInstance().updateMessage(phone, content);
        }
        if (MessagesActivity.getInstance() != null) {
            MessagesActivity.getInstance().fillMessages();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {
        dbMessage = new MessageHelper(context);
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs;
        String format = bundle.getString("format");
        Object[] pdus = (Object[]) bundle.get(pdu_type);
        if (pdus != null) {
            msgs = new SmsMessage[pdus.length];
            for (int i = 0; i < msgs.length; i++) {
                msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                updateActivity(msgs[i].getOriginatingAddress(), msgs[i].getMessageBody());
                Log.d(TAG, "onReceive: " + msgs[i].getOriginatingAddress() + " : " + msgs[i].getMessageBody());
            }
        }
    }
}