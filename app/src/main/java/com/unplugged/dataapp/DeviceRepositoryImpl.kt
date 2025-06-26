package com.unplugged.dataapp

import android.util.Log
import com.unplugged.dataapp.data.mapper.toDeviceEntityList
import com.unplugged.dataapp.data.mapper.toDeviceItemList
import com.unplugged.dataapp.database.dao.DeviceDao
import com.unplugged.dataapp.network.RestfulApiService
import com.unplugged.dataapp.network.model.DeviceDetails
import com.unplugged.dataapp.network.model.DeviceListItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceRepositoryImpl @Inject constructor(
    private val apiService: RestfulApiService,
    private val deviceDao: DeviceDao
) : DeviceRepository {

    private val TAG = "DeviceRepositoryImpl"

    override suspend fun getDeviceList(forceRefresh: Boolean): DataResult<List<DeviceListItem>> {
        return withContext(Dispatchers.IO) {
            try {
                val localDeviceCount = deviceDao.getCount()
                val shouldFetchFromNetwork = forceRefresh || localDeviceCount == 0

                if (shouldFetchFromNetwork) {
                    // Attempt to fetch from network, return local data if it fails
                    try {
                        val response = apiService.getDeviceList()
                        if (response.isSuccessful) {
                            val networkDeviceList = response.body()
                            if (networkDeviceList != null) {
                                deviceDao.deleteAll()
                                deviceDao.insertOrReplaceAll(networkDeviceList.toDeviceEntityList())
                                // local DB is single source of truth
                                val updatedLocalData = deviceDao.getAllDevices().toDeviceItemList()
                                return@withContext DataResult.Success(updatedLocalData)
                            } else {
                                if (localDeviceCount > 0) {
                                    val staleData = deviceDao.getAllDevices().toDeviceItemList()
                                    Log.d(
                                        TAG,
                                        "API for getDeviceList returned successful response with null body. Returning stale data."
                                    )
                                    return@withContext DataResult.Success(staleData)
                                } else {
                                    return@withContext DataResult.Error(Exception("API returned successful response with null body, and no local data."))
                                }
                            }
                        } else {
                            val errorBody = response.errorBody()?.string() ?: "Unknown HTTP error"
                            val errorMessage =
                                "Network request failed: ${response.code()} - $errorBody"
                            if (localDeviceCount > 0) {
                                val staleData = deviceDao.getAllDevices().toDeviceItemList()
                                Log.d(TAG, errorMessage + ". Returning stale data.")
                                return@withContext DataResult.Success(staleData)
                            } else {
                                return@withContext DataResult.Error(Exception(errorMessage))
                            }
                        }
                    } catch (e: IOException) {
                        if (localDeviceCount > 0) {
                            val staleData = deviceDao.getAllDevices().toDeviceItemList()
                            Log.d(
                                TAG,
                                "Network IOException for getDeviceList: ${e.message}. Returning stale data."
                            )
                            return@withContext DataResult.Success(staleData)
                        } else {
                            return@withContext DataResult.Error(e)
                        }
                    } catch (e: Exception) {
                        if (localDeviceCount > 0) {
                            val staleData = deviceDao.getAllDevices().toDeviceItemList()
                            Log.d(
                                TAG,
                                "Unexpected exception during network fetch for getDeviceList: ${e.message}. Returning stale data."
                            )
                            return@withContext DataResult.Success(staleData)
                        } else {
                            return@withContext DataResult.Error(e)
                        }
                    }
                } else {
                    val localData = deviceDao.getAllDevices().toDeviceItemList()
                    return@withContext DataResult.Success(localData)
                }
            } catch (e: Exception) {
                Log.d(TAG, "Unexpected exception in getDeviceList: ${e.message}")
                return@withContext DataResult.Error(e)
            }
        }
    }

    override suspend fun getDeviceDetails(
        deviceId: String,
        forceRefresh: Boolean
    ): DataResult<DeviceDetails> {
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