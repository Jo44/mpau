package fr.mpau.tool

import android.text.InputFilter
import android.text.Spanned

/**
 * Tool - Classe de filtre pour imposer un range possible d'un EditText de type Number
 *
 * Author: Jonathan
 * Created: 17/01/2018
 * Last Updated: 24/01/2025
 */
class MinMaxFilter(private val mIntMin: Int, private val mIntMax: Int) : InputFilter {

    /**
     * Filter
     *
     * @param source CharSequence
     * @param start Int
     * @param end Int
     * @param dest Spanned
     * @param dstart Int
     * @param dend Int
     * @return CharSequence?
     */
    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        try {
            val input = (dest.toString() + source.toString()).toInt()
            if (isInRange(mIntMin, mIntMax, input)) return null
        } catch (_: NumberFormatException) {
        }
        return ""
    }

    /**
     * Détermine si la valeur est dans le range
     *
     * @param min Int
     * @param max Int
     * @param value Int
     * @return Boolean
     */
    private fun isInRange(min: Int, max: Int, value: Int): Boolean {
        return if (max > min) value in min..max else value in max..min
    }

}