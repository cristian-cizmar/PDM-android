package com.example.mymovies.todo.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.mymovies.todo.data.Movie

@Dao
interface MovieDao {
    @Query("SELECT * from movies WHERE ownerUsername=:username ORDER BY name ASC")
    fun getAll(username: String): LiveData<List<Movie>>

    @Query("SELECT * FROM movies WHERE _id=:id ")
    fun getById(id: String): LiveData<Movie>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(movie: Movie)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(movie: Movie)

    @Query("DELETE FROM movies")
    suspend fun deleteAll()

    @Query("DELETE FROM movies WHERE _id=:id ")
    suspend fun delete(id: String)
}