//package com.hogent.mindfulness.data
//
//import android.content.res.Resources
//import android.provider.Settings.Global.getString
//import io.reactivex.android.schedulers.AndroidSchedulers
//import io.reactivex.disposables.Disposable
//import io.reactivex.schedulers.Schedulers
//import com.hogent.mindfulness.R
//
//class dataService {
//
//    private lateinit var disposable: Disposable
//
//    private val mindfulnessApiService by lazy {
//        MindfulnessApiService.create()
//    }
//
//    fun beginRetrieveExercises(session_id: String) {
//        disposable = mindfulnessApiService.getExercises(session_id)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(
//                { result -> showResultExercises(result) },
//                { error -> showError(error.message) }
//            )
//    }
//
//    private fun beginRetrieveSessionmap() {
//        disposable = mindfulnessApiService.getSessionmap(Resources.getSystem().getString(R.string.sessionmap_id))
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(
//                { result -> showResult(result) },
//                { error -> showError(error.message) }
//            )
//    }
//
//    interface AccessApiCalls<T> {
//        fun retrievedData(result: T)
//    }
//
//
//}