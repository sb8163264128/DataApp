package com.unplugged.dataapp.ui

import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.unplugged.dataapp.DataResult
import com.unplugged.dataapp.DeviceRepository
import com.unplugged.dataapp.network.model.DeviceDetails
import com.unplugged.dataapp.network.model.DeviceListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DeviceProviderViewModel @Inject constructor(
    private val deviceRepository: DeviceRepository,
    private val gson: Gson
) : ViewModel() {

    suspend fun fetchSerializedDeviceList(forceRefresh: Boolean): DataResult<String> {
        return when (val repoResult = deviceRepository.getDeviceList(forceRefresh)) {
            is DataResult.Success -> {
                withContext(Dispatchers.Default) {
                    try {
                        val listType = object : TypeToken<List<DeviceListItem>>() {}.type
                        val jsonString = gson.toJson(repoResult.data, listType)
                        DataResult.Success(jsonString)
                    } catch (e: Exception) {
                        DataResult.Error(
                            Exception(
                                "Serialization failed for device list: ${e.message}",
                                e
                            )
                        )
                    }
                }
            }

            is DataResult.Error -> {
                repoResult
            }
        }
    }

    suspend fun fetchSerializedDeviceDetails(
        deviceId: String,
        forceRefresh: Boolean
    ): DataResult<String> {
        if (deviceId.isBlank()) {
            return DataResult.Error(IllegalArgumentException("Device ID cannot be blank in ViewModel."))
        }
        return when (val repoResult = deviceRepository.getDeviceDetails(deviceId, forceRefresh)) {
            is DataResult.Success -> {
                withContext(Dispatchers.Default) {
                    try {
                        val jsonString = gson.toJson(repoResult.data)
                        DataResult.Success(jsonString)
                    } catch (e: Exception) {
                        DataResult.Error(
                            Exception(
                                "Serialization failed for device details (ID: $deviceId): ${e.message}",
                                e
                            )
                        )
                    }
                }
            }

            is DataResult.Error -> {
                repoResult
            }
        }
    }
}