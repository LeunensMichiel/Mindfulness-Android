package com.hogent.mindfulness.exercise_details


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.res.Configuration
import android.os.Bundle
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
class FragmentExerciseText : PagerFragment() {

    lateinit var paragraphs: Array<Model.Paragraph>
    private  lateinit var pageView:PageViewModel
    var position: Int = -1
    private lateinit var parAdapter: ParagraafAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageView = activity?.run {
            ViewModelProviders.of(this).get(PageViewModel::class.java)
        }?: throw Exception("Invalid activity.")

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
        Log.d("TEXT_VIEW", "IMG_RETRIEVE_CALL")
        pageView.pages.observe(this, Observer {
            Log.d("TEXT_VIEW_CHANGE_BOY_1","${it!![position].paragraphs[0]}")
            if (it.isNotEmpty()) {
                parAdapter.setDataSet(it[position].paragraphs)
            }
        })
        //pageView.retrieveTextPageImg(pageView.pages.value!![position].paragraphs, position)
        parAdapter = ParagraafAdapter(pageView.pages.value!![position].paragraphs, pageView)
        val viewManager = LinearLayoutManager(activity)

        rv_paragrafen.apply {
            layoutManager = viewManager
            adapter = parAdapter
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

        val viewAdapter = ParagraafAdapter(paragraphs, pageView)
        val viewManager = LinearLayoutManager(activity)

        rv_paragrafen.apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }
}
