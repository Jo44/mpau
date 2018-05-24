package fr.mpau.webservice;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import fr.mpau.exceptions.FonctionnalException;
import fr.mpau.exceptions.TechnicalException;
import fr.mpau.models.User;

/**
 * Classe de requête des utilisateurs du WebService
 * <p>
 * Author: Jonathan B.
 * Created: 25/01/2018
 * Last Updated: 28/02/2018
 */

public class RequestUsers {

    /**
     * Attributs
     */
    private static final String ERROR = "[ERROR]";
    private static final String INFO = "[INFO]";

    /**
     * Constructeur
     */
    public RequestUsers() {
    }

    /**
     * Récupère l'utilisateur via son cookie
     *
     * @return User
     */
    public User requestUser() throws FonctionnalException, TechnicalException {
        User user = null;
        Log.e(INFO, "Récupération de l'utilisateur");
        AsyncTask getUserTask = new RetrieveFeedTask().execute("user", "GET");
        try {
            AsyncTaskResult resultGetUserObject = (AsyncTaskResult) getUserTask.get();
            Object result = resultGetUserObject.getResult();
            if (result != null) {
                JSONObject jsonObject = (JSONObject) result;
                int id = jsonObject.getInt("id");
                String name = jsonObject.getString("name");
                String email = jsonObject.getString("email");
                int totalInter = jsonObject.getInt("nbTotalInter");
                int interIdMax = jsonObject.getInt("interIdMax");
                long inscriptionDate = jsonObject.getLong("inscriptionDate");
                user = new User(id, name, email, totalInter, interIdMax, inscriptionDate);
            } else {
                Exception ex = resultGetUserObject.getError();
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
        return user;
    }

}
