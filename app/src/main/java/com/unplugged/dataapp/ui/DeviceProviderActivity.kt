package com.unplugged.dataapp.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.unplugged.dataapp.DataResult
import com.unplugged.dataapp.ipc.InterAppContracts
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

@AndroidEntryPoint
class DeviceProviderActivity : AppCompatActivity() {

    private val viewModel: DeviceProviderViewModel by viewModels()
    private var currentDataJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let {
            handleIntent(it)
        }
    }

    private fun handleIntent(receivedIntent: Intent?) {
        if (receivedIntent == null) {
            if (currentDataJob == null || currentDataJob?.isActive == false) {
                finish()
            }
            return
        }

        val requestType = receivedIntent.getStringExtra(InterAppContracts.REQUEST_TYPE_EXTRA)

        if (isRecognizedIpcRequest(requestType)) {
            currentDataJob?.cancel()
        } else {
            if (currentDataJob == null || currentDataJob?.isActive == false) {
                val resultIntent = Intent()
                resultIntent.putExtra(
                    InterAppContracts.RESULT_EXTRA_ERROR_MESSAGE,
                    "Received unrelated or unknown intent."
                )
                setResult(Activity.RESULT_CANCELED, resultIntent)
                finish()
            }
            return
        }

        currentDataJob = lifecycleScope.launch {
            val resultIntent = Intent()
            var activityResultCode = Activity.RESULT_CANCELED

            try {
                when (requestType) {
                    InterAppContracts.REQUEST_DEVICE_LIST -> {
                        when (val dataResult = viewModel.fetchSerializedDeviceList(false)) {
                            is DataResult.Success -> {
                                resultIntent.putExtra(
                                    InterAppContracts.RESULT_EXTRA_DEVICE_LIST_JSON,
                                    dataResult.data
                                )
                                activityResultCode = Activity.RESULT_OK
                            }

                            is DataResult.Error -> {
                                resultIntent.putExtra(
                                    InterAppContracts.RESULT_EXTRA_ERROR_MESSAGE,
                                    "Failed to fetch/serialize device list: ${dataResult.exception.message}"
                                )
                            }
                        }
                    }

                    InterAppContracts.REQUEST_DEVICE_DETAILS -> {
                        val deviceId =
                            receivedIntent.getStringExtra(InterAppContracts.EXTRA_DEVICE_ID)

                        if (deviceId.isNullOrBlank()) {
                            resultIntent.putExtra(
                                InterAppContracts.RESULT_EXTRA_ERROR_MESSAGE,
                                "Device ID not provided or is blank for details request."
                            )
                        } else {
                            when (val dataResult =
                                viewModel.fetchSerializedDeviceDetails(deviceId, false)) {
                                is DataResult.Success -> {
                                    resultIntent.putExtra(
                                        InterAppContracts.RESULT_EXTRA_DEVICE_DETAILS_JSON,
                                        dataResult.data
                                    )
                                    activityResultCode = Activity.RESULT_OK
                                }

                                is DataResult.Error -> {
                                    resultIntent.putExtra(
                                        InterAppContracts.RESULT_EXTRA_ERROR_MESSAGE,
                                        "Failed to fetch/serialize device details for ID $deviceId: ${dataResult.exception.message}"
                                    )
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                if (e is CancellationException) {
                    throw e
                }
                resultIntent.putExtra(
                    InterAppContracts.RESULT_EXTRA_ERROR_MESSAGE,
                    "Internal error in DataApp: ${e.message}"
                )
                setResult(Activity.RESULT_CANCELED, resultIntent)
            } finally {
                if (this.isActive && currentDataJob == this.coroutineContext[Job]) {
                    setResult(activityResultCode, resultIntent)
                    finish()
                }
            }
        }
    }

    private fun isRecognizedIpcRequest(requestType: String?): Boolean {
        return requestType == InterAppContracts.REQUEST_DEVICE_LIST ||
                requestType == InterAppContracts.REQUEST_DEVICE_DETAILS
    }

    override fun onDestroy() {
        super.onDestroy()
        currentDataJob?.cancel()
    }
}
