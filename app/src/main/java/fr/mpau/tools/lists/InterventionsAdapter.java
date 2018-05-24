package fr.mpau.tools.lists;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import fr.mpau.R;
import fr.mpau.enums.SubtypeIntervention;
import fr.mpau.enums.TypeIntervention;
import fr.mpau.models.Intervention;
import fr.mpau.tools.StringTools;

/**
 * Adapter pour afficher la ListView des interventions
 * <p>
 * Author: Jonathan B.
 * Created: 19/01/2018
 */

public class InterventionsAdapter extends ArrayAdapter<Intervention> {

    /**
     * Attributs
     */
    private final Context context;
    private ArrayList<Intervention> listInterventions;

    /**
     * Constructeur
     *
     * @param context           Context
     * @param resource          int
     * @param listInterventions ArrayList<Intervention>
     */
    public InterventionsAdapter(Context context, int resource, ArrayList<Intervention> listInterventions) {
        super(context, resource, listInterventions);
        this.context = context;
        this.listInterventions = listInterventions;
    }

    /**
     * Construction d'une entrée (une ligne d'intervention)
     *
     * @param position    int
     * @param convertView View
     * @param parent      ViewGroup
     * @return View
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (inflater != null) {
                convertView = inflater.inflate(R.layout.row_intervention, parent, false);
            }
        } else {
            convertView = (RelativeLayout) convertView;
        }
        // Récupération des éléments du layout
        TextView typeInter = convertView.findViewById(R.id.typeInter);
        TextView subtypeInter = convertView.findViewById(R.id.subtypeInter);
        TextView smurInter = convertView.findViewById(R.id.smurInter);
        TextView secteurInter = convertView.findViewById(R.id.secteurInter);
        TextView commentInter = convertView.findViewById(R.id.commentInter);
        TextView datetimeInter = convertView.findViewById(R.id.datetimeInter);
        // Récupération de l'item Intervention
        Intervention inter = listInterventions.get(position);
        // Inflate le layout
        // Affichage 'SMUR'
        if (inter.isInterSmur()) {
            String smurText = "   " + context.getResources().getString(R.string.smur_tag);
            smurInter.setText(smurText);
            smurInter.setVisibility(View.VISIBLE);
        } else {
            smurInter.setVisibility(View.GONE);
        }
        // Formattage de l'heure
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(inter.getInterDate() * 1000);
        Date date = calendar.getTime();
        SimpleDateFormat formatDateTime = new SimpleDateFormat("dd/MM/yyyy  HH:mm", Locale.getDefault());
        typeInter.setText(TypeIntervention.values()[inter.getInterTypeId() - 1].toString());
        String subType = " - " + SubtypeIntervention.values()[inter.getInterSoustypeId() - 1].toString();
        subtypeInter.setText(subType);
        secteurInter.setText(inter.getInterSecteur().trim());
        // Formattage du commentaire
        String rawComment = inter.getInterCommentaire();
        String comment = StringTools.formatComment(rawComment);
        commentInter.setText(comment);
        String dateTime = formatDateTime.format(date) + "\n";
        datetimeInter.setText(dateTime);
        return convertView;
    }
}