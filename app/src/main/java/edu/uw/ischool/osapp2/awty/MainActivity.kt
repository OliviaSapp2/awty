package edu.uw.ischool.osapp2.awty

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class MainActivity : AppCompatActivity() {

    private lateinit var startButton: Button
    private lateinit var editTextMessage: EditText
    private lateinit var editTextPhoneNum: EditText
    private lateinit var editTextMinutes: EditText

    private var isSending = false
    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        startButton = findViewById(R.id.StartButton)
        startButton.isEnabled = false
        editTextMessage = findViewById(R.id.editTextMessage)
        editTextPhoneNum = findViewById(R.id.editTextPhoneNum)
        editTextMinutes = findViewById(R.id.editTextMinutes)

        startButton.setOnClickListener{
            //send toast messages every n minutes
            if(!isSending){
                startSending()
            } else {
                stopSending()
            }
        }

        var textWatcher: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // this function is called when text is edited
                checkFilled()
            }

            override fun afterTextChanged(s: Editable) {
            }

        }

        editTextMessage.addTextChangedListener(textWatcher)
        editTextPhoneNum.addTextChangedListener(textWatcher)
        editTextMinutes.addTextChangedListener(textWatcher)

        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    private fun checkFilled(){
        val messageVal = editTextMessage.text.toString().trim()
        val phoneVal = editTextPhoneNum.text.toString().trim()
        val minuteVal = editTextMinutes.text.toString().trim()

        startButton.isEnabled = messageVal.isNotEmpty() && phoneVal.isNotEmpty() && minuteVal.isNotEmpty()
    }

    private fun startSending(){
        val n = editTextMinutes.text.toString().toInt()
        val intervalMin = n * 60 * 1000L

        val message = editTextMessage.text.toString()
        val phoneNum = editTextPhoneNum.text.toString()
        val toastStr = "$phoneNum: $message"

        val toast = Toast.makeText(this, toastStr, Toast.LENGTH_LONG)
        toast.show()

        startButton.text = "Stop"
        isSending = true
    }

    private fun stopSending(){
        alarmManager.cancel(pendingIntent)
        startButton.text = "Start"
        isSending = false
    }

}