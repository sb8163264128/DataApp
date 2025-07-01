package com.unplugged.data.network.model

import com.google.gson.annotations.SerializedName

data class Device(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String?
)