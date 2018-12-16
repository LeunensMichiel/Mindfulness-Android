package com.hogent.mindfulness.exercise_details


import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.hogent.mindfulness.R
import com.hogent.mindfulness.domain.Model
import com.hogent.mindfulness.domain.ViewModels.PageViewModel
import kotlinx.android.synthetic.main.fragment_image_input.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

/**
 * A simple [Fragment] subclass.
 *
 */
class ImageInputFragment : PagerFragment() {

    private lateinit var post: Model.Post
    private lateinit var pageView: PageViewModel
    var position: Int = -1
    lateinit var page: Model.Page

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageView = activity?.run {
            ViewModelProviders.of(this).get(PageViewModel::class.java)
        } ?: throw Exception("Invalid activity.")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_image_input, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pageView.pages.observe(this, Observer {
            Log.d("CHECKY_BOY", "FUCK_OFF")
            if (it!!.isNotEmpty() && it!![position].post != null) {
                if (it[position].post?.bitmap == null) {
                    post = pageView.pages.value!![position].post!!
                    Log.d("CHECKY_BOY", "SHOW_RESULT_CALL")
                    showResult()
                } else {
                    fragment_image_input_image.setImageBitmap(it[position].post?.bitmap)
                    Log.d("POST_IMAGE", "OBSERVER_CHECK")
                }
            }
        })

        fragment_image_input_txf.setText("Geschiedenis aan het ophalen...")
        Log.d("IMAGE_INPUT_LIST", "ON_VIEW_CREATED")
        if (pageView.pages.value?.isNotEmpty()!!){
            if (pageView.pages.value!![position].post == null){
                fragment_image_input_photoBtn.onClick {
                    pageView.pageError.postValue(Model.errorMessage(null, "Input nog niet klaar."))
                }

                Log.d("CHECKY_BOY", "GET_THEM_BACK")
                pageView.checkInputPage(page._id, position)
            } else {
                fragment_image_input_txf.setText(pageView.pages.value!![position].title)
                fragment_image_input_image.setImageBitmap(pageView.pages.value!![position].post?.bitmap)
            }
        }
    }


    fun showResult() {
        val pm = context!!.getPackageManager()
        Log.d("CHECKY_BOY", "${page.post}")
        if (pageView.pages.value!![position].post?.bitmap == null) {
            pageView.retrievePostImg(page.post!!, position)
        }
        if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            fragment_image_input_photoBtn.onClick {
                var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, 0)
            }
        } else {
            fragment_image_input_photoBtn.onClick {
                pageView.pageError.postValue(Model.errorMessage(null, "Geen camera gevonden"))
            }
        }

        fragment_image_input_txf.setText(page.title)
    }

//    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
//        super.setUserVisibleHint(isVisibleToUser)
//        if (isAdded()) {
//            if (isVisibleToUser && pageView.pages.value!![position].post != null && isResumed) {
////                page = pageView.pages.value!![position]
////                Log.d("POST_BITMP", "${page.post?.bitmap}")
//
//            }
//        }
//    }

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
        if (Activity.RESULT_OK == resultCode) {
            var bitmap: Bitmap = data!!.extras.get("data") as Bitmap
            var file = File(context?.cacheDir, "temp")
            var bos = ByteArrayOutputStream()
            fragment_image_input_image.setImageBitmap(bitmap)
            file.createNewFile()
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos)
            var fos = FileOutputStream(file)
            fos.write(bos.toByteArray())
            fos.flush()
            fos.close()
            pageView.updatePostImage(file, position, page, post)
        } else {
            Toast.makeText(activity, "Geen foto genomen", Toast.LENGTH_SHORT).show()
        }
    }
}
