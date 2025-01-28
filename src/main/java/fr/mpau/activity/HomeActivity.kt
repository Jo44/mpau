package fr.mpau.activity

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import fr.mpau.R
import fr.mpau.bean.TimerMode
import fr.mpau.bean.User
import fr.mpau.enums.TimerCommand
import fr.mpau.exception.FonctionnalException
import fr.mpau.manager.TimerManager
import fr.mpau.tool.NumTools
import fr.mpau.tool.StringTools
import fr.mpau.tool.UserSettings
import fr.mpau.tool.dialog.CustomDialogMessage
import fr.mpau.tool.dialog.fragment.DatePickerFragment
import fr.mpau.tool.dialog.fragment.TimePickerFragment
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
import java.util.Calendar
import java.util.Locale

/**
 * Activité Home
 * -> traitement de déconnexion (changement d'utilisateur)
 * -> traitement de la création / mise à jour / clôture du timer
 * -> bouton voir les timers
 * -> bouton voir les interventions
 *
 * Author: Jonathan
 * Created: 16/11/2017
 * Last Updated: 27/01/2025
 */
class HomeActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener, View.OnClickListener {

    /**
     * Attributs
     */

    private val tag = "HomeActivity"
    private val context: Context = this
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private lateinit var timerCommand: TimerCommand
    private lateinit var displayMode: String
    private var lastTimestamp: Long = 0L
    private lateinit var welcome: TextView
    private lateinit var btnLogOut: ImageButton
    private lateinit var statusTimer: TextView
    private lateinit var btnDate: Button
    private lateinit var btnTime: Button
    private lateinit var layoutStart: LinearLayout
    private lateinit var layoutPause: LinearLayout
    private lateinit var layoutRestart: LinearLayout
    private lateinit var layoutError: RelativeLayout
    private lateinit var blocStartBtnStart: Button
    private lateinit var blocPauseBtnPause: Button
    private lateinit var blocPauseBtnFinish: Button
    private lateinit var blocRestartBtnRestart: Button
    private lateinit var blocRestartBtnFinish: Button
    private lateinit var btnViewTimer: Button
    private lateinit var btnViewInter: Button

    /**
     * Création de l'activité
     *
     * @param savedInstanceState Bundle?
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(tag, "Initialisation de l'activité Home")
        // Initialisation du layout
        initLayout()
        // Initialise les listeners
        initListeners()
        // Initialise l'affichage
        initDisplay()
    }

    /**
     * Focus sur l'activité
     */
    override fun onResume() {
        super.onResume()
        // Récupére le mode du timer
        tryToGetTimerMode()
    }

    /**
     * Lors du clic sur un bouton, traitement en fonction du bouton
     *
     * @param view View
     */
    override fun onClick(view: View) {
        when (view.id) {
            // Lors du clic sur le bouton 'Déconnexion'
            R.id.btnLogOut -> {
                // Lancement du Dialog pour confirmer la déconnexion de l'utilisateur
                dialogForLogOut()
            }
            // Lors du clic sur le bouton 'Date'
            R.id.timerDate -> {
                // Affiche le DatePicker
                val newDateFragment: DialogFragment = DatePickerFragment()
                newDateFragment.show(supportFragmentManager, "datePicker")
            }
            // Lors du clic sur le bouton 'Time'
            R.id.timerTime -> {
                // Affiche le TimePicker
                val newTimeFragment: DialogFragment = TimePickerFragment()
                newTimeFragment.show(supportFragmentManager, "timePicker")
            }
            // Lors du clic sur le bouton 'Commencer'
            R.id.blocStartBtnStart -> {
                // Initialisation du TimerCommand
                timerCommand = TimerCommand.START_TIMER
                // Lancement du dialog pour confirmer l'action TimerCommand
                dialogForUpdateTimer(
                    resources.getString(R.string.confirm_start) + " ",
                    btnDate.getText().toString(),
                    (" " + resources.getString(R.string.at)) + " ",
                    btnTime.getText().toString()
                )
            }
            // Lors du clic sur le bouton 'Pause'
            R.id.blocPauseBtnPause -> {
                // Initialisation du TimerCommand
                timerCommand = TimerCommand.PAUSE_TIMER
                // Lancement du dialog pour confirmer l'action TimerCommand
                dialogForUpdateTimer(
                    resources.getString(R.string.confirm_pause) + " ",
                    btnDate.getText().toString(),
                    (" " + resources.getString(R.string.at)) + " ",
                    btnTime.getText().toString()
                )
            }
            // Lors du clic sur le bouton 'Reprendre'
            R.id.blocRestartBtnRestart -> {
                // Initialisation du TimerCommand
                timerCommand = TimerCommand.RESTART_TIMER
                // Lancement du dialog pour confirmer l'action TimerCommand
                dialogForUpdateTimer(
                    resources.getString(R.string.confirm_restart) + " ",
                    btnDate.getText().toString(),
                    (" " + resources.getString(R.string.at)) + " ",
                    btnTime.getText().toString()
                )
            }
            // Lors du clic sur le bouton 'Terminer'
            R.id.blocPauseBtnFinish, R.id.blocRestartBtnFinish -> {
                // Initialisation du TimerCommand
                timerCommand = TimerCommand.STOP_TIMER
                // Lancement du dialog pour confirmer l'action TimerCommand
                dialogForUpdateTimer(
                    resources.getString(R.string.confirm_finish) + " ",
                    btnDate.getText().toString(),
                    (" " + resources.getString(R.string.at)) + " ",
                    btnTime.getText().toString()
                )
            }
            // Lors du clic sur 'Mes relevés'
            R.id.btnViewTimer -> {
                // Lance l'activité View Timers
                openViewTimers()
            }
            // Lors du clic sur 'Mes interventions'
            R.id.btnViewInter -> {
                // Lance l'activité View Interventions
                openViewInterventions()
            }
        }
    }

    /**
     * Lors du changement de la date via DatePicker, met à jour la date affichée
     *
     * @param datePicker DatePicker?
     * @param year Int
     * @param month Int
     * @param day Int
     */
    override fun onDateSet(datePicker: DatePicker?, year: Int, month: Int, day: Int) {
        val date = String.format(Locale.getDefault(), "%02d", day) +
                "/" + String.format(Locale.getDefault(), "%02d", month + 1) +
                "/" + String.format(Locale.getDefault(), "%04d", year)
        btnDate.text = date
    }

    /**
     * Lors du changement de l'heure via TimePicker, met à jour l'heure affichée
     *
     * @param timePicker TimePicker?
     * @param hour Int
     * @param minute Int
     */
    override fun onTimeSet(timePicker: TimePicker?, hour: Int, minute: Int) {
        val time = String.format(Locale.getDefault(), "%02d", hour) +
                ":" + String.format(Locale.getDefault(), "%02d", minute)
        btnTime.text = time
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
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Récupère les éléments du layout
        welcome = findViewById(R.id.welcome)
        btnLogOut = findViewById(R.id.btnLogOut)
        statusTimer = findViewById(R.id.statusTimer)
        btnDate = findViewById(R.id.timerDate)
        btnTime = findViewById(R.id.timerTime)
        layoutStart = findViewById(R.id.blocStart)
        layoutPause = findViewById(R.id.blocPause)
        layoutRestart = findViewById(R.id.blocRestart)
        layoutError = findViewById(R.id.blocError)
        blocStartBtnStart = findViewById(R.id.blocStartBtnStart)
        blocPauseBtnPause = findViewById(R.id.blocPauseBtnPause)
        blocPauseBtnFinish = findViewById(R.id.blocPauseBtnFinish)
        blocRestartBtnRestart = findViewById(R.id.blocRestartBtnRestart)
        blocRestartBtnFinish = findViewById(R.id.blocRestartBtnFinish)
        btnViewTimer = findViewById(R.id.btnViewTimer)
        btnViewInter = findViewById(R.id.btnViewInter)
        // Cache les layouts de timer par défaut
        layoutStart.visibility = View.GONE
        layoutPause.visibility = View.GONE
        layoutRestart.visibility = View.GONE
        layoutError.visibility = View.GONE
    }

    /**
     * Initialise les listeners
     */
    private fun initListeners() {
        // Initialise les listeners de tous les boutons
        btnLogOut.setOnClickListener(this)
        btnDate.setOnClickListener(this)
        btnTime.setOnClickListener(this)
        blocStartBtnStart.setOnClickListener(this)
        blocPauseBtnPause.setOnClickListener(this)
        blocPauseBtnFinish.setOnClickListener(this)
        blocRestartBtnRestart.setOnClickListener(this)
        blocRestartBtnFinish.setOnClickListener(this)
        btnViewTimer.setOnClickListener(this)
        btnViewInter.setOnClickListener(this)
    }

    /**
     * Initialise l'affichage
     */
    private fun initDisplay() {
        // Récupère l'utilisateur en mémoire
        val user: User? = UserSettings.readUser(context)
        // Initialise le textview Welcome
        val welcomeStr =
            resources.getString(R.string.welcome) + " " + StringTools.capitalizeFirstLetter(user?.name)
        welcome.text = welcomeStr
    }

    /**
     * Affiche / cache les boutons de commande du Timer en fonction du mode et affiche la dernière date/heure pour rappel si nécessaire
     */
    private fun setDisplayMode() {
        var date = ""
        var time = ""
        if (lastTimestamp != 0L) {
            date = StringTools.getDateStringFromTimestamp(lastTimestamp)
            time = StringTools.getTimeStringFromTimestamp(lastTimestamp, ":")
        }
        when (displayMode) {
            // Mode START
            "START" -> {
                statusTimer.text = " "
                layoutStart.visibility = View.VISIBLE
                layoutPause.visibility = View.GONE
                layoutRestart.visibility = View.GONE
                layoutError.visibility = View.GONE
            }
            // Mode PAUSE
            "PAUSE" -> {
                val timerRunning =
                    "(" + resources.getString(R.string.timer_running) + " " + date + " " + resources.getString(
                        R.string.at
                    ) + " " + time + ")"
                statusTimer.text = timerRunning
                layoutStart.visibility = View.GONE
                layoutPause.visibility = View.VISIBLE
                layoutRestart.visibility = View.GONE
                layoutError.visibility = View.GONE
            }
            // Mode RESTART
            "RESTART" -> {
                val timerPausing =
                    "(" + resources.getString(R.string.timer_pausing) + " " + date + " " + resources.getString(
                        R.string.at
                    ) + " " + time + ")"
                statusTimer.text = timerPausing
                layoutStart.visibility = View.GONE
                layoutPause.visibility = View.GONE
                layoutRestart.visibility = View.VISIBLE
                layoutError.visibility = View.GONE
            }
            // Mode ERROR
            else -> {
                statusTimer.text = " "
                layoutStart.visibility = View.GONE
                layoutPause.visibility = View.GONE
                layoutRestart.visibility = View.GONE
                layoutError.visibility = View.VISIBLE
            }
        }
    }

    /**
     * Initialise / met à jour l'affichage de la date et l'heure actuelle
     */
    private fun setDisplayDateTime() {
        val now = Calendar.getInstance()
        val date = StringTools.getDateStringFromTimestamp(now.timeInMillis / 1000)
        val time = StringTools.getTimeStringFromTimestamp(now.timeInMillis / 1000, ":")
        Log.i(tag, "Date/Heure: $date - $time")
        btnDate.text = date
        btnTime.text = time
    }

    /**
     * Récupère le mode du timer puis met à jour l'affichage en fonction
     */
    private fun tryToGetTimerMode() {
        // Scope de la coroutine
        lifecycleScope.launch {
            // Exception handler
            val exceptionHandler = CoroutineExceptionHandler { _, throwable -> throw throwable }
            try {
                // Requête au webservice
                coroutineScope {
                    // Récupération du mode du timer
                    val deferred = async(exceptionHandler) {
                        val result: WSResult<JSONObject> = withContext(Dispatchers.IO) {
                            TimerManager.getTimerMode(UserSettings.readCookie(context))
                        }
                        TimerManager.readGetTimerMode(result)
                    }
                    val mode: TimerMode? = deferred.await()
                    if (mode != null) {
                        displayMode = mode.mode
                        lastTimestamp = mode.lastTimestamp
                        // Met à jour l'affichage du mode approprié
                        setDisplayMode()
                        // Met à jour l'affichage de la date et l'heure actuelle
                        setDisplayDateTime()
                    } else {
                        // TimerMode non reconnu
                        throw FonctionnalException("TimerMode non reconnu")
                    }
                }
            } catch (fex: FonctionnalException) {
                displayMode = "ERROR"
                lastTimestamp = 0L
                // Met à jour l'affichage du mode approprié
                setDisplayMode()
                // Met à jour l'affichage de la date et l'heure actuelle
                setDisplayDateTime()
            } catch (ex: Exception) {
                Log.e(tag, ex.message!!)
                // Redirige vers l'activité Offline
                redirectToOffline()
            }
        }
    }

    /**
     * Initialise l'Alert Dialog de confirmation de mise à jour du Timer
     *
     * @param confirmMessageStart String
     * @param confirmMessageDate String
     * @param confirmMessageMiddle String
     * @param confirmMessageTime String
     */
    private fun dialogForUpdateTimer(
        confirmMessageStart: String,
        confirmMessageDate: String,
        confirmMessageMiddle: String,
        confirmMessageTime: String
    ) {
        // Création du dialog avec message
        val dialogMessage = CustomDialogMessage(
            this,
            { tryToUpdateTimer() },
            confirmMessageStart,
            confirmMessageDate,
            confirmMessageMiddle,
            confirmMessageTime,
            " ?"
        )
        dialogMessage.show()
    }

    /**
     * Essaye de mettre à jour le timer (début/pause/reprise/fin) en fonction du TimerCommand
     */
    private fun tryToUpdateTimer() {
        // Initialisation des paramètres en fonction du TimerCommand
        lateinit var newMode: String
        lateinit var succesMessage: String
        lateinit var failMessage: String
        // Scope de la coroutine
        lifecycleScope.launch {
            // Exception handler
            val exceptionHandler = CoroutineExceptionHandler { _, throwable -> throw throwable }
            try {
                // Récupère le timestamp à partir de la date / heure
                val timestamp = getTimestamp()
                // Si le timestamp est valide
                if (timestamp != 0L) {
                    // Créé le JSON envoyé dans la requête
                    val jsonObject = JSONObject()
                    jsonObject.put("timestamp", timestamp.toString())
                    // Créé/Met à jour/Termine un timer en fonction des paramètres
                    when (timerCommand) {
                        // Début
                        TimerCommand.START_TIMER -> {
                            newMode = "PAUSE"
                            succesMessage = resources.getString(R.string.start_timer_msg)
                            failMessage = resources.getString(R.string.error_start)
                            // Requête au webservice
                            coroutineScope {
                                // Démarre le timer
                                val deferred = async(exceptionHandler) {
                                    val result: WSResult<JSONObject> = withContext(Dispatchers.IO) {
                                        TimerManager.postStartTimer(
                                            UserSettings.readCookie(context),
                                            jsonObject
                                        )
                                    }
                                    TimerManager.readPostStartTimer(result)
                                }
                                val timer = deferred.await()
                                if (timer != null) {
                                    // Timer démarré
                                    Log.i(tag, succesMessage)
                                    displayMode = newMode
                                    lastTimestamp = timestamp
                                    setDisplayMode()
                                    Toast.makeText(context, succesMessage, Toast.LENGTH_SHORT)
                                        .show()
                                } else {
                                    // Erreur lors du lancement de la journée
                                    throw FonctionnalException(failMessage)
                                }
                            }
                        }
                        // Pause
                        TimerCommand.PAUSE_TIMER -> {
                            newMode = "RESTART"
                            succesMessage = resources.getString(R.string.pause_timer_msg)
                            failMessage = resources.getString(R.string.error_pause)
                            // Requête au webservice
                            coroutineScope {
                                // Met en pause le timer
                                val deferred = async(exceptionHandler) {
                                    val result: WSResult<JSONObject> = withContext(Dispatchers.IO) {
                                        TimerManager.putPauseTimer(
                                            UserSettings.readCookie(context),
                                            jsonObject
                                        )
                                    }
                                    TimerManager.readPutPauseTimer(result)
                                }
                                val timer = deferred.await()
                                if (timer != null) {
                                    // Timer en pause
                                    Log.i(tag, succesMessage)
                                    displayMode = newMode
                                    lastTimestamp = timestamp
                                    setDisplayMode()
                                    Toast.makeText(context, succesMessage, Toast.LENGTH_SHORT)
                                        .show()
                                } else {
                                    // Erreur lors du lancement de la pause
                                    throw FonctionnalException(failMessage)
                                }
                            }
                        }
                        // Relance
                        TimerCommand.RESTART_TIMER -> {
                            newMode = "PAUSE"
                            succesMessage = resources.getString(R.string.restart_timer_msg)
                            failMessage = resources.getString(R.string.error_restart)
                            // Requête au webservice
                            coroutineScope {
                                // Relance le timer
                                val deferred = async(exceptionHandler) {
                                    val result: WSResult<JSONObject> = withContext(Dispatchers.IO) {
                                        TimerManager.putRestartTimer(
                                            UserSettings.readCookie(context),
                                            jsonObject
                                        )
                                    }
                                    TimerManager.readPutRestartTimer(result)
                                }
                                val timer = deferred.await()
                                if (timer != null) {
                                    // Timer relancé
                                    Log.i(tag, succesMessage)
                                    displayMode = newMode
                                    lastTimestamp = timestamp
                                    setDisplayMode()
                                    Toast.makeText(context, succesMessage, Toast.LENGTH_SHORT)
                                        .show()
                                } else {
                                    // Erreur lors du lancement de la reprise
                                    throw FonctionnalException(failMessage)
                                }
                            }
                        }
                        // Arrêt
                        TimerCommand.STOP_TIMER -> {
                            newMode = "START"
                            succesMessage = resources.getString(R.string.finish_timer_msg)
                            failMessage = resources.getString(R.string.error_finish)
                            // Requête au webservice
                            coroutineScope {
                                // Arrête le timer
                                val deferred = async(exceptionHandler) {
                                    val result: WSResult<JSONObject> = withContext(Dispatchers.IO) {
                                        TimerManager.putStopTimer(
                                            UserSettings.readCookie(context),
                                            jsonObject
                                        )
                                    }
                                    TimerManager.readPutStopTimer(result)
                                }
                                val timer = deferred.await()
                                if (timer != null) {
                                    // Timer arrêté
                                    Log.i(tag, succesMessage)
                                    displayMode = newMode
                                    lastTimestamp = timestamp
                                    setDisplayMode()
                                    Toast.makeText(context, succesMessage, Toast.LENGTH_SHORT)
                                        .show()
                                } else {
                                    // Erreur lors de la clôture de la journée
                                    throw FonctionnalException(failMessage)
                                }
                            }
                        }
                    }
                } else {
                    // Si le timestamp n'est pas valide
                    Toast.makeText(context, R.string.wrong_date_time, Toast.LENGTH_SHORT).show()
                }
            } catch (fex: FonctionnalException) {
                Log.e(tag, failMessage)
                Toast.makeText(context, failMessage, Toast.LENGTH_SHORT).show()
            } catch (ex: Exception) {
                Log.e(tag, ex.message!!)
                // Redirige vers l'activité Offline
                redirectToOffline()
            }
        }
    }

    /**
     * Récupère le timestamp à partir de la date / heure
     *
     * @return Long
     */
    private fun getTimestamp(): Long {
        var timestamp = 0L
        if (btnDate.text.toString().trim { it <= ' ' }.isNotEmpty() && btnTime.text.toString()
                .trim { it <= ' ' }.isNotEmpty()
        ) {
            timestamp = NumTools.getTimestampFromStringDateAndTime(
                btnDate.text.toString().trim { it <= ' ' },
                btnTime.text.toString().trim { it <= ' ' })
        }
        return timestamp
    }

    /**
     * Initialise l'Alert Dialog de la déconnexion
     */
    private fun dialogForLogOut() {
        // Création du listener de la pop-up de confirmation de déconnexion
        val logOutListener: CustomDialogMessage.MyOnClickListener =
            CustomDialogMessage.MyOnClickListener { logOut() }
        // Création du dialog avec message
        val message = resources.getString(R.string.confirm_logout)
        val dialogMessage = CustomDialogMessage(this, logOutListener, message)
        dialogMessage.show()
    }

    /**
     * Déconnecte l'utilisateur et redémarre l'application
     */
    private fun logOut() {
        // Supprime le cookie et l'utilisateur
        UserSettings.deleteCookie(context)
        UserSettings.deleteUser(context)
        // Redémarre l'application
        restartApp()
    }

    /**
     * Redémarre l'application
     */
    private fun restartApp() {
        Log.i(tag, "Redémarrage de l'application")
        // Redémarre l'application
        val intent = baseContext.packageManager.getLaunchIntentForPackage(
            baseContext.packageName
        )
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        closeActivity()
    }

    /**
     * Lance l'activité View Timers
     */
    private fun openViewTimers() {
        Log.i(tag, "--> Ouverture de l'activité View Timers")
        // Ouvre l'activité View Timers
        val intentViewTimer = Intent(
            this@HomeActivity,
            ViewTimersActivity::class.java
        )
        startActivity(intentViewTimer)
    }

    /**
     * Lance l'activité View Interventions
     */
    private fun openViewInterventions() {
        Log.i(tag, "--> Ouverture de l'activité View Interventions")
        // Ouvre l'activité View Interventions
        val intentViewInter = Intent(
            this@HomeActivity,
            ViewInterventionsActivity::class.java
        )
        startActivity(intentViewInter)
    }

    /**
     * Redirige vers l'activité Offline
     */
    private fun redirectToOffline() {
        Log.i(tag, "--> Redirection vers l'activité Offline")
        // Redirige vers l'activité Offline
        val intent = Intent(
            this@HomeActivity,
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