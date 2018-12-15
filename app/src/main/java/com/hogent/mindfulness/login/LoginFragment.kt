package com.hogent.mindfulness.login

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.hogent.mindfulness.R
import com.hogent.mindfulness.domain.Model
import com.hogent.mindfulness.domain.ViewModels.UserViewModel
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : Fragment() {
    private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        userViewModel = activity?.run {
            ViewModelProviders.of(this).get(UserViewModel::class.java)
        } ?: throw Exception("Invalid activity")

        userViewModel.rawUser.observe(this, Observer {
            if (it != null){
                activity!!.getSharedPreferences(getString(R.string.sharedPreferenceUserDetailsKey), Context.MODE_PRIVATE)
                    .edit()
                    .putString(getString(R.string.authTokenKey), it.token)
                    .putString(getString(R.string.userIdKey), it._id)
                    .putString(getString(R.string.lastUnlockedSession), it.current_session_id)
                    .putBoolean(getString(R.string.wantsFeedback), it.feedbackSubscribed)
                    .apply()
            }
        })

        userViewModel.uiMessage.observe(this, android.arch.lifecycle.Observer {
            when(it!!.data) {
                "login_start_progress" -> showProgress(true)
                "login_end_progress" -> showProgress(false)
            }
        })

        userViewModel.errorMessage.observe(this, Observer {
            when(it!!.data) {
                "login_api_fail" -> kotlin.run {
                    login_password.error = getString(R.string.error_incorrect_password)
                    login_password.requestFocus()
                    showProgress(false)
                }
            }
        })

        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    /**
     * Add's a listener to the password TextField to automatically login
     * Add's listener to login Button
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        login_password.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin()
                return@OnEditorActionListener true
            }
            false
        })

        email_sign_in_button.setOnClickListener { attemptLogin() }

    }

    interface LoginFragmentCallBack {
        fun onclickRegister()
    }




    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private fun attemptLogin() {

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
        userViewModel.login(loginDetails)
//        val loginService = ServiceGenerator.createService(UserApiService::class.java, (activity as LoginActivity))
//        showProgress(true)
//
//        disposable = loginService.login(loginDetails)
////            .doOnSubscribe {  }
////            .doOnTerminate {  }
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(
//                { user ->
//                    Log.i("user", "$user")
//                    successfulLogin(user)
//                },
//                { error -> failedLogin(error.message) }
//            )
    }

    /**
     * Shows error msg when login failed
     */
    private fun failedLogin(error: String?) {
        // TODO geef hier later een betere foutmelding op mss niet speciefiek op password
        login_password.error = getString(R.string.error_incorrect_password)
        login_password.requestFocus()
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
                        if (login_form != null)
                            login_form.visibility = if (show) View.GONE else View.VISIBLE
                    }
                })

            login_progress.visibility = if (show) View.VISIBLE else View.GONE
            login_progress.animate()
                .setDuration(shortAnimTime)
                .alpha((if (show) 1 else 0).toFloat())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        if (login_form != null)
                            login_progress.visibility = if (show) View.VISIBLE else View.GONE
                    }
                })
        }

    }
}
