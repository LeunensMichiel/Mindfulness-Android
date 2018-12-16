package com.hogent.mindfulness.domain.ViewModels

import android.arch.lifecycle.MutableLiveData
import android.graphics.Bitmap
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
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

class PageViewModel : InjectedViewModel() {

    var pages = MutableLiveData<Array<Model.Page>>()
    var pageError = MutableLiveData<Model.errorMessage>()
    var uiMessage = MutableLiveData<Model.uiMessage>()
    var pageCopies = MutableLiveData<Array<Model.Page>>()

    lateinit var exercise_id: String
    private lateinit var subscription: Disposable
    lateinit var ex_name: String
    lateinit var session_name: String

    var bitHashMap: HashMap<String, Bitmap> = hashMapOf()

    @Inject
    lateinit var userRepo: UserRepository

    @Inject
    lateinit var pageService: PageApiService

    @Inject
    lateinit var postService: PostApiService

    @Inject
    lateinit var fileService: FIleApiService

    fun retrievePages() {
        pageCopies.postValue(arrayOf())
        pages.postValue(arrayOf())
        if (::exercise_id.isInitialized) {
            subscription = pageService.getPages(exercise_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        kotlin.run {
                            pages.postValue(result)
                            pageCopies.postValue(result)
                        }
                    }
                    ,
                    { error -> pageError.postValue(Model.errorMessage("", "Fout bij het ophalen van de pagina's")) }
                )
        }
    }

    fun retrieveAudio(position: Int) {
        pageError.value = Model.errorMessage()
        subscription = fileService.getFile("page_audio", pages.value!![position].audioFilename)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> convertByteArrayToAudio(result, position) },
                { error -> pageError.value?.error = "Audio niet opgeladen." }
            )
    }

    private fun convertByteArrayToAudio(result: ResponseBody, position: Int) {
        var audioFile: File
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

    fun retrieveTextPageImg(imageFilename: String) {
        subscription = fileService.getFile("paragraphs_image", imageFilename)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    convertToBitmap(result, imageFilename)
                },
                { error -> Log.i("EXERCISE ERROR", "$error") }
            )
    }

    private fun convertToBitmap(result: ResponseBody, fileName: String) {
        var imgFile = File.createTempFile(fileName, "png")
        val fos = FileOutputStream(imgFile)
        fos.write(result.bytes())
        bitHashMap.put(fileName, BitmapFactory.decodeFile(imgFile.absolutePath))
        pages.postValue(pages.value)
        imgFile.delete()
    }

    fun checkInputPage(id: String, position: Int) {
        pageError.postValue(Model.errorMessage())
        subscription = postService.checkPageId(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    pages.value!![position].post = result
                    pages.value = pages.value
                },
                { error -> pageError.postValue(Model.errorMessage("", "Fout bij het ophalen van geschiedenis.")) }
            )
    }


    fun updatePost(
        position: Int,
        page: Model.Page,
        description: String,
        newPost: Model.Post,
        mulChoiceList: Array<Model.MultipleChoiceItem> = arrayOf()
    ): Model.Post {
        var currentPost = Model.Post()
        currentPost.page_id = page._id
        currentPost.page_name = page.title
        currentPost.user_id = userRepo.user.value?._id
        currentPost._id = newPost._id

        if (mulChoiceList.isNotEmpty()) {
            currentPost.multiple_choice_items = mulChoiceList
        } else {
            currentPost.inhoud = description
        }

        if (currentPost._id == "none" || currentPost._id == null) {
            currentPost._id = null
            subscription = postService.addPost(currentPost)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        onPostSaveResult(position, result)
                        uiMessage.postValue(Model.uiMessage("textinputsucces"))
                    },
                    { error ->
                        pageError.value?.error = "Input niet opgeslagen."
                        uiMessage.postValue(Model.uiMessage("textinputerror"))
                    }
                )
        } else {
            subscription = postService.changePost(currentPost)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        Log.i("oldPost", "$result")
                        uiMessage.postValue(Model.uiMessage("textinputsucces"))
                    },
                    { error ->
                        pageError.value?.error = "Input niet opgeslagen."
                        uiMessage.postValue(Model.uiMessage("textinputerror"))
                    }
                )
        }
        return currentPost
    }

    fun onPostSaveResult(position: Int, post: Model.Post) {
        pages.value!![position].post = post
        pages.postValue(pages.value)
        pageError.value?.error = "Input opgeslagen."
    }

    fun updatePostImage(file: File, position: Int, page: Model.Page, newPost: Model.Post): Model.Post {
        val currentPost = Model.Post()
        currentPost.page_id = page._id
        currentPost.page_name = page.title
        currentPost.user_id = userRepo.user.value?._id
        currentPost._id = newPost._id
        pageError.postValue(Model.errorMessage())
        val reqFile = RequestBody.create(
            MediaType.parse("image/png"),
            file
        )
        val body = MultipartBody.Part.createFormData("file", file.name + ".png", reqFile)
        if (currentPost._id == "none" || currentPost._id == null) {
            currentPost._id = null
            subscription = postService.addImagePost(currentPost, body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        uiMessage.postValue(Model.uiMessage("imageinputsucces"))
                    },
                    { error ->
                        pageError.postValue(Model.errorMessage("", "Input niet opgeslagen."))
                        uiMessage.postValue(Model.uiMessage("imageinputerror"))
                    }
                )
        } else {
            subscription = postService.changeImagePost(currentPost._id!!, body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        Log.i("POST_RESULT", "$result")
                        uiMessage.postValue(Model.uiMessage("imageinputsucces"))
                    },
                    { error ->
                        Log.d("POST_ERROR_IMAGE", "${error.printStackTrace()}")
                        uiMessage.postValue(Model.uiMessage("imageinputerror"))
                    }
                )
        }
        return currentPost
    }

    fun retrievePostImg(post: Model.Post, position: Int) {
        subscription = fileService.getFile("post_image", post.image_file_name!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> convertPostBitmap(result, post.image_file_name!!, position) },
                { error -> Log.i("EXERCISE ERROR", "$error") }
            )
    }

    fun convertPostBitmap(result: ResponseBody, fileName: String, position: Int) {
        var imgFile = File.createTempFile(fileName, "png")
        imgFile.deleteOnExit()
        val fos = FileOutputStream(imgFile)
        fos.write(result.bytes())
        pages.value!![position].post?.bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
        pages.postValue(pages.value)
    }
}