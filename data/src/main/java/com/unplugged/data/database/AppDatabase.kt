package com.unplugged.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.unplugged.data.database.dao.DeviceDao
import com.unplugged.data.database.model.DeviceEntity

@Database(entities = [DeviceEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun deviceDao(): DeviceDao

}