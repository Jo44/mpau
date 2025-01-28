package fr.mpau.tool.dialog

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import fr.mpau.R

/**
 * Tool - CustomDialogInformations
 *
 * Author: Jonathan
 * Created: 08/02/2018
 * Last Updated: 24/01/2025
 */
class CustomDialogInformations(context: Context?, private val mode: String?) :
    AlertDialog(context) {

    /**
     * Initialisation
     *
     * @param savedInstanceState Bundle?
     */
    public override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (mode != null && mode == "INTER") {
            setContentView(R.layout.dialog_informations_inter)
        } else {
            setContentView(R.layout.dialog_informations_timer)
        }
        val btnOk = findViewById<Button>(R.id.alertBtnOk)
        val listener = View.OnClickListener { view ->
            when (view.id) {
                R.id.alertBtnOk -> dismiss()
            }
        }
        btnOk.setOnClickListener(listener)
    }

}