package fr.mpau.activity

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import fr.mpau.R
import fr.mpau.bean.Intervention
import fr.mpau.bean.User
import fr.mpau.enums.AgePatientIntervention
import fr.mpau.enums.SubtypeIntervention
import fr.mpau.enums.TypeIntervention
import fr.mpau.exception.FonctionnalException
import fr.mpau.manager.InterventionManager
import fr.mpau.manager.UserManager
import fr.mpau.tool.NumTools
import fr.mpau.tool.StringTools
import fr.mpau.tool.UserSettings
import fr.mpau.tool.dialog.CustomDialogDuration
import fr.mpau.tool.dialog.CustomDialogMessage
import fr.mpau.tool.dialog.fragment.DatePickerFragment
import fr.mpau.tool.dialog.fragment.TimePickerFragment
import fr.mpau.tool.list.SpinnersAdapter
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
import java.util.Locale

/**
 * Activité Modif Intervention
 * -> traitement de la modification d'une intervention
 * -> traitement de la suppression d'une intervention
 *
 * Author: Jonathan
 * Created: 19/01/2018
 * Last Updated: 27/01/2025
 */
class ModifInterventionActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener, View.OnClickListener {

    /**
     * Attributs
     */

    private val tag = "ModifInterventionActivity"
    private val context: Context = this
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var interId = 0
    private lateinit var intervention: Intervention
    private var duration = 0
    private lateinit var anchor: View
    private lateinit var backArrow: ImageButton
    private lateinit var btnDeleteInter: ImageButton
    private lateinit var btnSaveInter: ImageButton
    private lateinit var btnDate: Button
    private lateinit var btnTime: Button
    private lateinit var btnDuration: Button
    private lateinit var spinnerListType: Spinner
    private lateinit var spinnerListSubType: Spinner
    private lateinit var spinnerListPatientAge: Spinner
    private lateinit var sector: EditText
    private lateinit var checkSmur: CheckBox
    private lateinit var comment: EditText

    /**
     * Création de l'activité
     *
     * @param savedInstanceState Bundle?
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(tag, "Initialisation de l'activité ModifIntervention")
        // Récupère l'ID de l'intervention dans l'intent
        val extras = intent.extras
        if (extras != null) {
            interId = extras.getInt("id")
        }
        // Initialisation du layout
        initLayout()
        // Initialise les listeners
        initListeners()
        // Initialisation des listes (TypeInter / SousTypeInter / AgePatient)
        initVariousLists()
        // Récupère l'intervention
        getIntervention()
    }

    /**
     * Lors du clic sur un bouton, traitement en fonction du bouton
     *
     * @param view View
     */
    override fun onClick(view: View) {
        when (view.id) {
            // Lors du clic sur l'image 'Flèche retour'
            R.id.backArrowModif -> closeActivity()
            // Lors du clic sur le bouton 'Supprimer'
            R.id.btnDeleteInter -> dialogTryToDeleteInter()
            // Lors du clic sur le bouton 'Modifier'
            R.id.btnSaveInter -> dialogTryToModifInter()
            // Lors du clic sur le bouton 'Date'
            R.id.modifInterDate -> {
                // Affiche le DatePicker
                val newDateFragment: DialogFragment = DatePickerFragment()
                newDateFragment.show(supportFragmentManager, "datePicker")
            }
            // Lors du clic sur le bouton 'Heure'
            R.id.modifInterTime -> {
                // Affiche le TimePicker
                val newTimeFragment: DialogFragment = TimePickerFragment()
                newTimeFragment.show(supportFragmentManager, "timePicker")
            }
            // Lors du clic sur le bouton 'Durée'
            R.id.modifInterDuration -> dialogDuration()
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
        // Met à jour le bouton avec la date choisie
        val date = String.format(Locale.getDefault(), "%02d", day) + "/" +
                String.format(Locale.getDefault(), "%02d", month + 1) + "/" +
                String.format(Locale.getDefault(), "%04d", year)
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
        // Met à jour le bouton avec l'heure choisie
        val time = String.format(Locale.getDefault(), "%02d", hour) + ":" +
                String.format(Locale.getDefault(), "%02d", minute)
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
        setContentView(R.layout.activity_modif_intervention)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Récupère les éléments du layout
        anchor = findViewById(R.id.interAnchor)
        backArrow = findViewById(R.id.backArrowModif)
        btnDeleteInter = findViewById(R.id.btnDeleteInter)
        btnSaveInter = findViewById(R.id.btnSaveInter)
        btnDate = findViewById(R.id.modifInterDate)
        btnTime = findViewById(R.id.modifInterTime)
        btnDuration = findViewById(R.id.modifInterDuration)
        spinnerListType = findViewById(R.id.listType)
        spinnerListSubType = findViewById(R.id.listSubType)
        spinnerListPatientAge = findViewById(R.id.listPatientAge)
        checkSmur = findViewById(R.id.checkSmur)
        sector = findViewById(R.id.sector)
        comment = findViewById(R.id.comment)
    }

    /**
     * Initialise les listeners
     */
    private fun initListeners() {
        // Initialise les listeners de tous les boutons
        backArrow.setOnClickListener(this)
        btnDeleteInter.setOnClickListener(this)
        btnSaveInter.setOnClickListener(this)
        btnDate.setOnClickListener(this)
        btnTime.setOnClickListener(this)
        btnDuration.setOnClickListener(this)
    }

    /**
     * Initialise les 3 listes (TypeInter / SousTypeInter / AgePatient) et leurs adapters avec les différentes valeurs
     */
    private fun initVariousLists() {
        // Initialise la liste des Types d'intervention
        val listType = listOf(
            getString(R.string.unknown),
            TypeIntervention.SOS.getValue(),
            TypeIntervention.QUINZE.getValue()
        )
        // Initialise la liste des Sous-Types d'intervention
        val listSubType = listOf(
            getString(R.string.unknown),
            SubtypeIntervention.DETRESSE_CARDIO.getValue(),
            SubtypeIntervention.DETRESSE_NEURO.getValue(),
            SubtypeIntervention.DETRESSE_RESPI.getValue(),
            SubtypeIntervention.PETIT_BOBO.getValue(),
            SubtypeIntervention.PLAIE.getValue(),
            SubtypeIntervention.PSYCHIATRIE.getValue(),
            SubtypeIntervention.TRAUMATO.getValue(),
            SubtypeIntervention.AUTRE.getValue()
        )
        // Initialise la liste des tranches d'Age des Patients d'intervention
        val listPatientAge = listOf(
            getString(R.string.unknown),
            AgePatientIntervention.A_00_10.getValue(),
            AgePatientIntervention.A_10_20.getValue(),
            AgePatientIntervention.A_20_30.getValue(),
            AgePatientIntervention.A_30_40.getValue(),
            AgePatientIntervention.A_40_50.getValue(),
            AgePatientIntervention.A_50_60.getValue(),
            AgePatientIntervention.A_60_70.getValue(),
            AgePatientIntervention.A_70_80.getValue(),
            AgePatientIntervention.A_80_90.getValue(),
            AgePatientIntervention.A_90_100.getValue(),
            AgePatientIntervention.A_PLUS_100.getValue()
        )
        // Initialise les adapters des listes
        val adapterListType: SpinnersAdapter<Any?> =
            SpinnersAdapter(this, R.layout.row_spinner, listType as List<Any?>)
        adapterListType.setDropDownViewResource(R.layout.spinner)
        spinnerListType.adapter = adapterListType
        val adapterListSubType: SpinnersAdapter<Any?> =
            SpinnersAdapter(this, R.layout.row_spinner, listSubType as List<Any?>)
        adapterListSubType.setDropDownViewResource(R.layout.spinner)
        spinnerListSubType.adapter = adapterListSubType
        val adapterListPatientAge: SpinnersAdapter<Any?> =
            SpinnersAdapter(this, R.layout.row_spinner, listPatientAge as List<Any?>)
        adapterListPatientAge.setDropDownViewResource(R.layout.spinner)
        spinnerListPatientAge.adapter = adapterListPatientAge
    }

    /**
     * Récupère l'intervention
     */
    private fun getIntervention() {
        // Scope de la coroutine
        lifecycleScope.launch {
            // Exception handler
            val exceptionHandler = CoroutineExceptionHandler { _, throwable -> throw throwable }
            try {
                // Requête au webservice
                coroutineScope {
                    val deferred = async(exceptionHandler) {
                        val result: WSResult<JSONObject> = withContext(Dispatchers.IO) {
                            InterventionManager.getIntervention(
                                UserSettings.readCookie(context),
                                interId
                            )
                        }
                        InterventionManager.readGetIntervention(result)
                    }
                    intervention = deferred.await()!!
                    // Initialise l'affichage
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
     * Initialise l'affichage
     */
    private fun setDisplay() {
        // Initialise le focus sur l'ancre (invisible) pour éviter le focus sur le premier EditText
        anchor.isFocusableInTouchMode = true
        anchor.requestFocus()
        // Convertis le timestamp de l'inter en string date et time
        val date: String = StringTools.getDateStringFromTimestamp(intervention.dateInter)
        val time: String = StringTools.getTimeStringFromTimestamp(intervention.dateInter, ":")
        // Remplie les champs du formulaire
        btnDate.text = date
        btnTime.text = time
        duration = intervention.duree
        btnDuration.text = getString(R.string.duration_text, duration)
        spinnerListType.setSelection(intervention.mainTypeId)
        spinnerListSubType.setSelection(intervention.sousTypeId)
        spinnerListPatientAge.setSelection(intervention.agePatientId)
        if (intervention.smur) {
            checkSmur.isChecked = true
        }
        sector.setText(intervention.secteur)
        comment.setText(intervention.commentaire)
    }

    /**
     * Initialise l'Alert Dialog de suppression d'une intervention
     */
    private fun dialogTryToDeleteInter() {
        // Création du listener de la pop-up de confirmation de suppression
        val deleteListener: CustomDialogMessage.MyOnClickListener =
            CustomDialogMessage.MyOnClickListener { tryToDeleteInter() }
        // Création du dialog avec message
        val message = resources.getString(R.string.confirm_delete_inter)
        val dialogMessage = CustomDialogMessage(this, deleteListener, message)
        dialogMessage.show()
    }

    /**
     * Supprime l'intervention
     */
    private fun tryToDeleteInter() {
        // Scope de la coroutine
        lifecycleScope.launch {
            // Exception handler
            val exceptionHandler = CoroutineExceptionHandler { _, throwable -> throw throwable }
            try {
                // Requête au webservice
                coroutineScope {
                    // Supprime l'intervention
                    val deferred = async(exceptionHandler) {
                        val result: WSResult<JSONObject> = withContext(Dispatchers.IO) {
                            InterventionManager.deleteIntervention(
                                UserSettings.readCookie(context),
                                interId
                            )
                        }
                        InterventionManager.readDeleteIntervention(result)
                    }
                    val deleted: Boolean = deferred.await()
                    // Si bien supprimée
                    if (deleted) {
                        // Met à jour l'utilisateur
                        UserSettings.deleteUser(context)
                        val result2: WSResult<JSONObject> = withContext(Dispatchers.IO) {
                            UserManager.getUser(UserSettings.readCookie(context))
                        }
                        val user: User = UserManager.readGetUser(result2)!!
                        // Ajout de l'utilisateur en mémoire
                        UserSettings.setUser(user, context)
                        // Met à jour le paramètre 'RefreshListInter' pour la vue de la liste des interventions
                        UserSettings.setRefreshListInter(context)
                        // Met à jour le paramètre 'RestartListInter' pour la vue de la liste des interventions
                        UserSettings.setRestartListInter(context)
                        // Message succès suppression
                        Log.i(tag, resources.getString(R.string.deleted_inter))
                        Toast.makeText(context, R.string.deleted_inter, Toast.LENGTH_SHORT).show()
                        // Termine l'activité
                        closeActivity()
                    } else {
                        // Erreur lors de la suppression
                        throw FonctionnalException("Impossible de supprimer l'intervention")
                    }
                }
            } catch (fex: FonctionnalException) {
                Log.e(tag, resources.getString(R.string.error_delete_inter))
                Toast.makeText(context, R.string.error_delete_inter, Toast.LENGTH_SHORT).show()
            } catch (ex: Exception) {
                Log.e(tag, ex.message!!)
                // Redirige vers l'activité Offline
                redirectToOffline()
            }
        }
    }

    /**
     * Initialise l'Alert Dialog de modification d'une intervention
     */
    private fun dialogTryToModifInter() {
        // Création du listener de la pop-up de confirmation de modification
        val modifListener: CustomDialogMessage.MyOnClickListener =
            CustomDialogMessage.MyOnClickListener { tryToModifInter() }
        // Création du dialog avec message
        val message = resources.getString(R.string.confirm_modif_inter)
        val dialogMessage = CustomDialogMessage(this, modifListener, message)
        dialogMessage.show()
    }

    /**
     * Vérifie si le formulaire est valide puis modifie l'intervention
     */
    private fun tryToModifInter() {
        // Scope de la coroutine
        lifecycleScope.launch {
            // Exception handler
            val exceptionHandler = CoroutineExceptionHandler { _, throwable -> throw throwable }
            try {
                // Si le formulaire est valide
                if (controlForm()) {
                    // Récupère le timestamp à partir de la date / heure
                    val timestamp = getTimestamp()
                    // Si le timestamp est valide
                    if (timestamp != 0L) {
                        // Créé le JSON envoyé dans la requête
                        val jsonObject = formToInterJSON(timestamp)
                        // Requête au webservice
                        coroutineScope {
                            // Modifie l'intervention
                            val deferred = async(exceptionHandler) {
                                val result: WSResult<JSONObject> = withContext(Dispatchers.IO) {
                                    InterventionManager.putIntervention(
                                        UserSettings.readCookie(context),
                                        interId,
                                        jsonObject
                                    )
                                }
                                InterventionManager.readPutIntervention(result)
                            }
                            val modified: Boolean = deferred.await()
                            // Si bien modifiée
                            if (modified) {
                                // Met à jour le paramètre 'RefreshListInter' pour la vue de la liste des interventions
                                UserSettings.setRefreshListInter(context)
                                // Message succès modification
                                Log.i(tag, resources.getString(R.string.modified_inter))
                                Toast.makeText(context, R.string.modified_inter, Toast.LENGTH_SHORT)
                                    .show()
                                // Termine l'activité
                                closeActivity()
                            } else {
                                // Erreur lors de la modification
                                throw FonctionnalException("Impossible de modifier l'intervention")
                            }
                        }
                    } else {
                        // Si le timestamp n'est pas valide
                        Toast.makeText(context, R.string.wrong_date_time, Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (fex: FonctionnalException) {
                Log.e(tag, resources.getString(R.string.error_modify_inter))
                Toast.makeText(context, R.string.error_modify_inter, Toast.LENGTH_SHORT).show()
            } catch (ex: Exception) {
                Log.e(tag, ex.message!!)
                // Redirige vers l'activité Offline
                redirectToOffline()
            }
        }
    }

    /**
     * Contrôle le formulaire d'ajout
     *
     * @return Boolean
     */
    private fun controlForm(): Boolean {
        var valid = true
        // Valeur max/min de la durée d'une intervention
        val maxDuration = 4320
        val minDuration = 1
        // Vérification du formulaire
        if (btnDate.text.toString().trim { it <= ' ' }.isEmpty()) {
            // Si date non renseignée
            Toast.makeText(context, R.string.miss_date, Toast.LENGTH_SHORT).show()
            valid = false
        } else if (btnTime.text.toString().trim { it <= ' ' }.isEmpty()) {
            // Si heure non renseignée
            Toast.makeText(context, R.string.miss_time, Toast.LENGTH_SHORT).show()
            valid = false
        } else if (duration < minDuration || duration > maxDuration) {
            // Si durée non renseignée ou incorrect
            Toast.makeText(context, R.string.miss_duration, Toast.LENGTH_SHORT).show()
            valid = false
        } else if (spinnerListType.selectedItemPosition == 0) {
            // Si type non renseigné
            Toast.makeText(context, R.string.miss_type, Toast.LENGTH_SHORT).show()
            valid = false
        } else if (spinnerListSubType.selectedItemPosition == 0) {
            // Si sous-type non renseigné
            Toast.makeText(context, R.string.miss_subtype, Toast.LENGTH_SHORT).show()
            valid = false
        } else if (spinnerListPatientAge.selectedItemPosition == 0) {
            // Si âge du patient non renseigné
            Toast.makeText(context, R.string.miss_patient_age, Toast.LENGTH_SHORT).show()
            valid = false
        } else if (sector.text.toString().trim { it <= ' ' }.isEmpty()) {
            // Si secteur non renseignée
            sector.requestFocus()
            Toast.makeText(context, R.string.miss_sector, Toast.LENGTH_SHORT).show()
            valid = false
        }
        return valid
    }

    /**
     * Récupère le timestamp à partir de la date / heure
     *
     * @return timestamp Long
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
     * Créé le JSON à partir des données du formulaire d'ajout d'une intervention
     *
     * @param timestamp Long
     * @return JSONObject
     */
    private fun formToInterJSON(timestamp: Long): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.put("dateInter", timestamp.toString())
        jsonObject.put("duree", duration)
        jsonObject.put("secteur", sector.text.toString().trim { it <= ' ' })
        jsonObject.put("smur", checkSmur.isChecked)
        jsonObject.put("mainTypeId", spinnerListType.selectedItemPosition.toString())
        jsonObject.put("sousTypeId", spinnerListSubType.selectedItemPosition.toString())
        jsonObject.put("agePatientId", spinnerListPatientAge.selectedItemPosition.toString())
        jsonObject.put("commentaire", comment.text.toString().trim { it <= ' ' })
        return jsonObject
    }

    /**
     * Initialise l'Alert Dialog de sélection de la durée d'une intervention
     */
    private fun dialogDuration() {
        // Création du listener de la pop-up de sélection de la durée de l'intervention
        val durationListener: CustomDialogDuration.MyOnClickListener =
            object : CustomDialogDuration.MyOnClickListener {
                override fun onButtonClick(selectDuration: Int) {
                    // Récupère la durée
                    duration = selectDuration
                    // Met à jour l'affichage du bouton durée
                    btnDuration.text = getString(R.string.duration_text, duration)
                }
            }
        // Création du dialog
        val dialogDuration = CustomDialogDuration(this, durationListener)
        dialogDuration.show()
    }

    /**
     * Redirige vers l'activité Offline
     */
    private fun redirectToOffline() {
        Log.i(tag, "--> Redirection vers l'activité Offline")
        // Redirige vers l'activité Offline
        val intent = Intent(
            this@ModifInterventionActivity,
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