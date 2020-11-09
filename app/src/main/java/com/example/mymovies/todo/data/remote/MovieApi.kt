package com.example.mymovies.todo.data.remote

import retrofit2.http.*
import com.example.mymovies.core.Api
import com.example.mymovies.todo.data.Movie
import retrofit2.Response

object MovieApi {
    interface Service {
        @GET("/api/movie")
        suspend fun find(): List<Movie>

        @GET("/api/movie/{id}")
        suspend fun read(@Path("id") movieId: String): Movie;

        @Headers("Content-Type: application/json")
        @POST("/api/movie")
        suspend fun create(@Body movie: Movie): Movie

        @Headers("Content-Type: application/json")
        @PUT("/api/movie/{id}")
        suspend fun update(@Path("id") movieId: String, @Body movie: Movie): Movie

        @DELETE("/api/movie/{id}")
        suspend fun delete(@Path("id") movieId: String): Response<Unit>
    }

    val service: Service = Api.retrofit.create(Service::class.java)
}