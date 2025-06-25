package com.unplugged.dataapp.network

import com.unplugged.dataapp.network.model.DeviceDetails
import com.unplugged.dataapp.network.model.DeviceListItem
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface RestfulApiService {

    @GET("objects")
    suspend fun getDeviceList(): Response<List<DeviceListItem>>

    @GET("objects/{id}")
    suspend fun getDeviceDetails(@Path("id") deviceId: String): Response<DeviceDetails>
}