package fr.mpau.tools.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import fr.mpau.R;

/**
 * Author: Jonathan B.
 * Created: 06/02/2018
 */

public class CustomDialogNbInter extends AlertDialog {

    private myOnClickListener myListener;

    /**
     * Constructeurs
     */
    public CustomDialogNbInter(Context context, myOnClickListener myClick) {
        super(context, R.style.CustomAlertDialog);
        this.myListener = myClick;
    }

    /**
     * Initialisation
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_inter_by_page);
        final RadioGroup radioNbInter = findViewById(R.id.radio_nb_inter);
        Button btnValid = findViewById(R.id.alertBtnValid);
        Button btnCancel = findViewById(R.id.alertBtnCancel);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.alertBtnValid:
                        try {
                            // Récupération du radio button sélectionné et change le nombre d'intervention par page
                            int selectRadioId = radioNbInter.getCheckedRadioButtonId();
                            RadioButton radioSelected = findViewById(selectRadioId);
                            int selectNbInterByPage = Integer.parseInt(radioSelected.getText().toString());
                            myListener.onButtonClick(selectNbInterByPage);
                        } catch (NumberFormatException nfe) {
                            Log.e("DEBUG", "Aucun nombre d'intervention sélectionné");
                        }
                        dismiss();
                        break;
                    case R.id.alertBtnCancel:
                        dismiss();
                        break;
                }
            }
        };
        btnValid.setOnClickListener(listener);
        btnCancel.setOnClickListener(listener);
    }

    /**
     * Interface
     */
    public interface myOnClickListener {
        void onButtonClick(int selectNbInterByPage);
    }

}