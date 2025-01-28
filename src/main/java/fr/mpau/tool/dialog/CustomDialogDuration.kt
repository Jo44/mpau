package fr.mpau.tool.dialog

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import fr.mpau.R
import fr.mpau.tool.MinMaxFilter

/**
 * Tool - CustomDialogDuration
 *
 * Author: Jonathan
 * Created: 06/02/2018
 * Last Updated: 24/01/2025
 */
class CustomDialogDuration(context: Context?, private val myListener: MyOnClickListener) :
    AlertDialog(context) {

    /**
     * Attributs
     */

    private val tag = "CustomDialogDuration"

    /**
     * Initialisation
     *
     * @param savedInstanceState Bundle?
     */
    public override fun onCreate(savedInstanceState: Bundle?) {
        val maxDuration = 4320
        val minDuration = 1
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_number_pickup)
        val btn15 = findViewById<Button>(R.id.alertBtn15)
        val btn30 = findViewById<Button>(R.id.alertBtn30)
        val btn45 = findViewById<Button>(R.id.alertBtn45)
        val btn60 = findViewById<Button>(R.id.alertBtn60)
        val durationText = findViewById<EditText>(R.id.durationText)
        durationText.setFilters(arrayOf<InputFilter>(MinMaxFilter(minDuration, maxDuration)))
        val btnValid = findViewById<Button>(R.id.alertBtnValid)
        val btnCancel = findViewById<Button>(R.id.alertBtnCancel)
        val listener = View.OnClickListener { view ->
            when (view.id) {
                // 15 minutes
                R.id.alertBtn15 -> {
                    myListener.onButtonClick(15)
                    dismiss()
                }
                // 30 minutes
                R.id.alertBtn30 -> {
                    myListener.onButtonClick(30)
                    dismiss()
                }
                // 45 minutes
                R.id.alertBtn45 -> {
                    myListener.onButtonClick(45)
                    dismiss()
                }
                // 60 minutes
                R.id.alertBtn60 -> {
                    myListener.onButtonClick(60)
                    dismiss()
                }
                // Valider
                R.id.alertBtnValid -> {
                    try {
                        val selectDuration: Int = durationText.getText().toString().toInt()
                        myListener.onButtonClick(selectDuration)
                    } catch (nfe: NumberFormatException) {
                        Log.e(tag, "Erreur de saisie de la durée")
                    }
                    dismiss()
                }
                // Annuler
                R.id.alertBtnCancel -> dismiss()
            }
        }
        btn15.setOnClickListener(listener)
        btn30.setOnClickListener(listener)
        btn45.setOnClickListener(listener)
        btn60.setOnClickListener(listener)
        btnValid.setOnClickListener(listener)
        btnCancel.setOnClickListener(listener)
    }

    /**
     * Interface
     */
    interface MyOnClickListener {
        fun onButtonClick(selectDuration: Int)
    }

}