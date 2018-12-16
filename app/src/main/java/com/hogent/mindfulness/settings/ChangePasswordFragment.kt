package com.hogent.mindfulness.settings

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.hogent.mindfulness.MainActivity
import com.hogent.mindfulness.R
import com.hogent.mindfulness.domain.ViewModels.UserViewModel
import kotlinx.android.synthetic.main.fragment_change_password.*

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ChangePasswordFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 *
 */
class ChangePasswordFragment : Fragment() {
    //    private var listener: OnFragmentInteractionListener? = null
    private lateinit var userView: UserViewModel
    var cancel = false
    var focusView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        userView = activity?.run {
            ViewModelProviders.of(this).get(UserViewModel::class.java)
        } ?: throw Exception("Invalid activity.")

        return inflater.inflate(R.layout.fragment_change_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userView.uiMessage.observe(this, Observer {
            when (it!!.data) {
                "passwordchangedAuth" -> {
                    progressBar_changepassword.visibility = View.GONE
                    it.data = "changed"
                    createDialog()
                }
                "passwordchangederrorInput" -> {
                    progressBar_changepassword.visibility = View.GONE
                    change_pass_new_pass.text.clear()
                    change_pass_new_pass_repeat.text.clear()
                    change_pass_new_pass.error = getString(R.string.not_same_password)
                    change_pass_new_pass.requestFocus()
                    focusView = change_pass_new_pass
                    cancel = true

                }
                "passwordchangederrorAuth" -> {
                    progressBar_changepassword.visibility = View.GONE
                    change_pass_new_pass.text.clear()
                    change_pass_new_pass_repeat.text.clear()
                    change_pass_current.error = getString(R.string.fout_opgetreden)
                    change_pass_current.requestFocus()
                    focusView = change_pass_current
                    cancel = true
                }
            }
        })

        changePassword_saveBtn.setOnClickListener {
            attemptChangingPassword()
        }
    }

    private fun createDialog() {
        val builder: AlertDialog.Builder? = activity?.let {
            AlertDialog.Builder(it)
        }
        builder.apply {
            this?.setPositiveButton(
                "OK"
            ) { dialog, id ->
                (activity as MainActivity).logout()
                Toast.makeText(activity as MainActivity, "Wachtwoord geüpdated!", Toast.LENGTH_SHORT).show()
            }
        }
        builder?.setMessage("U wordt uitgelogd")
            ?.setTitle("Wachtwoord Veranderen")
        val dialog: AlertDialog? = builder?.create()

        dialog!!.show()
    }

    private fun attemptChangingPassword() {
        change_pass_current.error = null
        change_pass_new_pass.error = null
        change_pass_new_pass_repeat.error = null

        if (TextUtils.isEmpty(change_pass_current.text.toString())) {
            change_pass_current.error = getString(R.string.error_field_required)
            focusView = change_pass_current
            cancel = true
        }

        if (TextUtils.isEmpty(change_pass_new_pass.text.toString())) {
            change_pass_new_pass.error = getString(R.string.error_field_required)
            focusView = change_pass_new_pass
            cancel = true
        }
        if (TextUtils.isEmpty(change_pass_new_pass_repeat.text.toString())) {
            change_pass_new_pass_repeat.error = getString(R.string.error_field_required)
            focusView = change_pass_new_pass_repeat
            cancel = true
        }

        if (change_pass_new_pass_repeat.text.toString() != change_pass_new_pass.text.toString()) {
            change_pass_new_pass_repeat.text.clear()
            change_pass_new_pass.text.clear()
            change_pass_new_pass.error = getString(R.string.not_same_password)
            focusView = change_pass_new_pass
            cancel = true
        }

        if (cancel) {
            focusView?.requestFocus()
        } else {
            progressBar_changepassword.visibility = View.VISIBLE
            userView.changePasswordWithAuth(
                change_pass_new_pass_repeat.text.toString(),
                change_pass_current.text.toString()
            )
        }
    }


}
