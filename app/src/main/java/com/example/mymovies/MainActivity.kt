package com.example.mymovies

import android.net.ConnectivityManager
import android.net.Network
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.mymovies.core.TAG
import com.example.mymovies.todo.data.local.DeleteHelper


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        Log.i(TAG, "onCreate")

        DeleteHelper.init(this)
        observeConnectivity()
        observeSnackbar()
    }

    private fun observeConnectivity(){
        val connectivityManager = getSystemService(ConnectivityManager::class.java)

        connectivityManager.registerDefaultNetworkCallback(object :
            ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                Log.d(TAG, "internet onAvailable")
                MyProperties.instance.internetActive.postValue(1)
            }

            override fun onLost(network: Network) {
                Log.d(TAG, "internet onLost")
                MyProperties.instance.internetActive.postValue(2)
            }
        })
    }

    private fun observeSnackbar(){
        MyProperties.instance.snackbarMessage.observe(
            this, Observer {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        )
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