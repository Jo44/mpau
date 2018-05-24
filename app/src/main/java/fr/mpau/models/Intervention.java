package fr.mpau.models;

/**
 * Classe modèle métier d'une intervention
 * <p>
 * Author: Jonathan B.
 * Created: 19/01/2018
 */

public class Intervention {
    /**
     * Attributs
     */
    private int interId;
    private int interUserId;
    private long interDate;
    private int interDuree;
    private String interSecteur;
    private boolean interSmur;
    private int interTypeId;
    private int interSoustypeId;
    private int interAgepatientId;
    private String interCommentaire;

    /**
     * Constructeur
     */
    public Intervention(int interId, int interUserId, long interDate, int interDuree, String interSecteur, boolean interSmur, int interTypeId, int interSoustypeId, int interAgepatientId, String interCommentaire) {
        this.interId = interId;
        this.interUserId = interUserId;
        this.interDate = interDate;
        this.interDuree = interDuree;
        this.interSecteur = interSecteur;
        this.interSmur = interSmur;
        this.interTypeId = interTypeId;
        this.interSoustypeId = interSoustypeId;
        this.interAgepatientId = interAgepatientId;
        this.interCommentaire = interCommentaire;
    }

    /**
     * Getters
     */
    public int getInterId() {
        return interId;
    }

    public long getInterDate() {
        return interDate;
    }

    public int getInterDuree() {
        return interDuree;
    }

    public String getInterSecteur() {
        return interSecteur;
    }

    public boolean isInterSmur() {
        return interSmur;
    }

    public int getInterTypeId() {
        return interTypeId;
    }

    public int getInterSoustypeId() {
        return interSoustypeId;
    }

    public int getInterAgepatientId() {
        return interAgepatientId;
    }

    public String getInterCommentaire() {
        return interCommentaire;
    }
}
