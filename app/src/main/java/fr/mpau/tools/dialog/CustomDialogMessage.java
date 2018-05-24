package fr.mpau.tools.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import fr.mpau.R;

/**
 * Author: Jonathan B.
 * Created: 06/02/2018
 */

public class CustomDialogMessage extends AlertDialog {

    /**
     * Attributs
     */
    private myOnClickListener myListener;
    private String messageStart;
    private String messageDate;
    private String messageMiddle;
    private String messageTime;
    private String messageEnd;

    /**
     * Constructeurs
     */
    public CustomDialogMessage(Context context, myOnClickListener myClick, String messageStart) {
        super(context, R.style.CustomAlertDialog);
        this.myListener = myClick;
        this.messageStart = messageStart;
    }

    public CustomDialogMessage(Context context, myOnClickListener myClick, String messageStart, String messageDate, String messageMiddle, String messageTime, String messageEnd) {
        super(context, R.style.CustomAlertDialog);
        this.myListener = myClick;
        this.messageStart = messageStart;
        this.messageDate = messageDate;
        this.messageMiddle = messageMiddle;
        this.messageTime = messageTime;
        this.messageEnd = messageEnd;
    }

    /**
     * Initialisation
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_message);
        TextView messageStartView = findViewById(R.id.confirm_msg_start);
        TextView messageDateView = findViewById(R.id.confirm_msg_date);
        TextView messageMiddleView = findViewById(R.id.confirm_msg_middle);
        TextView messageTimeView = findViewById(R.id.confirm_msg_time);
        TextView messageEndView = findViewById(R.id.confirm_msg_end);
        Button btnYes = findViewById(R.id.alertBtnYes);
        Button btnNo = findViewById(R.id.alertBtnNo);
        messageStartView.setText(messageStart);
        messageDateView.setText(messageDate);
        messageMiddleView.setText(messageMiddle);
        messageTimeView.setText(messageTime);
        messageEndView.setText(messageEnd);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.alertBtnYes:
                        myListener.onYesButtonClick();
                        dismiss();
                        break;
                    case R.id.alertBtnNo:
                        dismiss();
                        break;
                }
            }
        };
        btnYes.setOnClickListener(listener);
        btnNo.setOnClickListener(listener);
    }

    /**
     * Interface
     */
    public interface myOnClickListener {
        void onYesButtonClick();
    }

}