package com.smaqu.rcgraphsystem.dialogs

import android.app.Dialog
import android.arch.lifecycle.ViewModelProviders
import android.bluetooth.BluetoothDevice
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.smaqu.rcgraphsystem.R
import com.smaqu.rcgraphsystem.viewmodel.MainVM
import kotlinx.android.synthetic.main.dialog_connection_type.view.*

class BluetoothConnectionTypeDialog : DialogFragment() {
    companion object {
        const val TAG = "BluetoothConnectionTypeDialog"
        private const val KEY_BLUETOOTH_DEVICE = "bluetooth_device"
        fun create(bluetoothDevice: BluetoothDevice): BluetoothConnectionTypeDialog {
            return BluetoothConnectionTypeDialog().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_BLUETOOTH_DEVICE,bluetoothDevice)
                }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AppCompatDialog(activity, R.style.DialogStyleNoTitle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_connection_type, container, false)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val bluetoothDevice: BluetoothDevice = arguments?.getParcelable(KEY_BLUETOOTH_DEVICE)!!
        val mainVM = ViewModelProviders.of(activity!!).get(MainVM::class.java)

        view.button_pin_connection.setOnClickListener{
            mainVM.connectBluetoothDevice(bluetoothDevice, true)
            dismiss()
        }
        view.button_without_pin_connection.setOnClickListener{
            mainVM.connectBluetoothDevice(bluetoothDevice, false)
            dismiss()
        }

        return view
    }
}