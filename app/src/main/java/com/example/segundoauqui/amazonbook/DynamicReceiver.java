package com.example.segundoauqui.amazonbook;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class DynamicReceiver extends BroadcastReceiver {


    private static final String TAG = "BroadcastReceiver";
    Bundle results;
    String hierarchy;

    public DynamicReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case Intent.ACTION_AIRPLANE_MODE_CHANGED:
                results = getResultExtras(true);
                hierarchy = results.getString("hierarchy");
                results.putString("hierarchy", TAG);
                Log.d(TAG, "Airplane");
                Toast.makeText(context, "Broadcast:  Airplane mode has changed ", Toast.LENGTH_SHORT).show();
                break;
            case Intent.ACTION_POWER_CONNECTED:
                results = getResultExtras(true);
                hierarchy = results.getString("hierarchy");
                results.putString("hierarchy", hierarchy + "->" + TAG);
                Log.d(TAG, "Connected");
                Toast.makeText(context, "Broadcast:  Power connected", Toast.LENGTH_SHORT).show();
                break;
            case Intent.ACTION_SCREEN_ON:
                results = getResultExtras(true);
                hierarchy = results.getString("hierarchy");
                results.putString("hierarchy", hierarchy + "->" + TAG);
                Log.d(TAG, "Screen on");
                Toast.makeText(context, "Broadcast:  Screen on", Toast.LENGTH_SHORT).show();
                break;
            case Intent.ACTION_SCREEN_OFF:
                results = getResultExtras(true);
                hierarchy = results.getString("hierarchy");
                results.putString("hierarchy", hierarchy + "->" + TAG);
                Log.d(TAG, "Screen off");
                Toast.makeText(context, "Broadcast:   Screen off ", Toast.LENGTH_SHORT).show();
                break;
            case Intent.ACTION_POWER_DISCONNECTED:
                results = getResultExtras(true);
                hierarchy = results.getString("hierarchy");
                results.putString("hierarchy", hierarchy + "->" + TAG);
                Log.d(TAG, "Disconnected");
                Toast.makeText(context, "Broadcast:   Power disconnected ", Toast.LENGTH_SHORT).show();
                break;

        }
    }
}
