
package com.krishna.skillsprint

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val unlocked: Boolean
)

data class StreakResult(
    val currentStreak: Int,
    val lastActiveDate: String,
    val shouldSave: Boolean
)

object AchievementManager {

    private const val FIRST_MISSION = "first_mission"
    private const val FIVE_MISSIONS = "five_missions"
    private const val ALL_MISSIONS = "all_missions"
    private const val FIVE_DAY_STREAK = "five_day_streak"
    private const val SEVEN_DAY_STREAK = "seven_day_streak"

    @RequiresApi(Build.VERSION_CODES.O)
    fun calculateStreak(
        savedStreak: Int,
        savedDate: String,
        today: LocalDate = LocalDate.now()
    ): StreakResult {

        if (savedDate.isBlank()) {
            return StreakResult(
                currentStreak = 1,
                lastActiveDate = today.toString(),
                shouldSave = true
            )
        }

        val previousDate = try {
            LocalDate.parse(savedDate)
        } catch (_: Exception) {
            return StreakResult(
                currentStreak = 1,
                lastActiveDate = today.toString(),
                shouldSave = true
            )
        }

        return when (ChronoUnit.DAYS.between(previousDate, today)) {

            0L -> StreakResult(
                currentStreak = savedStreak,
                lastActiveDate = savedDate,
                shouldSave = false
            )

            1L -> StreakResult(
                currentStreak = savedStreak + 1,
                lastActiveDate = today.toString(),
                shouldSave = true
            )

            else -> StreakResult(
                currentStreak = 1,
                lastActiveDate = today.toString(),
                shouldSave = true
            )
        }
    }

    fun getAchievements(
        completedMissions: Int,
        completedProjects: Int,
        currentStreak: Int,
        previouslyUnlocked: List<String>
    ): List<Achievement> {

        val unlockedIds = previouslyUnlocked.toMutableSet()

        if (completedMissions >= 1) {
            unlockedIds.add(FIRST_MISSION)
        }

        if (completedMissions >= 5) {
            unlockedIds.add(FIVE_MISSIONS)
        }

        if (completedMissions >= 4) {
            unlockedIds.add(ALL_MISSIONS)
        }

        if (currentStreak >= 5) {
            unlockedIds.add(FIVE_DAY_STREAK)
        }

        if (currentStreak >= 7) {
            unlockedIds.add(SEVEN_DAY_STREAK)
        }

        return listOf(
            Achievement(
                id = FIRST_MISSION,
                title = "First Step",
                description = "Complete your first mission.",
                icon = "🎯",
                unlocked = FIRST_MISSION in unlockedIds
            ),
            Achievement(
                id = FIVE_MISSIONS,
                title = "Mission Master",
                description = "Complete five missions.",
                icon = "🏆",
                unlocked = FIVE_MISSIONS in unlockedIds
            ),
            Achievement(
                id = ALL_MISSIONS,
                title = "Perfect Day",
                description = "Complete all four available missions.",
                icon = "⭐",
                unlocked = ALL_MISSIONS in unlockedIds
            ),
            Achievement(
                id = FIVE_DAY_STREAK,
                title = "On Fire",
                description = "Maintain a five-day streak.",
                icon = "🔥",
                unlocked = FIVE_DAY_STREAK in unlockedIds
            ),
            Achievement(
                id = SEVEN_DAY_STREAK,
                title = "Consistency Champion",
                description = "Maintain a seven-day streak.",
                icon = "👑",
                unlocked = SEVEN_DAY_STREAK in unlockedIds
            )
        )
    }
}
