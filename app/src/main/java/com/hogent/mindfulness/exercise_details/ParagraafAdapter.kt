package com.hogent.mindfulness.exercise_details

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.hogent.mindfulness.R
import com.hogent.mindfulness.R.drawable.tweedetestfoto
import com.hogent.mindfulness.domain.Model
import com.hogent.mindfulness.domain.ViewModels.PageViewModel
import com.hogent.mindfulness.exercise_details.ParagraafAdapter.ParagraafViewHolder
import kotlinx.android.synthetic.main.paragraaf_list_item.view.*
import org.jetbrains.anko.imageResource

/**
 * Dit is de adapter voor de recyclerview van paragrafen
 * de variabele mParagrafen heeft de data voor de recyclerviewadapter
 * tutorial voor RecyclerView: https://www.youtube.com/watch?v=Vyqz_-sJGFk
 */
class ParagraafAdapter(private var mParagrafen: Array<Model.Paragraph>, private var pageView:PageViewModel): RecyclerView.Adapter<ParagraafViewHolder>(){

    fun setDataSet(pars: Array<Model.Paragraph>) {
        Log.d("TEXT_VIEW_CHANGE","DATASET_SET")
        mParagrafen = pars
        notifyDataSetChanged()
    }

    /**
     * deze functie laadt de item view in
     * deze methode is verantwoordelijk voor het inflaten van de view
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParagraafViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val layoutIdParListItem = R.layout.paragraaf_list_item

        val view = inflater.inflate(layoutIdParListItem, parent, false)

        return ParagraafViewHolder(view)
    }
    /**
     * deze functie geeft de grootte van de datalijst weer
     */
    override fun getItemCount(): Int {
        return mParagrafen.size
    }

    /**
     * Deze functie voegt de data toe aan de item view
     */
    @SuppressLint("WrongConstant")
    override fun onBindViewHolder(holder: ParagraafAdapter.ParagraafViewHolder, position: Int) {
        val paragraafTitle = mParagrafen[position]
        holder.setIsRecyclable(false)
        if(paragraafTitle.type == "TEXT"){
            holder.title.text = paragraafTitle.description
        }
        else if(paragraafTitle.type == "IMAGE"){
            Log.d("FILE_NAME", mParagrafen[position].imageFilename)
            if (pageView.bitHashMap.containsKey(mParagrafen[position].imageFilename)) {
                Log.d("FILE_NAME", "")
                holder.foto.setImageBitmap(pageView.bitHashMap.get(mParagrafen[position].imageFilename))

            } else {
                pageView.retrieveTextPageImg(mParagrafen[position].imageFilename)
                holder.foto.imageResource = tweedetestfoto
            }
        }
    }

    /**
     * De viewholder zal elke list item in het geheugen houden
     */
    inner class ParagraafViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.tv_paragraaf_tekst
        val foto: ImageView = view.tv_paragraaf_foto
    }
}