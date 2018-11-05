package com.hogent.mindfulness.show_sessions


import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.hogent.mindfulness.R
import com.hogent.mindfulness.domain.Model
import kotlinx.android.synthetic.main.session_item_list.*
import kotlinx.android.synthetic.main.session_item_list.view.*
import org.w3c.dom.Text

class SessionAdapter(
    private val mSessionData: Array<Model.Session>,
    private val mClickHandler: SessionAdapterOnClickHandler
) : RecyclerView.Adapter<SessionAdapter.SessionViewHolder>() {


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): SessionViewHolder {
        val context = viewGroup.context
        val layoutIdForListItem = R.layout.session_item_list
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(layoutIdForListItem, viewGroup, false)
        return SessionViewHolder(view)

    }


    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        holder.title.text = (position + 1).toString()
    }

    override fun getItemCount(): Int {
        return mSessionData.count()
    }

    inner class SessionViewHolder(view: View) : RecyclerView.ViewHolder(view){

        val button: FloatingActionButton = view.fab
        val title: TextView = view.tv_session_title

        init {
            button.setOnClickListener{
                val adapterPosition = adapterPosition
                val session = mSessionData[adapterPosition]

                //Log.d("test", "onclick2")
                //mClickHandler.onClick(session)

                mClickHandler.onClick(session)
            }
        }

    }

    interface SessionAdapterOnClickHandler {
        fun onClick(session: Model.Session)
    }
}
