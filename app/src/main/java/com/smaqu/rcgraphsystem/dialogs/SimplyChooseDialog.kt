package com.smaqu.rcgraphsystem.dialogs

import android.app.Activity
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
import kotlinx.android.synthetic.main.dialog_simply_choose.view.*

class SimplyChooseDialog : DialogFragment() {

    companion object {
        const val TAG = "SimplyChooseDialog"
        const val DELETE_FILE_REQUEST = 1337
        const val SET_COMMUNICATION_DEFAULT = 1410
        const val SET_ARDUINO_DEFAULT = 966

        private const val ARG_DIALOG_TITLE= "arg_dialog_title"
        private const val ARG_DIALOG_INFO = "arg_dialog_info"

        fun create(dialogTitle: String, dialogInfo: String): SimplyChooseDialog {
            return SimplyChooseDialog().apply {
                arguments = Bundle().apply {
                    putString(ARG_DIALOG_TITLE, dialogTitle)
                    putString(ARG_DIALOG_INFO, dialogInfo)
                }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AppCompatDialog(activity, R.style.DialogStyleNoTitle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_simply_choose, container, false)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        view.text_dialog_title.text = arguments?.getString(ARG_DIALOG_TITLE)
        view.text_dialog_info.text = arguments?.getString(ARG_DIALOG_INFO)

        view.button_yes.setOnClickListener {
            targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, activity?.intent)
            dismiss()
        }
        view.button_no.setOnClickListener {
            dismiss()
        }
        return view
    }
}