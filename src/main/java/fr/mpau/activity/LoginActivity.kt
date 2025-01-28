package fr.mpau.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import fr.mpau.R
import fr.mpau.bean.User
import fr.mpau.exception.FonctionnalException
import fr.mpau.manager.UserManager
import fr.mpau.tool.StringTools
import fr.mpau.tool.UserSettings
import fr.mpau.webservice.WSResult
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

/**
 * Activité Login
 * -> traitement formulaire lors du clic sur 'Connexion'
 *
 * Author: Jonathan
 * Created: 17/11/2017
 * Last Updated: 27/01/2025
 */
class LoginActivity : AppCompatActivity(), View.OnClickListener {

    /**
     * Attributs
     */

    private val tag = "LoginActivity"
    private val context: Context = this
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var btnConnection: Button

    /**
     * Création de l'activité
     *
     * @param savedInstanceState Bundle?
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(tag, "Initialisation de l'activité Login")
        // Initialisation du layout
        initLayout()
        // Initialise le listener
        initListener()
    }

    /**
     * Lors du clic sur le bouton 'Connexion'
     *
     * @param view View
     */
    override fun onClick(view: View) {
        // Lors du clic sur le bouton 'Connexion'
        if (view.id == R.id.btnConnection) {
            tryToConnect()
        }
    }

    /**
     * Destruction de l'activité
     */
    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

    /**
     * Initialise le layout et récupère les éléments
     */
    private fun initLayout() {
        // Initialise la view
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Récupère les éléments du layout
        username = findViewById(R.id.userName)
        password = findViewById(R.id.userPass)
        btnConnection = findViewById(R.id.btnConnection)
    }

    /**
     * Initialise le listener
     */
    private fun initListener() {
        // Listener du bouton 'Connexion'
        btnConnection.setOnClickListener(this)
    }

    /**
     * Essaye de se connecter à l'application via le formulaire de connexion
     */
    private fun tryToConnect() {
        // Scope de la coroutine
        lifecycleScope.launch {
            // Exception handler
            val exceptionHandler = CoroutineExceptionHandler { _, throwable -> throw throwable }
            try {
                // Si le formulaire est valide
                if (controlForm()) {
                    // Crypter mot de passe
                    val cryptPass: String =
                        StringTools.hashMD5(password.text.toString().trim { it <= ' ' })
                    // Génération du cookie à partir du user et cryptPass
                    val cookie: String = StringTools.capitalizeFirstLetter(
                        username.text.toString().trim { it <= ' ' }) + ":" + cryptPass
                    // Ecriture du cookie en mémoire pour test de connexion
                    UserSettings.setCookie(cookie, context)
                    // Requête au webservice
                    coroutineScope {
                        // Récupère l'utilisateur associé au cookie
                        val deferred = async(exceptionHandler) {
                            val result: WSResult<JSONObject> = withContext(Dispatchers.IO) {
                                UserManager.getUser(UserSettings.readCookie(context))
                            }
                            UserManager.readGetUser(result)
                        }
                        val user: User? = deferred.await()
                        if (user != null) {
                            // Utilisateur récupéré
                            Log.i(tag, "--> connection par identifiants")
                            // Redirige vers l'activité Home
                            redirectToHome(user)
                        } else {
                            // Utilisateur inconnu
                            throw FonctionnalException("Utilisateur inconnu")
                        }
                    }
                }
            } catch (fex: FonctionnalException) {
                // Supprime le cookie
                UserSettings.deleteCookie(context)
                // Init UI
                password.setText("")
                password.requestFocus()
                Toast.makeText(context, R.string.wrong_logins, Toast.LENGTH_SHORT).show()
            } catch (ex: Exception) {
                Log.e(tag, ex.message!!)
                // Redirige vers l'activité Offline
                redirectToOffline()
            }
        }
    }

    /**
     * Contrôle le formulaire de connexion
     *
     * @return Boolean
     */
    private fun controlForm(): Boolean {
        var valid = true
        // Test si les champs Username / Password sont valides pour soumission
        if (username.text.toString().trim { it <= ' ' }.isEmpty()) {
            // Si champs Username non valide pour soumission
            valid = false
            username.requestFocus()
            Toast.makeText(context, R.string.miss_username, Toast.LENGTH_SHORT).show()
        } else if (password.text.toString().trim { it <= ' ' }.isEmpty()) {
            // Si champs Password non valide pour soumission
            valid = false
            password.requestFocus()
            Toast.makeText(context, R.string.miss_password, Toast.LENGTH_SHORT).show()
        }
        return valid
    }

    /**
     * Redirige vers l'activité Home
     *
     * @param user User
     */
    private fun redirectToHome(user: User) {
        Log.i(tag, "--> Redirection vers l'activité Home")
        // Affiche le toast 'Connecté'
        Toast.makeText(context, R.string.connected, Toast.LENGTH_SHORT).show()
        // Ajout de l'utilisateur en mémoire
        UserSettings.setUser(user, context)
        // Redirige vers l'activité Home
        val intent = Intent(
            this@LoginActivity,
            HomeActivity::class.java
        )
        startActivity(intent)
        closeActivity()
    }

    /**
     * Redirige vers l'activité Offline
     */
    private fun redirectToOffline() {
        Log.i(tag, "--> Redirection vers l'activité Offline")
        // Redirige vers l'activité Offline
        val intent = Intent(
            this@LoginActivity,
            OfflineActivity::class.java
        )
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