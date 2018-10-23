package com.hogent.mindfulness

import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import com.hogent.mindfulness.R.id.*
import kotlinx.android.synthetic.main.fragment_fragment_oefeningaudio.*
import android.R.attr.orientation
import android.R.attr.textEditSideNoPasteWindowLayout
import android.content.res.Configuration
import android.util.Log
import android.app.Activity
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager

/**
 * Deze klasse is een Fragment die het audioscherm van de oefeningdetails toont
 * De layout die hiermee gelinkt is is fragment_fragment_oefeningaudio
 */
class FragmentOefeningAudio : Fragment() {

    /**
     * variabele mp van type MediaPlayer declareren
     * https://developer.android.com/reference/android/media/MediaPlayer
     * bron: https://www.youtube.com/watch?v=zCYQBIcePaw
     */
    var mp: MediaPlayer? = null

    /**
     * variabele totalTime om dit later gelijk te zetten aan het totale aantal van de duratie van de audiofile
     */
    var totalTime: Int = 0

    /**
     * in de onCreateView-methode inflaten we onze layout fragment_fragment_oefeningaudio
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            return inflater.inflate(R.layout.fragment_fragment_oefeningaudio, container, false)
        }
        else{
            return inflater.inflate(R.layout.fragment_fragment_oefeningaudio, container, false)
        }
    }

    /**
     * Deze methode wordt direct na de onCreateView-methode uitgevoerd
     * we zetten hier de tekst "Oefening - Audio' in de TextView fragment_oefeningaudio
     * er wordt een nieuwe MediaPlayer gecreeerd en dit steken we in variabele mp
     * in de create-methode geven we de context en de audiofile mee als parameters
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(!hasAudio()){
            playButn.setOnClickListener{
                Toast.makeText(activity, "Geen audio gedetecteerd!",Toast.LENGTH_LONG).show()
            }
        }
        else {
            audioVoorbereiden()
       }
    }

    /**
     * Als de onResume-methode uitgevoerd wordt, setten we de click listener van de playbutton:
     * als de mediaplayer niet aan het spelen is starten we het nu, anders pauzeren we het
     */
    override fun onResume() {
        super.onResume()

        if(hasAudio()) {
            if(mp == null){
                audioVoorbereiden()
            }
            playButn.setOnClickListener {
                if (!mp!!.isPlaying) {
                    // Stopping
                    mp!!.start()
                    playButn.setBackgroundResource(R.drawable.stop)

                } else {
                    // Playing
                    mp!!.pause()
                    playButn.setBackgroundResource(R.drawable.play)
                }
            }
        }
        else{
            playButn.setOnClickListener{
                Toast.makeText(activity, "Geen audio gedetecteerd!",Toast.LENGTH_LONG).show()
            }
        }
    }

    private val handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            val currentPosition = msg.what
            // Update positionBar.
            positionBar.progress = currentPosition

            // Update Labels.
            val elapsedTime = createTimeLabel(currentPosition)
            elapsedTimeLabel.text = elapsedTime

            val remainingTime = createTimeLabel(totalTime - currentPosition)
            remainingTimeLabel.text = "- $remainingTime"
        }
    }

    /**
     * Als de onPause-methode uitgevoerd wordt, zorgen we dat de playbutton niet meer luistert, dus geen click listener meer heeft
     */
    override fun onPause() {
        super.onPause()
        playButn.setOnClickListener (null)
    }

    fun createTimeLabel(time: Int): String {
        var timeLabel = ""
        val min = time / 1000 / 60
        val sec = time / 1000 % 60

        timeLabel = min.toString() + ":"
        if (sec < 10) timeLabel += "0"
        timeLabel += sec

        return timeLabel
    }

    fun hasAudio(): Boolean{
        var audioManager:AudioManager = this.context!!.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        var packageManager:PackageManager = this.context!!.packageManager

        if(audioManager.isWiredHeadsetOn)
        {
            return true
        }
        else if(audioManager.isSpeakerphoneOn)
        {
            return true
        }
        else if(packageManager.hasSystemFeature(PackageManager.FEATURE_AUDIO_OUTPUT)){
            return true
        }
        else{
            return false
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        checkOrientation(newConfig!!)
    }

    private fun checkOrientation(newConfig: Configuration) {
        // Checks the orientation of the screen
        if (newConfig.orientation === Configuration.ORIENTATION_LANDSCAPE) {
            Log.d("OrientationMyApp", "Current Orientation : Landscape")
            val ft = fragmentManager!!.beginTransaction()
            ft.detach(this).attach(this).commit()
        } else if (newConfig.orientation === Configuration.ORIENTATION_PORTRAIT) {
            Log.d("OrientationMyApp", "Current Orientation : Portrait")
            val ft = fragmentManager!!.beginTransaction()
            ft.detach(this).attach(this).commit()
        }
    }

    override fun onStop() {
        super.onStop()
        mp?.release();
        mp = null;
        playButn.setBackgroundResource(R.drawable.play)
    }

    fun audioVoorbereiden(){
        mp = MediaPlayer.create(activity, R.raw.testaudio)
        mp!!.isLooping = false
        mp!!.seekTo(0)
        totalTime = mp!!.duration

        positionBar.max = totalTime
        positionBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        mp!!.seekTo(progress)
                        positionBar.progress = progress
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {

                }
            })

        // Thread (Update positionBar & timeLabel)
        Thread(Runnable {
            while (mp != null) {
                try {
                    val msg = Message()
                    msg.what = mp!!.currentPosition
                    handler.sendMessage(msg)
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                }

            }
        }).start()
    }
}
