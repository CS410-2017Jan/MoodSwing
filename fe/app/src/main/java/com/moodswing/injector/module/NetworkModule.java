package com.moodswing.injector.module;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.moodswing.injector.scope.PerApplication;
import com.moodswing.rest.MoodSwingRestRepository;
import com.moodswing.mvp.network.Repository;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by daniel on 13/02/17.
 */

@Module
public class NetworkModule {
    private String apiEndpointUrl = "http://172.16.42.14:3000";

    @Provides
    @PerApplication
    Repository provideRepository(Retrofit retrofit) {return new MoodSwingRestRepository(retrofit);}

    @Provides
    @PerApplication
    Retrofit provideRetrofit() {
        // Gson
        String endpointUrl = apiEndpointUrl;
        Gson gson = new GsonBuilder().create(); // TODO: setDateFormat
        GsonConverterFactory gsonConverterFactory = GsonConverterFactory.create(gson);

        // Client
        OkHttpClient client = new OkHttpClient();

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
