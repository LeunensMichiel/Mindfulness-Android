package com.hogent.mindfulness.profile

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.hogent.mindfulness.MainActivity
import com.hogent.mindfulness.R
import com.hogent.mindfulness.R.id.profileFragment_info_icon
import com.hogent.mindfulness.R.id.profileFragment_info_text
import com.hogent.mindfulness.data.LocalDatabase.MindfulnessDBHelper
import com.hogent.mindfulness.data.ServiceGenerator
import com.hogent.mindfulness.data.UserApiService
import com.hogent.mindfulness.domain.Model
import com.mikhaellopez.circularimageview.CircularImageView
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlin.properties.Delegates.observable


class ProfileFragment : Fragment() {
    private lateinit var disposable: Disposable
    private lateinit var dbUser: Model.User
    private val mMindfulDB by lazy {
        MindfulnessDBHelper((activity as MainActivity))
    }
    private var icons: IntArray = intArrayOf()
    private var info: Array<String> = arrayOf()
    lateinit var profilePicUri: Uri
    //De circularimage, niet de effectieve profilepic URL
    lateinit var img: CircularImageView

    private var userService: UserApiService? by observable(null) { property, oldValue: UserApiService?, newValue: UserApiService? ->
        dbUser = mMindfulDB.getUser()!!
        disposable = newValue!!.getUser(dbUser._id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> initUserScreen(result) },
                { error -> Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show() }
            )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //Profile Layout attach to this Fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userService = ServiceGenerator.createService(UserApiService::class.java, (activity as MainActivity))

        val lv = profileFragment_lv
        lv.adapter = ProfileAdapter(this.context!!, icons, info)



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
    }

    private fun initUserScreen(result: Model.User) {
        icons = intArrayOf(
            R.drawable.ic_email_black_24dp,
            R.drawable.ic_group_black_24dp,
            R.drawable.ic_feedback_black_24dp
        )
        info = arrayOf(
            result.email,
            result.group!!.name,
            "Feedback: " + if (result.feedbackSubscribed) "ingeschreven" else "uitgeschreven"
        )


        profileFragment_profilename.text = result.firstname + " " + result.lastname
        profileFragment_Unlockedsessioncount.text = result.unlocked_sessions.size.toString()
        //APARTE API CALL MAKEN VOOR SESSION NAME.
        //profileFragment_CurrentSession.text = result.current_session!!.title
        profileFragment_postCount.text = dbUser.post_ids.size.toString()
        (profileFragment_lv.adapter as ProfileAdapter).setdatasets(icons, info)
    }

    private fun selectImage() {
        CropImage.activity()
            .setGuidelines(CropImageView.Guidelines.ON)
            .setAspectRatio(1, 1)
            .start(context!!, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode === CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode === RESULT_OK) {
                this.profilePicUri = result.uri
                img.setImageURI(profilePicUri)

            } else if (resultCode === CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }
}

class ProfileAdapter(private val context: Context, private var icons: IntArray, private var info: Array<String>) :
    BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    fun setdatasets(iconData: IntArray, infoData: Array<String>) {
        icons = iconData
        info = infoData
        this.notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val profielView = inflater.inflate(R.layout.profile_info_item, parent, false)
        profielView.findViewById<TextView>(R.id.profileFragment_info_text).text = info[position]
        profielView.findViewById<ImageView>(R.id.profileFragment_info_icon).setImageResource(icons[position])
        return profielView
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return info.size
    }

    inner class ProfileViewHolder(view: View) {
        val textInfo: TextView = view.findViewById(profileFragment_info_text)
        var iconInfo: ImageView = view.findViewById(profileFragment_info_icon)

    }
}
