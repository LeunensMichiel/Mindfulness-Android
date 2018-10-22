package com.hogent.mindfulness

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val adapter = MyViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(FragmentOefeningText(),"Beschrijving")
        adapter.addFragment(FragmentOefeningAudio(), "Audio")
        adapter.addFragment(FragmentOefeningInvoer(), "Invoer")
        viewPager.adapter = adapter
    }

    class MyViewPagerAdapter(manager: FragmentManager): FragmentPagerAdapter(manager)
    {
        private var fragmentList:MutableList<Fragment> = ArrayList()
        private var titles:MutableList<String> = ArrayList()

        override fun getItem(position: Int): Fragment {
            return fragmentList[position]
        }

        override fun getCount(): Int {
            return fragmentList.size
        }

        fun addFragment(fragment: Fragment,title:String){
            fragmentList.add(fragment)
            titles.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? = titles.get(position)
    }
}
