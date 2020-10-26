package com.example.mymovies.todo.movies

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.mymovies.R
import kotlinx.android.synthetic.main.fragment_home.*
import com.example.mymovies.core.TAG

class HomeFragment : Fragment() {
    public lateinit var moviesAdapter: MoviesAdapter
    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v(TAG, "onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.v(TAG, "onActivityCreated")
        setupMovieList()

        fab.setOnClickListener {
            Log.v(TAG, "add new movie")
            findNavController().navigate(R.id.MovieEditFragment)
        }
    }

    private fun setupMovieList() {
        moviesAdapter = MoviesAdapter(this)
        movie_list.adapter = moviesAdapter
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        viewModel.movies.observe(viewLifecycleOwner, { movies ->
            Log.v(TAG, "update movies")
            moviesAdapter.movies = movies
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
        viewModel.loadMovies()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v(TAG, "onDestroy")
    }
}