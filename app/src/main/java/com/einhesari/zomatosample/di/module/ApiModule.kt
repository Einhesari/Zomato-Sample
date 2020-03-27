package com.einhesari.zomatosample.di.module

import android.content.Context
import android.content.res.Resources
import com.einhesari.zomatosample.R
import com.einhesari.zomatosample.network.ApiService
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class ApiModule {

    private val REQUEST_TIMEOUT = 60L
    private val ZOMATO_BASE_URL = "https://developers.zomato.com/"
    private val HEADER_API_KEY = "user-key"
    private val HEADER_CONTENT_TYPE = "Content-Type"
    private val CONTENT_TYPE = "application/json"

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ZOMATO_BASE_URL)
            .client(okHttpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        interceptor: Interceptor
    ): OkHttpClient {
        return OkHttpClient().newBuilder()
            .connectTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .addInterceptor(httpLoggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @Provides
    @Singleton
    fun provideInterceptor(context: Context): Interceptor {
        return Interceptor { chain: Interceptor.Chain ->
            var request = chain.request()
            request = request.newBuilder()
                .addHeader(HEADER_API_KEY, context.getString(R.string.zomato_api_key))
                .addHeader(HEADER_CONTENT_TYPE, CONTENT_TYPE)
                .build()
            chain.proceed(request)
        }

    }
}
