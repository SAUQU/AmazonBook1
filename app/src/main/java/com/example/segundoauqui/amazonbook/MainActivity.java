package com.example.segundoauqui.amazonbook;

import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.segundoauqui.amazonbook.Model.Book;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    RecyclerView rvBooks;
    LinearLayoutManager layoutManager;
    RecyclerView.ItemAnimator itemAnimator;
    ArrayList<Book> book;
    BookAdapter bookAdapter;
    ProgressBar progressBar;
    DynamicReceiver dynamicReceiver;
    IntentFilter intentFilter;
    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = -1; // -1 remove the limit of pages with -1 this should be > 0
    private int currentPage = PAGE_START;
    private int limit = 20;
    String breadcrumb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Amazon Books");
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if(ContextCompat.checkSelfPermission(getBaseContext(), "android.permission.SEND_SMS") != PackageManager.PERMISSION_GRANTED) {
            final int REQUEST_CODE_ASK_PERMISSIONS = 123;
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.SEND_SMS"}, REQUEST_CODE_ASK_PERMISSIONS);
        }
        if(ContextCompat.checkSelfPermission(getBaseContext(), "android.permission.READ_SMS") != PackageManager.PERMISSION_GRANTED) {
            final int REQUEST_CODE_ASK_PERMISSIONS = 123;
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.READ_SMS"}, REQUEST_CODE_ASK_PERMISSIONS);
        }
        dynamicReceiver = new DynamicReceiver();
        rvBooks = (RecyclerView) findViewById(R.id.rvBooks);
        progressBar = (ProgressBar) findViewById(R.id.pgProgess);
        final SendSMS sendSms = new SendSMS();
        final FragmentManager fragmentManager = getFragmentManager();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendSms.show(fragmentManager,"sendSms");
                Snackbar snackbar = Snackbar.make(view,"https://github.com/SAUQU", Snackbar.LENGTH_INDEFINITE)
                        .setAction("GO TO ", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                                startActivity(intent);
                            }
                        }).setActionTextColor(Color.RED);
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.YELLOW);
                snackbar.show();
            }
        });
        layoutManager = new LinearLayoutManager(getApplicationContext());
        itemAnimator = new DefaultItemAnimator();
        bookAdapter = new BookAdapter(this);
        rvBooks.setLayoutManager(layoutManager);
        rvBooks.setItemAnimator(itemAnimator);
        rvBooks.setAdapter(bookAdapter);
        rvBooks.addOnScrollListener(new Pagination(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;

                // mocking network delay for API call
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadNextPage();
                    }
                }, 1000);
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
        loadFirstPage();
    }
    private void loadFirstPage() {
        final Call<ArrayList<Book>> bookCall = RetroFitHelper.getBooks();
        bookCall.enqueue(new Callback<ArrayList<Book>>() {
            @Override
            public void onResponse(Call<ArrayList<Book>> call, Response<ArrayList<Book>> response) {
                book = fetchBooksFirst(response);
                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = sharedPrefs.edit();
                Gson gson = new Gson();
                String json = gson.toJson(book);
                editor.putString("booksList", json);
                editor.commit();
                progressBar.setVisibility(View.GONE);
                ArrayList<Book> booksList = fetchBooksSub(book,currentPage,limit);
                bookAdapter.addAll(booksList);
                if (currentPage != TOTAL_PAGES) bookAdapter.addLoadingFooter();
                else isLastPage = true;
            }

            @Override
            public void onFailure(Call<ArrayList<Book>> call, Throwable t) {
                t.printStackTrace();
                try {
                    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    Gson gson = new Gson();
                    String json = sharedPrefs.getString("booksList", null);
                    Type type = new TypeToken<ArrayList<Book>>(){}.getType();
                    ArrayList<Book> amazonBook = gson.fromJson(json, type);
                    ArrayList<Book> amazonBookSub = fetchBooksSub(amazonBook,currentPage,limit);
                    bookAdapter.addAll(amazonBookSub);
                }catch(Exception ex){}
            }
        });
    }



    private ArrayList<Book> fetchBooksFirst(Response<ArrayList<Book>> response) {
        ArrayList<Book> amazonBook = response.body();
        return amazonBook;
    }

    private ArrayList<Book> fetchBooksSub(ArrayList<Book> amazonBook, int currentPage, int limit) {
        ArrayList<Book> amazonBooksub = new ArrayList<Book> (amazonBook.subList((currentPage-1)*limit,currentPage*limit));
        return amazonBooksub;
    }

    private void loadNextPage() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Gson gson = new Gson();
        String json = sharedPrefs.getString("booksList", null);
        Type type = new TypeToken<ArrayList<Book>>() {}.getType();
        ArrayList<Book> book = gson.fromJson(json, type);
        ArrayList<Book> amazonBookSub = fetchBooksSub(book,currentPage,limit);
        bookAdapter.removeLoadingFooter();
        isLoading = false;
        bookAdapter.addAll(amazonBookSub);

        if (currentPage != TOTAL_PAGES) bookAdapter.addLoadingFooter();
        else isLastPage = true;
    }
    @Override
    protected void onStart() {
        super.onStart();
        intentFilter = new IntentFilter("doSomethingElse");
        intentFilter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        intentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        registerReceiver(dynamicReceiver,intentFilter);


    }
    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(dynamicReceiver);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent("ACTION_AIRPLANE_MODE_CHANGED");
        sendOrderedBroadcast(intent, null, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle results = getResultExtras(true);
                String hierarchy = results.getString("hierarchy");
                System.out.println(hierarchy);
                Log.d(TAG, "Final Receiver");
            }
        }, null, MainActivity.RESULT_OK, null, null);

    }

}
