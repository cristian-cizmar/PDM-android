package com.example.mymovies.todo.data

import androidx.lifecycle.*
import com.example.mymovies.todo.data.local.MovieDao
import com.example.mymovies.core.Result
import com.example.mymovies.todo.data.remote.MovieApi

class MovieRepository(private val movieDao: MovieDao) {

    var movies =  MutableLiveData<List<Movie>>().apply { postValue(emptyList()) }

    suspend fun refresh(): Result<Boolean> {
        try {
            val moviesApi = MovieApi.service.find()
            movies.value = moviesApi
            for (movie in moviesApi) {
                movieDao.insert(movie)
            }
            return Result.Success(true)
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }

    fun getById(movieId: String): LiveData<Movie> {
        return movieDao.getById(movieId)
    }

    suspend fun save(movie: Movie): Result<Movie> {
        try {
            val createdMovie = MovieApi.service.create(movie)
            movieDao.insert(createdMovie)
            return Result.Success(createdMovie)
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }

    suspend fun update(movie: Movie): Result<Movie> {
        try {
            val updatedMovie = MovieApi.service.update(movie._id, movie)
            movieDao.update(updatedMovie)
            return Result.Success(updatedMovie)
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }

    suspend fun delete(movieId: String): Result<Boolean> {
        try {
            MovieApi.service.delete(movieId)
            movieDao.delete(movieId)
            return Result.Success(true)
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }
}