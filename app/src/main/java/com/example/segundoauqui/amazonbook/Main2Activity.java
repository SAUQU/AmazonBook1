package com.example.segundoauqui.amazonbook;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.segundoauqui.amazonbook.Model.SMS;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class Main2Activity extends AppCompatActivity {
    WebView web;
    Button btnOrderedBroadcast;
    TextView tvMessage;
    String message;
    private static final String TAG = "Message Activity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Intent intent = getIntent();
        setTitle("GitHub");
        String url = "https://github.com/SAUQU";
        final PackageManager pm = this.getPackageManager();
        web = (WebView) findViewById(R.id.webview);
        btnOrderedBroadcast = (Button) findViewById(R.id.btnOrderedBroadcast);
        tvMessage = (TextView) findViewById(R.id.tvMessage);
        if (savedInstanceState == null) {
            web.loadUrl(url);
        }
        // Enable Javascript
        WebSettings webSettings = web.getSettings();
        webSettings.setJavaScriptEnabled(true);
        // Force links and redirects to open in the WebView instead of in a browser
        web.setWebViewClient(new WebViewClient());
        web.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    WebView webView = (WebView) v;
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_BACK:
                            if (webView.canGoBack()) {
                                webView.goBack();
                                return true;
                            }
                            break;
                    }
                }
                return false;
            }
        });
        btnOrderedBroadcast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setAction("com.br1");

                sendOrderedBroadcast(intent, null, new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        Bundle bundle = getResultExtras(true);
                        String breadcrumb = bundle.getString("brodcast");
                        breadcrumb = breadcrumb + "->" + TAG;
                        //Toast.makeText(context, "On Receive: " + breadcrumb, Toast.LENGTH_LONG).show();
                    }
                }, null, Main2Activity.RESULT_OK, null, null);
            }
        });
        if (!pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY) &&
                !pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY_CDMA)) {
            Toast.makeText(this, "We are sorry, but your device cannot send/receive messages", Toast.LENGTH_LONG).show();
        } else {
            if(ContextCompat.checkSelfPermission(getBaseContext(), "android.permission.READ_SMS") != PackageManager.PERMISSION_GRANTED) {
                final int REQUEST_CODE_ASK_PERMISSIONS = 123;
                ActivityCompat.requestPermissions(Main2Activity.this, new String[]{"android.permission.READ_SMS"}, REQUEST_CODE_ASK_PERMISSIONS);
            }
            ContentResolver contentResolver = getContentResolver();
            Cursor smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null);
            message =  contentResolver.query(Uri.parse("content://sms/inbox"),null,null,null,null).getString(0).toString();
            tvMessage.setText(message);
        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        web.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        web.restoreState(savedInstanceState);
    }
    @Override
    protected void onStart() {
        super.onStart();

        EventBus.getDefault().register(this);
    }


    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SMS smsObject) {
        message = smsObject.getMessage();
        tvMessage.setText(smsObject.getMessage());

        }
}
