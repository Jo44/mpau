package fr.mpau.tools.dialog.fragments;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;

import java.util.Calendar;

/**
 * Tools TimePickerFragment
 * -> Initialise le Time Picker Dialog
 * <p>
 * Author: Jonathan B.
 * Created: 18/11/2017
 */

public class TimePickerFragment extends DialogFragment {

    /**
     * Initialise un Time Picker Dialog
     *
     * @param savedInstanceState Bundle
     * @return Dialog
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Utilise l'heure actuelle par défaut dans le picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        return new TimePickerDialog(getActivity(), (TimePickerDialog.OnTimeSetListener) getActivity(), hour, minute, DateFormat.is24HourFormat(getActivity()));
    }
}