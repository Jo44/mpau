package fr.mpau.tool.list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RelativeLayout
import android.widget.TextView
import fr.mpau.R
import fr.mpau.bean.Intervention
import fr.mpau.enums.AgePatientIntervention
import fr.mpau.enums.SubtypeIntervention
import fr.mpau.enums.TypeIntervention
import fr.mpau.tool.StringTools
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Tool - Adaptateur pour afficher la ListView des interventions
 *
 * Author: Jonathan
 * Created: 19/01/2018
 * Last Updated: 27/01/2025
 */
class InterventionsAdapter(
    private val context: Context,
    resource: Int,
    list: ArrayList<Intervention>
) : ArrayAdapter<Intervention?>(context, resource, list as List<Intervention?>) {

    /**
     * Attributs
     */

    private val listInterventions: ArrayList<Intervention> = list

    /**
     * Construction d'une entrée (une ligne d'intervention)
     *
     * @param position Int
     * @param convertView View?
     * @param parent ViewGroup
     * @return View
     */
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            val inflater: LayoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.row_intervention, parent, false)
        } else {
            view = view as RelativeLayout
        }
        // Récupération des éléments du layout
        val mainTypeInter = view!!.findViewById<TextView>(R.id.mainTypeInter)
        val subTypeInter = view.findViewById<TextView>(R.id.subTypeInter)
        val smurInter = view.findViewById<TextView>(R.id.smurInter)
        val agePatientInter = view.findViewById<TextView>(R.id.agePatientInter)
        val sectorInter = view.findViewById<TextView>(R.id.sectorInter)
        val commentInter = view.findViewById<TextView>(R.id.commentInter)
        val datetimeInter = view.findViewById<TextView>(R.id.datetimeInter)
        val durationInter = view.findViewById<TextView>(R.id.durationInter)
        // Récupération de l'item Intervention
        val inter: Intervention = listInterventions[position]
        // Inflation des éléments
        val mainType = TypeIntervention.entries[inter.mainTypeId - 1].getValue()
        mainTypeInter.text = mainType
        val subType = " - " + SubtypeIntervention.entries[inter.sousTypeId - 1].getValue()
        subTypeInter.text = subType
        if (inter.smur) {
            val smur = "   " + context.resources.getString(R.string.smur_tag)
            smurInter.text = smur
            smurInter.visibility = View.VISIBLE
        } else {
            smurInter.visibility = View.GONE
        }
        agePatientInter.text = AgePatientIntervention.entries[inter.agePatientId - 1].getValue()
        val sector = " ans - " + inter.secteur.trim()
        sectorInter.text = sector
        // Formattage du commentaire
        val rawComment: String = inter.commentaire
        val comment: String = StringTools.formatComment(rawComment)
        commentInter.text = comment
        // Formattage de l'heure
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = inter.dateInter * 1000
        val date = calendar.time
        val formatDateTime = SimpleDateFormat("dd/MM/yyyy  HH:mm", Locale.getDefault())
        val dateTime = formatDateTime.format(date) + "\n"
        datetimeInter.text = dateTime
        val duration = "  " + inter.duree.toString() + " min"
        durationInter.text = duration
        return view
    }

}