package com.lab.tb.distributed.adapter.model

class DataConnectionState(
    private var identifierValue: String,
    private var isConnectedValue: Boolean,
    private var errorValue: String?
) {
    var identifier: String
        get() = identifierValue
        private set(value) {
            identifierValue = value
        }
    var isConnected: Boolean
        get() = isConnectedValue
        private set(value) {
            isConnectedValue = value
        }
    var errror: String?
        get() = errorValue
        private set(value) {
            errorValue = value
        }
}
