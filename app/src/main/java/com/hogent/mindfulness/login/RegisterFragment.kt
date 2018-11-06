package com.hogent.mindfulness.login

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
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
import com.hogent.mindfulness.data.LoginApiService
import com.hogent.mindfulness.data.ServiceGenerator
import com.hogent.mindfulness.domain.Model
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_register.*


class RegisterFragment : Fragment() {

    private lateinit var disposable: Disposable


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        edit_register_repeat_password.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin()
                return@OnEditorActionListener true
            }
            false
        })

        btn_register.setOnClickListener { attemptLogin() }
    }

    private fun attemptLogin() {
//        if (mAuthTask != null) {
//            return
//        }

        // Reset errors.
        email.error = null
        edit_register_repeat_password.error = null
        edit_group_code.error = null
        // Store values at the time of the login attempt.
        val emailStr = email.text.toString()
        val passwordStr = edit_register_repeat_password.text.toString()
        val groupcodeStr = edit_group_code.text.toString()

        var cancel = false
        var focusView: View? = null

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(passwordStr)) {
            edit_register_repeat_password.error = getString(R.string.error_field_required)
            focusView = edit_register_repeat_password
            cancel = true

            Log.d("logintest", passwordStr)
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

        if (TextUtils.isEmpty(groupcodeStr)) {
            edit_group_code.error = getString(R.string.error_field_required)
            focusView = edit_group_code
            cancel = true

        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView?.requestFocus()
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            val registerDetails = Model.Register(emailStr, passwordStr, groupcodeStr)
            startRegistrationCall(registerDetails)
        }


    }

    private fun startRegistrationCall(registerDetails: Model.Register) {
        val loginService = ServiceGenerator.createService(LoginApiService::class.java)
        showProgress(true)

        disposable = loginService.register(registerDetails)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { user -> successfulRegistration(user) },
                { error -> failedRegistration(error.message) }
            )
    }

    private fun successfulRegistration(user: Model.User) {

        showProgress(false)
        activity!!.getSharedPreferences(getString(R.string.sharedPreferenceUserDetailsKey), Context.MODE_PRIVATE)
            .edit()
            .putString(getString(R.string.authTokenKey), user.token)
            .putString(getString(R.string.userIdKey), user._id)
            .apply()

        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
    }

    private fun failedRegistration(error: String?) {
        // TODO geef hier later een betere foutmelding op mss niet speciefiek op password
        edit_register_password.error = getString(R.string.error_incorrect_password)
        edit_register_password.requestFocus()
        showProgress(false)
    }

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

            register_form.visibility = if (show) View.GONE else View.VISIBLE
            register_form.animate()
                .setDuration(shortAnimTime)
                .alpha((if (show) 0 else 1).toFloat())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        register_form.visibility = if (show) View.GONE else View.VISIBLE
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
}