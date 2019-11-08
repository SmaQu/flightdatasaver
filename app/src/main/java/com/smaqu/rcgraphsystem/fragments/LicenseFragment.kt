package com.smaqu.rcgraphsystem.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import com.smaqu.rcgraphsystem.R
import kotlinx.android.synthetic.main.fragment_license.view.*

class LicenseFragment : Fragment() {

    companion object {
        const val TAG = "LicenseFragment"
        fun create() = LicenseFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_license, container, false)
        view.web_view.loadUrl("file:///android_asset/license.html")
        return view
    }
}