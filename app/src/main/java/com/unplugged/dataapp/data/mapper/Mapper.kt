package com.unplugged.dataapp.data.mapper

import com.unplugged.dataapp.database.model.DeviceEntity
import com.unplugged.dataapp.network.model.DeviceDetails
import com.unplugged.dataapp.network.model.DeviceListItem
import com.unplugged.dataapp.network.model.DeviceSpecificData

// --- Network DTO to Room Entity Mappers ---

fun DeviceListItem.toDeviceEntity(): DeviceEntity {
    return DeviceEntity(
        id = this.id,
        name = this.name
    )
}

fun List<DeviceListItem>.toDeviceEntityList(): List<DeviceEntity> {
    return this.map { it.toDeviceEntity() }
}

fun DeviceDetails.toDeviceEntity(): DeviceEntity {
    return DeviceEntity(
        id = this.id,
        name = this.name,
        color = deviceSpecificData?.color,
        capacity = deviceSpecificData?.capacity,
        price = deviceSpecificData?.price,
        generation = deviceSpecificData?.generation,
        year = deviceSpecificData?.year,
        cpuModel = deviceSpecificData?.cpuModel,
        hardDiskSize = deviceSpecificData?.hardDiskSize,
        strapColour = deviceSpecificData?.strapColour,
        caseSize = deviceSpecificData?.caseSize,
        altColor = deviceSpecificData?.altColor,
        description = deviceSpecificData?.description,
        screenSize = deviceSpecificData?.screenSize,
        capacityGB = deviceSpecificData?.capacityGB,
        altPrice = deviceSpecificData?.altPrice
    )
}

// --- Room Entity to IPC DTO Mappers ---

fun DeviceEntity.toDeviceListItem(): DeviceListItem {
    return DeviceListItem(
        id = this.id,
        name = this.name
    )
}

fun List<DeviceEntity>.toDeviceItemList(): List<DeviceListItem> {
    return this.map { it.toDeviceListItem() }
}

fun DeviceEntity.toDeviceDetails(): DeviceDetails {
    return DeviceDetails(
        id = this.id,
        name = this.name,
        deviceSpecificData = DeviceSpecificData(
            color = this.color,
            capacity = this.capacity,
            price = this.price,
            generation = this.generation,
            year = this.year,
            cpuModel = this.cpuModel,
            hardDiskSize = this.hardDiskSize,
            strapColour = this.strapColour,
            caseSize = this.caseSize,
            altColor = this.altColor,
            description = this.description,
            screenSize = this.screenSize,
            capacityGB = this.capacityGB,
            altPrice = this.altPrice
        )
    )
}