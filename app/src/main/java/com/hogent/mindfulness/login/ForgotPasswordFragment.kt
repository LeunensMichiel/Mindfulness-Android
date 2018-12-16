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

class ForgotPasswordFragment : Fragment() {

    private lateinit var userView: UserViewModel

    /**
     * In the onCreateView, we'll initialize the UserViewModel so we can use its Login Methods.
     * After that the layout for this fragment will be inflated
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        userView = activity?.run {
            ViewModelProviders.of(this).get(UserViewModel::class.java)
        } ?: throw Exception("Invalid activity.")

        return inflater.inflate(R.layout.fragment_forgotpassword, container, false)
    }

    /**
     * In the OnViewCreated Method, we add observers to uiMessage. When our API CALL has succeeded and thus UIMessage won't be null anymore, we'll enable or disable other Views
     * If everything succeeds, we''l be asked to go back to login fragment
     * By observing UImessage we can display input to the user
     * There are also ClickListeners to attempt the forgotPassword method
     */
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

    /**
     * This method will enable or disable the views for entering the code
     * @param boolean enables on true and disables on false
     */
    private fun enableOtherFields(boolean: Boolean) {
        forgotpassword_password.isEnabled = boolean
        forgotpassword_passwordrepeat.isEnabled = boolean
        forgotpassword_code.isEnabled = boolean
        forgotpassword_passBtn.isEnabled = boolean
    }

    /**
     * Attempts to alter the password of the user.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual change attempt is made.
     */
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
    /**
     * Attempts to sends a code to the users email.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual call attempt is made.
     */
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

    /**
     * Check's if email is valid
     * Email is valid when it has a '@' sign
     *
     * no regex
     * From Stack Overflow: Apparently the following is a reg-ex that correctly validates most e-mails addresses that conform to RFC 2822,
     * (and will still fail on things like "user@gmail.com.nospam", as will org.apache.commons.validator.routines.EmailValidator)
     * @param email is the email that will be checked
     */
    private fun isEmailValid(email: String): Boolean {
        //TODO: Replace this with your own logic
        return email.contains("@")
    }
}



