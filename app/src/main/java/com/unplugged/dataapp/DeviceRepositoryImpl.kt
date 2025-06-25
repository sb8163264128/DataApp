package com.unplugged.dataapp

import android.content.Context
import android.util.Log
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

    private val TAG = "DeviceRepositoryImpl"

    override suspend fun getDeviceListFromApi(): DataResult<List<DeviceListItem>> {
        try {
            val response = apiService.getDeviceList()

            if (response.isSuccessful) {
                val deviceList = response.body()
                if (deviceList != null) {
                    Log.d(TAG, "Successfully fetched ${deviceList.size} devices.")
                    return DataResult.Success(deviceList)
                } else {
                    Log.e(TAG, "Successful response but device list is null.")
                    return DataResult.Error(Exception("API returned successful response with null body."))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown HTTP error"
                val errorMessage = "Network request failed: ${response.code()} - $errorBody"
                Log.e(TAG, "HTTP error: ${response.code()} - $errorBody")
                return DataResult.Error(Exception(errorMessage))
            }
        } catch (e: IOException) {
            Log.e(TAG, "Network error (IOException): ${e.message}", e)
            return DataResult.Error(e)
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error in getDeviceListFromApi: ${e.message}", e)
            return DataResult.Error(e)
        }
    }

    override suspend fun getDeviceDetailsFromApi(deviceId: String): DataResult<DeviceDetails> {
        Log.d(TAG, "getDeviceDetailsFromApi: Attempting to fetch details for device ID: $deviceId")
        if (deviceId.isBlank()) {
            Log.e(TAG, "getDeviceDetailsFromApi: deviceId is blank.")
            return DataResult.Error(IllegalArgumentException("Device ID cannot be blank."))
        }

        try {
            val response = apiService.getDeviceDetails(deviceId)

            if (response.isSuccessful) {
                val deviceDetails = response.body()
                if (deviceDetails != null) {
                    Log.d(TAG, "getDeviceDetailsFromApi: Successfully fetched details for device ID: $deviceId. Details: ${deviceDetails.id}")
                    return DataResult.Success(deviceDetails)
                } else {
                    val errorMessage = "getDeviceDetailsFromApi: API returned successful response (2xx) but with a null body for device ID: $deviceId."
                    Log.e(TAG, errorMessage)
                    return DataResult.Error(Exception(errorMessage))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown HTTP error"
                val httpErrorCode = response.code()
                val errorMessage = "getDeviceDetailsFromApi: Network request failed for device ID $deviceId: $httpErrorCode - $errorBody"
                Log.e(TAG, "getDeviceDetailsFromApi: HTTP error for device ID $deviceId: $httpErrorCode - $errorBody")
                return DataResult.Error(Exception(errorMessage))
            }
        } catch (e: IOException) {
            val networkErrorMessage = "getDeviceDetailsFromApi: Network error (IOException) while fetching details for device ID $deviceId: ${e.message}"
            Log.e(TAG, networkErrorMessage, e)
            return DataResult.Error(e)
        } catch (e: Exception) {
            val unexpectedErrorMessage = "getDeviceDetailsFromApi: Unexpected error while fetching details for device ID $deviceId: ${e.message}"
            Log.e(TAG, unexpectedErrorMessage, e)
            return DataResult.Error(e)
        }
    }
}