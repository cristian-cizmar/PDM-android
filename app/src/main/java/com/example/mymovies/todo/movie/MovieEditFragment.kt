package com.example.mymovies.todo.movie

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.mymovies.R
import com.example.mymovies.auth.data.AuthRepository
import com.example.mymovies.core.TAG
import com.example.mymovies.todo.data.Movie
import com.example.mymovies.todo.data.local.LocationHelper
import com.example.mymovies.todo.maps.BasicMapActivity
import com.example.mymovies.todo.maps.EventsActivity
import kotlinx.android.synthetic.main.fragment_movie_edit.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MovieEditFragment : Fragment() {
    companion object {
        const val ITEM_ID = "ITEM_ID"
    }

    private lateinit var viewModel: MovieEditViewModel
    private var movieId: String? = null
    private var movie: Movie? = null

    private val REQUEST_PERMISSION = 10
    private val REQUEST_IMAGE_CAPTURE = 1
    lateinit var currentPhotoPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v(TAG, "onCreate")
        arguments?.let {
            if (it.containsKey(ITEM_ID)) {
                movieId = it.getString(ITEM_ID).toString()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkCameraPermission()
        initMovieLocation()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.v(TAG, "onCreateView")
        return inflater.inflate(R.layout.fragment_movie_edit, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.v(TAG, "onActivityCreated")
        setupViewModel()
        fab.setOnClickListener {
            Log.v(TAG, "save movie")
            val i = movie
            if (i != null) {
                i.name = movie_name.text.toString()
                i.length = movie_length.text.toString().toInt()
                i.releaseDate = movie_date.text.toString()
                i.isWatched = movie_is_watched.text.toString().toBoolean()
                viewModel.saveOrUpdateMovie(i)
            }
        }

        delete_button.setOnClickListener {
            viewModel.deleteItem(movieId ?: "")
            findNavController().navigate(R.id.fragment_movie_list)
        }

        btCapturePhoto.setOnClickListener { openCamera() }

        btnLocation.setOnClickListener {
            LocationHelper.setPinLocation(movie?.latitude!!, movie?.longitude!!)
            val intent = Intent(requireContext(), EventsActivity::class.java)
            startActivity(intent)
        }

        txtLocation.setOnClickListener {
            if (movie != null && movie?.latitude != null && movie?.longitude != null) {
                LocationHelper.setPinLocation(movie?.latitude!!, movie?.longitude!!)
                val intent = Intent(requireContext(), BasicMapActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_PERMISSION
            )
        }
    }

    private fun openCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            intent.resolveActivity(requireActivity().packageManager)?.also {
                val photoFile: File? = try {
                    createCapturedPhoto()
                } catch (ex: IOException) {
                    null
                }
                Log.d(TAG, "photofile $photoFile")
                photoFile?.also {
                    val photoURI = FileProvider.getUriForFile(
                        requireContext(),
                        "com.example.mymovies.fileprovider",
                        it
                    )
                    Log.d(TAG, "photoURI: $photoURI");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createCapturedPhoto(): File {
        val timestamp: String = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US).format(Date())
        val storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        var f = File.createTempFile("PHOTO_${timestamp}", ".jpg", storageDir).apply {
            currentPhotoPath = absolutePath
        }
        movie?.imageURI = currentPhotoPath
        return f
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                val uri = Uri.parse(currentPhotoPath)
                ivImage.setImageURI(uri)
            }
        }
    }

    private fun initMovieLocation() {
        val location = LocationHelper.getLocationAndClear()
        if (location.first != 0f || location.second != 0f) {
            txtLocation.text = "${location.first} ${location.second}"
            movie?.latitude = location.first
            movie?.longitude = location.second
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(MovieEditViewModel::class.java)
        viewModel.fetching.observe(viewLifecycleOwner, { fetching ->
            Log.v(TAG, "update fetching")
            progress.visibility = if (fetching) View.VISIBLE else View.GONE
        })
        viewModel.fetchingError.observe(viewLifecycleOwner, { exception ->
            if (exception != null) {
                Log.v(TAG, "update fetching error")
                val message = "Fetching exception ${exception.message}"
                val parentActivity = activity?.parent
                if (parentActivity != null) {
                    Toast.makeText(parentActivity, message, Toast.LENGTH_SHORT).show()
                }
            }
        })
        viewModel.completed.observe(viewLifecycleOwner, { completed ->
            if (completed) {
                Log.v(TAG, "completed, navigate back")
                findNavController().popBackStack()
            }
        })
        val id = movieId
        if (id == null) {
            movie = Movie(
                "",
                "",
                0,
                "01-01-1000",
                false,
                AuthRepository.getUsername(),
                true,
                "",
                0,
                "",
                0f,
                0f
            )
        } else {
            viewModel.getMovieById(id).observe(viewLifecycleOwner, {
                Log.v(TAG, "update items")
                if (it != null) {
                    movie = it
                    movie!!.openTime = Date().time
                    movie_name.setText(it.name)
                    movie_length.setText(it.length.toString())
                    movie_date.setText(it.releaseDate)
                    movie_is_watched.setText(it.isWatched.toString())
                    if (!movie?.imageURI.isNullOrBlank()) {
                        ivImage.setImageURI(Uri.parse(movie?.imageURI))
                        Log.d(TAG, "change image to ${movie?.imageURI}")
                    }
                    txtLocation.text = "${it.latitude} ${it.longitude}"
                }
            })
        }
    }
}