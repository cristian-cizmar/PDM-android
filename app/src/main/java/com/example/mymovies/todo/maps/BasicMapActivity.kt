package com.example.mymovies.todo.maps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mymovies.R
import com.example.mymovies.todo.data.local.LocationHelper

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class BasicMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basic_map)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val position = LocationHelper.getPinLocation()
        val latLng = LatLng(position.first.toDouble(), position.second.toDouble())
        mMap.addMarker(MarkerOptions().position(latLng).title("My Marker"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
    }
}