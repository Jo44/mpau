package fr.mpau.tools;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * Classe de filtre pour imposer un range possible d'un EditText de type Number
 * <p>
 * Author: Jonathan B.
 * Created: 17/01/2018
 */


public class MinMaxFilter implements InputFilter {

    /**
     * Attributs
     */
    private int mIntMin;
    private int mIntMax;

    /**
     * Constructeur
     *
     * @param minValue int
     * @param maxValue int
     */
    public MinMaxFilter(int minValue, int maxValue) {
        this.mIntMin = minValue;
        this.mIntMax = maxValue;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            int input = Integer.parseInt(dest.toString() + source.toString());
            if (isInRange(mIntMin, mIntMax, input))
                return null;
        } catch (NumberFormatException nfe) {
        }
        return "";
    }

    /**
     * Détermine si la valeur est dans le range
     *
     * @param min   int
     * @param max   int
     * @param value int
     * @return boolean
     */
    private boolean isInRange(int min, int max, int value) {
        return max > min ? value >= min && value <= max : value >= max && value <= min;
    }
}