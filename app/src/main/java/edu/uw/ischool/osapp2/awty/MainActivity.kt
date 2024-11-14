package edu.uw.ischool.osapp2.awty

import android.os.Bundle
import android.os.Looper
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.telephony.SmsManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var startButton: Button
    private lateinit var editTextMessage: EditText
    private lateinit var editTextPhoneNum: EditText
    private lateinit var editTextMinutes: EditText

    private var isSending = false

    private var executor = Executors.newSingleThreadScheduledExecutor()
    private val handler = Handler(Looper.getMainLooper())
    private var task: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //initialize UI elements
        startButton = findViewById(R.id.StartButton)
        startButton.isEnabled = false
        editTextMessage = findViewById(R.id.editTextMessage)
        editTextPhoneNum = findViewById(R.id.editTextPhoneNum)
        editTextMinutes = findViewById(R.id.editTextMinutes)

        startButton.setOnClickListener{
            if(!isSending){
                startSending()
            } else {
                stopSending()
            }
        }

        val textWatcher: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                checkFilled()
            }

            override fun afterTextChanged(s: Editable) {}
        }

        editTextMessage.addTextChangedListener(textWatcher)
        editTextPhoneNum.addTextChangedListener(textWatcher)
        editTextMinutes.addTextChangedListener(textWatcher)
}
    private fun checkFilled(){
        //checks if all text editors are filled
        val messageVal = editTextMessage.text.toString().trim()
        val phoneVal = editTextPhoneNum.text.toString().trim()
        val minuteVal = editTextMinutes.text.toString().trim()

        startButton.isEnabled = messageVal.isNotEmpty() && phoneVal.isNotEmpty() && minuteVal.isNotEmpty()
    }

    private fun startSending(){
        //makes new executor because executor get destroyed in stopSending
        if (executor.isShutdown) {
            executor = Executors.newSingleThreadScheduledExecutor()
        }

        val n = editTextMinutes.text.toString().toInt()
        val intervalMin = n * 60 * 1000L

        //sends the sms every n minutes
        task = Runnable {
            val phoneNum = editTextPhoneNum.text.toString()
            val message = editTextMessage.text.toString()
            handler.post {
                try {
                    val sendText = SmsManager.getDefault()
                    sendText.sendTextMessage(phoneNum, null, message, null, null)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        executor.scheduleWithFixedDelay(task, 0, intervalMin, TimeUnit.MILLISECONDS)

        startButton.text = "Stop"
        isSending = true
    }

    private fun stopSending() {
        task?.let {
            executor.shutdownNow()
            task = null
        }
        startButton.text = "Start"
        isSending = false
    }

    override fun onDestroy() {
        super.onDestroy()
        stopSending()
    }

}
