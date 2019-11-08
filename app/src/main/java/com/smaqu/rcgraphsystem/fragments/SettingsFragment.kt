package com.smaqu.rcgraphsystem.fragments

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import android.widget.*
import com.smaqu.rcgraphsystem.R
import com.smaqu.rcgraphsystem.dialogs.SettingSetterDialog
import com.smaqu.rcgraphsystem.dialogs.SimplyChooseDialog
import com.smaqu.rcgraphsystem.dialogs.SimplyChooseDialog.Companion.SET_ARDUINO_DEFAULT
import com.smaqu.rcgraphsystem.dialogs.SimplyChooseDialog.Companion.SET_COMMUNICATION_DEFAULT
import com.smaqu.rcgraphsystem.fragmentcontrol.FragmentHandler
import com.smaqu.rcgraphsystem.util.ApplicationPreferences
import com.smaqu.rcgraphsystem.util.ApplicationPreferences.Companion.DEFAULT_EXTRA_SIGN
import com.smaqu.rcgraphsystem.util.ApplicationPreferences.Companion.DEFAULT_PACKAGE_SEPARATOR
import com.smaqu.rcgraphsystem.util.ApplicationPreferences.Companion.DEFAULT_SHOW_LIST
import com.smaqu.rcgraphsystem.util.ApplicationPreferences.Companion.DEFAULT_VARIABLE_SEPARATOR
import com.smaqu.rcgraphsystem.util.ApplicationPreferences.Companion.PREF_EXTRA_SIGN_KEY
import com.smaqu.rcgraphsystem.util.ApplicationPreferences.Companion.PREF_PACKAGE_KEY
import com.smaqu.rcgraphsystem.util.ApplicationPreferences.Companion.PREF_SHOW_FILE_LIST_KEY
import com.smaqu.rcgraphsystem.util.ApplicationPreferences.Companion.PREF_VARIABLE_KEY
import com.smaqu.rcgraphsystem.viewmodel.MainVM
import kotlinx.android.synthetic.main.fragment_settings.view.*

class SettingsFragment : Fragment(),
        View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {

    private lateinit var applicationPreferences: ApplicationPreferences
    private lateinit var fragmentHandler: FragmentHandler
    private lateinit var mainVM: MainVM

    private lateinit var extraSignTV: TextView
    private lateinit var showListCb: CheckBox
    private lateinit var rootShowList: RelativeLayout
    private lateinit var defaultExtraSignSetterTV: Button

    private lateinit var variableSeparatorTV: TextView
    private lateinit var packageSeparatorTV: TextView
    private lateinit var defaultSeparatorsSetterTV: Button

    companion object {
        const val TAG = "SettingsFragment"
        fun create() = SettingsFragment()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        fragmentHandler = context as FragmentHandler
        applicationPreferences = ApplicationPreferences(requireActivity())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        setHasOptionsMenu(true)
        setUpViews(view)

        mainVM = ViewModelProviders.of(activity!!).get(MainVM::class.java)

        extraSignTV.setOnClickListener(this)
        rootShowList.setOnClickListener(this)
        showListCb.setOnCheckedChangeListener(this)
        defaultExtraSignSetterTV.setOnClickListener(this)

        variableSeparatorTV.setOnClickListener(this)
        packageSeparatorTV.setOnClickListener(this)
        defaultSeparatorsSetterTV.setOnClickListener(this)

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.settings_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.item_show_license -> {
                fragmentHandler.showLicenseFragment()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            SET_COMMUNICATION_DEFAULT -> {
                if (resultCode == Activity.RESULT_OK) {
                    setDefaultCommunicationPreferences()
                }
            }
            SET_ARDUINO_DEFAULT -> {
                if (resultCode == Activity.RESULT_OK) {
                    setDefaultArduinoPreferences()
                }
            }
        }
    }

    override fun onCheckedChanged(p0: CompoundButton?, isChecked: Boolean) {
        setShowListPreferences(isChecked)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.text_extra_sign -> {
                showSettingSetterDialog(
                        getString(R.string.extra_sign),
                        PREF_EXTRA_SIGN_KEY,
                        DEFAULT_EXTRA_SIGN)
            }

            R.id.root_show_list -> {
                showListCb.toggle()
            }

            R.id.text_extra_sign_default -> {
                showSimpleChooserDialog(
                        getString(R.string.communication_configuration),
                        getString(R.string.set_communication_default_dialog),
                        SET_COMMUNICATION_DEFAULT)
            }

            R.id.text_variable_separator -> {
                showSettingSetterDialog(
                        getString(R.string.variable_separator_info),
                        PREF_VARIABLE_KEY,
                        DEFAULT_VARIABLE_SEPARATOR)
            }

            R.id.text_package_separator -> {
                showSettingSetterDialog(
                        getString(R.string.package_separator_info),
                        PREF_PACKAGE_KEY,
                        DEFAULT_PACKAGE_SEPARATOR)
            }

            R.id.text_arduino_conf_default -> {
                showSimpleChooserDialog(
                        getString(R.string.arduino_configuration),
                        getString(R.string.set_arduino_default_dialog),
                        SET_ARDUINO_DEFAULT)
            }
        }
    }

    private fun showSettingSetterDialog(dialogTitle: String, preferencesKey: String, defaultValue: String) {
        SettingSetterDialog.create(dialogTitle, preferencesKey, defaultValue)
                .also {
                    it.show(fragmentManager, SettingSetterDialog.TAG)
                }
    }

    private fun showSimpleChooserDialog(dialogTitle: String, dialogInfo: String, requestCode: Int) {
        SimplyChooseDialog.create(dialogTitle, dialogInfo)
                .also {
                    it.setTargetFragment(this, requestCode)
                    it.show(fragmentManager, SimplyChooseDialog.TAG)
                }
    }

    private fun setDefaultArduinoPreferences() {
        applicationPreferences.Editor()
                .putString(PREF_PACKAGE_KEY, DEFAULT_PACKAGE_SEPARATOR)
                .putString(PREF_VARIABLE_KEY, DEFAULT_VARIABLE_SEPARATOR)
                .apply()

        mainVM.PACKAGE_SEPARATOR = DEFAULT_PACKAGE_SEPARATOR
        mainVM.VARIABLE_SEPARATOR = DEFAULT_VARIABLE_SEPARATOR
    }

    private fun setDefaultCommunicationPreferences() {
        applicationPreferences.Editor()
                .putString(PREF_EXTRA_SIGN_KEY, DEFAULT_EXTRA_SIGN)
                .putBoolean(PREF_SHOW_FILE_LIST_KEY, DEFAULT_SHOW_LIST)
                .apply()
        initCheckBoxs()
    }

    private fun setShowListPreferences(isChecked: Boolean) {
        applicationPreferences.Editor()
                .putBoolean(PREF_SHOW_FILE_LIST_KEY, isChecked)
                .apply()
    }

    private fun setUpViews(view: View) {
        extraSignTV = view.text_extra_sign
        rootShowList = view.root_show_list
        showListCb = view.check_show_list
        defaultExtraSignSetterTV = view.text_extra_sign_default

        variableSeparatorTV = view.text_variable_separator
        packageSeparatorTV = view.text_package_separator
        defaultSeparatorsSetterTV = view.text_arduino_conf_default
        initCheckBoxs()
    }

    private fun initCheckBoxs() {
        showListCb.isChecked = applicationPreferences
                .getSharedPreferences(
                        PREF_SHOW_FILE_LIST_KEY,
                        DEFAULT_SHOW_LIST)
    }
}