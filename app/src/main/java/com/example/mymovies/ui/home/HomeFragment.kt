package com.example.mymovies.ui.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.mymovies.R
import com.example.mymovies.extensions.TAG
import com.example.mymovies.ui.home.recyclerView.MoviesAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {
    private lateinit var moviesAdapter: MoviesAdapter
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i(TAG, "onCreateView")
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(TAG, "onViewCreated")

        view.findViewById<Button>(R.id.next_frag_btn).setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        view.findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            Log.v(TAG, "adding a new item")
            viewModel.addNewItem()
        }

        setupItemList()
    }

    private fun setupItemList() {
        moviesAdapter = MoviesAdapter(this)
        movies_list.adapter = moviesAdapter
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        viewModel.items.observe(viewLifecycleOwner, { value ->
            moviesAdapter.items = value
        })
    }
}