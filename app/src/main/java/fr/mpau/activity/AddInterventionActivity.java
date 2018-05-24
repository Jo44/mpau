package fr.mpau.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import fr.mpau.R;
import fr.mpau.enums.AgePatientIntervention;
import fr.mpau.enums.SubtypeIntervention;
import fr.mpau.enums.TypeIntervention;
import fr.mpau.exceptions.FonctionnalException;
import fr.mpau.exceptions.TechnicalException;
import fr.mpau.models.User;
import fr.mpau.tools.NumTools;
import fr.mpau.tools.UserSettings;
import fr.mpau.tools.dialog.CustomDialogDuration;
import fr.mpau.tools.dialog.CustomDialogMessage;
import fr.mpau.tools.dialog.fragments.DatePickerFragment;
import fr.mpau.tools.dialog.fragments.TimePickerFragment;
import fr.mpau.tools.lists.CustomArrayAdapter;
import fr.mpau.webservice.RequestInterventions;
import fr.mpau.webservice.RequestUsers;

/**
 * Activité Add Intervention
 * -> traitement de l'ajout d'une intervention
 * <p>
 * Author: Jonathan B.
 * Created: 17/11/2017
 * Last Updated: 01/03/2018
 */

public class AddInterventionActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, View.OnClickListener, View.OnTouchListener {

    /**
     * Attributs
     */
    private final String INFO = "[INFO]";
    private final Context context = this;
    private RequestUsers requestUsers;
    private RequestInterventions requestInterventions;
    private InputMethodManager inputManager;
    private int duration = 0;
    private View anchor;
    private ImageButton backArrow;
    private Button btnSendInter;
    private Button btnDate;
    private Button btnTime;
    private Button btnDuration;
    private Spinner spinnerListType;
    private Spinner spinnerListSubType;
    private Spinner spinnerListPatientAge;
    private EditText sector;
    private CheckBox checkSmur;
    private EditText comment;

    /**
     * Création de l'activité
     *
     * @param savedInstanceState Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(INFO, "=> AddInterventionActivity");
        // Initialise le contrôleur de requête WS
        requestUsers = new RequestUsers();
        requestInterventions = new RequestInterventions();
        // Récupère le Input Method Manager pour gestion du clavier virtuel (caché le clavier visuel lors du clic sur une liste)
        inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        // Initialisation du layout
        initLayout();
        // Initialisation des listes (TypeInter / SousTypeInter / AgePatient)
        initListes();
        // Initialise les listeners
        initListeners();
        // Initialise l'affichage
        setDisplay();
    }

    /**
     * Focus sur l'activité
     */
    @Override
    protected void onResume() {
        super.onResume();
        // Initialise / met à jour l'affichage de la date et l'heure actuelle
        setDisplayDateTime();
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
            case R.id.backArrowAdd:
                closeActivity();
                break;
            // Lors du clic sur le bouton 'Envoyer'
            case R.id.btnSendInter:
                // Affiche le dialog d'ajout d'une intervention
                dialogTryToSendInter();
                break;
            // Lors du clic sur le bouton 'Date'
            case R.id.addInterDate:
                // Affiche le DatePicker
                DialogFragment newDateFragment = new DatePickerFragment();
                newDateFragment.show(getSupportFragmentManager(), "datePicker");
                break;
            // Lors du clic sur le bouton 'Heure'
            case R.id.addInterTime:
                // Affiche le TimePicker
                DialogFragment newTimeFragment = new TimePickerFragment();
                newTimeFragment.show(getSupportFragmentManager(), "timePicker");
                break;
            // Lors du clic sur le bouton 'Durée'
            case R.id.addInterDuration:
                // Affiche le dialog de sélection de la durée
                dialogDuration();
                break;
        }
    }

    /**
     * Lors du toucher d'une liste, cache le clavier virtuel
     *
     * @param view        View
     * @param motionEvent MotionEvent
     * @return boolean
     */
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (view.getId()) {
            // Lors du clic sur une liste
            case R.id.listType:
            case R.id.listSubType:
            case R.id.listPatientAge:
                // Désactive le clavier virtuel pour correctement afficher la liste
                inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                break;
        }
        return false;
    }

    /**
     * Lors du changement de la date via DatePicker, met à jour la date affichée
     *
     * @param datePicker DatePicker
     * @param year       int
     * @param month      int
     * @param day        int
     */
    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        // Met à jour le bouton avec la date choisie
        String date = String.format(Locale.getDefault(), "%02d", day) + "/" + String.format(Locale.getDefault(), "%02d", month + 1) + "/" + String.format(Locale.getDefault(), "%04d", year);
        btnDate.setText(date);
    }

    /**
     * Lors du changement de l'heure via TimePicker, met à jour l'heure affichée
     *
     * @param timePicker TimePicker
     * @param hour       int
     * @param minute     int
     */
    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        // Met à jour le bouton avec l'heure choisie
        String time = String.format(Locale.getDefault(), "%02d", hour) + ":" + String.format(Locale.getDefault(), "%02d", minute);
        btnTime.setText(time);
    }

    /**
     * Initialise le layout et récupère les éléments
     */
    private void initLayout() {
        // Charge le layout
        setContentView(R.layout.activity_add_intervention);
        // Récupère les éléments du layout
        anchor = findViewById(R.id.interAnchor);
        backArrow = findViewById(R.id.backArrowAdd);
        btnSendInter = findViewById(R.id.btnSendInter);
        btnDate = findViewById(R.id.addInterDate);
        btnTime = findViewById(R.id.addInterTime);
        btnDuration = findViewById(R.id.addInterDuration);
        spinnerListType = findViewById(R.id.listType);
        spinnerListSubType = findViewById(R.id.listSubType);
        spinnerListPatientAge = findViewById(R.id.listPatientAge);
        sector = findViewById(R.id.sector);
        checkSmur = findViewById(R.id.checkSmur);
        comment = findViewById(R.id.comment);
    }

    /**
     * Initialise les 3 listes (TypeInter / SousTypeInter / AgePatient) et leurs adapters avec les différentes valeurs
     */
    private void initListes() {
        // Initialise la liste des Types d'intervention
        List<String> listType = new ArrayList<>();
        listType.add(getString(R.string.unknown));
        listType.add(TypeIntervention.SOS.toString());
        listType.add(TypeIntervention.QUINZE.toString());
        // Initialise la liste des Sous-Types d'intervention
        List<String> listSubType = new ArrayList<>();
        listSubType.add(getString(R.string.unknown));
        listSubType.add(SubtypeIntervention.DETRESSE_CARDIO.toString());
        listSubType.add(SubtypeIntervention.DETRESSE_NEURO.toString());
        listSubType.add(SubtypeIntervention.DETRESSE_RESPI.toString());
        listSubType.add(SubtypeIntervention.PETIT_BOBO.toString());
        listSubType.add(SubtypeIntervention.PLAIE.toString());
        listSubType.add(SubtypeIntervention.PSYCHIATRIE.toString());
        listSubType.add(SubtypeIntervention.TRAUMATO.toString());
        listSubType.add(SubtypeIntervention.AUTRE.toString());
        // Initialise la liste des tranches d'Age des Patients d'intervention
        List<String> listPatientAge = new ArrayList<>();
        listPatientAge.add(getString(R.string.unknown));
        listPatientAge.add(AgePatientIntervention._00_10.toString());
        listPatientAge.add(AgePatientIntervention._10_20.toString());
        listPatientAge.add(AgePatientIntervention._20_30.toString());
        listPatientAge.add(AgePatientIntervention._30_40.toString());
        listPatientAge.add(AgePatientIntervention._40_50.toString());
        listPatientAge.add(AgePatientIntervention._50_60.toString());
        listPatientAge.add(AgePatientIntervention._60_70.toString());
        listPatientAge.add(AgePatientIntervention._70_80.toString());
        listPatientAge.add(AgePatientIntervention._80_90.toString());
        listPatientAge.add(AgePatientIntervention._90_100.toString());
        listPatientAge.add(AgePatientIntervention._PLUS_100.toString());
        // Initialise les Adapters des listes
        CustomArrayAdapter adapterListType = new CustomArrayAdapter<>(this, R.layout.simple_custom_spinner_item, listType);
        adapterListType.setDropDownViewResource(R.layout.simple_custom_spinner_dropdown_item);
        spinnerListType.setAdapter(adapterListType);
        CustomArrayAdapter adapterListSubtype = new CustomArrayAdapter<>(this, R.layout.simple_custom_spinner_item, listSubType);
        adapterListSubtype.setDropDownViewResource(R.layout.simple_custom_spinner_dropdown_item);
        spinnerListSubType.setAdapter(adapterListSubtype);
        CustomArrayAdapter adapterListPatientAge = new CustomArrayAdapter<>(this, R.layout.simple_custom_spinner_item, listPatientAge);
        adapterListPatientAge.setDropDownViewResource(R.layout.simple_custom_spinner_dropdown_item);
        spinnerListPatientAge.setAdapter(adapterListPatientAge);
    }

    /**
     * Initialise les listeners
     */
    private void initListeners() {
        // Initialise les listeners de tous les boutons
        backArrow.setOnClickListener(this);
        btnSendInter.setOnClickListener(this);
        btnDate.setOnClickListener(this);
        btnTime.setOnClickListener(this);
        btnDuration.setOnClickListener(this);
        spinnerListType.setOnTouchListener(this);
        spinnerListSubType.setOnTouchListener(this);
        spinnerListPatientAge.setOnTouchListener(this);
    }

    /**
     * Initialise l'affichage
     */
    private void setDisplay() {
        // Initialise le focus sur l'ancre (invisible) pour éviter le focus sur le premier EditText
        anchor.setFocusableInTouchMode(true);
        anchor.requestFocus();
    }

    /**
     * Initialise / met à jour l'affichage de la date et l'heure actuelle
     */
    private void setDisplayDateTime() {
        Calendar now = Calendar.getInstance();
        String date = String.format(Locale.getDefault(), "%02d", now.get(Calendar.DAY_OF_MONTH)) + "/" + String.format(Locale.getDefault(), "%02d", now.get(Calendar.MONTH) + 1) + "/" + String.format(Locale.getDefault(), "%04d", now.get(Calendar.YEAR));
        String time = String.format(Locale.getDefault(), "%02d", now.get(Calendar.HOUR_OF_DAY)) + ":" + String.format(Locale.getDefault(), "%02d", now.get(Calendar.MINUTE));
        Log.e(INFO, "Date/Heure: " + date + " - " + time);
        btnDate.setText(date);
        btnTime.setText(time);
    }


    /**
     * Initialise l'Alert Dialog d'ajout d'une intervention
     */
    private void dialogTryToSendInter() {
        // Création du listener de la pop-up de confirmation d'ajout
        CustomDialogMessage.myOnClickListener sendListener = new CustomDialogMessage.myOnClickListener() {
            @Override
            public void onYesButtonClick() {
                // Essaye d'ajouter l'intervention
                tryToSendInter();
            }
        };
        // Création du dialog avec message
        String message = getResources().getString(R.string.confirm_add_inter);
        CustomDialogMessage dialogMessage = new CustomDialogMessage(this, sendListener, message);
        dialogMessage.show();
    }

    /**
     * Vérifie si le formulaire est valide puis ajoute l'intervention
     */
    private void tryToSendInter() {
        // Valeur max/min de la durée d'une intervention
        int MAX_DURATION = 4320;
        int MIN_DURATION = 1;
        // Vérification du formulaire
        if (btnDate.getText().toString().trim().isEmpty()) {
            // Si date non renseignée
            Toast.makeText(context, R.string.miss_date, Toast.LENGTH_SHORT).show();
        } else if (btnTime.getText().toString().trim().isEmpty()) {
            // Si heure non renseignée
            Toast.makeText(context, R.string.miss_time, Toast.LENGTH_SHORT).show();
        } else if (duration < MIN_DURATION || duration > MAX_DURATION) {
            // Si durée non renseignée ou incorrect
            Toast.makeText(context, R.string.miss_duration, Toast.LENGTH_SHORT).show();
        } else if (spinnerListType.getSelectedItemPosition() == 0) {
            // Si type non renseigné
            Toast.makeText(context, R.string.miss_type, Toast.LENGTH_SHORT).show();
        } else if (spinnerListSubType.getSelectedItemPosition() == 0) {
            // Si sous-type non renseigné
            Toast.makeText(context, R.string.miss_subtype, Toast.LENGTH_SHORT).show();
        } else if (spinnerListPatientAge.getSelectedItemPosition() == 0) {
            // Si âge du patient non renseigné
            Toast.makeText(context, R.string.miss_patient_age, Toast.LENGTH_SHORT).show();
        } else if (sector.getText().toString().trim().isEmpty()) {
            // Si secteur non renseignée
            sector.requestFocus();
            Toast.makeText(context, R.string.miss_sector, Toast.LENGTH_SHORT).show();
        } else {
            // Reforme le timestamp à partir du bouton Date et Heure
            long timestamp = NumTools.getTimestampFromStringDateAndTime(btnDate.getText().toString().trim(), btnTime.getText().toString().trim());
            // Si le timestamp est valide
            if (timestamp != 0) {
                // Créé le JSON envoyé dans la requête
                JSONObject jsonSendObject = new JSONObject();
                String ERROR = "[ERROR]";
                try {
                    jsonSendObject.put("interDate", String.valueOf(timestamp));
                    jsonSendObject.put("interDuree", duration);
                    jsonSendObject.put("interSecteur", sector.getText().toString().trim());
                    jsonSendObject.put("interSmur", checkSmur.isChecked());
                    jsonSendObject.put("interTypeId", String.valueOf(spinnerListType.getSelectedItemPosition()));
                    jsonSendObject.put("interSoustypeId", String.valueOf(spinnerListSubType.getSelectedItemPosition()));
                    jsonSendObject.put("interAgepatientId", String.valueOf(spinnerListPatientAge.getSelectedItemPosition()));
                    jsonSendObject.put("interCommentaire", comment.getText().toString().trim());
                } catch (JSONException e) {
                    Log.e(ERROR, "Impossible de créer le JSON Object pour la requête");
                    jsonSendObject = null;
                }
                try {
                    // Ajoute l'intervention
                    boolean added = requestInterventions.requestAddIntervention(jsonSendObject);
                    // Si bien ajouté
                    if (added) {
                        // Met à jour l'utilisateur
                        UserSettings.deleteUser(context);
                        User user = requestUsers.requestUser();
                        // Si récupération ok
                        if (user != null && !user.getUserName().trim().isEmpty()) {
                            // Ajout de l'utilisateur en mémoire
                            UserSettings.setUser(user, context);
                        }
                        // Met à jour le paramètre 'RefreshListInter' pour la vue de la liste des interventions
                        UserSettings.setRefreshListInter(context);
                        // Met à jour le paramètre 'RestartListInter' pour la vue de la liste des interventions
                        UserSettings.setRestartListInter(context);
                        Log.e(INFO, getResources().getString(R.string.added_inter));
                        Toast.makeText(context, R.string.added_inter, Toast.LENGTH_SHORT).show();
                        // Termine l'activité
                        closeActivity();
                    }
                } catch (FonctionnalException fex) {
                    Log.e(ERROR, getResources().getString(R.string.error_add_inter));
                    Toast.makeText(context, R.string.error_add_inter, Toast.LENGTH_SHORT).show();
                } catch (TechnicalException tex) {
                    redirectOffline();
                }
            } else {
                // Si le timestamp n'est pas valide
                Toast.makeText(context, R.string.wrong_date_time, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Initialise l'Alert Dialog de sélection de la durée d'une intervention
     */
    private void dialogDuration() {
        // Création du listener de la pop-up de sélection de la durée de l'intervention
        CustomDialogDuration.myOnClickListener durationListener = new CustomDialogDuration.myOnClickListener() {
            @Override
            public void onButtonClick(int selectDuration) {
                // Récupère la durée
                duration = selectDuration;
                // Met à jour l'affichage du bouton durée
                String txtDuration = String.valueOf(duration) + " " + getResources().getString(R.string.min);
                btnDuration.setText(txtDuration);
            }
        };
        // Création du dialog
        CustomDialogDuration dialogDuration = new CustomDialogDuration(this, durationListener);
        dialogDuration.show();
    }

    /**
     * Redirige vers l'activité Offline
     */
    private void redirectOffline() {
        // Redirige vers l'activité Offline
        Intent intent = new Intent(AddInterventionActivity.this, OfflineActivity.class);
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
