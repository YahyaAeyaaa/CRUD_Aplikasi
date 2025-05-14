package com.example.yayaya.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "https://crud-android.vercel.app/api/"

    // Menggunakan OkHttpClient tanpa pengaturan timeout `xkustom
    private val client = OkHttpClient.Builder()
        .build() // Default timeout akan digunakan

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val authService: AuthService = retrofit.create(AuthService::class.java)
}
