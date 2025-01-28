package fr.mpau.webservice

import android.util.Base64
import android.util.Log
import fr.mpau.exception.FonctionnalException
import fr.mpau.exception.TechnicalException
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.ConnectException
import java.net.HttpURLConnection
import java.net.URL

/**
 * Classe d'envoi d'une requête au WebService MPAU-WS
 *
 * Author: Jonathan
 * Created: 24/01/2025
 * Last Updated: 24/01/2025
 */
class WSRequest {
    companion object {

        /**
         * Attributs
         */

        private const val TAG = "WSRequest"
        private const val BASE_REQUEST_URL = "[REDACTED]"

        /**
         * Envoi une requête au WebService selon les paramètres fournis et retourne un WSResult<JSONObject>
         *
         * @param cookie String
         * @param servlet String
         * @param method String
         * @param body Any?
         * @return WSResult<JSONObject>
         */
        fun requestObject(
            cookie: String,
            servlet: String,
            method: String,
            body: Any?
        ): WSResult<JSONObject> {
            @Suppress("UNCHECKED_CAST")
            return requestGlobal(cookie, servlet, method, body, false) as WSResult<JSONObject>
        }

        /**
         * Envoi une requête au WebService selon les paramètres fournis et retourne un WSResult<JSONArray>
         *
         * @param cookie String
         * @param servlet String
         * @param method String
         * @param body Any?
         * @return WSResult<JSONArray>
         */
        fun requestObjects(
            cookie: String,
            servlet: String,
            method: String,
            body: Any?
        ): WSResult<JSONArray> {
            @Suppress("UNCHECKED_CAST")
            return requestGlobal(cookie, servlet, method, body, true) as WSResult<JSONArray>
        }

        /**
         * Envoi une requête au WebService avec les paramètres fournis et récupére le WSResult<JSONObject> / WSResult<JSONArray> résultant
         *
         * @param cookie String
         * @param servlet String
         * @param method String
         * @param body Any?
         * @param isArray Boolean
         * @return WSResult<Any>
         */
        private fun requestGlobal(
            cookie: String,
            servlet: String,
            method: String,
            body: Any?,
            isArray: Boolean
        ): WSResult<Any> {
            var taskResult: WSResult<Any>
            var urlConnection: HttpURLConnection? = null
            try {
                val requestURL = BASE_REQUEST_URL + servlet
                Log.i(TAG, "{** Request WS : [$method] $requestURL **}")
                val url = URL(requestURL)
                urlConnection = url.openConnection() as HttpURLConnection
                // Configure le timeout de la connexion à 5s si pas de réponse
                urlConnection.connectTimeout = 5000
                // Création de l'Authentification String
                val basicAuth = "Basic " + String(
                    Base64.encode(cookie.toByteArray(), Base64.NO_WRAP)
                )
                // Création des Headers Params
                urlConnection.setRequestProperty("Authorization", basicAuth)
                urlConnection.requestMethod = method
                urlConnection.setRequestProperty("Accept", "application/json")
                urlConnection.setRequestProperty("Content-Type", "application/json")
                // Création du Body (si nécessaire)
                if (body != null) {
                    val bodyContent = body as JSONObject
                    val os = urlConnection.outputStream
                    val osw = OutputStreamWriter(os, "UTF-8")
                    osw.write(bodyContent.toString())
                    osw.flush()
                    os.close()
                }
                // Récupération du code retour
                val responseCode = urlConnection.responseCode
                Log.i(TAG, "{** Response Code : $responseCode **}")
                // Traitement en fonction du code retour
                when (responseCode) {
                    // Réponse valide et fournie
                    200 -> {
                        val bufferedReader =
                            BufferedReader(InputStreamReader(urlConnection.inputStream))
                        val stringBuilder = StringBuilder()
                        var line: String?
                        while ((bufferedReader.readLine().also { line = it }) != null) {
                            stringBuilder.append(line).append("\n")
                        }
                        bufferedReader.close()
                        if (isArray) {
                            // Parse le résultat en JSONArray
                            val jsonArray = JSONArray(stringBuilder.toString())
                            taskResult = WSResult(jsonArray)
                        } else {
                            // Parse le résultat en JSONObject
                            val jsonObject = JSONObject(stringBuilder.toString())
                            taskResult = WSResult(jsonObject)
                        }
                    }
                    // Réponse valide et vide
                    204 -> {
                        taskResult = if (isArray) {
                            WSResult(JSONArray())
                        } else {
                            WSResult(JSONObject())
                        }
                    }
                    // Conditions non valides
                    412 -> taskResult =
                        WSResult(FonctionnalException("Les conditions ne sont pas valides"))
                    // Erreur du webservice
                    else -> taskResult =
                        WSResult(TechnicalException("Le webservice a rencontré une erreur"))
                }
            } catch (ce: ConnectException) {
                // Si le service ne réponds pas, retourner Technical Exception
                val error = "Le WebService ne réponds pas"
                Log.e(TAG, error)
                Log.e(TAG, ce.message!!)
                taskResult = WSResult(TechnicalException(error))
            } catch (e: Exception) {
                // Si autre erreur, retourner Technical Exception
                Log.e(TAG, e.message!!)
                taskResult = WSResult(TechnicalException(e.message!!))
            } finally {
                urlConnection?.disconnect()
            }
            return taskResult
        }

    }
}