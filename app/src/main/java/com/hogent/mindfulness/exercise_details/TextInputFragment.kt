package com.hogent.mindfulness.exercise_details


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.hogent.mindfulness.R
import com.hogent.mindfulness.domain.Model
import com.hogent.mindfulness.domain.ViewModels.PageViewModel
import kotlinx.android.synthetic.main.fragment_fragment_oefeninginvoer.*
import kotlinx.android.synthetic.main.fragment_text_input.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import java.lang.Exception

/**
 * A simple [Fragment] subclass.
 *
 */
class TextInputFragment : PagerFragment() {

    private lateinit var pageView: PageViewModel
    private var post: Model.Post? = null
    var position:Int = -1
    lateinit var page: Model.Page

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageView = activity?.run {
            ViewModelProviders.of(this).get(PageViewModel::class.java)
        }?: throw Exception("Invlaid activity.")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_text_input, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pageView.pages.observe(this, Observer {
            Log.d("PAGE_VIEW", "FUCK_OFF")
            if(pageView.pages.value!![position].post != null){
                post = pageView.pages.value!![position].post!!
                setupTextInput()
            }
        })

        fragment_textinput_titel.setText("Geschiedenis aan het nakijken...")

        fragment_textinput_btn.onClick {
            pageView.pageError.postValue(Model.errorMessage(null, "Input nog niet klaar."))
        }

        pageView.checkInputPage(page._id, position)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        Log.d("INPUT", "WHY")
        if (isAdded()) {
            Log.d("TEXT_INPUT_POST", "${pageView.pages.value!![position].post}")
            if (isVisibleToUser && pageView.pages.value!![position].post == null){
                pageView.checkInputPage(page._id, position)
            }
        }
    }

    fun setupTextInput(){
        if (post != null)
            fragment_textinput_textfield.hint = post?.inhoud
        fragment_textinput_titel.setText(page.title)

        fragment_textinput_btn.onClick {
            post = pageView.updatePost(position, page, fragment_textinput_textfield.text.toString(), post!!)
        }
    }

}
