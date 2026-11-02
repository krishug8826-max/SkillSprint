
package com.krishna.skillsprint

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class DailyReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val title = inputData.getString(KEY_TITLE)
            ?: "What's the plan for today?"

        val message = inputData.getString(KEY_MESSAGE)
            ?: "Take a moment to review your SkillSprint daily notes."

        createNotificationChannel()

        return try {
            val hasNotificationPermission =
                Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                        ContextCompat.checkSelfPermission(
                            applicationContext,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED

            if (hasNotificationPermission) {
                val notification = NotificationCompat.Builder(
                    applicationContext,
                    CHANNEL_ID
                )
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setStyle(
                        NotificationCompat.BigTextStyle()
                            .bigText(message)
                    )
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .build()

                NotificationManagerCompat
                    .from(applicationContext)
                    .notify(NOTIFICATION_ID, notification)
            }

            // Schedule the next day's reminder.
            DailyReminderScheduler.scheduleNextReminder(
                applicationContext
            )

            Result.success()
        } catch (_: SecurityException) {
            DailyReminderScheduler.scheduleNextReminder(
                applicationContext
            )
            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Daily Notes Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders to review your daily notes"
            }

            val manager = applicationContext.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "skillsprint_daily_reminders"
        const val NOTIFICATION_ID = 1001

        const val KEY_TITLE = "reminder_title"
        const val KEY_MESSAGE = "reminder_message"
    }
}
