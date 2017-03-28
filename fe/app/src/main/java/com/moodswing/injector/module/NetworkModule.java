package com.moodswing.injector.module;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.moodswing.injector.scope.PerApplication;
import com.moodswing.mvp.network.Repository;
import com.moodswing.rest.MoodSwingRestRepository;

import java.util.concurrent.TimeUnit;

import dagger.Module2;
import dagger.Provides2;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by daniel on 13/02/17.
 */

@Module2
public class NetworkModule {

    private String apiEndpointUrl = "http://128.189.247.173:3000";

    @Provides2
    @PerApplication
    Repository provideRepository(Retrofit retrofit) {return new MoodSwingRestRepository(retrofit);}

    @Provides2
    @PerApplication
    Retrofit provideRetrofit() {
        // Gson
        String endpointUrl = apiEndpointUrl;
        Gson gson = new GsonBuilder().create();
        GsonConverterFactory gsonConverterFactory = GsonConverterFactory.create(gson);

        // Logging
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Client
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .retryOnConnectionFailure(true)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        // Rx
        RxJava2CallAdapterFactory rxJavaCallAdapterFactory = RxJava2CallAdapterFactory.create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(endpointUrl)
                .client(client)
                .addConverterFactory(gsonConverterFactory)
                .addCallAdapterFactory(rxJavaCallAdapterFactory)
                .build();
        return retrofit;
    }
}
