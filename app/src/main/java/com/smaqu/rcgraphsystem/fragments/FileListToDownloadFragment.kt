package com.smaqu.rcgraphsystem.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RelativeLayout
import android.widget.Toast
import com.smaqu.rcgraphsystem.R
import com.smaqu.rcgraphsystem.adapter.FileToDownloadAdapter
import com.smaqu.rcgraphsystem.util.ApplicationPreferences
import com.smaqu.rcgraphsystem.viewmodel.MainVM
import kotlinx.android.synthetic.main.fragment_file_list_to_download.view.*

class FileListToDownloadFragment : Fragment() {

    private lateinit var extraSignCb: CheckBox
    private lateinit var rootExtraSign: RelativeLayout
    private lateinit var applicationPreferences: ApplicationPreferences

    companion object {
        fun create() = FileListToDownloadFragment()
        private const val TAG = "FileListToDownloadFrag"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_file_list_to_download, container, false)
        extraSignCb = view.check_extra_sign
        rootExtraSign = view.root_check_box

        applicationPreferences = ApplicationPreferences(requireActivity())

        extraSignCb.isChecked = applicationPreferences
                .getSharedPreferences(
                        ApplicationPreferences.PREF_SHOW_EXTRA_SIGN_KEY,
                        false)

        val fileToDownloadAdapter = FileToDownloadAdapter(ArrayList())
        val mainVM = ViewModelProviders.of(activity!!).get(MainVM::class.java)

        mainVM.listOfFileList.observe(this, Observer {
            it?.let { list ->
                fileToDownloadAdapter.updateAdapter(list)
            }
        })

        val recyclerView = view.rv_file_list
        recyclerView.apply {
            adapter = fileToDownloadAdapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
        }

        fileToDownloadAdapter.setOnClickListener(object : FileToDownloadAdapter.ClickListener {
            override fun onItemClick(fileName: String) {
                var downloadQuery = "Download:"
                downloadQuery += fileName

                if (extraSignCb.isChecked) {
                    downloadQuery += getExtraSign()
                }

                mainVM.sendMessage(downloadQuery)
                Toast.makeText(requireContext(), getString(R.string.download_started), Toast.LENGTH_LONG).show()
                mainVM.listOfFileList.value = null
                activity?.supportFragmentManager?.popBackStack()
            }
        })

        extraSignCb.setOnCheckedChangeListener { compoundButton, isChecked ->
            applicationPreferences.Editor()
                    .putBoolean(ApplicationPreferences.PREF_SHOW_EXTRA_SIGN_KEY, isChecked)
                    .apply()
        }

        rootExtraSign.setOnClickListener {
            extraSignCb.toggle()
        }

        view.isFocusableInTouchMode = true
        view.requestFocus()
        view.setOnKeyListener(View.OnKeyListener { view, keyCode, keyEvent ->
            if (keyEvent?.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                mainVM.listOfFileList.value = null
                activity?.supportFragmentManager?.popBackStack()
                return@OnKeyListener true
            }
            false
        })

        return view
    }

    private fun getExtraSign(): String {
        return applicationPreferences.getSharedPreferences(
                ApplicationPreferences.PREF_EXTRA_SIGN_KEY,
                ApplicationPreferences.DEFAULT_EXTRA_SIGN)
    }
}