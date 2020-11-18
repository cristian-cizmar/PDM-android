package com.example.mymovies.todo

import android.content.Context
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.work.*
import com.example.mymovies.MyProperties
import com.example.mymovies.todo.data.MovieRepoHelper
import com.example.mymovies.todo.data.local.DeleteHelper
import kotlinx.coroutines.launch

class RepoWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        when {
            inputData.getString("operation") == "save" -> MovieRepoHelper.saveNewVersion()
            inputData.getString("operation") == "update" -> MovieRepoHelper.updateNewVersion()
            inputData.getString("operation") == "delete" -> deleteIfNeeded()
            else -> return Result.failure()
        }
        return Result.success()
    }

    private fun deleteIfNeeded() {
        val deleteValue = DeleteHelper.getDeleteAndClear()
        if (deleteValue != "") {
            deleteValue.split(",").forEach {
                MovieRepoHelper.setMovieToBeDeletedId(it)
                MovieRepoHelper.deleteMovie()
            }
        }
    }
}