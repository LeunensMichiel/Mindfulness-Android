package com.hogent.mindfulness.domain.ViewModels

import android.arch.lifecycle.MutableLiveData
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
    var postError = MutableLiveData<String>()
    private lateinit var subscription: Disposable
    var bitHashMap: HashMap<String, Bitmap> = HashMap()

    @Inject
    lateinit var userRepo: UserRepository

    @Inject
    lateinit var postService: PostApiService

    @Inject
    lateinit var fileService: FIleApiService

    fun setPosts(newPosts: Array<Model.Post>) {
        posts.postValue(newPosts)
    }


    /**
     * Hier worden alle posts opgehaald. Deze methode word opgeroepen in de postfragment.
     * Bij succes word de posts geupdate en word de observer in postfragment getriggered.
     * De retrieveimages functie word ook opgeroepen bij succes. Bij error word de waarde
     * van posterror gezet. De observer in mainacvtity word getriggered en toont een toast
     */
    fun retrievePosts() {
        posts.postValue(null)
        postError.postValue(null)
        subscription = postService.getPosts(userRepo.user.value?._id!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    posts.postValue(result)
                    retrieveImages(result)
                },
                { error ->
                    this.postError.postValue("Geschiedenis is niet beschikbaar")
                }
            )
    }

    /**
     * Voor elke post die een image bevat en die nog niet in de hashmap staat word de image opgehaald. Bij succes
     * word de opgehaalde ResponseBody opgehaald van de backend. Bij een error word ene toast getoond adhv een observer
     * in de mainactivity.
     */
    fun retrieveImages(newPosts: Array<Model.Post>) {
        postError.postValue(null)
        newPosts.forEachIndexed { index, it ->
            if (it.image_file_name != null && !bitHashMap.containsKey(it.image_file_name!!)) {
                subscription = fileService.getFile("post_image", it.image_file_name!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { result -> convertToBitmap(result, it.image_file_name!!) },
                        { error ->
                            this.postError.postValue("De afbeeldingen van de geschiedenis zijn niet beschikbaar")
                        }
                    )
            }
        }
    }

    /**
     * ResponseBody word omgezet naar een bitmap die word opgeslagen inde hashmap te samen met de imagefilename als key.
     */
    private fun convertToBitmap(result: ResponseBody, fileName: String) {
        var imgFile = File.createTempFile(fileName, "png")
        imgFile.deleteOnExit()
        val fos = FileOutputStream(imgFile)
        fos.write(result.bytes())
        //posts.value!![position].bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
        bitHashMap.put(fileName, BitmapFactory.decodeFile(imgFile.absolutePath))
        posts.postValue(posts.value!!)
    }
}