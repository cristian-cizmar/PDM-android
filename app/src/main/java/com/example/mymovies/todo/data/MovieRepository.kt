package com.example.mymovies.todo.data

import android.util.Log
import com.example.mymovies.core.TAG
import com.example.mymovies.todo.data.remote.MovieApi

object MovieRepository {
    private var cachedMovies: MutableList<Movie>? = null;
    private lateinit var functionHolder: FunctionHolder

    fun setFunctionHolder(functionHolder: FunctionHolder) {
        this.functionHolder = functionHolder
    }

    suspend fun loadAll(): List<Movie> {
        Log.i(TAG, "loadAll")
        if (cachedMovies != null) {
            return cachedMovies as List<Movie>;
        }
        cachedMovies = mutableListOf()
        val movie = MovieApi.service.find()
        cachedMovies?.addAll(movie)

        return cachedMovies as List<Movie>
    }

    suspend fun load(movieId: String): Movie {
        Log.i(TAG, "load")
        val movie = cachedMovies?.find { it.id == movieId }
        if (movie != null) {
            return movie
        }
        return MovieApi.service.read(movieId)
    }

    suspend fun save(movie: Movie): Movie {
        Log.i(TAG, "save")
        val createdMovie = MovieApi.service.create(movie)
        return createdMovie
    }

    fun saveLocally(movie: Movie): Movie {
        Log.i(TAG, "save")
        cachedMovies?.add(movie)
        functionHolder.function()
        return movie
    }

    suspend fun update(movie: Movie): Movie {
        Log.i(TAG, "update")
        cachedMovies?.remove(movie)
        val updatedMovie = MovieApi.service.update(movie.id, movie)
        return updatedMovie
    }
}