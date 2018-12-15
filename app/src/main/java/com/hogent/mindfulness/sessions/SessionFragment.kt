package com.hogent.mindfulness.sessions


import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.model.KeyPath
import com.hogent.mindfulness.MainActivity
import com.hogent.mindfulness.R
import com.hogent.mindfulness.data.FIleApiService
import com.hogent.mindfulness.data.LocalDatabase.MindfulnessDBHelper
import com.hogent.mindfulness.data.SessionApiService
import com.hogent.mindfulness.domain.Model
import com.hogent.mindfulness.domain.ViewModels.SessionViewModel
import com.hogent.mindfulness.domain.ViewModels.StateViewModel
import com.hogent.mindfulness.scanner.ScannerActivity
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.session_fragment.*
import kotlinx.android.synthetic.main.session_item_list.view.*
import org.jetbrains.anko.sdk27.coroutines.onClick


class SessionFragment : Fragment() {
    /**
     * Here will the sessionData be stored
     * Disposable used for calling api calls
     */

    private lateinit var sessions: Array<Model.Session>
    private lateinit var mAdapter: SessionAdapter
    private lateinit var sessionBools: BooleanArray
    private lateinit var disposable: Disposable
    private lateinit var user: Model.User
    private lateinit var sessionView: SessionViewModel
    private lateinit var stateView:StateViewModel
    lateinit var unlockSession: String
    private lateinit var sessionService:SessionApiService
    private lateinit var fileService: FIleApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionView = activity?.run {
            ViewModelProviders.of(this).get(SessionViewModel::class.java)
        } ?: throw Exception("Invalid activity")

        stateView = activity?.run {
            ViewModelProviders.of(this).get(StateViewModel::class.java)
        } ?: throw Exception("Invalid activity")

        sessionView.retrieveSessions()
    }

    /**
     * I used this resource: https://developer.android.com/guide/topics/ui/layout/recyclerview
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.session_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Progress_subtext.text = ("Huidige sessie: Nog geen vrijgespeeld!").toUpperCase()
        monsterCount.text = "  x 0"
        sessionView.sessionList.observe(this, Observer {
            if (it != null) {
                sessionView.loadImages()
                var unlocked:Array<Model.Session> = arrayOf()
                var unlocked_session:Model.Session? = null
                if (sessionView.userRepo.user.value?.unlocked_sessions!!.isNotEmpty()) {
                    unlocked = sessionView.sessionList.value!!
                    unlocked_session = unlocked.filter {
                        it._id == sessionView.userRepo.user.value?.unlocked_sessions?.last()
                    }.last()
                }
                if (unlocked_session != null){
                    Progress_subtext.text = ("Huidige sessie: " + unlocked_session.title).toUpperCase()
                    monsterCount.text = "  x " + sessionView.sessionList.value?.filter {
                        it.unlocked
                    }?.size
                    Log.d("SESSION_VIEW_HOLDER","SCROLL_START")
                    rv_sessions.scrollToPosition(it.indexOf(unlocked_session))
                    Log.d("SESSION_VIEW_HOLDER","SCROLL_END")
                    //rv_sessions.adapter?.notifyDataSetChanged()
                } else {
                    Progress_subtext.text = ("Huidige sessie: Nog geen vrijgespeeld!").toUpperCase()
                    monsterCount.text = "  x 0"
                }
            }
        })
        mAdapter = SessionAdapter( this, sessionView, stateView, activity as SessionAdapter.SessionAdapterOnUnlockSession, (activity as MainActivity))
        val viewManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        val snappy = LinearSnapHelper()
        rv_sessions.apply {
            layoutManager = viewManager
            adapter = mAdapter
        }
        snappy.attachToRecyclerView(rv_sessions)
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

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).setActionBarTitle("Mindfulness")

    }

    /***********************************************************************************************
     * Adapter
     *
     ***********************************************************************************************
     */

    class SessionAdapter(
        private var lifecycleOwner: LifecycleOwner,
        private var sessionView: SessionViewModel,
        private var stateView:StateViewModel,
        private val sessionAdapterOnUnlockSession: SessionAdapterOnUnlockSession,
        var context: Context
    ) : RecyclerView.Adapter<SessionAdapter.SessionViewHolder>() {

        private var mSessionData: Array<Model.Session>
        private var user:Model.User

        val sharedpref = context.getSharedPreferences(context.getString(R.string.sharedPreferenceUserDetailsKey), Context.MODE_PRIVATE)


        init {
            mSessionData = arrayOf()
            user = sessionView.userRepo.user.value!!
            sessionView.sessionList.observe(lifecycleOwner, android.arch.lifecycle.Observer {
                if (mSessionData.isEmpty()){
                    mSessionData = it!!
                    this.notifyDataSetChanged()
                }
            })
        }

        // This is for knowing what the last unlocked session was. If user gets an update, this will trigger the animations
        /**
         * This function loads in the item view
         */
        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): SessionViewHolder {
            val context = viewGroup.context
            val layoutIdForListItem = R.layout.session_item_list
            val inflater = LayoutInflater.from(context)
            val view = inflater.inflate(layoutIdForListItem, viewGroup, false)

            return SessionViewHolder(view)

        }

        /**
         * This function attaches the data to item view
         */
        override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {

            val currentSession = mSessionData[position]
            holder.title.text = (position + 1).toString()
            holder.button.setOnClickListener {
                sessionView.selectedSession?.value = currentSession
                stateView.viewState?.value = "EXERCISE_VIEW"
            }

            val posie = holder.adapterPosition
//            sharedpref.edit().remove(context.getString(R.string.lastUnlockedSession)).apply()
//            sessionView.resetunlockedSession()

            if (mSessionData[posie].unlocked) { // De SESSIONS ZIJN UNLOCKED
                holder.title.visibility = View.VISIBLE
                holder.lock.visibility = View.INVISIBLE
                holder.button.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#bedacd"))
                holder.button.isClickable = true

                holder.button.setOnLongClickListener {
                    sessionView.selectedSession?.value = currentSession
                    stateView.dialogState?.value = "FEEDBACK_DIALOG"
                    return@setOnLongClickListener true
                }
                if (mSessionData[posie]._id == user.unlocked_sessions.last()) { // We gaan in de if clause als we het hebben over de laatste unlockte sessie
                    Log.d("SESSION_VIEW_HOLDER","ANIM_START")
                    if (sharedpref.getString(context.getString(R.string.lastUnlockedSession), null) == "unlocked" + posie.toString()) { //We gaan nakijken of de animatie ooit al eens is afgespeeld door te kijken in de sharedpref
                        //Instellen op reeds afgespeeld
                        holder.progressAnimation.frame = 50
                        holder.progressAnimation.addValueCallback(KeyPath("**"), LottieProperty.COLOR) {
                            Color.parseColor("#f9a825")
                        }
                        holder.glowing_orbAnimation.addValueCallback(KeyPath("**"), LottieProperty.COLOR) {
                            Color.parseColor("#f9a825")
                        }
                    } else {
                        //Animatie spelen en dan in de sharedprefs zeggen dat hij al is afgespeeld
                        holder.progressAnimation.setMaxFrame(50)
                        holder.progressAnimation.playAnimation()
                        holder.progressAnimation.addAnimatorListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator?) {
                                holder.progressAnimation.addValueCallback(KeyPath("**"), LottieProperty.COLOR) {
                                    Color.parseColor("#f9a825")
                                }
                                holder.glowing_orbAnimation.playAnimation()
                            }
                        })
                        holder.glowing_orbAnimation.addAnimatorListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator?) {
                                holder.glowing_orbAnimation.addValueCallback(KeyPath("**"), LottieProperty.COLOR) {
                                    Color.parseColor("#f9a825")
                                }
                                sessionAdapterOnUnlockSession.showMonsterDialog()

                            }
                        })
                        sharedpref.edit().putString(context.getString(R.string.lastUnlockedSession), "unlocked" + posie.toString()).apply()
                    }
                } else { //De overige unlocked sessions
                    holder.progressAnimation.frame = 50
                    holder.progressAnimation.addValueCallback(KeyPath("**"), LottieProperty.COLOR) {
                        Color.parseColor("#f9a825")
                    }
                    holder.glowing_orbAnimation.addValueCallback(KeyPath("**"), LottieProperty.COLOR) {
                        Color.parseColor("#f9a825")
                    }
                }

            } else {       //DE SESSIONS ZIJN GELOCKED
                holder.title.visibility = View.INVISIBLE
                holder.lock.visibility = View.VISIBLE
                holder.button.isClickable = false
                holder.button.setOnClickListener(null)

                holder.button.onClick {
                    Toast
                        .makeText(context, "Deze sessie is nog niet unlocked.", Toast.LENGTH_SHORT)
                        .show()

                }
                //Animations
                holder.glowing_orbAnimation.cancelAnimation()
                holder.progressAnimation.cancelAnimation()
                holder.progressAnimation.addValueCallback(KeyPath("**"), LottieProperty.COLOR) {
                    Color.parseColor("#BDBDBD")
                }
                holder.glowing_orbAnimation.addValueCallback(KeyPath("**"), LottieProperty.COLOR) {
                   Color.parseColor("#BDBDBD")
                }
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
            val glowing_orbAnimation: LottieAnimationView = view.glowing_orb_animation
            val progressAnimation: LottieAnimationView = view.progressbar_animation

        }

        interface  SessionAdapterOnUnlockSession {
            fun showMonsterDialog()
        }

    }
}