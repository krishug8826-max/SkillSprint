package com.krishna.skillsprint

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import java.util.Calendar

class DailyReminderReceiver : BroadcastReceiver() {


    override fun onReceive(context: Context, intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED -> {
                scheduleDailyReminders(context)
            }

            else -> {
                if (intent != null) {
                    showNotification(context, intent)
                    scheduleNextReminder(context, intent)
                }
            }
        }
    }

    companion object {
        private const val CHANNEL_ID = "skillsprint_daily_reminders"
        private const val MORNING_ID = 1001
        private const val EVENING_ID = 1002

        fun scheduleDailyReminders(context: Context) {
            scheduleAlarm(context, 10, MORNING_ID)
            scheduleAlarm(context, 20, EVENING_ID)
        }

        private fun scheduleAlarm(
            context: Context,
            hour: Int,
            requestCode: Int
        ) {
            val alarmManager =
                context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val alarmIntent =
                Intent(context, DailyReminderReceiver::class.java).apply {
                    putExtra("hour", hour)
                    putExtra("requestCode", requestCode)
                }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)

                if (timeInMillis <= System.currentTimeMillis()) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }

            if (
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                !alarmManager.canScheduleExactAlarms()
            ) {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
        }

        private fun scheduleNextReminder(
            context: Context,
            intent: Intent
        ) {
            when (intent.getIntExtra("requestCode", -1)) {
                MORNING_ID -> scheduleAlarm(context, 10, MORNING_ID)
                EVENING_ID -> scheduleAlarm(context, 20, EVENING_ID)
            }
        }

        private fun showNotification(
            context: Context,
            intent: Intent
        ) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE)
                        as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    "Daily Learning Reminders",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description =
                        "Reminders to complete daily SkillSprint missions"
                }

                notificationManager.createNotificationChannel(channel)
            }

            val hour = intent.getIntExtra("hour", 10)

            val title: String
            val message: String

            if (hour == 10) {
                title = "Good morning! ☀️"
                message =
                    "Aaj ki missions complete karke apni skills improve karo!"
            } else {
                title = "Your daily reminder 🌙"
                message =
                    "Don't break your streak! Aaj ki pending missions complete karo."
            }

            val launchIntent =
                context.packageManager.getLaunchIntentForPackage(
                    context.packageName
                )

            val contentIntent = launchIntent?.let {
                PendingIntent.getActivity(
                    context,
                    2001,
                    it,
                    PendingIntent.FLAG_UPDATE_CURRENT or
                            PendingIntent.FLAG_IMMUTABLE
                )
            }

            val notification =
                NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setStyle(
                        NotificationCompat.BigTextStyle().bigText(message)
                    )
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .apply {
                        if (contentIntent != null) {
                            setContentIntent(contentIntent)
                        }
                    }
                    .build()

            notificationManager.notify(
                if (hour == 10) MORNING_ID else EVENING_ID,
                notification
            )
        }
    }


}
