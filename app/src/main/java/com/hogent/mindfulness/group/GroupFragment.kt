package com.hogent.mindfulness.group

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hogent.mindfulness.R
import com.hogent.mindfulness.scanner.ScannerActivity
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_group.*
import kotlinx.android.synthetic.main.session_fragment.*

class GroupFragment() : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group, container, false)
    }

    override fun onStart() {
        super.onStart()

        rv_group.setOnClickListener { view ->
            val intent = Intent(activity, ScannerActivity::class.java)
            startActivity(intent)
        }
    }
}