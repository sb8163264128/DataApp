package com.unplugged.dataapp.ipc

object InterAppContracts {

    const val DATA_APP_PACKAGE_NAME = "com.unplugged.dataapp" // DataApp's package

    // Activity in DataApp that will provide the data
    const val DEVICE_PROVIDER_ACTIVITY_ACTION = "com.unplugged.dataapp.action.PROVIDE_DEVICE_DATA"

    // Request types (can be put as an extra in the intent)
    const val REQUEST_TYPE_EXTRA = "REQUEST_TYPE"
    const val REQUEST_DEVICE_LIST = "GET_DEVICE_LIST"
    const val REQUEST_DEVICE_DETAILS = "GET_DEVICE_DETAILS"

    // Extras for sending data
    const val EXTRA_SEARCH_QUERY = "SEARCH_QUERY"
    const val EXTRA_DEVICE_ID = "DEVICE_ID"

    // Extras for returning results
    const val RESULT_EXTRA_DEVICE_LIST_JSON = "DEVICE_LIST_JSON"
    const val RESULT_EXTRA_DEVICE_DETAILS_JSON = "DEVICE_DETAILS_JSON"
    const val RESULT_EXTRA_ERROR_MESSAGE = "ERROR_MESSAGE"
}