package com.smaqu.rcgraphsystem.dialogs

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.smaqu.rcgraphsystem.R
import kotlinx.android.synthetic.main.dialog_bt_not_supported.view.*

class BTNotSupportedDialog : DialogFragment() {

    companion object {
        const val TAG = "BTNotSupportedDialog"
        fun create() = BTNotSupportedDialog()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AppCompatDialog(activity, R.style.DialogStyleNoTitle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_bt_not_supported, container, false)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        view.button_close_device.setOnClickListener {
            activity?.finish()
        }
        return view
    }
}