package com.hogent.mindfulness.oefeningdetails


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_fragment_oefeningtext.*
import android.text.method.ScrollingMovementMethod
import com.hogent.mindfulness.R


/**
 * Deze klasse is een Fragment die het tekstscherm van de oefeningdetails toont
 * De layout die hiermee gelinkt is is fragment_fragment_oefeningtext
 */
class FragmentOefeningText : Fragment() {

    /**
     * in de onCreateView-methode inflaten we onze layout fragment_fragment_oefeningtext
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_oefeningtext, container, false)
    }

    /**
     * Deze methode wordt direct na de onCreateView-methode uitgevoerd, we zetten hier de tekst "Oefening - Beschrijving' in de TextView fragment_oefeningtext
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        oefeningText_beschrijving.setMovementMethod(ScrollingMovementMethod())
    }
}