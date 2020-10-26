package com.example.mymovies

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.mymovies.core.TAG
import com.example.mymovies.todo.data.EchoWebSocketListener
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        Log.i(TAG, "onCreate")

        var client = OkHttpClient()
        val request: Request = Request.Builder().url("ws://192.168.0.101:3000/").build()
        val listener = EchoWebSocketListener(com.example.mymovies.todo.data.MovieRepository)
        val ws: WebSocket = client.newWebSocket(request, listener)
//        client.dispatcher.executorService.shutdown()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}