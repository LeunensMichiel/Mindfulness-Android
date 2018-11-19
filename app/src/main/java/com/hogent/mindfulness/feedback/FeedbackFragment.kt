package com.hogent.mindfulness.feedback


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hogent.mindfulness.R
import com.hogent.mindfulness.domain.Model
import io.reactivex.disposables.Disposable


class FeedbackFragment : Fragment() {
    var sessies : ArrayList<Model.Session> = ArrayList()
    private lateinit var disposable: Disposable
    private lateinit var sessionBools: BooleanArray
    private lateinit var user: Model.User



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_feedback, container, false)
        val recview = view.findViewById(R.id.recyclerFeedback) as RecyclerView
        val adapter = SessionAdapter(sessies, activity as Context)
        recview.layoutManager = LinearLayoutManager(activity)
        recview.adapter = adapter
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        test()
    }

    private fun test() {
        sessies.add(Model.Session("fesf", 4, "haha"))
    }

//    private fun beginRetrieveUser() {
//
//        val userid = activity!!.getSharedPreferences(getString(R.string.sharedPreferenceUserDetailsKey), Context.MODE_PRIVATE)
//            .getString(getString(R.string.userIdKey), "")
//        val userService = ServiceGenerator.createService(UserApiService::class.java)
//
//        disposable = userService.getUser(userid)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(
//                { result -> showResultUser(result) },
//                { error -> showError(error.message) }
//            )
//    }
//
//    /**
//     * this function retrieves a sessionmap from the database
//     */
//    private fun beginRetrieveSessionmap(sessionmap_id: String) {
//        val sessionService = ServiceGenerator.createService(SessionApiService::class.java)
//
//        disposable = sessionService.getSessions(sessionmap_id)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(
//                { result -> showResult(result) },
//                { error -> showError(error.message) }
//            )
//    }
//
//    /**
//     * Initialize SessionAdapter
//     * Initialize LayoutManager
//     * Add Manager and Adapter to recyclerview
//     */
//    private fun showResultUser(resultUser: Model.User) {
//        user = resultUser
//        Log.d("testtest", user.group.sessionmap_id)
//        beginRetrieveSessionmap(user.group.sessionmap_id)
//
//    }
//
//    private fun showError(errMsg: String?) {
//        Toast.makeText(activity, errMsg, Toast.LENGTH_SHORT).show()
//    }
//
//    /**
//     * Initialize SessionAdapter
//     * Initialize LayoutManager
//     * Add Manager and Adapter to recyclerview
//     */
//    private fun showResult(sessions: Array<Model.Session>) {
////        val user:Model.User = arguments!!.get("user") as Model.User
////         mAdapter = SessionAdapter(sessions, activity as SessionAdapter.SessionAdapterOnClickHandler)
////        val viewManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
////
////        Log.d("sessions", sessions.size.toString())
////
////        rv_sessions.apply {
////            layoutManager = viewManager
////            adapter = mAdapter
////        }
//
//        this.sessies = sessions
//        sessionBools = BooleanArray(10)
//        sessions.forEach {
//            sessionBools[it.position] = user.unlocked_sessions.contains(it._id)
//
//        }
//        sessionBools.forEach {
//            Log.d("bools", it.toString())
//
//        }
//
//    }

}
