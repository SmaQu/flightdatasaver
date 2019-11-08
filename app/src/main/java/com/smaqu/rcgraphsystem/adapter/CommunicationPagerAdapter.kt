package com.smaqu.rcgraphsystem.adapter

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.smaqu.rcgraphsystem.R
import com.smaqu.rcgraphsystem.fragments.ButtonsFragment
import com.smaqu.rcgraphsystem.fragments.TerminalFragment

class CommunicationPagerAdapter(fm: FragmentManager, private val context: Context) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment? {
        when (position) {
            0 -> {
                return ButtonsFragment.create()
            }
            1 -> {
                return TerminalFragment.create()
            }
        }
        return null
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence {
        when (position) {
            0 -> {
                return context.resources.getString(R.string.advanced_terminal)
            }
            1 -> {
                return context.resources.getString(R.string.terminal)
            }
        }
        return "Console"
    }
}