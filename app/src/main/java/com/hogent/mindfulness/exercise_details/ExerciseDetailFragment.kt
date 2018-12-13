package com.hogent.mindfulness.exercise_details

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
class ExerciseDetailFragment(): Fragment(){

    lateinit var exerciseId:String
    lateinit var manager: FragmentManager
    private lateinit var pageView:PageViewModel
    private var pagesLock = false

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
        pageView.retrievePages()
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.oefening_details_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pageView.pages.observe(this, Observer {
            if (it != null && !pagesLock) {
                initializePages(it!!)
                pagesLock = true
            }
        })
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
        val adapter = OefeningViewPagerAdapter(manager)
        pages.forEachIndexed { index, it ->
            when (it.type) {
                "AUDIO" ->
                {
                    val fragment = FragmentExerciseAudio()
                    fragment.position = index
                    Log.i("FUCKINGAUDIO_" + index, it.audioFilename)
                    fragment.audioFilename = it.audioFilename
                    val arg = Bundle()
                    arg.putString("audioFilename", it.audioFilename)
                    fragment.arguments = arg
                    adapter.addFragment(fragment, "Audio")
                }
                "TEXT" ->
                {
                    Log.i("PAGE", "TEXT")
                    val fragment = FragmentExerciseText()
                    val arg = Bundle()
                    arg.putString("description", it.description)

                    fragment.paragraphs = it.paragraphs

                    fragment.arguments = arg
                    adapter.addFragment(fragment, "Beschrijving")
                }
                "INPUT" ->
                {
                    val fragment = FragmentExerciseInvoer()
                    fragment.position = index
                    fragment.page = it
                    val arg = Bundle()
                    arg.putString("opgave", it.title)
                    fragment.arguments = arg
                    adapter.addFragment(fragment, "Invoer")
                }
            }
        }
        viewPager.adapter = adapter
    }
}