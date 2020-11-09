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
import kotlinx.android.synthetic.main.view_movie.view.*
import com.example.mymovies.core.TAG
import com.example.mymovies.todo.data.Movie
import com.example.mymovies.todo.movie.MovieEditFragment

class MovieListAdapter(
    private val fragment: Fragment
) : RecyclerView.Adapter<MovieListAdapter.ViewHolder>() {

    var movies = emptyList<Movie>()
        set(value) {
            field = value
            notifyDataSetChanged();
        }

    private var onMovieClick: View.OnClickListener;

    init {
        onMovieClick = View.OnClickListener { view ->
            val movie = view.tag as Movie
            fragment.findNavController().navigate(R.id.fragment_movie_edit, Bundle().apply {
                putString(MovieEditFragment.ITEM_ID, movie._id)
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
        Log.v(TAG, "onBindViewHolder $position")
        val movie = movies[position]
        holder.itemView.tag = movie
        holder.textView.text = movie.name
        holder.length.text = movie.length.toString()
        holder.releaseDate.date.text = movie.releaseDate
        holder.isWatched.isWatched.text = movie.isWatched.toString()
        holder.itemView.setOnClickListener(onMovieClick)
    }

    override fun getItemCount() = movies.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.text
        val length: TextView = view.length
        val releaseDate: TextView = view.date
        val isWatched: TextView = view.isWatched
    }
}
