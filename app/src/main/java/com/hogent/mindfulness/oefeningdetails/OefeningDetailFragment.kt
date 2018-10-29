package com.hogent.mindfulness.oefeningdetails

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.hogent.mindfulness.R
import com.hogent.mindfulness.data.MindfulnessApiService
import com.hogent.mindfulness.domain.Model
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.oefening_details_fragment.*

class OefeningDetailFragment(): Fragment(){

    lateinit var exerciseId:String
    lateinit var manager: FragmentManager
    private lateinit var disposable: Disposable
    private val mindfulnessApiService by lazy {
        MindfulnessApiService.create()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        beginRetrieveExercise(exerciseId)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.oefening_details_fragment, container, false)
    }
    

    private fun beginRetrieveExercise(exerciseId: String) {
        disposable = mindfulnessApiService.getPages(exerciseId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> showResultExercise(result) },
                { error -> showError(error.message) }
            )
    }

    private fun showError(errMsg: String?) {
        Toast.makeText(activity, errMsg, Toast.LENGTH_SHORT).show()
    }

    private fun showResultExercise(pages: Array<Model.Page>) {
        val adapter = OefeningViewPagerAdapter(manager)

        pages.forEach {
            Log.d("page", it.description)
            when (it.type) {
                "AUDIO" -> adapter.addFragment(FragmentOefeningAudio(), "Audio")
                "TEXT" -> {
                    val fragment = FragmentOefeningText()
                    val arg = Bundle()
                    arg.putString("description", it.description)
                    fragment.arguments = arg
                    adapter.addFragment(fragment, "Beschrijving")
                }
                "INPUT" -> adapter.addFragment(FragmentOefeningInvoer(), "Invoer")

            }
        }

        viewPager.adapter = adapter

    }
}