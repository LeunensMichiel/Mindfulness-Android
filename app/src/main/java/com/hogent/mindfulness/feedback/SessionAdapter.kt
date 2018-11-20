package com.hogent.mindfulness.feedback

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hogent.mindfulness.R
import com.hogent.mindfulness.domain.Model
import kotlinx.android.synthetic.main.feedback_session_item.view.*

class SessionAdapter(val items: ArrayList<Model.Session>, val context: Context) :
    RecyclerView.Adapter<SessionAdapter.sessionViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): sessionViewHolder {
        val vholder = sessionViewHolder(LayoutInflater.from(context).inflate(R.layout.feedback_session_item, p0, false))

        return vholder
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(p0: sessionViewHolder, p1: Int) {
        p0.titel.text = items[p1].title

    }


    class sessionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titel = itemView.sessionNameFeedback
    }
}