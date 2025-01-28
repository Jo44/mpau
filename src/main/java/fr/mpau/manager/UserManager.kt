package fr.mpau.manager

import android.util.Log
import com.google.gson.Gson
import fr.mpau.bean.User
import fr.mpau.webservice.WSRequest
import fr.mpau.webservice.WSResult
import org.json.JSONObject

/**
 * User Manager
 *
 * Author: Jonathan
 * Created: 25/01/2018
 * Last Updated: 24/01/2025
 */
class UserManager {
    companion object {

        /**
         * Attributs
         */

        private const val TAG = "UserManager"

        /**
         * Récupère l'utilisateur à partir du cookie
         *
         * @param cookie String
         * @return WSResult<JSONObject>
         */
        fun getUser(cookie: String): WSResult<JSONObject> {
            Log.i(TAG, "Demande de récupération de l'utilisateur")
            return WSRequest.requestObject(cookie, "user", "GET", null)
        }

        /**
         * Récupère l'utilisateur à partir du WSResult<JSONObject>
         *
         * @param wsResult WSResult<JSONObject>
         * @return User?
         */
        fun readGetUser(wsResult: WSResult<JSONObject>): User? {
            Log.i(TAG, "Récupération de l'utilisateur")
            var user: User? = null
            val result = wsResult.result
            if (result != null) {
                user = Gson().fromJson(result.toString(), User::class.java)
            } else {
                val error = wsResult.error
                if (error != null) {
                    Log.e(TAG, error.message!!)
                    throw error
                }
            }
            return user
        }

    }
}
