package fr.mpau.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import fr.mpau.R;
import fr.mpau.enums.PaginationMode;
import fr.mpau.exceptions.FonctionnalException;
import fr.mpau.exceptions.TechnicalException;
import fr.mpau.models.Intervention;
import fr.mpau.models.User;
import fr.mpau.tools.UserSettings;
import fr.mpau.tools.dialog.CustomDialogInformations;
import fr.mpau.tools.dialog.CustomDialogNbInter;
import fr.mpau.tools.lists.InterventionsAdapter;
import fr.mpau.webservice.RequestInterventions;

/**
 * Activité View Interventions
 * -> affichage de la liste des interventions
 * -> ajouter une intervention (clic Ajouter)
 * -> modifier l'intervention (clic sur une intervention)
 * -> sélectionner le nombre d'interventions affiché par page (clic Options)
 * -> pop-up informations
 * -> réinitialise la liste au début après ajout / suppression
 * -> focus sur l'inter sélectionnée lors du retour si juste visualisation / modification
 * <p>
 * Author: Jonathan B.
 * Created: 18/01/2017
 * Last Updated: 28/02/2018
 */

public class ViewInterventionsActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private RequestInterventions requestInterventions;
    private PaginationMode mode;
    private int currentPage = 1;
    private int pageTotal = 0;
    private int interByPage = 10;
    private int idMaxInter = 0;
    private int totalInter = 0;
    private int selectedInterPosition = 0;
    private ArrayList<Intervention> listeInterventions = null;
    private ImageButton backArrow;
    private ImageButton addInterView;
    private ImageButton optionsView;
    private ImageButton informationsView;
    private TextView textViewEmpty;
    private ListView listViewInter;
    private RelativeLayout blocPagination;
    private ImageButton btnPrevious;
    private TextView paginationText;
    private ImageButton btnNext;

    /**
     * Création de l'activité
     *
     * @param savedInstanceState Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("[INFO]", "=> ViewInterventionsActivity");
        // Initialise le contrôleur de requête WS
        requestInterventions = new RequestInterventions();
        // Initialisation du layout
        initLayout();
        // Initialise les listeners
        initListeners();
        // Récupère la liste depuis de début
        getBasicCall();
    }

    /**
     * Focus sur l'activité
     */
    @Override
    protected void onResume() {
        super.onResume();
        // Traitement du Resume en fonction des paramètres Refresh et Restart
        resumeAction();
    }

    /**
     * Lors du clic sur un bouton, traitement en fonction du bouton
     *
     * @param view View
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // Lors du clic sur l'image 'Flèche retour'
            case R.id.backArrowView:
                closeActivity();
                break;
            // Lors du clic sur l'image 'Ajouter'
            case R.id.addInterView:
                addInterActivity();
                break;
            // Lors du clic sur l'image 'Options'
            case R.id.optionsView:
                dialogOptions();
                break;
            // Lors du clic sur l'image 'Informations'
            case R.id.informationsView:
                dialogInformations();
                break;
            // Lors du clic sur l'image 'Page précédente'
            case R.id.previousArrowPage:
                navigationPage(true);
                break;
            // Lors du clic sur l'image 'Page suivante'
            case R.id.nextArrowPage:
                navigationPage(false);
                break;
        }
    }

    /**
     * Lors du clic sur un élément de la ListView, ouvre l'activité Modif Intervention
     *
     * @param adapterView AdapterView
     * @param view        view
     * @param index       int
     * @param l           long
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
        // Récupère la position de l'intervention dans la liste pour repositionner la vue au bon endroit lors du re-focus sur l'activité
        selectedInterPosition = index;
        // Récupère l'identifiant de l'intervention cliquée
        int interId = listeInterventions.get(index).getInterId();
        // Lance l'activité Modif Intervention
        Intent intentViewInter = new Intent(ViewInterventionsActivity.this, ModifInterventionActivity.class);
        // Charge les paramètres dans le bundle
        intentViewInter.putExtra("interId", interId);
        startActivity(intentViewInter);
    }

    /**
     * Initialise le layout et récupère les éléments
     */
    private void initLayout() {
        // Charge le layout
        setContentView(R.layout.activity_view_interventions);
        // Récupère les éléments du layout
        backArrow = findViewById(R.id.backArrowView);
        addInterView = findViewById(R.id.addInterView);
        optionsView = findViewById(R.id.optionsView);
        informationsView = findViewById(R.id.informationsView);
        textViewEmpty = findViewById(R.id.textViewEmpty);
        listViewInter = findViewById(R.id.listViewInter);
        blocPagination = findViewById(R.id.blocPagination);
        btnPrevious = findViewById(R.id.previousArrowPage);
        paginationText = findViewById(R.id.paginationText);
        btnNext = findViewById(R.id.nextArrowPage);
    }

    /**
     * Initialise les listeners
     */
    private void initListeners() {
        // Initialise les listeners de tous les boutons et de la liste
        backArrow.setOnClickListener(this);
        addInterView.setOnClickListener(this);
        optionsView.setOnClickListener(this);
        informationsView.setOnClickListener(this);
        listViewInter.setOnItemClickListener(this);
        btnPrevious.setOnClickListener(this);
        btnNext.setOnClickListener(this);
    }

    /**
     * Lance l'activité d'ajout d'une intervention
     */
    private void addInterActivity() {
        // Lance l'activité Add Intervention
        Intent intentAddInter = new Intent(ViewInterventionsActivity.this, AddInterventionActivity.class);
        startActivity(intentAddInter);
    }

    /**
     * Initialise l'Alert Dialog de sélection du nombre d'interventions affichées par page
     */
    private void dialogOptions() {
        // Création du listener de la pop-up de sélection du nombre d'interventions affichées par page
        CustomDialogNbInter.myOnClickListener nbInterListener = new CustomDialogNbInter.myOnClickListener() {
            @Override
            public void onButtonClick(int selectNbInterByPage) {
                // Récupère le nombre d'intervention par page
                interByPage = selectNbInterByPage;
                // Met à jour la liste des interventions selon les nouveaux paramètres
                getBasicCall();
            }
        };
        // Création du dialog
        CustomDialogNbInter dialogNbInter = new CustomDialogNbInter(this, nbInterListener);
        dialogNbInter.show();
    }

    /**
     * Initialise l'Alert Dialog d'affichage des informations pour l'utilisateur
     */
    private void dialogInformations() {
        // Création du dialog
        CustomDialogInformations dialogInformations = new CustomDialogInformations(this, "INTER");
        dialogInformations.show();
    }

    /**
     * Récupère la liste depuis de début (appel de base WS)
     */
    private void getBasicCall() {
        // Récupère l'utilisateur
        User user = UserSettings.readUser(this);
        if (user != null) {
            idMaxInter = user.getUserInterIdMax();
            totalInter = user.getUserTotalInter();
        }
        // Réinitialise sur la première page
        currentPage = 1;
        // Appel WS de base soit :
        try {
            listeInterventions = requestInterventions.requestGetList(idMaxInter, interByPage, false);
            // Configuration de la ListView
            getMode();
            setDisplay();
        } catch (FonctionnalException fex) {
            listeInterventions = new ArrayList<>();
            // Configuration de la ListView
            getMode();
            setDisplay();
        } catch (TechnicalException fex) {
            redirectOffline();
        }

    }

    /**
     * Met à jour la liste actuelle
     */
    private void updateList() {
        // Appel WS en fonction du sens et de l'ID de la première intervention de la liste soit :
        try {
            listeInterventions = requestInterventions.requestGetList(listeInterventions.get(0).getInterId(), interByPage, false);
            // Configuration de la ListView
            getMode();
            setDisplay();
        } catch (FonctionnalException fex) {
            listeInterventions = new ArrayList<>();
            // Configuration de la ListView
            getMode();
            setDisplay();
        } catch (TechnicalException fex) {
            redirectOffline();
        }
    }

    /**
     * Met à jour les paramètres de récupération des interventions selon l'ordre de navigation voulu
     *
     * @param next boolean
     */
    private void navigationPage(boolean next) {
        int newInterId;
        // Met à jour les paramètres selon l'ordre de navigation voulu
        if (next) {
            currentPage--;
            newInterId = listeInterventions.get(0).getInterId() + 1;
        } else {
            currentPage++;
            newInterId = listeInterventions.get(listeInterventions.size() - 1).getInterId() - 1;
        }
        // Mise à jour de la liste
        onNavUpdate(next, newInterId);
    }

    /**
     * Met à jour la liste des interventions avec les nouveaux paramètres de navigation
     *
     * @param next       boolean
     * @param newInterId int
     */
    private void onNavUpdate(boolean next, int newInterId) {
        // Appel WS en fonction du sens et de l'ID soit :
        try {
            listeInterventions = requestInterventions.requestGetList(newInterId, interByPage, next);
            // Configuration de la ListView
            getMode();
            setDisplay();
        } catch (FonctionnalException fex) {
            listeInterventions = new ArrayList<>();
            // Configuration de la ListView
            getMode();
            setDisplay();
        } catch (TechnicalException fex) {
            redirectOffline();
        }
    }

    /**
     * Si besoin de Refresh / Restart, réinitialise les paramètres si besoin puis appel WS, sinon, repositionne juste la vue de la liste au bon endroit si nécessaire
     */
    private void resumeAction() {
        // Récupère le paramètre de Refresh de la liste
        boolean refresh = getRefreshStatus();
        // Récupère le paramètre de Restart de la liste
        boolean restart = getRestartStatus();
        if (refresh) {
            UserSettings.deleteRefreshListInter(this);
            if (restart) {
                UserSettings.deleteRestartListInter(this);
                currentPage = 0;
                selectedInterPosition = 0;
                getBasicCall();
            } else {
                updateList();
                replaceViewOnList();
            }
        } else {
            replaceViewOnList();
        }
    }

    /**
     * Récupère le paramètre déterminant si la liste a besoin d'être rafraichi (suite à un ajout, une modification ou une suppression) dans les Shared Preferences
     *
     * @return boolean
     */
    private boolean getRefreshStatus() {
        // Récupère le paramètre 'RefreshListInter' dans les SharedPreferences
        return UserSettings.readRefreshListInter(this);
    }

    /**
     * Récupère le paramètre déterminant si la liste a besoin d'être redémarré (suite à un ajout ou une suppression) dans les Shared Preferences
     *
     * @return boolean
     */
    private boolean getRestartStatus() {
        // Récupère le paramètre 'RestartListInter' dans les SharedPreferences
        return UserSettings.readRestartListInter(this);
    }

    /**
     * Repositionne la vue de la liste sur l'intervention sélectionnée
     */
    private void replaceViewOnList() {
        if (selectedInterPosition != 0) {
            if (listeInterventions != null && listeInterventions.size() > 0) {
                listViewInter.setSelection(selectedInterPosition);
            }
            selectedInterPosition = 0;
        }
    }

    /**
     * Détermine le mode d'affichage
     */
    private void getMode() {
        // Détermine le mode en fonction du nombre d'interventions et de la page actuelle
        if (totalInter == 0) {
            mode = PaginationMode.TYPE_PAS_AFFICHAGE;
        } else {
            if (currentPage == 1) {
                if (totalInter <= interByPage) {
                    mode = PaginationMode.PAGE_UNIQUE;
                } else {
                    mode = PaginationMode.PREMIERE_PAGE;
                }
            } else {
                if (totalInter <= (interByPage * currentPage)) {
                    mode = PaginationMode.DERNIERE_PAGE;
                } else {
                    mode = PaginationMode.PAGE_INTERMEDIAIRE;
                }
            }
            getTotalPage();
        }
    }

    /**
     * Détermine le nombre de page total
     */
    private void getTotalPage() {
        if (totalInter > 0) {
            if (totalInter % interByPage > 0) {
                pageTotal = (totalInter / interByPage) + 1;
            } else {
                pageTotal = (totalInter / interByPage);
            }
        }
    }

    /**
     * Gestion de l'affichage de la ListView
     */
    private void setDisplay() {
        // Si la liste n'est pas vide, affichage de la ListView
        if (listeInterventions != null && listeInterventions.size() > 0) {
            InterventionsAdapter adapter = new InterventionsAdapter(getApplicationContext(), R.layout.row_intervention, listeInterventions);
            listViewInter.setAdapter(adapter);
        }
        setDisplayMode();
    }

    /**
     * Configure l'affichage en fonction du mode d'affichage choisie
     */
    private void setDisplayMode() {
        // Met à jour la pagination (Page actuelle / Page totale)
        String paginationStr = getString(R.string.page) + " " + String.valueOf(currentPage) + " / " + String.valueOf(pageTotal);
        paginationText.setText(paginationStr);
        // Détermine si les éléments / composants sont visibles/actifs ou non
        switch (mode) {
            case TYPE_PAS_AFFICHAGE:
                textViewEmpty.setVisibility(View.VISIBLE);
                listViewInter.setVisibility(View.GONE);
                blocPagination.setVisibility(View.GONE);
                break;
            case PAGE_UNIQUE:
                textViewEmpty.setVisibility(View.GONE);
                listViewInter.setVisibility(View.VISIBLE);
                blocPagination.setVisibility(View.VISIBLE);
                btnPrevious.setClickable(false);
                btnPrevious.setEnabled(false);
                btnNext.setClickable(false);
                btnNext.setEnabled(false);
                break;
            case PREMIERE_PAGE:
                textViewEmpty.setVisibility(View.GONE);
                listViewInter.setVisibility(View.VISIBLE);
                blocPagination.setVisibility(View.VISIBLE);
                btnPrevious.setClickable(false);
                btnPrevious.setEnabled(false);
                btnNext.setClickable(true);
                btnNext.setEnabled(true);
                break;
            case PAGE_INTERMEDIAIRE:
                textViewEmpty.setVisibility(View.GONE);
                listViewInter.setVisibility(View.VISIBLE);
                blocPagination.setVisibility(View.VISIBLE);
                btnPrevious.setClickable(true);
                btnPrevious.setEnabled(true);
                btnNext.setClickable(true);
                btnNext.setEnabled(true);
                break;
            case DERNIERE_PAGE:
                textViewEmpty.setVisibility(View.GONE);
                listViewInter.setVisibility(View.VISIBLE);
                blocPagination.setVisibility(View.VISIBLE);
                btnPrevious.setClickable(true);
                btnPrevious.setEnabled(true);
                btnNext.setClickable(false);
                btnNext.setEnabled(false);
                break;
        }
    }

    /**
     * Redirige vers l'activité Offline
     */
    private void redirectOffline() {
        // Redirige vers l'activité Offline
        Intent intent = new Intent(ViewInterventionsActivity.this, OfflineActivity.class);
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
