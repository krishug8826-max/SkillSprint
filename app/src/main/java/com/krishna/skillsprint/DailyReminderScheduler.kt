
package com.krishna.skillsprint

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

object DailyReminderScheduler {

    private const val PREFS_NAME = "skillsprint_reminder_settings"
    private const val KEY_ENABLED = "reminder_enabled"
    private const val KEY_HOUR = "reminder_hour"
    private const val KEY_MINUTE = "reminder_minute"

    private const val UNIQUE_WORK_NAME = "skillsprint_daily_note_reminder"

    private const val DEFAULT_HOUR = 20
    private const val DEFAULT_MINUTE = 0

    fun saveReminderSettings(
        context: Context,
        enabled: Boolean,
        hour: Int,
        minute: Int
    ) {
        require(hour in 0..23)
        require(minute in 0..59)

        context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        ).edit()
            .putBoolean(KEY_ENABLED, enabled)
            .putInt(KEY_HOUR, hour)
            .putInt(KEY_MINUTE, minute)
            .apply()

        if (enabled) {
            scheduleDailyReminder(context, hour, minute)
        } else {
            cancelReminder(context)
        }
    }

    fun isReminderEnabled(context: Context): Boolean {
        return context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        ).getBoolean(KEY_ENABLED, false)
    }

    fun getReminderHour(context: Context): Int {
        return context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        ).getInt(KEY_HOUR, DEFAULT_HOUR)
    }

    fun getReminderMinute(context: Context): Int {
        return context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        ).getInt(KEY_MINUTE, DEFAULT_MINUTE)
    }

    fun scheduleSavedReminder(context: Context) {
        if (!isReminderEnabled(context)) return

        scheduleDailyReminder(
            context,
            getReminderHour(context),
            getReminderMinute(context)
        )
    }

    fun scheduleDailyReminder(
        context: Context,
        hour: Int,
        minute: Int
    ) {
        require(hour in 0..23)
        require(minute in 0..59)

        val now = Calendar.getInstance()

        val nextReminder = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            if (!after(now)) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val delayMillis = (
                nextReminder.timeInMillis - now.timeInMillis
                ).coerceAtLeast(1_000L)

        val inputData = Data.Builder()
            .putString(
                DailyReminderWorker.KEY_TITLE,
                "What's the plan for today?"
            )
            .putString(
                DailyReminderWorker.KEY_MESSAGE,
                "Take a moment to review your SkillSprint daily notes."
            )
            .build()

        val request = OneTimeWorkRequestBuilder<DailyReminderWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(context.applicationContext)
            .enqueueUniqueWork(
                UNIQUE_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                request
            )
    }

    fun cancelReminder(context: Context) {
        WorkManager.getInstance(context.applicationContext)
            .cancelUniqueWork(UNIQUE_WORK_NAME)
    }

    /**
     * Call this after a reminder notification is delivered
     * to schedule the next occurrence.
     */
    fun scheduleNextReminder(context: Context) {
        if (!isReminderEnabled(context)) return

        scheduleDailyReminder(
            context,
            getReminderHour(context),
            getReminderMinute(context)
        )
    }
}
