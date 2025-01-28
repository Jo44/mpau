package fr.mpau.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import fr.mpau.R
import fr.mpau.bean.Intervention
import fr.mpau.bean.User
import fr.mpau.enums.PaginationMode
import fr.mpau.exception.FonctionnalException
import fr.mpau.manager.InterventionManager
import fr.mpau.tool.UserSettings
import fr.mpau.tool.dialog.CustomDialogInformations
import fr.mpau.tool.dialog.CustomDialogNbInter
import fr.mpau.tool.list.InterventionsAdapter
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

/**
 * Activité View Interventions
 * -> affichage de la liste des interventions
 * -> ajouter une intervention (clic Ajouter)
 * -> modifier l'intervention (clic sur une intervention)
 * -> sélectionner le nombre d'interventions affiché par page (clic Options)
 * -> pop-up informations
 * -> réinitialise la liste au début après ajout / suppression
 * -> focus sur l'inter sélectionnée lors du retour si juste visualisation / modification
 *
 * Author: Jonathan
 * Created: 18/01/2017
 * Last Updated: 27/01/2025
 */
class ViewInterventionsActivity : AppCompatActivity(), View.OnClickListener,
    AdapterView.OnItemClickListener {

    /**
     * Attributs
     */

    private val tag = "ViewInterventionsActivity"
    private val context: Context = this
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private lateinit var mode: PaginationMode
    private var currentPage = 1
    private var pageTotal = 0
    private var interByPage = 10
    private var idMaxInter = 0
    private var totalInter = 0
    private var selectedInterPosition = 0
    private lateinit var listeInterventions: ArrayList<Intervention>
    private lateinit var backArrow: ImageButton
    private lateinit var addInterView: ImageButton
    private lateinit var optionsView: ImageButton
    private lateinit var informationsView: ImageButton
    private lateinit var layoutViewEmpty: LinearLayout
    private lateinit var layoutViewInters: RelativeLayout
    private lateinit var listViewInter: ListView
    private lateinit var layoutPagination: RelativeLayout
    private lateinit var btnPrevious: ImageButton
    private lateinit var paginationText: TextView
    private lateinit var btnNext: ImageButton

    /**
     * Création de l'activité
     *
     * @param savedInstanceState Bundle?
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(tag, "Initialisation de l'activité ViewInterventions")
        // Initialisation du layout
        initLayout()
        // Initialise les listeners
        initListeners()
        // Initilise la liste des interventions
        initInterList()
    }

    /**
     * Focus sur l'activité
     */
    override fun onResume() {
        super.onResume()
        // Traitement du Resume en fonction des paramètres Refresh et Restart
        resumeAction()
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
            // Lors du clic sur l'image 'Ajouter'
            R.id.addInterView -> openAddIntervention()
            // Lors du clic sur l'image 'Options'
            R.id.optionsView -> dialogOptions()
            // Lors du clic sur l'image 'Informations'
            R.id.informationsView -> dialogInformations()
            // Lors du clic sur l'image 'Page précédente'
            R.id.previousArrowPage -> navigationPage(true)
            // Lors du clic sur l'image 'Page suivante'
            R.id.nextArrowPage -> navigationPage(false)
        }
    }

    /**
     * Lors du clic sur un élément de la ListView, ouvre l'activité Modif Intervention
     *
     * @param adapterView AdapterView<*>?
     * @param view View?
     * @param index Int
     * @param l Long
     */
    override fun onItemClick(adapterView: AdapterView<*>?, view: View?, index: Int, l: Long) {
        // Récupère la position de l'intervention dans la liste pour repositionner la vue au bon endroit lors du re-focus sur l'activité
        selectedInterPosition = index
        // Récupère l'identifiant de l'intervention cliquée
        val id: Int = listeInterventions[index].id
        // Lance l'activité Modif Intervention
        openModifIntervention(id)
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
        setContentView(R.layout.activity_view_interventions)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Récupère les éléments du layout
        backArrow = findViewById(R.id.backArrowView)
        addInterView = findViewById(R.id.addInterView)
        optionsView = findViewById(R.id.optionsView)
        informationsView = findViewById(R.id.informationsView)
        layoutViewEmpty = findViewById(R.id.blocEmpty)
        layoutViewInters = findViewById(R.id.blocViewInters)
        listViewInter = findViewById(R.id.listViewInter)
        layoutPagination = findViewById(R.id.blocPagination)
        btnPrevious = findViewById(R.id.previousArrowPage)
        paginationText = findViewById(R.id.paginationText)
        btnNext = findViewById(R.id.nextArrowPage)
        // Affiche le bloc de liste vide
        layoutViewEmpty.visibility = View.VISIBLE
        // Cache par défaut le bloc ViewInters et le bloc Pagination
        layoutViewInters.visibility = View.GONE
        layoutPagination.visibility = View.GONE
    }

    /**
     * Initialise les listeners
     */
    private fun initListeners() {
        // Initialise les listeners de tous les boutons et de la liste
        backArrow.setOnClickListener(this)
        addInterView.setOnClickListener(this)
        optionsView.setOnClickListener(this)
        informationsView.setOnClickListener(this)
        listViewInter.onItemClickListener = this
        btnPrevious.setOnClickListener(this)
        btnNext.setOnClickListener(this)
    }

    /**
     * Initialise l'Alert Dialog de sélection du nombre d'interventions affichées par page
     */
    private fun dialogOptions() {
        // Création du listener de la pop-up de sélection du nombre d'interventions affichées par page
        val nbInterListener: CustomDialogNbInter.MyOnClickListener =
            object : CustomDialogNbInter.MyOnClickListener {
                override fun onButtonClick(selectNbInterByPage: Int) {
                    // Récupère le nombre d'intervention par page
                    interByPage = selectNbInterByPage
                    // Met à jour la liste des interventions selon les nouveaux paramètres
                    initInterList()
                }
            }
        // Création du dialog
        val dialogNbInter = CustomDialogNbInter(this, nbInterListener)
        dialogNbInter.show()
    }

    /**
     * Initialise l'Alert Dialog d'affichage des informations pour l'utilisateur
     */
    private fun dialogInformations() {
        // Création du dialog
        val dialogInformations = CustomDialogInformations(this, "INTER")
        dialogInformations.show()
    }

    /**
     * Récupère la liste des interventions depuis de début (appel de base WS)
     */
    private fun initInterList() {
        // Récupère les paramètres de l'utilisateur
        val user: User = UserSettings.readUser(this)!!
        idMaxInter = user.interIdMax
        totalInter = user.nbInter
        // Réinitialise sur la première page
        currentPage = 1
        // Scope de la coroutine
        lifecycleScope.launch {
            // Exception handler
            val exceptionHandler = CoroutineExceptionHandler { _, throwable -> throw throwable }
            try {
                // Si au moins une intervention
                if (idMaxInter > 0) {
                    // Requête au webservice
                    coroutineScope {
                        // Récupère la liste des interventions en fonction de l'ID
                        val deferred = async(exceptionHandler) {
                            val result: WSResult<JSONArray> = withContext(Dispatchers.IO) {
                                InterventionManager.getListInterventions(
                                    UserSettings.readCookie(context),
                                    idMaxInter,
                                    interByPage,
                                    false
                                )
                            }
                            InterventionManager.readGetListInterventions(result)
                        }
                        listeInterventions = deferred.await()!!
                        // Configuration de la ListView
                        getMode()
                        setDisplay()
                    }
                } else {
                    throw FonctionnalException("Pas d'intervention à afficher")
                }
            } catch (fex: FonctionnalException) {
                listeInterventions = ArrayList()
                // Configuration de la ListView
                getMode()
                setDisplay()
            } catch (ex: Exception) {
                Log.e(tag, ex.message!!)
                // Redirige vers l'activité Offline
                redirectToOffline()
            }
        }
    }

    /**
     * Met à jour la liste actuelle
     */
    private fun updateList() {
        // Scope de la coroutine
        lifecycleScope.launch {
            // Exception handler
            val exceptionHandler = CoroutineExceptionHandler { _, throwable -> throw throwable }
            try {
                // Requête au webservice
                coroutineScope {
                    // Récupère la liste des interventions en fonction de l'ID de la première intervention de la liste
                    val deferred = async(exceptionHandler) {
                        val result: WSResult<JSONArray> = withContext(Dispatchers.IO) {
                            InterventionManager.getListInterventions(
                                UserSettings.readCookie(context),
                                listeInterventions[0].id,
                                interByPage,
                                false
                            )
                        }
                        InterventionManager.readGetListInterventions(result)
                    }
                    listeInterventions = deferred.await()!!
                    // Configuration de la ListView
                    getMode()
                    setDisplay()
                }
            } catch (fex: FonctionnalException) {
                Log.e(tag, resources.getString(R.string.error_nav_inter))
                Toast.makeText(context, R.string.error_nav_inter, Toast.LENGTH_SHORT).show()
                listeInterventions = ArrayList()
                // Configuration de la ListView
                getMode()
                setDisplay()
            } catch (ex: Exception) {
                Log.e(tag, ex.message!!)
                // Redirige vers l'activité Offline
                redirectToOffline()
            }
        }
    }

    /**
     * Met à jour les paramètres de récupération des interventions selon l'ordre de navigation voulu
     *
     * @param next Boolean
     */
    private fun navigationPage(next: Boolean) {
        val newInterId: Int
        // Met à jour les paramètres selon l'ordre de navigation voulu
        if (next) {
            currentPage--
            newInterId = listeInterventions[0].id + 1
        } else {
            currentPage++
            newInterId = listeInterventions[listeInterventions.size - 1].id - 1
        }
        // Mise à jour de la liste
        onNavUpdate(next, newInterId)
    }

    /**
     * Met à jour la liste des interventions avec les nouveaux paramètres de navigation
     *
     * @param next Boolean
     * @param newInterId Int
     */
    private fun onNavUpdate(next: Boolean, newInterId: Int) {
        // Scope de la coroutine
        lifecycleScope.launch {
            // Exception handler
            val exceptionHandler = CoroutineExceptionHandler { _, throwable -> throw throwable }
            try {
                // Requête au webservice
                coroutineScope {
                    // Récupère la liste des interventions en fonction du sens et de l'ID
                    val deferred = async(exceptionHandler) {
                        val result: WSResult<JSONArray> = withContext(Dispatchers.IO) {
                            InterventionManager.getListInterventions(
                                UserSettings.readCookie(context),
                                newInterId,
                                interByPage,
                                next
                            )
                        }
                        InterventionManager.readGetListInterventions(result)
                    }
                    listeInterventions = deferred.await()!!
                    // Tri de la liste selon l'ordre de navigation
                    if (next) {
                        listeInterventions.reverse()
                    }
                    // Configuration de la ListView
                    getMode()
                    setDisplay()
                }
            } catch (fex: FonctionnalException) {
                Log.e(tag, resources.getString(R.string.error_nav_inter))
                Toast.makeText(context, R.string.error_nav_inter, Toast.LENGTH_SHORT).show()
                listeInterventions = ArrayList()
                // Configuration de la ListView
                getMode()
                setDisplay()
            } catch (ex: Exception) {
                Log.e(tag, ex.message!!)
                // Redirige vers l'activité Offline
                redirectToOffline()
            }
        }
    }

    /**
     * Si besoin de Refresh / Restart, réinitialise les paramètres si besoin puis appel WS, sinon, repositionne juste la vue de la liste au bon endroit si nécessaire
     */
    private fun resumeAction() {
        // Récupère les paramètres de Refresh / Restart de la liste
        val refresh: Boolean = getRefreshStatus()
        val restart: Boolean = getRestartStatus()
        // Selon les besoins
        if (refresh) {
            // Nécessite un refresh de la liste
            UserSettings.deleteRefreshListInter(this)
            if (restart) {
                // Nécessite un restart de la liste
                UserSettings.deleteRestartListInter(this)
                currentPage = 1
                selectedInterPosition = 0
                // Réinitialise la liste
                initInterList()
            } else {
                // Mise à jour de la liste
                updateList()
                // Repositionne la vue de la liste
                replaceViewOnList()
            }
        } else {
            // Repositionne la vue de la liste
            replaceViewOnList()
        }
    }

    /**
     * Récupère le paramètre déterminant si la liste a besoin d'être rafraichi (suite à un ajout, une modification ou une suppression) dans les Shared Preferences
     *
     * @return Boolean
     */
    private fun getRefreshStatus(): Boolean {
        // Récupère le paramètre 'RefreshListInter' dans les SharedPreferences
        return UserSettings.readRefreshListInter(this)
    }

    /**
     * Récupère le paramètre déterminant si la liste a besoin d'être redémarré (suite à un ajout ou une suppression) dans les Shared Preferences
     *
     * @return Boolean
     */
    private fun getRestartStatus(): Boolean {
        // Récupère le paramètre 'RestartListInter' dans les SharedPreferences
        return UserSettings.readRestartListInter(this)
    }

    /**
     * Repositionne la vue de la liste sur l'intervention sélectionnée
     */
    private fun replaceViewOnList() {
        if (selectedInterPosition != 0) {
            if (listeInterventions.isNotEmpty()) {
                listViewInter.setSelection(selectedInterPosition)
            }
            selectedInterPosition = 0
        }
    }

    /**
     * Détermine le mode d'affichage
     */
    private fun getMode() {
        // Détermine le mode en fonction du nombre d'interventions et de la page actuelle
        if (totalInter == 0) {
            mode = PaginationMode.PAS_AFFICHAGE
        } else {
            mode = if (currentPage == 1) {
                if (totalInter <= interByPage) {
                    PaginationMode.PAGE_UNIQUE
                } else {
                    PaginationMode.PREMIERE_PAGE
                }
            } else {
                if (totalInter <= (interByPage * currentPage)) {
                    PaginationMode.DERNIERE_PAGE
                } else {
                    PaginationMode.PAGE_INTERMEDIAIRE
                }
            }
            // Détermine le nombre de page total
            getTotalPage()
        }
    }

    /**
     * Détermine le nombre de page total
     */
    private fun getTotalPage() {
        if (totalInter > 0) {
            pageTotal = if (totalInter % interByPage > 0) {
                (totalInter / interByPage) + 1
            } else {
                (totalInter / interByPage)
            }
        }
    }

    /**
     * Gestion de l'affichage de la ListView
     */
    private fun setDisplay() {
        // Si la liste n'est pas vide, mise à jour de l'adapter
        if (listeInterventions.isNotEmpty()) {
            val adapter = InterventionsAdapter(
                applicationContext,
                R.layout.row_intervention,
                listeInterventions
            )
            listViewInter.adapter = adapter
        }
        // Configure l'affichage
        setDisplayMode()
    }

    /**
     * Configure l'affichage en fonction du mode d'affichage choisie
     */
    private fun setDisplayMode() {
        // Met à jour la pagination (Page actuelle / Page totale)
        val paginationStr =
            getString(R.string.page) + " " + currentPage.toString() + " / " + pageTotal.toString()
        paginationText.text = paginationStr
        // Détermine si les éléments / composants sont visibles/actifs ou non
        when (mode) {
            // Pas d'affichage
            PaginationMode.PAS_AFFICHAGE -> {
                layoutViewEmpty.visibility = View.VISIBLE
                layoutViewInters.visibility = View.GONE
                layoutPagination.visibility = View.GONE
            }
            // Page unique
            PaginationMode.PAGE_UNIQUE -> {
                layoutViewEmpty.visibility = View.GONE
                layoutViewInters.visibility = View.VISIBLE
                layoutPagination.visibility = View.VISIBLE
                btnPrevious.isClickable = false
                btnPrevious.isEnabled = false
                btnNext.isClickable = false
                btnNext.isEnabled = false
            }
            // Première page
            PaginationMode.PREMIERE_PAGE -> {
                layoutViewEmpty.visibility = View.GONE
                layoutViewInters.visibility = View.VISIBLE
                layoutPagination.visibility = View.VISIBLE
                btnPrevious.isClickable = false
                btnPrevious.isEnabled = false
                btnNext.isClickable = true
                btnNext.isEnabled = true
            }
            // Page intermédiaire
            PaginationMode.PAGE_INTERMEDIAIRE -> {
                layoutViewEmpty.visibility = View.GONE
                layoutViewInters.visibility = View.VISIBLE
                layoutPagination.visibility = View.VISIBLE
                btnPrevious.isClickable = true
                btnPrevious.isEnabled = true
                btnNext.isClickable = true
                btnNext.isEnabled = true
            }
            // Dernière page
            PaginationMode.DERNIERE_PAGE -> {
                layoutViewEmpty.visibility = View.GONE
                layoutViewInters.visibility = View.VISIBLE
                layoutPagination.visibility = View.VISIBLE
                btnPrevious.isClickable = true
                btnPrevious.isEnabled = true
                btnNext.isClickable = false
                btnNext.isEnabled = false
            }
        }
    }

    /**
     * Lance l'activité Add Intervention
     */
    private fun openAddIntervention() {
        Log.i(tag, "--> Ouverture de l'activité Add Intervention")
        val intentViewInter = Intent(
            this@ViewInterventionsActivity,
            AddInterventionActivity::class.java
        )
        startActivity(intentViewInter)
    }

    /**
     * Lance l'activité Mofif Intervention
     *
     * @param id Int
     */
    private fun openModifIntervention(id: Int) {
        Log.i(tag, "--> Ouverture de l'activité Modif Intervention {$id}")
        val intentViewInter = Intent(
            this@ViewInterventionsActivity,
            ModifInterventionActivity::class.java
        )
        // Charge l'ID de l'intervention dans l'intent
        intentViewInter.putExtra("id", id)
        startActivity(intentViewInter)
    }

    /**
     * Redirige vers l'activité Offline
     */
    private fun redirectToOffline() {
        Log.i(tag, "--> Redirection vers l'activité Offline")
        // Redirige vers l'activité Offline
        val intent = Intent(
            this@ViewInterventionsActivity,
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