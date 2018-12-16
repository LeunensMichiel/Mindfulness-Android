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
import com.hogent.mindfulness.domain.Model
import com.hogent.mindfulness.domain.ViewModels.SessionViewModel
import com.hogent.mindfulness.domain.ViewModels.StateViewModel
import com.hogent.mindfulness.scanner.ScannerActivity
import kotlinx.android.synthetic.main.session_fragment.*
import kotlinx.android.synthetic.main.session_item_list.view.*
import org.jetbrains.anko.sdk27.coroutines.onClick


class SessionFragment : Fragment() {

    private lateinit var mAdapter: SessionAdapter
    private lateinit var sessionView: SessionViewModel
    private lateinit var stateView: StateViewModel

    /**
     * In the onCreateView, we'll initialize the sessionViewModel and StateViewModel so we can use its Login Methods.
     * After that the layout for this fragment will be inflated and the sessions will be retrieved
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        sessionView = activity?.run {
            ViewModelProviders.of(this).get(SessionViewModel::class.java)
        } ?: throw Exception("Invalid activity")

        stateView = activity?.run {
            ViewModelProviders.of(this).get(StateViewModel::class.java)
        } ?: throw Exception("Invalid activity")

        sessionView.retrieveSessions()

        return inflater.inflate(R.layout.session_fragment, container, false)
    }

    /**
     * In the onViewCreated, we update the UI to display the current status of the user.
     * we also observe on the list of sessions to trigger UI changes. Thanks to this the
     * textfields are updated, we scroll to the our latest unlocked session.
     * We also Initialize the Recyclerview with its adapter and make it *Snappy*
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Progress_subtext.text = (getString(R.string.NoSessionsYet)).toUpperCase()
        monsterCount.text = "  x 0"

        sessionView.sessionList.observe(this, Observer {
            if (it != null) {
                val unlocked: Array<Model.Session>
                var unlocked_session: Model.Session? = null
                val tempUnlockedCheck: List<Model.Session>
                if (sessionView.userRepo.user.value?.unlocked_sessions!!.isNotEmpty()) {
                    unlocked = sessionView.sessionList.value!!
                    tempUnlockedCheck = unlocked.filter {
                        it._id == sessionView.userRepo.user.value?.unlocked_sessions?.last()
                    }
                    if (tempUnlockedCheck.isNotEmpty()) {
                        unlocked_session = tempUnlockedCheck.last()
                    }
                }
                if (unlocked_session != null) {
                    Progress_subtext.text = ("Huidige sessie: " + unlocked_session.title).toUpperCase()
                    monsterCount.text = "  x " + sessionView.sessionList.value?.filter {
                        it.unlocked
                    }?.size
                    rv_sessions.scrollToPosition(it.indexOf(unlocked_session))
                } else {
                    Progress_subtext.text = (getString(R.string.NoSessionsYet)).toUpperCase()
                    monsterCount.text = "  x 0"
                }
            }
        })

        mAdapter = SessionAdapter(
            this,
            sessionView,
            stateView,
            activity as SessionAdapter.SessionAdapterOnUnlockSession,
            (activity as MainActivity)
        )
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
     * - Sets the cameraOnClickListener to get to the ScanningActivity
     */
    override fun onStart() {
        super.onStart()
        fabCamera.setOnClickListener { view ->
            val intent = Intent(activity, ScannerActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * In the onResume Method we change the actionbarTitle
     */
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
        lifecycleOwner: LifecycleOwner,
        private var sessionView: SessionViewModel,
        private var stateView: StateViewModel,
        private val sessionAdapterOnUnlockSession: SessionAdapterOnUnlockSession,
        var context: Context
    ) : RecyclerView.Adapter<SessionAdapter.SessionViewHolder>() {

        private var mSessionData: Array<Model.Session>
        private var user: Model.User
        private val sharedpref = context.getSharedPreferences(
            context.getString(R.string.sharedPreferenceUserDetailsKey),
            Context.MODE_PRIVATE
        )

        /**
         * We initialise another observer in our Adapter so we can later on check
         * if an animation has to be played and a session has to be unlocked
         */
        init {
            mSessionData = arrayOf()
            user = sessionView.userRepo.user.value!!
            sessionView.sessionList.observe(lifecycleOwner, android.arch.lifecycle.Observer {
                if (mSessionData.isEmpty()) {
                    mSessionData = it!!
                    this.notifyDataSetChanged()
                }
            })
        }

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
                sessionView.selectedSession.value = currentSession
                stateView.viewState.value = "EXERCISE_VIEW"
            }

            /**
             * This is a fun one. Each item has its animations. We first check if the item we want to load is already unlocked or not
             * If not, the animations will be greyed out and the buttons will be locked.
             * If so, we check if the current item is also the last unlocked item. If not, the animations will be put in their already played state.
             * We then continue to check if this item's animation has already played. If so, we also put it in his already played state and if not, the animation plays and a fullscreen dialog will appear
             */
            val posie = holder.adapterPosition
            if (mSessionData[posie].unlocked) { // De SESSIONS ZIJN UNLOCKED
                holder.title.visibility = View.VISIBLE
                holder.lock.visibility = View.INVISIBLE
                holder.button.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#bedacd"))
                holder.button.isClickable = true

                holder.button.setOnLongClickListener {
                    sessionView.selectedSession.value = currentSession
                    stateView.dialogState.value = "FEEDBACK_DIALOG"
                    return@setOnLongClickListener true
                }
                if (mSessionData[posie]._id == user.unlocked_sessions.last()) { // We gaan in de if clause als we het hebben over de laatste unlockte sessie
                    if (sharedpref.getString(
                            context.getString(R.string.lastUnlockedSession),
                            null
                        ) == "unlocked" + posie.toString()
                    ) { //We gaan nakijken of de animatie ooit al eens is afgespeeld door te kijken in de sharedpref
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
                        sharedpref.edit()
                            .putString(context.getString(R.string.lastUnlockedSession), "unlocked" + posie.toString())
                            .apply()
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

            } else {  //DE SESSIONS ZIJN GELOCKED
                holder.title.visibility = View.INVISIBLE
                holder.lock.visibility = View.VISIBLE
                holder.button.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#a6bfb4"))
                holder.button.isClickable = false
                holder.button.setOnClickListener(null)

                holder.button.onClick {
                    Toast
                        .makeText(context, context.getString(R.string.lockedSession), Toast.LENGTH_SHORT)
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

        /*
         * This function attaches the data to item view
         */
        override fun getItemCount(): Int {
            return mSessionData.count()
        }

        /**
         * Viewholder for the sessionlist Recyclerview
         */
        inner class SessionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val title: TextView = view.tv_session_title
            val button: FloatingActionButton = view.fab
            val lock: ImageView = view.iv_lock
            val glowing_orbAnimation: LottieAnimationView = view.glowing_orb_animation
            val progressAnimation: LottieAnimationView = view.progressbar_animation

        }

        /**
         * This interface will be used to trigger a fullscreen Dialog in the main Activity when a session gets unlocked
         */
        interface SessionAdapterOnUnlockSession {
            fun showMonsterDialog()
        }

    }
}