package com.unplugged.dataapp.ui // Or your preferred package

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.unplugged.dataapp.DataResult
import com.unplugged.dataapp.DeviceRepository
import com.unplugged.dataapp.ipc.InterAppContracts
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DeviceProviderActivity : AppCompatActivity() {

    @Inject
    lateinit var deviceRepository: DeviceRepository

    @Inject
    lateinit var gson: Gson

    private val TAG = "DeviceProviderActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // No UI needed for this activity if it only processes and returns data.
        // If you want to show something, setContentView(R.layout.your_layout)

        Log.d(TAG, "onCreate: Intent action = ${intent.action}, Request Type = ${intent.getStringExtra(
            InterAppContracts.REQUEST_TYPE_EXTRA)}")

        lifecycleScope.launch {
            handleRequest()
        }
    }

    private suspend fun handleRequest() {
        val requestType = intent.getStringExtra(InterAppContracts.REQUEST_TYPE_EXTRA)
        val resultIntent = Intent()

        try {
            when (requestType) {
                InterAppContracts.REQUEST_DEVICE_LIST -> {
                    val searchQuery = intent.getStringExtra(InterAppContracts.EXTRA_SEARCH_QUERY)
                    Log.d(TAG, "Processing REQUEST_DEVICE_LIST")
                    when (val dataResult = deviceRepository.getDeviceListFromApi()) {
                        is DataResult.Success -> {
                            val list = if (searchQuery.isNullOrEmpty()) {
                                dataResult.data
                            } else {
                                dataResult.data.filter { it.name?.contains(searchQuery, true) ?: true }
                            }
                            val jsonDeviceList = gson.toJson(list)

                            resultIntent.putExtra(InterAppContracts.RESULT_EXTRA_DEVICE_LIST_JSON, jsonDeviceList)
                            setResult(Activity.RESULT_OK, resultIntent)
                            Log.d(TAG, "Successfully fetched device list. Returning OK.")
                        }
                        is DataResult.Error -> {
                            Log.e(TAG, "Error fetching device list: ${dataResult.exception.message}")
                            resultIntent.putExtra(InterAppContracts.RESULT_EXTRA_ERROR_MESSAGE, dataResult.exception.message ?: "Error fetching device list")
                            setResult(Activity.RESULT_CANCELED, resultIntent)
                        }
                    }
                }

                InterAppContracts.REQUEST_DEVICE_DETAILS -> {
                    val deviceId = intent.getStringExtra(InterAppContracts.EXTRA_DEVICE_ID)
                    Log.d(TAG, "Processing REQUEST_DEVICE_DETAILS for ID: $deviceId")
                    if (deviceId == null) {
                        resultIntent.putExtra(InterAppContracts.RESULT_EXTRA_ERROR_MESSAGE, "Device ID is missing")
                        setResult(Activity.RESULT_CANCELED, resultIntent)
                    } else {
                        when (val dataResult = deviceRepository.getDeviceDetailsFromApi(deviceId)) {
                            is DataResult.Success -> {
                                val jsonDeviceDetails = gson.toJson(dataResult.data)
                                resultIntent.putExtra(InterAppContracts.RESULT_EXTRA_DEVICE_DETAILS_JSON, jsonDeviceDetails)
                                setResult(Activity.RESULT_OK, resultIntent)
                                Log.d(TAG, "Successfully fetched device details. Returning OK.")
                            }
                            is DataResult.Error -> {
                                Log.e(TAG, "Error fetching device details: ${dataResult.exception.message}")
                                resultIntent.putExtra(InterAppContracts.RESULT_EXTRA_ERROR_MESSAGE, dataResult.exception.message ?: "Error fetching device details")
                                setResult(Activity.RESULT_CANCELED, resultIntent)
                            }
                        }
                    }
                }
                else -> {
                    Log.w(TAG, "Unknown request type: $requestType")
                    resultIntent.putExtra(InterAppContracts.RESULT_EXTRA_ERROR_MESSAGE, "Unknown request type")
                    setResult(Activity.RESULT_CANCELED, resultIntent)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception in DeviceProviderActivity: ${e.message}", e)
            resultIntent.putExtra(InterAppContracts.RESULT_EXTRA_ERROR_MESSAGE, "Internal error in DataApp: ${e.message}")
            setResult(Activity.RESULT_CANCELED, resultIntent)
        } finally {
            finish()
            Log.d(TAG, "Finishing DeviceProviderActivity")
        }
    }
}
