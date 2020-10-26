package com.example.mymovies.todo.data

data class Movie(
    var id: String,
    var name: String?,
    var length: Int?,
    var releaseDate: String?,
    var isWatched: Boolean?
) {
    override fun toString(): String = "$id ${name.orEmpty()} $length $releaseDate $isWatched"
}