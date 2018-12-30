package com.am.searchplayground.di

import android.app.Application
import android.content.Context
import com.am.searchplayground.SEARCH_PLACES_URL
import com.am.searchplayground.network.SearchApi
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import rx.schedulers.Schedulers
import javax.inject.Singleton


@Module
class AppModule(private val application: Application) {

    @Singleton
    @Provides
    fun providesContext(): Context = application.applicationContext
}


@Module
class NetworkModule {

    @Singleton
    @Provides
    fun providesOKHttpModule(): OkHttpClient {
        val builder = OkHttpClient.Builder()

        val httpLoggingInterceptor = HttpLoggingInterceptor()

        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        builder.networkInterceptors().add(httpLoggingInterceptor)
        return builder.build()
    }


    @Singleton
    @Provides
    fun providesRetrofit(httpClient: OkHttpClient): Retrofit {
        val retrofit = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()))
            .client(httpClient)
            .baseUrl(SEARCH_PLACES_URL)
        return retrofit.build()
    }

    @Singleton
    @Provides
    fun providesSearchApi(retrofit: Retrofit): SearchApi = retrofit.create(SearchApi::class.java)

}

@Module
class StorageModule