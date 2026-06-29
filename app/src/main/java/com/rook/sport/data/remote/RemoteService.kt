package com.rook.sport.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Url
import java.util.concurrent.TimeUnit

interface RemoteApi {
    /** يجلب ملف التهيئة من أي رابط مباشر (GitHub raw / استضافتك). */
    @GET
    suspend fun getConfig(@Url url: String): RemoteConfig
}

object RemoteService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .build()

    val api: RemoteApi by lazy {
        Retrofit.Builder()
            // base ثابت، الرابط الحقيقي يُمرَّر عبر @Url
            .baseUrl("https://raw.githubusercontent.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RemoteApi::class.java)
    }
}
