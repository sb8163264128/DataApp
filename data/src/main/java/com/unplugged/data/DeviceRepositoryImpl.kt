package com.unplugged.data

import com.unplugged.data.database.dao.DeviceDao
import com.unplugged.data.mapper.toDetailedDeviceListItem
import com.unplugged.data.mapper.toDeviceEntity
import com.unplugged.data.mapper.toDeviceEntityList
import com.unplugged.data.mapper.toDeviceItemList
import com.unplugged.data.model.DeviceItem
import com.unplugged.data.network.ConnectivityMonitor
import com.unplugged.data.network.RestfulApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceRepositoryImpl @Inject constructor(
    private val apiService: RestfulApiService,
    private val deviceDao: DeviceDao,
    private val connectivityMonitor: ConnectivityMonitor
) : DeviceRepository {

    override suspend fun getDeviceList(forceRefresh: Boolean): DataResult<List<DeviceItem>> {
        return withContext(Dispatchers.IO) {
            try {
                if (!forceRefresh) {
                    val localEntities = deviceDao.getAllDevices()
                    if (localEntities.isNotEmpty()) {
                        return@withContext DataResult.Success(localEntities.toDeviceItemList())
                    }
                }

                if (!connectivityMonitor.isOnline()) {
                    val localEntitiesWhenOffline = deviceDao.getAllDevices()
                    return@withContext if (localEntitiesWhenOffline.isNotEmpty()) {
                        DataResult.Success(localEntitiesWhenOffline.toDeviceItemList())
                    } else {
                        DataResult.Error(IOException("No internet connection and no local data available for device list."))
                    }
                }

                val response = apiService.getDeviceList()

                if (response.isSuccessful) {
                    val networkDeviceList = response.body()
                    if (networkDeviceList != null) {
                        val entitiesToSave = networkDeviceList.toDeviceEntityList()
                        deviceDao.deleteAll()
                        deviceDao.insertOrReplaceAll(entitiesToSave)

                        // Return data from Room (Single Source of Truth)
                        val updatedLocalData = deviceDao.getAllDevices().toDeviceItemList()
                        return@withContext DataResult.Success(updatedLocalData)
                    } else {
                        val errorMessage = "getDeviceList: Network response body is null."
                        val localBackup = deviceDao.getAllDevices()
                        if (localBackup.isNotEmpty()) {
                            return@withContext DataResult.Success(localBackup.toDeviceItemList())
                        }
                        return@withContext DataResult.Error(Exception(errorMessage))
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown HTTP error"
                    val errorMessage =
                        "getDeviceList: Network request failed: ${response.code()} - $errorBody"
                    val localBackup = deviceDao.getAllDevices()
                    if (localBackup.isNotEmpty()) {
                        return@withContext DataResult.Success(localBackup.toDeviceItemList())
                    }
                    return@withContext DataResult.Error(Exception(errorMessage))
                }
            } catch (e: IOException) {
                val localBackup = deviceDao.getAllDevices()
                if (localBackup.isNotEmpty()) {
                    return@withContext DataResult.Success(localBackup.toDeviceItemList())
                }
                return@withContext DataResult.Error(e)
            } catch (e: Exception) {
                return@withContext DataResult.Error(e)
            }
        }
    }

    override suspend fun getDeviceDetails(
        deviceId: String,
        forceRefresh: Boolean
    ): DataResult<DeviceItem> {
        return withContext(Dispatchers.IO) {
            try {
                val localDeviceEntity = deviceDao.getDeviceById(deviceId)
                val localDetailedDeviceItem = localDeviceEntity?.toDetailedDeviceListItem()

                if (localDetailedDeviceItem?.details != null &&
                    !localDetailedDeviceItem.details.isEmpty() &&
                    !forceRefresh
                ) {
                    return@withContext DataResult.Success(localDetailedDeviceItem)
                }

                if (!connectivityMonitor.isOnline()) {
                    return@withContext if (localDetailedDeviceItem != null) {
                        DataResult.Success(localDetailedDeviceItem)
                    } else {
                        DataResult.Error(IOException("Offline and no local data for device ID: $deviceId"))
                    }
                }

                val response = apiService.getDeviceDetails(deviceId)

                if (response.isSuccessful) {
                    val networkDeviceDetailsData = response.body()
                    if (networkDeviceDetailsData != null) {
                        val apiDeviceEntity = networkDeviceDetailsData.toDeviceEntity()
                        if (localDeviceEntity != null) {
                            val updatedDeviceEntity = localDeviceEntity.copy(
                                color = apiDeviceEntity.color,
                                capacity = apiDeviceEntity.capacity,
                                price = apiDeviceEntity.price,
                                generation = apiDeviceEntity.generation,
                                year = apiDeviceEntity.year,
                                cpuModel = apiDeviceEntity.cpuModel,
                                hardDiskSize = apiDeviceEntity.hardDiskSize,
                                strapColour = apiDeviceEntity.strapColour,
                                caseSize = apiDeviceEntity.caseSize,
                                altColor = apiDeviceEntity.altColor,
                                description = apiDeviceEntity.description,
                                screenSize = apiDeviceEntity.screenSize,
                                capacityGB = apiDeviceEntity.capacityGB,
                                altPrice = apiDeviceEntity.altPrice
                            )
                            deviceDao.insertOrReplace(updatedDeviceEntity)
                            return@withContext DataResult.Success(updatedDeviceEntity.toDetailedDeviceListItem())
                        } else {
                            return@withContext DataResult.Error(Exception("Failed to find base device to attach details for $deviceId"))
                        }
                    } else {
                        if (localDetailedDeviceItem != null) {
                            return@withContext DataResult.Success(localDetailedDeviceItem)
                        }
                        return@withContext DataResult.Error(Exception("Network response for details was null for $deviceId"))
                    }
                } else {
                    if (localDetailedDeviceItem != null) {
                        return@withContext DataResult.Success(localDetailedDeviceItem)
                    }
                    return@withContext DataResult.Error(Exception("Network request for details failed for $deviceId: ${response.code()}"))
                }

            } catch (e: IOException) {
                val localBackup = deviceDao.getDeviceById(deviceId)
                if (localBackup != null) {
                    return@withContext DataResult.Success(localBackup.toDetailedDeviceListItem())
                }
                return@withContext DataResult.Error(e)
            } catch (e: Exception) {
                if (!forceRefresh) {
                    val localBackupOnGenericException = deviceDao.getDeviceById(deviceId)
                    if (localBackupOnGenericException != null) {
                        return@withContext DataResult.Success(localBackupOnGenericException.toDetailedDeviceListItem())
                    }
                }
                return@withContext DataResult.Error(e)
            }
        }
    }
}

