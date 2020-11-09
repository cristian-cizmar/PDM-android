package com.example.mymovies.todo.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class Movie(
    @PrimaryKey @ColumnInfo(name = "_id") val _id: String,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "length") var length: Int,
    @ColumnInfo(name = "releaseDate") var releaseDate: String,
    @ColumnInfo(name = "isWatched") var isWatched: Boolean
) {
    override fun toString(): String = name
}