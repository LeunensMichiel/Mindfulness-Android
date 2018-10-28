package com.hogent.mindfulness.oefeningdetails


import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_fragment_oefeningtext.*
import android.text.method.ScrollingMovementMethod
import com.hogent.mindfulness.R
import com.hogent.mindfulness.domain.Model


/**
 * Deze klasse is een Fragment die verantwoordelijk is voor een tekstpagina van een oefening
 */
class FragmentOefeningText : Fragment() {

    var page: Model.Page = Model.Page("","","","","","")


    /**
     * in de onCreateView-methode inflaten we onze layout fragment_fragment_oefeningtext
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_oefeningtext, container, false)
    }

    /**
     * Deze methode wordt direct na de onCreateView-methode uitgevoerd
     * oefeningText_beschrijving is een TextView
     * we zorgen ervoor dat de beschrijving van de oefening scrollbaar is (vb. voor lange teksten)
     * als in de argumenten van de fragment de key description zit, dan steken we de value van deze key in onze beschrijving van de oefening
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        oefeningText_beschrijving.setMovementMethod(ScrollingMovementMethod())
        if(this.arguments!!.containsKey("description")){
            oefeningText_beschrijving.text = this.arguments!!.getString("description", "check")
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
        if(this.arguments!!.containsKey("description")){
            oefeningText_beschrijving.text = this.arguments!!.getString("description", "check")
        }
    }
}
