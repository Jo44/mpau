package fr.mpau.tool.dialog

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView
import fr.mpau.R

/**
 * Tool - CustomDialogMessage
 *
 * Author: Jonathan
 * Created: 06/02/2018
 * Last Updated: 24/01/2025
 */
class CustomDialogMessage : AlertDialog {

    /**
     * Attributs
     */

    private var myListener: MyOnClickListener
    private var messageStart: String
    private var messageDate: String? = null
    private var messageMiddle: String? = null
    private var messageTime: String? = null
    private var messageEnd: String? = null

    /**
     * Constructeurs
     */
    constructor(
        context: Context?,
        myClick: MyOnClickListener,
        messageStart: String
    ) : super(context) {
        this.myListener = myClick
        this.messageStart = messageStart
    }

    constructor(
        context: Context?,
        myClick: MyOnClickListener,
        messageStart: String,
        messageDate: String?,
        messageMiddle: String?,
        messageTime: String?,
        messageEnd: String?
    ) : super(context) {
        this.myListener = myClick
        this.messageStart = messageStart
        this.messageDate = messageDate
        this.messageMiddle = messageMiddle
        this.messageTime = messageTime
        this.messageEnd = messageEnd
    }

    /**
     * Initialisation
     *
     * @param savedInstanceState Bundle?
     */
    public override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_message)
        val messageStartView = findViewById<TextView>(R.id.confirm_msg_start)
        val messageDateView = findViewById<TextView>(R.id.confirm_msg_date)
        val messageMiddleView = findViewById<TextView>(R.id.confirm_msg_middle)
        val messageTimeView = findViewById<TextView>(R.id.confirm_msg_time)
        val messageEndView = findViewById<TextView>(R.id.confirm_msg_end)
        val btnYes = findViewById<Button>(R.id.alertBtnYes)
        val btnNo = findViewById<Button>(R.id.alertBtnNo)
        messageStartView.text = messageStart
        messageDateView.text = messageDate
        messageMiddleView.text = messageMiddle
        messageTimeView.text = messageTime
        messageEndView.text = messageEnd
        val listener = View.OnClickListener { view ->
            when (view.id) {
                // Oui
                R.id.alertBtnYes -> {
                    myListener.onYesButtonClick()
                    dismiss()
                }
                // Non
                R.id.alertBtnNo -> dismiss()
            }
        }
        btnYes.setOnClickListener(listener)
        btnNo.setOnClickListener(listener)
    }

    /**
     * Interface
     */
    fun interface MyOnClickListener {
        fun onYesButtonClick()
    }

}