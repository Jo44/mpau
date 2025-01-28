package fr.mpau.tool.dialog.fragment

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.util.Calendar

/**
 * Tool - DatePickerFragment
 * -> Initialise le Date Picker Dialog
 *
 * Author: Jonathan
 * Created: 17/11/2017
 * Last Updated: 24/01/2025
 */
class DatePickerFragment : DialogFragment() {

    /**
     * Initialise un Date Picker Dialog
     *
     * @param savedInstanceState Bundle?
     * @return Dialog
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Utilise la date actuelle par défaut dans le picker
        val cal = Calendar.getInstance()
        val year = cal[Calendar.YEAR]
        val month = cal[Calendar.MONTH]
        val day = cal[Calendar.DAY_OF_MONTH]
        return DatePickerDialog(
            requireContext(),
            activity as DatePickerDialog.OnDateSetListener?,
            year,
            month,
            day
        )
    }

}