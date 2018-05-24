package fr.mpau.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import fr.mpau.R;
import fr.mpau.exceptions.FonctionnalException;
import fr.mpau.exceptions.TechnicalException;
import fr.mpau.models.Timer;
import fr.mpau.tools.NumTools;
import fr.mpau.tools.dialog.CustomDialogInformations;
import fr.mpau.tools.dialog.CustomDialogMessage;
import fr.mpau.tools.lists.TimersAdapter;
import fr.mpau.webservice.RequestTimers;

/**
 * Activité View Timers
 * -> affichage de la liste des timers
 * -> pop-up informations
 * -> suppression du timer (clic long)
 * <p>
 * Author: Jonathan B.
 * Created: 10/02/2018
 * Last Updated: 28/02/2018
 */

public class ViewTimersActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemLongClickListener, AdapterView.OnItemSelectedListener {

    /**
     * Attributs
     */
    private final String INFO = "[INFO]";
    private final Context context = this;
    private RequestTimers requestTimers;
    private ArrayList<Timer> listTimers = null;
    private int selectedMonth;
    private ImageButton backArrow;
    private ImageButton informationsView;
    private Spinner spinnerListMonthChoice;
    private TextView textViewEmpty;
    private RelativeLayout layoutViewTimers;
    private ListView listViewTimers;
    private TextView totalHours;

    /**
     * Création de l'activité
     *
     * @param savedInstanceState Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(INFO, "=> ViewTimersActivity");
        // Initialise le contrôleur de requête WS
        requestTimers = new RequestTimers();
        // Initialisation du layout
        initLayout();
        // Initialisation de la liste des mois disponibles à l'affichage
        initListe();
        // Initialise les listeners
        initListeners();
    }

    /**
     * Focus sur l'activité
     */
    @Override
    protected void onResume() {
        super.onResume();
        // Récupère la liste des timers automatiquement au début via méthode onItemSelected()
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
            // Lors du clic sur l'image 'Informations'
            case R.id.informationsView:
                dialogInformations();
                break;
        }
    }

    /**
     * Lors du clic long sur un timer de la liste, confirmation de suppression
     *
     * @param adapterView AdapterView
     * @param view        View
     * @param index       int
     * @param l           long
     * @return boolean
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int index, long l) {
        dialogTryToDeleteTimer(index);
        return false;
    }

    /**
     * Lors de la sélection d'un item de la liste des mois disponibles
     *
     * @param adapterView AdapterView
     * @param view        View
     * @param index       int
     * @param l           long
     */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int index, long l) {
        try {
            // Met à jour le mois voulu
            selectedMonth = -index;
            // Met à jour la liste des timers du mois sélectionné
            listTimers = requestTimers.requestGetTimers(selectedMonth);
            // Met à jour l'affichage
            setDisplay();
        } catch (FonctionnalException | TechnicalException e) {
            // Redirige vers l'activité Offline si problème
            redirectOffline();
        }
    }

    /**
     * Lors de la non-sélection d'un item de la liste des mois disponibles
     *
     * @param adapterView AdapterView
     */
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // Non utilisé
    }

    /**
     * Initialise le layout et récupère les éléments
     */
    private void initLayout() {
        // Charge le layout
        setContentView(R.layout.activity_view_timers);
        // Récupère les éléments du layout
        backArrow = findViewById(R.id.backArrowView);
        informationsView = findViewById(R.id.informationsView);
        spinnerListMonthChoice = findViewById(R.id.listMonthChoice);
        textViewEmpty = findViewById(R.id.textViewEmpty);
        layoutViewTimers = findViewById(R.id.layoutViewTimers);
        listViewTimers = findViewById(R.id.listViewTimers);
        totalHours = findViewById(R.id.totalHours);
        // Cache par défaut le bloc ViewTimers
        layoutViewTimers.setVisibility(View.GONE);
    }

    /**
     * Initialise la liste des mois disponibles à l'affichage et son adapter
     */
    private void initListe() {
        // Initialise la liste des mois disponibles (mois actuel + les 12 mois précédents)
        List<String> listMonth = new ArrayList<>();
        for (int i = 0; i > -13; i--) {
            Calendar now = Calendar.getInstance();
            now.add(Calendar.MONTH, i);
            String monthStr = new SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(now.getTime());
            listMonth.add(monthStr);
        }
        // Initialise l'Adapter de la liste
        ArrayAdapter adapterListMonth = new ArrayAdapter<>(this, R.layout.simple_custom_spinner_item, listMonth);
        adapterListMonth.setDropDownViewResource(R.layout.simple_custom_spinner_dropdown_item);
        spinnerListMonthChoice.setAdapter(adapterListMonth);
    }

    /**
     * Initialise les listeners
     */
    private void initListeners() {
        // Initialise les listeners de tous les boutons
        backArrow.setOnClickListener(this);
        informationsView.setOnClickListener(this);
        spinnerListMonthChoice.setOnItemSelectedListener(this);
        listViewTimers.setOnItemLongClickListener(this);
    }

    /**
     * Initialise l'affichage
     */
    private void setDisplay() {
        // Si la liste n'est pas vide
        if (listTimers != null && listTimers.size() > 0) {
            textViewEmpty.setVisibility(View.GONE);
            layoutViewTimers.setVisibility(View.VISIBLE);
            TimersAdapter adapter = new TimersAdapter(getApplicationContext(), R.layout.row_timer, listTimers);
            listViewTimers.setAdapter(adapter);
            // Détermine le temps de travail total de la liste
            String total = getTotalHours(listTimers);
            totalHours.setText(total);
        } else {
            // Si vide, affiche message utilisateur
            textViewEmpty.setVisibility(View.VISIBLE);
            layoutViewTimers.setVisibility(View.GONE);
        }
    }

    /**
     * Récupère le temps total de travail de toute la liste des timers au format 'HHHhMM' (ex: 138h04)
     *
     * @param listTimers List<Timer>
     * @return String
     */
    private String getTotalHours(List<Timer> listTimers) {
        String totalHours;
        long totalHoursLong = NumTools.getListTimerDuration(listTimers);
        // Transforme la différence de seconde à heure / minute
        long hour = totalHoursLong / 3600;
        long minute = (totalHoursLong % 3600) / 60;
        totalHours = String.valueOf(hour) + "h " + String.format(Locale.getDefault(), "%02d", minute) + "min";
        return totalHours;
    }

    /**
     * Initialise l'Alert Dialog d'affichage des informations pour l'utilisateur
     */
    private void dialogInformations() {
        // Création du dialog
        CustomDialogInformations dialogInformations = new CustomDialogInformations(this, "TIMER");
        dialogInformations.show();
    }

    /**
     * Initialise l'Alert Dialog de confirmation de suppression du timer
     */
    private void dialogTryToDeleteTimer(final int index) {
        // Création du listener de la pop-up de confirmation d'ajout
        CustomDialogMessage.myOnClickListener deleteListener = new CustomDialogMessage.myOnClickListener() {
            @Override
            public void onYesButtonClick() {
                // Essaye de supprimer le timer
                tryToDeleteTimer(index);
            }
        };
        // Création du dialog avec message
        String message = getResources().getString(R.string.confirm_delete_timer);
        CustomDialogMessage dialogMessage = new CustomDialogMessage(context, deleteListener, message);
        dialogMessage.show();
    }

    /**
     * Supprime le timer (à partir de l'index du timer dans la liste)
     *
     * @param index int
     */
    private void tryToDeleteTimer(int index) {
        // Récupère l'ID de la WorkDay
        int workdayId = listTimers.get(index).getTimerWorkday().getWdId();
        // Supprime le timer
        try {
            requestTimers.requestDeleteTimer(workdayId);
            Log.e(INFO, getResources().getString(R.string.deleted_inter));
            Toast.makeText(context, R.string.deleted_timer, Toast.LENGTH_SHORT).show();
            // Met à jour la liste des timers du mois sélectionné
            listTimers = requestTimers.requestGetTimers(selectedMonth);
            // Met à jour l'affichage
            setDisplay();
        } catch (FonctionnalException fex) {
            Log.e("[ERROR]", getResources().getString(R.string.error_delete_timer));
            Toast.makeText(context, R.string.error_delete_timer, Toast.LENGTH_SHORT).show();
        } catch (TechnicalException tex) {
            // Redirige vers l'activité Offline si problème
            redirectOffline();
        }
    }

    /**
     * Redirige vers l'activité Offline
     */
    private void redirectOffline() {
        // Redirige vers l'activité Offline
        Intent intent = new Intent(ViewTimersActivity.this, OfflineActivity.class);
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
