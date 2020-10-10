package com.example.mymovies.ui.home.recyclerView

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.mymovies.R
import com.example.mymovies.data.model.Movie
import com.example.mymovies.extensions.TAG
import kotlinx.android.synthetic.main.view_movie.view.*

class MoviesAdapter(
    private val fragment: Fragment
) : RecyclerView.Adapter<MoviesAdapter.ViewHolder>() {

    var items = emptyList<Movie>()
        set(value) {
            field = value
            notifyDataSetChanged();
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_movie, parent, false)
        Log.v(TAG, "onCreateViewHolder")
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.v(TAG, "onBindViewHolder $position")
        val item = items[position]
        with(holder) {
            nameView.text = item.name
            dateView.text = item.releaseDate?.year.toString()
            lengthView.text = "${item.length}m"
            isWatchedView.text = if (item.isWatched == true) "watched" else "not watched"
        }
    }

    override fun getItemCount() = items.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameView: TextView = view.name
        val dateView: TextView = view.date
        val lengthView: TextView = view.length
        val isWatchedView: TextView = view.isWatched
    }
}
