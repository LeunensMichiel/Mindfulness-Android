package com.hogent.mindfulness.oefeningdetails

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.hogent.mindfulness.R
import com.hogent.mindfulness.R.drawable.*
import com.hogent.mindfulness.domain.Model
import com.hogent.mindfulness.exercises_List_display.ExerciseAdapter
import com.hogent.mindfulness.oefeningdetails.ParagraafAdapter.ParagraafViewHolder
import kotlinx.android.synthetic.main.exercise_list_item.view.*
import kotlinx.android.synthetic.main.fragment_paragraaf_tekst.view.*
import kotlinx.android.synthetic.main.paragraaf_list_item.view.*
import org.jetbrains.anko.image
import org.jetbrains.anko.imageResource

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
    @SuppressLint("WrongConstant")
    override fun onBindViewHolder(holder: ParagraafAdapter.ParagraafViewHolder, position: Int) {
        val paragraafTitle = mParagrafen[position]
        //holder.title.text = paragraafTitle.description
        if(paragraafTitle.type == "TEXT"){
            holder.title.text = paragraafTitle.description
            //holder.foto.visibility = View.INVISIBLE
        }
        else if(paragraafTitle.type == "IMAGE"){
            //holder.title.text = paragraafTitle.description
            //holder.foto.imageResource = testfoto
            //holder.title.text = "foto"
            holder.foto.imageResource = tweedetestfoto
        }
    }

    inner class ParagraafViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.tv_paragraaf_tekst
        val foto: ImageView = view.tv_paragraaf_foto
    }
}