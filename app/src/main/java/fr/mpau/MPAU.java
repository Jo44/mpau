package fr.mpau;

import android.app.Application;
import android.content.Context;

/**
 * Author: Jonathan B.
 * Created: 05/02/2018
 */

public class MPAU extends Application {

    /**
     * Attributs
     */
    private static Context context;

    /**
     * Getter
     *
     * @return Context
     */
    public static Context getAppContext() {
        return MPAU.context;
    }

    /**
     * OnCreate
     */
    public void onCreate() {
        super.onCreate();
        MPAU.context = getApplicationContext();
    }
}