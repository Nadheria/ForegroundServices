package com.app.foregorundservices

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class MainActivity2 : AppCompatActivity() {

    private val REQUEST_BACKGROUND_LOCATION = 123
    val PERMISSION_REQUEST_CODE = 112


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)


        if (Build.VERSION.SDK_INT > 32) {
            if (!shouldShowRequestPermissionRationale("112")){
                getNotificationPermission();
            }
        }

        val serviceIntent = Intent(this, ForegroundService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)


        val stopBtn = findViewById<Button>(R.id.stop_btn)
        val startBtn = findViewById<Button>(R.id.start_btn)

        stopBtn.setOnClickListener {
            val stopIntent = Intent(this, ForegroundService::class.java)
            stopIntent.action = ForegroundService.STOP_TIMER_ACTION
            sendBroadcast(stopIntent)
            stopForegroundService()

//            sendCustomActionToService()
        }
        startBtn.setOnClickListener {
            startForegroundService()
//            sendCustomActionToService2()
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

    private fun sendCustomActionToService() {
        val serviceIntent = Intent(this, ForegroundService::class.java)
        serviceIntent.action = "CUSTOM_ACTION" // Set your custom action here
        startService(serviceIntent)
    }

    private fun sendCustomActionToService2() {
        val serviceIntent = Intent(this, ForegroundService::class.java)
        serviceIntent.action = "CUSTOM_ACTION2" // Set your custom action here
        startService(serviceIntent)
    }

    fun getNotificationPermission() {
        try {
            if (Build.VERSION.SDK_INT > 32) {
                ActivityCompat.requestPermissions(
                    this, arrayOf<String>(android.Manifest.permission.POST_NOTIFICATIONS),
                    PERMISSION_REQUEST_CODE
                )
            }
        } catch (e: Exception) {
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    // allow
                } else {
                    //deny
                }
                return
            }
        }
    }

}





//
//var pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//    PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_MUTABLE)
//} else {
//    PendingIntent.getActivity(this, 0, notificationIntent,
//        PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
//}