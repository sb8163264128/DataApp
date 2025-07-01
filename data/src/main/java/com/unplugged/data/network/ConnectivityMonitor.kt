package com.unplugged.data.network

interface ConnectivityMonitor {
    fun isOnline(): Boolean
}