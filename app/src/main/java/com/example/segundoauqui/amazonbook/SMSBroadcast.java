package com.example.segundoauqui.amazonbook;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.example.segundoauqui.amazonbook.Model.SMS;

import org.greenrobot.eventbus.EventBus;

public class SMSBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle intentExtras = intent.getExtras();

        if (intentExtras != null) {
            /* Get Messages */

            Object[] sms = (Object[]) intentExtras.get("pdus");
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[0]);
            //ArrayList<SMS> smsList = new ArrayList<>();
            String smsText = smsMessage.getMessageBody();
            SMS textSMS = new SMS(smsMessage.getOriginatingAddress(),smsText);
            Toast.makeText(context, "SMS From: " + smsMessage.getOriginatingAddress() + ": " + smsText,
                    Toast.LENGTH_SHORT).show();
            EventBus.getDefault().post(textSMS);
        }
    }

}