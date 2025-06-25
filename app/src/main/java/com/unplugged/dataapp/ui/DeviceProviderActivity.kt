package com.unplugged.dataapp.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
                    when (val dataResult = deviceRepository.getDeviceListFromApi()) {
                        is DataResult.Success -> {
                            val list = if (searchQuery.isNullOrEmpty()) {
                                dataResult.data
                            } else {
                                dataResult.data.filter {
                                    it.name?.contains(searchQuery, true) ?: true
                                }
                            }
                            val jsonDeviceList = gson.toJson(list)

                            resultIntent.putExtra(
                                InterAppContracts.RESULT_EXTRA_DEVICE_LIST_JSON,
                                jsonDeviceList
                            )
                            setResult(Activity.RESULT_OK, resultIntent)
                        }

                        is DataResult.Error -> {
                            resultIntent.putExtra(
                                InterAppContracts.RESULT_EXTRA_ERROR_MESSAGE,
                                dataResult.exception.message ?: "Error fetching device list"
                            )
                            setResult(Activity.RESULT_CANCELED, resultIntent)
                        }
                    }
                }

                InterAppContracts.REQUEST_DEVICE_DETAILS -> {
                    val deviceId = intent.getStringExtra(InterAppContracts.EXTRA_DEVICE_ID)
                    if (deviceId == null) {
                        resultIntent.putExtra(
                            InterAppContracts.RESULT_EXTRA_ERROR_MESSAGE,
                            "Device ID is missing"
                        )
                        setResult(Activity.RESULT_CANCELED, resultIntent)
                    } else {
                        when (val dataResult = deviceRepository.getDeviceDetailsFromApi(deviceId)) {
                            is DataResult.Success -> {
                                val jsonDeviceDetails = gson.toJson(dataResult.data)
                                resultIntent.putExtra(
                                    InterAppContracts.RESULT_EXTRA_DEVICE_DETAILS_JSON,
                                    jsonDeviceDetails
                                )
                                setResult(Activity.RESULT_OK, resultIntent)
                            }

                            is DataResult.Error -> {
                                resultIntent.putExtra(
                                    InterAppContracts.RESULT_EXTRA_ERROR_MESSAGE,
                                    dataResult.exception.message ?: "Error fetching device details"
                                )
                                setResult(Activity.RESULT_CANCELED, resultIntent)
                            }
                        }
                    }
                }

                else -> {
                    resultIntent.putExtra(
                        InterAppContracts.RESULT_EXTRA_ERROR_MESSAGE,
                        "Unknown request type"
                    )
                    setResult(Activity.RESULT_CANCELED, resultIntent)
                }
            }
        } catch (e: Exception) {
            resultIntent.putExtra(
                InterAppContracts.RESULT_EXTRA_ERROR_MESSAGE,
                "Internal error in DataApp: ${e.message}"
            )
            setResult(Activity.RESULT_CANCELED, resultIntent)
        } finally {
            finish()
        }
    }
}
