package fr.mpau.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.util.Timer;
import java.util.TimerTask;

import fr.mpau.R;

/**
 * Activité Splashscreen
 * <p>
 * Author: Jonathan B.
 * Created: 16/11/2017
 * Last Updated: 28/02/2018
 */

public class SplashActivity extends AppCompatActivity {

    /**
     * Attributs
     */
    private Timer timer;
    private ProgressBar spinner;

    /**
     * Création de l'activité
     *
     * @param savedInstanceState Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String INFO = "[INFO]";
        Log.e(INFO, "=> SplashActivity");
        // Initialisation du layout
        initLayout();
        // Initialise l'affichage
        setDisplay();
    }

    /**
     * Focus sur l'activité
     */
    @Override
    protected void onResume() {
        super.onResume();
        // Début du timer avant de lancer la suite
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // Redirige vers l'activité Login
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                closeActivity();
            }
        }, 2000);
    }

    /**
     * Lors de la fermeture de l'activité, stop le timer
     */
    @Override
    protected void onStop() {
        super.onStop();
        if (timer != null) {
            timer.cancel();
        }
    }

    /**
     * Initialise le layout et récupère les éléments
     */
    private void initLayout() {
        // Charge le layout
        setContentView(R.layout.activity_splash);
        // Récupère les éléments du layout
        spinner = findViewById(R.id.loadingCircle);
    }

    /**
     * Initialise l'affichage
     */
    private void setDisplay() {
        // Affiche la Loading Bar
        spinner.setVisibility(View.VISIBLE);
    }

    /**
     * Ferme l'activité
     */
    private void closeActivity() {
        finish();
    }

}

