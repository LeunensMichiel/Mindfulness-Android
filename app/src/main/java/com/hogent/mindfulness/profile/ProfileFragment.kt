package com.hogent.mindfulness.profile

import android.app.Activity.RESULT_OK
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
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
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.profile_info_item.view.*
import org.jetbrains.anko.imageBitmap


class ProfileFragment : Fragment() {
    private lateinit var dbUser: Model.User
    private lateinit var sessions: Array<Model.Session>
    private lateinit var userViewModel: UserViewModel
    private lateinit var sessionView: SessionViewModel

    private var icons: IntArray = intArrayOf()
    private var info: Array<String> = arrayOf()
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

        icons = intArrayOf(
            R.drawable.ic_email_black_24dp,
            R.drawable.ic_group_black_24dp,
            R.drawable.ic_feedback_black_24dp
        )
        info = arrayOf(
            dbUser.email!!,
            dbUser.group!!.name!!,
            "Feedback: " + if (dbUser.feedbackSubscribed!!) "ingeschreven" else "uitgeschreven"
        )

        val profileAdapter = ProfileAdapter(this, userViewModel, icons, info)

        val lv = profileFragment_lv
        lv.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = profileAdapter
        }

        initUserScreen()
    }

    private fun initUserScreen() {
        //profilePic
        img = profileFragment_profilepic
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

        //Name
        profileFragment_profilename.text = dbUser.firstname + " " + dbUser.lastname
        //Level
        profileFragment_Unlockedsessioncount.text = dbUser.unlocked_sessions.size.toString()
        //SessionName
        profileFragment_CurrentSession.text = sessions.last().title
        //Posts
        profileFragment_postCount.text = dbUser.post_ids.size.toString()

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
            val result = CropImage.getActivityResult(data)
            if (resultCode === RESULT_OK) {
                this.profilePicBitmap = result.bitmap
                img.imageBitmap = profilePicBitmap

            } else if (resultCode === CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }
}

class ProfileAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val userViewModel: UserViewModel,
    private var icons: IntArray,
    private var info: Array<String>
) : RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder>() {

    init {
        userViewModel.dbUser.observe(lifecycleOwner, Observer {
            info[0] = it!!.email!!
            info[1] = it.group!!.name!!
            info[2] = "Feedback: " + if (it.feedbackSubscribed) "ingeschreven" else "uitgeschreven"
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


    inner class ProfileViewHolder(view:View) : RecyclerView.ViewHolder(view) {
        val icon : ImageView = view.profileFragment_info_icon
        val info : TextView = view.profileFragment_info_text
    }
}
