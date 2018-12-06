package com.hogent.mindfulness.domain

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.hogent.mindfulness.data.LocalDatabase.repository.UserRepository
import com.hogent.mindfulness.data.UserApiService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class UserViewModel : InjectedViewModel() {

    val rawUser = MutableLiveData<Model.User>()
    val uiMessage = MutableLiveData<Model.uiMessage>()
    val errorMessage = MutableLiveData<Model.errorMessage>()

    @Inject
    lateinit var userApi: UserApiService

    @Inject
    lateinit var userRepo:UserRepository

    private lateinit var subscription: Disposable

    init {
        //TODO - LoadingAnimations door DoOnTerminate en DoOnSubscribe
        Log.i("USER_API", "$userApi")
    }

     fun login(loginDetails:Model.Login) {
        subscription = userApi.login(loginDetails)
            .doOnSubscribe {
                uiMessage.value!!.data = "Login_On_Subscribe"
            }
            .doOnTerminate {
                uiMessage.value!!.data = "Login_On_Terminate"
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { user ->
                    Log.i("user", "$user")
                    onRetrieveUserSucces(user)
                },
                { error ->
                    errorMessage.value!!.error = error.message!!
                    errorMessage.value!!.data = "login_api_fail"
                }
            )
    }

    private fun onRetrieveUserSucces(user: Model.User?) {
        rawUser.value = user
        userRepo.insert(user!!)
    }
}