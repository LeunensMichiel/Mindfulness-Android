package com.hogent.mindfulness.exercise_details


import android.arch.lifecycle.ViewModelProviders
import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hogent.mindfulness.R
import com.hogent.mindfulness.domain.Model
import com.hogent.mindfulness.domain.ViewModels.PageViewModel
import kotlinx.android.synthetic.main.recyclerview_paragrafen.*
import java.lang.Exception


/**
 * Deze klasse is een Fragment die verantwoordelijk is voor een tekstpagina van een oefening
 */
class FragmentExerciseText : Fragment() {

    lateinit var paragraphs: Array<Model.Paragraph>
    private  lateinit var pageView:PageViewModel

    init {
        Log.d("INITILIAZE_PAGES", "TEXT_INIT")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("INITILIAZE_PAGES", "TEXT")
        pageView = activity?.run {
            ViewModelProviders.of(this).get(PageViewModel::class.java)
        }?: throw Exception("Invalid activity.")
        Log.d("INITILIAZE_PAGES_VM", "${pageView}")
    }

    /**
     * in de onCreateView-methode inflaten we onze layout, hierin zit een recyclerview (we tonen een lijst van paragrafen in deze page)
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.recyclerview_paragrafen, container, false)
    }

    /**
     * Deze methode wordt direct na de onCreateView-methode uitgevoerd
     * Het zorgt ervoor dat de recyclerview getoond zal worden met de juiste data via de SessionAdapter
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pageView.retrieveTextPageImg(paragraphs)
        val viewAdapter = ParagraafAdapter(paragraphs)
        val viewManager = LinearLayoutManager(activity)

        rv_paragrafen.apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }

    }

    /**
     * Fragment herladen als de fragment zichtbaar is voor de user
     * zonder deze functie is de data leeg als je deze fragment verlaat en terugkeert
     */
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isAdded()) {
            if (isVisibleToUser) {
                // Refresh your fragment here
                fragmentManager!!.beginTransaction().detach(this).attach(this).commit()
            }
        }
    }

    /**
     * deze methode wordt bijvoorbeeld aangeroepen als de app verandert van portrait mode naar landscape mode
     * we moeten dan ook de fragment veranderen, we herladen de fragment en steken opnieuw de beschrijving in onze TextView
     */
    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)

        val ft = fragmentManager!!.beginTransaction()
        ft.detach(this).attach(this).commit()

        val viewAdapter = ParagraafAdapter(paragraphs)
        val viewManager = LinearLayoutManager(activity)

        rv_paragrafen.apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }
}
