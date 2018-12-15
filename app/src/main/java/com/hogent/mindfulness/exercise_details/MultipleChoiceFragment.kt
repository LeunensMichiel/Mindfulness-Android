package com.hogent.mindfulness.exercise_details


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.hogent.mindfulness.R
import com.hogent.mindfulness.domain.Model
import com.hogent.mindfulness.domain.ViewModels.PageViewModel
import kotlinx.android.synthetic.main.fragment_image_input.*
import kotlinx.android.synthetic.main.fragment_multiple_choice.*
import kotlinx.android.synthetic.main.fragment_text_input.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import java.lang.Exception

/**
 * A simple [Fragment] subclass.
 *
 */
class MultipleChoiceFragment : Fragment() {

    private lateinit var post: Model.Post
    private lateinit var pageView: PageViewModel
    var position: Int = -1
    lateinit var page: Model.Page

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageView = activity?.run {
            ViewModelProviders.of(this).get(PageViewModel::class.java)
        } ?: throw Exception("Invalid activity.")

        pageView.pages.observe(this, Observer {
            if (it!![position].post != null) {
                post = it!![position].post!!
                initialiseMulChoiceList(it!![position])
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_multiple_choice, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("MULTIPLE_CHOICE_FRAG","${pageView.pages.value!![position].multiple_choice_items}")
        if (pageView.pages.value!![position].multiple_choice_items.isNotEmpty()){
            pageView.pages.value!![position].multiple_choice_items.forEach {
                Log.d("MULTIPLE_CHOICE_FRAG","$it")
            }
        }

        fragment_mulchoice_btn.onClick {
            pageView.pageError.postValue(Model.errorMessage(null, "Input nog niet klaar."))
        }

        if (pageView.pages.value!![position].post == null){
            Log.d("MUL_CHOICE", "POST_IS_NULL")
            pageView.checkInputPage(page._id, position)
        } else {
            initialiseMulChoiceList(pageView.pages.value!![position])
        }

    }

    fun initialiseMulChoiceList(it: Model.Page) {
        var mulChoiceList:Array<Model.MultipleChoiceItem> = arrayOf()
        if (it.post?.multiple_choice_items?.isNotEmpty()!!){
            mulChoiceList = it.post?.multiple_choice_items!!

        } else {
            mulChoiceList = it.multiple_choice_items
        }
        if (rv_mulchoice != null) {
            Log.d("MUL_CHOICE_ADAPTER", "NULL")
            val viewAdapter = MultipleChoiceItemAdapter(mulChoiceList)
            val viewManager = LinearLayoutManager(activity)

            rv_mulchoice.apply {
                layoutManager = viewManager
                adapter = viewAdapter
            }

            fragment_mulchoice_input_txf.setText(page.title)

            fragment_mulchoice_btn.onClick {
                post = pageView.updatePost(position, page, fragment_mulchoice_input_txf.text.toString(), post, (rv_mulchoice.adapter as MultipleChoiceItemAdapter).getMulChoiceItems())
            }
        }
    }
}
