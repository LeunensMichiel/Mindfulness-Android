package com.hogent.mindfulness.domain.ViewModels

import android.arch.lifecycle.MutableLiveData
import com.hogent.mindfulness.domain.InjectedViewModel

class StateViewModel:InjectedViewModel() {

    var viewState = MutableLiveData<String>()
    var dialogState = MutableLiveData<String>()

}