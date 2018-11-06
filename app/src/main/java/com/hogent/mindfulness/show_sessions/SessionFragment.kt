package com.hogent.mindfulness.show_sessions


import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.hogent.mindfulness.R
import com.hogent.mindfulness.data.MindfulnessApiService
import com.hogent.mindfulness.domain.Model.Point
import com.hogent.mindfulness.domain.Model.Session
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.session_fragment.*
import kotlinx.android.synthetic.main.session_item_list.view.*
import android.util.DisplayMetrics
import android.util.Log
import android.view.ViewTreeObserver


class SessionFragment : Fragment() {

    private val coordinations: Array<Point> = arrayOf(
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
    private lateinit var disposable: Disposable
    private val mindfulnessApiService by lazy {
        MindfulnessApiService.create()
    }

    /**
     * I used this resource: https://developer.android.com/guide/topics/ui/layout/recyclerview
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        beginRetrieveSessionmap()
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.session_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.viewTreeObserver.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener{
            override fun onGlobalLayout() {
                sessionFragment.post {

                    val height = view.height
                    val width = view.width

                    val centerx = (progress_img.width.toFloat() / 2.0).toFloat()
                    val bottomy = progress_img.height

                    val currentSession = 1.0 - 1
                    val sessionSize = 15.0
                    val coPoint = (currentSession / sessionSize.toFloat()) * coordinations.size.toFloat()
                    val point = coordinations[coPoint.toInt()]
                    val newHeight = (point.y.toFloat() / imgHeight) * height.toFloat()
                    val newWidth = (point.x.toFloat() / imgWidth) * width.toFloat()

                    progress_img.x = newWidth.toFloat() - centerx
                    progress_img.y = newHeight.toFloat() - bottomy

                    Log.d("ventje_x", progress_img.x.toString())
                    Log.d("ventje_y", width.toString())

                    view.viewTreeObserver.removeOnGlobalLayoutListener(this)

                }
            }
        })

    }


    /**
     * this function retrieves a sessionmap from the database
     */
    private fun beginRetrieveSessionmap() {
        disposable = mindfulnessApiService.getSessionmap(getString(R.string.sessionmap_id))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> showResult(result) },
                { error -> showError(error.message) }
            )
    }

    private fun showError(errMsg: String?) {
        Toast.makeText(activity, errMsg, Toast.LENGTH_SHORT).show()
    }

    /**
     * Initialize SessionAdapter
     * Initialize LayoutManager
     * Add Manager and Adapter to recyclerview
     */
    private fun showResult(sessions: Array<Session>) {
        val viewAdapter = SessionAdapter(sessions, activity as SessionAdapter.SessionAdapterOnClickHandler)
        val viewManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)



        rv_sessions.apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    /***********************************************************************************************
     * Adapter
     *
     ***********************************************************************************************
     */

    class SessionAdapter(
        // This array has the data for the recyclerview adapter
        private val mSessionData: Array<Session>,
        //mClickHandler is for communicating whit the activity when item clicked
        private val mClickHandler: SessionAdapterOnClickHandler
    ) : RecyclerView.Adapter<SessionAdapter.SessionViewHolder>() {


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
            holder.title.text = (position + 1).toString()
        }

        /**
         * This function gives the size back of the data list
         */
        override fun getItemCount(): Int {
            return mSessionData.count()
        }


        inner class SessionViewHolder(view: View) : RecyclerView.ViewHolder(view) {

            // Initialize Floating Action Button
            val button: FloatingActionButton = view.fab
            // Initialize TextView title
            val title: TextView = view.tv_session_title

            /**
             * Add clicklistener on the Button from the recyclerview
             */
            init {
                button.setOnClickListener {
                    val adapterPosition = adapterPosition
                    val session = mSessionData[adapterPosition]

                    //Log.d("test", "onclick2")
                    //mClickHandler.onClick(session)

                    mClickHandler.onClick(session)
                }
            }

        }

        // Implement this interface for passing click event
        interface SessionAdapterOnClickHandler {
            fun onClick(session: Session)
        }

    }
}