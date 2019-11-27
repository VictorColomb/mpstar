package com.stan.mpstar

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavDeepLinkBuilder
import java.util.*


class NotificationService: Service(
)
{
    var timer: Timer? = null
    lateinit var timerTask: TimerTask
    private var timeUntilNotification: Long = 0
    private lateinit var myTitle: String
    private lateinit var myContent: String
    private lateinit var myBigText: String
    private var notificationID: Int = 0
    var priority: Int = NotificationCompat.PRIORITY_HIGH

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "On Start Command")
        super .onStartCommand(intent, flags, startId)

        myTitle = intent?.extras?.getString("Title")?:"Lorem Ipsum Dolor"
        myContent = intent?.extras?.getString("Content")?:"Lorem Ipsum Dolor"
        myBigText = intent?.extras?.getString("BigText")?:"Lorem Ipsum Dolor"
        timeUntilNotification = intent?.extras?.getLong("Time")?: 1000
        notificationID = intent?.extras?.getInt("ID")?: 0


        startTimer()
        return START_STICKY
    }

    override fun onCreate() {
        Log.i(TAG, "On Create")
        super.onCreate()
    }

    override fun onDestroy() {
        Log.i(TAG, "On Destroy")
        stopTimerTask()
        super.onDestroy()
    }

    val handler = Handler()
    private fun startTimer(){
        timer = Timer()
        initializeTimerTask()
        timer!!.schedule(timerTask, timeUntilNotification)
    }

    private fun stopTimerTask(){
        if (timer != null){
            timer!!.cancel()
            timer = null
        }
    }

    private fun initializeTimerTask() {
        timerTask = object : TimerTask() {
            override fun run() {
                handler.post { makeNotification(myTitle, myContent, notificationID, priority) }
            }
        }
    }

    private fun makeNotification(myTitle: String, myContent: String, notificationID: Int, priority: Int){
        /*val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }*/
        val pendingIntent: PendingIntent = NavDeepLinkBuilder(this).setComponentName(MainActivity::class.java).setGraph(R.navigation.mobile_navigation).setDestination(R.id.nav_planning_colles).createPendingIntent()

        val builder = NotificationCompat.Builder(this, MainActivity.CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(myTitle)
                .setContentText(myContent)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(NotificationCompat.BigTextStyle()
                        .bigText(myBigText))
                // sets the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                .setTimeoutAfter(24*3600*1000)

        with(NotificationManagerCompat.from(this)){
            // notification id is a unique int for each notification that you must define
            notify(notificationID, builder.build())
        }
    }

    companion object{
        const val TAG = "Timers"
    }

}