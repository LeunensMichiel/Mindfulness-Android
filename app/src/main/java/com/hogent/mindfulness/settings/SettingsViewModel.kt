package com.hogent.mindfulness.settings

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.hogent.mindfulness.domain.Model

class SettingsViewModel(): ViewModel() {

//    private var  mMindfullDB:MindfulnessDBHelper = MindfulnessDBHelper(application)
    lateinit var user: MutableLiveData<Model.User>

//    init {
//        user?.value = mMindfullDB.getUser()!!
//        user?.value?.group = mMindfullDB.getGroup()
//    }



    fun loadUser(currentuser: Model.User) {
        if (!::user.isInitialized) {
            user = MutableLiveData()
        }
        user.value = currentuser
    }
}