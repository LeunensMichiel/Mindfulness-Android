package com.hogent.mindfulness.domain.ViewModels

import android.arch.lifecycle.MutableLiveData
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.hogent.mindfulness.data.ExerciseApiService
import com.hogent.mindfulness.data.FIleApiService
import com.hogent.mindfulness.domain.InjectedViewModel
import com.hogent.mindfulness.domain.Model
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

class ExerciseViewModel:InjectedViewModel() {

    var exercises = MutableLiveData<Array<Model.Exercise>>()
    var selectedExercise = MutableLiveData<Model.Exercise>()
    var toastMessage = MutableLiveData<String?>()
    var sessionBitmap = MutableLiveData<Bitmap>()

    private lateinit var subscription:Disposable
    lateinit var session_id:String
    lateinit var session_image_filename:String

    @Inject
    lateinit var exerciseService:ExerciseApiService

    @Inject
    lateinit var fileService: FIleApiService

    fun retrieveExercises(){
        sessionBitmap.postValue(null)
        if (::session_id.isInitialized){
            subscription = exerciseService.getExercises(session_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        exercises.value = result
                        //retrieveExerciseImg()
                    },
                    { error -> Log.d("EX_RETRIEVAL_ERR", "$error") }
                )
        }
    }

    fun retrieveExerciseImg(sessionImage: String){
        subscription = fileService.getFile("session_image", sessionImage)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> convertToBitmap(result) },
                { error -> setToast("Er ging iets mis. De afbeelding van de oefeningen werden niet opgehaald.") }
            )
    }

    private fun convertToBitmap(result: ResponseBody) {
        var imgFile: File
        var fos: FileOutputStream
        var bMap:Bitmap
        try {
            imgFile = File.createTempFile(session_image_filename, "jpg")
            imgFile.deleteOnExit()
            fos = FileOutputStream(imgFile)
            fos.write(result.bytes())
            fos.close()
            bMap = BitmapFactory.decodeFile(imgFile.absolutePath)
            Log.d("EX_IMG", "CHECK")
            sessionBitmap.postValue(bMap)
        } catch (ex: IOException) {
           setToast("Er ging iets mis. De afbeelding van de oefeningen werden niet opgehaald.")
        }
    }

    private fun setToast(message:String){
        toastMessage.postValue(null)
        toastMessage.postValue(message)
    }
}