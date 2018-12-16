package com.hogent.mindfulness.profile

import android.app.Activity.RESULT_OK
import android.app.Instrumentation
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.hogent.mindfulness.MainActivity
import com.hogent.mindfulness.R
import com.hogent.mindfulness.domain.Model
import com.hogent.mindfulness.domain.ViewModels.SessionViewModel
import com.hogent.mindfulness.domain.ViewModels.UserViewModel
import com.mikhaellopez.circularimageview.CircularImageView
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.fragment_image_input.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.profile_info_item.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.imageBitmap
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


class ProfileFragment : Fragment() {
    private lateinit var dbUser: Model.User
    private lateinit var sessions: Array<Model.Session>
    private lateinit var userViewModel: UserViewModel
    private lateinit var sessionView: SessionViewModel

    private var icons: IntArray = intArrayOf()
    private var info: Array<String?> = arrayOf()
    var profilePicBitmap: Bitmap? = null
    //De circularimage, niet de effectieve profilepic URL
    lateinit var img: CircularImageView


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //Profile Layout attach to this Fragment
        userViewModel = activity?.run {
            ViewModelProviders.of(this).get(UserViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        sessionView = activity?.run {
            ViewModelProviders.of(this).get(SessionViewModel::class.java)
        } ?: throw Exception("Invalid activity")

        dbUser = userViewModel.dbUser.value!!
        sessions = sessionView.sessionList.value!!
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userViewModel.bitmap.observe(this, Observer {
            if (it != null && ::img.isInitialized) {
                img.setImageBitmap(it)
            }
        })

        userViewModel.dbUser.observe(this, Observer {
            if (it != null) {
                //Name
                profileFragment_profilename.text = it.firstname + " " + it.lastname
                info = arrayOf(
                    it.email,
                    it.group?.name,
                    "Feedback: " + if (it.feedbackSubscribed) "ingeschreven" else "uitgeschreven"
                )
                if (it.unlocked_sessions.isNotEmpty()) {
                    //Level
                    profileFragment_Unlockedsessioncount.text = it.unlocked_sessions.size.toString()
                } else {
                    profileFragment_Unlockedsessioncount.text = "1"
                }
                if (it.post_ids.isNotEmpty()) {
                    //Posts
                    profileFragment_postCount.text = it.post_ids.size.toString()
                } else {
                    profileFragment_postCount.text = "0"
                }
            }
        })

        sessionView.sessionList.observe(this, Observer {
            if (it != null) {
                val unlocked = sessionView.sessionList.value?.filter { ses ->
                    ses.unlocked
                }
                //SessionName
                if (unlocked!!.isNotEmpty()) {
                    profileFragment_CurrentSession.text = unlocked.last().title
                } else {
                    profileFragment_CurrentSession.text = "Nog geen vrijgespeeld"
                }
            }
        })

        icons = intArrayOf(
            R.drawable.ic_email_black_24dp,
            R.drawable.ic_group_black_24dp,
            R.drawable.ic_feedback_black_24dp
        )

        val profileAdapter = ProfileAdapter(this, userViewModel, icons)

        val lv = profileFragment_lv
        lv.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = profileAdapter
            setHasFixedSize(true)
        }

        initUserScreen()
    }

    private fun initUserScreen() {
        //profilePic
        img = profileFragment_profilepic
        img.setImageBitmap(userViewModel.bitmap.value)
        img.setOnClickListener() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(
                        activity as MainActivity,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        activity as MainActivity,
                        arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 1
                    )
                }
                if (ContextCompat.checkSelfPermission(
                        activity as MainActivity,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        activity as MainActivity,
                        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1
                    )
                } else {
                    selectImage()
                }
            } else {
                selectImage()
            }
        }

    }

    private fun selectImage() {
        CropImage.activity()
            .setGuidelines(CropImageView.Guidelines.ON)
            .setAspectRatio(1, 1)
            .start(context!!, this)
    }

    //This changes the image visually, DONT REMOVE!! It is part of the API
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode === CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//            data!!.extras.keySet().forEach {
//                Log.d("FUCKING_IMAGE", it)
//            }

//            var result: = data!!.extras.get("CROP_IMAGE_EXTRA_RESULT") as Instrumentation.ActivityResult
////            Log.d("WUK", "${result }")
////            Log.d("WUK", "${data!!.extras.get("CROP_IMAGE_EXTRA_RESULT") }")
////            Log.d("WUK", "${data!!.extras.get("CROP_IMAGE_EXTRA_BUNDLE") }")
//            var extras:Bundle = data.getExtras()
            // get the cropped bitmap
//            Log.d("CHECK", "${extras.getParcelable<Bitmap>("data")}")

            if (resultCode === RESULT_OK) {
                var result:CropImage.ActivityResult = CropImage.getActivityResult(data)
                val bitmap:Bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, result.uri)
                Log.d("URI", "${result.uri}")
                var file = File(context?.cacheDir, "temp")
                var bos = ByteArrayOutputStream()
                file.createNewFile()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
                var fos = FileOutputStream(file)
                fos.write(bos.toByteArray())
                fos.flush()
                fos.close()
                userViewModel.updateProfilePicture(file, bitmap)
            } else if (resultCode === CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

            }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).setActionBarTitle("Uw profiel")

    }
}

class ProfileAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val userViewModel: UserViewModel,
    private var icons: IntArray
) : RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder>() {

    private var info: Array<String?>

    init {
        info = arrayOf(
            userViewModel.dbUser.value?.email,
            userViewModel.dbUser.value?.group?.name,
            "Feedback: " + if (userViewModel.dbUser.value?.feedbackSubscribed!!) "ingeschreven" else "uitgeschreven"
        )
        userViewModel.dbUser.observe(lifecycleOwner, Observer {
            if (it != null) {
                info = arrayOf(
                    it.email,
                    it.group?.name,
                    "Feedback: " + if (it.feedbackSubscribed) "ingeschreven" else "uitgeschreven"
                )
            }
        })

    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ProfileViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.profile_info_item, p0, false)
        return ProfileViewHolder(view)
    }

    override fun getItemCount(): Int {
        return icons.size
    }

    override fun onBindViewHolder(p0: ProfileViewHolder, p1: Int) {
        p0.icon.setImageResource(icons[p1])
        p0.info.text = info[p1]
    }


    inner class ProfileViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.profileFragment_info_icon
        val info: TextView = view.profileFragment_info_text
    }
}
