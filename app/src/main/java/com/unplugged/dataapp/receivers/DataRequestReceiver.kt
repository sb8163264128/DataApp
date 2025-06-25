package com.unplugged.dataapp.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.ResultReceiver
import android.util.Log
import com.google.gson.Gson
import com.unplugged.dataapp.DataResult
import com.unplugged.dataapp.DeviceRepository
import com.unplugged.dataapp.ipc.InterAppContracts
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

class DataRequestReceiver : BroadcastReceiver() {

    @Inject
    lateinit var deviceRepository: DeviceRepository

    @Inject
    lateinit var gson: Gson

    private val receiverScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val TAG = "DataRequestReceiver"

    override fun onReceive(context: Context?, intent: Intent?) {
/*
        if (context == null || intent == null) {
            Log.e(TAG, "Context or Intent is null, aborting.")
            return
        }

        val action = intent.action
        val resultReceiver = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(
                InterAppContracts.EXTRA_RESULT_RECEIVER,
                ResultReceiver::class.java
            )
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(InterAppContracts.EXTRA_RESULT_RECEIVER)
        }

        if (resultReceiver == null) {
            Log.e(TAG, "No ResultReceiver provided in the intent. Action: $action")
            return
        }

        val pendingResult = goAsync()

        receiverScope.launch {
            try {
                when (action) {
                    InterAppContracts.ACTION_REQUEST_DEVICE_LIST -> {
                        val searchQuery =
                            intent.getStringExtra(InterAppContracts.EXTRA_SEARCH_QUERY)
                        Log.d(
                            TAG,
                            "Processing ACTION_REQUEST_DEVICE_LIST. Search query: '$searchQuery'"
                        )

                        when (val repoResult = deviceRepository.getDeviceListFromApi()) {
                            is DataResult.Success -> {
                                val deviceList =
                                    searchQuery?.let { repoResult.data.filter { it.name == searchQuery } }
                                        ?: repoResult.data
                                Log.i(
                                    TAG,
                                    "Successfully fetched device list. Count: ${deviceList.size}"
                                )
                                val bundle = Bundle().apply {
                                    val devicesJson = gson.toJson(deviceList)
                                    putString(InterAppContracts.KEY_RESULT_DEVICE_LIST, devicesJson)
                                }
                                resultReceiver.send(InterAppContracts.RESULT_CODE_SUCCESS, bundle)
                            }

                            is DataResult.Error -> {
                                Log.e(
                                    TAG,
                                    "Error fetching device list from repository: ${repoResult.exception.message}",
                                    repoResult.exception
                                )
                                resultReceiver.send(
                                    InterAppContracts.RESULT_CODE_ERROR,
                                    Bundle().apply {
                                        putString(
                                            InterAppContracts.KEY_RESULT_ERROR_MESSAGE,
                                            repoResult.exception.message
                                                ?: "Unknown error fetching device list."
                                        )
                                    })
                            }
                        }
                    }

                    InterAppContracts.ACTION_REQUEST_DEVICE_DETAILS -> {
                        val deviceId = intent.getStringExtra(InterAppContracts.EXTRA_DEVICE_ID)
                        if (deviceId.isNullOrEmpty()) {
                            Log.e(
                                TAG,
                                "Device ID is null or empty for ACTION_REQUEST_DEVICE_DETAILS."
                            )
                            resultReceiver.send(
                                InterAppContracts.RESULT_CODE_ERROR,
                                Bundle().apply {
                                    putString(
                                        InterAppContracts.KEY_RESULT_ERROR_MESSAGE,
                                        "Device ID not provided."
                                    )
                                })
                        } else {
                            Log.d(TAG, "Processing ACTION_REQUEST_DEVICE_DETAILS for ID: $deviceId")

                            when (val repoResult =
                                deviceRepository.getDeviceDetailsFromApi(deviceId)) { // Adjusted method name
                                is DataResult.Success -> {
                                    val deviceDetails =
                                        repoResult.data
                                    Log.i(
                                        TAG,
                                        "Successfully fetched device details for ID: $deviceId"
                                    )
                                    val bundle = Bundle().apply {
                                        val detailsJson = gson.toJson(deviceDetails)
                                        putString(
                                            InterAppContracts.KEY_RESULT_DEVICE_DETAILS,
                                            detailsJson
                                        )
                                    }
                                    resultReceiver.send(
                                        InterAppContracts.RESULT_CODE_SUCCESS,
                                        bundle
                                    )
                                }

                                is DataResult.Error -> {
                                    Log.e(
                                        TAG,
                                        "Error fetching device details for ID $deviceId: ${repoResult.exception.message}",
                                        repoResult.exception
                                    )
                                    resultReceiver.send(
                                        InterAppContracts.RESULT_CODE_ERROR,
                                        Bundle().apply {
                                            putString(
                                                InterAppContracts.KEY_RESULT_ERROR_MESSAGE,
                                                repoResult.exception.message
                                                    ?: "Unknown error fetching details for ID $deviceId."
                                            )
                                        })
                                }
                            }
                        }
                    }

                    else -> {
                        Log.w(TAG, "Unknown action received: $action")
                        resultReceiver.send(InterAppContracts.RESULT_CODE_ERROR, Bundle().apply {
                            putString(
                                InterAppContracts.KEY_RESULT_ERROR_MESSAGE,
                                "Unknown action: $action"
                            )
                        })
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception in DataRequestReceiver coroutine: ${e.message}", e)
                resultReceiver.send(InterAppContracts.RESULT_CODE_ERROR, Bundle().apply {
                    putString(
                        InterAppContracts.KEY_RESULT_ERROR_MESSAGE,
                        "Internal error in DataApp: ${e.message}"
                    )
                })
            } finally {
                pendingResult.finish()
            }

        }

 */
    }
}