package com.hogent.mindfulness.sessions

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.hogent.mindfulness.R
import com.hogent.mindfulness.domain.ViewModels.UserViewModel
import kotlinx.android.synthetic.main.full_screen_animation_dialog.view.*

class FullscreenDialogWithAnimation : DialogFragment() {

    lateinit var monsterimage : ImageView
    private lateinit var userViewModel: UserViewModel


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

        userViewModel = activity?.run {
            ViewModelProviders.of(this).get(UserViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        monsterimage = view.monster

        view.fullscreenDialogBtn.setOnClickListener() {
            dialog.dismiss()
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            dialog.window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
        val monstercount = ("R.drawable.ic_monster" + ((userViewModel.dbUser.value!!.unlocked_sessions.size + 1) % 14)) + ".xml"

        initAnimation(monstercount.toInt())
    }

    private fun initAnimation(url: Int) {
        monsterimage.setImageResource(url)
    }
}
