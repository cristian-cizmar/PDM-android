package com.example.mymovies.todo.movies

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.mymovies.R
import kotlinx.android.synthetic.main.fragment_movie_list.*
import com.example.mymovies.auth.data.AuthRepository
import com.example.mymovies.core.TAG

class MovieListFragment : Fragment() {
    private lateinit var movieListAdapter: MovieListAdapter
    private lateinit var movieModel: MovieListViewModel

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
            findNavController().navigate(R.id.fragment_movie_edit)
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
        movieModel = ViewModelProvider(this).get(MovieListViewModel::class.java)
        movieModel.movies.observe(viewLifecycleOwner, { movie ->
            Log.v(TAG, "update items")
            Log.d(TAG, "setupItemList items length: ${movie.size}")
            movieListAdapter.movies = movie
        })
        movieModel.loading.observe(viewLifecycleOwner, { loading ->
            Log.i(TAG, "update loading")
            progress.visibility = if (loading) View.VISIBLE else View.GONE
        })
        movieModel.loadingError.observe(viewLifecycleOwner, { exception ->
            if (exception != null) {
                Log.i(TAG, "update loading error")
                val message = "Loading exception ${exception.message}"
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
            }
        })
        movieModel.refresh()

        search.doOnTextChanged { text, _, _, _ ->
            movieModel.movies.observe(viewLifecycleOwner, { movie ->
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
            movieModel.movies.observe(viewLifecycleOwner, { movie ->
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
            movieModel.movies.observe(viewLifecycleOwner, { movie ->
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