package com.unplugged.data.mapper

import com.unplugged.data.database.model.DeviceEntity
import com.unplugged.data.model.DeviceItem
import com.unplugged.data.model.ItemDetails
import com.unplugged.data.network.model.Device
import com.unplugged.data.network.model.DeviceDetails

// --- Network DTO to Room Entity Mappers ---

fun Device.toDeviceEntity(): DeviceEntity {
    return DeviceEntity(
        id = this.id,
        name = this.name
    )
}

fun List<Device>.toDeviceEntityList(): List<DeviceEntity> {
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

// --- Room Entity to DTO Mappers ---

fun DeviceEntity.toDeviceListItem(): DeviceItem {
    return DeviceItem(
        id = this.id,
        name = this.name
    )
}

fun List<DeviceEntity>.toDeviceItemList(): List<DeviceItem> {
    return this.map { it.toDeviceListItem() }
}

fun DeviceEntity.toDetailedDeviceListItem(): DeviceItem {
    return DeviceItem(
        id = this.id,
        name = this.name,
        details = ItemDetails(
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