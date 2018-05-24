package fr.mpau.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

import fr.mpau.models.User;

/**
 * Tools UserSettings
 * <p>
 * - Stockage et récupération du cookie de l'utilisateur depuis les préférences partagées
 * - Stockage et récupération des infos de l'utilisateur depuis les préférences partagées
 * - Stockage et récupération du paramètre signalant le besoin de refresh de la liste des interventions depuis les préférences partagées
 * - Stockage et récupération du paramètre signalant le besoin de restart la liste des interventions depuis les préférences partagées
 * <p>
 * -> setCookie(String cookie, Context context)
 * -> readCookie(Context context)
 * -> deleteCookie(Context context)
 * <p>
 * -> setUser(User user, Context context)
 * -> readUser(Context context)
 * -> deleteUser(Context context)
 * <p>
 * -> setRefreshListInter(Context context)
 * -> readRefreshListInter(Context context)
 * -> deleteRefreshListInter(Context context)
 * <p>
 * -> setRestartListInter(Context context)
 * -> readRestartListInter(Context context)
 * -> deleteRestartListInter(Context context)
 * <p>
 * Author: Jonathan B.
 * Created: 18/11/2017
 * Last Updated: 10/02/2018
 */

public class UserSettings {

    /**
     * Attributs
     */
    private static final String ERROR = "[ERROR]";
    private static final String INFO = "[INFO]";

    /**
     * Enregistre le cookie dans les préférences partagées
     *
     * @param cookie  String
     * @param context Context
     */
    public static void setCookie(String cookie, Context context) {
        // Récupération des préférences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        // Ajout du cookie
        Editor edit = preferences.edit();
        edit.putString("authString", cookie);
        edit.apply();
        Log.e(INFO, "Cookie créé avec succès");
    }

    /**
     * Récupère le cookie dans les préférences partagées
     *
     * @param context Context
     * @return User
     */
    public static String readCookie(Context context) {
        String cookie;
        // Récupération des préférences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        // Récupération du cookie
        cookie = preferences.getString("authString", "");
        return cookie;
    }

    /**
     * Supprime le cookie des préférences partagées
     *
     * @param context Context
     */
    public static void deleteCookie(Context context) {
        // Récupération des préférences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        // Suppression du cookie
        Editor edit = preferences.edit();
        edit.remove("authString");
        edit.apply();
        Log.e(INFO, "Cookie supprimé avec succès");
    }

    /**
     * Enregistre l'utilisateur dans les préférences partagées
     *
     * @param user    User
     * @param context Context
     */
    public static void setUser(User user, Context context) {
        // Récupération des préférences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        // Ajout de l'utilisateur
        Editor edit = preferences.edit();
        edit.putInt("userId", user.getUserId());
        edit.putString("userName", user.getUserName());
        edit.putString("userEmail", user.getUserEmail());
        edit.putInt("userTotalInter", user.getUserTotalInter());
        edit.putInt("userInterIdMax", user.getUserInterIdMax());
        edit.putLong("userInscriptionDate", user.getUserInscriptionDate());
        edit.apply();
        Log.e(INFO, "Utilisateur enregistré avec succès");
    }

    /**
     * Récupère l'utilisateur dans les préférences partagées
     *
     * @param context Context
     * @return User
     */
    public static User readUser(Context context) {
        User user;
        // Récupération des préférences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        // Récupération de l'utilisateur
        int userId = preferences.getInt("userId", 0);
        String userName = preferences.getString("userName", "");
        String userEmail = preferences.getString("userEmail", "");
        int userTotalInter = preferences.getInt("userTotalInter", 0);
        int userInterIdMax = preferences.getInt("userInterIdMax", 0);
        long userInscriptionDate = preferences.getLong("userInscriptionDate", 0);
        if (userId != 0) {
            user = new User(userId, userName, userEmail, userTotalInter, userInterIdMax, userInscriptionDate);
        } else {
            user = null;
            Log.e(ERROR, "Echec de récupération de l'utilisateur");
        }
        return user;
    }

    /**
     * Supprime l'utilisateur des préférences partagées
     *
     * @param context Context
     */
    public static void deleteUser(Context context) {
        // Récupération des préférences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        // Suppression de l'utilisateur
        Editor edit = preferences.edit();
        edit.remove("userId");
        edit.remove("userName");
        edit.remove("userEmail");
        edit.remove("userTotalInter");
        edit.remove("userInterIdMax");
        edit.remove("userInscriptionDate");
        edit.apply();
        Log.e(INFO, "Utilisateur supprimé avec succès");
    }

    /**
     * Enregistre le paramètre 'RefreshListInter' dans les préférences partagées
     *
     * @param context Context
     */
    public static void setRefreshListInter(Context context) {
        // Récupération des préférences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        // Ajout du paramètre
        Editor edit = preferences.edit();
        edit.putBoolean("refreshListInter", true);
        edit.apply();
    }

    /**
     * Récupère le paramètre 'RefreshListInter' dans les préférences partagées
     *
     * @param context Context
     * @return boolean
     */
    public static boolean readRefreshListInter(Context context) {
        boolean refresh;
        // Récupération des préférences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        // Récupération du paramètre
        refresh = preferences.getBoolean("refreshListInter", false);
        return refresh;
    }

    /**
     * Supprime le paramètre 'RefreshListInter' des préférences partagées
     *
     * @param context Context
     */
    public static void deleteRefreshListInter(Context context) {
        // Récupération des préférences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        // Suppression du paramètre
        Editor edit = preferences.edit();
        edit.remove("refreshListInter");
        edit.apply();
    }

    /**
     * Enregistre le paramètre 'RestartListInter' dans les préférences partagées
     *
     * @param context Context
     */
    public static void setRestartListInter(Context context) {
        // Récupération des préférences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        // Ajout du paramètre
        Editor edit = preferences.edit();
        edit.putBoolean("restartListInter", true);
        edit.apply();
    }

    /**
     * Récupère le paramètre 'RestartListInter' dans les préférences partagées
     *
     * @param context Context
     * @return boolean
     */
    public static boolean readRestartListInter(Context context) {
        boolean restart;
        // Récupération des préférences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        // Récupération du paramètre
        restart = preferences.getBoolean("restartListInter", false);
        return restart;
    }

    /**
     * Supprime le paramètre 'RestartListInter' des préférences partagées
     *
     * @param context Context
     */
    public static void deleteRestartListInter(Context context) {
        // Récupération des préférences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        // Suppression du paramètre
        Editor edit = preferences.edit();
        edit.remove("restartListInter");
        edit.apply();
    }

}
