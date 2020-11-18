package com.example.mymovies.todo.data

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.mymovies.MyProperties
import com.example.mymovies.core.Result
import com.example.mymovies.core.TAG
import com.example.mymovies.todo.data.remote.MovieApi
import kotlinx.coroutines.launch

object MovieRepoHelper {
    var movieRepository: MovieRepository? = null
    private var movie: Movie? = null
    private var viewLifecycleOwner: LifecycleOwner? = null
    private var movieToBeDeletedId: String? = null

    fun setMovieRepo(movieParam: MovieRepository) {
        this.movieRepository = movieParam
    }

    fun setMovie(movieParam: Movie) {
        this.movie = movieParam
    }

    fun setViewLifecycleOwner(viewLifecycleOwnerParam: LifecycleOwner) {
        viewLifecycleOwner = viewLifecycleOwnerParam
    }

    fun setMovieToBeDeletedId(id: String) {
        movieToBeDeletedId = id
    }

    fun saveNewVersion() {
        viewLifecycleOwner!!.lifecycleScope.launch {
            saveNewVersionHelper()
        }
    }

    private suspend fun saveNewVersionHelper(): Result<Movie> {
        try {
            if (MyProperties.instance.internetActive.value == 1) {
                Log.d(TAG, "saveNewVersionHelper")
                movie!!.upToDateWithBackend = true
                movie!!.backendUpdateType = ""
                val createdMovie = MovieApi.service.create(movie!!)
                movieRepository!!.movieDao.deleteByName(createdMovie.name)
                movieRepository!!.movieDao.insert(createdMovie)
                MyProperties.instance.snackbarMessage.postValue("The save was registered on the server")
                return Result.Success(createdMovie)
            } else {
                Log.d(TAG, "internet still not working...")
                return Result.Error(Exception("internet still not working..."))
            }
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }

    fun updateNewVersion() {
        viewLifecycleOwner!!.lifecycleScope.launch {
            updateNewVersionHelper()
        }
    }

    private suspend fun updateNewVersionHelper(): Result<Movie> {
        try {
            if (MyProperties.instance.internetActive.value == 1) {
                Log.d(TAG, "updateNewVersionHelper")
                movie!!.upToDateWithBackend = true
                movie!!.backendUpdateType = ""
                val updatedMovie = MovieApi.service.update(movie!!._id, movie!!)
                movieRepository!!.movieDao.update(updatedMovie)
                MyProperties.instance.snackbarMessage.postValue("The update was registered on the server")
                return Result.Success(updatedMovie)
            } else {
                Log.d(TAG, "internet still not working...")
                return Result.Error(Exception("internet still not working..."))
            }
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }

    fun deleteMovie(){
        viewLifecycleOwner!!.lifecycleScope.launch {
            deleteMovieHelper()
        }
    }

    private suspend fun deleteMovieHelper(){
        Log.d(TAG, "deleteMovieHelper")
        movieRepository!!.delete(movieToBeDeletedId!!)
        MyProperties.instance.snackbarMessage.postValue("The delete was registered on the server")
    }
}