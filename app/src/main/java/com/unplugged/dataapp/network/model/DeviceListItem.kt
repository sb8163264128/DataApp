package com.unplugged.dataapp.network.model

import com.google.gson.annotations.SerializedName

data class DeviceListItem(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String?
)


