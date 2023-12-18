package com.app.foregorundservices


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.widget.Button
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

class ForegroundService : Service() {
    private var isServiceRunning = false
    private lateinit var notificationManager: NotificationManager
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private var currentTime = 0L
    private val STOP_ACTION = "stop_action"
    private val RESUME_ACTION = "resume_action"
    private val CA = "CUSTOM_ACTION"
    private val CA2 = "CUSTOM_ACTION2"


    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        handler = Handler()
        val filter = IntentFilter(STOP_TIMER_ACTION)
        registerReceiver(stopTimerReceiver, filter)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            STOP_ACTION -> stopTimer()
            RESUME_ACTION -> resumeTimer()
            CA-> stopTimer()
            CA2-> startTimer()
            else -> startService()


        }
        return START_STICKY


    }
    private fun startService() {
        if (!isServiceRunning) {
            startForegroundService()
            isServiceRunning = true
        }
    }


    private fun startForegroundService() {

        val channelId = createNotificationChannel("ForegroundServiceChannel", "Foreground Service")

        val stopIntent = Intent(this, ForegroundService::class.java).apply {
            action = STOP_ACTION
        }
        val stopPendingIntent = PendingIntent.getService(
            this,
            0,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val resumeIntent = Intent(this, ForegroundService::class.java).apply {
            action = RESUME_ACTION
        }
        val resumePendingIntent = PendingIntent.getService(
            this,
            0,
            resumeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationIntent = Intent(this, ForegroundService::class.java)


        var pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
        }


        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Foreground Service")
            .setContentText("Timer: $currentTime seconds")
            .setSmallIcon(R.drawable.baseline_notifications_24)
            .setContentIntent(pendingIntent)
            .addAction(R.drawable.pause, "Stop Timer", stopPendingIntent)
            .addAction(R.drawable.play, "Resume Timer", resumePendingIntent)
            .build()



       startForeground(ONGOING_NOTIFICATION_ID, notification)

        startTimer()

    }

    private fun stopTimer() {
        handler.removeCallbacks(runnable)
        isServiceRunning = false
        stopForeground(true)
        stopSelf()

    }

    private fun resumeTimer() {
        startTimer()
        isServiceRunning = true
    }

    private fun createNotificationChannel(channelId: String, channelName: String): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
            return channelId
        }
        return ""
    }

    private fun startTimer() {
        runnable = object : Runnable {
            override fun run() {
                currentTime++
                updateNotification()
                handler.postDelayed(this, 1000) // Update every second
            }
        }
        handler.postDelayed(runnable, 1000)
    }

    private fun updateNotification() {
        val notification = NotificationCompat.Builder(this, "ForegroundServiceChannel")
            .setContentTitle("Foreground Service")
            .setContentText("Timer: $currentTime seconds")
            .setSmallIcon(R.drawable.baseline_notifications_24)
            .build()

        notificationManager.notify(ONGOING_NOTIFICATION_ID, notification)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
        stopForeground(true)
        isServiceRunning = false
        unregisterReceiver(stopTimerReceiver)
    }

    companion object {
        private const val ONGOING_NOTIFICATION_ID = 101
        const val STOP_TIMER_ACTION = "com.app.foregorundservices.STOP_TIMER_ACTION"
    }

    private val stopTimerReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == STOP_TIMER_ACTION) {
                stopTimer()
            }
        }
    }

    private fun buildPendingIntent(context: Context, action: String, serviceName: ComponentName?, ): PendingIntent {
        val intent = Intent(action)
        intent.component = serviceName
        return PendingIntent.getService(context, 0, intent, if (VersionUtils.hasMarshmallow()) PendingIntent.FLAG_IMMUTABLE else 0)
    }

}

