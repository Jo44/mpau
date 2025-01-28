package fr.mpau.tool.list

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import fr.mpau.R

/**
 * Tool - Adaptateur pour afficher les spinners
 *
 * Author: Jonathan
 * Created: 20/11/2017
 * Last Updated: 27/01/2025
 */
class SpinnersAdapter<T>(context: Context, resource: Int, objects: List<T>) :
    ArrayAdapter<T>(context, resource, objects) {

    /**
     * Détermine si l'élément de la liste est sélectionnable
     *
     * @param position Int
     * @return Boolean
     */
    override fun isEnabled(position: Int): Boolean {
        var selectable = false
        if (position != 0) {
            selectable = true
        }
        return selectable
    }

    /**
     * Détermine la couleur des éléments de la liste en fonction de leur position
     *
     * @param position Int
     * @param convertView View?
     * @param parent ViewGroup
     * @return View
     */
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        val tv = view as TextView
        if (position == 0) {
            // Couleur pour item non sélectionnable
            tv.setTextColor(ContextCompat.getColor(context, R.color.orange))
        } else {
            // Couleur pour item sélectionnable
            tv.setTextColor(ContextCompat.getColor(context, R.color.lightergrey))
        }
        return view
    }

}