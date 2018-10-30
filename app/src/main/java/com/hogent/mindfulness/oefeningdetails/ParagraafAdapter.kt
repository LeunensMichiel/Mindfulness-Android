package com.hogent.mindfulness.oefeningdetails

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.hogent.mindfulness.R
import com.hogent.mindfulness.domain.Model
import com.hogent.mindfulness.exercises_List_display.ExerciseAdapter
import com.hogent.mindfulness.oefeningdetails.ParagraafAdapter.ParagraafViewHolder
import kotlinx.android.synthetic.main.exercise_list_item.view.*
import kotlinx.android.synthetic.main.paragraaf_list_item.view.*

class ParagraafAdapter(private val mParagrafen: Array<Model.Paragraph>): RecyclerView.Adapter<ParagraafViewHolder>(){

    // This function loads in the item view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParagraafViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val layoutIdParListItem = R.layout.paragraaf_list_item

        val view = inflater.inflate(layoutIdParListItem, parent, false)

        return ParagraafViewHolder(view)
    }
    //  This function gives the size back of the data list
    override fun getItemCount(): Int {
        return mParagrafen.size
    }

    //  This function attaches the data to item view
    override fun onBindViewHolder(holder: ParagraafAdapter.ParagraafViewHolder, position: Int) {
        val paragraafTitle = mParagrafen[position]
        holder.title.text = paragraafTitle.description
    }

    inner class ParagraafViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.tv_paragraaf_tekst

    }
}