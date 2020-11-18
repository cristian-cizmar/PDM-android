package com.example.mymovies.todo.data

import android.util.Log
import androidx.lifecycle.*
import com.example.mymovies.MyProperties
import com.example.mymovies.auth.data.AuthRepository
import com.example.mymovies.todo.data.local.MovieDao
import com.example.mymovies.core.Result
import com.example.mymovies.core.TAG
import com.example.mymovies.todo.data.local.DeleteHelper
import com.example.mymovies.todo.data.remote.MovieApi
import com.google.android.material.snackbar.Snackbar

class MovieRepository(val movieDao: MovieDao) {

    var movies = MediatorLiveData<List<Movie>>().apply { postValue(emptyList()) }

    suspend fun refresh(): Result<Boolean> {
        try {
            if (MyProperties.instance.internetActive.value == 1) {
                val moviesApi = MovieApi.service.find()
                movies.value = moviesApi
                for (movie in moviesApi) {
                    movie.ownerUsername = AuthRepository.getUsername()
                    movieDao.insert(movie)
                }
                return Result.Success(true)
            } else {
                movies.addSource(movieDao.getAll(AuthRepository.getUsername())) {
                    movies.value = it
                }
                return Result.Success(true)
            }
        } catch (e: Exception) {
            movies.addSource(movieDao.getAll(AuthRepository.getUsername())) {
                movies.value = it
            }
            return Result.Error(e)
        }
    }

    fun getById(movieId: String): LiveData<Movie> {
        return movieDao.getById(movieId)
    }

    suspend fun save(movie: Movie): Result<Movie> {
        try {
            if (MyProperties.instance.internetActive.value == 1) {
                movie.upToDateWithBackend = true
                movie.backendUpdateType = ""
                val createdMovie = MovieApi.service.create(movie)
                movieDao.insert(createdMovie)
                return Result.Success(createdMovie)
            } else {
                movie.upToDateWithBackend = false
                movie.backendUpdateType = "save"
                MyProperties.instance.snackbarMessage.postValue("The save won't be sent to the server until you have an active internet connection")
                Log.d(TAG, "save: no internet connection (manual check)")
                movieDao.insert(movie)
                return Result.Success(movie)
            }
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }

    suspend fun update(movie: Movie): Result<Movie> {
        try {
            if (MyProperties.instance.internetActive.value == 1) {
                movie.upToDateWithBackend = true
                movie.backendUpdateType = ""
                val updatedMovie = MovieApi.service.update(movie._id, movie)
                movieDao.update(updatedMovie)
                return Result.Success(updatedMovie)
            } else {
                movie.upToDateWithBackend = false
                movie.backendUpdateType = "update"
                MyProperties.instance.snackbarMessage.postValue("The update won't be sent to the server until you have an active internet connection")
                Log.d(TAG, "update: no internet connection (manual check)")
                movieDao.update(movie)
                return Result.Success(movie)
            }
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }

    suspend fun delete(movieId: String): Result<Boolean> {
        try {
            if (MyProperties.instance.internetActive.value == 1) {
                MovieApi.service.delete(movieId)
                movieDao.delete(movieId)
                return Result.Success(true)
            } else {
                MyProperties.instance.snackbarMessage.postValue("The delete won't be sent to the server until you have an active internet connection")
                Log.d(TAG, "delete: no internet connection (manual check)")
                movieDao.delete(movieId)
                DeleteHelper.addDelete(movieId)
                return Result.Success(true)
            }
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }
}