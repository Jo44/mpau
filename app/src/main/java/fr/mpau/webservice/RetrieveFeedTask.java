package fr.mpau.webservice;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

import fr.mpau.MPAU;
import fr.mpau.exceptions.FonctionnalException;
import fr.mpau.exceptions.TechnicalException;
import fr.mpau.tools.UserSettings;

/**
 * Tools RetrieveFeedTask
 * -> Permet d'envoyer une requête au WebService selon des paramètres et de récupérer l'objet JSON résultant
 * -> Le cookie de l'utilisateur est stocké dans les Shared Preferences
 * <p>
 * Author: Jonathan B.
 * Created: 18/11/2017
 * Last Updated : 12/02/2018
 */

public class RetrieveFeedTask extends AsyncTask<Object, Void, AsyncTaskResult<JSONObject>> {

    /**
     * Envoi une requête au WebService avec les paramètres fournis et récupére l'AsyncTaskResult<JSONObject> résultant
     * // param[0] = (String) Request URL
     * // param[1] = (String) Request Method
     * // param[2] = (JSONObject) Body Content
     *
     * @param params Object...
     * @return AsyncTaskResult<JSONObject>
     */
    protected AsyncTaskResult<JSONObject> doInBackground(Object... params) {
        final String ERROR = "[ERROR]";
        final String INFO = "[INFO]";
        final String BASE_REQUEST_URL = "****";
        AsyncTaskResult<JSONObject> taskResult;
        HttpURLConnection urlConnection = null;
        try {
            String requestURL = BASE_REQUEST_URL + params[0];
            Log.e(INFO, "{** Request WS : [" + params[1] + "] " + requestURL + " **}");
            URL url = new URL(requestURL);
            urlConnection = (HttpURLConnection) url.openConnection();
            // Configure le timeout de la connexion à 3s si pas de réponse
            urlConnection.setConnectTimeout(3000);
            // Récupération du cookie
            String cookie = UserSettings.readCookie(MPAU.getAppContext());
            // Création de l'Authentification String
            String basicAuth = "Basic " + new String(Base64.encode(cookie.getBytes(), Base64.NO_WRAP));
            // Création des Headers Params
            urlConnection.setRequestProperty("Authorization", basicAuth);
            urlConnection.setRequestMethod((String) params[1]);
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            // Création du Body (si nécessaire)
            if (params.length == 3 && params[2] != null) {
                JSONObject bodyContent = (JSONObject) params[2];
                OutputStream os = urlConnection.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
                osw.write(bodyContent.toString());
                osw.flush();
                os.close();
            }
            // Récupération du code retour
            int responseCode = urlConnection.getResponseCode();
            Log.e(INFO, "{** Response Code : " + String.valueOf(responseCode) + " **}");
            // Traitement en fonction du code retour
            switch (responseCode) {
                // Si code 200 (OK), retourner le JSONObject
                case 200:
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    JSONObject jsonObject = new JSONObject(stringBuilder.toString());
                    taskResult = new AsyncTaskResult<>(jsonObject);
                    break;
                // Si code 204(No Content), retourner un JSONObject vide
                case 204:
                    taskResult = new AsyncTaskResult<>(new JSONObject());
                    break;
                // Si code 412 (Precondition Failed), retourner Fonctionnal Exception
                case 412:
                    taskResult = new AsyncTaskResult<>(new FonctionnalException("Les conditions ne sont pas valides"));
                    break;
                // Si code 503 (Service Unavailable), retourner Technical Exception
                case 503:
                    taskResult = new AsyncTaskResult<>(new TechnicalException("Le service est indisponible"));
                    break;
                // Si autre réponse, retourner Technical Exception
                default:
                    taskResult = new AsyncTaskResult<>(new TechnicalException("Le service est indisponible"));
                    break;
            }
        } catch (ConnectException ce) {
            // Si le service ne réponds pas, retourner Technical Exception
            String error = "Le WebService ne réponds pas";
            Log.e(ERROR, error);
            Log.e(ERROR, ce.getMessage());
            taskResult = new AsyncTaskResult<>(new TechnicalException(error));
        } catch (Exception e) {
            // Si autre erreur, retourner Technical Exception
            Log.e(ERROR, e.getMessage());
            taskResult = new AsyncTaskResult<>(new TechnicalException(e.getMessage()));
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return taskResult;
    }

}