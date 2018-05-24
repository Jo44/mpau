package fr.mpau.webservice;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import fr.mpau.exceptions.FonctionnalException;
import fr.mpau.exceptions.TechnicalException;
import fr.mpau.models.Timer;
import fr.mpau.models.TimerMode;
import fr.mpau.models.WorkDay;
import fr.mpau.models.WorkPeriod;

/**
 * Classe de requêtes du timer du WebService
 * <p>
 * Author: Jonathan B.
 * Created: 25/01/2018
 * Last Updated: 28/02/2018
 */

public class RequestTimers {

    /**
     * Attributs
     */
    private static final String ERROR = "[ERROR]";
    private static final String INFO = "[INFO]";

    /**
     * Constructeur
     */
    public RequestTimers() {
    }

    /**
     * Récupère le mode du timer de l'utilisateur pour gestion affichage
     *
     * @return TimerMode
     */
    public TimerMode requestGetTimerMode() throws FonctionnalException, TechnicalException {
        TimerMode mode = null;
        Log.e(INFO, "Récupération du mode du timer");
        AsyncTask getModeTask = new RetrieveFeedTask().execute("timer/mode", "GET");
        try {
            AsyncTaskResult resultGetModeObject = (AsyncTaskResult) getModeTask.get();
            Object result = resultGetModeObject.getResult();
            if (result != null) {
                JSONObject jsonObject = (JSONObject) result;
                String displayMode = jsonObject.getString("mode").trim();
                long lastTimestamp = jsonObject.getLong("lastTimestamp");
                mode = new TimerMode(displayMode, lastTimestamp);
                Log.e(INFO, "Mode Timer récupéré");
            } else {
                Exception ex = resultGetModeObject.getError();
                if (ex instanceof FonctionnalException) {
                    Log.e(ERROR, ex.getMessage());
                    throw (FonctionnalException)ex;
                } else if (ex instanceof TechnicalException) {
                    Log.e(ERROR, ex.getMessage());
                    throw (TechnicalException)ex;
                }
            }
        } catch (InterruptedException | ExecutionException | JSONException e) {
            Log.e(ERROR, "Le WebService est-il en ligne et opérationnel ?");
        }
        return mode;
    }

    /**
     * Récupère les timers de l'utilisateur pour le mois souhaité en paramètre
     *
     * @param relatifMonth int
     * @return ArrayList<Timer>
     */
    public ArrayList<Timer> requestGetTimers(int relatifMonth) throws FonctionnalException, TechnicalException {
        ArrayList<Timer> listTimers = new ArrayList<>();
        Log.e(INFO, "Récupération des timers pour le mois M" + String.valueOf(relatifMonth));
        String url = "timer/list/" + String.valueOf(relatifMonth);
        AsyncTask getTimersTask = new RetrieveFeedTask().execute(url, "GET");
        try {
            AsyncTaskResult resultGetTimersObject = (AsyncTaskResult) getTimersTask.get();
            Object result = resultGetTimersObject.getResult();
            if (result != null) {
                JSONObject resultGetTimersJSONObject = (JSONObject) result;
                Object obj = resultGetTimersJSONObject.get("timer");
                // Si un seul objet
                if (obj instanceof JSONObject) {
                    JSONObject timerObj = (JSONObject) obj;
                    Timer timer = getTimer(timerObj);
                    listTimers.add(timer);
                    Log.e(INFO, "Un timer récupéré");
                    // Si plusieurs objets
                } else if (obj instanceof JSONArray) {
                    JSONArray timersArray = (JSONArray) obj;
                    for (int i = 0; i < timersArray.length(); i++) {
                        JSONObject timerObj = timersArray.getJSONObject(i);
                        Timer timer = getTimer(timerObj);
                        listTimers.add(timer);
                    }
                    Log.e(INFO, "Plusieurs timers récupérés");
                } else {
                    Log.e(ERROR, "Erreur lors de la récupération des timers");
                }
            } else {
                Exception ex = resultGetTimersObject.getError();
                if (ex instanceof FonctionnalException) {
                    Log.e(ERROR, ex.getMessage());
                    throw (FonctionnalException)ex;
                } else if (ex instanceof TechnicalException) {
                    Log.e(ERROR, ex.getMessage());
                    throw (TechnicalException)ex;
                }
            }
        } catch (InterruptedException | ExecutionException | JSONException e) {
            Log.e(ERROR, "Aucun timer récupéré");
        }
        return listTimers;
    }

    /**
     * Parse un Timer à partir d'un objet JSON
     *
     * @param timerObj JSONObject
     * @return Timer
     * @throws JSONException ex
     */
    private Timer getTimer(JSONObject timerObj) throws JSONException {
        Timer timer;
        int timerId = timerObj.getInt("timerId");
        JSONObject wdObject = timerObj.getJSONObject("timerWorkday");
        int wdId = wdObject.getInt("wdId");
        int wdUserId = wdObject.getInt("wdUserId");
        long wdStart = wdObject.getLong("wdStart");
        long wdStop = wdObject.getLong("wdStop");
        boolean wdFinished = wdObject.getBoolean("wdFinished");
        WorkDay timerWorkday = new WorkDay(wdId, wdUserId, wdStart, wdStop, wdFinished);
        List<WorkPeriod> wpList = new ArrayList<>();
        Object wpListObject = timerObj.get("timerWorkperiodsList");
        if (wpListObject instanceof JSONObject) {
            JSONObject wpObj = (JSONObject) wpListObject;
            WorkPeriod timerWorkperiod = getWorkPeriod(wpObj);
            wpList.add(timerWorkperiod);
        } else if (wpListObject instanceof JSONArray) {
            JSONArray wpArray = (JSONArray) wpListObject;
            for (int i = 0; i < wpArray.length(); i++) {
                JSONObject wpObj = wpArray.getJSONObject(i);
                WorkPeriod timerWorkperiod = getWorkPeriod(wpObj);
                wpList.add(timerWorkperiod);
            }
        } else {
            Log.e(ERROR, "Erreur lors de la récupération des workperiods");
        }
        timer = new Timer(timerId, timerWorkday, wpList);
        return timer;
    }

    /**
     * Parse une WorkPeriod à partir d'un objet JSON
     *
     * @param wpObj JSONObject
     * @return WorkPeriod
     * @throws JSONException ex
     */
    private WorkPeriod getWorkPeriod(JSONObject wpObj) throws JSONException {
        WorkPeriod wp;
        int wpId = wpObj.getInt("wpId");
        int wpWdId = wpObj.getInt("wpWdId");
        long wpStart = wpObj.getLong("wpStart");
        long wpStop = wpObj.getLong("wpStop");
        boolean wpFinished = wpObj.getBoolean("wpFinished");
        wp = new WorkPeriod(wpId, wpWdId, wpStart, wpStop, wpFinished);
        return wp;
    }

    /**
     * Met à jour le timer en fonction du mode et l'objet JSON contenant le timestamp de MàJ
     *
     * @param requestUrl     String
     * @param method         String
     * @param jsonSendObject JSONObject
     * @return boolean
     */
    public boolean requestUpdateTimer(String requestUrl, String method, JSONObject jsonSendObject) throws FonctionnalException, TechnicalException {
        boolean executed = false;
        Log.e(INFO, "Mise à jour du timer");
        AsyncTask updateTimerTask = new RetrieveFeedTask().execute(requestUrl, method, jsonSendObject);
        try {
            AsyncTaskResult resultUpdateTimerObject = (AsyncTaskResult) updateTimerTask.get();
            Object result = resultUpdateTimerObject.getResult();
            if (result != null) {
                JSONObject resultUpdateTimerJSONObject = (JSONObject) result;
                // Si renvoi un JSON avec un timer
                if (resultUpdateTimerJSONObject.getInt("timerId") == 0) {
                    executed = true;
                }
            } else {
                Exception ex = resultUpdateTimerObject.getError();
                if (ex instanceof FonctionnalException) {
                    Log.e(ERROR, ex.getMessage());
                    throw (FonctionnalException)ex;
                } else if (ex instanceof TechnicalException) {
                    Log.e(ERROR, ex.getMessage());
                    throw (TechnicalException)ex;
                }
            }
        } catch (InterruptedException | ExecutionException | JSONException e) {
            Log.e(ERROR, "Le WebService est-il en ligne et opérationnel ?");
        }
        return executed;
    }

    /**
     * Supprime le timer en fonction de sa WorkDay ID
     *
     * @param workdayId int
     * @return boolean
     */
    public boolean requestDeleteTimer(int workdayId) throws FonctionnalException, TechnicalException {
        boolean executed = false;
        String url = "timer/" + String.valueOf(workdayId);
        AsyncTask deleteTimerTask = new RetrieveFeedTask().execute(url, "DELETE");
        try {
            AsyncTaskResult resultDeleteTimerObject = (AsyncTaskResult) deleteTimerTask.get();
            Object result = resultDeleteTimerObject.getResult();
            if (result != null) {
                JSONObject resultDeleteTimerJSONObject = (JSONObject) result;
                // Si renvoi un JSON avec une intervention
                if (resultDeleteTimerJSONObject.getJSONObject("timerWorkday") != null && resultDeleteTimerJSONObject.getJSONObject("timerWorkday").getInt("wdId") > 0) {
                    executed = true;
                }
            } else {
                Exception ex = resultDeleteTimerObject.getError();
                if (ex instanceof FonctionnalException) {
                    Log.e(ERROR, ex.getMessage());
                    throw (FonctionnalException)ex;
                } else if (ex instanceof TechnicalException) {
                    Log.e(ERROR, ex.getMessage());
                    throw (TechnicalException)ex;
                }
            }
        } catch (InterruptedException | ExecutionException | JSONException e) {
            Log.e(ERROR, "Le WebService est-il en ligne et opérationnel ?");
        }
        return executed;
    }

}
