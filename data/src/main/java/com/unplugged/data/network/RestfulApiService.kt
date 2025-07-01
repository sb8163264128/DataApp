package com.unplugged.data.network

import com.unplugged.data.network.model.Device
import com.unplugged.data.network.model.DeviceDetails
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface RestfulApiService {

    @GET("objects")
    suspend fun getDeviceList(): Response<List<Device>>

    @GET("objects/{id}")
    suspend fun getDeviceDetails(@Path("id") deviceId: String): Response<DeviceDetails>
}