package com.smaqu.rcgraphsystem.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import com.smaqu.rcgraphsystem.R
import com.smaqu.rcgraphsystem.adapter.PairedDevicesAdapter
import com.smaqu.rcgraphsystem.bluetoothconnection.BluetoothClient
import com.smaqu.rcgraphsystem.fragmentcontrol.FragmentHandler
import com.smaqu.rcgraphsystem.viewmodel.MainVM
import com.smaqu.rcgraphsystem.viewmodel.callback.StateCallback

import kotlinx.android.synthetic.main.fragment_paired_device.*
import kotlinx.android.synthetic.main.fragment_paired_device.view.*

class PairedDevicesFragment : Fragment(),
        View.OnClickListener {

    companion object {
        fun create() = PairedDevicesFragment()
        const val TAG = "PairedDevicesFragment"
    }

    private lateinit var mainVM: MainVM
    private lateinit var pairedDevicesAdapter: PairedDevicesAdapter
    private lateinit var recyclerPairedDevices: RecyclerView
    private lateinit var navigation: FragmentHandler

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        navigation = context as FragmentHandler
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        mainVM = ViewModelProviders.of(activity!!).get(MainVM::class.java)
        mainVM.searchPairedDevices()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_paired_device, container, false)

        view.switch_bluetooth.setOnClickListener(this)
        view.tv_bluetooth_switch.setOnClickListener(this)
        view.bt_search_device.setOnClickListener(this)

        pairedDevicesAdapter = PairedDevicesAdapter(ArrayList())
        pairedDevicesAdapter.clearAdapterItems()
        recyclerPairedDevices = view.rv_paired_devices
        recyclerPairedDevices.apply {
            adapter = pairedDevicesAdapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
        }

        mainVM.bluetoothState.observe(this, Observer<Int> {
            when (it) {
                BluetoothAdapter.STATE_TURNING_OFF -> {
                    showUi(false)
                    pairedDevicesAdapter.clearAdapterItems()
                }
                BluetoothAdapter.STATE_ON -> {
                    showUi(true)
                    mainVM.searchPairedDevices()
                }
            }
        })

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

        pairedDevicesAdapter.setOnClickListener(object : PairedDevicesAdapter.ClickListener {
            override fun onItemClick(bluetoothDevice: BluetoothDevice) {
                mainVM.connectBluetoothDevice(bluetoothDevice, false)
            }
        })

        mainVM.pairedDevices.observe(this, Observer<Set<BluetoothDevice>> {
            if (it != null && it.isNotEmpty() && mainVM.isBluetoothEnable()) {
                pairedDevicesAdapter.updateAdapterItems(it)
            }
        })

        return view
    }

    override fun onResume() {
        super.onResume()
        showUi(mainVM.isBluetoothEnable())
    }

    override fun onDestroy() {
        if (mainVM.getBlueToothState() == BluetoothClient.STATE_CONNECTING) {
            mainVM.stopBluetoothClientConnection()
        }
        super.onDestroy()
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            bt_search_device.id -> {
                navigation.showSearchDevicesFragment()
            }
            tv_bluetooth_switch.id -> {
                switch_bluetooth.isChecked = !switch_bluetooth.isChecked
            }
            switch_bluetooth.id -> {
                mainVM.enableBluetooth((view as Switch).isChecked)
            }
        }
    }

    private fun showUi(value: Boolean) {
        view?.switch_bluetooth?.isChecked = value
        view?.bt_search_device?.isEnabled = value
    }

    private fun showConnectingUi(value: Boolean) {
        if (value) {
            view?.pb_connecting_paired?.visibility = View.VISIBLE
            view?.view_disable_recycler?.visibility = View.VISIBLE
            view?.bt_search_device?.isEnabled = false
        } else {
            view?.pb_connecting_paired?.visibility = View.GONE
            view?.view_disable_recycler?.visibility = View.GONE
            view?.bt_search_device?.isEnabled = true
        }
    }
}