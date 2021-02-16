package com.aatech_aplha.news.data

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import android.util.Log
import androidx.lifecycle.LiveData
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ConnectionLiveData @Inject constructor(@ApplicationContext val context: Context) :
    LiveData<Boolean>() {
    private val TAG = "ConnectionLiveData"
    private lateinit var networkCallbacks: ConnectivityManager.NetworkCallback
    private val cm = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager


    override fun onActive() {
        super.onActive()
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
                cm.registerDefaultNetworkCallback(connectivityManagerCallback())
            }
        }
    }


    private fun connectivityManagerCallback(): ConnectivityManager.NetworkCallback {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            postValue(true)
            networkCallbacks = object : ConnectivityManager
            .NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    postValue(false)
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    postValue(true)
                    Log.d(TAG, "onLost")
                }

            }
            return networkCallbacks
        } else {
            throw IllegalAccessError("Error")
        }
    }
}