package com.example.mymovies.todo.movies

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.TranslateAnimation
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.work.*
import com.example.mymovies.MyProperties
import com.example.mymovies.R
import kotlinx.android.synthetic.main.fragment_movie_list.*
import com.example.mymovies.auth.data.AuthRepository
import com.example.mymovies.core.TAG
import com.example.mymovies.todo.RepoWorker
import com.example.mymovies.todo.data.MovieRepoHelper
import com.google.android.material.snackbar.Snackbar

class MovieListFragment : Fragment() {
    private lateinit var movieListAdapter: MovieListAdapter
    private lateinit var viewModel: MovieListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v(TAG, "onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_movie_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MovieRepoHelper.setViewLifecycleOwner(viewLifecycleOwner)
        observeInternetConnection()

        val animSet = AnimationSet(true)
        val firstAnimation = TranslateAnimation(
            Animation.ABSOLUTE, 0.0f, Animation.ABSOLUTE, -100.0f,
            Animation.ABSOLUTE, 0.0f, Animation.ABSOLUTE, -100.0f
        )
        firstAnimation.duration = 1000
        animSet.addAnimation(firstAnimation)
        val secondAnimation = TranslateAnimation(
            Animation.ABSOLUTE, 0.0f, Animation.ABSOLUTE, 100.0f,
            Animation.ABSOLUTE, 0.0f, Animation.ABSOLUTE, 100.0f
        )
        secondAnimation.duration = 2000
        secondAnimation.startOffset = 3500
        animSet.addAnimation(secondAnimation)
        fab.startAnimation(animSet)
    }

    private fun observeInternetConnection(){
        MyProperties.instance.internetActive.observe(
            viewLifecycleOwner, Observer {
                if (it == 1) {
                    Snackbar.make(
                        requireActivity().findViewById(android.R.id.content),
                        "Internet connection active",
                        Snackbar.LENGTH_LONG
                    )
                        .setActionTextColor(Color.RED)
                        .show()

                    updateMoviesOnServer()
                } else {
                    Snackbar.make(
                        requireActivity().findViewById(android.R.id.content),
                        "No internet connection",
                        Snackbar.LENGTH_INDEFINITE
                    )
                        .setActionTextColor(Color.RED)
                        .show()
                }
            }
        )
    }

    private fun updateMoviesOnServer() {
        // delete
        val dataParam = Data.Builder().putString("operation", "delete")
        val request = OneTimeWorkRequestBuilder<RepoWorker>()
            .setInputData(dataParam.build())
            .build()
        WorkManager.getInstance(requireContext()).enqueue(request)

        // save & update
        val movies = viewModel.movieRepository.movieDao.getAllSimple(AuthRepository.getUsername())
        movies.forEach { movie ->
            if (movie.upToDateWithBackend == null) {
                movie.upToDateWithBackend = true
            }
            if (!movie.upToDateWithBackend!!) {
                if (movie.backendUpdateType == "save") { // save
                    MovieRepoHelper.setMovie(movie)
                    val dataParam = Data.Builder().putString("operation", "save")
                    val request = OneTimeWorkRequestBuilder<RepoWorker>()
                        .setInputData(dataParam.build())
                        .build()
                    WorkManager.getInstance(requireContext()).enqueue(request)
                } else if (movie.backendUpdateType == "update") { // update
                    MovieRepoHelper.setMovie(movie)
                    val dataParam = Data.Builder().putString("operation", "update")
                    val request = OneTimeWorkRequestBuilder<RepoWorker>()
                        .setInputData(dataParam.build())
                        .build()
                    WorkManager.getInstance(requireContext()).enqueue(request)
                }
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.v(TAG, "onActivityCreated")
        if (!AuthRepository.isLoggedIn(requireContext())) {
            Log.d(TAG, "is not logged in")
            findNavController().navigate(R.id.fragment_login)
            return
        }
        setupMovieList()

        fab.setOnClickListener {
            Log.v(TAG, "add new item")
            findNavController().navigate(R.id.action_MovieListFragment_to_MovieEditFragment)
        }

        log_out_button.setOnClickListener {
            Log.v(TAG, "log out")
            AuthRepository.logout()
            findNavController().navigate(R.id.fragment_login)
        }
    }

    private fun setupMovieList() {
        movieListAdapter = MovieListAdapter(this)
        item_list.adapter = movieListAdapter
        viewModel = ViewModelProvider(this).get(MovieListViewModel::class.java)
        viewModel.movies.observe(viewLifecycleOwner, { movie ->
            Log.v(TAG, "update items")
            Log.d(TAG, "setupItemList items length: ${movie.size}")
            movieListAdapter.movies = movie
        })
        viewModel.loading.observe(viewLifecycleOwner, { loading ->
            Log.i(TAG, "update loading")
            progress.visibility = if (loading) View.VISIBLE else View.GONE
        })
        viewModel.loadingError.observe(viewLifecycleOwner, { exception ->
            if (exception != null) {
                Log.i(TAG, "update loading error")
                val message = "Loading exception ${exception.message}"
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
            }
        })
        viewModel.refresh()

        search.doOnTextChanged { text, _, _, _ ->
            viewModel.movies.observe(viewLifecycleOwner, { movie ->
                movieListAdapter.movies = movie
                var watchedFilter = "";
                if (watched.isChecked) watchedFilter = "true"
                if (not_watched.isChecked) watchedFilter = "false"
                movieListAdapter.movies =
                    movieListAdapter.searchAndFilter(search.text.toString(), watchedFilter)
                movieListAdapter.notifyDataSetChanged()
            })
        }

        watched.setOnClickListener {
            viewModel.movies.observe(viewLifecycleOwner, { movie ->
                movieListAdapter.movies = movie
                if (watched.isChecked) {
                    not_watched.isChecked = false
                    movieListAdapter.movies =
                        movieListAdapter.searchAndFilter(search.text.toString(), "true")
                    movieListAdapter.notifyDataSetChanged()
                } else {
                    movieListAdapter.movies =
                        movieListAdapter.searchAndFilter(search.text.toString(), "")
                    movieListAdapter.notifyDataSetChanged()
                }
            })
        }

        not_watched.setOnClickListener {
            viewModel.movies.observe(viewLifecycleOwner, { movie ->
                movieListAdapter.movies = movie
                if (not_watched.isChecked) {
                    watched.isChecked = false
                    movieListAdapter.movies =
                        movieListAdapter.searchAndFilter(search.text.toString(), "false")
                    movieListAdapter.notifyDataSetChanged()
                } else {
                    movieListAdapter.movies =
                        movieListAdapter.searchAndFilter(search.text.toString(), "")
                    movieListAdapter.notifyDataSetChanged()
                }
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v(TAG, "onDestroy")
    }
}