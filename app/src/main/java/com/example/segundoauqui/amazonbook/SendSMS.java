package com.example.segundoauqui.amazonbook;

import android.Manifest;
import android.app.DialogFragment;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * Created by auquisegundo on 8/13/2017.
 */

public class SendSMS extends DialogFragment {
    EditText etNumber, etMessage;
    ImageButton btnSMS;
    private  static final int REQUEST_SEND_SMS = 1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sending_sms, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        etMessage = (EditText) view.findViewById(R.id.etMessage);
        etNumber = (EditText) view.findViewById(R.id.etNumber);
        btnSMS = (ImageButton) view.findViewById(R.id.btnSMS);
        getDialog().setTitle("Send Message");
        btnSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
                dismiss();
                etMessage.setText("");
                etNumber.setText("");
                showNotification();
            }
        });

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case REQUEST_SEND_SMS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getActivity(), "Permission Granted", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getActivity(), "No Permission Granted", Toast.LENGTH_SHORT).show();
                }
        }

    }

    private void sendMessage() {
        // Get the default instance of the SmsManager
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(etNumber.getText().toString(), null, etMessage.getText().toString(), null, null);
            Toast.makeText(getActivity(), "Your sms was successfully sent!",
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Message failed to send", Toast.LENGTH_SHORT).show();
        }

    }
    public void showNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity());
        builder.setSmallIcon(R.drawable.ic_message);
        builder.setContentTitle("Message sent");
        builder.setContentText("A message has being sent");
        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
        Toast.makeText(getActivity(), " Alert.. Notification Sent!!!", Toast.LENGTH_SHORT).show();
    }
}
