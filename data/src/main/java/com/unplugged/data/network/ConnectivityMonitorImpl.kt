package com.unplugged.data.network

import android.net.ConnectivityManager
import android.net.NetworkCapabilities

class ConnectivityMonitorImpl(private val connectivityManager: ConnectivityManager) :
    ConnectivityMonitor {
    override fun isOnline(): Boolean {
        val activeNetwork = connectivityManager.activeNetwork ?: return false

        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
            ?: return false

        return when {
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) -> true

            else -> false
        }
    }
}