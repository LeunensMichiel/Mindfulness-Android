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

    lateinit var exercise_id: String
    private lateinit var subscription: Disposable
    lateinit var ex_name: String
    lateinit var session_name: String

    @Inject
    lateinit var userRepo: UserRepository

    @Inject
    lateinit var pageService: PageApiService

    @Inject
    lateinit var postService: PostApiService

    @Inject
    lateinit var fileService: FIleApiService

    fun retrievePages() {
        if (::exercise_id.isInitialized) {
            subscription = pageService.getPages(exercise_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        pages.value = result
                        Log.d("PAGE_RESULT", "FUCK")
                    }
                    ,
                    { error -> Log.d("PAGE_ERR", error.message) }
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
            Log.d("PAGE_VIEW", "REPEAT_1")
        }
    }

    fun retrieveTextPageImg(paragragphs: Array<Model.Paragraph>, pagePosition: Int) {
        Log.d("TEXT_VIEW", "IMG_RETRIEVE_FUNCTION")
        paragragphs
            .filter { it.type == "IMAGE" }
            .forEach {
                Log.i("TEXT_VIEW", it.imageFilename)
                subscription = fileService.getFile("paragraphs_image", it.imageFilename)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { result ->
                            Log.d("TEXT_VIEW_CHANGE_BOY_2", "RESULT_CHECK")
                            convertToBitmap(paragragphs, result, it.imageFilename, it.position, pagePosition)
                        },
                        { error -> Log.i("EXERCISE ERROR", "$error") }
                    )
            }
    }

    private fun convertToBitmap(
        paragragphs: Array<Model.Paragraph>,
        result: ResponseBody,
        fileName: String,
        position: Int,
        pagePosition: Int
    ) {
        var imgFile = File.createTempFile(fileName, "png")
        imgFile.deleteOnExit()
        val fos = FileOutputStream(imgFile)
        fos.write(result.bytes())
        Log.d("TEXT_VIEW_CHANGE_BOY_3", "1")
        //paragragphs[position].bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
        pages.value!![pagePosition].paragraphs[position].bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
        Log.d("TEXT_VIEW_CHANGE_BOY_3", "2")
        Log.d("TEXT_VIEW_CHANGE_BOY_3", "${pages.value!![position].paragraphs[position].bitmap}")
        Log.d("TEXT_VIEW_CHANGE_BOY_3", "3")
        pages.postValue(pages.value)
    }

    fun checkInputPage(id: String, position: Int) {

        subscription = postService.checkPageId(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    pages.value!![position].post = result
                    pages.value = pages.value
                    Log.d("CHECKY_BOY", "RESULT_CREATED")
                },
                { error -> Log.i("fuck", "fuck") }
            )
    }


    fun updatePost(
        position: Int,
        page: Model.Page,
        description: String,
        newPost: Model.Post,
        mulChoiceList: Array<Model.MultipleChoiceItem> = arrayOf()
    ): Model.Post {
        Log.d("POSTUPDATE", "CHECK")
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

        Log.d("POSTS_SAVED", "CHECK")
        pages.value!![position].post = post
        pages.postValue(pages.value)
        Log.d("PAGE_VIEW", "REPEAT_3")
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
        Log.d("POST_IMAGE", "$reqFile")
        val body = MultipartBody.Part.createFormData("file", file.name + ".png", reqFile)
        if (currentPost._id == "none" || currentPost._id == null) {
            currentPost._id = null
            subscription = postService.addImagePost(currentPost, body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result -> Log.d("POST_IMAGE", "$result") },
                    { error -> pageError.postValue(Model.errorMessage("", "Input niet opgeslagen.")) }
                )
        } else {
            Log.d("POST_IMAGE_BODY", "$body")
            Log.d("POST_IMAGE_ID", currentPost._id!!)
            subscription = postService.changeImagePost(currentPost._id!!, body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result -> Log.i("POST_RESULT", "$result") },
                    { error -> Log.d("POST_ERROR_IMAGE", "${error.printStackTrace()}") }
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
        Log.d("POST_IMAGE", "CREATION_CHECK")
    }
}