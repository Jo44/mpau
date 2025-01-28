package fr.mpau.tool

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import fr.mpau.bean.User

/**
 * Tool - UserSettings
 *
 * - Stockage et récupération du cookie de l'utilisateur depuis les préférences partagées
 * - Stockage et récupération des infos de l'utilisateur depuis les préférences partagées
 * - Stockage et récupération du paramètre signalant le besoin de refresh de la liste des interventions depuis les préférences partagées
 * - Stockage et récupération du paramètre signalant le besoin de restart la liste des interventions depuis les préférences partagées
 *
 * -> setCookie(String cookie, Context context)
 * -> readCookie(Context context)
 * -> deleteCookie(Context context)
 *
 * -> setUser(User user, Context context)
 * -> readUser(Context context)
 * -> deleteUser(Context context)
 *
 * -> setRefreshListInter(Context context)
 * -> readRefreshListInter(Context context)
 * -> deleteRefreshListInter(Context context)
 *
 * -> setRestartListInter(Context context)
 * -> readRestartListInter(Context context)
 * -> deleteRestartListInter(Context context)
 *
 * Author: Jonathan
 * Created: 18/11/2017
 * Last Updated: 24/01/2025
 */
object UserSettings {

    /**
     * Attributs
     */

    private const val TAG = "UserSettings"
    private const val MPAU_PREFS = "MPAU_Prefs"

    /**
     * Enregistre le cookie dans les préférences partagées
     *
     * @param cookie String
     * @param context Context
     */
    fun setCookie(cookie: String, context: Context) {
        // Récupération des préférences
        val preferences: SharedPreferences =
            context.getSharedPreferences(MPAU_PREFS, Context.MODE_PRIVATE)
        // Ajout du cookie
        val edit: SharedPreferences.Editor = preferences.edit()
        edit.putString("auth_string", cookie)
        edit.apply()
        Log.d(TAG, "Cookie créé avec succès")
    }

    /**
     * Récupère le cookie dans les préférences partagées
     *
     * @param context Context
     * @return String
     */
    fun readCookie(context: Context): String {
        val cookie: String
        // Récupération des préférences
        val preferences: SharedPreferences =
            context.getSharedPreferences(MPAU_PREFS, Context.MODE_PRIVATE)
        // Récupération du cookie
        cookie = preferences.getString("auth_string", "").toString()
        return cookie
    }

    /**
     * Supprime le cookie des préférences partagées
     *
     * @param context Context
     */
    fun deleteCookie(context: Context) {
        // Récupération des préférences
        val preferences: SharedPreferences =
            context.getSharedPreferences(MPAU_PREFS, Context.MODE_PRIVATE)
        // Suppression du cookie
        val edit: SharedPreferences.Editor = preferences.edit()
        edit.remove("auth_string")
        edit.apply()
        Log.d(TAG, "Cookie supprimé avec succès")
    }

    /**
     * Enregistre l'utilisateur dans les préférences partagées
     *
     * @param user User
     * @param context Context
     */
    fun setUser(user: User, context: Context) {
        // Récupération des préférences
        val preferences: SharedPreferences =
            context.getSharedPreferences(MPAU_PREFS, Context.MODE_PRIVATE)
        // Ajout de l'utilisateur
        val edit: SharedPreferences.Editor = preferences.edit()
        edit.putInt("user_id", user.id)
        edit.putString("user_name", user.name)
        edit.putString("user_pass", user.pass)
        edit.putString("user_email", user.email)
        edit.putInt("user_nb_inter", user.nbInter)
        edit.putInt("user_inter_id_max", user.interIdMax)
        edit.putLong("user_inscription_date", user.inscriptionDate)
        edit.apply()
        Log.d(TAG, "Utilisateur enregistré avec succès")
    }

    /**
     * Récupère l'utilisateur dans les préférences partagées
     *
     * @param context Context
     * @return User?
     */
    fun readUser(context: Context): User? {
        val user: User?
        // Récupération des préférences
        val preferences: SharedPreferences =
            context.getSharedPreferences(MPAU_PREFS, Context.MODE_PRIVATE)
        // Récupération de l'utilisateur
        val id: Int = preferences.getInt("user_id", 0)
        val name: String = preferences.getString("user_name", "").toString()
        val pass: String = preferences.getString("user_pass", "").toString()
        val email: String = preferences.getString("user_email", "").toString()
        val nbInter: Int = preferences.getInt("user_nb_inter", 0)
        val interIdMax: Int = preferences.getInt("user_inter_id_max", 0)
        val inscriptionDate: Long = preferences.getLong("user_inscription_date", 0)
        if (id != 0) {
            user = User(id, name, pass, email, nbInter, interIdMax, inscriptionDate)
        } else {
            user = null
            Log.e(TAG, "Echec de récupération de l'utilisateur")
        }
        return user
    }

    /**
     * Supprime l'utilisateur des préférences partagées
     *
     * @param context Context
     */
    fun deleteUser(context: Context) {
        // Récupération des préférences
        val preferences: SharedPreferences =
            context.getSharedPreferences(MPAU_PREFS, Context.MODE_PRIVATE)
        // Suppression de l'utilisateur
        val edit: SharedPreferences.Editor = preferences.edit()
        edit.remove("user_id")
        edit.remove("user_name")
        edit.remove("user_pass")
        edit.remove("user_email")
        edit.remove("user_nb_inter")
        edit.remove("user_inter_id_max")
        edit.remove("user_inscription_date")
        edit.apply()
        Log.d(TAG, "Utilisateur supprimé avec succès")
    }

    /**
     * Enregistre le paramètre 'refresh_list_inter' dans les préférences partagées
     *
     * @param context Context
     */
    fun setRefreshListInter(context: Context) {
        // Récupération des préférences
        val preferences: SharedPreferences =
            context.getSharedPreferences(MPAU_PREFS, Context.MODE_PRIVATE)
        // Ajout du paramètre
        val edit: SharedPreferences.Editor = preferences.edit()
        edit.putBoolean("refresh_list_inter", true)
        edit.apply()
    }

    /**
     * Récupère le paramètre 'refresh_list_inter' dans les préférences partagées
     *
     * @param context Context
     * @return Boolean
     */
    fun readRefreshListInter(context: Context): Boolean {
        val refresh: Boolean
        // Récupération des préférences
        val preferences: SharedPreferences =
            context.getSharedPreferences(MPAU_PREFS, Context.MODE_PRIVATE)
        // Récupération du paramètre
        refresh = preferences.getBoolean("refresh_list_inter", false)
        return refresh
    }

    /**
     * Supprime le paramètre 'refresh_list_inter' des préférences partagées
     *
     * @param context Context
     */
    fun deleteRefreshListInter(context: Context) {
        // Récupération des préférences
        val preferences: SharedPreferences =
            context.getSharedPreferences(MPAU_PREFS, Context.MODE_PRIVATE)
        // Suppression du paramètre
        val edit: SharedPreferences.Editor = preferences.edit()
        edit.remove("refresh_list_inter")
        edit.apply()
    }

    /**
     * Enregistre le paramètre 'restart_list_inter' dans les préférences partagées
     *
     * @param context Context
     */
    fun setRestartListInter(context: Context) {
        // Récupération des préférences
        val preferences: SharedPreferences =
            context.getSharedPreferences(MPAU_PREFS, Context.MODE_PRIVATE)
        // Ajout du paramètre
        val edit: SharedPreferences.Editor = preferences.edit()
        edit.putBoolean("restart_list_inter", true)
        edit.apply()
    }

    /**
     * Récupère le paramètre 'restart_list_inter' dans les préférences partagées
     *
     * @param context Context
     * @return Boolean
     */
    fun readRestartListInter(context: Context): Boolean {
        val restart: Boolean
        // Récupération des préférences
        val preferences: SharedPreferences =
            context.getSharedPreferences(MPAU_PREFS, Context.MODE_PRIVATE)
        // Récupération du paramètre
        restart = preferences.getBoolean("restart_list_inter", false)
        return restart
    }

    /**
     * Supprime le paramètre 'restart_list_inter' des préférences partagées
     *
     * @param context Context
     */
    fun deleteRestartListInter(context: Context) {
        // Récupération des préférences
        val preferences: SharedPreferences =
            context.getSharedPreferences(MPAU_PREFS, Context.MODE_PRIVATE)
        // Suppression du paramètre
        val edit: SharedPreferences.Editor = preferences.edit()
        edit.remove("restart_list_inter")
        edit.apply()
    }

}
