package com.example.mymovies.todo.data.local

import android.content.Context
import android.content.SharedPreferences

object DeleteHelper {

    var prefs: SharedPreferences? = null

    fun init(context: Context) {
        prefs = context.getSharedPreferences("com.example.mymovies", Context.MODE_PRIVATE)
    }

    fun addDelete(id: String) {
        var newDeleteValue: String = getDelete()
        if (newDeleteValue != "") newDeleteValue += ",$id"
        else newDeleteValue = id

        val editor = prefs!!.edit()
        editor.putString("delete", newDeleteValue)
        editor.apply()
    }

    private fun getDelete() = prefs!!.getString("delete", "")!!

    fun getDeleteAndClear(): String {
        val deleteValue = prefs!!.getString("delete", "")!!
        val editor = prefs!!.edit()
        editor.putString("delete", "")
        editor.apply()
        return deleteValue
    }
}