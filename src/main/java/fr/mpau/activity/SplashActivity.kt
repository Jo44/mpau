package fr.mpau.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
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
import java.util.Timer
import java.util.TimerTask

/**
 * Activité Splash
 * -> si cookie valide présent, essaye de se connecter avec, puis redirection Home
 * -> sinon redirection Login
 *
 * Author: Jonathan
 * Created: 16/11/2017
 * Last Updated: 27/01/2025
 */
@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    /**
     * Attributs
     */

    private val tag = "SplashActivity"
    private val context: Context = this
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private lateinit var timer: Timer
    private lateinit var loadingCircle: ProgressBar

    /**
     * Création de l'activité
     *
     * @param savedInstanceState Bundle?
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(tag, "Initialisation de l'activité Splash")
        // Initialisation du layout
        initLayout()
        // Initialise l'affichage
        initDisplay()
    }

    /**
     * Focus sur l'activité
     */
    override fun onResume() {
        super.onResume()
        // Début du timer avant de lancer la suite
        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                // Essaye de récupérer l'utilisateur précédemment connecté
                tryToReconnectUser()
            }
        }, 1000)
    }

    /**
     * Lors de la fermeture de l'activité, stop le timer
     */
    override fun onStop() {
        super.onStop()
        timer.cancel()
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
        setContentView(R.layout.activity_splash)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Récupère la ProgressBar
        loadingCircle = findViewById(R.id.loadingCircle)
    }

    /**
     * Initialise l'affichage
     */
    private fun initDisplay() {
        // Affiche la ProgressBar
        loadingCircle.visibility = View.VISIBLE
    }

    /**
     * Essaye de récupérer l'utilisateur en mémoire pour se connecter
     */
    private fun tryToReconnectUser() {
        // Scope de la coroutine
        lifecycleScope.launch {
            // Exception handler
            val exceptionHandler = CoroutineExceptionHandler { _, throwable -> throw throwable }
            try {
                // Récupère le cookie en mémoire
                val cookie = UserSettings.readCookie(context)
                if (cookie.isEmpty()) {
                    throw FonctionnalException("Pas de cookie")
                } else {
                    // Requête au webservice
                    coroutineScope {
                        // Récupère l'utilisateur associé au cookie
                        val deferred = async(exceptionHandler) {
                            val result: WSResult<JSONObject> = withContext(Dispatchers.IO) {
                                UserManager.getUser(cookie)
                            }
                            UserManager.readGetUser(result)
                        }
                        val user: User? = deferred.await()
                        // En fonction du résultat
                        if (user != null) {
                            // Utilisateur récupéré
                            Log.i(tag, "--> connection par cookie")
                            // Redirige vers l'activité Home
                            redirectToHome(user)
                        } else {
                            // Utilisateur inconnu
                            throw FonctionnalException("Utilisateur inconnu")
                        }
                    }
                }
            } catch (fex: FonctionnalException) {
                // Redirige vers l'activité Login
                redirectToLogin()
            } catch (ex: Exception) {
                Log.e(tag, ex.message!!)
                // Redirige vers l'activité Offline
                redirectToOffline()
            }
        }
    }

    /**
     * Redirige vers l'activité Login
     */
    private fun redirectToLogin() {
        Log.i(tag, "--> Redirection vers l'activité Login")
        // Supprime le cookie et l'utilisateur en mémoire
        UserSettings.deleteCookie(context)
        UserSettings.deleteUser(context)
        // Redirige vers l'activité Login
        val intent = Intent(
            this@SplashActivity,
            LoginActivity::class.java
        )
        startActivity(intent)
        closeActivity()
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
            this@SplashActivity,
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
            this@SplashActivity,
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