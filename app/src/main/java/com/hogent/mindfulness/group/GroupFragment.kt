package com.hogent.mindfulness.group

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hogent.mindfulness.MainActivity
import com.hogent.mindfulness.R
import com.hogent.mindfulness.domain.Model
import com.hogent.mindfulness.domain.ViewModels.UserViewModel
import com.hogent.mindfulness.scanner.ScannerActivity
import kotlinx.android.synthetic.main.fragment_group.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class GroupFragment() : Fragment() {

    private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        userViewModel =activity?.run {
            ViewModelProviders.of(this).get(UserViewModel::class.java)
        }?: throw Exception("Invalid activity.")

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(activity!!.intent.hasExtra("group")) {
            groepscodeScanner.setText(activity!!.intent.getStringExtra("code"))
        }

        val photoBtn = fragment_GroupBtn
        photoBtn.setOnClickListener {
            val intent = Intent(activity, ScannerActivity::class.java)
            intent.putExtra("returnActivity", 2)
            startActivity(intent)
        }

        groepscanners_BtnConfirm.onClick {
            userViewModel.addGroup(Model.user_group(groepscodeScanner.text.toString()))
            (activity as MainActivity).toSessions()
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).setActionBarTitle("Uw groep kiezen")
    }

}