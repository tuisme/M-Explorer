package vinova.intern.nhomxnxx.mexplorer.service

import android.app.IntentService
import android.app.Notification
import android.content.Intent
import android.app.NotificationManager
import androidx.core.app.NotificationCompat
import vinova.intern.nhomxnxx.mexplorer.R
import android.app.PendingIntent
import android.content.Context
import android.provider.Settings
import vinova.intern.nhomxnxx.mexplorer.home.HomeActivity


class NotificationService : IntentService(NotificationService::class.java.simpleName) {

    override fun onHandleIntent(intent: Intent?) {
        val type = intent?.getStringExtra("K")
        val notificationIntent = Intent(this, HomeActivity::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

        val contentIntent = PendingIntent
                .getActivity(this, 1, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(this)
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("$type")
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true)
                .setPriority(1)
                .setContentIntent(contentIntent)

        val pendingIntent = PendingIntent.getActivity(this, 1,
                Intent(this, HomeActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setContentIntent(pendingIntent)

        val manager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(1, builder.build())
    }
}