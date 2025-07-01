package com.unplugged.data.model

data class DeviceItem(
    val id: String,
    val name: String?,
    val details: ItemDetails? = null
)

data class ItemDetails(
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
) {
    fun isEmpty() = color.isNullOrEmpty() && capacity.isNullOrEmpty() && price == null &&
            generation.isNullOrEmpty() && year == null && cpuModel.isNullOrEmpty() &&
            hardDiskSize.isNullOrEmpty() && strapColour.isNullOrEmpty() &&
            caseSize.isNullOrEmpty() && altColor.isNullOrEmpty() &&
            description.isNullOrEmpty() && screenSize == null &&
            capacityGB.isNullOrEmpty() && altPrice.isNullOrEmpty()
}

