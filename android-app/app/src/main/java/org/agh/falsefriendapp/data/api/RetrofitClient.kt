package org.agh.falsefriendapp.data.api

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

object RetrofitClient {
    private const val BASE_URL = "http://192.168.8.101:8000/"
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    val api: ExerciseApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(ExerciseApi::class.java)
    }
}
