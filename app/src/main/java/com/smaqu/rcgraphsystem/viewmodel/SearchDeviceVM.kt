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
import android.util.Log

class SearchDeviceVM(application: Application) : AndroidViewModel(application) {
    var discoveredDevices: MutableLiveData<BluetoothDevice> = MutableLiveData()
        private set
    var discoveryFinished: MutableLiveData<Boolean> = MutableLiveData()
        private set

    companion object {
        const val TAG = "SearchDeviceVM"
    }

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    Log.d(TAG, "ACTION_FOUND")
                    discoveredDevices.value = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    Log.d(TAG, "ACTION_DISCOVERY_FINISHED")
                    discoveryFinished.value = true
                }
            }
        }
    }

    fun registerBroadcastReceiver() {
        val intentFilter = IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        }
        getApplication<Application>().registerReceiver(broadcastReceiver, intentFilter)
    }

    fun unRegisterBroadcastReceiver() {
        getApplication<Application>().unregisterReceiver(broadcastReceiver)
    }
}