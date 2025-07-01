package com.unplugged.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "devices")
data class DeviceEntity(
    @PrimaryKey
    val id: String,
    val name: String? = null,
    val color: String? = null,
    val capacity: String? = null,
    val price: Double? = null,
    val generation: String? = null,
    val year: Int? = null,
    val cpuModel: String? = null,
    val hardDiskSize: String? = null,
    val strapColour: String? = null,
    val caseSize: String? = null,
    val altColor: String? = null,
    val description: String? = null,
    val screenSize: Double? = null,
    val capacityGB: String? = null,
    val altPrice: String? = null
)
