package com.hogent.mindfulness.oefeningdetails

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.widget.Toast
import com.hogent.mindfulness.MainActivity
import com.hogent.mindfulness.R
import com.hogent.mindfulness.data.MindfulnessApiService
import com.hogent.mindfulness.domain.Model
import com.hogent.mindfulness.show_sessions.SessionAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_oefeningdetail.*

class OefeningDetailsActivity : AppCompatActivity() {

    private lateinit var disposable: Disposable

    private val mindfulnessApiService by lazy {
        MindfulnessApiService.create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intentThatStartedThisActivity = getIntent()
        if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
            val exercise_id = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT)

            beginRetrieveExercise(exercise_id)

        } else {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    class MyViewPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {
        private var fragmentList: MutableList<Fragment> = ArrayList()
        private var titles: MutableList<String> = ArrayList()

        override fun getItem(position: Int): Fragment {
            return fragmentList[position]
        }

        override fun getCount(): Int {
            return fragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            fragmentList.add(fragment)
            titles.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? = titles.get(position)
    }

    private fun beginRetrieveExercise(exerciseId: String) {
        disposable = mindfulnessApiService.getPages(exerciseId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> showResult(result) },
                { error -> showError(error.message) }
            )
    }

    private fun showResult(pages: Array<Model.Page>) {


        setContentView(R.layout.activity_oefeningdetail)

        val adapter = MyViewPagerAdapter(supportFragmentManager)

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

    private fun showError(errMsg: String?) {
        Toast.makeText(this, errMsg, Toast.LENGTH_SHORT).show()
    }
}
