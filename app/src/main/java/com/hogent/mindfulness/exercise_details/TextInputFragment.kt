package com.hogent.mindfulness.exercise_details


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.hogent.mindfulness.MainActivity
import com.hogent.mindfulness.R
import com.hogent.mindfulness.domain.Model
import com.hogent.mindfulness.domain.ViewModels.PageViewModel
import kotlinx.android.synthetic.main.fragment_text_input.*
import org.jetbrains.anko.sdk27.coroutines.onClick

/**
 * A simple [Fragment] subclass.
 *
 */
class TextInputFragment : PagerFragment() {

    private lateinit var pageView: PageViewModel
    private var post: Model.Post? = null
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
        return inflater.inflate(R.layout.fragment_text_input, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pageView.uiMessage.observe(this, Observer {
            when (it!!.data) {
                "textinputsucces" -> {
                    progressBar_textInput.visibility = View.GONE
                    fragment_textinput_btn.isEnabled = false
                    it.data = "changed"
                }
                "textinputerror" -> {
                    progressBar_textInput.visibility = View.GONE
                    fragment_textinput_btn.isEnabled = true
                    Toast.makeText(activity as MainActivity, "Er is iets misgelopen", Toast.LENGTH_SHORT).show()
                }
            }
        })

        pageView.pages.observe(this, Observer {
            Log.d("PAGE_INIT_CHECK_SIZE", "${it?.size}")
            Log.d("PAGE_INIT_CHECK_POS", "$position")
            if (pageView.pages.value?.isNotEmpty()!!) {
                Log.d("PAGE_INIT_CHECK_EMPT", "NOT_EMPTY")
                if(pageView.pages.value!![position].post != null){
                    post = pageView.pages.value!![position].post!!
                    setupTextInput()
                }
            }
        })

        if(pageView.pages.value?.isNotEmpty()!! && pageView.pages.value!![position].post != null){
            post = pageView.pages.value!![position].post!!
            setupTextInput()
        } else {
            fragment_textinput_titel.setText("Geschiedenis aan het nakijken...")

            fragment_textinput_btn.onClick {
                pageView.pageError.postValue(Model.errorMessage(null, "Input nog niet klaar."))
            }

            pageView.checkInputPage(page._id, position)
        }
    }

    fun setupTextInput() {
        if (post != null)
            fragment_textinput_textfield.hint = page.description
        fragment_textinput_titel.setText(page.title)
        fragment_text_input_desc.text = page.description

        fragment_textinput_btn.onClick {
            progressBar_textInput.visibility = View.VISIBLE
            post = pageView.updatePost(position, page, fragment_textinput_textfield.text.toString(), post!!)
        }
    }

}
