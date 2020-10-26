package com.example.mymovies.todo.data.remote

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import com.example.mymovies.todo.data.Movie

object MovieApi {
    private const val URL = "http://192.168.0.101:3000/"

    interface Service {
        @GET("/movie")
        suspend fun find(): List<Movie>

        @GET("/movie/{id}")
        suspend fun read(@Path("id") movieId: String): Movie;

        @Headers("Content-Type: application/json")
        @POST("/movie")
        suspend fun create(@Body movie: Movie): Movie

        @Headers("Content-Type: application/json")
        @PUT("/movie/{id}")
        suspend fun update(@Path("id") movieId: String, @Body movie: Movie): Movie
    }

    private val client: OkHttpClient = OkHttpClient.Builder().build()

    private var gson = GsonBuilder()
        .setLenient()
        .create()

    private val retrofit = Retrofit.Builder()
        .baseUrl(URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(client)
        .build()

    val service: Service = retrofit.create(Service::class.java)
}