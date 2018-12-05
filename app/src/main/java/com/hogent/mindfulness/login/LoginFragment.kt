package com.hogent.mindfulness.login

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.hogent.mindfulness.MainActivity
import com.hogent.mindfulness.R
import com.hogent.mindfulness.data.*
import com.hogent.mindfulness.data.LocalDatabase.MindfulnessDBHelper
import com.hogent.mindfulness.domain.Model
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_login.*
import org.jetbrains.anko.support.v4.toast

class LoginFragment : Fragment() {
    private lateinit var disposable: Disposable
    private val mMindfullDB by lazy {
        MindfulnessDBHelper(context!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Log.i("FRAGMENT", "LOGINFRAGMENT CREATED")
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    /**
     * Add's a listener to the password TextField to automatically login
     * Add's listener to login Button
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Set up the login form.
//        populateAutoComplete()
        Log.i("FRAGMENT", "LOGINFRAGMENT VIEW CREATED")
        login_password.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin()
                return@OnEditorActionListener true
            }
            false
        })
        email_sign_in_button.setOnClickListener { attemptLogin() }

        login_form_register_btn.setOnClickListener {
            val loginCallback = activity as LoginFragmentCallBack
            loginCallback.onclickRegister()
        }

    }

    interface LoginFragmentCallBack {
        fun onclickRegister()
    }


//    private fun populateAutoComplete() {
//        if (!mayRequestContacts()) {
//            return
//        }
//
//        loaderManager.initLoader(0, null, this)
//    }
//
//    private fun mayRequestContacts(): Boolean {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//            return true
//        }
//        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
//            return true
//        }
//        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
//            Snackbar.make(email, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
//                .setAction(android.R.string.ok,
//                    {
//                        requestPermissions(
//                            arrayOf(READ_CONTACTS),
//                            REQUEST_READ_CONTACTS
//                        )
//                    })
//        } else {
//            requestPermissions(
//                arrayOf(READ_CONTACTS),
//                REQUEST_READ_CONTACTS
//            )
//        }
//        return false
//    }

    /**
     * Callback received when a permissions request has been completed.
     */
//    override fun onRequestPermissionsResult(
//        requestCode: Int, permissions: Array<String>,
//        grantResults: IntArray
//    ) {
//        if (requestCode == REQUEST_READ_CONTACTS) {
//            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                populateAutoComplete()
//            }
//        }
//    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private fun attemptLogin() {
//        if (mAuthTask != null) {
//            return
//        }

        // Reset errors.
        email.error = null
        login_password.error = null

        // Store values at the time of the login attempt.
        val emailStr = email.text.toString()
        val passwordStr = login_password.text.toString()

        var cancel = false
        var focusView: View? = null

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(passwordStr)) {
            login_password.error = getString(R.string.error_field_required)
            focusView = login_password
            cancel = true

        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(emailStr)) {
            email.error = getString(R.string.error_field_required)
            focusView = email
            cancel = true
        } else if (!isEmailValid(emailStr)) {
            email.error = getString(R.string.error_invalid_email)
            focusView = email
            cancel = true
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView?.requestFocus()
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            val login = Model.Login(emailStr, passwordStr)
            doLogin(login)
        }
    }

    private fun doLogin(loginDetails: Model.Login) {
        val loginService = ServiceGenerator.createService(UserApiService::class.java, (activity as LoginActivity))
        showProgress(true)

        disposable = loginService.login(loginDetails)
//            .doOnSubscribe {  }
//            .doOnTerminate {  }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { user ->
                    Log.i("user", "$user")
                    successfulLogin(user)
                },
                { error -> failedLogin(error.message) }
            )
    }

    private fun successfulLogin(user: Model.User) {
        if (mMindfullDB.addUser(user)) toast("User added to local db") else toast("User not added to local db")
        Log.i("DATABASE AFTER LOGIN", "${mMindfullDB.getUser()}")
        Log.i("USERCHECK AFTER LOGIN", "$user")
        showProgress(false)
        activity!!.getSharedPreferences(getString(R.string.sharedPreferenceUserDetailsKey), Context.MODE_PRIVATE)
        .edit()
            .putString(getString(R.string.authTokenKey), user.token)
            .putString(getString(R.string.userIdKey), user._id)
            .putString(getString(R.string.lastUnlockedSession), user.current_session_id)
            .putBoolean(getString(R.string.wantsFeedback), user.feedbackSubscribed)
            .apply()
//        val repository = UserRepository(AppDatabase.getDatabase(activity!!.applicationContext))
//        repository.insertUser(user)

        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
    }

    /**
     * Shows error msg when login failed
     */
    private fun failedLogin(error: String?) {
        // TODO geef hier later een betere foutmelding op mss niet speciefiek op password
        login_password.error = getString(R.string.error_incorrect_password)
        login_password.requestFocus()
        Log.d("logintje", error)
        showProgress(false)
    }

    /**
     * Check's if email is valid
     * Email is valid when it has a '@' sign
     *
     * It's maybe better to chance this the a regex
     */
    private fun isEmailValid(email: String): Boolean {
        //TODO: Replace this with your own logic
        return email.contains("@")
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private fun showProgress(show: Boolean) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

            login_form.visibility = if (show) View.GONE else View.VISIBLE
            login_form.animate()
                .setDuration(shortAnimTime)
                .alpha((if (show) 0 else 1).toFloat())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        login_form.visibility = if (show) View.GONE else View.VISIBLE
                    }
                })

            login_progress.visibility = if (show) View.VISIBLE else View.GONE
            login_progress.animate()
                .setDuration(shortAnimTime)
                .alpha((if (show) 1 else 0).toFloat())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        login_progress.visibility = if (show) View.VISIBLE else View.GONE
                    }
                })
        }

    }

//    override fun onCreateLoader(i: Int, bundle: Bundle?): Loader<Cursor> {
//        return CursorLoader(
//            this,
//            // Retrieve data rows for the device user's 'profile' contact.
//            Uri.withAppendedPath(
//                ContactsContract.ProfileFragment.CONTENT_URI,
//                ContactsContract.Contacts.Data.CONTENT_DIRECTORY
//            ), ProfileQuery.PROJECTION,
//
//            // Select only email addresses.
//            ContactsContract.Contacts.Data.MIMETYPE + " = ?", arrayOf(
//                ContactsContract.CommonDataKinds.Email
//                    .CONTENT_ITEM_TYPE
//            ),
//
//            // Show primary email addresses first. Note that there won't be
//            // a primary email address if the user hasn't specified one.
//            ContactsContract.Contacts.Data.IS_PRIMARY + " DESC"
//        )
//    }

//    override fun onLoadFinished(cursorLoader: Loader<Cursor>, cursor: Cursor) {
//        val emails = ArrayList<String>()
//        cursor.moveToFirst()
//        while (!cursor.isAfterLast) {
//            emails.add(cursor.getString(ProfileQuery.ADDRESS))
//            cursor.moveToNext()
//        }
//
//        addEmailsToAutoComplete(emails)
//    }
//
//    override fun onLoaderReset(cursorLoader: Loader<Cursor>) {
//
//    }

//    private fun addEmailsToAutoComplete(emailAddressCollection: List<String>) {
//        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
//        val adapter = ArrayAdapter(
//            this@LoginActivity,
//            android.R.layout.simple_dropdown_item_1line, emailAddressCollection
//        )
//
//        email.setAdapter(adapter)
//    }
//
//    object ProfileQuery {
//        val PROJECTION = arrayOf(
//            ContactsContract.CommonDataKinds.Email.ADDRESS,
//            ContactsContract.CommonDataKinds.Email.IS_PRIMARY
//        )
//        val ADDRESS = 0
//        val IS_PRIMARY = 1
//    }
}
