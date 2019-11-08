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
import com.smaqu.rcgraphsystem.fragments.ButtonsFragment.Companion.EMPTY
import com.smaqu.rcgraphsystem.util.ApplicationPreferences
import kotlinx.android.synthetic.main.dialog_advanced_buttons.view.*

class AdvancedButtonsDialog : DialogFragment() {

    private var sharedPrefLabel = "label_-1"
    private var sharedPrefCommand = "command_-1"
    private lateinit var applicationPreferences: ApplicationPreferences

    companion object {
        const val TAG = "AdvancedButtonsDialog"

        private const val KEY_BUTTON_ID = "key_command"

        fun create(buttonID: Int): AdvancedButtonsDialog {
            return AdvancedButtonsDialog().apply {
                arguments = Bundle().apply {
                    putInt(KEY_BUTTON_ID, buttonID)
                }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AppCompatDialog(activity, R.style.DialogStyleNoTitle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_advanced_buttons, container, false)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        applicationPreferences = ApplicationPreferences(requireActivity())

        val buttonID = arguments?.getInt(KEY_BUTTON_ID, -1)
        sharedPrefLabel = "label_$buttonID"
        sharedPrefCommand = "command_$buttonID"

        view.et_button_label.hint =
                applicationPreferences.getSharedPreferences(sharedPrefLabel, EMPTY)
        view.et_button_command.hint =
                applicationPreferences.getSharedPreferences(sharedPrefCommand, EMPTY)

        if (savedInstanceState == null) {
            view.et_button_label.setText(
                    applicationPreferences.getSharedPreferences(sharedPrefLabel, EMPTY))
            view.et_button_command.setText(
                    applicationPreferences.getSharedPreferences(sharedPrefCommand, EMPTY))
        }

        view.bt_save.setOnClickListener({
            saveToSharedPreferences(view)
            dismiss()
        })
        return view
    }

    private fun saveToSharedPreferences(view: View) {
        var label = EMPTY
        var command = EMPTY
        if (view.et_button_label.text.isNotEmpty()) {
            label = view.et_button_label?.text.toString()
        }
        if (view.et_button_command.text.isNotEmpty()) {
            command = view.et_button_command.text.toString()
        }

        applicationPreferences.Editor()
                .putString(sharedPrefLabel, label)
                .putString(sharedPrefCommand, command)
                .apply()
    }
}