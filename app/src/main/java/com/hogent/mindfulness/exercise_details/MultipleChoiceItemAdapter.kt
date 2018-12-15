package com.hogent.mindfulness.exercise_details

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import com.hogent.mindfulness.R
import com.hogent.mindfulness.domain.Model
import com.hogent.mindfulness.domain.ViewModels.PageViewModel
import kotlinx.android.synthetic.main.multiple_choice_checkbox.view.*
import org.jetbrains.anko.sdk27.coroutines.onCheckedChange

class MultipleChoiceItemAdapter(
    private var mulChoiceItems: Array<Model.MultipleChoiceItem>
): RecyclerView.Adapter<MultipleChoiceItemAdapter.MultipleChoiceViewHolder>() {

    fun getMulChoiceItems(): Array<Model.MultipleChoiceItem> {
        return mulChoiceItems
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultipleChoiceViewHolder {
        val context:Context = parent.context
        val inflater = LayoutInflater.from(context)
        val layoutIdCboxItem = R.layout.multiple_choice_checkbox

        val view = inflater.inflate(layoutIdCboxItem, parent, false)

        return MultipleChoiceViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mulChoiceItems.size
    }

    override fun onBindViewHolder(holder: MultipleChoiceViewHolder, position: Int) {
        val mulChoice = mulChoiceItems[position]
        holder.cBox.setText(mulChoice.message)
        holder.cBox.isChecked = mulChoice.checked
        holder.cBox.onCheckedChange { buttonView, isChecked ->
            mulChoiceItems[position].checked = isChecked
        }
    }

    inner class MultipleChoiceViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var cBox: CheckBox = view.checkBox
    }
}