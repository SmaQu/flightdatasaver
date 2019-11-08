package com.smaqu.rcgraphsystem.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RelativeLayout
import com.smaqu.rcgraphsystem.R
import com.smaqu.rcgraphsystem.util.ApplicationPreferences
import com.smaqu.rcgraphsystem.viewmodel.MainVM
import kotlinx.android.synthetic.main.fragment_terminal.view.*

class TerminalFragment : Fragment() {

    private lateinit var extraSignCb: CheckBox
    private lateinit var rootExtraSign: RelativeLayout
    private lateinit var applicationPreferences: ApplicationPreferences

    companion object {
        fun create() = TerminalFragment()
        private const val TAG = "TerminalFragment"
    }

    private lateinit var mainVM: MainVM

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_terminal, container, false)
        extraSignCb = view.check_extra_sign
        rootExtraSign = view.root_check_box

        applicationPreferences = ApplicationPreferences(requireActivity())

        extraSignCb.isChecked = applicationPreferences
                .getSharedPreferences(
                        ApplicationPreferences.PREF_SHOW_EXTRA_SIGN_KEY,
                        false)

        mainVM = ViewModelProviders.of(activity!!).get(MainVM::class.java)

        if (savedInstanceState == null) {
            view.tv_terminal_communication_history.text = mainVM.messageFullHistory.value
        }

        mainVM.messageFullHistory.observe(this, Observer<StringBuilder> {
            view.tv_terminal_communication_history.text = it
        })

        extraSignCb.setOnCheckedChangeListener { compoundButton, isChecked ->
            applicationPreferences.Editor()
                    .putBoolean(ApplicationPreferences.PREF_SHOW_EXTRA_SIGN_KEY, isChecked)
                    .apply()
        }

        rootExtraSign.setOnClickListener {
            extraSignCb.toggle()
        }

        view.iv_send_command.setOnClickListener({
            val enterCommand = view.et_enter_command_area
            var sendQuery = enterCommand.text.toString()

            if (extraSignCb.isChecked) {
                sendQuery += getExtraSign()
            }

            mainVM.sendMessage(sendQuery)
            enterCommand.setText("")
        })
        return view
    }

    private fun getExtraSign(): String {
        return applicationPreferences.getSharedPreferences(
                ApplicationPreferences.PREF_EXTRA_SIGN_KEY,
                ApplicationPreferences.DEFAULT_EXTRA_SIGN)
    }
}