package com.smaqu.rcgraphsystem.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.util.Log
import android.widget.Toast
import com.smaqu.rcgraphsystem.bluetoothconnection.BluetoothClient
import com.smaqu.rcgraphsystem.bluetoothconnection.BluetoothClient.Companion.STATE_CONNECTED
import com.smaqu.rcgraphsystem.bluetoothconnection.BluetoothClient.Companion.STATE_CONNECTING
import com.smaqu.rcgraphsystem.bluetoothconnection.BluetoothClient.Companion.STATE_DISCONNECTED
import com.smaqu.rcgraphsystem.bluetoothconnection.ClientListener
import com.smaqu.rcgraphsystem.models.GraphFile
import com.smaqu.rcgraphsystem.viewmodel.callback.StateCallback
import android.text.style.ForegroundColorSpan
import android.text.SpannableString
import com.smaqu.rcgraphsystem.util.ApplicationPreferences


class MainVM(application: Application) : AndroidViewModel(application) {

    companion object {
        const val TAG = "MainVM"
    }

    private var bluetoothClient: BluetoothClient? = null
    private var downloadDataStringBuilder: StringBuilder = StringBuilder()
    private var fileListStringBuilder: StringBuilder = StringBuilder()
    private var fullMessageBuilder: StringBuilder = StringBuilder()
    private var shortMessageBuilder: StringBuilder = StringBuilder()

    var PACKAGE_SEPARATOR = ApplicationPreferences.DEFAULT_PACKAGE_SEPARATOR
    var VARIABLE_SEPARATOR = ApplicationPreferences.DEFAULT_VARIABLE_SEPARATOR

    var connectionStateCallback: MutableLiveData<StateCallback> = MutableLiveData()
        private set
    var bluetoothAdapter: MutableLiveData<BluetoothAdapter> = MutableLiveData()
        private set
    var bluetoothState: MutableLiveData<Int> = MutableLiveData()
        private set
    var pairedDevices: MutableLiveData<Set<BluetoothDevice>> = MutableLiveData()
        private set
    var messageFullHistory: MutableLiveData<StringBuilder> = MutableLiveData()
        private set
    var messageShortHistory: MutableLiveData<StringBuilder> = MutableLiveData()
        private set
    var listOfFileList: MutableLiveData<List<String>> = MutableLiveData()
        private set
    var downloadedData: MutableLiveData<String> = MutableLiveData()
        private set
    var graphFile: MutableLiveData<GraphFile> = MutableLiveData()

    init {
        bluetoothAdapter.value = BluetoothAdapter.getDefaultAdapter()
    }

    fun isBluetoothEnable() = bluetoothAdapter.value?.isEnabled!!

    fun enableBluetooth(value: Boolean) {
        if (value) {
            bluetoothAdapter.value?.enable()
        } else {
            bluetoothAdapter.value?.disable()
        }
    }

    fun isDiscovering() = bluetoothAdapter.value?.isDiscovering!!

    fun startDiscovery() {
        bluetoothAdapter.value?.startDiscovery()
    }

    fun cancelDiscovery() {
        bluetoothAdapter.value?.cancelDiscovery()
    }

    fun searchPairedDevices() {
        pairedDevices.value = bluetoothAdapter.value?.bondedDevices
    }

    ////////// Broadcast //////////
    fun registerBroadcastReceiver() {
        val intentFilter = IntentFilter().apply {
            addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
            addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        }
        getApplication<Application>().registerReceiver(broadcastReceiver, intentFilter)
    }

    fun unRegisterBroadcastReceiver() {
        getApplication<Application>().unregisterReceiver(broadcastReceiver)
    }

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                BluetoothAdapter.ACTION_STATE_CHANGED -> {
                    bluetoothState.value = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)
                }
            }
        }
    }

    ////////// BlueToothClient //////////
    fun sendMessage(message: String) {
        bluetoothClient?.send(message)
    }

    fun connectBluetoothDevice(bluetoothDevice: BluetoothDevice, pinConnection: Boolean) {
        bluetoothAdapter.value?.cancelDiscovery()
        stopBluetoothClientConnection()
        bluetoothClient = BluetoothClient(bluetoothDevice, bluetoothClientListener)
        bluetoothClient?.connect(pinConnection)
    }

    fun stopBluetoothClientConnection() {
        bluetoothClient?.let {
            it.stop()
            bluetoothClient = null
        }
    }

    fun getBlueToothState(): Byte {
        if (bluetoothClient != null) {
            return bluetoothClient?.getState()!!
        }
        return STATE_DISCONNECTED
    }

    private val bluetoothClientListener = object : ClientListener {
        override fun messageShortReceived(client: BluetoothClient, message: String) {
            Log.d(TAG, "Bluetooth Client Message Short: $message")
            val redSpannable = SpannableString(message)
            redSpannable.setSpan(ForegroundColorSpan(Color.YELLOW), 0, message.length, 0)
            shortMessageBuilder.append(message)
            messageShortHistory.value = shortMessageBuilder
        }

        override fun messageFullReceived(client: BluetoothClient, message: String) {
            Log.d(TAG, "Bluetooth Client Message Full: $message")
            val redSpannable = SpannableString(message)
            redSpannable.setSpan(ForegroundColorSpan(Color.WHITE), 0, message.length, 0)
            fullMessageBuilder.append(message)
            messageFullHistory.value = fullMessageBuilder
        }

        override fun messageList(client: BluetoothClient, message: String) {
            fileListStringBuilder.append(message)
            if (message.contains("*")) {
                val data = fileListStringBuilder.removePrefix("LIST:")
                        .toString()

                val lastIndex = data.lastIndexOf(VARIABLE_SEPARATOR)
                listOfFileList.value = data.substring(0, lastIndex)
                        .split(VARIABLE_SEPARATOR)
                fileListStringBuilder = StringBuilder()
            }
        }

        override fun messageFile(client: BluetoothClient, message: String) {
            downloadDataStringBuilder.append(message)
            if (message.contains("*")) {
                Log.d(TAG, "Bluetooth Client Message File: $message")
                val data = downloadDataStringBuilder.removePrefix("DATA:")
                        .toString()
                val lastIndex = data.lastIndexOf(PACKAGE_SEPARATOR)
                downloadedData.value = data.substring(0, lastIndex)
                downloadDataStringBuilder = StringBuilder()
            }
        }

        override fun messageSend(client: BluetoothClient, message: String) {
            Log.d(TAG, "Bluetooth Client Message Send: $message")
            val redSpannable = SpannableString(message)
            redSpannable.setSpan(ForegroundColorSpan(Color.RED), 0, message.length, 0)

            fullMessageBuilder.append(message)
            messageFullHistory.value = fullMessageBuilder

            shortMessageBuilder.append(message)
            messageShortHistory.value = shortMessageBuilder
        }

        override fun messageInfo(client: BluetoothClient, message: String) {
            Log.d(TAG, "Bluetooth Client Info: $message")
            Toast.makeText(application, message, Toast.LENGTH_LONG).show()
        }

        override fun stateConnecting() {
            connectionStateCallback.value = StateCallback(null, STATE_CONNECTING)
        }

        override fun stateConnected(device: BluetoothDevice) {
            connectionStateCallback.value = StateCallback(device, STATE_CONNECTED)
        }

        override fun stateDisconnected(device: BluetoothDevice) {
            connectionStateCallback.value = StateCallback(device, STATE_DISCONNECTED)
        }
    }
}