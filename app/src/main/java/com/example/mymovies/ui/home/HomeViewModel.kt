package com.example.mymovies.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymovies.data.model.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import kotlin.random.Random

class HomeViewModel : ViewModel() {
    private val mutableItems = MutableLiveData<List<Movie>>().apply { value = emptyList() }
    val items: LiveData<List<Movie>> = mutableItems

    init {
        viewModelScope.launch {
            while (true) {
                simulateNewItemNotification()
                addNewItem()
            }
        }
    }

    fun addNewItem() {
        val list = mutableListOf<Movie>()
        list.addAll(mutableItems.value!!)
        list.add(generateRandomMovie())
        mutableItems.value = list
    }

    private fun generateRandomMovie() =
        Movie(
            ('a'..'z').map { it }.shuffled().subList(0, 10).joinToString(""),
            (90..150).random(),
            LocalDate.now(),
            Random.nextBoolean()
        )

    private suspend fun simulateNewItemNotification() = withContext(Dispatchers.Default) {
        delay(1500)
    }
}
