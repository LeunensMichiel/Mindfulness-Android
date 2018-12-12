package com.hogent.mindfulness.domain.ViewModels

import android.arch.lifecycle.MutableLiveData
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.util.Log
import com.hogent.mindfulness.data.FIleApiService
import com.hogent.mindfulness.data.LocalDatabase.repository.UserRepository
import com.hogent.mindfulness.data.PageApiService
import com.hogent.mindfulness.data.PostApiService
import com.hogent.mindfulness.domain.InjectedViewModel
import com.hogent.mindfulness.domain.Model
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
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
    lateinit var ex_name:String
    lateinit var session_name:String

    @Inject
    lateinit var userRepo: UserRepository

    @Inject
    lateinit var pageService:PageApiService

    @Inject
    lateinit var postService:PostApiService

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
                        Log.d("PAGE_RESULT", "FUCK")
                    }
                    ,
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
            Log.d("PAGE_VIEW", "REPEAT_1")
        }
    }

    fun retrieveTextPageImg(paragragphs: Array<Model.Paragraph>){
        paragragphs
            .filter { it.type == "IMAGE" }
            .forEach {
                Log.i("IMAGE_FILE_PATH", it.imageFilename)
                subscription = fileService.getFile("paragraphs_image", it.imageFilename)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { result -> convertToBitmap(paragragphs, result, it.imageFilename, it.position) },
                        { error -> Log.i("EXERCISE ERROR", "$error") }
                    )
            }
    }

    private fun convertToBitmap(paragragphs: Array<Model.Paragraph>, result: ResponseBody, fileName: String, position: Int) {
        var imgFile = File.createTempFile(fileName, "png")
        imgFile.deleteOnExit()
        val fos = FileOutputStream(imgFile)
        fos.write(result.bytes())
        paragragphs[position].bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
    }

    fun checkInputPage(id: String, position: Int){
        subscription = postService.checkPageId(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    pages.value!![position].post = result
                    pages.value = pages.value
                    Log.d("PAGE_VIEW", "REPEAT_2")
                },
                { error -> Log.i("fuck", "fuck") }
            )
    }


    fun updatePost(position: Int, page:Model.Page, description:String, newPost:Model.Post):Model.Post {
        var currentPost = Model.Post()
        currentPost.page_id = page._id
        currentPost.page_name = page.title
        currentPost.inhoud = description
        currentPost.user_id = userRepo.user.value?._id
        currentPost._id = newPost._id
        if (currentPost._id == "none" || currentPost._id == null){
            currentPost._id = null
            subscription = postService.addPost(currentPost)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->  onPostSaveResult(position, result) },
                    { error ->  pageError.value?.error = "Input niet opgeslagen." }
                )
        } else {
            subscription = postService.changePost(currentPost)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result -> Log.i("oldPost", "$result") },
                    { error -> Log.i("ERRORCHECK",error.message) }
                )
        }
        return currentPost
    }

    fun onPostSaveResult(position: Int, post:Model.Post) {
        pages.value!![position].post = post
        pages.postValue(pages.value)
        Log.d("PAGE_VIEW", "REPEAT_3")
        pageError.value?.error = "Input opgeslagen."
    }
}