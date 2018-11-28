package com.hogent.mindfulness.exercise_details


import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_fragment_oefeninginvoer.*
import android.content.pm.PackageManager
import android.util.Log
import com.hogent.mindfulness.MainActivity
import com.hogent.mindfulness.R
import com.hogent.mindfulness.data.PostApiService
import com.hogent.mindfulness.data.PostInformation
import com.hogent.mindfulness.data.ServiceGenerator
import com.hogent.mindfulness.domain.Model
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


/**
 * Deze klasse is een Fragment die verantwoordelijk is voor de invoerpagina van de oefening
 * De layout die hiermee gelinkt is is fragment_fragment_oefeninginvoer
 */
class FragmentExerciseInvoer : Fragment() {

    private var postId:String? = null
    private lateinit var disposable: Disposable
    private lateinit var post:Model.Post

    private lateinit var postService:PostApiService

    lateinit var page:Model.Page

    /**
     * in de onCreateView-methode inflaten we onze layout fragment_fragment_oefeninginvoer
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        //beginRetrievePost()
        // Inflate the layout for this fragment
        postService = ServiceGenerator.createService(PostApiService::class.java, (activity as MainActivity))
        return inflater.inflate(R.layout.fragment_fragment_oefeninginvoer, container, false)
    }
    /**
     * Dit is een methode om eventuele fouten te tonen
     */
    fun showError(errMsg: String?) {
        Toast.makeText(activity, errMsg, Toast.LENGTH_SHORT).show()
    }

    /**
     * Deze methode wordt direct na de onCreateView-methode uitgevoerd
     *
     * we checken of de arguments van de fragment een key opgave heeft, als dit zo is, dan stellen we de hint van input (zie xml) gelijk aan de
     * waarde van de key opgave
     *
     * we checken of het device waarop de app gerund wordt wel een camera heeft: als het een camera heeft dan:
     * setten we een clicklistener op de button:
     * als de button btnCamera gedrukt wordt, openen we de camera via een intent
     * als het geen camera heeft, dan wordt een toastmessage getoond
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        disposable = postService.checkPageId(page!!._id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {result ->
                    post = result
                    showResult()
                    Log.i("OBSERVABLE", "$post")
                },
                {error -> Log.i("fuck", "fuck")}
            )
        if(this.arguments!!.containsKey("opgave")){
            inputlayout.hint = this.arguments!!.getString("opgave", "check")
        }

    }

    fun showResult() {
        text_edit.setText(post.inhoud)
        val pm = context!!.getPackageManager()
        if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            btnCamera.setOnClickListener {
                var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, 0)
            }
        }
        else
        {
            btnCamera.setOnClickListener {
                Toast.makeText(context,"Geen camera gedetecteerd",Toast.LENGTH_SHORT).show()
            }
        }

        btnOpslaan.setOnClickListener {
            // TEDOEN: nog een check: als er niets is veranderd, dan moet er geen nieuwe post gemaakt worden of geupdate wordenge
            Log.i("FUCKINGINVOER", "$post")
            post = (activity as MainActivity).updatePost(page!!, text_edit.text.toString(), post)
            Log.d("button","-----------test1--------------")
        }

    }

    /**
     * Als de configuratie verandert, wordt deze methode aangeroepen
     * we herladen hier de fragment en setten de hint van de input opnieuw
     */
    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)

        val ft = fragmentManager!!.beginTransaction()
        ft.detach(this).attach(this).commit()
        if(this.arguments!!.containsKey("opgave")){
            inputlayout.hint = this.arguments!!.getString("opgave", "check")
        }
        text_edit.setText("Test")
    }

    /**
     * we gebruiken onActivityResult omdat we een result verwachten van de activity
     * als we dat result krijgen, wordt deze methode uitgevoerd
     * het result dat we verwachten is de genomen foto
     *
     * we checken of we wel een result gekregen hebben, indien ja dan halen we de image op en tonen we het in een imageview
     * indien niet, dan tonen we een toastmessage
     *
     * extra info:
     * de android camera applicatie encodeert de genomen foto in de return intent die meegeleverd wordt aan de onActivityResult() als een bitmap
     * in de extras met als key 'data'
     * we halen dus de image op en tonen het in een imageview
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(RESULT_OK == resultCode){
            var bitmap: Bitmap = data!!.extras.get("data") as Bitmap
            imageView.setImageBitmap(bitmap)
        }
        else{
            Toast.makeText(activity, "Geen foto genomen", Toast.LENGTH_SHORT).show()
        }
    }
}