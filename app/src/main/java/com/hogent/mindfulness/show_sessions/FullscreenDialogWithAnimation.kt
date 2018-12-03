package com.hogent.mindfulness.show_sessions

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.Log
import android.util.MonthDisplayHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.TextDelegate
import com.hogent.mindfulness.R
import kotlinx.android.synthetic.main.full_screen_animation_dialog.*
import kotlinx.android.synthetic.main.full_screen_animation_dialog.view.*

class FullscreenDialogWithAnimation : DialogFragment() {
    lateinit var MONSTER : LottieAnimationView


    companion object {
         val TAG = "FullScreenDialog"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.full_screen_animation_dialog, container, false)

        view.fullscreenDialogBtn.setOnClickListener() {
            dialog.dismiss()
        }

        MONSTER = view.monster

        return view
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            dialog.window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }

        initAnimation()

    }

    private fun initAnimation() {
        val textDelegate = TextDelegate(MONSTER)
        textDelegate.setText("Monster", "Monster")
        MONSTER.setMaxFrame(90)
        MONSTER.playAnimation()
    }
}
