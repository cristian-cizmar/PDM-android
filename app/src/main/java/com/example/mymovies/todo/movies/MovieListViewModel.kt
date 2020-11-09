package com.example.mymovies.todo.movies

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import com.example.mymovies.todo.data.local.MovieDatabase
import com.example.mymovies.core.Result
import com.example.mymovies.core.TAG
import com.example.mymovies.todo.data.Movie
import com.example.mymovies.todo.data.MovieRepository

class MovieListViewModel(application: Application) : AndroidViewModel(application) {
    private val mutableLoading = MutableLiveData<Boolean>().apply { value = false }
    private val mutableException = MutableLiveData<Exception>().apply { value = null }

    val movies: LiveData<List<Movie>>
    val loading: LiveData<Boolean> = mutableLoading
    val loadingError: LiveData<Exception> = mutableException

    val movieRepository: MovieRepository

    init {
        val movieDao = MovieDatabase.getDatabase(application, viewModelScope).movieDao()
        movieRepository = MovieRepository(movieDao)
        movies = movieRepository.movies
    }

    fun refresh() {
        viewModelScope.launch {
            Log.v(TAG, "refresh...");
            mutableLoading.value = true
            mutableException.value = null
            when (val result = movieRepository.refresh()) {
                is Result.Success -> {
                    Log.d(TAG, "refresh succeeded");
                }
                is Result.Error -> {
                    Log.w(TAG, "refresh failed", result.exception);
                    mutableException.value = result.exception
                }
            }
            mutableLoading.value = false
        }
    }
}

