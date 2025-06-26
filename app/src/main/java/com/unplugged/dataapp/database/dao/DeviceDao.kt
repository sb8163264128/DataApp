package com.unplugged.dataapp.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.unplugged.dataapp.database.model.DeviceEntity

@Dao
interface DeviceDao {

    @Query("SELECT * FROM devices")
    suspend fun getAllDevices(): List<DeviceEntity>

    @Query("SELECT * FROM devices WHERE id = :deviceId")
    suspend fun getDeviceByIdSync(deviceId: String): DeviceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplaceAll(devices: List<DeviceEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(device: DeviceEntity)

    @Query("DELETE FROM devices")
    suspend fun deleteAll()

    @Query("SELECT COUNT(id) FROM devices")
    suspend fun getCount(): Int
}