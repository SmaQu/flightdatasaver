package com.smaqu.rcgraphsystem.fragmentcontrol

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import com.smaqu.rcgraphsystem.R


internal interface FragmentModel {
    fun addMainFragment(mainFragment: Fragment)

    fun isMainFragment(): Boolean

    fun backToMainFragment()

    fun replaceFragment(newFragment: Fragment)

    fun replaceFragment(newFragment: Fragment, preserveNavigation: Boolean)
}

internal class FragmentModelImplementation private constructor(private val fragmentManager: FragmentManager) :
        FragmentModel {

    companion object {
        fun create(fragmentManager: FragmentManager) = FragmentModelImplementation(fragmentManager)
    }

    override fun addMainFragment(mainFragment: Fragment) {
        if (fragmentManager.fragments.count() == 0)
            fragmentManager.beginTransaction()
                    .add(R.id.fl_activity_main, mainFragment, mainFragment.javaClass.name)
                    .setPrimaryNavigationFragment(mainFragment)
                    .commit()
    }

    override fun isMainFragment(): Boolean {
        return fragmentManager.primaryNavigationFragment != null && fragmentManager.primaryNavigationFragment.isVisible
    }

    override fun backToMainFragment() {
        popAllBackStackEntries(fragmentManager)
    }

    override fun replaceFragment(newFragment: Fragment) {
        replaceFragment(newFragment, false)
    }

    override fun replaceFragment(newFragment: Fragment, preserveNavigation: Boolean) {
        val fragmentManager: FragmentManager = fragmentManager
        val currentFragment: Fragment = fragmentManager.findFragmentById(R.id.fl_activity_main)
        val currentFragmentTag: String = currentFragment.javaClass.name
        val newFragmentTag: String = newFragment.javaClass.name

        if (currentFragmentTag == newFragmentTag) return

        if (!preserveNavigation) popAllBackStackEntries(fragmentManager)

        fragmentManager.beginTransaction()
                .replace(R.id.fl_activity_main, newFragment, newFragmentTag)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack(newFragmentTag)
                .commit()
    }

    private fun popAllBackStackEntries(fragmentManager: FragmentManager) {
        if (fragmentManager.backStackEntryCount > 0)
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }
}
