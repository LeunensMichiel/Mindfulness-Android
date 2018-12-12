package com.hogent.mindfulness.exercise_details

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.hogent.mindfulness.MainActivity
import com.hogent.mindfulness.R
import com.hogent.mindfulness.data.FIleApiService
import com.hogent.mindfulness.data.ServiceGenerator
import com.hogent.mindfulness.domain.ViewModels.PageViewModel
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_fragment_oefeningaudio.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

/**
 * Deze klasse is een Fragment die verantwoordelijk is voor de audiopagina van de oefening
 * De layout die hiermee gelinkt is is fragment_fragment_oefeningaudio
 */
class FragmentExerciseAudio : Fragment(), MediaPlayer.OnPreparedListener {

    /**
     * de variabele mp is van het type MediaPlayer
     * we gebruiken MediaPlayer om het audiobestand te laten afspelen
     * android documentatie: https://developer.android.com/reference/android/media/MediaPlayer
     * bron voor mediaplayer: https://www.youtube.com/watch?v=zCYQBIcePaw
     */
    var mp: MediaPlayer? = null
    private lateinit var audioFile: File
    private lateinit var fos: FileOutputStream
    private lateinit var fis: FileInputStream
    lateinit var audioFilename: String
    lateinit var disposable: Disposable
    lateinit var fileService: FIleApiService
    var position:Int = -1
    private lateinit var pageView:PageViewModel
    var totalTime: Int = 0
    private var progress: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageView = activity?.run {
            ViewModelProviders.of(this).get(PageViewModel::class.java)
        }?: throw Exception("Invalid acitivity")


        Log.d("MEDIA_PLAYER_" + position, "ON_CREATE")

        pageView.pages.observe(this, android.arch.lifecycle.Observer {
            if (it != null){
                if (it[position].mediaPlayer != null){
                    mp = it[position].mediaPlayer
                    onPrepared(mp)
                    Log.d("MEDIA_PLAYER_" + position, "MP_OBSERVED")
                }
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (hasAudio()){
            if (pageView.pages.value!![position].mediaPlayer == null){
                pageView.retrieveAudio(position)
            }
        }

        fileService = ServiceGenerator.createService(FIleApiService::class.java, (activity as MainActivity))
        return inflater.inflate(R.layout.fragment_fragment_oefeningaudio, container, false)
    }

    /**
     * Als de onResume-methode uitgevoerd wordt, checken we of de gebruiker audio kan laten afspelen of niet
     * Indien het device audio kan afspelen en de mediaplayer null is, gaan we de audio voorbereiden
     * we setten de click listener van de playbutton:
     * als de mediaplayer niet aan het spelen is starten we het nu, anders pauzeren we het
     * we veranderen ook het icoontje van de button naar het juiste icoontje
     */
    override fun onResume() {
        super.onResume()

        Log.i("MEDIA_PLAYER_" + position, "RESUME")
        if (mp != null){
            Log.i("MEDIA_PLAYER_" + position, "RESUME_PREPARE_CHECK")
//            pageView.pages.value!![position].mediaPlayer?.reset()
//            pageView.pageReset()
        }

    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser && isResumed){
            Log.i("MEDIA_PLAYER_" + position, "IS_VISIBLE_&_RESUMED")
            onPrepared(mp)
        } else if (!isVisibleToUser){
            if (playButn != null)
                playButn.setBackgroundResource(R.drawable.play)
            Log.i("MEDIA_PLAYER_" + position, "IS_INVISIBLE")
            if (mp != null){
                if (mp?.isPlaying!!){
                    mp?.pause()
                }
            }
        }
    }

    /**
     * we steken hier een handler object in een variabele
     * handlers worden gebruikt om threads te beheren, we creeren hier een handler voor een nieuwe thread
     * een handler object ontvangt messages en runt code om die messages te handlen
     * de handler is gebonden aan de thread of message wachtrij van de thread die het gemaakt heeft
     * de sendMessage laat je toe om een Message-object in de wachtrij te plaatsen dat een bundle van data bevat, die verwerkt zal worden door de
     * handleMessage-methode van de Handler
     * zie ook de Thread... van de functie audioVoorbereiden():
     * daar wordt een Message-object gemaakt
     * er wordt daar ook een message code meegegeven met daarin de message en dit wordt verzonden naar de handler hier
     *
     * we setten de huidige positie naar de waarde die we als message hebben meegekregen en veranderen onze vooruitgang daarnaar
     * we setten ook de elapsedtime en de remainingtime
     */
    private val handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            val currentPosition = msg.what
            // Update positionBar.
            if (positionBar != null){
                positionBar.progress = currentPosition
            }
            // Update Labels.
            if (elapsedTimeLabel != null){
                val elapsedTime = createTimeLabel(currentPosition)
                elapsedTimeLabel.text = elapsedTime
            }

            if (remainingTimeLabel != null){
                val remainingTime = createTimeLabel(totalTime - currentPosition)
                remainingTimeLabel.text = "- $remainingTime"
            }
        }
    }

    /**
     * Als de onPause-methode uitgevoerd wordt, zorgen we dat de playbutton niet meer luistert, dus geen click listener meer heeft
     */
    override fun onPause() {
        Log.i("MEDIA_PLAYER_" + position, "PAUSE")
        super.onPause()
//        if (mp != null) {
//            if (mp?.isPlaying!!)
//                mp?.pause()
//        }
        //playButn.setOnClickListener(null)
    }

    /**
     * In deze functie maken we de timelabel in orde
     * we maken twee variabelen, 1 voor de minuten en 1 voor de seconden
     * de timelabel combineert deze twee variabelen, zodat het voor de gebruiker duidelijk is hoe ver hij in de audiofile zit
     */
    fun createTimeLabel(time: Int): String {
        var timeLabel = ""
        val min = time / 1000 / 60
        val sec = time / 1000 % 60

        timeLabel = min.toString() + ":"
        if (sec < 10) timeLabel += "0"
        timeLabel += sec

        return timeLabel
    }

    /**
     * Dit is een functie die true of false retourneert, afhankelijk van of het device audio kan afspelen of niet
     * we checken hier op verschillende dingen om te weten of audio kan afgespeeld worden of niet
     */
    fun hasAudio(): Boolean {
        var audioManager: AudioManager = this.context!!.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        var packageManager: PackageManager = this.context!!.packageManager

        if (audioManager.isWiredHeadsetOn) {
            return true
        } else if (audioManager.isSpeakerphoneOn) {
            return true
        } else return packageManager.hasSystemFeature(PackageManager.FEATURE_AUDIO_OUTPUT)
    }

    /**
     * Als de configuratie verandert (vb. van portrait mode naar landscape mode veranderen), wordt deze methode aangeroepen
     * we herladen het fragment
     */
    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        val ft = fragmentManager!!.beginTransaction()
        ft.detach(this).attach(this).commit()
    }

    /**
     * Als deze methode wordt aangeroepen, releasen we onze mediaplayer, we doen dit omdat de mediaplayer toch niet meer gebruikt wordt en zodat die
     * resources vrijkomen
     * we setten de mediaplayer op null en veranderen het icoontje naar het juiste icoontje
     */
    override fun onStop() {
        super.onStop()
//        Log.d("MEDIA_PLAYER_" + position, "FRAGMENT_ON_STOP")
//        if (mp != null){
//            if (mp?.isPlaying!!){
//                mp?.pause()
//            }
//        }

//        pageView.pages.value!![position].progress = positionBar.progress
//        Log.d("MEDIA_PLAYER" + position, "${positionBar.progress}")
        playButn.setBackgroundResource(R.drawable.play)
    }


    override fun onPrepared(mp: MediaPlayer?) {
        Log.d("MEDIA_PLAYER_" + position, "PREPARED")
        if (mp != null) {
            audioVoorbereiden()
            if (playButn != null){

                Log.d("MEDIA_PLAYER_" + position, "PREPARED_ON_CLICK")
                playButn.setOnClickListener {
                    if (!mp.isPlaying) {
                        mp.start()
                        playButn.setBackgroundResource(R.drawable.stop)

                    } else {
                        mp.pause()
                        playButn.setBackgroundResource(R.drawable.play)
                    }
                }
            }

        }
    }

    /**
     * er wordt een nieuwe MediaPlayer gecreeerd en dit steken we in variabele mp
     * in de create-methode geven we de context en de audiofile mee als parameters
     * we zetten looping af, dit betekent dat nadat de audiofile volledig is afgespeeld, dit niet opnieuw begint
     * we beginnen bij het begin van de audiofile (seekTo verplaatst de media naar de gespecifieerde tijdpositie)
     * we setten de variabele totalTime gelijk aan de duratie van de mediaplayer
     * we maken de positiebar in orde, dit is een SeekBar die toont hoe ver je in de audiofile zit, deze verandert steeds als de vooruitgang van de file verandert
     * We updaten de positiebar en de timelabel in een aparte thread
     */
    fun audioVoorbereiden() {
        mp!!.isLooping = false
        mp!!.seekTo(mp!!.currentPosition)
        totalTime = mp!!.duration
        if (positionBar != null){
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
        }

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
