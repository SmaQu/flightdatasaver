package com.smaqu.rcgraphsystem.bluetoothconnection

interface BluetoothApi {
    companion object {
        const val MESSAGE_INFO = 0
        const val MESSAGE_WRITE = 1
        const val MESSAGE_SHORT_READ = 2
        const val MESSAGE_FULL_READ = 3
        const val MESSAGE_FILE = 4
        const val MESSAGE_LIST = 5
        const val MESSAGE_CLIENT_CONNECTING = 6
        const val MESSAGE_CLIENT_CONNECTED = 7
        const val MESSAGE_CLIENT_DISCONNECTED = 8
    }
}