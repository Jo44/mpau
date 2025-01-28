package fr.mpau.manager

import android.util.Log
import com.google.gson.Gson
import fr.mpau.bean.Intervention
import fr.mpau.webservice.WSRequest
import fr.mpau.webservice.WSResult
import org.json.JSONArray
import org.json.JSONObject

/**
 * Intervention Manager
 *
 * Author: Jonathan
 * Created: 25/01/2018
 * Last Updated: 24/01/2025
 */
class InterventionManager {
    companion object {

        /**
         * Attributs
         */

        private const val TAG = "InterventionManager"

        /**
         * Récupère une intervention en fonction de l'ID de l'intervention
         *
         * @param cookie String
         * @param id Int
         * @return WSResult<JSONObject>
         */
        fun getIntervention(cookie: String, id: Int): WSResult<JSONObject> {
            Log.i(TAG, "Demande de récupération de l'intervention")
            return WSRequest.requestObject(cookie, "intervention/$id", "GET", null)
        }

        /**
         * Récupère l'intervention à partir du WSResult<JSONObject>
         *
         * @param wsResult WSResult<JSONObject>
         * @return Intervention?
         */
        fun readGetIntervention(wsResult: WSResult<JSONObject>): Intervention? {
            Log.i(TAG, "Récupération de l'intervention")
            var intervention: Intervention? = null
            val result = wsResult.result
            if (result != null) {
                intervention = Gson().fromJson(result.toString(), Intervention::class.java)
            } else {
                val error = wsResult.error
                if (error != null) {
                    Log.e(TAG, error.message!!)
                    throw error
                }
            }
            return intervention
        }

        /**
         * Récupère les X interventions précédentes ou suivantes de l'ID de l'intervention selon les paramètres
         *
         * @param cookie String
         * @param id Int
         * @param byPage Int
         * @param next Boolean
         * @return WSResult<JSONArray>
         */
        fun getListInterventions(
            cookie: String,
            id: Int,
            byPage: Int,
            next: Boolean
        ): WSResult<JSONArray> {
            if (next) {
                Log.i(
                    TAG,
                    "Demande de récupération des $byPage interventions suivant l'intervention {$id}"
                )
            } else {
                Log.i(
                    TAG,
                    "Demande de récupération des $byPage interventions précédent l'intervention {$id}"
                )
            }
            return WSRequest.requestObjects(
                cookie,
                "intervention/list/$next/from/$id/by/$byPage",
                "GET",
                null
            )
        }

        /**
         * Récupère la liste des interventions à partir du WSResult<JSONArray>
         *
         * @param wsResult WSResult<JSONArray>
         * @return ArrayList<Intervention>?
         */
        fun readGetListInterventions(wsResult: WSResult<JSONArray>): ArrayList<Intervention>? {
            Log.i(TAG, "Récupération des interventions")
            var list: ArrayList<Intervention>? = null
            val result = wsResult.result
            if (result != null) {
                list = ArrayList()
                for (i in 0 until result.length()) {
                    val resultInter = result.getJSONObject(i)
                    val intervention =
                        Gson().fromJson(resultInter.toString(), Intervention::class.java)
                    list.add(intervention)
                }
            } else {
                val error = wsResult.error
                if (error != null) {
                    Log.e(TAG, error.message!!)
                    throw error
                }
            }
            return list
        }

        /**
         * Ajoute l'intervention en fonction de l'objet JSON
         *
         * @param cookie String
         * @param jsonObject JSONObject
         * @return WSResult<JSONObject>
         */
        fun postIntervention(cookie: String, jsonObject: JSONObject): WSResult<JSONObject> {
            Log.i(TAG, "Demande d'ajout d'une intervention")
            return WSRequest.requestObject(cookie, "intervention", "POST", jsonObject)
        }

        /**
         * Détermine si l'intervention a été ajoutée à partir du WSResult<JSONObject>
         *
         * @param wsResult WSResult<JSONObject>
         * @return Boolean
         */
        fun readPostIntervention(wsResult: WSResult<JSONObject>): Boolean {
            Log.i(TAG, "Ajout de l'intervention")
            var added = false
            val result = wsResult.result
            if (result != null) {
                added = true
            } else {
                val error = wsResult.error
                if (error != null) {
                    Log.e(TAG, error.message!!)
                    throw error
                }
            }
            return added
        }

        /**
         * Modifie l'intervention en fonction de l'objet JSON et de l'ID de l'intervention
         *
         * @param cookie String
         * @param id Int
         * @param jsonObject JSONObject
         * @return WSResult<JSONObject>
         */
        fun putIntervention(cookie: String, id: Int, jsonObject: JSONObject): WSResult<JSONObject> {
            Log.i(TAG, "Demande de modification d'une intervention")
            return WSRequest.requestObject(cookie, "intervention/$id", "PUT", jsonObject)
        }

        /**
         * Détermine si l'intervention a été modifiée à partir du WSResult<JSONObject>
         *
         * @param wsResult WSResult<JSONObject>
         * @return Boolean
         */
        fun readPutIntervention(wsResult: WSResult<JSONObject>): Boolean {
            Log.i(TAG, "Modification de l'intervention")
            var updated = false
            val result = wsResult.result
            if (result != null) {
                updated = true
            } else {
                val error = wsResult.error
                if (error != null) {
                    Log.e(TAG, error.message!!)
                    throw error
                }
            }
            return updated
        }

        /**
         * Supprime l'intervention en fonction de l'ID de l'intervention
         *
         * @param cookie
         * @param id Int
         * @return WSResult<JSONObject>
         */
        fun deleteIntervention(cookie: String, id: Int): WSResult<JSONObject> {
            Log.i(TAG, "Demande de suppression d'une intervention")
            return WSRequest.requestObject(cookie, "intervention/$id", "DELETE", null)
        }

        /**
         * Détermine si l'intervention a été supprimée à partir du WSResult<JSONObject>
         *
         * @param wsResult WSResult<JSONObject>
         * @return Boolean
         */
        fun readDeleteIntervention(wsResult: WSResult<JSONObject>): Boolean {
            Log.i(TAG, "Suppression de l'intervention")
            var deleted = false
            val result = wsResult.result
            if (result != null) {
                deleted = true
            } else {
                val error = wsResult.error
                if (error != null) {
                    Log.e(TAG, error.message!!)
                    throw error
                }
            }
            return deleted
        }

    }
}