package com.unplugged.dataapp

import com.unplugged.dataapp.network.model.DeviceDetails
import com.unplugged.dataapp.network.model.DeviceListItem

sealed class DataResult<out T> {
    data class Success<T>(val data: T) : DataResult<T>()
    data class Error(val exception: Exception) : DataResult<Nothing>()
}

interface DeviceRepository {
    suspend fun getDeviceList(forceRefresh: Boolean = false): DataResult<List<DeviceListItem>>
    suspend fun getDeviceDetails(
        deviceId: String,
        forceRefresh: Boolean = false
    ): DataResult<DeviceDetails>
}