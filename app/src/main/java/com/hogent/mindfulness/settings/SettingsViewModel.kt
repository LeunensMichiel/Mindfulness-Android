package com.hogent.mindfulness.settings

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.hogent.mindfulness.data.LocalDatabase.MindfulnessDBHelper
import com.hogent.mindfulness.domain.Model

class SettingsViewModel(application: Application): AndroidViewModel(Application()) {

    private var  mMindfullDB:MindfulnessDBHelper = MindfulnessDBHelper(application)
    var user: MutableLiveData<Model.User>? = null

    init {
        user?.value = mMindfullDB.getUser()!!
        user?.value?.group = mMindfullDB.getGroup()
    }

//    private fun loadUser( value: Model.User?) {
//        Log.i("VIEWMODEL_LOADDATA","START")
//        if (value == null) {
//            user?.value =
//        }
//    }
}