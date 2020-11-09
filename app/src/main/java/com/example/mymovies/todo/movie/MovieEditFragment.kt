package com.example.mymovies.todo.movie

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
import kotlinx.android.synthetic.main.fragment_movie_edit.*
import com.example.mymovies.core.TAG
import com.example.mymovies.todo.data.Movie

class MovieEditFragment : Fragment() {
    companion object {
        const val ITEM_ID = "ITEM_ID"
    }

    private lateinit var viewModel: MovieEditViewModel
    private var movieId: String? = null
    private var movie: Movie? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v(TAG, "onCreate")
        arguments?.let {
            if (it.containsKey(ITEM_ID)) {
                movieId = it.getString(ITEM_ID).toString()
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.v(TAG, "onCreateView")
        return inflater.inflate(R.layout.fragment_movie_edit, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.v(TAG, "onActivityCreated")
        setupViewModel()
        fab.setOnClickListener {
            Log.v(TAG, "save movie")
            val i = movie
            if (i != null) {
                i.name = movie_name.text.toString()
                i.length = movie_length.text.toString().toInt()
                i.releaseDate = movie_date.text.toString()
                i.isWatched = movie_is_watched.text.toString().toBoolean()
                viewModel.saveOrUpdateMovie(i)
            }
        }

        delete_button.setOnClickListener {
            viewModel.deleteItem(movieId ?: "")
            findNavController().navigate(R.id.fragment_movie_list)
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(MovieEditViewModel::class.java)
        viewModel.fetching.observe(viewLifecycleOwner, { fetching ->
            Log.v(TAG, "update fetching")
            progress.visibility = if (fetching) View.VISIBLE else View.GONE
        })
        viewModel.fetchingError.observe(viewLifecycleOwner, { exception ->
            if (exception != null) {
                Log.v(TAG, "update fetching error")
                val message = "Fetching exception ${exception.message}"
                val parentActivity = activity?.parent
                if (parentActivity != null) {
                    Toast.makeText(parentActivity, message, Toast.LENGTH_SHORT).show()
                }
            }
        })
        viewModel.completed.observe(viewLifecycleOwner, { completed ->
            if (completed) {
                Log.v(TAG, "completed, navigate back")
                findNavController().popBackStack()
            }
        })
        val id = movieId
        if (id == null) {
            movie = Movie("", "", 0, "01-01-1000", false);
        } else {
            viewModel.getMovieById(id).observe(viewLifecycleOwner, {
                Log.v(TAG, "update items")
                if (it != null) {
                    movie = it
                    movie_name.setText(it.name)
                    movie_length.setText(it.length.toString())
                    movie_date.setText(it.releaseDate)
                    movie_is_watched.setText(it.isWatched.toString())
                }
            })
        }
    }
}