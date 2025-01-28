package fr.mpau.tool.dialog.fragment

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import androidx.fragment.app.DialogFragment
import java.util.Calendar

/**
 * Tool - TimePickerFragment
 * -> Initialise le Time Picker Dialog
 *
 * Author: Jonathan
 * Created: 18/11/2017
 * Last Updated: 24/01/2025
 */
class TimePickerFragment : DialogFragment() {

    /**
     * Initialise un Time Picker Dialog
     *
     * @param savedInstanceState Bundle?
     * @return Dialog
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Utilise l'heure actuelle par défaut dans le picker
        val cal = Calendar.getInstance()
        val hour = cal[Calendar.HOUR_OF_DAY]
        val minute = cal[Calendar.MINUTE]
        return TimePickerDialog(
            activity,
            activity as TimePickerDialog.OnTimeSetListener?,
            hour,
            minute,
            DateFormat.is24HourFormat(activity)
        )
    }

}