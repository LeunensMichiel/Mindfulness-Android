package com.hogent.mindfulness.domain.ViewModels

import android.arch.lifecycle.MutableLiveData
import android.media.MediaPlayer
import android.util.Log
import com.hogent.mindfulness.data.FIleApiService
import com.hogent.mindfulness.data.PageApiService
import com.hogent.mindfulness.domain.InjectedViewModel
import com.hogent.mindfulness.domain.Model
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import org.jetbrains.anko.support.v4.toast
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

class PageViewModel:InjectedViewModel() {

    var pages = MutableLiveData<Array<Model.Page>>()
    var pageError = MutableLiveData<Model.errorMessage>()

    lateinit var exercise_id:String
    private lateinit var subscription: Disposable

    @Inject
    lateinit var pageService:PageApiService

    @Inject
    lateinit var fileService: FIleApiService

    fun retrievePages(){
        if (::exercise_id.isInitialized){
            subscription = pageService.getPages(exercise_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        pages.value = result
                    },
                    { error -> Log.d("PAGE_ERR", "$error") }
                )
        }
    }

    fun retrieveAudio(position: Int){
        pageError.value = Model.errorMessage()
        subscription = fileService.getFile("page_audio", pages.value!![position].audioFilename)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> convertByteArrayToAudio(result, position) },
                { error -> pageError.value?.error = "Audio niet opgeladen."}
            )
    }

    private fun convertByteArrayToAudio(result: ResponseBody, position: Int) {
        var audioFile:File
        var fos: FileOutputStream
        var fis: FileInputStream
        try {
            audioFile = File.createTempFile(pages.value!![position].audioFilename, "aac")
            audioFile.deleteOnExit()
            fos = FileOutputStream(audioFile)
            fos.write(result.bytes())
            fos.close()
            fis = FileInputStream(audioFile)
            prepareMediaPlayer(fis, position)
        } catch (ex: IOException) {
            pageError.value?.error = "Audio niet opgeladen."
        }
    }

    private fun prepareMediaPlayer(fis: FileInputStream, position: Int) {
        var mp = MediaPlayer()
        mp.reset()
        mp.setDataSource(fis.fd)
        mp.prepareAsync()
        mp.setOnPreparedListener { mp ->
            pages.value!![position].mediaPlayer = mp
            pages.value = pages.value
        }
    }
}