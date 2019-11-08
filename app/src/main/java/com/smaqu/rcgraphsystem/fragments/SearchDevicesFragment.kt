package com.smaqu.rcgraphsystem.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.smaqu.rcgraphsystem.R
import com.smaqu.rcgraphsystem.adapter.DiscoveredDevicesAdapter
import com.smaqu.rcgraphsystem.bluetoothconnection.BluetoothClient
import com.smaqu.rcgraphsystem.bluetoothconnection.BluetoothClient.Companion.STATE_CONNECTING
import com.smaqu.rcgraphsystem.dialogs.BluetoothConnectionTypeDialog
import com.smaqu.rcgraphsystem.viewmodel.MainVM
import com.smaqu.rcgraphsystem.viewmodel.SearchDeviceVM
import com.smaqu.rcgraphsystem.viewmodel.callback.StateCallback
import kotlinx.android.synthetic.main.fragment_search_devices.view.*


class SearchDevicesFragment : Fragment() {

    companion object {
        fun create() = SearchDevicesFragment()
        const val TAG = "SearchDevicesFragment"
    }

    private lateinit var mainVM: MainVM
    private lateinit var searchDeviceVM: SearchDeviceVM
    private lateinit var discoveredDevicesAdapter: DiscoveredDevicesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true

        discoveredDevicesAdapter = DiscoveredDevicesAdapter(ArrayList())
        discoveredDevicesAdapter.clearAdapterItems()

        searchDeviceVM = ViewModelProviders.of(this).get(SearchDeviceVM::class.java)
        mainVM = ViewModelProviders.of(activity!!).get(MainVM::class.java)

        searchDeviceVM.registerBroadcastReceiver()
        mainVM.startDiscovery()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_search_devices, container, false)

        val recyclerDiscoveredDevices = view.rv_discovered_devices
        recyclerDiscoveredDevices.apply {
            adapter = discoveredDevicesAdapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
        }

        mainVM.connectionStateCallback.observe(this, Observer<StateCallback> {
            when (it?.state) {
                BluetoothClient.STATE_DISCONNECTED -> {
                    Log.d(TAG, "State_None")
                    showConnectingUi(false)
                }
                BluetoothClient.STATE_CONNECTING -> {
                    Log.d(TAG, "State_connecting")
                    showConnectingUi(true)
                }
                BluetoothClient.STATE_CONNECTED -> {
                    Log.d(TAG, "state_connected")
                    showConnectingUi(false)
                    //TODO close fragment
                }
            }
        })

        searchDeviceVM.discoveredDevices.observe(this, Observer<BluetoothDevice> {
            Log.d(TAG, "Device found: ${it?.name}")
            if (it != null && discoveredDevicesAdapter.getItemPosition(it) == -1) {
                discoveredDevicesAdapter.addAdapterItem(it)
            }
        })

        searchDeviceVM.discoveryFinished.observe(this, Observer<Boolean> {
            if (it != null && it) {
                showDiscoveringProgressBar(!it)
            }
        })

        discoveredDevicesAdapter.setOnClickListener(object : DiscoveredDevicesAdapter.ClickListener {
            override fun onItemClick(bluetoothDevice: BluetoothDevice) {
                BluetoothConnectionTypeDialog.create(bluetoothDevice).also {
                    it.show(fragmentManager, BluetoothConnectionTypeDialog.TAG)
                }
            }
        })
        return view
    }

    override fun onStart() {
        super.onStart()
        showDiscoveringProgressBar(mainVM.isDiscovering())
    }

    override fun onDestroy() {
        searchDeviceVM.unRegisterBroadcastReceiver()
        mainVM.cancelDiscovery()
        if (mainVM.getBlueToothState() == STATE_CONNECTING) {
            mainVM.stopBluetoothClientConnection()
        }
        super.onDestroy()
    }

    private fun showDiscoveringProgressBar(value: Boolean) {
        view?.pb_discovering?.visibility =
                if (value) View.VISIBLE else View.GONE
    }

    private fun showConnectingUi(value: Boolean) {
        if (value) {
            view?.view_disable_recycler?.visibility = View.VISIBLE
            view?.pb_connecting?.visibility = View.VISIBLE
        } else {
            view?.view_disable_recycler?.visibility = View.GONE
            view?.pb_connecting?.visibility = View.GONE
        }
    }
}