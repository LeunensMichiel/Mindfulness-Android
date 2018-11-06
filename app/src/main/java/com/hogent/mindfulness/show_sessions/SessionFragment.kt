package com.hogent.mindfulness.show_sessions


import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.DisplayMetrics
import android.view.*
import android.widget.TextView
import android.widget.Toast
import com.hogent.mindfulness.MainActivity
import com.hogent.mindfulness.R
import com.hogent.mindfulness.ScannerActivity
import com.hogent.mindfulness.data.MindfulnessApiService
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
    private fun showResult(sessions:Array<Model.Session>){
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
        private val mSessionData: Array<Model.Session>,
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

            // Initialize TextView title
            val title: TextView = view.tv_session_title

            /**
             * Add clicklistener on the Button from the recyclerview
             */
            init {


                view.fab.setOnClickListener {
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
            fun onClick(session: Model.Session)
        }

    }
}