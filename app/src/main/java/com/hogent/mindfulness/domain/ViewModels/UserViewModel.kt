package com.hogent.mindfulness.domain.ViewModels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.hogent.mindfulness.data.API.UserApiService
import com.hogent.mindfulness.data.LocalDatabase.repository.UserRepository
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
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { user ->
                    onRetrieveUserSucces(user)
                    uiMessage.postValue(Model.uiMessage("login_end_progress"))
                },
                { error ->
                    uiMessage.postValue(Model.uiMessage("login_end_progress"))
                    errorMessage.postValue(Model.loginErrorMessage("login_api_fail"))
                    toastMessage.value = "Login gefaald."
                    toastMessage.value = null
                }
            )
    }
    
    fun register(registerDetails: Model.Register){
        uiMessage.postValue(Model.uiMessage("registere_start_progress"))
        subscription = userApi.register(registerDetails)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { user ->
                    rawUser.value = user
                     userRepo.insert(user!!)
                    uiMessage.postValue(Model.uiMessage("registere_end_progress"))
                },
                { error ->
                    toastMessage.value = "Registreren gefaald."
                    uiMessage.postValue(Model.uiMessage("registere_end_progress"))
                }
            )
    }

    fun addGroup(group: Model.user_group){
        toastMessage.postValue(null)
        subscription = userApi.updateUserGroup(userRepo.user.value?._id!!, group)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    userRepo.user.value?.group = result
                    userRepo.updateUser(userRepo.user.value!!)
                },
                { error ->
                    toastMessage.postValue("Code niet herkend.")
                }
            )
    }

    fun unlockSession(unlockedSession: Model.unlock_session){
        unlockedSession.id = userRepo.user.value?._id!!
        subscription = userApi.updateUser(unlockedSession)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    userRepo.user.value?.unlocked_sessions!!.add(unlockedSession.session_id)
                    userRepo.updateUser(userRepo.user.value!!)
                },
                { error ->
                    toastMessage.postValue("Code niet herkend.")
                }
            )
    }

    fun updateUserGroep(group: String) {
        val groupuser = Model.user_group(group)
        subscription = userApi.updateUserGroup(userRepo.user.value!!._id!!, groupuser)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> Log.d("USERGROUP_RESULT", "$result") },
                { error -> Log.d("USERGROUP_ERROR", "$error") }
            )
    }

    fun updateFeedback(feedback: Boolean){
        userRepo.user.value?.feedbackSubscribed = feedback
        dbUser.value?.feedbackSubscribed = feedback
        subscription = userApi.updateUserFeedback(userRepo.user.value!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> Log.d("FEEDBACK_RESULT", "$result") },
                { error -> Log.d("FEEDBACK_ERROR", "$error") }
            )
    }

    fun updateProfilePicture(imageFileName : String) {
        userRepo.user.value?.image_file_name = imageFileName
        subscription = userApi.updateUserProfilePicture(userRepo.user.value!!._id!!, userRepo.user.value!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> Log.d("PROFILE_PICTURE_RESULT", "$result") },
                { error -> Log.d("PROFILE_PICTURE_ERROR", "$error") }
            )
    }

    private fun onRetrieveUserSucces(user: Model.User?) {
        rawUser.value = user
        userRepo.insert(user!!)
    }
}