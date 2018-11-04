package com.hogent.mindfulness.oefeningdetails


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.hogent.mindfulness.R
import kotlinx.android.synthetic.main.fragment_paragraaf_tekst.*

class ParagraafTekst : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_paragraaf_tekst, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*
        if(this.arguments!!.containsKey("tekst")){
            paragraafTekstTextView.text = this.arguments!!.getString("tekst", "check")
            paragraafFotoImageView.setVisibility(View.INVISIBLE);
        } */
    }
}
