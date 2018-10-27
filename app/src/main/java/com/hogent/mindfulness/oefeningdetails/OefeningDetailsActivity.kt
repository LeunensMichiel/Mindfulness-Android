package com.hogent.mindfulness.oefeningdetails

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.widget.Toast
import com.hogent.mindfulness.MainActivity
import com.hogent.mindfulness.R
import com.hogent.mindfulness.data.MindfulnessApiService
import com.hogent.mindfulness.domain.Model
import com.hogent.mindfulness.show_sessions.SessionAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_oefeningdetail.*

/**
 * Dit is de tweede activity
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
class OefeningDetailsActivity : AppCompatActivity() {

    private lateinit var disposable: Disposable

    private val mindfulnessApiService by lazy {
        MindfulnessApiService.create()
    }

    /**
     * We maken hier gebruik van intents
     * deze activity wordt aangeroepen door de onClick-methode van in de ExercisesListFragment
     * Normaal gezien heeft die methode de id van de exercise meegegeven aan de intent en kunnen we ze hier ook gebruiken
     * We checken toch voor best practices of de intent een Intent.EXTRA_TEXT heeft
     * Indien dit klopt, dan steken we dit in een variabele
     * We halen dan via de mindfulnessApiService de oefening op
     * Indien de intent geen Intent.EXTRA_TEXT heeft, dan gaan we via een intent terug naar de MainActivity
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intentThatStartedThisActivity = getIntent()
        if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
            val exercise_id = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT)

            beginRetrieveExercise(exercise_id)

        } else {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * We gebruiken een ViewPager om door de verschillende fragments van de oefening te swipen
     * De FragmentPagerAdapter is nodig om te bepalen hoeveel pages er zullen bestaan en welke fragment er op elk van
     * de pages moet getoond worden
     * we definieren hier een inner class, de klasse MyViewPager erft van FragmentPagerAdapter
     * We hebben twee variabelen, dit zijn allebei veranderbare lijsten
     * De methodes getItem, getCount, addFragment en getPageTitle spreken voor zichzelf
     * de addFragment-methode zal dienen om het meegegeven fragment in de ViewPager te steken
     * de getPageTitle-methode zal dienen om de titel van de page tonen van de fragment
     */
    class MyViewPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {
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

    /**
     * We halen hier de oefening op via de mindfulnessApiService
     */
    private fun beginRetrieveExercise(exerciseId: String) {
        disposable = mindfulnessApiService.getPages(exerciseId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> showResult(result) },
                { error -> showError(error.message) }
            )
    }

    /**
     * We zorgen hier dat de juiste layout getoond zal worden
     * we maken een instantie van MyViewPagerAdapter
     * we krijgen het aantal pages mee en we doen een forEach hierop
     * afhankelijk van het type page wordt de juiste fragment aangemaakt en steken wat we nodig hebben in de arguments van de fragment
     * we voegen dan de fragment toe aan de adapter
     * op het einde zeggen we dat de adapter die we gemaakt hebben de adapter is van onze viewpager
     */
    private fun showResult(pages: Array<Model.Page>) {


        setContentView(R.layout.activity_oefeningdetail)

        val adapter = MyViewPagerAdapter(supportFragmentManager)

        pages.forEach {
            Log.d("page", it.description)
            when (it.type) {
                "AUDIO" -> adapter.addFragment(FragmentOefeningAudio(), "Audio")
                "TEXT" -> {
                    val fragment = FragmentOefeningText()
                    val arg = Bundle()
                    arg.putString("description", it.description)
                    fragment.arguments = arg
                    adapter.addFragment(fragment, "Beschrijving")
                }
                "INPUT" -> adapter.addFragment(FragmentOefeningInvoer(), "Invoer")

            }
        }

        viewPager.adapter = adapter

    }

    /**
     * Dit is een methode om eventuele fouten te tonen
     */
    private fun showError(errMsg: String?) {
        Toast.makeText(this, errMsg, Toast.LENGTH_SHORT).show()
    }
}
