package com.hogent.mindfulness.group

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.hogent.mindfulness.MainActivity
import com.hogent.mindfulness.R
import com.hogent.mindfulness.scanner.ScannerActivity
import kotlinx.android.synthetic.main.fragment_group.*

class GroupFragment() : Fragment() {
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

        if(activity!!.intent.hasExtra("code")) {
            groepscodeScanner.setText(activity!!.intent.getStringExtra("code"))
        }
    }

    override fun onStart() {
        super.onStart()
        fragment_GroupBtn.setOnClickListener {
            val intent = Intent(activity, ScannerActivity::class.java)
            intent.putExtra("returnActivity", 2)
            startActivity(intent)
            activity!!.finish()
        }

        groepscanners_BtnConfirm.setOnClickListener {
            (activity as MainActivity).updateUserGroup()
            Toast.makeText(activity, "Group updated!", Toast.LENGTH_SHORT)
        }
    }


}