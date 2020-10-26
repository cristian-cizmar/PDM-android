package com.example.mymovies.todo.movies

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.mymovies.todo.data.MovieRepository
import com.example.mymovies.core.TAG
import com.example.mymovies.todo.data.Movie

class HomeViewModel : ViewModel() {
    private val mutableMovies = MutableLiveData<List<Movie>>().apply { value = emptyList() }
    private val mutableLoading = MutableLiveData<Boolean>().apply { value = false }
    private val mutableException = MutableLiveData<Exception>().apply { value = null }

    val movies: LiveData<List<Movie>> = mutableMovies
    val loading: LiveData<Boolean> = mutableLoading
    val loadingError: LiveData<Exception> = mutableException

    fun loadMovies() {
        viewModelScope.launch {
            Log.v(TAG, "loadMovies...");
            mutableLoading.value = true
            mutableException.value = null
            try {
                mutableMovies.value = MovieRepository.loadAll()
                Log.d(TAG, "loadMovies succeeded");
                mutableLoading.value = false
            } catch (e: Exception) {
                Log.w(TAG, "loadMovies failed", e);
                mutableException.value = e
                mutableLoading.value = false
            }
        }
    }
}
