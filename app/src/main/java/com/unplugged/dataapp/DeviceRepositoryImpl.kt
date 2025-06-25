package com.unplugged.dataapp

import android.content.Context
import com.unplugged.dataapp.network.RestfulApiService
import com.unplugged.dataapp.network.model.DeviceDetails
import com.unplugged.dataapp.network.model.DeviceListItem
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceRepositoryImpl @Inject constructor(
    private val apiService: RestfulApiService,
    @ApplicationContext private val context: Context
) : DeviceRepository {

    override suspend fun getDeviceListFromApi(): DataResult<List<DeviceListItem>> {
        try {
            val response = apiService.getDeviceList()

            if (response.isSuccessful) {
                val deviceList = response.body()
                if (deviceList != null) {
                    return DataResult.Success(deviceList)
                } else {
                    return DataResult.Error(Exception("API returned successful response with null body."))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown HTTP error"
                val errorMessage = "Network request failed: ${response.code()} - $errorBody"
                return DataResult.Error(Exception(errorMessage))
            }
        } catch (e: IOException) {
            return DataResult.Error(e)
        } catch (e: Exception) {
            return DataResult.Error(e)
        }
    }

    override suspend fun getDeviceDetailsFromApi(deviceId: String): DataResult<DeviceDetails> {
        if (deviceId.isBlank()) {
            return DataResult.Error(IllegalArgumentException("Device ID cannot be blank."))
        }

        try {
            val response = apiService.getDeviceDetails(deviceId)

            if (response.isSuccessful) {
                val deviceDetails = response.body()
                if (deviceDetails != null) {
                    return DataResult.Success(deviceDetails)
                } else {
                    val errorMessage =
                        "getDeviceDetailsFromApi: API returned successful response (2xx) but with a null body for device ID: $deviceId."
                    return DataResult.Error(Exception(errorMessage))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown HTTP error"
                val httpErrorCode = response.code()
                val errorMessage =
                    "getDeviceDetailsFromApi: Network request failed for device ID $deviceId: $httpErrorCode - $errorBody"
                return DataResult.Error(Exception(errorMessage))
            }
        } catch (e: IOException) {
            return DataResult.Error(e)
        } catch (e: Exception) {
            return DataResult.Error(e)
        }
    }
}