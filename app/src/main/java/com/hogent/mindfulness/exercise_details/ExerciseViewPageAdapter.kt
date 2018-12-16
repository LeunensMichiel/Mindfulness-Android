package com.hogent.mindfulness.exercise_details

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
import android.view.ViewGroup

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
class OefeningViewPagerAdapter(manager: FragmentManager) : FragmentStatePagerAdapter(manager) {
    var fragmentList: MutableList<PagerFragment> = ArrayList()
    var titles: MutableList<String> = ArrayList()
    private var baseId:Long = 0

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }

    override fun getItem(position: Int): Fragment {
//        fragmentList[position].initializePage()
        return fragmentList[position]
    }

    override fun getCount(): Int {
        return fragmentList.size
    }

    fun addFragment(fragment: PagerFragment, title: String) {
        fragmentList.add(fragment)
        titles.add(title)
        notifyDataSetChanged()
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        super.destroyItem(container, position, `object`)
    }

    override fun getPageTitle(position: Int): CharSequence? = titles.get(position)
}