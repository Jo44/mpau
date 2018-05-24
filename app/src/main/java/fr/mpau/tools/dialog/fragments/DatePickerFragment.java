package fr.mpau.tools.dialog.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import java.util.Calendar;
import java.util.IllegalFormatConversionException;

/**
 * Tools DatePickerFragment
 * -> Initialise le Date Picker Dialog
 * <p>
 * Author: Jonathan B.
 * Created: 17/11/2017
 */

public class DatePickerFragment extends DialogFragment {

    /**
     * Détermine si le périphérique est reconnu comme un 'Samsung cassé'
     * BUGFIX Samsung Galaxy S4, S5, Note 3 - Android 5.0.1
     *
     * @return boolean
     */
    private static boolean isBrokenSamsungDevice() {
        return (Build.MANUFACTURER.equalsIgnoreCase("samsung")
                && isBetweenAndroidVersions(
                Build.VERSION_CODES.LOLLIPOP,
                Build.VERSION_CODES.LOLLIPOP_MR1));
    }

    /**
     * Détermine si le périphérique tourne sous une version d'Android entre 5.0 et 5.1
     * BUGFIX Samsung Galaxy S4, S5, Note 3 - Android 5.0.1
     *
     * @param min int
     * @param max max
     * @return boolean
     */
    private static boolean isBetweenAndroidVersions(int min, int max) {
        return Build.VERSION.SDK_INT >= min && Build.VERSION.SDK_INT <= max;
    }

    /**
     * Initialise un Date Picker Dialog
     * BUGFIX Samsung Galaxy S4, S5, Note 3 - Android 5.0.1
     *
     * @param savedInstanceState Bundle
     * @return Dialog
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Utilise la date actuelle par défaut dans le picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        // BUGFIX Samsung Galaxy S4, S5, Note 3 - Android 5.0.1
        Context context = getActivity();
        if (isBrokenSamsungDevice()) {
            context = new ContextWrapper(getActivity()) {
                private Resources wrappedResources;

                @Override
                public Resources getResources() {
                    Resources r = super.getResources();
                    if (wrappedResources == null) {
                        wrappedResources = new Resources(r.getAssets(), r.getDisplayMetrics(), r.getConfiguration()) {
                            @NonNull
                            @Override
                            public String getString(int id, Object... formatArgs) throws NotFoundException {
                                try {
                                    return super.getString(id, formatArgs);
                                } catch (IllegalFormatConversionException ifce) {
                                    Log.e("DatePickerDialogFix", "IllegalFormatConversionException Fixed!");
                                    String template = super.getString(id);
                                    template = template.replaceAll("%" + ifce.getConversion(), "%s");
                                    return String.format(getConfiguration().locale, template, formatArgs);
                                }
                            }
                        };
                    }
                    return wrappedResources;
                }
            };
        }
        // ENDFIX
        return new DatePickerDialog(context, (DatePickerDialog.OnDateSetListener) getActivity(), year, month, day);
    }
}