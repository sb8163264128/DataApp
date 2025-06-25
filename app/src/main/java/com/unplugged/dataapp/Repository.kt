package com.unplugged.dataapp

import com.unplugged.dataapp.network.model.DeviceDetails
import com.unplugged.dataapp.network.model.DeviceListItem

sealed class DataResult<out T> {
    data class Success<T>(val data: T) : DataResult<T>()
    data class Error(val exception: Exception) : DataResult<Nothing>()
}

interface DeviceRepository {
    suspend fun getDeviceListFromApi(): DataResult<List<DeviceListItem>>
    suspend fun getDeviceDetailsFromApi(deviceId: String): DataResult<DeviceDetails>
}