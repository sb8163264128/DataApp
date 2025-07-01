package com.unplugged.ui.viewmodels

import com.unplugged.data.model.DeviceItem

sealed interface DeviceListUiState {
    data object Loading : DeviceListUiState
    data class Success(
        val devices: List<DeviceItem>,
        val searchQuery: String = "",
        // For preventing double taps. Can bu used for item level loading progress indicator
        val isLoadingDetailsFor: Set<String> = emptySet()
    ) : DeviceListUiState

    data class Error(val message: String) : DeviceListUiState
}