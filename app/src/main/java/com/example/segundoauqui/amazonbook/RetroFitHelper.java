package com.example.segundoauqui.amazonbook;

import android.content.Context;

import com.example.segundoauqui.amazonbook.Model.Book;

import java.util.ArrayList;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;


public class RetroFitHelper {
    public static final String BASE_URL = "http://de-coding-test.s3.amazonaws.com/";
    public static final String PATH = "books.json";
    private static final String CACHE_CONTROL = "Cache-Control";
    private static long SIZE_OF_CACHE = 10 * 1024 * 1024; // 10 MB
    private static Context mContext;

    public static Retrofit create() {

        int cacheSize = 10 * 1024 * 1024; // 10 MB
        Cache cache = new Cache(android.os.Environment.getExternalStorageDirectory(), cacheSize);
        OkHttpClient okHttpClient = new OkHttpClient.Builder().cache(cache)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }

    public static Call<ArrayList<Book>> getBooks() {
        Retrofit retrofit = create();
        BookService bookService = retrofit.create(BookService.class);
        return bookService.getBooksData();
    }

    public interface BookService {
        @GET(PATH)
        Call<ArrayList<Book>> getBooksData();
    }
}
