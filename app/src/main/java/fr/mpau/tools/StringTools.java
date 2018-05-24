package fr.mpau.tools;

import android.util.Log;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Locale;

/**
 * Tools StringTools
 * -> capitalizeFirstLetter(String value)
 * -> getDateStringFromTimestamp(long timestamp)
 * -> getShortDateStringFromTimestamp(long timestamp)
 * -> getTimeStringFromTimestamp(long timestamp, String separator)
 * -> hashMD5(String value)
 * -> formatComment(String rawComment)
 * <p>
 * Author: Jonathan B.
 * Created: 19/11/2017
 * Last Updated: 11/02/2018
 */

public class StringTools {

    /**
     * Attributs
     */
    private static final String ERROR = "[ERROR]";

    /**
     * Met en majuscule la première lettre d'un string
     *
     * @param value String
     * @return String
     */
    public static String capitalizeFirstLetter(String value) {
        String retour = null;
        if (value != null) {
            if (value.length() == 0) {
                retour = value;
            } else {
                StringBuilder result = new StringBuilder(value);
                result.replace(0, 1, result.substring(0, 1).toUpperCase());
                retour = result.toString();
            }
        }
        return retour;
    }

    /**
     * Récupère la date en String au format "dd/MM/yyyy" à partir d'un timestamp
     *
     * @param timestamp long
     * @return String
     */
    public static String getDateStringFromTimestamp(long timestamp) {
        String retour = "";
        if (timestamp != 0) {
            Calendar datetime = Calendar.getInstance();
            datetime.setTimeInMillis(timestamp * 1000);
            retour = String.format(Locale.getDefault(), "%02d", datetime.get(Calendar.DAY_OF_MONTH)) + "/" + String.format(Locale.getDefault(), "%02d", (datetime.get(Calendar.MONTH) + 1)) + "/" + datetime.get(Calendar.YEAR);
        }
        return retour;
    }

    /**
     * Récupère la date en String au format court "dd/MM" à partir d'un timestamp
     *
     * @param timestamp long
     * @return String
     */
    public static String getShortDateStringFromTimestamp(long timestamp) {
        String retour = "";
        if (timestamp != 0) {
            Calendar datetime = Calendar.getInstance();
            datetime.setTimeInMillis(timestamp * 1000);
            retour = String.format(Locale.getDefault(), "%02d", datetime.get(Calendar.DAY_OF_MONTH)) + "/" + String.format(Locale.getDefault(), "%02d", (datetime.get(Calendar.MONTH) + 1));
        }
        return retour;
    }

    /**
     * Récupère l'heure en String au format "HH:mm" à partir d'un timestamp et ':' = separator
     *
     * @param timestamp long
     * @return String
     */
    public static String getTimeStringFromTimestamp(long timestamp, String separator) {
        String retour = "";
        if (timestamp != 0) {
            Calendar datetime = Calendar.getInstance();
            datetime.setTimeInMillis(timestamp * 1000);
            retour = String.format(Locale.getDefault(), "%02d", datetime.get(Calendar.HOUR_OF_DAY)) + separator + String.format(Locale.getDefault(), "%02d", datetime.get(Calendar.MINUTE));
        }
        return retour;
    }

    /**
     * Permet de hasher un string en MD5
     *
     * @param value String
     * @return String
     */
    public static String hashMD5(String value) {
        String hash = "";
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(value.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            hash = number.toString(16);
        } catch (NoSuchAlgorithmException e) {
            String erreur = "Erreur de cryptage MD5";
            Log.e(ERROR, erreur);
        }
        return hash;
    }

    /**
     * Formatte le commentaire pour ne conserver que 50 caractères maximum ou le message 'Pas de commentaire' (pour l'affichage dans la liste des interventions)
     *
     * @param rawComment String
     * @return String
     */
    public static String formatComment(String rawComment) {
        String comment;
        if (rawComment == null || rawComment.trim().length() == 0) {
            comment = "(Pas de commentaire)";
        } else {
            rawComment = rawComment.replaceAll("(\r\n|\n)", " ");
            if (rawComment.trim().length() > 50) {
                comment = rawComment.trim().substring(0, 47) + "...";
            } else {
                comment = rawComment.trim();
            }
        }
        return comment;
    }

}
