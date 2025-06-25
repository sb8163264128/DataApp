package com.unplugged.dataapp.network.model

import com.google.gson.annotations.SerializedName

data class DeviceDetails(
    @SerializedName("id")
    val id: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("data")
    val deviceSpecificData: DeviceSpecificData?
)

data class DeviceSpecificData(
    @SerializedName("color")
    val color: String? = null,

    @SerializedName("capacity")
    val capacity: String? = null, // e.g., "256 GB"

    @SerializedName("price") // Assuming it's a number
    val price: Double? = null,

    @SerializedName("generation")
    val generation: String? = null,

    @SerializedName("year")
    val year: Int? = null,

    // If the API uses keys with spaces or special characters:
    @SerializedName("CPU model")
    val cpuModel: String? = null,

    @SerializedName("Hard disk size")
    val hardDiskSize: String? = null,

    @SerializedName("Strap Colour")
    val strapColour: String? = null,

    @SerializedName("Case Size")
    val caseSize: String? = null,

    @SerializedName("Color") // Alternative for the Beats example
    val altColor: String? = null, // If this is semantically the same as 'color', pick one canonical name.

    @SerializedName("Description")
    val description: String? = null,

    @SerializedName("Screen size")
    val screenSize: Double? = null, // Assuming it's a numeric value like 13.3

    @SerializedName("capacity GB")
    val capacityGB: String? = null,

    @SerializedName("Price") // For the capitalized version if "price" doesn't catch it.
    val altPrice: String? = null // Price from iPad Air is "419.99"
)
