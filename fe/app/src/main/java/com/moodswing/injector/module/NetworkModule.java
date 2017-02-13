package com.moodswing.injector.module;

import com.moodswing.BuildConfig;
import com.moodswing.injector.scope.PerApplication;
import com.moodswing.rest.MoodSwingRestRepository;
import com.mvp.network.Repository;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

/**
 * Created by daniel on 13/02/17.
 */

@Module
public class NetworkModule {

    @Provides
    @PerApplication
    Repository provideRepository(Retrofit retrofit) {return new MoodSwingRestRepository(retrofit);}

    @Provides
    @PerApplication
    Retrofit provideRetrofit() {
        String endpointUrl = BuildConfig.apiEndpointUrl;
        // TODO: Gson ...
        // TODO: Client ...
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(endpointUrl)
                .build();
        return retrofit;
    }
}
