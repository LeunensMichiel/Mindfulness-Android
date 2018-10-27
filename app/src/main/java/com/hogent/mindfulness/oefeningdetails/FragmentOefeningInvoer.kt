package com.hogent.mindfulness.oefeningdetails


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hogent.mindfulness.R

/**
 * Deze klasse is een Fragment die verantwoordelijk is voor de invoerpagina van de oefening
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
     * Deze methode wordt direct na de onCreateView-methode uitgevoerd
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}
