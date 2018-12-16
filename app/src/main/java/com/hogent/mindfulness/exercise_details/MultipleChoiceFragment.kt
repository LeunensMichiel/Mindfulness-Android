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
import android.widget.Toast
import com.hogent.mindfulness.MainActivity
import com.hogent.mindfulness.R
import com.hogent.mindfulness.domain.Model
import com.hogent.mindfulness.domain.ViewModels.PageViewModel
import kotlinx.android.synthetic.main.fragment_multiple_choice.*
import org.jetbrains.anko.sdk27.coroutines.onClick

/**
 * A simple [Fragment] subclass.
 *
 */
class MultipleChoiceFragment : PagerFragment() {

    private lateinit var post: Model.Post
    private lateinit var pageView: PageViewModel
    var position: Int = -1
    lateinit var page: Model.Page

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageView = activity?.run {
            ViewModelProviders.of(this).get(PageViewModel::class.java)
        } ?: throw Exception("Invalid activity.")

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

        Log.d("MUL_CHOICE_CHECKY_BOI", "ON_VIEW_CREATED")

        pageView.pages.observe(this, Observer {
            if (it != null && it!!.isNotEmpty()){
                if (it!![position].post != null) {
                    Log.d("MUL_CHOICE_CHECKY_BOI", "POST")
                    post = it!![position].post!!
                    initialiseMulChoiceList(it!![position])
                }
            }
        })

        pageView.uiMessage.observe(this, Observer {
            when (it!!.data) {
                "textinputsucces" -> {
                    progressBar_multipleChoice.visibility = View.INVISIBLE
                    fragment_mulchoice_btn.isEnabled = false
                    it.data = "succes"
                }
                "textinputerror" -> {
                    progressBar_multipleChoice.visibility = View.INVISIBLE
                    fragment_mulchoice_btn.isEnabled = true
                    Toast.makeText(activity as MainActivity, "Er is iets misgelopen", Toast.LENGTH_SHORT).show()
                }
            }
        })
        fragment_mulchoice_btn.isEnabled = true
        fragment_mulchoice_btn.onClick {
            pageView.pageError.postValue(Model.errorMessage(null, "Input nog niet klaar."))
        }

        if (pageView.pages.value?.isNotEmpty()!!) {
            if (pageView.pages.value!![position].post == null){
                Log.d("MUL_CHOICE_CHECKY_BOI", "POST_IS_NULL")
                pageView.checkInputPage(page._id, position)
            } else {
                Log.d("MUL_CHOICE_CHECKY_BOI", "POST")
                initialiseMulChoiceList(pageView.pages.value!![position])
            }
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
            fragment_text_multiplechoice_desc.text = page.description

            fragment_mulchoice_btn.onClick {
                progressBar_multipleChoice.visibility = View.VISIBLE
                post = pageView.updatePost(position, page, fragment_mulchoice_input_txf.text.toString(), post, (rv_mulchoice.adapter as MultipleChoiceItemAdapter).getMulChoiceItems())
            }
        }
    }
}
