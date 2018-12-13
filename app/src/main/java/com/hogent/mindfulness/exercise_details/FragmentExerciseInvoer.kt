package com.hogent.mindfulness.exercise_details


import android.app.Activity.RESULT_OK
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_fragment_oefeninginvoer.*
import com.hogent.mindfulness.MainActivity
import com.hogent.mindfulness.R
import com.hogent.mindfulness.data.PostApiService
import com.hogent.mindfulness.data.PostInformation
import com.hogent.mindfulness.data.ServiceGenerator
import com.hogent.mindfulness.domain.Model
import com.hogent.mindfulness.domain.ViewModels.PageViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_fragment_oefeningaudio.*
import kotlinx.android.synthetic.main.fragment_fragment_oefeninginvoer.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import kotlin.properties.Delegates


/**
 * Deze klasse is een Fragment die verantwoordelijk is voor de invoerpagina van de oefening
 * De layout die hiermee gelinkt is is fragment_fragment_oefeninginvoer
 */
class FragmentExerciseInvoer : Fragment() {

    private var postId: String? = null
    private lateinit var disposable: Disposable
    private lateinit var post: Model.Post
    private lateinit var pageView:PageViewModel
    var position:Int = -1

    private lateinit var postService: PostApiService

    lateinit var page: Model.Page

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageView = activity?.run {
            ViewModelProviders.of(this).get(PageViewModel::class.java)
        }?: throw Exception("Invalid activity.")

        pageView.pages.observe(this, Observer {
            Log.d("PAGE_VIEW", "FUCK_OFF")
            if(pageView.pages.value!![position].post != null){
                post = pageView.pages.value!![position].post!!
                showResult()
            }
        })
    }

    /**
     * in de onCreateView-methode inflaten we onze layout fragment_fragment_oefeninginvoer
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //beginRetrievePost()
        // Inflate the layout for this fragment
        postService = ServiceGenerator.createService(PostApiService::class.java, (activity as MainActivity))
        return inflater.inflate(R.layout.fragment_fragment_oefeninginvoer, container, false)
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

        if (this.arguments!!.containsKey("opgave")) {
            inputlayout.hint = this.arguments!!.getString("opgave", "check")
        }

    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser && pageView.pages.value!![position].post == null){
            pageView.checkInputPage(page._id, position)
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
        } else {
            btnCamera.setOnClickListener {
                Toast.makeText(context, "Geen camera gedetecteerd", Toast.LENGTH_SHORT).show()
            }
        }

        btnOpslaan.setOnClickListener {
            post = pageView.updatePost(position, page, text_edit.text.toString(), post)
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
        if (this.arguments!!.containsKey("opgave")) {
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
        if (RESULT_OK == resultCode) {
            var bitmap: Bitmap = data!!.extras.get("data") as Bitmap
            var file = File(context?.cacheDir, "temp")
            var bos = ByteArrayOutputStream()
            imageView.setImageBitmap(bitmap)
            file.createNewFile()
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos)
            var fos = FileOutputStream(file)
            fos.write(bos.toByteArray())
            fos.flush()
            fos.close()
            pageView.updatePostImage(file, position, page, text_edit.text.toString(), post)
        } else {
            Toast.makeText(activity, "Geen foto genomen", Toast.LENGTH_SHORT).show()
        }
    }
}