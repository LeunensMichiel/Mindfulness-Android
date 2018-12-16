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
    private var pagesLock = false

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).setActionBarTitle(pageView.ex_name)

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
        pageView.retrievePages()
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.oefening_details_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("INITILIAZE_PAGES_DETAIL", "VIEW_CREATED")
        if (pageView.pages.value != null && pageView.pages.value!!.isNotEmpty()) {
            initializePages(pageView.pages.value!!)
        }
        pageView.pages.observe(this, Observer {
            Log.d("PAGE_INIT_OBSERVER", "CHECK")
            if (it != null && !pagesLock) {
                it.forEach {
                    Log.d("PAGE_INIT", "${it}")
                }
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
        Log.d("INITILIAZE_PAGES_MAN", "${manager}")
        val adapter = OefeningViewPagerAdapter(manager)
        adapter.notifyDataSetChanged()
        pages.forEachIndexed { index, it ->
            when (it.type) {
                "AUDIO" ->
                {
                    Log.i("INITILIAZE_PAGES", "AUDIO")
                    val fragment = FragmentExerciseAudio()
                    fragment.position = index
                    Log.i("FUCKINGAUDIO_" + index, it.audioFilename)
                    fragment.audioFilename = it.audioFilename
                    val arg = Bundle()
                    arg.putString("audioFilename", it.audioFilename)
                    fragment.arguments = arg
                    adapter.addFragment(fragment, "Audio")
//                    frags.add(fragment)
//                    titles.add("Audio")
                }
                "TEXT" ->
                {
                    Log.i("INITILIAZE_PAGES", "TEXT")
                    val fragment = FragmentExerciseText()
                    val arg = Bundle()
                    arg.putString("description", it.description)
                    fragment.position = index
                    fragment.paragraphs = it.paragraphs

                    fragment.arguments = arg
                    adapter.addFragment(fragment, "Beschrijving")
//                    frags.add(fragment)
//                    titles.add("Beschrijving")
                }
                "INPUT" ->
                {

                    Log.d("INPUT_TYPE", it.type_input)
                    when(it.type_input) {
                        "TEXT" -> {
                            val fragment = TextInputFragment()
                            fragment.position = index
                            fragment.page = it
                            adapter.addFragment(fragment, "Invoer")
                        }
                        "IMAGE" -> {
                            val fragment = ImageInputFragment()
                            fragment.position = index
                            fragment.page = it
                            adapter.addFragment(fragment, "Invoer")
                        }
                        "MULTIPLE_CHOICE" -> {
                            val fragment = MultipleChoiceFragment()
                            fragment.position = index
                            fragment.page = it
                            adapter.addFragment(fragment, "Invoer")
                        }
                    }
                }
            }
        }
        viewPager.offscreenPageLimit = 1
        viewPager.setAdapter(adapter);
    }
}