package com.example.mymovies

import androidx.lifecycle.MutableLiveData

class MyProperties protected constructor() {
    var internetActive = MutableLiveData(0)
    var snackbarMessage = MutableLiveData<String>()

    companion object {
        private var mInstance: MyProperties? = null

        @get:Synchronized
        val instance: MyProperties
            get() {
                if (null == mInstance) {
                    mInstance = MyProperties()
                }
                return mInstance!!
            }
    }
}