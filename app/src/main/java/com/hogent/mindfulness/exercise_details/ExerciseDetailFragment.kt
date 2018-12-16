package com.hogent.mindfulness.exercise_details

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hogent.mindfulness.MainActivity
import com.hogent.mindfulness.R
import com.hogent.mindfulness.domain.Model
import com.hogent.mindfulness.domain.ViewModels.PageViewModel
import kotlinx.android.synthetic.main.oefening_details_fragment.*

/**
 * Dit is de fragment die verantwoordelijk is om de pages van de oefening te tonen
 * Hierin wordt ook de ViewPager gebruikt, die zorgt ervoor dat je kan swipen tussen de verschillende fragments
 * Elke oefening bestaat uit enkele pages (pagina's)
 * we hebben verschillende fragments voor de verschillende pages, namelijk
 * de fragment FragmentExerciseText voor de tekstpagina
 * de fragment FragmentExerciseAudio voor de audiopagina
 * de fragment FragmentExerciseInvoer voor de invoerpagina
 *
 * Deze klasse heeft als taak de fragments specifiek voor de oefening die hij meegekregen heeft van de MainActivity te tonen in de juiste
 * volgorde en met de juiste data
 */
class ExerciseDetailFragment: Fragment(){

    lateinit var exerciseId:String
    lateinit var manager: FragmentManager
    private lateinit var pageView:PageViewModel
    private lateinit var pageAdapter:OefeningViewPagerAdapter
    var pagesLock = false

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).setActionBarTitle(pageView.ex_name)
        if (pageView.pageCopies.value != null && pageView.pageCopies.value!!.isNotEmpty()) {
            viewPager.visibility = View.VISIBLE
            emptyExerciseLayout.visibility = View.GONE
            initializePages(pageView.pageCopies.value!!)
        } else if(pageView.pageCopies.value != null && pageView.pageCopies.value!!.isEmpty()) {
            emptyExerciseLayout.visibility = View.VISIBLE
            viewPager.visibility = View.GONE
        }
        pageView.pageCopies.observe(this, Observer {
            //Ik heb hier code bijgevoegd voor te controleren voor lege pages maar zou niets mogen breken
            if (it == null || it.isEmpty()) {
                emptyExerciseLayout.visibility = View.VISIBLE
                viewPager.visibility = View.GONE
            }
            else {
                viewPager.visibility = View.VISIBLE
                emptyExerciseLayout.visibility = View.GONE
            }
            if (it != null) {

                initializePages(it!!)
                pagesLock = true
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pagesLock = false

    }

    /**
     * we halen eerst de exercise op en dan inflaten we de layout
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        pageView = activity?.run {
            ViewModelProviders.of(this).get(PageViewModel::class.java)
        }?: throw Exception("Invalid activity.")
        //pageView.retrievePages()
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.oefening_details_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pageAdapter = OefeningViewPagerAdapter(manager)
        viewPager.adapter = pageAdapter
        viewPager.adapter!!.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (viewPager.adapter as OefeningViewPagerAdapter).fragmentList.forEach {
            manager.beginTransaction().remove(it).commit()
        }
        //viewPager.adapter!!.notifyDataSetChanged()
    }

    /**
     * We zorgen hier dat de viewpageradapter de juiste fragments (mee)krijgt
     * we maken een instantie van OefeningViewPagerAdapter
     * we krijgen het aantal pages mee en we doen een forEach hierop
     * afhankelijk van het type page wordt de juiste fragment aangemaakt en steken we wat nodig hebben in de arguments van de fragment
     * we voegen dan de fragment toe aan de adapter
     * op het einde zeggen we dat de adapter die we gemaakt hebben de adapter is van onze viewpager
     */
    private fun initializePages(pages: Array<Model.Page>) {

        pageAdapter = OefeningViewPagerAdapter(manager)
        viewPager.adapter = pageAdapter
        viewPager.adapter!!.notifyDataSetChanged()
        (viewPager.adapter as OefeningViewPagerAdapter).fragmentList = mutableListOf()
        (viewPager.adapter as OefeningViewPagerAdapter).titles = mutableListOf()
        viewPager.adapter?.notifyDataSetChanged()
        pages.forEachIndexed { index, it ->
            when (it.type) {
                "AUDIO" ->
                {
                    val fragment = FragmentExerciseAudio()
                    fragment.position = index
                    fragment.audioFilename = it.audioFilename
                    val arg = Bundle()
                    arg.putString("audioFilename", it.audioFilename)
                    fragment.arguments = arg
                    (viewPager.adapter as OefeningViewPagerAdapter).addFragment(fragment, it.title)
//                    frags.add(fragment)
//                    titles.add("Audio")
                }
                "TEXT" ->
                {
                    val fragment = FragmentExerciseText()
                    val arg = Bundle()
                    arg.putString("description", it.description)
                    fragment.position = index
                    fragment.paragraphs = it.paragraphs

                    fragment.arguments = arg
                    (viewPager.adapter as OefeningViewPagerAdapter).addFragment(fragment, it.title)
//                    frags.add(fragment)
//                    titles.add("Beschrijving")
                }
                "INPUT" ->
                {

                    when(it.type_input) {
                        "TEXT" -> {
                            val fragment = TextInputFragment()
                            fragment.position = index
                            fragment.page = it
                            (viewPager.adapter as OefeningViewPagerAdapter).addFragment(fragment, it.title)
                        }
                        "IMAGE" -> {
                            val fragment = ImageInputFragment()
                            fragment.position = index
                            fragment.page = it
                            (viewPager.adapter as OefeningViewPagerAdapter).addFragment(fragment, it.title)
                        }
                        "MULTIPLE_CHOICE" -> {
                            val fragment = MultipleChoiceFragment()
                            fragment.position = index
                            fragment.page = it
                            (viewPager.adapter as OefeningViewPagerAdapter).addFragment(fragment, it.title)
                        }
                    }
                }
            }
        }
        viewPager.offscreenPageLimit = 1
        viewPager.adapter?.notifyDataSetChanged()
    }
}