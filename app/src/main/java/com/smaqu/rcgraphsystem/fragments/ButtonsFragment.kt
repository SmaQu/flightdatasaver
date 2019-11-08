package com.smaqu.rcgraphsystem.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.smaqu.rcgraphsystem.R
import com.smaqu.rcgraphsystem.dialogs.AdvancedButtonsDialog
import com.smaqu.rcgraphsystem.util.ApplicationPreferences.Companion.SETTINGS_PREFERENCE_FILE
import com.smaqu.rcgraphsystem.viewmodel.MainVM
import kotlinx.android.synthetic.main.fragment_advanced_terminal.view.*


class ButtonsFragment : Fragment(),
        View.OnClickListener,
        View.OnLongClickListener {

    private lateinit var mainVM: MainVM
    private var listLabelsSP = ArrayList<String>()
    private var listCommandSP = ArrayList<String>()
    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        fun create() = ButtonsFragment()
        private const val TAG = "ButtonsFragment"
        const val EMPTY = "Empty"

        private val listButtonsID = listOf(
                R.id.bt_first,
                R.id.bt_second,
                R.id.bt_third,
                R.id.bt_fourth,
                R.id.bt_fifth,
                R.id.bt_sixth,
                R.id.bt_seventh,
                R.id.bt_eighth,
                R.id.bt_ninth,
                R.id.bt_tenth,
                R.id.bt_eleventh,
                R.id.bt_twelfth)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_advanced_terminal, container, false)
        mainVM = ViewModelProviders.of(activity!!).get(MainVM::class.java)
        createSPLists()

        val smallTerminal = view.tv_communication_history
        smallTerminal.movementMethod = ScrollingMovementMethod()

        if (savedInstanceState == null) {
            smallTerminal.text = mainVM.messageFullHistory.value
        }

        mainVM.messageShortHistory.observe(this, Observer<StringBuilder> {
            smallTerminal.text = it
        })

        sharedPreferences = activity?.getSharedPreferences(SETTINGS_PREFERENCE_FILE, Context.MODE_PRIVATE)!!
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferencesListener)

        for ((index, value) in listLabelsSP.withIndex()) {
            view.findViewById<Button>(listButtonsID[index])?.text =
                    sharedPreferences.getString(value, EMPTY)
        }
        return view
    }

    override fun onDestroy() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferencesListener)
        super.onDestroy()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUpOnClickListener()
        setUpOnLongClickListener()
    }

    override fun onClick(view: View?) {
        val commandMessage = sharedPreferences.getString("command_${view?.id}", "Empty")
        mainVM.sendMessage(commandMessage)
    }

    override fun onLongClick(view: View?): Boolean {
        AdvancedButtonsDialog.create(view?.id!!).also {
            it.show(fragmentManager, AdvancedButtonsDialog.TAG)
        }
        return true
    }

    private val sharedPreferencesListener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
        for ((index, value) in listLabelsSP.withIndex()) {
            if (key == value) {
                view?.findViewById<Button>(listButtonsID[index])?.text =
                        sharedPreferences?.getString(value, EMPTY)
            }
        }
    }

    private fun setUpOnClickListener() {
        listButtonsID.forEach {
            view?.findViewById<Button>(it)?.setOnClickListener(this)
        }
    }

    private fun setUpOnLongClickListener() {
        listButtonsID.forEach {
            view?.findViewById<Button>(it)?.setOnLongClickListener(this)
        }
    }

    private fun createSPLists() {
        listButtonsID.forEach {
            listCommandSP.add("command_$it")
            listLabelsSP.add("label_$it")
        }
    }
}