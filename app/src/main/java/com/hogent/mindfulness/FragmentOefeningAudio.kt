package com.hogent.mindfulness

import android.media.MediaPlayer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_fragment_oefeningaudio.*


/**
 * Deze klasse is een Fragment die het audioscherm van de oefeningdetails toont
 * De layout die hiermee gelinkt is is fragment_fragment_oefeningaudio
 */
class FragmentOefeningAudio : Fragment() {

    /**
     * in de onCreateView-methode inflaten we onze layout fragment_fragment_oefeningaudio
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_oefeningaudio, container, false)
    }

    /**
     * Deze methode wordt direct na de onCreateView-methode uitgevoerd, we zetten hier de tekst "Oefening - Audio' in de TextView fragment_oefeningaudio
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragment_oefeningaudio.text = "Oefening - Audio"
    }

}
