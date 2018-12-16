package com.hogent.mindfulness.settings

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
import kotlinx.android.synthetic.main.fragment_change_email_settings.*

class ChangeEmailSettingsFragment : Fragment() {

    private lateinit var userView: UserViewModel
    var cancel = false
    var focusView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        userView = activity?.run {
            ViewModelProviders.of(this).get(UserViewModel::class.java)
        } ?: throw Exception("Invalid activity.")
        return inflater.inflate(R.layout.fragment_change_email_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userView.uiMessage.observe(this, Observer {
            when (it!!.data) {
                "emailchanged" -> {
                    progressBar_changeEmail.visibility = View.GONE
                    it.data = "changed"
                    (activity as MainActivity).toSettings()
                    Toast.makeText(activity as MainActivity, "Email geüpdated!", Toast.LENGTH_SHORT).show()
                }
                "emailchangederror" -> {
                    progressBar_changeEmail.visibility = View.GONE

                    change_email_newEmail.text.clear()
                    change_email_newEmailRepeat.text.clear()
                    change_email_newEmail.error = getString(R.string.fout_opgetreden)
                    change_email_newEmail.requestFocus()
                    focusView = change_email_newEmail
                    cancel = true
                }
            }
        })

        change_emailSaveBtn.setOnClickListener {
            attemptChangingEmail()
        }
    }

    private fun attemptChangingEmail() {
        change_email_currEmail.error = null
        change_email_newEmail.error = null
        change_email_newEmailRepeat.error = null

        if (TextUtils.isEmpty(change_email_currEmail.text.toString())) {
            change_email_currEmail.error = getString(R.string.error_field_required)
            focusView = change_email_currEmail
            cancel = true
        }

        if (TextUtils.isEmpty(change_email_newEmail.text.toString())) {
            change_email_newEmail.error = getString(R.string.error_field_required)
            focusView = change_email_newEmail
            cancel = true
        }
        if (TextUtils.isEmpty(change_email_newEmailRepeat.text.toString())) {
            change_email_newEmailRepeat.error = getString(R.string.error_field_required)
            focusView = change_email_newEmailRepeat
            cancel = true
        }

        if (change_email_newEmail.text.toString() != change_email_newEmailRepeat.text.toString()) {
            change_email_newEmailRepeat.text.clear()
            change_email_newEmail.text.clear()
            change_email_newEmail.error = getString(R.string.email_not_same)
            focusView = change_email_newEmail
            cancel = true
        }


        if (cancel) {
            focusView?.requestFocus()
        } else {
            progressBar_changeEmail.visibility = View.VISIBLE
            userView.changeEmail(change_email_newEmailRepeat.text.toString())
        }
    }

}
