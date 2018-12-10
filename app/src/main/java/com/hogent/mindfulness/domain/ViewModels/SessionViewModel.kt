package com.hogent.mindfulness.domain.ViewModels

import android.arch.lifecycle.MutableLiveData
import android.util.Log
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
    private lateinit var subscribe: Disposable

    @Inject
    lateinit var sessionService: SessionApiService

    @Inject
    lateinit var userRepo: UserRepository

    init {
        Log.d("SESSION_VM", "WTF")
    }

    fun setSelectedSession(session:Model.Session){
        Log.d("SET_SELECTED_SESSION", "$session")
        selectedSession.postValue(session)
    }

    fun retrieveSessions(){
        subscribe = sessionService.getSessions(userRepo.user.value?.group?.sessionmap_id!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> sessionsResult(result)
                },
                { error -> sessionError(error) }
            )
    }

    private fun sessionsResult(sessions: Array<Model.Session>) {
        sessions.forEach {
            it.unlocked = (userRepo.user.value?.unlocked_sessions!!.contains(it._id))
            Log.d("SESSION", "$it")
        }
        sessionList.postValue(sessions)
    }

    private fun sessionError(error: Throwable) {
        Log.d("SESSION_ERR", "$error")
    }

}