package fr.mpau.tools.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import fr.mpau.R;
import fr.mpau.tools.MinMaxFilter;

/**
 * Author: Jonathan B.
 * Created: 06/02/2018
 */

public class CustomDialogDuration extends AlertDialog {

    private myOnClickListener myListener;

    /**
     * Constructeurs
     */
    public CustomDialogDuration(Context context, myOnClickListener myClick) {
        super(context, R.style.CustomAlertDialog);
        this.myListener = myClick;
    }

    /**
     * Initialisation
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        int MAX_DURATION = 4320;
        int MIN_DURATION = 1;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_number_pickup);
        Button btn15 = findViewById(R.id.alertBtn15);
        Button btn30 = findViewById(R.id.alertBtn30);
        Button btn45 = findViewById(R.id.alertBtn45);
        Button btn60 = findViewById(R.id.alertBtn60);
        final EditText durationText = findViewById(R.id.durationText);
        durationText.setFilters(new InputFilter[]{new MinMaxFilter(MIN_DURATION, MAX_DURATION)});
        Button btnValid = findViewById(R.id.alertBtnValid);
        Button btnCancel = findViewById(R.id.alertBtnCancel);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.alertBtn15:
                        myListener.onButtonClick(15);
                        dismiss();
                        break;
                    case R.id.alertBtn30:
                        myListener.onButtonClick(30);
                        dismiss();
                        break;
                    case R.id.alertBtn45:
                        myListener.onButtonClick(45);
                        dismiss();
                        break;
                    case R.id.alertBtn60:
                        myListener.onButtonClick(60);
                        dismiss();
                        break;
                    case R.id.alertBtnValid:
                        try {
                            int selectDuration = Integer.parseInt(durationText.getText().toString());
                            myListener.onButtonClick(selectDuration);
                        } catch (NumberFormatException nfe) {
                            Log.e("DEBUG", "Aucune durée sélectionnée");
                        }
                        dismiss();
                        break;
                    case R.id.alertBtnCancel:
                        dismiss();
                        break;
                }
            }
        };
        btn15.setOnClickListener(listener);
        btn30.setOnClickListener(listener);
        btn45.setOnClickListener(listener);
        btn60.setOnClickListener(listener);
        btnValid.setOnClickListener(listener);
        btnCancel.setOnClickListener(listener);
    }

    /**
     * Interface
     */
    public interface myOnClickListener {
        void onButtonClick(int selectDuration);
    }

}