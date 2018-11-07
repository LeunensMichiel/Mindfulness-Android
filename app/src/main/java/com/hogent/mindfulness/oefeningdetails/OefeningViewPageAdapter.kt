package com.hogent.mindfulness.oefeningdetails

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

/**
 * We gebruiken een ViewPager om door de verschillende fragments van de oefening te swipen
 * De FragmentPagerAdapter is nodig om te bepalen hoeveel pages er zullen bestaan en welke fragment er op elk van
 * de pages moet getoond worden
 * de klasse OefeningViewPagerAdapter erft van FragmentPagerAdapter
 * We hebben twee variabelen, dit zijn allebei veranderbare lijsten
 * De methodes getItem, getCount, addFragment en getPageTitle spreken voor zichzelf
 * de addFragment-methode zal dienen om het meegegeven fragment in de ViewPager te steken
 * de getPageTitle-methode zal dienen om de titel van de page tonen van de fragment
 */
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