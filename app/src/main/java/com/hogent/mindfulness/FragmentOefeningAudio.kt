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
     * variabele mp van type MediaPlayer declareren
     * https://developer.android.com/reference/android/media/MediaPlayer
     */
    var mp: MediaPlayer? = null

    /**
     * in de onCreateView-methode inflaten we onze layout fragment_fragment_oefeningaudio
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_oefeningaudio, container, false)
    }

    /**
     * Deze methode wordt direct na de onCreateView-methode uitgevoerd
     * we zetten hier de tekst "Oefening - Audio' in de TextView fragment_oefeningaudio
     * er wordt een nieuwe MediaPlayer gecreeerd en dit steken we in variabele mp
     * in de create-methode geven we de context en de audiofile mee als parameters
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragment_oefeningaudio.text = "Oefening - Audio"
        mp = MediaPlayer.create(activity, R.raw.testaudio);
    }

    /**
     * Als de onResume-methode uitgevoerd wordt, setten we de click listener van de playbutton:
     * als de mediaplayer niet aan het spelen is starten we het nu, anders pauzeren we het
     */
    override fun onResume() {
        super.onResume()
        playButn.setOnClickListener {
            if (!mp!!.isPlaying) {
                // Stopping
                mp!!.start()
                //playButn.setBackgroundResource()

            } else {
                // Playing
                mp!!.pause()
            }
        }
    }

    /**
     * Als de onPause-methode uitgevoerd wordt, zorgen we dat de playbutton niet meer luistert, dus geen click listener meer heeft
     */
    override fun onPause() {
        super.onPause()
        playButn.setOnClickListener (null)
    }
}
