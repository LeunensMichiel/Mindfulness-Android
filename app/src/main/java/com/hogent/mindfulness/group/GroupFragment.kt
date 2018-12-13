package com.hogent.mindfulness.group

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.hogent.mindfulness.R
import com.hogent.mindfulness.domain.Model
import com.hogent.mindfulness.domain.ViewModels.UserViewModel
import com.hogent.mindfulness.scanner.ScannerActivity
import kotlinx.android.synthetic.main.fragment_group.*

class GroupFragment() : Fragment() {

    private lateinit var userView: UserViewModel
    private lateinit var dbUser: Model.User
    private lateinit var txfield : EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userView = activity?.run {
            ViewModelProviders.of(this).get(UserViewModel::class.java)
        }?: throw Exception("Invalid activity.")
        dbUser = userView.dbUser.value!!

        txfield = groepscodeScanner

        val photoBtn = fragment_GroupBtn
        photoBtn.setOnClickListener {
            val intent = Intent(activity, ScannerActivity::class.java)
            startActivity(intent)
        }

        val saveBtn = groepscanners_BtnConfirm
        saveBtn.setOnClickListener {v ->
            if (txfield.text.isNotEmpty()) {
                userView.updateUserGroep(txfield.text.toString())
            }
        }
    }

    override fun onResume() {
        super.onResume()
            val sharedPreferences = activity?.getSharedPreferences(getString(R.string.sharedPreferenceUserDetailsKey), Context.MODE_PRIVATE)
            val id = sharedPreferences?.getString(getString(R.string.userGroupId), "def")
            txfield.setText(id)
    }

//5bf730b27a76ee0b049432a6
}