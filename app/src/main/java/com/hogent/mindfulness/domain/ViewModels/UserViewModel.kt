package com.hogent.mindfulness.domain.ViewModels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.hogent.mindfulness.data.LocalDatabase.repository.UserRepository
import com.hogent.mindfulness.data.API.UserApiService
import com.hogent.mindfulness.domain.InjectedViewModel
import com.hogent.mindfulness.domain.Model
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class UserViewModel : InjectedViewModel() {

    var rawUser = MutableLiveData<Model.User>()
    var uiMessage = MutableLiveData<Model.uiMessage>()
    var errorMessage = MutableLiveData<Model.loginErrorMessage>()
    var toastMessage = MutableLiveData<String>()
    var dbUser: LiveData<Model.User> = userRepo.user

    @Inject
    lateinit var userApi: UserApiService

    @Inject
    lateinit var userRepo: UserRepository

    private lateinit var subscription: Disposable

    init {
        errorMessage.value = Model.loginErrorMessage()
    }

    fun login(loginDetails: Model.Login) {

        uiMessage.value = Model.uiMessage("login_start_progress")
        subscription = userApi.login(loginDetails)
            .doOnTerminate {
                uiMessage.postValue(Model.uiMessage("login_end_progress"))
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { user ->
                    Log.i("user", "$user")
                    onRetrieveUserSucces(user)
                },
                { error ->
                    Log.d("USER_ERROR", "${error}")
                    errorMessage.postValue(Model.loginErrorMessage("login_api_fail"))
                    toastMessage.value = "Login gefaald."
                    toastMessage.value = null
                }
            )
    }
    
    fun register(){

    }

    fun updateFeedback(){
        userRepo.user.value?.feedbackSubscribed = false
        subscription = userApi.updateUserFeedback(userRepo.user.value!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> Log.d("FEEDBACK_RESULT", "$result") },
                { error -> Log.d("FEEDBACK_ERROR", "$error") }
            )
    }

    private fun onRetrieveUserSucces(user: Model.User?) {
        rawUser.value = user
        userRepo.insert(user!!)
    }
}