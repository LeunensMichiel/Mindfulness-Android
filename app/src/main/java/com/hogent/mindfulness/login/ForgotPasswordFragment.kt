package com.hogent.mindfulness.login


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.hogent.mindfulness.MainActivity
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
                    teforgotpassword_statustext.text = getString(R.string.fout_opgetreden)
                    forgotpassword_email.requestFocus()
                }
                "passwordchanged" -> {
                    it.data = "changed"
                    forgotpassword_loading.visibility = View.GONE
                    teforgotpassword_statustext.text = ""
                    teforgotpassword_statustext.visibility = View.INVISIBLE
                    (activity as MainActivity).toLogin(btnBackToLoginfromBla)
                    Toast.makeText(activity as MainActivity, "Wachtwoord geüpdated!", Toast.LENGTH_SHORT).show()
                }
                "passwordchangederror" -> {
                    forgotpassword_loading.visibility = View.GONE
                    teforgotpassword_statustext.text = getString(R.string.fout_opgetreden)
                    forgotpassword_password.requestFocus()
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
    }

    private fun attemptChangingPassword() {
        var cancel = false
        var focusView: View? = null
        forgotpassword_password.error = null
        forgotpassword_passwordrepeat.error = null
        forgotpassword_code.error = null

        if (TextUtils.isEmpty(forgotpassword_password.text.toString())) {
            forgotpassword_password.error = getString(R.string.error_field_required)
            focusView = forgotpassword_password
            cancel = true
        }

        if (TextUtils.isEmpty(forgotpassword_passwordrepeat.text.toString())) {
            forgotpassword_passwordrepeat.error = getString(R.string.error_field_required)
            focusView = forgotpassword_passwordrepeat
            cancel = true
        }
        if (TextUtils.isEmpty(forgotpassword_code.text.toString())) {
            forgotpassword_code.error = getString(R.string.error_field_required)
            focusView = forgotpassword_code
            cancel = true
        }

        if (forgotpassword_password.text.toString() != forgotpassword_passwordrepeat.text.toString()) {
            forgotpassword_passwordrepeat.text.clear()
            forgotpassword_password.text.clear()
            forgotpassword_password.error = getString(R.string.not_same_password)
            focusView = forgotpassword_password
            cancel = true
        }


        if (cancel) {
            focusView?.requestFocus()
        } else {
            forgotpassword_loading.visibility = View.VISIBLE
            teforgotpassword_statustext.text = "Wachtwoord wordt veranderd..."
            userView.changePasswordWithoutAuth(forgotpassword_passwordrepeat.text.toString(), forgotpassword_email.text.toString(), forgotpassword_code.text.toString())
        }

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
            teforgotpassword_statustext.text = "Er wordt een code naar het opgegeven emailadres verstuurd..."
            teforgotpassword_statustext.visibility = View.VISIBLE
            userView.sendPasswordEmail(forgotpassword_email.text.toString())
        }
    }

    private fun isEmailValid(email: String): Boolean {
        //TODO: Replace this with your own logic
        return email.contains("@")
    }
}



