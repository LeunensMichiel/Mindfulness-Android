package com.hogent.mindfulness.show_sessions

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.hogent.mindfulness.R
import com.hogent.mindfulness.domain.Model
import kotlinx.android.synthetic.main.session_item_list.view.*

class SessionAdapter(private val mSessionData: Array<Model.Session>,
                     private val mClickHandler: SessionAdapterOnClickHandler) : RecyclerView.Adapter<SessionAdapter.SessionViewHolder>() {


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): SessionViewHolder {
        val context = viewGroup.context
        val layoutIdForListItem = R.layout.session_item_list
        val inflater = LayoutInflater.from(context)

        val view = inflater.inflate(layoutIdForListItem, viewGroup, false)

        return  SessionViewHolder(view)

    }

    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        val sessionTitle = mSessionData[position]
        holder.title.text = sessionTitle.title

    }


    override fun getItemCount(): Int {
        return mSessionData.count()
    }


    inner class SessionViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        val title: TextView = view.tv_session_title

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            val adapterPosition = adapterPosition
            val session = mSessionData[adapterPosition]

            Log.d("test", "onclick2")
            mClickHandler.onClick(session)

        }

    }

    interface SessionAdapterOnClickHandler {
        fun onClick(session: Model.Session)
    }
}
