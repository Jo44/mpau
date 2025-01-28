package fr.mpau.tool.dialog

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import fr.mpau.R

/**
 * Tool - CustomDialogNbInter
 *
 * Author: Jonathan
 * Created: 06/02/2018
 * Last Updated: 24/01/2025
 */
class CustomDialogNbInter(context: Context?, private val myListener: MyOnClickListener) :
    AlertDialog(context) {

    /**
     * Initialisation
     *
     * @param savedInstanceState Bundle?
     */
    public override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_inter_by_page)
        val radioNbInter = findViewById<RadioGroup>(R.id.radio_nb_inter)
        val btnValid = findViewById<Button>(R.id.alertBtnValid)
        val btnCancel = findViewById<Button>(R.id.alertBtnCancel)
        val listener = View.OnClickListener { view ->
            when (view.id) {
                // Valider
                R.id.alertBtnValid -> {
                    try {
                        // Récupération du radio button sélectionné et change le nombre d'interventions par page
                        val selectRadioId = radioNbInter.checkedRadioButtonId
                        val radioSelected = findViewById<RadioButton>(selectRadioId)
                        val selectNbInterByPage = radioSelected.text.toString().toInt()
                        myListener.onButtonClick(selectNbInterByPage)
                    } catch (_: NumberFormatException) {
                    }
                    dismiss()
                }
                // Annuler
                R.id.alertBtnCancel -> dismiss()
            }
        }
        btnValid.setOnClickListener(listener)
        btnCancel.setOnClickListener(listener)
    }

    /**
     * Interface
     */
    interface MyOnClickListener {
        fun onButtonClick(selectNbInterByPage: Int)
    }

}