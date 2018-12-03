package com.hogent.mindfulness.show_sessions


import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.airbnb.lottie.LottieAnimationView
import com.hogent.mindfulness.MainActivity
import com.hogent.mindfulness.R
import com.hogent.mindfulness.data.FeedbackApiService
import com.hogent.mindfulness.data.LocalDatabase.MindfulnessDBHelper
import com.hogent.mindfulness.data.ServiceGenerator
import com.hogent.mindfulness.data.SessionApiService
import com.hogent.mindfulness.data.UserApiService
import com.hogent.mindfulness.domain.Model
import com.hogent.mindfulness.domain.Model.Point
import com.hogent.mindfulness.scanner.ScannerActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.session_fragment.*
import kotlinx.android.synthetic.main.session_item_list.view.*
import java.util.*


class SessionFragment : Fragment() {

    private val coordinates: Array<Point> = arrayOf(
        Point(50, 1840, true),
        Point(500, 1860, true),
        Point(910, 1836, true),
        Point(847, 1740, false),
        Point(500, 1715, false),
        Point(164, 1670, false),
        Point(200, 1550, true),
        Point(500, 1555, true),
        Point(825, 1553, true),
        Point(732, 1455, false),
        Point(360, 1440, false),
        Point(370, 1355, true),
        Point(663, 1348, true),
        Point(545, 1255, false),
        Point(560, 1160, false)
    )

    private val imgWidth = 1080.0
    private val imgHeight = 1920.0

    /**
     * Here will the sessionData be stored
     * Disposable used for calling api calls
     */
    private val mMindfullDB by lazy {
        MindfulnessDBHelper(activity as MainActivity )
    }
    private lateinit var sessions: Array<Model.Session>
    private lateinit var mAdapter: SessionAdapter
    private lateinit var sessionBools: BooleanArray
    private lateinit var disposable: Disposable
    private lateinit var user: Model.User
    lateinit var unlockSession: String
    private lateinit var sessionService:SessionApiService
    /**
     * I used this resource: https://developer.android.com/guide/topics/ui/layout/recyclerview
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        user = mMindfullDB.getUser()!!
        Log.i("DBUSER", "$user")
        //beginRetrieveUser()
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.session_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessions = arrayOf<Model.Session>()
        sessionBools = BooleanArray(10)

        beginRetrieveSessionmap(user.group!!.sessionmap_id)

        mAdapter = SessionAdapter(sessions, activity as SessionAdapter.SessionAdapterOnClickHandler, sessionBools, user)

        val viewManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

        Log.d("sessions", sessions.size.toString())

        rv_sessions.apply {
            layoutManager = viewManager
            adapter = mAdapter
        }

//        view.viewTreeObserver.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener{
//            override fun onGlobalLayout() {
//                sessionFragment.post {
//
//                    val height = view.height
//                    val width = view.width
//
//                    val centerx = (progress_img.width.toFloat() / 2.0).toFloat()
//                    val bottomy = progress_img.height
//
//                    val currentSession = user.unlocked_sessions.size
//                    val sessionSize = 15.0
//                    val coPoint = (currentSession / sessionSize.toFloat()) * coordinates.size.toFloat()
//                    val point = coordinates[coPoint.toInt()]
//                    val newHeight = (point.y.toFloat() / imgHeight) * height.toFloat()
//                    val newWidth = (point.x.toFloat() / imgWidth) * width.toFloat()
//
//                    progress_img.x = newWidth.toFloat() - centerx
//                    progress_img.y = newHeight.toFloat() - bottomy
//
//                    Log.d("ventje_x", progress_img.x.toString())
//                    Log.d("ventje_y", width.toString())
//
//                    view.viewTreeObserver.removeOnGlobalLayoutListener(this)
//
//                }
//            }
//        })
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
        val userService = ServiceGenerator.createService(UserApiService::class.java, (activity as MainActivity))

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
        val sessionService = ServiceGenerator.createService(SessionApiService::class.java,
            activity!!.getSharedPreferences(getString(R.string.sharedPreferenceUserDetailsKey), Context.MODE_PRIVATE)
            .getString(getString(R.string.authTokenKey), null))

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
        Log.d("testtest", user.group!!.sessionmap_id)
        beginRetrieveSessionmap(user.group!!.sessionmap_id)

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
        var sessionBools: BooleanArray,
        val user: Model.User
    ) : RecyclerView.Adapter<SessionAdapter.SessionViewHolder>() {

        private lateinit var disposable: Disposable
        private val LIKE_START = 0.4f
        private val LIKE_END = 0.7f
        private val UNLIKE_START = 0.93f
        private val UNLIKE_END = 1f
        private val PLAY_START = 0f
        private val PLAY_END = 0.5f
        private val PAUSE_START = 0.5f
        private val PAUSE_END = 1f


        /**
         * This function loads in the item view
         */
        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): SessionViewHolder {
            val context = viewGroup.context
            val layoutIdForListItem = R.layout.session_item_list
            val inflater = LayoutInflater.from(context)
            val view = inflater.inflate(layoutIdForListItem, viewGroup, false)

            //Tijdelijke Manier om van Sessions Feedback Te krijgen
            val feedbackDialog = Dialog(context)
            feedbackDialog.setContentView(R.layout.feedback_popup)
            feedbackDialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val sessionname = feedbackDialog.findViewById<TextView>(R.id.feedback_namesessie)
            val description = feedbackDialog.findViewById<TextView>(R.id.feedback_description)
            val sendBtn = feedbackDialog.findViewById<Button>(R.id.feedback_sendBtn)
            val cancelBtn = feedbackDialog.findViewById<Button>(R.id.feedback_cancelBtn)
            val noFeedbackbtn = feedbackDialog.findViewById<Button>(R.id.feedback_uitschrijvenBtn)

            SessionViewHolder(view).button.setOnLongClickListener() {
                sessionname.text = mSessionData[SessionViewHolder(view).adapterPosition + 1].title
                sendBtn.setOnClickListener() {
                    val feedback = Model.Feedback(Date(), description.text.toString(), mSessionData[SessionViewHolder(view).adapterPosition + 1]._id)
                    val feedbackService = ServiceGenerator.createService(FeedbackApiService::class.java, user.token)
                    disposable = feedbackService.addFeedback(feedback)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                            { result ->
                                feedbackDialog.hide()
                                Toast.makeText(context, "Bedankt voor uw Feedback!", Toast.LENGTH_SHORT).show()
                            },
                            { error ->  Log.d("error", error.message)
                            }
                        )
                }
                cancelBtn.setOnClickListener() {
                    feedbackDialog.hide()
                }
                noFeedbackbtn.setOnClickListener() {
                    user.feedbackSubscribed = false
                    val userService = ServiceGenerator.createService(UserApiService::class.java, user.token)
                    disposable = userService.updateUserFeedback(user)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                            { result ->
                                    feedbackDialog.hide()
                            },
                            { error ->  Log.d("error", error.message)
                            }
                        )
                }
                feedbackDialog.show()
                return@setOnLongClickListener true
            }
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
//            val animator = ValueAnimator.ofFloat(LIKE_START, LIKE_END).setDuration(500)
//            animator.addUpdateListener {
//                animation -> holder.glowing_orb.progress = animation.getAnimatedValue() as Float
//            }
//            animator.start()
            sessionBools.forEach {
                Log.d("codes", it.toString())
            }
            if (sessionBools[position]) {
                holder.title.visibility = View.VISIBLE
                holder.lock.visibility = View.INVISIBLE
                holder.button.isClickable = true
            } else {
                holder.title.visibility = View.INVISIBLE
                holder.lock.visibility = View.VISIBLE
                holder.button.isClickable = false
                holder.button.setOnClickListener(null)
            }
            Log.i("BOOLCHECK","${holder.button.isClickable}")
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
            val glowing_orb: LottieAnimationView = view.glowing_orb_animation

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