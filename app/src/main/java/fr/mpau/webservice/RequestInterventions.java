package fr.mpau.webservice;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

import fr.mpau.exceptions.FonctionnalException;
import fr.mpau.exceptions.TechnicalException;
import fr.mpau.models.Intervention;

/**
 * Classe de requêtes des interventions du WebService
 * <p>
 * Author: Jonathan B.
 * Created: 25/01/2018
 * Last Updated: 28/02/2018
 */

public class RequestInterventions {

    /**
     * Attributs
     */
    private static final String ERROR = "[ERROR]";
    private static final String INFO = "[INFO]";

    /**
     * Constructeur
     */
    public RequestInterventions() {
    }

    /**
     * Récupère une intervention en fonction de l'ID de l'intervention
     *
     * @param interID int
     * @return Intervention
     */
    public Intervention requestGetIntervention(int interID) throws FonctionnalException, TechnicalException {
        Intervention inter = null;
        Log.e(INFO, "Récupération de l'intervention {" + interID + "}");
        String url = "interventions/" + String.valueOf(interID);
        AsyncTask getOneInterventionTask = new RetrieveFeedTask().execute(url, "GET");
        try {
            AsyncTaskResult resultGetOneInterventionObject = (AsyncTaskResult) getOneInterventionTask.get();
            Object result = resultGetOneInterventionObject.getResult();
            if (result != null) {
                JSONObject resultGetOneInterventionJSONObject = (JSONObject) result;
                // Si récupère une intervention
                inter = new Intervention(resultGetOneInterventionJSONObject.getInt("interId"), resultGetOneInterventionJSONObject.getInt("interUserId"),
                        resultGetOneInterventionJSONObject.getLong("interDate"), resultGetOneInterventionJSONObject.getInt("interDuree"),
                        resultGetOneInterventionJSONObject.getString("interSecteur"), resultGetOneInterventionJSONObject.getBoolean("interSmur"),
                        resultGetOneInterventionJSONObject.getInt("interTypeId"), resultGetOneInterventionJSONObject.getInt("interSoustypeId"),
                        resultGetOneInterventionJSONObject.getInt("interAgepatientId"), resultGetOneInterventionJSONObject.getString("interCommentaire"));
            } else {
                Exception ex = resultGetOneInterventionObject.getError();
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
        return inter;
    }

    /**
     * Récupère les X interventions précédentes ou suivantes de l'ID de l'intervention selon les paramètres
     *
     * @param interId     int
     * @param interByPage int
     * @param next        int
     * @return ArrayList<Intervention>
     */
    public ArrayList<Intervention> requestGetList(int interId, int interByPage, boolean next) throws FonctionnalException, TechnicalException {
        ArrayList<Intervention> listeInterventions = new ArrayList<>();
        String url;
        if (next) {
            Log.e(INFO, "Récupération des " + interByPage + " interventions suivant l'intervention {" + interId + "}");
            url = "interventions/list/true/from/" + String.valueOf(interId) + "/by/" + String.valueOf(interByPage);
        } else {
            Log.e(INFO, "Récupération des " + interByPage + " interventions précédent l'intervention {" + interId + "}");
            url = "interventions/list/false/from/" + String.valueOf(interId) + "/by/" + String.valueOf(interByPage);
        }
        AsyncTask getInterventionsTask = new RetrieveFeedTask().execute(url, "GET");
        try {
            AsyncTaskResult resultGetInterventionsObject = (AsyncTaskResult) getInterventionsTask.get();
            Object result = resultGetInterventionsObject.getResult();
            if (result != null) {
                JSONObject resultGetInterventionsJSONObject = (JSONObject) result;
                Object obj = resultGetInterventionsJSONObject.get("intervention");
                // Si un seul objet
                if (obj instanceof JSONObject) {
                    JSONObject interObj = (JSONObject) obj;
                    Intervention inter = new Intervention(interObj.getInt("interId"), interObj.getInt("interUserId"), interObj.getLong("interDate"), interObj.getInt("interDuree"), interObj.getString("interSecteur"), interObj.getBoolean("interSmur"), interObj.getInt("interTypeId"), interObj.getInt("interSoustypeId"), interObj.getInt("interAgepatientId"), interObj.getString("interCommentaire"));
                    listeInterventions.add(inter);
                    Log.e(INFO, "Une intervention récupérée");
                    // Si plusieurs objets
                } else if (obj instanceof JSONArray) {
                    JSONArray inters = (JSONArray) obj;
                    for (int i = 0; i < inters.length(); i++) {
                        JSONObject interObj = inters.getJSONObject(i);
                        Intervention inter = new Intervention(interObj.getInt("interId"), interObj.getInt("interUserId"), interObj.getLong("interDate"), interObj.getInt("interDuree"), interObj.getString("interSecteur"), interObj.getBoolean("interSmur"), interObj.getInt("interTypeId"), interObj.getInt("interSoustypeId"), interObj.getInt("interAgepatientId"), interObj.getString("interCommentaire"));
                        listeInterventions.add(inter);
                    }
                    Log.e(INFO, "Plusieurs interventions récupérées");
                } else {
                    Log.e(ERROR, "Erreur lors de la récupération des interventions");
                }
                // Inverse la liste pour garder les ID en ordre décroissant si besoin
                if (next) {
                    Collections.reverse(listeInterventions);
                }
            } else {
                Exception ex = resultGetInterventionsObject.getError();
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
        return listeInterventions;
    }

    /**
     * Ajoute l'intervention en fonction de l'objet JSON
     *
     * @param jsonSendObject JSONObject
     * @return boolean
     */
    public boolean requestAddIntervention(JSONObject jsonSendObject) throws FonctionnalException, TechnicalException {
        boolean executed = false;
        Log.e(INFO, "Ajout d'une intervention");
        AsyncTask addInterTask = new RetrieveFeedTask().execute("interventions", "POST", jsonSendObject);
        try {
            AsyncTaskResult resultAddInterObject = (AsyncTaskResult) addInterTask.get();
            Object result = resultAddInterObject.getResult();
            if (result != null) {
                JSONObject resultAddInterJSONObject = (JSONObject) result;
                // Si renvoi un JSON avec une intervention
                if (resultAddInterJSONObject.getInt("interId") == 0) {
                    executed = true;
                }
            } else {
                Exception ex = resultAddInterObject.getError();
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
     * Modifie l'intervention en fonction de son ID et de l'objet JSON
     *
     * @param interId        int
     * @param jsonSendObject JSONObject
     * @return boolean
     */
    public boolean requestModifyIntervention(int interId, JSONObject jsonSendObject) throws FonctionnalException, TechnicalException {
        boolean executed = false;
        String url = "interventions/" + String.valueOf(interId);
        AsyncTask modifyInterTask = new RetrieveFeedTask().execute(url, "PUT", jsonSendObject);
        try {
            AsyncTaskResult resultModifyInterObject = (AsyncTaskResult) modifyInterTask.get();
            Object result = resultModifyInterObject.getResult();
            if (result != null) {
                JSONObject resultModifyInterJSONObject = (JSONObject) result;
                // Si renvoi un JSON avec une intervention
                if (resultModifyInterJSONObject.getInt("interId") != 0) {
                    executed = true;
                }
            } else {
                Exception ex = resultModifyInterObject.getError();
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
     * Supprime l'intervention en fonction de son ID
     *
     * @param interId int
     * @return boolean
     */
    public boolean requestDeleteIntervention(int interId) throws FonctionnalException, TechnicalException {
        boolean executed = false;
        String url = "interventions/" + String.valueOf(interId);
        AsyncTask deleteInterTask = new RetrieveFeedTask().execute(url, "DELETE");
        try {
            AsyncTaskResult resultDeleteInterObject = (AsyncTaskResult) deleteInterTask.get();
            Object result = resultDeleteInterObject.getResult();
            if (result != null) {
                JSONObject resultDeleteInterJSONObject = (JSONObject) result;
                // Si renvoi un JSON avec une intervention
                if (resultDeleteInterJSONObject.getInt("interId") != 0) {
                    executed = true;
                }
            } else {
                Exception ex = resultDeleteInterObject.getError();
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
