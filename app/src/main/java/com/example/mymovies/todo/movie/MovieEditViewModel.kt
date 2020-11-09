package com.example.mymovies.todo.movie

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import com.example.mymovies.todo.data.local.MovieDatabase
import com.example.mymovies.core.Result
import com.example.mymovies.core.TAG
import com.example.mymovies.todo.data.Movie
import com.example.mymovies.todo.data.MovieRepository

class MovieEditViewModel(application: Application) : AndroidViewModel(application) {
    private val mutableFetching = MutableLiveData<Boolean>().apply { value = false }
    private val mutableCompleted = MutableLiveData<Boolean>().apply { value = false }
    private val mutableException = MutableLiveData<Exception>().apply { value = null }

    val fetching: LiveData<Boolean> = mutableFetching
    val fetchingError: LiveData<Exception> = mutableException
    val completed: LiveData<Boolean> = mutableCompleted

    val movieRepository: MovieRepository

    init {
        val movieDao = MovieDatabase.getDatabase(application, viewModelScope).movieDao()
        movieRepository = MovieRepository(movieDao)
    }

    fun getMovieById(movieId: String): LiveData<Movie> {
        Log.v(TAG, "getMovieById...")
        return movieRepository.getById(movieId)
    }

    fun saveOrUpdateMovie(movie: Movie) {
        viewModelScope.launch {
            Log.v(TAG, "saveOrUpdateMovie...");
            mutableFetching.value = true
            mutableException.value = null
            val result: Result<Movie>
            if (movie._id.isNotEmpty()) {
                result = movieRepository.update(movie)
            } else {
                result = movieRepository.save(movie)
            }
            when (result) {
                is Result.Success -> {
                    Log.d(TAG, "saveOrUpdateItem succeeded");
                }
                is Result.Error -> {
                    Log.w(TAG, "saveOrUpdateItem failed", result.exception);
                    mutableException.value = result.exception
                }
            }
            mutableCompleted.value = true
            mutableFetching.value = false
        }
    }

    fun deleteItem(movieId: String) {
        viewModelScope.launch {
            Log.v(TAG, "deleteItem...");
//            mutableFetching.value = true
//            mutableException.value = null
            val result = movieRepository.delete(movieId)
            when (result) {
                is Result.Success -> {
                    Log.d(TAG, "deleteItem succeeded");
                }
                is Result.Error -> {
                    Log.w(TAG, "deleteItem failed", result.exception);
                    mutableException.value = result.exception
                }
            }
//            mutableCompleted.value = true
//            mutableFetching.value = false
        }
    }
}