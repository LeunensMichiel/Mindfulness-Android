package com.hogent.mindfulness.profile

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.hogent.mindfulness.MainActivity
import com.hogent.mindfulness.R
import com.hogent.mindfulness.data.LocalDatabase.MindfulnessDBHelper
import com.hogent.mindfulness.data.ServiceGenerator
import com.hogent.mindfulness.data.UserApiService
import com.hogent.mindfulness.domain.Model
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlin.properties.Delegates.observable


class ProfileFragment : Fragment() {
    private  lateinit var disposable: Disposable
    private  lateinit var dbUser: Model.User
    private val mMindfulDB by lazy {
        MindfulnessDBHelper((activity as MainActivity))
    }


    private var userService: UserApiService? by observable(null) { property, oldValue: UserApiService?, newValue: UserApiService? ->
        dbUser = mMindfulDB.getUser()!!
        disposable = newValue!!.getUser(dbUser._id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {result -> initUserScreen(result)},
                {error -> Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()}
            )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //Profile Layout attach to this Fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userService = ServiceGenerator.createService(UserApiService::class.java, (activity as MainActivity))
    }

    private fun initUserScreen(result: Model.User) {
//        val icons : IntArray = intArrayOf(R.drawable.ic_email_black_24dp, R.drawable.ic_group_black_24dp, R.drawable.ic_feedback_black_24dp)
//        val listtext : MutableList<String> = mutableListOf(result.email, result.group!!.name, "Wenst Feedbacknotificaties: " + if (result.feedbackSubscribed) "Ja" else "Neen")
//        var userInfo : MutableList<HashMap<String, String>> = ArrayList()

        profileFragment_profilename.text = result.firstname + " " + result.lastname
        profileFragment_Unlockedsessioncount.text = result.unlocked_sessions.size.toString()
        profileFragment_CurrentSession.text = result.current_session!!.title
        profileFragment_postCount.text = result.post_ids.size.toString()

    }

}
