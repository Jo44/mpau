package fr.mpau.tools.lists;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import fr.mpau.R;

/**
 * Tools CustomArrayAdapter
 * -> Initialise les List Adapters
 * <p>
 * Author: Jonathan B.
 * Created: 20/11/2017
 */

public class CustomArrayAdapter<T> extends ArrayAdapter<T> {


    public CustomArrayAdapter(@NonNull Context context, int resource, @NonNull List<T> objects) {
        super(context, resource, objects);
    }

    /**
     * Détermine si l'élément de la liste est sélectionnable
     *
     * @param position int
     * @return boolean
     */
    @Override
    public boolean isEnabled(int position) {
        boolean selectable = false;
        if (position != 0) {
            selectable = true;
        }
        return selectable;
    }

    /**
     * Détermine la couleur des éléments de la liste en fonction de leur position
     *
     * @param position    int
     * @param convertView View
     * @param parent      ViewGroup
     * @return View
     */
    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        TextView tv = (TextView) view;
        if (position == 0) {
            // Couleur pour item non sélectionnable
            tv.setTextColor(getContext().getResources().getColor(R.color.orange));
        } else {
            // Couleur pour item sélectionnable
            tv.setTextColor(getContext().getResources().getColor(R.color.lightgrey));
        }
        return view;
    }
}
