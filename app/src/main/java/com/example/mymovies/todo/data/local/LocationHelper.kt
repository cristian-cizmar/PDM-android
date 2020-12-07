package com.example.mymovies.todo.data.local

import android.content.Context
import android.content.SharedPreferences

object LocationHelper {

    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        prefs = context.getSharedPreferences("com.example.mymovies", Context.MODE_PRIVATE)
    }

    fun setLocation(lat: Float, long: Float) {
        val editor = prefs!!.edit()
        editor.putFloat("lat", lat)
        editor.putFloat("long", long)
        editor.apply()
    }

    fun getLocationAndClear(): Pair<Float, Float> {
        val pair = Pair(prefs!!.getFloat("lat", 0f), prefs!!.getFloat("long", 0f))

        val editor = prefs!!.edit()
        editor.putFloat("lat", 0f)
        editor.putFloat("long", 0f)
        editor.apply()

        return pair
    }

    fun setPinLocation(lat: Float, long: Float) {
        val editor = prefs!!.edit()
        editor.putFloat("latPin", lat)
        editor.putFloat("longPin", long)
        editor.apply()
    }

    fun getPinLocation() = Pair(prefs!!.getFloat("latPin", 0f), prefs!!.getFloat("longPin", 0f))
}