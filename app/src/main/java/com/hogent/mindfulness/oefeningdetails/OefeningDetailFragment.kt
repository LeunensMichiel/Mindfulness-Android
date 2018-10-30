package com.hogent.mindfulness.oefeningdetails

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.hogent.mindfulness.R
import com.hogent.mindfulness.data.MindfulnessApiService
import com.hogent.mindfulness.domain.Model
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.oefening_details_fragment.*

/**
 * Dit is de fragment die verantwoordelijk is om de pages van de oefening te tonen
 * Hierin zit de onder andere de ViewPager die ervoor zorgt dat je kan swipen tussen de verschillende fragments
 * Elke oefening bestaat uit enkele pages (pagina's)
 * we hebben een verschillende fragments voor de verschillende pages, namelijk
 * de fragment FragmentOefeningText voor de tekstpagina
 * de fragment FragmentOefeningAudio voor de audiopagina
 * de fragment FragmentOefeningInvoer voor de invoerpagina
 *
 * Deze klasse heeft als taak de fragments specifiek voor de oefening die hij meegekregen heeft van de MainActivity te tonen in de juiste
 * volgorde en met de juiste data
 */
class OefeningDetailFragment(): Fragment(){

    lateinit var exerciseId:String
    lateinit var manager: FragmentManager
    private lateinit var disposable: Disposable
    private val mindfulnessApiService by lazy {
        MindfulnessApiService.create()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        beginRetrieveExercise(exerciseId)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.oefening_details_fragment, container, false)
    }

    /**
     * We halen hier de oefening op via de mindfulnessApiService
     */
    private fun beginRetrieveExercise(exerciseId: String) {
        disposable = mindfulnessApiService.getPages(exerciseId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> showResultExercise(result) },
                { error -> showError(error.message) }
            )
    }

    /*
    private fun beginRetrieveParagraphs(page_id:String){
        disposable = mindfulnessApiService.getParagraphs(page_id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> showResultParagraphs(result) },
                { error -> showError(error.message) }
            )
    }

    private fun showResultParagraphs(pages: Array<Model.Paragraph>) {

    } */

    /**
     * Dit is een methode om eventuele fouten te tonen
     */
    private fun showError(errMsg: String?) {
        Toast.makeText(activity, errMsg, Toast.LENGTH_SHORT).show()
    }

    /**
     * We zorgen hier dat de juiste layout getoond zal worden
     * we maken een instantie van MyViewPagerAdapter
     * we krijgen het aantal pages mee en we doen een forEach hierop
     * afhankelijk van het type page wordt de juiste fragment aangemaakt en steken wat we nodig hebben in de arguments van de fragment
     * we voegen dan de fragment toe aan de adapter
     * op het einde zeggen we dat de adapter die we gemaakt hebben de adapter is van onze viewpager
     */
    private fun showResultExercise(pages: Array<Model.Page>) {
        val adapter = OefeningViewPagerAdapter(manager)

        pages.forEach {
            Log.d("page", it.description)
            when (it.type) {
                "AUDIO" -> //adapter.addFragment(FragmentOefeningAudio(), "Audio")
                {
                    val fragment = FragmentOefeningAudio()
                    val arg = Bundle()
                    arg.putString("audiopad", it.pathaudio)
                    fragment.arguments = arg
                    adapter.addFragment(fragment, "Audio")
                }
                "TEXT" -> {
                    val fragment = FragmentOefeningText()
                    val arg = Bundle()
                    arg.putString("description", it.description)

                    fragment.paragraphs = it.paragraphs

                    fragment.arguments = arg
                    adapter.addFragment(fragment, "Beschrijving")
                }
                "INPUT" -> //adapter.addFragment(FragmentOefeningInvoer(), "Invoer")
                {
                    val fragment = FragmentOefeningInvoer()
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