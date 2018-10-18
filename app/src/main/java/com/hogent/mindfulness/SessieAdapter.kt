package com.hogent.mindfulness

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class SessieAdapter(private val mNumberItems: Int) : RecyclerView.Adapter<SessieAdapter.NumberViewHolder>() {



    init {
        viewHolderCount = 0
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): NumberViewHolder {
        val context = viewGroup.context
        val layoutIdForListItem = R.layout.number_list_item
        val inflater = LayoutInflater.from(context)
        val shouldAttachToParentImmediately = false

        val view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately)
        val viewHolder = NumberViewHolder(view)

        viewHolder.viewHolderIndex.text = "ViewHolder index: $viewHolderCount"


        viewHolderCount++
        Log.d(TAG, "onCreateViewHolder: number of ViewHolders created: $viewHolderCount")
        return viewHolder
    }


    override fun onBindViewHolder(holder: NumberViewHolder, position: Int) {
        Log.d(TAG, "#$position")
        holder.bind(position)
    }


    override fun getItemCount(): Int {
        return mNumberItems
    }


    inner class NumberViewHolder

        (itemView: View) : RecyclerView.ViewHolder(itemView) {

        var listItemNumberView: TextView

        var viewHolderIndex: TextView

        init {

            listItemNumberView = itemView.findViewById(R.id.tv_item_number) as TextView
            viewHolderIndex = itemView.findViewById(R.id.tv_view_holder_instance) as TextView

        }

        fun bind(listIndex: Int) {
            listItemNumberView.text = listIndex.toString()
        }


    }

    companion object {

        private val TAG = SessieAdapter::class.java.simpleName

        private var viewHolderCount: Int = 0
    }
}
