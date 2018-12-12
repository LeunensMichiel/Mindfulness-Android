package com.hogent.mindfulness.domain.ViewModels

import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.hogent.mindfulness.data.ExerciseApiService
import com.hogent.mindfulness.domain.InjectedViewModel
import com.hogent.mindfulness.domain.Model
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ExerciseViewModel:InjectedViewModel() {

    var exercises = MutableLiveData<Array<Model.Exercise>>()
    var selectedExercise = MutableLiveData<Model.Exercise>()

    private lateinit var subscription:Disposable
    lateinit var session_id:String

    @Inject
    lateinit var exerciseService:ExerciseApiService

    fun retrieveExercises(){
        if (::session_id.isInitialized){
            subscription = exerciseService.getExercises(session_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result -> exercises.value = result},
                    { error -> Log.d("EX_RETRIEVAL_ERR", "$error") }
                )
        }
    }
}