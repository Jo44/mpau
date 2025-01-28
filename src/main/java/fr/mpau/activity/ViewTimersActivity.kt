package fr.mpau.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.RelativeLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import fr.mpau.R
import fr.mpau.bean.Timer
import fr.mpau.exception.FonctionnalException
import fr.mpau.manager.TimerManager
import fr.mpau.tool.NumTools
import fr.mpau.tool.UserSettings
import fr.mpau.tool.dialog.CustomDialogInformations
import fr.mpau.tool.dialog.CustomDialogMessage
import fr.mpau.tool.list.TimersAdapter
import fr.mpau.webservice.WSResult
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Activité View Timers
 * -> affichage de la liste des timers
 * -> pop-up informations
 * -> suppression du timer (clic long)
 *
 * Author: Jonathan
 * Created: 10/02/2018
 * Last Updated: 27/01/2025
 */
class ViewTimersActivity : AppCompatActivity(), AdapterView.OnItemLongClickListener,
    AdapterView.OnItemSelectedListener, View.OnClickListener {

    /**
     * Attributs
     */

    private val tag = "ViewTimersActivity"
    private val context: Context = this
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private lateinit var listTimers: ArrayList<Timer>
    private var selectedMonth: Int = 0
    private lateinit var backArrow: ImageButton
    private lateinit var informationsView: ImageButton
    private lateinit var spinnerListMonthChoice: Spinner
    private lateinit var layoutViewEmpty: LinearLayout
    private lateinit var layoutViewTimers: RelativeLayout
    private lateinit var listViewTimers: ListView
    private lateinit var layoutTotalDuration: LinearLayout
    private lateinit var totalHours: TextView

    /**
     * Création de l'activité
     *
     * @param savedInstanceState Bundle?
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(tag, "Initialisation de l'activité ViewTimers")
        // Initialisation du layout
        initLayout()
        // Initialise les listeners
        initListeners()
        // Initialisation de la liste des mois disponibles à l'affichage
        initMonthsList()
    }

    /**
     * Lors du clic sur un bouton, traitement en fonction du bouton
     *
     * @param view View
     */
    override fun onClick(view: View) {
        when (view.id) {
            // Lors du clic sur l'image 'Flèche retour'
            R.id.backArrowView -> closeActivity()
            // Lors du clic sur l'image 'Informations'
            R.id.informationsView -> dialogInformations()
        }
    }

    /**
     * Lors du clic long sur un timer de la liste, confirmation de suppression
     *
     * @param adapterView AdapterView<*>?
     * @param view View?
     * @param index Int
     * @param l Long
     * @return Boolean
     */
    override fun onItemLongClick(
        adapterView: AdapterView<*>?,
        view: View?,
        index: Int,
        l: Long
    ): Boolean {
        dialogTryToDeleteTimer(index)
        return false
    }

    /**
     * Lors de la sélection d'un item de la liste des mois disponibles
     *
     * @param adapterView AdapterView<*>?
     * @param view View?
     * @param index Int
     * @param l Long
     */
    override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, index: Int, l: Long) {
        // Scope de la coroutine
        lifecycleScope.launch {
            // Exception handler
            val exceptionHandler = CoroutineExceptionHandler { _, throwable -> throw throwable }
            try {
                // Met à jour le mois voulu
                selectedMonth = -index
                // Requête au webservice
                coroutineScope {
                    // Met à jour la liste des timers du mois sélectionné
                    val deferred = async(exceptionHandler) {
                        val result: WSResult<JSONArray> = withContext(Dispatchers.IO) {
                            TimerManager.getMonthTimers(
                                UserSettings.readCookie(context),
                                selectedMonth
                            )
                        }
                        TimerManager.readGetMonthTimers(result)
                    }
                    listTimers = deferred.await()!!
                    // Met à jour l'affichage
                    setDisplay()
                }
            } catch (ex: Exception) {
                Log.e(tag, ex.message!!)
                // Redirige vers l'activité Offline
                redirectToOffline()
            }
        }
    }

    /**
     * Lors de la non-sélection d'un item de la liste des mois disponibles
     *
     * @param adapterView AdapterView<*>?
     */
    override fun onNothingSelected(adapterView: AdapterView<*>?) {
        // Non utilisé
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
        setContentView(R.layout.activity_view_timers)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Récupère les éléments du layout
        backArrow = findViewById(R.id.backArrowView)
        informationsView = findViewById(R.id.informationsView)
        spinnerListMonthChoice = findViewById(R.id.listMonthChoice)
        layoutViewEmpty = findViewById(R.id.blocEmpty)
        layoutViewTimers = findViewById(R.id.blocViewTimers)
        listViewTimers = findViewById(R.id.listViewTimers)
        layoutTotalDuration = findViewById(R.id.blocTotalDuration)
        totalHours = findViewById(R.id.totalHours)
        // Affiche le bloc de liste vide
        layoutViewEmpty.visibility = View.VISIBLE
        // Cache par défaut le bloc ViewTimers et le bloc TotalDuration
        layoutViewTimers.visibility = View.GONE
        layoutTotalDuration.visibility = View.GONE
    }

    /**
     * Initialise les listeners
     */
    private fun initListeners() {
        // Initialise les listeners de tous les boutons et de la liste
        backArrow.setOnClickListener(this)
        informationsView.setOnClickListener(this)
        spinnerListMonthChoice.onItemSelectedListener = this
        listViewTimers.onItemLongClickListener = this
    }

    /**
     * Initialise la liste des mois disponibles à l'affichage et son adapter
     */
    private fun initMonthsList() {
        // Initialise la liste des mois disponibles (mois actuel + les 12 mois précédents)
        val listMonth: ArrayList<String?> = ArrayList()
        for (i in 0 downTo -13 + 1) {
            val now = Calendar.getInstance()
            now.add(Calendar.MONTH, i)
            val monthStr = SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(now.time)
            listMonth.add(monthStr)
        }
        // Initialise l'Adapter de la liste
        val adapterListMonth: ArrayAdapter<Any?> =
            ArrayAdapter(this, R.layout.row_spinner, listMonth as List<Any?>)
        adapterListMonth.setDropDownViewResource(R.layout.spinner)
        spinnerListMonthChoice.adapter = adapterListMonth
    }

    /**
     * Initialise l'affichage
     */
    private fun setDisplay() {
        if (listTimers.isNotEmpty()) {
            // Si la liste n'est pas vide, affiche le bloc ViewTimers et le bloc TotalDuration
            layoutViewEmpty.visibility = View.GONE
            layoutViewTimers.visibility = View.VISIBLE
            layoutTotalDuration.visibility = View.VISIBLE
            // Met à jour l'adapter de la liste
            val adapter = TimersAdapter(applicationContext, R.layout.row_timer, listTimers)
            listViewTimers.adapter = adapter
            // Détermine le temps de travail total de la liste
            val total: String = getTotalHours(listTimers)
            totalHours.text = total
        } else {
            // Si la liste est vide, Affiche le textView de liste vide
            layoutViewEmpty.visibility = View.VISIBLE
            layoutViewTimers.visibility = View.GONE
            layoutTotalDuration.visibility = View.GONE
        }
    }

    /**
     * Récupère le temps total de travail de toute la liste des timers au format 'HHHhMM' (ex: 138h04)
     *
     * @param listTimers List<Timer>
     * @return String
     */
    private fun getTotalHours(listTimers: List<Timer>): String {
        // Récupère la durée totale de travail de tous les timers
        val totalHoursSec: Long = NumTools.getListTimerDuration(listTimers)
        // Transforme la différence de seconde à heure / minute
        val hour = totalHoursSec / 3600
        val minute = (totalHoursSec % 3600) / 60
        return hour.toString() + "h " + String.format(Locale.getDefault(), "%02d", minute) + "min"
    }

    /**
     * Initialise l'Alert Dialog d'affichage des informations pour l'utilisateur
     */
    private fun dialogInformations() {
        // Création du dialog
        val dialogInformations = CustomDialogInformations(this, "TIMER")
        dialogInformations.show()
    }

    /**
     * Initialise l'Alert Dialog de confirmation de suppression du timer
     *
     * @param index Int
     */
    private fun dialogTryToDeleteTimer(index: Int) {
        // Création du listener de la pop-up de confirmation d'ajout
        val deleteListener: CustomDialogMessage.MyOnClickListener =
            CustomDialogMessage.MyOnClickListener { tryToDeleteTimer(index) }
        // Création du dialog avec message
        val message = resources.getString(R.string.confirm_delete_timer)
        val dialogMessage = CustomDialogMessage(context, deleteListener, message)
        dialogMessage.show()
    }

    /**
     * Supprime le timer (à partir de l'index du timer dans la liste)
     *
     * @param index Int
     */
    private fun tryToDeleteTimer(index: Int) {
        // Scope de la coroutine
        lifecycleScope.launch {
            // Exception handler
            val exceptionHandler = CoroutineExceptionHandler { _, throwable -> throw throwable }
            try {
                // Récupère l'ID de la WorkDay
                val workdayId: Int = listTimers[index].workday.id
                // Requête au webservice
                coroutineScope {
                    // Supprime le timer
                    val deferred = async(exceptionHandler) {
                        val result: WSResult<JSONObject> = withContext(Dispatchers.IO) {
                            TimerManager.deleteTimer(UserSettings.readCookie(context), workdayId)
                        }
                        TimerManager.readDeleteTimer(result)
                    }
                    val timer = deferred.await()
                    if (timer != null) {
                        // Message de succès
                        Log.i(tag, resources.getString(R.string.deleted_timer))
                        Toast.makeText(context, R.string.deleted_timer, Toast.LENGTH_SHORT).show()
                        // Met à jour la liste des timers du mois sélectionné
                        val result2: WSResult<JSONArray> = withContext(Dispatchers.IO) {
                            TimerManager.getMonthTimers(
                                UserSettings.readCookie(context),
                                selectedMonth
                            )
                        }
                        listTimers = TimerManager.readGetMonthTimers(result2)!!
                        // Met à jour l'affichage
                        setDisplay()
                    }
                }
            } catch (fex: FonctionnalException) {
                Log.e(tag, resources.getString(R.string.error_delete_timer))
                Toast.makeText(context, R.string.error_delete_timer, Toast.LENGTH_SHORT).show()
            } catch (ex: Exception) {
                Log.e(tag, ex.message!!)
                // Redirige vers l'activité Offline
                redirectToOffline()
            }
        }
    }

    /**
     * Redirige vers l'activité Offline
     */
    private fun redirectToOffline() {
        Log.i(tag, "--> Redirection vers l'activité Offline")
        // Redirige vers l'activité Offline
        val intent = Intent(
            this@ViewTimersActivity,
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