package com.hogent.mindfulness.login


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hogent.mindfulness.R
import com.hogent.mindfulness.domain.ViewModels.UserViewModel
import kotlinx.android.synthetic.main.fragment_forgotpassword.*
import kotlinx.android.synthetic.main.fragment_register.*

/**
 * A simple [Fragment] subclass.
 *
 */
class ForgotPasswordFragment : Fragment() {

    private lateinit var userView: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        userView = activity?.run {
            ViewModelProviders.of(this).get(UserViewModel::class.java)
        } ?: throw Exception("Invalid activity.")

        return inflater.inflate(R.layout.fragment_forgotpassword, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        enableOtherFields(false)

        userView.uiMessage.observe(this, Observer {
            when (it!!.data) {
                "emailsent" -> {
                    forgotpassword_loading.visibility = View.GONE
                    enableOtherFields(true)
                    teforgotpassword_statustext.text = "Controleer uw emailadres voor een code"
                }
                "emailerror" -> {
                    forgotpassword_loading.visibility = View.GONE
                    enableOtherFields(false)
                    teforgotpassword_statustext.text = "Er is een fout opgelopen"
                }
            }
        })

        forgotpassword_emailBtn.setOnClickListener {
            attemptGettingCode()
        }

        forgotpassword_passBtn.setOnClickListener {
            attemptChangingPassword()
        }
    }


    private fun enableOtherFields(boolean: Boolean) {
        forgotpassword_password.isEnabled = boolean
        forgotpassword_passwordrepeat.isEnabled = boolean
        forgotpassword_code.isEnabled = boolean
        forgotpassword_passBtn.isEnabled = boolean

        forgotpassword_password.isFocusable = boolean
        forgotpassword_passwordrepeat.isFocusable = boolean
        forgotpassword_code.isFocusable = boolean
        forgotpassword_passBtn.isFocusable = boolean
    }

    private fun attemptChangingPassword() {
        var cancel = false
        var focusView: View? = null
        forgotpassword_password.error = null
        forgotpassword_passwordrepeat.error = null
        forgotpassword_code.error = null


    }

    private fun attemptGettingCode() {
        var cancel = false
        var focusView: View? = null
        forgotpassword_email.error = null

        if (TextUtils.isEmpty(forgotpassword_email.text.toString())) {
            register_email.error = getString(R.string.error_field_required)
            focusView = register_email
            cancel = true
        } else if (!isEmailValid(forgotpassword_email.text.toString())) {
            register_email.error = getString(R.string.error_invalid_email)
            focusView = register_email
            cancel = true
        }

        if (cancel) {
            focusView?.requestFocus()
        } else {
            forgotpassword_loading.visibility = View.VISIBLE
            teforgotpassword_statustext.text = "Er wordt een code naar het opgegeven emailadres verstuurt"
            userView.sendPasswordEmail(forgotpassword_email.text.toString())
        }
    }

    private fun isEmailValid(email: String): Boolean {
        //TODO: Replace this with your own logic
        return email.contains("@")
    }
}



