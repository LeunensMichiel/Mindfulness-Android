package com.hogent.mindfulness.domain.ViewModels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.hogent.mindfulness.data.API.UserApiService
import com.hogent.mindfulness.data.FIleApiService
import com.hogent.mindfulness.data.LocalDatabase.repository.UserRepository
import com.hogent.mindfulness.domain.InjectedViewModel
import com.hogent.mindfulness.domain.Model
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

class UserViewModel : InjectedViewModel() {

    var rawUser = MutableLiveData<Model.User>()
    var uiMessage = MutableLiveData<Model.uiMessage>()
    var errorMessage = MutableLiveData<Model.loginErrorMessage>()
    var toastMessage = MutableLiveData<String>()
    var dbUser: LiveData<Model.User> = userRepo.user
    var bitmap = MutableLiveData<Bitmap>()
    var loggingIn = true

    @Inject
    lateinit var userApi: UserApiService

    @Inject
    lateinit var userRepo: UserRepository

    @Inject
    lateinit var fIleApiService: FIleApiService

    private lateinit var subscription: Disposable

    @Inject
    lateinit var context: Context

    init {
        errorMessage.value = Model.loginErrorMessage()
    }

    fun login(loginDetails: Model.Login) {

        uiMessage.value = Model.uiMessage("login_start_progress")
        subscription = userApi.login(loginDetails)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { user ->
                    onRetrieveUserSucces(user)
                    Log.d("PROFILE_PIC", "${user}")
                    uiMessage.postValue(Model.uiMessage("login_end_progress"))
                },
                { error ->
                    uiMessage.postValue(Model.uiMessage("login_end_progress"))
                    errorMessage.postValue(Model.loginErrorMessage("login_api_fail"))
                    toastMessage.value = "Login gefaald."
                    toastMessage.value = null
                }
            )
    }
    
    fun register(registerDetails: Model.Register){
        uiMessage.postValue(Model.uiMessage("registeren_start_progress"))
        subscription = userApi.register(registerDetails)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { user ->
                    Log.d("register", "$user")
                    onRetrieveUserSucces(user)
                    uiMessage.postValue(Model.uiMessage("registere_end_progress"))
                },
                { error ->
                    toastMessage.value = "Registreren gefaald."
                    uiMessage.postValue(Model.uiMessage("registere_end_progress"))
                }
            )
    }

    fun addGroup(group: Model.user_group){
        toastMessage.postValue(null)
        subscription = userApi.updateUserGroup(userRepo.user.value?._id!!, group)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    Log.d("group", "$result")
                    userRepo.user.value?.group = result
                    userRepo.updateUser(userRepo.user.value!!)
                },
                { error ->
                    Log.e("group", "$error")
                    toastMessage.postValue("Code niet herkend.")
                }
            )
    }

    fun unlockSession(unlockedSession: Model.unlock_session){
        unlockedSession.id = userRepo.user.value?._id!!
        subscription = userApi.updateUser(unlockedSession)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    userRepo.user.value?.unlocked_sessions!!.add(unlockedSession.session_id)
                    userRepo.updateUser(userRepo.user.value!!)
                },
                { error ->
                    toastMessage.postValue("Code niet herkend.")
                }
            )
    }

    fun updateUserGroep(group: String) {
        val groupuser = Model.user_group(group)
        subscription = userApi.updateUserGroup(userRepo.user.value!!._id!!, groupuser)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> Log.d("USERGROUP_RESULT", "$result") },
                { error -> Log.d("USERGROUP_ERROR", "$error") }
            )
    }

    fun updateFeedback(feedback: Boolean){
        userRepo.user.value?.feedbackSubscribed = feedback
        dbUser.value?.feedbackSubscribed = feedback
        subscription = userApi.updateUserFeedback(userRepo.user.value!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> Log.d("FEEDBACK_RESULT", "$result") },
                { error -> Log.d("FEEDBACK_ERROR", "$error") }
            )
    }

    fun retrieveProfilePicture() {
        doAsync {
            if (userRepo.user.value?.image_file_name != null){
                Log.d("PROFILE_PIC", "CHECK")
                subscription = fIleApiService.getFile("profile_image", userRepo.user.value?.image_file_name!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { result -> convertToBitmap(result) },
                        { error -> setToast("Profiel afbeeldingen niet kunnen ophalen.") }
                    )

            }
        }
    }

    fun convertToBitmap(body: ResponseBody){
        doAsync {
            var imgFile: File
            var fos: FileOutputStream
            var bMap:Bitmap
            try {
                imgFile = File.createTempFile(userRepo.user.value?.image_file_name, "jpg")
                imgFile.deleteOnExit()
                fos = FileOutputStream(imgFile)
                fos.write(body.bytes())
                fos.close()
                bMap = BitmapFactory.decodeFile(imgFile.absolutePath)
                bitmap.postValue(bMap)
                Log.d("PROFILE_PIC", "CHECK")
            } catch (ex: IOException) {
                setToast("Er ging iets mis. De afbeelding van de oefeningen werden niet opgehaald.")
            }
        }
    }

    fun updateProfilePicture(file: File, bitmap: Bitmap){
            val reqFile = RequestBody.create(
                MediaType.parse("image/jpg"),
                file
            )
            val body = MultipartBody.Part.createFormData("file", file.name + ".jpg", reqFile)
            subscription = userApi.updateUserProfilePicture(userRepo.user.value?._id!!, body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        Log.d("PROFILE_CHANGED", "RESULT")
                        setBitmap(bitmap)
                        loggingIn = false
                        userRepo.user.value?.image_file_name = result.result
                        userRepo.updateUser(userRepo.user.value!!)
                    },
                    { error ->
                        setToast("Er is iets mis gegaan. Profielfoto is niet veranderd.")
                    }
                )

    }

    fun setBitmap(bitmap: Bitmap){
        this.bitmap.postValue(bitmap)
    }

    private fun setToast(message:String){
        toastMessage.postValue(null)
        toastMessage.postValue(message)
    }

    fun sendPasswordEmail(email : String) {
        val emailUser = Model.ForgotPassword(email, null)
        subscription = userApi.sendPasswordEmail(emailUser)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> run {
                    uiMessage.postValue(Model.uiMessage("emailsent"))
                    Log.d("EMAIL_SENT_RESULT", "$result") }
                },
                { error -> run {
                    uiMessage.postValue(Model.uiMessage("emailerror"))
                    Log.d("EMAIL_SENT_ERROR", "$error") }
                }
            )
    }

    fun changePasswordWithoutAuth(password: String, email: String, code: String) {
        val changePasswordWithCode = Model.ForgotPasswordWithCode(email, code, password)
        subscription = userApi.changePasswordWithoutAuth(changePasswordWithCode)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
            { result -> run {
                uiMessage.postValue(Model.uiMessage("passwordchanged"))
                Log.d("PASSWORD_CHANGED_RESULT", "$result") }
            },
            { error -> run {
                uiMessage.postValue(Model.uiMessage("passwordchangederror"))
                Log.d("PASSWORD_CHANGED_ERROR", "$error") }
            }
        )
    }

    fun changePasswordWithAuth(new: String, old: String) {
        val changePassword = Model.OldAndNewPassword(new, old, null)
            subscription = userApi.changePasswordWithAuth(dbUser.value?._id!!, changePassword)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> run {
                    uiMessage.postValue(Model.uiMessage("passwordchangedAuth"))
                    Log.d("PASSWORD_CHANGED_RESULT", "$result") }
                },
                { error -> run {
                    when (error.message){
                        "Input invalid!" -> uiMessage.postValue(Model.uiMessage("passwordchangederrorInput"))
                        "Unauthorized!" -> uiMessage.postValue(Model.uiMessage("passwordchangederrorAuth"))
                    }
                    Log.d("PASSWORD_CHANGED_ERROR", "$error") }
                }
            )
    }

    fun changeEmail(email: String) {
        //I REUSE THE FORGETPASSWORDMODEL BECAUSE IT HAS THE EXACT SAME DATA I NEED FOR THIS
        val email = Model.ForgotPassword(email, null)
        subscription = userApi.changeEmail(dbUser.value?._id!!, email)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> run {
                    uiMessage.postValue(Model.uiMessage("emailchanged"))
                    Log.d("EMAIL_CHANGED_RESULT", "$result") }
                },
                { error -> run {
                    when (error.message){
                        "Input invalid!" -> uiMessage.postValue(Model.uiMessage("emailchangederror"))
                    }
                    Log.d("EMAIL_CHANGED_ERROR", "$error") }
                }
            )
    }


    private fun onRetrieveUserSucces(user: Model.User?) {
        var pref = context.getSharedPreferences("userDetails", Context.MODE_PRIVATE)
        pref.edit().putString("authToken", user?.token).apply()
        rawUser.value = user
        userRepo.insert(user!!)
    }
}