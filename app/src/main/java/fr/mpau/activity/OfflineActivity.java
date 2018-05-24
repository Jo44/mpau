package fr.mpau.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import fr.mpau.R;

/**
 * Activité Offline
 * -> clic 'Réessayer' : relance l'application
 * -> clic 'Fermer' : ferme l'application
 * <p>
 * Author: Jonathan
 * Created: 19/11/2017
 * Last Updated: 06/02/2018
 */

public class OfflineActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * Attributs
     */
    private final String INFO = "[INFO]";
    private Button btnRetry;
    private Button btnClose;

    /**
     * Création de l'activité
     *
     * @param savedInstanceState Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(INFO, "=> Offline Activity");
        // Initialisation du layout
        initLayout();
        // Initialise les listeners
        initListeners();
    }

    /**
     * Lors du clic sur un bouton, traitement en fonction du bouton
     *
     * @param view View
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // Lors du clic sur le bouton 'Réessayer'
            case R.id.btnRetry:
                // Relance l'application
                Log.e(INFO, "Redémarrage de l'application");
                Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                }
                startActivity(intent);
                closeActivity();
                break;
            // Lors du clic sur le bouton 'Fermer'
            case R.id.btnClose:
                closeActivity();
                break;
        }
    }

    /**
     * Initialise le layout et récupère les éléments
     */
    private void initLayout() {
        // Charge le layout
        setContentView(R.layout.activity_offline);
        // Récupère les éléments du layout
        btnRetry = findViewById(R.id.btnRetry);
        btnClose = findViewById(R.id.btnClose);
    }

    /**
     * Initialise les listeners
     */
    private void initListeners() {
        // Listeners des boutons 'Retry' et 'Close'
        btnRetry.setOnClickListener(this);
        btnClose.setOnClickListener(this);
    }

    /**
     * Ferme l'activité
     */
    private void closeActivity() {
        finish();
    }

}
