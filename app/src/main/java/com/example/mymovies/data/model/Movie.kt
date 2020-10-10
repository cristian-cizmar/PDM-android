package com.example.mymovies.data.model

import java.time.LocalDate

data class Movie(
    val name: String?,
    val length: Int?,
    val releaseDate: LocalDate?,
    val isWatched: Boolean?
)