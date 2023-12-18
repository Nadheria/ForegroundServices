package com.app.foregorundservices

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity2 : AppCompatActivity() {

    private val REQUEST_BACKGROUND_LOCATION = 123


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        val serviceIntent = Intent(this, ForegroundService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)


        val stopBtn = findViewById<Button>(R.id.stop_btn)
        val startBtn = findViewById<Button>(R.id.start_btn)

        stopBtn.setOnClickListener {
            val stopIntent = Intent(this, ForegroundService::class.java)
            stopIntent.action = ForegroundService.STOP_TIMER_ACTION
            sendBroadcast(stopIntent)
            stopForegroundService()
        }
        startBtn.setOnClickListener {
            startForegroundService()
        }


    }

    private fun stopForegroundService() {
        val stopServiceIntent = Intent(this, ForegroundService::class.java)
        stopService(stopServiceIntent)
    }

    // Example: You can call the method in response to a button click or any other user interaction
    private fun onButtonStopClick() {
        stopForegroundService()
    }


    private fun startForegroundService() {
        val startServiceIntent = Intent(this, ForegroundService::class.java)
        startService(startServiceIntent)
    }


}





//
//var pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//    PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_MUTABLE)
//} else {
//    PendingIntent.getActivity(this, 0, notificationIntent,
//        PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
//}