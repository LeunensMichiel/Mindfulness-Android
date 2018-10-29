package com.hogent.mindfulness.oefeningdetails

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class OefeningViewPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {
    private var fragmentList: MutableList<Fragment> = ArrayList()
    private var titles: MutableList<String> = ArrayList()

    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getCount(): Int {
        return fragmentList.size
    }

    fun addFragment(fragment: Fragment, title: String) {
        fragmentList.add(fragment)
        titles.add(title)
    }

    override fun getPageTitle(position: Int): CharSequence? = titles.get(position)
}