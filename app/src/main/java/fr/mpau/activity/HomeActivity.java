package fr.mpau.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ButtonBarLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Locale;

import fr.mpau.R;
import fr.mpau.enums.TimerCommand;
import fr.mpau.exceptions.FonctionnalException;
import fr.mpau.exceptions.TechnicalException;
import fr.mpau.models.TimerMode;
import fr.mpau.models.User;
import fr.mpau.tools.NumTools;
import fr.mpau.tools.StringTools;
import fr.mpau.tools.UserSettings;
import fr.mpau.tools.dialog.CustomDialogMessage;
import fr.mpau.tools.dialog.fragments.DatePickerFragment;
import fr.mpau.tools.dialog.fragments.TimePickerFragment;
import fr.mpau.webservice.RequestTimers;

/**
 * Activité Home
 * -> traitement de déconnexion (changement d'utilisateur)
 * -> traitement de la création / mise à jour / clôture du timer
 * -> bouton voir les timers
 * -> bouton voir les interventions
 * <p>
 * Author: Jonathan B.
 * Created: 16/11/2017
 * Last Updated: 28/02/2018
 */

public class HomeActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, View.OnClickListener {

    /**
     * Attributs
     */
    private final String INFO = "[INFO]";
    private final Context context = this;
    private RequestTimers requestTimers;
    private TimerCommand timerCommand;
    private String displayMode;
    private long lastTimestamp;
    private TextView welcome;
    private Button btnDisconnection;
    private TextView statusTimer;
    private Button btnDate;
    private Button btnTime;
    private ButtonBarLayout layoutStart;
    private ButtonBarLayout layoutPause;
    private ButtonBarLayout layoutRestart;
    private RelativeLayout layoutError;
    private Button blocStartBtnStart;
    private Button blocPauseBtnPause;
    private Button blocPauseBtnFinish;
    private Button blocRestartBtnRestart;
    private Button blocRestartBtnFinish;
    private Button btnViewTimer;
    private Button btnViewInter;

    /**
     * Création de l'activité
     *
     * @param savedInstanceState Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(INFO, "=> HomeActivity");
        // Initialise le contrôleur de requête WS
        requestTimers = new RequestTimers();
        // Initialisation du layout
        initLayout();
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
        // Récupére le mode du timer
        tryToGetTimerMode();
    }

    /**
     * Lors du clic sur un bouton, traitement en fonction du bouton
     *
     * @param view View
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // Lors du clic sur le bouton 'Déconnexion'
            case R.id.btnDisconnection:
                // Lancement du Dialog pour confirmer la déconnexion de l'utilisateur
                dialogForDisconnection();
                break;
            // Lors du clic sur le bouton 'Date'
            case R.id.timerDate:
                // Affiche le DatePicker
                DialogFragment newDateFragment = new DatePickerFragment();
                newDateFragment.show(getSupportFragmentManager(), "datePicker");
                break;
            // Lors du clic sur le bouton 'Time'
            case R.id.timerTime:
                // Affiche le TimePicker
                DialogFragment newTimeFragment = new TimePickerFragment();
                newTimeFragment.show(getSupportFragmentManager(), "timePicker");
                break;
            // Lors du clic sur le bouton 'Commencer'
            case R.id.blocStartBtnStart:
                // Initialisation du TimerCommand
                timerCommand = TimerCommand.START_TIMER;
                // Lancement du Dialog pour confirmer l'action dépendante du TimerCommand avec le message de confirmation en paramètre
                dialogForUpdateTimer(getResources().getString(R.string.confirm_start) + " ", btnDate.getText().toString(), " " + getResources().getString(R.string.at) + " ", btnTime.getText().toString());
                break;
            // Lors du clic sur le bouton 'Pause'
            case R.id.blocPauseBtnPause:
                // Initialisation du TimerCommand
                timerCommand = TimerCommand.PAUSE_TIMER;
                // Lancement du Dialog pour confirmer l'action dépendante du TimerCommand avec le message de confirmation en paramètre
                dialogForUpdateTimer(getResources().getString(R.string.confirm_pause) + " ", btnDate.getText().toString(), " " + getResources().getString(R.string.at) + " ", btnTime.getText().toString());
                break;
            // Lors du clic sur le bouton 'Reprendre'
            case R.id.blocRestartBtnRestart:
                // Initialisation du TimerCommand
                timerCommand = TimerCommand.RESTART_TIMER;
                // Lancement du Dialog pour confirmer l'action dépendante du TimerCommand avec le message de confirmation en paramètre
                dialogForUpdateTimer(getResources().getString(R.string.confirm_restart) + " ", btnDate.getText().toString(), " " + getResources().getString(R.string.at) + " ", btnTime.getText().toString());
                break;
            // Lors du clic sur le bouton 'Terminer'
            case R.id.blocPauseBtnFinish:
            case R.id.blocRestartBtnFinish:
                // Initialisation du TimerCommand
                timerCommand = TimerCommand.STOP_TIMER;
                // Lancement du Dialog pour confirmer l'action dépendante du TimerCommand avec le message de confirmation en paramètre
                dialogForUpdateTimer(getResources().getString(R.string.confirm_finish) + " ", btnDate.getText().toString(), " " + getResources().getString(R.string.at) + " ", btnTime.getText().toString());
                break;
            // Lors du clic sur 'Mes relevés'
            case R.id.btnViewTimer:
                // Lance l'activité View Timers
                Intent intentViewTimer = new Intent(HomeActivity.this, ViewTimersActivity.class);
                startActivity(intentViewTimer);
                break;
            // Lors du clic sur 'Mes interventions'
            case R.id.btnViewInter:
                // Lance l'activité View Interventions
                Intent intentViewInter = new Intent(HomeActivity.this, ViewInterventionsActivity.class);
                startActivity(intentViewInter);
                break;
        }
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
        String time = String.format(Locale.getDefault(), "%02d", hour) + ":" + String.format(Locale.getDefault(), "%02d", minute);
        btnTime.setText(time);
    }

    /**
     * Initialise le layout et récupère les éléments
     */
    private void initLayout() {
        // Charge le layout
        setContentView(R.layout.activity_home);
        // Récupère les éléments du layout
        welcome = findViewById(R.id.welcome);
        btnDisconnection = findViewById(R.id.btnDisconnection);
        statusTimer = findViewById(R.id.statusTimer);
        btnDate = findViewById(R.id.timerDate);
        btnTime = findViewById(R.id.timerTime);
        layoutStart = findViewById(R.id.blocStart);
        layoutPause = findViewById(R.id.blocPause);
        layoutRestart = findViewById(R.id.blocRestart);
        layoutError = findViewById(R.id.blocError);
        blocStartBtnStart = findViewById(R.id.blocStartBtnStart);
        blocPauseBtnPause = findViewById(R.id.blocPauseBtnPause);
        blocPauseBtnFinish = findViewById(R.id.blocPauseBtnFinish);
        blocRestartBtnRestart = findViewById(R.id.blocRestartBtnRestart);
        blocRestartBtnFinish = findViewById(R.id.blocRestartBtnFinish);
        btnViewTimer = findViewById(R.id.btnViewTimer);
        btnViewInter = findViewById(R.id.btnViewInter);
    }

    /**
     * Initialise les listeners
     */
    private void initListeners() {
        // Initialise les listeners de tous les boutons
        btnDisconnection.setOnClickListener(this);
        btnDate.setOnClickListener(this);
        btnTime.setOnClickListener(this);
        blocStartBtnStart.setOnClickListener(this);
        blocPauseBtnPause.setOnClickListener(this);
        blocPauseBtnFinish.setOnClickListener(this);
        blocRestartBtnRestart.setOnClickListener(this);
        blocRestartBtnFinish.setOnClickListener(this);
        btnViewTimer.setOnClickListener(this);
        btnViewInter.setOnClickListener(this);
    }

    /**
     * Initialise l'affichage
     */
    private void setDisplay() {
        // Affiche le toast 'Connecté'
        Toast.makeText(context, R.string.connected, Toast.LENGTH_SHORT).show();
        // Récupère l'utilisateur en mémoire
        User user = UserSettings.readUser(context);
        // Initialise le textview Welcome
        String welcomeStr = getResources().getString(R.string.welcome) + " " + StringTools.capitalizeFirstLetter(user.getUserName());
        welcome.setText(welcomeStr);
    }

    /**
     * Affiche / cache les boutons de commande du Timer en fonction du mode et affiche la dernière date/heure pour rappel si nécessaire
     *
     * @param displayMode String
     */
    private void setDisplayMode(String displayMode) {
        String date = "";
        String time = "";
        if (lastTimestamp != 0) {
            date = StringTools.getDateStringFromTimestamp(lastTimestamp);
            time = StringTools.getTimeStringFromTimestamp(lastTimestamp, ":");
        }
        switch (displayMode) {
            case "START":
                statusTimer.setText(" ");
                layoutStart.setVisibility(View.VISIBLE);
                layoutPause.setVisibility(View.GONE);
                layoutRestart.setVisibility(View.GONE);
                layoutError.setVisibility(View.GONE);
                break;
            case "PAUSE":
                String timerRunning = "(" + getResources().getString(R.string.timer_running) + " " + date + " " + getResources().getString(R.string.at) + " " + time + ")";
                statusTimer.setText(timerRunning);
                layoutStart.setVisibility(View.GONE);
                layoutPause.setVisibility(View.VISIBLE);
                layoutRestart.setVisibility(View.GONE);
                layoutError.setVisibility(View.GONE);
                break;
            case "RESTART":
                String timerPausing = "(" + getResources().getString(R.string.timer_pausing) + " " + date + " " + getResources().getString(R.string.at) + " " + time + ")";
                statusTimer.setText(timerPausing);
                layoutStart.setVisibility(View.GONE);
                layoutPause.setVisibility(View.GONE);
                layoutRestart.setVisibility(View.VISIBLE);
                layoutError.setVisibility(View.GONE);
                break;
            default:
                statusTimer.setText(" ");
                layoutStart.setVisibility(View.GONE);
                layoutPause.setVisibility(View.GONE);
                layoutRestart.setVisibility(View.GONE);
                layoutError.setVisibility(View.VISIBLE);
                break;
        }
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
     * Récupère le mode du timer puis met à jour l'affichage en fonction
     */
    private void tryToGetTimerMode() {
        try {
            // Récupération du mode du timer
            TimerMode mode = requestTimers.requestGetTimerMode();
            if (mode != null) {
                lastTimestamp = mode.getLastTimestamp();
                displayMode = mode.getMode();
            }
            // En fonction du mode, met à jour l'affichage approprié
            setDisplayMode(displayMode);
            // Initialise / met à jour l'affichage de la date et l'heure actuelle
            setDisplayDateTime();
        } catch (FonctionnalException | TechnicalException e) {
            // Redirige vers l'activité Offline si problème
            redirectOffline();
        }
    }

    /**
     * Initialise l'Alert Dialog de confirmation de mise à jour du Timer
     *
     * @param confirmMessageStart  String
     * @param confirmMessageDate   String
     * @param confirmMessageMiddle String
     * @param confirmMessageTime   String
     */
    private void dialogForUpdateTimer(String confirmMessageStart, String confirmMessageDate, String confirmMessageMiddle, String confirmMessageTime) {
        // Création du listener de la pop-up de confirmation de mise à jour du Timer
        CustomDialogMessage.myOnClickListener updateListener = new CustomDialogMessage.myOnClickListener() {
            @Override
            public void onYesButtonClick() {
                tryToUpdateTimer();
            }
        };
        // Création du dialog avec message
        CustomDialogMessage dialogMessage = new CustomDialogMessage(this, updateListener, confirmMessageStart, confirmMessageDate, confirmMessageMiddle, confirmMessageTime, " ?");
        dialogMessage.show();
    }

    /**
     * Vérifie si le formulaire est valide puis contrôle le timer (début/pause/reprise/fin) en fonction du TimerCommand
     */
    private void tryToUpdateTimer() {
        // Initialisation des paramètres en fonction du TimerCommand
        String requestUrl = null;
        String method = null;
        String newMode = null;
        String succesMessage = null;
        String failMessage = null;
        switch (timerCommand) {
            case START_TIMER:
                requestUrl = "timer/start";
                method = "POST";
                newMode = "PAUSE";
                succesMessage = getResources().getString(R.string.start_timer_msg);
                failMessage = getResources().getString(R.string.error_start);
                break;
            case PAUSE_TIMER:
                requestUrl = "timer/pause";
                method = "PUT";
                newMode = "RESTART";
                succesMessage = getResources().getString(R.string.pause_timer_msg);
                failMessage = getResources().getString(R.string.error_pause);
                break;
            case RESTART_TIMER:
                requestUrl = "timer/restart";
                method = "PUT";
                newMode = "PAUSE";
                succesMessage = getResources().getString(R.string.restart_timer_msg);
                failMessage = getResources().getString(R.string.error_restart);
                break;
            case STOP_TIMER:
                requestUrl = "timer/stop";
                method = "PUT";
                newMode = "START";
                succesMessage = getResources().getString(R.string.finish_timer_msg);
                failMessage = getResources().getString(R.string.error_finish);
                break;
        }
        // Vérification du formulaire puis traitement en fonction des paramètres
        if (!btnDate.getText().toString().trim().isEmpty() && !btnTime.getText().toString().trim().isEmpty()) {
            // Reforme le timestamp à partir du bouton Date et Heure
            long timestamp = NumTools.getTimestampFromStringDateAndTime(btnDate.getText().toString().trim(), btnTime.getText().toString().trim());
            // Si le timestamp est valide
            if (timestamp != 0) {
                // Créé le JSON envoyé dans la requête
                JSONObject jsonSendObject = new JSONObject();
                String ERROR = "[ERROR]";
                try {
                    jsonSendObject.put("timestamp", String.valueOf(timestamp));
                } catch (JSONException e) {
                    Log.e(ERROR, "Impossible de créer le JSON Object pour la requête");
                    jsonSendObject = null;
                }
                try {
                    // Créé/Met à jour/Termine un timer en fonction des paramètres
                    boolean updated = requestTimers.requestUpdateTimer(requestUrl, method, jsonSendObject);
                    // Si changement du timer effectué
                    if (updated) {
                        Log.e(INFO, succesMessage);
                        displayMode = newMode;
                        lastTimestamp = timestamp;
                        setDisplayMode(displayMode);
                        Toast.makeText(context, succesMessage, Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e(ERROR, failMessage);
                        Toast.makeText(context, failMessage, Toast.LENGTH_SHORT).show();
                    }
                } catch (FonctionnalException | TechnicalException e) {
                    // Redirige vers l'activité Offline si problème
                    redirectOffline();
                }
            } else {
                // Si le timestamp n'est pas valide
                Toast.makeText(context, R.string.wrong_date_time, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Initialise l'Alert Dialog de la déconnexion
     */
    private void dialogForDisconnection() {
        // Création du listener de la pop-up de confirmation de déconnexion
        CustomDialogMessage.myOnClickListener disconnectionListener = new CustomDialogMessage.myOnClickListener() {
            @Override
            public void onYesButtonClick() {
                disconnection();
            }
        };
        // Création du dialog avec message
        String message = getResources().getString(R.string.confirm_disconnection);
        CustomDialogMessage dialogMessage = new CustomDialogMessage(this, disconnectionListener, message);
        dialogMessage.show();
    }

    /**
     * Déconnecte l'utilisateur et redémarre l'application
     */
    private void disconnection() {
        // Supprime le cookie et l'utilisateur
        UserSettings.deleteCookie(context);
        UserSettings.deleteUser(context);
        // Relance l'application
        Log.e(INFO, "Relancement de l'application");
        Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        startActivity(intent);
        closeActivity();
    }

    /**
     * Redirige vers l'activité Offline
     */
    private void redirectOffline() {
        // Redirige vers l'activité Offline
        Intent intent = new Intent(HomeActivity.this, OfflineActivity.class);
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
