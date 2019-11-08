package com.smaqu.rcgraphsystem.dialogs

import android.app.Dialog
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatDialog
import android.support.v7.widget.CardView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.smaqu.rcgraphsystem.R
import com.smaqu.rcgraphsystem.util.ApplicationPreferences
import com.smaqu.rcgraphsystem.util.ApplicationPreferences.Companion.DEFAULT_PACKAGE_SEPARATOR
import com.smaqu.rcgraphsystem.util.ApplicationPreferences.Companion.DEFAULT_VARIABLE_SEPARATOR
import com.smaqu.rcgraphsystem.util.ApplicationPreferences.Companion.PREF_PACKAGE_KEY
import com.smaqu.rcgraphsystem.util.ApplicationPreferences.Companion.PREF_VARIABLE_KEY
import com.smaqu.rcgraphsystem.viewmodel.MainVM
import kotlinx.android.synthetic.main.dialog_change_text.view.*

class SettingSetterDialog : DialogFragment() {

    companion object {
        const val TAG = "SettingSetterDialog"

        private const val ARG_INFO = "arg_info"
        private const val ARG_PREF_KEY = "arg_pref_key"
        private const val ARG_DEFAULT_VALUE = "arg_default_value"

        fun create(info: String, prefKey: String, defaultValue: String): SettingSetterDialog {
            return SettingSetterDialog().apply {
                arguments = Bundle().apply {
                    putString(ARG_INFO, info)
                    putString(ARG_PREF_KEY, prefKey)
                    putString(ARG_DEFAULT_VALUE, defaultValue)
                }
            }
        }
    }

    private val onClickListener = { _: View ->
        var newPreferencesValue = preferenceDefaultValue

        if (editNewSeparatorValue.text.isNotEmpty()) {
            newPreferencesValue = editNewSeparatorValue.text.toString()
        }

        if (checkRepeatability(newPreferencesValue)) {
            Toast.makeText(context,
                    resources.getString(R.string.separator_need_to_be_different),
                    Toast.LENGTH_SHORT).show()
        } else {
            applicationPreferences.Editor()
                    .putString(preferenceKey, newPreferencesValue)
                    .apply()
            if (preferenceKey == PREF_VARIABLE_KEY || preferenceKey == PREF_PACKAGE_KEY) {
                mainVM.PACKAGE_SEPARATOR = applicationPreferences.getSharedPreferences(
                        ApplicationPreferences.PREF_PACKAGE_KEY,
                        ApplicationPreferences.DEFAULT_PACKAGE_SEPARATOR)

                mainVM.VARIABLE_SEPARATOR = applicationPreferences.getSharedPreferences(
                        ApplicationPreferences.PREF_VARIABLE_KEY,
                        ApplicationPreferences.DEFAULT_VARIABLE_SEPARATOR)
            }

            dismiss()
        }
    }

    private lateinit var applicationPreferences: ApplicationPreferences
    private lateinit var dialogTitleInfo: String
    private lateinit var preferenceKey: String
    private lateinit var preferenceDefaultValue: String
    private lateinit var mainVM: MainVM

    private lateinit var textDialogTitleInfo: TextView
    private lateinit var editNewSeparatorValue: EditText
    private lateinit var buttonSaveNewValueState: Button

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AppCompatDialog(activity, R.style.DialogStyleNoTitle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_change_text, container, false)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mainVM = ViewModelProviders.of(activity!!).get(MainVM::class.java)

        textDialogTitleInfo = view.text_dialog_title_info
        editNewSeparatorValue = view.edit_new_separator_value

        buttonSaveNewValueState = view.button_save_new_state
        buttonSaveNewValueState.setOnClickListener(onClickListener)

        dialogTitleInfo = arguments?.getString(ARG_INFO)!!
        preferenceKey = arguments?.getString(ARG_PREF_KEY)!!
        preferenceDefaultValue = arguments?.getString(ARG_DEFAULT_VALUE)!!
        applicationPreferences = ApplicationPreferences(requireActivity())

        val prefValue = applicationPreferences.getSharedPreferences(preferenceKey, preferenceDefaultValue)

        textDialogTitleInfo.text = dialogTitleInfo
        editNewSeparatorValue.hint = prefValue

        if (savedInstanceState == null) {
            editNewSeparatorValue.setText(prefValue)
        }

        return view
    }

    private fun checkRepeatability(newPreferencesValue: String): Boolean {
        if (isPackagePreferenceTheSame(newPreferencesValue)
                || isVariablePreferenceTheSame(newPreferencesValue)) {
            return true
        }
        return false
    }

    private fun isVariablePreferenceTheSame(newPreferencesValue: String): Boolean {
        return preferenceKey == PREF_VARIABLE_KEY &&
                newPreferencesValue == applicationPreferences.getSharedPreferences(PREF_PACKAGE_KEY, DEFAULT_PACKAGE_SEPARATOR)
    }

    private fun isPackagePreferenceTheSame(newPreferencesValue: String): Boolean {
        return preferenceKey == PREF_PACKAGE_KEY &&
                newPreferencesValue == applicationPreferences.getSharedPreferences(PREF_VARIABLE_KEY, DEFAULT_VARIABLE_SEPARATOR)
    }
}