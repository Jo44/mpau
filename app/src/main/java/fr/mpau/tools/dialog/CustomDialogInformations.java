package fr.mpau.tools.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import fr.mpau.R;

/**
 * Author: Jonathan B.
 * Created: 08/02/2018
 * Last updated: 12/02/2018
 */

public class CustomDialogInformations extends AlertDialog {

    /**
     * Attribut
     */
    private String mode;

    /**
     * Constructeurs
     */
    public CustomDialogInformations(Context context, String mode) {
        super(context, R.style.CustomAlertDialog);
        this.mode = mode;
    }

    /**
     * Initialisation
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (mode != null && mode.equals("INTER")) {
            setContentView(R.layout.dialog_informations_inter);
        } else {
            setContentView(R.layout.dialog_informations_timer);
        }
        Button btnOk = findViewById(R.id.alertBtnOk);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.alertBtnOk:
                        dismiss();
                        break;
                }
            }
        };
        btnOk.setOnClickListener(listener);
    }

}