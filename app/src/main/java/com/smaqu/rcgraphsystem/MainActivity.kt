package com.smaqu.rcgraphsystem

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.bluetooth.BluetoothAdapter
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.smaqu.rcgraphsystem.bluetoothconnection.BluetoothClient.Companion.STATE_CONNECTED
import com.smaqu.rcgraphsystem.bluetoothconnection.BluetoothClient.Companion.STATE_CONNECTING
import com.smaqu.rcgraphsystem.bluetoothconnection.BluetoothClient.Companion.STATE_DISCONNECTED
import com.smaqu.rcgraphsystem.dialogs.BTNotSupportedDialog
import com.smaqu.rcgraphsystem.fragmentcontrol.FragmentHandler
import com.smaqu.rcgraphsystem.fragmentcontrol.FragmentModel
import com.smaqu.rcgraphsystem.fragmentcontrol.FragmentModelImplementation
import com.smaqu.rcgraphsystem.fragments.*
import com.smaqu.rcgraphsystem.models.FileEntity
import com.smaqu.rcgraphsystem.roomdatabase.repository.FileRepository
import com.smaqu.rcgraphsystem.util.ApplicationPreferences
import com.smaqu.rcgraphsystem.viewmodel.MainVM
import com.smaqu.rcgraphsystem.viewmodel.callback.StateCallback
import java.util.*


class MainActivity : AppCompatActivity(),
        FragmentHandler {

    private val fragmentModelManagement: FragmentModel = FragmentModelImplementation.create(supportFragmentManager)
    private lateinit var mainVM: MainVM
    private lateinit var repository: FileRepository
    private lateinit var applicationPreferences: ApplicationPreferences

    companion object {
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            fragmentModelManagement.addMainFragment(MainFragment.create())
        }
        repository = FileRepository(application)
        applicationPreferences = ApplicationPreferences(this)

        mainVM = ViewModelProviders.of(this).get(MainVM::class.java)
        mainVM.registerBroadcastReceiver()

        mainVM.bluetoothAdapter.observe(this, Observer<BluetoothAdapter> {
            if (it == null) {
                BTNotSupportedDialog.create().also {
                    it.isCancelable = false
                    it.show(supportFragmentManager, BTNotSupportedDialog.TAG)
                }
            }
        })

        mainVM.listOfFileList.observe(this, Observer {
            if (it != null && it.isNotEmpty() && showFileListPreferences()) {
                showFileListFragment()
            }
        })

        mainVM.downloadedData.observe(this, Observer {
            if (it != null) {
                val fileEntity = FileEntity()
                fileEntity.name = Calendar.getInstance().time.toString()
                fileEntity.data = it

                repository.insertFile(fileEntity)
                mainVM.downloadedData.value = null
            }
        })

        mainVM.bluetoothState.observe(this, Observer<Int> {
            when (it) {
                BluetoothAdapter.STATE_OFF -> {
                    mainVM.stopBluetoothClientConnection()
                    mainVM.cancelDiscovery()
                    Log.d(TAG, "STATE_OFF")
                }
                BluetoothAdapter.STATE_TURNING_OFF -> {
                    Log.d(TAG, "STATE_TURNING_OFF")
                }
                BluetoothAdapter.STATE_ON -> {
                    Log.d(TAG, "STATE_ON")
                }
                BluetoothAdapter.STATE_TURNING_ON -> {
                    Log.d(TAG, "STATE_TURNING_OFF")
                }
            }
        })

        mainVM.connectionStateCallback.observe(this, Observer<StateCallback> {
            when (it?.state) {
                STATE_DISCONNECTED -> {
                    Log.d(TAG, "State_None")
                    supportActionBar?.title = getString(R.string.bluetooth_not_connected)
                }
                STATE_CONNECTING -> {
                    Log.d(TAG, "State_connecting")
                    supportActionBar?.title = getString(R.string.connecting)
                }
                STATE_CONNECTED -> {
                    Log.d(TAG, "state_connected")
                    supportActionBar?.title = it.device?.name
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        mainVM.PACKAGE_SEPARATOR = applicationPreferences.getSharedPreferences(
                ApplicationPreferences.PREF_PACKAGE_KEY,
                ApplicationPreferences.DEFAULT_PACKAGE_SEPARATOR)

        mainVM.VARIABLE_SEPARATOR = applicationPreferences.getSharedPreferences(
                ApplicationPreferences.PREF_VARIABLE_KEY,
                ApplicationPreferences.DEFAULT_VARIABLE_SEPARATOR)
    }

    override fun onDestroy() {
        mainVM.unRegisterBroadcastReceiver()
        super.onDestroy()
    }

    override fun showPairedDevicesFragment() {
        fragmentModelManagement.replaceFragment(PairedDevicesFragment.create())
    }

    override fun showCommunicationFragment() {
        fragmentModelManagement.replaceFragment(CommunicationFragment.create())
    }

    override fun showGraphFragment(preserveNavigation: Boolean) {
        fragmentModelManagement.replaceFragment(GraphFragment.create(), preserveNavigation)
    }

    override fun showSavedFilesFragment(preserveNavigation: Boolean) {
        fragmentModelManagement.replaceFragment(SavedFilesFragment.create(), preserveNavigation)
    }

    override fun showSettingsFragment() {
        fragmentModelManagement.replaceFragment(SettingsFragment.create())
    }

    override fun showFileListFragment() {
        fragmentModelManagement.replaceFragment(FileListToDownloadFragment.create(), false)
    }

    override fun showEditFileFragment(fileEntity: FileEntity) {
        fragmentModelManagement.replaceFragment(EditFileFragment.create(fileEntity), true)
    }

    override fun showSearchDevicesFragment() {
        fragmentModelManagement.replaceFragment(SearchDevicesFragment.create(), true)
    }

    override fun showLicenseFragment() {
        fragmentModelManagement.replaceFragment(LicenseFragment.create(), true)
    }

    override fun showFullScreenGraph() {
        fragmentModelManagement.replaceFragment(FullScreenGraph.create(), true)
    }

    private fun showFileListPreferences(): Boolean {
        return applicationPreferences.getSharedPreferences(
                ApplicationPreferences.PREF_SHOW_FILE_LIST_KEY,
                ApplicationPreferences.DEFAULT_SHOW_LIST)
    }
}
