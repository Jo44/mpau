package fr.mpau.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import fr.mpau.R

/**
 * Activité Offline
 * -> redémarre l'application lors du clic sur 'Ré-essayer'
 * -> ferme l'application lors du clic sur 'Fermer'
 *
 * Author: Jonathan
 * Created: 19/11/2017
 * Last Updated: 27/01/2025
 */
class OfflineActivity : AppCompatActivity(), View.OnClickListener {

    /**
     * Attributs
     */

    private val tag = "OfflineActivity"
    private lateinit var btnRetry: Button
    private lateinit var btnClose: Button

    /**
     * Création de l'activité
     *
     * @param savedInstanceState Bundle?
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(tag, "Initialisation de l'activité Offline")
        // Initialisation du layout
        initLayout()
        // Initialise les listeners
        initListeners()
    }

    /**
     * Lors du clic sur un bouton, traitement en fonction du bouton
     *
     * @param view View
     */
    override fun onClick(view: View) {
        when (view.id) {
            // Lors du clic sur le bouton 'Ré-essayer'
            R.id.btnRetry -> {
                // Redémarre l'application
                restartApp()
            }
            // Lors du clic sur le bouton 'Fermer'
            R.id.btnClose -> closeActivity()
        }
    }

    /**
     * Initialise le layout et récupère les éléments
     */
    private fun initLayout() {
        // Initialise la view
        enableEdgeToEdge()
        setContentView(R.layout.activity_offline)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Récupère les éléments du layout
        btnRetry = findViewById(R.id.btnRetry)
        btnClose = findViewById(R.id.btnClose)
    }

    /**
     * Initialise les listeners
     */
    private fun initListeners() {
        // Listeners des boutons 'Réessayer' et 'Fermer'
        btnRetry.setOnClickListener(this)
        btnClose.setOnClickListener(this)
    }

    /**
     * Redémarre l'application
     */
    private fun restartApp() {
        Log.i(tag, "Redémarrage de l'application")
        val intent = baseContext.packageManager.getLaunchIntentForPackage(
            baseContext.packageName
        )
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        closeActivity()
    }

    /**
     * Ferme l'activité
     */
    private fun closeActivity() {
        finish()
    }

}