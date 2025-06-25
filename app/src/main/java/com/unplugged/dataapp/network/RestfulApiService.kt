package com.unplugged.dataapp.network

import com.unplugged.dataapp.network.model.DeviceDetails
import com.unplugged.dataapp.network.model.DeviceListItem
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface RestfulApiService {

    // Endpoint: https://api.restful-api.dev/objects
    // Fetches the list of all devices
    // Note: The API actually returns a list of full DeviceDetails objects.
    @GET("objects")
    suspend fun getDeviceList(): Response<List<DeviceListItem>>

    // Endpoint: https://api.restful-api.dev/objects/{id}
    // Fetches details for a specific device
    @GET("objects/{id}")
    suspend fun getDeviceDetails(@Path("id") deviceId: String): Response<DeviceDetails>
}