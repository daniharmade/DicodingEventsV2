package com.example.dicodingeventsv2.ui.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.dicodingeventsv2.R
import com.example.dicodingeventsv2.data.remote.EventRepository
import com.example.dicodingeventsv2.di.Injection
import java.text.SimpleDateFormat
import java.util.Locale

class MyWorker(context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {
    private val eventRepository: EventRepository = Injection.provideRepository(context)

    override suspend fun doWork(): Result {
        return try {
            eventRepository.getNearestEvent()?.listEvents?.firstOrNull()?.let { event ->
                showNotification(
                    title = "[Important] Event Reminder: ${formatEventDate(event.beginTime)}",
                    description = event.name,
                    link = event.link
                )
                Result.success()
            } ?: Result.failure()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun formatEventDate(dateString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = inputFormat.parse(dateString) ?: return "Unknown date"
        return SimpleDateFormat("d MMM, HH:mm", Locale.getDefault()).format(date)
    }

    private fun showNotification(title: String, description: String, link: String) {
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, Intent(Intent.ACTION_VIEW).setData(Uri.parse(link)),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        createNotificationChannel()

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_upcoming)
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(description))
            .setAutoCancel(true)
            .build()

        (applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Channel for event reminders"
                (applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(this)
            }
        }
    }

    companion object {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "event_reminder_channel"
        const val CHANNEL_NAME = "Event Reminder Channel"
    }
}