package fr.mpau.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import fr.mpau.R;
import fr.mpau.exceptions.FonctionnalException;
import fr.mpau.exceptions.TechnicalException;
import fr.mpau.models.User;
import fr.mpau.tools.StringTools;
import fr.mpau.tools.UserSettings;
import fr.mpau.webservice.RequestUsers;

/**
 * Activité Login
 * -> si cookie valide présent et valide, redirige vers Home
 * -> sinon, traitement formulaire lors du clic sur 'Connexion'
 * <p>
 * Author: Jonathan B.
 * Created: 17/11/2017
 * Last Updated: 28/02/2018
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * Attributs
     */
    private final String INFO = "[INFO]";
    private final Context context = this;
    private RequestUsers requestUsers;
    private EditText username;
    private EditText password;
    private Button btnConnection;

    /**
     * Création de l'activité
     *
     * @param savedInstanceState Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(INFO, "=> LoginActivity");
        // Initialise le contrôleur de requête WS
        requestUsers = new RequestUsers();
        // Initialisation du layout
        initLayout();
        // Initialise les listeners
        initListeners();
    }

    /**
     * Focus sur l'activité
     */
    @Override
    protected void onResume() {
        super.onResume();
        // Essaye de récupérer l'utilisateur précédemment connecté
        tryToGetUserToConnect();
    }

    /**
     * Lors du clic sur le bouton 'Connexion'
     *
     * @param view View
     */
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnConnection) {
            tryToConnect();
        }
    }

    /**
     * Initialise le layout et récupère les éléments
     */
    private void initLayout() {
        // Charge le layout
        setContentView(R.layout.activity_login);
        // Récupère les éléments du layout
        username = findViewById(R.id.userName);
        password = findViewById(R.id.userPass);
        btnConnection = findViewById(R.id.btnConnection);
    }

    /**
     * Initialise les listeners
     */
    private void initListeners() {
        // Listener du bouton 'Connexion'
        btnConnection.setOnClickListener(this);
    }

    /**
     * Essaye de récupérer l'utilisateur en mémoire pour se connecter
     */
    private void tryToGetUserToConnect() {
        try {
            // Récupère l'utilisateur associé au cookie (renvoi Fonctionnal Exception si personne ne corresponds)
            User user = requestUsers.requestUser();
            // Ajout de l'utilisateur en mémoire
            UserSettings.setUser(user, context);
            // Redirige vers l'activité Home
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            Log.e(INFO, "--> connection par cookie");
            closeActivity();
        } catch (FonctionnalException fex) {
            // Supprime le cookie et l'utilisateur en mémoire
            UserSettings.deleteCookie(context);
            UserSettings.deleteUser(context);
        } catch (TechnicalException tex) {
            // Redirige vers l'activité Offline
            redirectOffline();
        }
    }

    /**
     * Lors de la tentative de connexion par identifiants
     */
    private void tryToConnect() {
        // Si le formulaire est valide
        if (controlForm()) {
            // Crypter mot de passe
            String cryptPass = StringTools.hashMD5(password.getText().toString().trim());
            // Génération du cookie à partir du user et cryptPass
            String cookie = StringTools.capitalizeFirstLetter(username.getText().toString().trim()) + ":" + cryptPass;
            // Ecriture du cookie en mémoire pour test de connexion
            UserSettings.setCookie(cookie, context);
            try {
                // Récupération de l'utilisateur par le cookie (renvoi Fonctionnal Exception si personne ne corresponds)
                User user = requestUsers.requestUser();
                // Ajout de l'utilisateur en mémoire
                UserSettings.setUser(user, context);
                // Redirect Home
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
                Log.e(INFO, "--> connection par identifiants");
                closeActivity();
            } catch (FonctionnalException fex) {
                // Supprime le cookie
                UserSettings.deleteCookie(context);
                // Si identifiants incorrects
                password.setText("");
                password.requestFocus();
                Toast.makeText(context, R.string.wrong_logins, Toast.LENGTH_SHORT).show();
            } catch (TechnicalException tex) {
                redirectOffline();
            }
        }
    }

    /**
     * Contrôle le formulaire de connexion
     */
    private boolean controlForm() {
        boolean valid = true;
        // Test si les champs Username / Password sont valides pour soumission
        if (username.getText().toString().trim().isEmpty()) {
            // Si champs Username non valide pour soumission
            valid = false;
            username.requestFocus();
            Toast.makeText(context, R.string.miss_username, Toast.LENGTH_SHORT).show();
        } else if (password.getText().toString().trim().isEmpty()) {
            // Si champs Password non valide pour soumission
            valid = false;
            password.requestFocus();
            Toast.makeText(context, R.string.miss_password, Toast.LENGTH_SHORT).show();
        }
        return valid;
    }

    /**
     * Redirige vers l'activité Offline
     */
    private void redirectOffline() {
        // Redirige vers l'activité Offline
        Intent intent = new Intent(LoginActivity.this, OfflineActivity.class);
        startActivity(intent);
        closeActivity();
    }

    /**
     * Ferme l'activité
     */
    private void closeActivity() {
        finish();
    }

}
