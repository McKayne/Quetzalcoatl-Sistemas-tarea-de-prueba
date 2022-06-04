package com.el_nico.cappasitytesttask.ui.weatherdetails

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class TabsAdapter(activity: FragmentActivity): FragmentStateAdapter(activity) {

    private val fragmentList = ArrayList<Fragment>()

    private val fragmentTitleList = ArrayList<String>()

    fun getTabTitle(position: Int): String {
        return fragmentTitleList[position]
    }

    fun addFragment(fragment: Fragment, title: String) {
        fragmentList.add(fragment)
        fragmentTitleList.add(title)
    }

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }
}