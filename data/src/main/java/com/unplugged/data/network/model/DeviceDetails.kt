package com.unplugged.data.network.model

import com.google.gson.annotations.SerializedName

data class DeviceDetails(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String?,
    @SerializedName("data")
    val deviceSpecificData: DeviceSpecificData?
)

data class DeviceSpecificData(
    @SerializedName("color")
    val color: String? = null,

    @SerializedName("capacity")
    val capacity: String? = null,

    @SerializedName("price")
    val price: Double? = null,

    @SerializedName("generation")
    val generation: String? = null,

    @SerializedName("year")
    val year: Int? = null,

    @SerializedName("CPU model")
    val cpuModel: String? = null,

    @SerializedName("Hard disk size")
    val hardDiskSize: String? = null,

    @SerializedName("Strap Colour")
    val strapColour: String? = null,

    @SerializedName("Case Size")
    val caseSize: String? = null,

    @SerializedName("Color")
    val altColor: String? = null,

    @SerializedName("Description")
    val description: String? = null,

    @SerializedName("Screen size")
    val screenSize: Double? = null,

    @SerializedName("capacity GB")
    val capacityGB: String? = null,

    @SerializedName("Price")
    val altPrice: String? = null
)
