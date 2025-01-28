package fr.mpau.manager

import android.util.Log
import com.google.gson.Gson
import fr.mpau.bean.Timer
import fr.mpau.bean.TimerMode
import fr.mpau.webservice.WSRequest
import fr.mpau.webservice.WSResult
import org.json.JSONArray
import org.json.JSONObject

/**
 * Timer Manager
 *
 * Author: Jonathan
 * Created: 25/01/2018
 * Last Updated: 24/01/2025
 */
class TimerManager {
    companion object {

        /**
         * Attributs
         */

        private const val TAG = "TimerManager"

        /**
         * Récupère le mode du timer de l'utilisateur pour gestion affichage
         *
         * @param cookie String
         * @return WSResult<JSONObject>
         */
        fun getTimerMode(cookie: String): WSResult<JSONObject> {
            Log.i(TAG, "Demande de récupération du mode du timer")
            return WSRequest.requestObject(cookie, "timer/mode", "GET", null)
        }

        /**
         * Récupère le mode du timer à partir du WSResult<JSONObject>
         *
         * @param wsResult WSResult<JSONObject>
         * @return TimerMode?
         */
        fun readGetTimerMode(wsResult: WSResult<JSONObject>): TimerMode? {
            Log.i(TAG, "Récupération du mode du timer")
            var timerMode: TimerMode? = null
            val result = wsResult.result
            if (result != null) {
                timerMode = Gson().fromJson(result.toString(), TimerMode::class.java)
            } else {
                val error = wsResult.error
                if (error != null) {
                    Log.e(TAG, error.message!!)
                    throw error
                }
            }
            return timerMode
        }

        /**
         * Récupère le timer non terminé le plus ancien de l'utilisateur
         *
         * @param cookie String
         * @return WSResult<JSONObject>
         */
        @Suppress("unused")
        fun getTimerUnfinished(cookie: String): WSResult<JSONObject> {
            Log.i(TAG, "Demande de récupération du timer non terminé")
            return WSRequest.requestObject(cookie, "timer/last", "GET", null)
        }

        /**
         * Récupère le timer non terminé à partir du WSResult<JSONObject>
         *
         * @param wsResult WSResult<JSONObject>
         * @return Timer?
         */
        @Suppress("unused")
        fun readGetTimerUnfinished(wsResult: WSResult<JSONObject>): Timer? {
            Log.i(TAG, "Récupération du timer non terminé")
            var timer: Timer? = null
            val result = wsResult.result
            if (result != null) {
                timer = Gson().fromJson(result.toString(), Timer::class.java)
            } else {
                val error = wsResult.error
                if (error != null) {
                    Log.e(TAG, error.message!!)
                    throw error
                }
            }
            return timer
        }

        /**
         * Récupère tous les timers de l'utilisateur
         *
         * @param cookie String
         * @return WSResult<JSONArray>
         */
        @Suppress("unused")
        fun getTimers(cookie: String): WSResult<JSONArray> {
            Log.i(TAG, "Demande de récupération des timers")
            return WSRequest.requestObjects(cookie, "timer/all", "GET", null)
        }

        /**
         * Récupère tous les timers à partir du WSResult<JSONArray>
         *
         * @param wsResult WSResult<JSONArray>
         * @return ArrayList<Timer>?
         */
        @Suppress("unused")
        fun readGetTimers(wsResult: WSResult<JSONArray>): ArrayList<Timer>? {
            Log.i(TAG, "Récupération des timers")
            var list: ArrayList<Timer>? = null
            val result = wsResult.result
            if (result != null) {
                list = ArrayList()
                for (i in 0 until result.length()) {
                    val resultTimer = result.getJSONObject(i)
                    val timer = Gson().fromJson(resultTimer.toString(), Timer::class.java)
                    list.add(timer)
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
         * Récupère tous les timers terminés de l'utilisateur du mois voulu (de 0 pour mois actuel à -12 pour même mois année précédente)
         *
         * @param cookie String
         * @param month Int
         * @return WSResult<JSONArray>
         */
        fun getMonthTimers(cookie: String, month: Int): WSResult<JSONArray> {
            Log.i(TAG, "Demande de récupération des timers du mois {$month}")
            return WSRequest.requestObjects(cookie, "timer/list/$month", "GET", null)
        }

        /**
         * Récupère tous les timers terminés du mois voulu à partir du WSResult<JSONArray>
         *
         * @param wsResult WSResult<JSONArray>
         * @return ArrayList<Timer>?
         */
        fun readGetMonthTimers(wsResult: WSResult<JSONArray>): ArrayList<Timer>? {
            Log.i(TAG, "Récupération des timers du mois")
            var list: ArrayList<Timer>? = null
            val result = wsResult.result
            if (result != null) {
                list = ArrayList()
                for (i in 0 until result.length()) {
                    val resultTimer = result.getJSONObject(i)
                    val timer = Gson().fromJson(resultTimer.toString(), Timer::class.java)
                    list.add(timer)
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
         * Débute un nouveau timer pour l'utilisateur
         *
         * @param cookie String
         * @param jsonObject JSONObject
         * @return WSResult<JSONObject>
         */
        fun postStartTimer(cookie: String, jsonObject: JSONObject): WSResult<JSONObject> {
            Log.i(TAG, "Demande de début d'un nouveau timer")
            return WSRequest.requestObject(cookie, "timer/start", "POST", jsonObject)
        }

        /**
         * Récupère le timer une fois débuté à partir du WSResult<JSONObject>
         *
         * @param wsResult WSResult<JSONObject>
         * @return Timer?
         */
        fun readPostStartTimer(wsResult: WSResult<JSONObject>): Timer? {
            Log.i(TAG, "Début d'un nouveau timer")
            var timer: Timer? = null
            val result = wsResult.result
            if (result != null) {
                timer = Gson().fromJson(result.toString(), Timer::class.java)
            } else {
                val error = wsResult.error
                if (error != null) {
                    Log.e(TAG, error.message!!)
                    throw error
                }
            }
            return timer
        }

        /**
         * Met en pause le timer de l'utilisateur
         *
         * @param cookie String
         * @param jsonObject JSONObject
         * @return WSResult<JSONObject>
         */
        fun putPauseTimer(cookie: String, jsonObject: JSONObject): WSResult<JSONObject> {
            Log.i(TAG, "Demande de pause du timer")
            return WSRequest.requestObject(cookie, "timer/pause", "PUT", jsonObject)
        }

        /**
         * Récupère le timer une fois en pause à partir du WSResult<JSONObject>
         *
         * @param wsResult WSResult<JSONObject>
         * @return Timer?
         */
        fun readPutPauseTimer(wsResult: WSResult<JSONObject>): Timer? {
            Log.i(TAG, "Pause du timer")
            var timer: Timer? = null
            val result = wsResult.result
            if (result != null) {
                timer = Gson().fromJson(result.toString(), Timer::class.java)
            } else {
                val error = wsResult.error
                if (error != null) {
                    Log.e(TAG, error.message!!)
                    throw error
                }
            }
            return timer
        }

        /**
         * Relance le timer de l'utilisateur
         *
         * @param cookie String
         * @param jsonObject JSONObject
         * @return WSResult<JSONObject>
         */
        fun putRestartTimer(cookie: String, jsonObject: JSONObject): WSResult<JSONObject> {
            Log.i(TAG, "Demande de relance du timer")
            return WSRequest.requestObject(cookie, "timer/restart", "PUT", jsonObject)
        }

        /**
         * Récupère le timer une fois relancé à partir du WSResult<JSONObject>
         *
         * @param wsResult WSResult<JSONObject>
         * @return Timer?
         */
        fun readPutRestartTimer(wsResult: WSResult<JSONObject>): Timer? {
            Log.i(TAG, "Relance du timer")
            var timer: Timer? = null
            val result = wsResult.result
            if (result != null) {
                timer = Gson().fromJson(result.toString(), Timer::class.java)
            } else {
                val error = wsResult.error
                if (error != null) {
                    Log.e(TAG, error.message!!)
                    throw error
                }
            }
            return timer
        }

        /**
         * Termine le timer de l'utilisateur
         *
         * @param cookie String
         * @param jsonObject JSONObject
         * @return WSResult<JSONObject>
         */
        fun putStopTimer(cookie: String, jsonObject: JSONObject): WSResult<JSONObject> {
            Log.i(TAG, "Demande d'arrêt du timer")
            return WSRequest.requestObject(cookie, "timer/stop", "PUT", jsonObject)
        }

        /**
         * Récupère le timer une fois arrêté à partir du WSResult<JSONObject>
         *
         * @param wsResult WSResult<JSONObject>
         * @return Timer?
         */
        fun readPutStopTimer(wsResult: WSResult<JSONObject>): Timer? {
            Log.i(TAG, "Arrêt du timer")
            var timer: Timer? = null
            val result = wsResult.result
            if (result != null) {
                timer = Gson().fromJson(result.toString(), Timer::class.java)
            } else {
                val error = wsResult.error
                if (error != null) {
                    Log.e(TAG, error.message!!)
                    throw error
                }
            }
            return timer
        }

        /**
         * Supprime un timer de l'utilisateur
         *
         * @param cookie String
         * @param id Int
         * @return WSResult<JSONObject>
         */
        fun deleteTimer(cookie: String, id: Int): WSResult<JSONObject> {
            Log.i(TAG, "Demande de suppression du timer {$id}")
            return WSRequest.requestObject(cookie, "timer/$id", "DELETE", null)
        }

        /**
         * Récupère le timer une fois supprimé à partir du WSResult<JSONObject>
         *
         * @param wsResult WSResult<JSONObject>
         * @return Timer?
         */
        fun readDeleteTimer(wsResult: WSResult<JSONObject>): Timer? {
            Log.i(TAG, "Suppression du timer")
            var timer: Timer? = null
            val result = wsResult.result
            if (result != null) {
                timer = Gson().fromJson(result.toString(), Timer::class.java)
            } else {
                val error = wsResult.error
                if (error != null) {
                    Log.e(TAG, error.message!!)
                    throw error
                }
            }
            return timer
        }

    }
}