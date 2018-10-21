package com.hogent.mindfulness


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_fragment_oefeninginvoer.*

/**
 * Deze klasse is een Fragment die het invoerscherm van de oefeningdetails toont
 * De layout die hiermee gelinkt is is fragment_fragment_oefeninginvoer
 */
class FragmentOefeningInvoer : Fragment() {

    /**
     * in de onCreateView-methode inflaten we onze layout fragment_fragment_oefeninginvoer
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_oefeninginvoer, container, false)
    }

    /**
     * Deze methode wordt direct na de onCreateView-methode uitgevoerd, we zetten hier de tekst "Oefening - Invoer' in de TextView fragment_oefeningbeschrijving
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragment_oefeningbeschrijving.text = "Oefening - Invoer"
    }
}
