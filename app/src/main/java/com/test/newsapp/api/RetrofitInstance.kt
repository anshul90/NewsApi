package com.test.newsapp.api

import com.test.newsapp.util.Constants.Companion.BASE_API
import com.test.newsapp.util.NetworkConnectionInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {
    companion object {
        operator fun invoke(networkConnectionInterceptor: NetworkConnectionInterceptor): NewsAPI {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)

            val client = OkHttpClient.Builder().addInterceptor(logging)
                .addInterceptor(networkConnectionInterceptor).build()
            return Retrofit.Builder().baseUrl(BASE_API)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client).build().create(NewsAPI::class.java)
        }

    }
}