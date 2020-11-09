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
import com.example.mymovies.todo.data.Movie
import com.example.mymovies.todo.movie.MovieEditFragment
import kotlinx.android.synthetic.main.view_movie.view.*
import android.widget.Filter;

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

    fun searchAndFilter(s: String, watched: String): MutableList<Movie> {
        val filteredList: MutableList<Movie> = ArrayList()
        val s2 = s.toLowerCase().trim()
        for (item in movies) {
            if (s2.isNotEmpty() && !item.name.contains(s2))
                continue
            if(watched.isNotEmpty() && item.isWatched.toString()!=watched)
                continue
            filteredList.add(item)
        }
        return filteredList
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
