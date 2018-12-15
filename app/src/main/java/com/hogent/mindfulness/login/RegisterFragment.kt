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
import kotlinx.android.synthetic.main.fragment_register.*


class RegisterFragment : Fragment() {

    private lateinit var userView:UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        userView = activity?.run {
            ViewModelProviders.of(this).get(UserViewModel::class.java)
        }?: throw Exception("Invalid activity.")

        userView.uiMessage.observe(this, android.arch.lifecycle.Observer {
            when(it!!.data) {
                "register_start_progress" -> showProgress(true)
                "register_end_progress" -> showProgress(false)
            }
        })

        userView.rawUser.observe(this, Observer {
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

        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        edit_register_repeat_password.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptRegister()
                return@OnEditorActionListener true
            }
            false
        })

        btn_register.setOnClickListener { attemptRegister() }
    }

    //    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//
//        outState.putString("email", email.text.toString())
//        outState.putString("password", edit_register_password.text.toString())
//        outState.putString("repPassword", edit_register_repeat_password.text.toString())
//        outState.putString("groupCode", edit_group_code.text.toString())
//        Toast.makeText(activity, "email: "+outState.getString("email"), Toast.LENGTH_SHORT)
//    }

//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//
//        if(savedInstanceState != null) {
//
//            email.setText(savedInstanceState.getString("email"))
//            edit_register_password.setText(savedInstanceState.getString("password"))
//            edit_register_repeat_password.setText(savedInstanceState.getString("repPassword"))
//        }
//    }

    private fun attemptRegister() {
//        if (mAuthTask != null) {
//            return
//        }

        // Reset errors.
        register_email.error = null
        edit_register_repeat_password.error = null
        register_firstname.error = null
        register_lastname.error = null
        // Store values at the time of the login attempt.
        val emailStr = register_email.text.toString()
        val passwordStr = edit_register_repeat_password.text.toString()

        var cancel = false
        var focusView: View? = null

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(passwordStr)) {
            edit_register_repeat_password.error = getString(R.string.error_field_required)
            focusView = edit_register_repeat_password
            cancel = true
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(emailStr)) {
            register_email.error = getString(R.string.error_field_required)
            focusView = register_email
            cancel = true
        } else if (!isEmailValid(emailStr)) {
            register_email.error = getString(R.string.error_invalid_email)
            focusView = register_email
            cancel = true
        }

        if (TextUtils.isEmpty(register_firstname.text.toString())) {
            register_firstname.error = getString(R.string.error_field_required)
            focusView = register_firstname
            cancel = true
        }

        if (TextUtils.isEmpty(register_lastname.text.toString())) {
            register_lastname.error = getString(R.string.error_field_required)
            focusView = register_lastname
            cancel = true
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView?.requestFocus()
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            val registerDetails = Model.Register(emailStr, passwordStr, register_firstname.text.toString(),
                register_lastname.text.toString())
            userView.register(registerDetails)
        }
    }

//    private fun successfulRegistration(user: Model.User) {
//
//        showProgress(false)
//        activity!!.getSharedPreferences(getString(R.string.sharedPreferenceUserDetailsKey), Context.MODE_PRIVATE)
//            .edit()
//            .putString(getString(R.string.authTokenKey), user.token)
//            .putString(getString(R.string.userIdKey), user._id)
//            .apply()
//
//        val intent = Intent(activity, MainActivity::class.java)
//        startActivity(intent)
//    }
//
//    private fun failedRegistration(error: String?) {
//        edit_register_password.error = getString(R.string.failed_registration)
//        edit_register_password.requestFocus()
//        showProgress(false)
//    }

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

            register_progress.visibility = if (show) View.VISIBLE else View.GONE
            register_progress.animate()
                .setDuration(shortAnimTime)
                .alpha((if (show) 1 else 0).toFloat())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        register_progress.visibility = if (show) View.VISIBLE else View.GONE
                    }
                })
        }

    }

    interface LoginFragmentCallBack {
        fun onClickGoBackToLogin()
    }
}
