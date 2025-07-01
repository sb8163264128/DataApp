package com.unplugged.data

import com.unplugged.data.model.DeviceItem

sealed class DataResult<out T> {
    data class Success<T>(val data: T) : DataResult<T>()
    data class Error(val exception: Exception) : DataResult<Nothing>()
}

interface DeviceRepository {
    suspend fun getDeviceList(forceRefresh: Boolean = false): DataResult<List<DeviceItem>>
    suspend fun getDeviceDetails(
        deviceId: String,
        forceRefresh: Boolean = false
    ): DataResult<DeviceItem>
}