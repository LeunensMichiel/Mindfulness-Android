package com.hogent.mindfulness.show_sessions


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.hogent.mindfulness.R
import com.hogent.mindfulness.data.UserApiService
import com.hogent.mindfulness.scanner.ScannerActivity
import com.hogent.mindfulness.data.ServiceGenerator
import com.hogent.mindfulness.data.SessionApiService
import com.hogent.mindfulness.domain.Model
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.session_fragment.*
import kotlinx.android.synthetic.main.session_item_list.view.*


class SessionFragment() : Fragment() {


    /**
     * Here will the sessionData be stored
     * Disposable used for calling api calls
     */
    private lateinit var sessions: Array<Model.Session>
    private lateinit var mAdapter: SessionAdapter
    private lateinit var sessionBools: BooleanArray
    private lateinit var disposable: Disposable
    private lateinit var user: Model.User
    lateinit var unlockSession: String

    /**
     * I used this resource: https://developer.android.com/guide/topics/ui/layout/recyclerview
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        beginRetrieveUser()
//        beginRetrieveSessionmap(getString(R.string.sessionmap_id))
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.session_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessions = arrayOf<Model.Session>()
        val sessionBools = BooleanArray(10)

        mAdapter = SessionAdapter(sessions, activity as SessionAdapter.SessionAdapterOnClickHandler, sessionBools)
        val viewManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

        Log.d("sessions", sessions.size.toString())

        rv_sessions.apply {
            layoutManager = viewManager
            adapter = mAdapter
        }
    }

    /**
     * Starts the Activity
     * - Allocate resources
     * - register click listeners
     * - update UI
     */
    override fun onStart() {
        super.onStart()

        fabCamera.setOnClickListener { view ->
            val intent = Intent(activity, ScannerActivity::class.java)
            startActivity(intent)
        }
    }

    private fun beginRetrieveUser() {

        val userid = activity!!.getSharedPreferences(getString(R.string.sharedPreferenceUserDetailsKey), Context.MODE_PRIVATE)
                .getString(getString(R.string.userIdKey), "")
        val userService = ServiceGenerator.createService(UserApiService::class.java)

        disposable = userService.getUser(userid)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> showResultUser(result) },
                { error -> showError(error.message) }
            )
    }

    /**
     * this function retrieves a sessionmap from the database
     */
    private fun beginRetrieveSessionmap(sessionmap_id: String) {
        val sessionService = ServiceGenerator.createService(SessionApiService::class.java)

        disposable = sessionService.getSessions(sessionmap_id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> showResult(result) },
                { error -> showError(error.message) }
            )
    }

    /**
     * Initialize SessionAdapter
     * Initialize LayoutManager
     * Add Manager and Adapter to recyclerview
     */
    private fun showResultUser(resultUser: Model.User) {
        user = resultUser
        Log.d("testtest", user.group.sessionmap_id)
        beginRetrieveSessionmap(user.group.sessionmap_id)

    }

    private fun showError(errMsg: String?) {
        Toast.makeText(activity, errMsg, Toast.LENGTH_SHORT).show()
    }

    /**
     * Initialize SessionAdapter
     * Initialize LayoutManager
     * Add Manager and Adapter to recyclerview
     */
    private fun showResult(sessions: Array<Model.Session>) {
//        val user:Model.User = arguments!!.get("user") as Model.User
//         mAdapter = SessionAdapter(sessions, activity as SessionAdapter.SessionAdapterOnClickHandler)
//        val viewManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
//
//        Log.d("sessions", sessions.size.toString())
//
//        rv_sessions.apply {
//            layoutManager = viewManager
//            adapter = mAdapter
//        }

        this.sessions = sessions
        sessionBools = BooleanArray(10)
        sessions.forEach {
            sessionBools[it.position] = user.unlocked_sessions.contains(it._id)

        }
        sessionBools.forEach {
            Log.d("bools", it.toString())

        }
        mAdapter.mSessionData = sessions
        mAdapter.sessionBools = sessionBools
        mAdapter.notifyDataSetChanged()
    }

    /***********************************************************************************************
     * Adapter
     *
     ***********************************************************************************************
     */

    class SessionAdapter(
        // This array has the data for the recyclerview adapter
        var mSessionData: Array<Model.Session>,
        //mClickHandler is for communicating whit the activity when item clicked
        private val mClickHandler: SessionAdapterOnClickHandler,
        var sessionBools: BooleanArray
    ) : RecyclerView.Adapter<SessionAdapter.SessionViewHolder>() {


        /**
         * This function loads in the item view
         */
        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): SessionViewHolder {
            val context = viewGroup.context
            val layoutIdForListItem = R.layout.session_item_list
            val inflater = LayoutInflater.from(context)
            val view = inflater.inflate(layoutIdForListItem, viewGroup, false)
            Log.d("view", "i am created")
            return SessionViewHolder(view)

        }

        /**
         * This function attaches the data to item view
         */
        override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
            holder.title.text = (position + 1).toString()
            holder.button.setOnClickListener {
                val session = mSessionData[position]
                mClickHandler.onClick(session)
            }

            if (sessionBools[position]) {
                holder.title.visibility = View.VISIBLE
                holder.lock.visibility = View.INVISIBLE
                holder.button.isClickable = true

            } else {
                holder.title.visibility = View.INVISIBLE
                holder.lock.visibility = View.VISIBLE
                holder.button.isClickable = false

            }


        }

        /**
         * This function gives the size back of the data list
         */
        override fun getItemCount(): Int {
            return mSessionData.count()
        }


        inner class SessionViewHolder(view: View) : RecyclerView.ViewHolder(view) {


            // Initialize TextView title
            val title: TextView = view.tv_session_title
            val button: FloatingActionButton = view.fab
            val lock: ImageView = view.iv_lock

            init {
//                view.fab.setOnClickListener {
//                    val session = mSessionData[position]
//                    mClickHandler.onClick(session)
//                }
            }

        }


        // Implement this interface for passing click event
        interface SessionAdapterOnClickHandler {
            fun onClick(session: Model.Session)
        }

    }
}