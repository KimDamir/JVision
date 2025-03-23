package vision

import android.R
import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.util.Pair


object NotificationUtils {
    const val NOTIFICATION_ID: Int = 1337
    private const val NOTIFICATION_CHANNEL_ID = "com.mtsahakis.mediaprojectiondemo.app"
    private const val NOTIFICATION_CHANNEL_NAME = "com.mtsahakis.mediaprojectiondemo.app"

    fun getNotification(context: Context): Pair<Int, Notification> {
        createNotificationChannel(context)
        val notification = createNotification(context)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
        return Pair(NOTIFICATION_ID, notification)
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    private fun createNotification(context: Context): Notification {
        val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
        builder.setSmallIcon(R.mipmap.sym_def_app_icon)
        builder.setContentTitle("JVision")
        builder.setContentText("Capturing screenshot")
        builder.setOngoing(true)
        builder.setCategory(Notification.CATEGORY_SERVICE)
        builder.setPriority(Notification.PRIORITY_LOW)
        builder.setShowWhen(true)
        return builder.build()
    }
}