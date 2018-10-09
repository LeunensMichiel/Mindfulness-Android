package com.hogent.mindfulness

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var disposable: Disposable? = null

    val wikiApiService by lazy {
        WikiApiService.create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_send.setOnClickListener {
            if (edit_search.text.toString().isNotEmpty()) {
                beginSendMessage(edit_search.text.toString())
            }


        }

        btn_get.setOnClickListener {
            beginRetrieveMessage()
        }
    }

    private fun beginSendMessage(message: String) {
        disposable = wikiApiService.sendResponse(Model.Response(message))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result -> showResult(result.test) },
                        { error -> showError(error.message) }
                )
    }

    private fun beginRetrieveMessage() {
        disposable = wikiApiService.getResult()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result -> showResult(result.test) },
                        { error -> showError(error.message) }
                )
    }


    private fun showResult(result: String) {
        tvResult.text = result
    }

    private fun showError(errorMsg: String?) {
        Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }
}
