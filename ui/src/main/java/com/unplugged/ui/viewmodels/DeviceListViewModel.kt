package com.unplugged.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unplugged.data.DataResult
import com.unplugged.data.DeviceRepository
import com.unplugged.data.model.DeviceItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeviceListViewModel @Inject constructor(
    private val deviceRepository: DeviceRepository,
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<DeviceListUiState>(DeviceListUiState.Success(emptyList()))
    val uiState: StateFlow<DeviceListUiState> = _uiState.asStateFlow()

    private var originalDeviceList: List<DeviceItem> = emptyList()
    private var searchQuery: String? = null

    fun processIntentQuery(inputQuery: String?, forceRefreshList: Boolean = false) {
        val query = inputQuery?.trim() ?: ""
        this.searchQuery = query

        if (originalDeviceList.isNotEmpty() && !forceRefreshList) {
            applyFilterToState(query, originalDeviceList)
        } else {
            loadDeviceList(forceRefresh = forceRefreshList)
        }
    }

    fun handleDeviceItemClick(deviceId: String) {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState !is DeviceListUiState.Success) {
                return@launch
            }

            val deviceToUpdate = currentState.devices.find { it.id == deviceId }
            if (deviceToUpdate == null) {
                return@launch
            }

            if (deviceToUpdate.details != null || currentState.isLoadingDetailsFor.contains(deviceId)) {
                return@launch
            }

            _uiState.update { currentSuccessState ->
                if (currentSuccessState is DeviceListUiState.Success) {
                    currentSuccessState.copy(isLoadingDetailsFor = currentSuccessState.isLoadingDetailsFor + deviceId)
                } else {
                    currentSuccessState
                }
            }

            when (val result = deviceRepository.getDeviceDetails(deviceId, forceRefresh = false)) {
                is DataResult.Success -> {
                    val detailedDeviceItem = result.data

                    originalDeviceList =
                        originalDeviceList.map { if (it.id == deviceId) detailedDeviceItem else it }
                    val queryForFilter =
                        (_uiState.value as? DeviceListUiState.Success)?.searchQuery ?: ""
                    applyFilterToState(queryForFilter, originalDeviceList)

                    _uiState.update { currentSuccessState ->
                        if (currentSuccessState is DeviceListUiState.Success) {
                            currentSuccessState.copy(isLoadingDetailsFor = currentSuccessState.isLoadingDetailsFor - deviceId)
                        } else {
                            currentSuccessState
                        }
                    }
                }

                is DataResult.Error -> {
                    _uiState.update { currentSuccessState ->
                        if (currentSuccessState is DeviceListUiState.Success) {
                            currentSuccessState.copy(isLoadingDetailsFor = currentSuccessState.isLoadingDetailsFor - deviceId)
                        } else {
                            currentSuccessState
                        }
                    }
                }
            }
        }
    }

    private fun loadDeviceList(forceRefresh: Boolean) {
        viewModelScope.launch {
            if (forceRefresh || _uiState.value !is DeviceListUiState.Success) {
                _uiState.value = DeviceListUiState.Loading
            }
            when (val result = deviceRepository.getDeviceList(forceRefresh)) {
                is DataResult.Success -> {
                    originalDeviceList = result.data
                    applyFilterToState(searchQuery, originalDeviceList)
                }

                is DataResult.Error -> {
                    _uiState.value =
                        DeviceListUiState.Error(result.exception.message ?: "Unknown error")
                }
            }
        }

    }

    private fun applyFilterToState(query: String?, sourceList: List<DeviceItem>) {
        val filteredList = if (query.isNullOrBlank()) {
            sourceList
        } else {
            sourceList.filter { device ->
                (device.name?.contains(query, ignoreCase = true) == true)
            }
        }
        _uiState.update { currentState ->
            val currentIsLoadingDetailsFor =
                (currentState as? DeviceListUiState.Success)?.isLoadingDetailsFor ?: emptySet()
            DeviceListUiState.Success(
                searchQuery = query ?: "",
                devices = filteredList,
                isLoadingDetailsFor = currentIsLoadingDetailsFor
            )
        }
    }

}