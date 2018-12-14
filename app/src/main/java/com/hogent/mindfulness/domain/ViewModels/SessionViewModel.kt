package com.hogent.mindfulness.domain.ViewModels

import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.hogent.mindfulness.data.FeedbackApiService
import com.hogent.mindfulness.data.LocalDatabase.repository.UserRepository
import com.hogent.mindfulness.data.SessionApiService
import com.hogent.mindfulness.domain.InjectedViewModel
import com.hogent.mindfulness.domain.Model
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class SessionViewModel:InjectedViewModel() {

    var sessionList = MutableLiveData<Array<Model.Session>>()
    var selectedSession = MutableLiveData<Model.Session>()
    var sessionToast = MutableLiveData<String>()

    private lateinit var subscribe: Disposable

    @Inject
    lateinit var sessionService: SessionApiService

    @Inject
    lateinit var feedbackService:FeedbackApiService

    @Inject
    lateinit var userRepo: UserRepository

    init {
        Log.d("SESSION_VM", "WTF")
    }

    fun retrieveSessions(){
        Log.d("SESSION_VM", "API")
        subscribe = sessionService.getSessions(userRepo.user.value?.group?.sessionmap_id!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> sessionsResult(result) },
                { error -> sessionError(error) }
            )
    }

    fun resetunlockedSession() {
        Log.d("SESSION_VM", "RESET")
        if (sessionList.value != null)
            sessionsResult(sessionList.value!!)
    }

    private fun sessionsResult(sessions: Array<Model.Session>) {
        sessions.forEach {
            it.unlocked = (userRepo.user.value?.unlocked_sessions!!.contains(it._id))
            Log.d("SESSION_VM", "$it")
        }
        sessionList.postValue(sessions)
    }

    private fun sessionError(error: Throwable) {
        Log.d("SESSION_ERR", "$error")
    }

    fun saveFeedBack(feedback: Model.Feedback){
        feedback.session = selectedSession.value!!._id
        subscribe = feedbackService.addFeedback(feedback)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    Log.d("FEEDBACK_RESULT", "${result}")
                    sessionToast?.value = "Feedback opgeslagen."
                },
                { error ->
                    Log.d("FEEDBACK_ERROR", "${error}")
                    sessionToast?.value = "Er ging iets mis. Feedback is niet opgeslagen."
                }
            )
    }
}