package com.smaqu.rcgraphsystem.fragmentcontrol

import com.smaqu.rcgraphsystem.models.FileEntity

interface FragmentHandler {

    fun showPairedDevicesFragment()

    fun showCommunicationFragment()

    fun showGraphFragment(preserveNavigation: Boolean)

    fun showSavedFilesFragment(preserveNavigation: Boolean)

    fun showSettingsFragment()

    fun showFileListFragment()

    fun showEditFileFragment(fileEntity: FileEntity)

    fun showSearchDevicesFragment()

    fun showLicenseFragment()

    fun showFullScreenGraph()
}