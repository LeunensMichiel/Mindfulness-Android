package com.hogent.mindfulness.group

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hogent.mindfulness.MainActivity
import com.hogent.mindfulness.R
import com.hogent.mindfulness.data.LocalDatabase.MindfulnessDBHelper
import com.hogent.mindfulness.data.ServiceGenerator
import com.hogent.mindfulness.data.UserApiService
import com.hogent.mindfulness.domain.Model
import com.hogent.mindfulness.scanner.ScannerActivity
import com.hogent.mindfulness.settings.SettingsViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_group.*
import java.lang.Exception
import java.nio.channels.Selector

class GroupFragment() : Fragment() {

    lateinit var  mMindfullDB:MindfulnessDBHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group, container, false)
    }

    private lateinit var model: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = activity?.run {
            ViewModelProviders.of(this).get(SettingsViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        groepscodeScannerTxf.setText("5bf730b27a76ee0b049432a6")
        val photoBtn = fragment_GroupBtn
        photoBtn.setOnClickListener {
            val intent = Intent(activity, ScannerActivity::class.java)
            startActivity(intent)
        }

        val saveBtn = groepscanners_BtnConfirm
        val txfield = groepscodeScannerTxf
        saveBtn.setOnClickListener {
            if (txfield.text.isNotEmpty()) {
                model.user?.value?.group?._id = txfield.text.toString()
            }
        }
    }
//5bf730b27a76ee0b049432a6

}