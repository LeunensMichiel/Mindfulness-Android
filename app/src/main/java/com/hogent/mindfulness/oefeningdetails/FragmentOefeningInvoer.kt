package com.hogent.mindfulness.oefeningdetails


import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.hogent.mindfulness.R
import kotlinx.android.synthetic.main.fragment_fragment_oefeninginvoer.*

/**
 * Deze klasse is een Fragment die verantwoordelijk is voor de invoerpagina van de oefening
 * De layout die hiermee gelinkt is is fragment_fragment_oefeninginvoer
 */
class FragmentOefeningInvoer : Fragment() {

    /**
     * in de onCreateView-methode inflaten we onze layout fragment_fragment_oefeninginvoer
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_oefeninginvoer, container, false)
    }

    /**
     * Deze methode wordt direct na de onCreateView-methode uitgevoerd
     *
     * we checken of de arguments van de fragment een key opgave heeft, als dit zo is, dan stellen we de hint van input (zie xml) gelijk aan de
     * waarde van de key opgave
     * als de button btnCamera gedrukt wordt, openen we de camera via een intent
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(this.arguments!!.containsKey("opgave")){
            inputlayout.hint = this.arguments!!.getString("opgave", "check")
        }

        btnCamera.setOnClickListener {
            var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent,0)
        }
    }

    /**
     * Als de configuratie verandert, wordt deze methode aangeroepen
     * we herladen hier de fragment en setten de hint van de input opnieuw
     */
    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)

        val ft = fragmentManager!!.beginTransaction()
        ft.detach(this).attach(this).commit()
        if(this.arguments!!.containsKey("opgave")){
            inputlayout.hint = this.arguments!!.getString("opgave", "check")
        }
    }

    /**
     * we gebruiken onActivityResult omdat we een result verwachten van de activity
     * als we dat result krijgen, wordt deze methode uitgevoerd
     * het result dat we verwachten is genomen foto
     *
     * de android camera applicatie encodeert de genomen foto in de return intent die meegeleverd wordt aan de onActivityResult() als een bitmap
     * in de extras met als key 'data'
     * we halen dus de image op en tonen het in een imageview
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(RESULT_OK == resultCode){
            var bitmap: Bitmap = data!!.extras.get("data") as Bitmap
            imageView.setImageBitmap(bitmap)
        }
        else{
            Toast.makeText(activity, "Geen foto genomen", Toast.LENGTH_SHORT).show()
        }
    }
}