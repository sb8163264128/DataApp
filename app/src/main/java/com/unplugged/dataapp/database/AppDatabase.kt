package com.unplugged.dataapp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.unplugged.dataapp.database.dao.DeviceDao
import com.unplugged.dataapp.database.model.DeviceEntity

@Database(entities = [DeviceEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun deviceDao(): DeviceDao

}