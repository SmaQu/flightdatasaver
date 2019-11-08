package com.smaqu.rcgraphsystem.bluetoothconnection

import android.bluetooth.BluetoothDevice

interface ClientListener {
    fun messageFullReceived(client: BluetoothClient, message: String)

    fun messageShortReceived(client: BluetoothClient, message: String)

    fun messageList(client: BluetoothClient, message: String)

    fun messageFile(client: BluetoothClient, message: String)

    fun messageSend(client: BluetoothClient, message: String)

    fun messageInfo(client: BluetoothClient, message: String)

    fun stateConnected(device: BluetoothDevice)

    fun stateConnecting()

    fun stateDisconnected(device: BluetoothDevice)
}