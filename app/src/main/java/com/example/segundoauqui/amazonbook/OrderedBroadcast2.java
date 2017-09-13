package com.example.segundoauqui.amazonbook;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by auquisegundo on 20/08/2017.
 */

public class OrderedBroadcast2 extends BroadcastReceiver {

    private static final String TAG=OrderedBroadcast2.class.getSimpleName();

    private static String BREAD_CRUMB = "Breadcrumb";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle=getResultExtras(true);
        String trail=bundle.getString(BREAD_CRUMB);
        trail=(trail==null?"Start->"+TAG:trail+"->"+TAG);
        bundle.putString(BREAD_CRUMB, trail);
        Toast.makeText(context, "BroadCastReceiver2 triggered: "+trail, Toast.LENGTH_LONG).show();
    }
}