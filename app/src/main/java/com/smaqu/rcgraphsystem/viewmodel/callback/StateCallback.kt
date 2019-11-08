package com.smaqu.rcgraphsystem.viewmodel.callback

import android.bluetooth.BluetoothDevice

data class StateCallback(val device: BluetoothDevice?,val state: Byte)