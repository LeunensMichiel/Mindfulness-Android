package com.hogent.mindfulness.domain.ViewModels

import android.arch.lifecycle.MutableLiveData
import android.graphics.BitmapFactory
import android.util.Log
import com.hogent.mindfulness.data.FIleApiService
import com.hogent.mindfulness.data.LocalDatabase.repository.UserRepository
import com.hogent.mindfulness.data.PostApiService
import com.hogent.mindfulness.domain.InjectedViewModel
import com.hogent.mindfulness.domain.Model
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class PostViewModel : InjectedViewModel() {

    var posts = MutableLiveData<Array<Model.Post>>()
    var error = MutableLiveData<Model.errorMessage>()
    private lateinit var subscription: Disposable

    @Inject
    lateinit var userRepo: UserRepository

    @Inject
    lateinit var postService: PostApiService

    @Inject
    lateinit var fileService: FIleApiService

    fun setPosts(newPosts: Array<Model.Post>) {
        posts.postValue(newPosts)
    }

    fun retrievePosts() {
        error.postValue(Model.errorMessage())
        subscription = postService.getPosts(userRepo.user.value?._id!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    Log.d("POSTIE_WOSTIES", "POST_RETRIEVAL")
                    result.forEach {
                        Log.d("POSTIE_WOSTIES", "$it")
                    }
                    posts.postValue(result)
                    retrieveImages(result)
                },
                { error ->
                    Log.d("POSTIE_WOSTIES", "${error.message}")
                    Log.d("POSTIE_WOSTIES", "${error.printStackTrace()}")
                    this.error.value?.error = "Feed niet bereikbaar."
                }
            )
    }

    fun retrieveImages(newPosts: Array<Model.Post>) {
        error.postValue(Model.errorMessage())
        newPosts.forEachIndexed { index, it ->
            if (it.image_file_name != null) {
                subscription = fileService.getFile("post_image", it.image_file_name!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { result -> convertToBitmap(result, it.image_file_name!!, index) },
                        { error -> this.error.value?.error = "afbeeldingen van feed niet bereikbaar." }
                    )
            }
        }
    }

    private fun convertToBitmap(result: ResponseBody, fileName: String, position: Int) {
        var imgFile = File.createTempFile(fileName, "png")
        imgFile.deleteOnExit()
        val fos = FileOutputStream(imgFile)
        fos.write(result.bytes())
        posts.value!![position].bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
        posts.postValue(posts.value!!)
    }
}