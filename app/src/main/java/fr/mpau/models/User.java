package fr.mpau.models;

/**
 * Classe modèle métier d'un utilisateur
 * <p>
 * Author: Jonathan B.
 * Created: 25/01/2018
 */

public class User {

    /**
     * Attributs
     */
    private int userId;
    private String userName;
    private String userEmail;
    private int userTotalInter;
    private int userInterIdMax;
    private long userInscriptionDate;

    /**
     * Constructeur
     */
    public User(int userId, String userName, String userEmail, int userTotalInter, int userInterIdMax, long userInscriptionDate) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userTotalInter = userTotalInter;
        this.userInterIdMax = userInterIdMax;
        this.userInscriptionDate = userInscriptionDate;
    }

    /**
     * Getters
     */
    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public int getUserTotalInter() {
        return userTotalInter;
    }

    public int getUserInterIdMax() {
        return userInterIdMax;
    }

    public long getUserInscriptionDate() {
        return userInscriptionDate;
    }
}
