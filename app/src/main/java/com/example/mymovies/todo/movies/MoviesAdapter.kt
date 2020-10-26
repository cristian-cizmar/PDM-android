package com.example.mymovies.todo.movies

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.mymovies.R
import com.example.mymovies.core.TAG
import com.example.mymovies.todo.data.FunctionHolder
import com.example.mymovies.todo.data.Movie
import com.example.mymovies.todo.data.MovieRepository
import com.example.mymovies.todo.movie.MovieEditFragment
import kotlinx.android.synthetic.main.view_movie.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MoviesAdapter(
    private val fragment: Fragment
) : RecyclerView.Adapter<MoviesAdapter.ViewHolder>() {

    var movies = emptyList<Movie>()
        set(value) {
            field = value
            notifyDataSetChanged();
        }

    private var onMovieClick: View.OnClickListener

    init {
        MovieRepository.setFunctionHolder(object : FunctionHolder {
            override fun function() {
                MainScope().launch {
                    withContext(Dispatchers.Main) {
                        notifyDataSetChanged()
                    }
                }
            }
        })
        onMovieClick = View.OnClickListener { view ->
            val movie = view.tag as Movie
            fragment.findNavController().navigate(R.id.MovieEditFragment, Bundle().apply {
                putString(MovieEditFragment.MOVIE_ID, movie.id)
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_movie, parent, false)
        Log.v(TAG, "onCreateViewHolder")
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(holder, position)
    }

    override fun getItemCount() = movies.size

    inner class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        private val text: TextView = view.text
        private val length: TextView = view.length
        private val releaseDate: TextView = view.date
        private val isWatched: TextView = view.isWatched

        fun bind(holder: ViewHolder, position: Int) {
            Log.v(TAG, "onBindViewHolder $position")
            val movie = movies[position]

            with(holder) {
                itemView.tag = movie
                text.text = movie.name
                length.text = movie.length.toString()

                releaseDate.date.text = movie.releaseDate
                isWatched.isWatched.text = movie.isWatched.toString()
                itemView.setOnClickListener(onMovieClick)
            }
        }
    }
}
