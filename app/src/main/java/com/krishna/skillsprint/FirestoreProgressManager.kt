package com.krishna.skillsprint

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import java.time.LocalDate

class FirestoreProgressManager {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // ----------------------------------------------------
    // USER REFERENCES
    // ----------------------------------------------------

    private fun currentUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User login nahi hai.")
    }

    private fun userProgressDocument() =
        firestore.collection("users")
            .document(currentUserId())
            .collection("progress")
            .document("current")

    private fun userDocument() =
        firestore.collection("users")
            .document(currentUserId())

    private fun currentDisplayName(): String {
        val user = auth.currentUser

        return user?.displayName
            ?.takeIf { it.isNotBlank() }
            ?: user?.email?.substringBefore("@")
            ?: "SkillSprint User"
    }

    // ----------------------------------------------------
    // XP CALCULATION
    // ----------------------------------------------------

    private fun calculateTotalPoints(
        basePoints: Int,
        missionXp: Int,
        dailyPlanXp: Int
    ): Int {
        return basePoints.coerceAtLeast(0) +
                missionXp.coerceAtLeast(0) +
                dailyPlanXp.coerceAtLeast(0)
    }

    // ----------------------------------------------------
    // LOAD TOTAL XP
    // ----------------------------------------------------

    suspend fun loadTotalPoints(): Int {
        val snapshot = userDocument().get().await()
        return snapshot.getLong("points")?.toInt() ?: 0
    }

    // ----------------------------------------------------
    // DAILY PRACTICE AND STREAK
    // ----------------------------------------------------

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun recordDailyPractice(): CloudStreak {

        val document = userProgressDocument()
        val today = LocalDate.now().toString()

        return firestore.runTransaction { transaction ->

            val snapshot = transaction.get(document)

            val previousDate =
                snapshot.getString("lastActiveDate") ?: ""

            val previousStreak =
                snapshot.getLong("currentStreak")?.toInt() ?: 0

            val previousLongest =
                snapshot.getLong("longestStreak")?.toInt() ?: 0

            val currentStreak: Int
            val lastActiveDate: String

            when {
                previousDate == today -> {
                    currentStreak = previousStreak
                    lastActiveDate = previousDate
                }

                previousDate.isNotBlank() &&
                        runCatching {
                            LocalDate.parse(previousDate)
                                .plusDays(1) == LocalDate.parse(today)
                        }.getOrDefault(false) -> {

                    currentStreak = previousStreak + 1
                    lastActiveDate = today
                }

                else -> {
                    currentStreak = 1
                    lastActiveDate = today
                }
            }

            val longestStreak =
                maxOf(previousLongest, currentStreak)

            transaction.set(
                document,
                mapOf(
                    "currentStreak" to currentStreak,
                    "lastActiveDate" to lastActiveDate,
                    "longestStreak" to longestStreak,
                    "updatedAt" to System.currentTimeMillis()
                ),
                SetOptions.merge()
            )

            CloudStreak(
                currentStreak = currentStreak,
                lastActiveDate = lastActiveDate,
                longestStreak = longestStreak
            )
        }.await()
    }

    // ----------------------------------------------------
    // SAVE STREAK
    // ----------------------------------------------------

    suspend fun saveStreak(
        currentStreak: Int,
        lastActiveDate: String
    ) {
        val document = userProgressDocument()

        firestore.runTransaction { transaction ->

            val snapshot = transaction.get(document)

            val existingLongest =
                snapshot.getLong("longestStreak")?.toInt() ?: 0

            transaction.set(
                document,
                mapOf(
                    "currentStreak" to currentStreak,
                    "lastActiveDate" to lastActiveDate,
                    "longestStreak" to maxOf(
                        existingLongest,
                        currentStreak
                    ),
                    "updatedAt" to System.currentTimeMillis()
                ),
                SetOptions.merge()
            )
        }.await()
    }

    // ----------------------------------------------------
    // LOAD STREAK
    // ----------------------------------------------------

    suspend fun loadStreak(): CloudStreak {

        val snapshot = userProgressDocument().get().await()

        return CloudStreak(
            currentStreak =
                snapshot.getLong("currentStreak")?.toInt() ?: 0,

            lastActiveDate =
                snapshot.getString("lastActiveDate") ?: "",

            longestStreak =
                snapshot.getLong("longestStreak")?.toInt() ?: 0
        )
    }

    // ----------------------------------------------------
    // SAVE ACHIEVEMENTS
    // ----------------------------------------------------

    suspend fun saveAchievements(
        achievements: List<String>
    ) {
        userProgressDocument()
            .set(
                mapOf(
                    "achievements" to achievements.distinct(),
                    "updatedAt" to System.currentTimeMillis()
                ),
                SetOptions.merge()
            )
            .await()
    }

    // ----------------------------------------------------
    // LOAD ACHIEVEMENTS
    // ----------------------------------------------------

    suspend fun loadAchievements(): List<String> {

        val snapshot = userProgressDocument().get().await()

        return (snapshot.get("achievements") as? List<*>)
            ?.filterIsInstance<String>()
            ?: emptyList()
    }

    // ----------------------------------------------------
    // COUNT ALL COMPLETED MISSIONS
    // ----------------------------------------------------

    suspend fun loadTotalCompletedMissions(): Int {

        val userId = currentUserId()

        val snapshot = firestore.collection("users")
            .document(userId)
            .collection("dailyMissions")
            .get()
            .await()

        return snapshot.documents.sumOf { document ->

            val completedIds =
                (document.get("completedIds") as? List<*>)
                    ?.filterIsInstance<String>()
                    ?: emptyList()

            completedIds.distinct().size
        }
    }

    // ----------------------------------------------------
    // UPDATE ACHIEVEMENTS AUTOMATICALLY
    // IMPORTANT: CLASS MEMBER, NOT INSIDE loadAchievements()
    // ----------------------------------------------------

    suspend fun updateAchievements(
        totalCompletedMissions: Int,
        todayCompletedMissions: Int,
        currentStreak: Int
    ): List<String> {

        val document = userProgressDocument()

        return firestore.runTransaction { transaction ->

            val snapshot = transaction.get(document)

            val existingAchievements =
                (snapshot.get("achievements") as? List<*>)
                    ?.filterIsInstance<String>()
                    ?.toSet()
                    ?: emptySet()

            val updatedAchievements =
                existingAchievements.toMutableSet()

            // Achievement 1: First mission
            if (totalCompletedMissions >= 1) {
                updatedAchievements.add("first_mission")
            }

            // Achievement 2: Five missions in total
            if (totalCompletedMissions >= 5) {
                updatedAchievements.add("five_missions")
            }

            // Achievement 3: Complete all four missions today
            if (todayCompletedMissions >= 4) {
                updatedAchievements.add("all_missions")
            }

            // Achievement 4: Five-day streak
            if (currentStreak >= 5) {
                updatedAchievements.add("five_day_streak")
            }

            // Achievement 5: Seven-day streak
            if (currentStreak >= 7) {
                updatedAchievements.add("seven_day_streak")
            }

            transaction.set(
                document,
                mapOf(
                    "achievements" to updatedAchievements.toList(),
                    "updatedAt" to System.currentTimeMillis()
                ),
                SetOptions.merge()
            )

            updatedAchievements.toList()
        }.await()
    }

    // ----------------------------------------------------
    // SAVE DAILY LEARNING PLAN
    // ----------------------------------------------------

    suspend fun saveDailyLearningPlan(
        date: String,
        completedTasks: List<Boolean>
    ) {
        require(completedTasks.size == 4) {
            "Daily Learning Plan must contain exactly 4 tasks."
        }

        val parsedDate = LocalDate.parse(date).toString()

        val data = mapOf(
            "date" to parsedDate,
            "completedTasks" to completedTasks,
            "updatedAt" to System.currentTimeMillis()
        )

        userProgressDocument()
            .collection("dailyPlans")
            .document(parsedDate)
            .set(data, SetOptions.merge())
            .await()
    }

    // ----------------------------------------------------
    // LOAD DAILY LEARNING PLAN
    // ----------------------------------------------------

    suspend fun loadDailyLearningPlan(
        date: String
    ): List<Boolean> {

        val parsedDate = LocalDate.parse(date).toString()

        val snapshot = userProgressDocument()
            .collection("dailyPlans")
            .document(parsedDate)
            .get()
            .await()

        val savedTasks =
            snapshot.get("completedTasks") as? List<*>
                ?: return List(4) { false }

        return List(4) { index ->
            savedTasks.getOrNull(index) as? Boolean ?: false
        }
    }

    // ----------------------------------------------------
    // COMPLETE DAILY LEARNING TASK — XP ONCE
    // ----------------------------------------------------

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun completeDailyLearningTask(
        date: String,
        taskIndex: Int,
        xp: Int
    ): DailyPlanTaskResult {

        require(taskIndex in 0..3) {
            "Invalid daily task index."
        }

        require(xp > 0) {
            "XP must be greater than zero."
        }

        val parsedDate = LocalDate.parse(date).toString()

        val user = auth.currentUser
            ?: throw IllegalStateException("User login nahi hai.")

        val userRef = firestore.collection("users")
            .document(user.uid)

        val planRef = userProgressDocument()
            .collection("dailyPlans")
            .document(parsedDate)

        return firestore.runTransaction { transaction ->

            val userSnapshot = transaction.get(userRef)
            val planSnapshot = transaction.get(planRef)

            val savedTasks =
                planSnapshot.get("completedTasks") as? List<*>

            val completedTasks = MutableList(4) { index ->
                savedTasks?.getOrNull(index) as? Boolean ?: false
            }

            val previousDailyXp =
                userSnapshot.getLong("dailyPlanXp")?.toInt() ?: 0

            if (completedTasks[taskIndex]) {
                return@runTransaction DailyPlanTaskResult(
                    newlyCompleted = false,
                    completedTasks = completedTasks.toList(),
                    earnedXp = previousDailyXp
                )
            }

            completedTasks[taskIndex] = true

            val updatedDailyXp = previousDailyXp + xp

            val existingMissionXp =
                userSnapshot.getLong("missionXp")?.toInt() ?: 0

            val existingPoints =
                userSnapshot.getLong("points")?.toInt() ?: 0

            val basePoints =
                userSnapshot.getLong("basePoints")?.toInt()
                    ?: (
                            existingPoints -
                                    existingMissionXp -
                                    previousDailyXp
                            ).coerceAtLeast(0)

            val totalPoints = calculateTotalPoints(
                basePoints = basePoints,
                missionXp = existingMissionXp,
                dailyPlanXp = updatedDailyXp
            )

            transaction.set(
                planRef,
                mapOf(
                    "date" to parsedDate,
                    "completedTasks" to completedTasks.toList(),
                    "updatedAt" to System.currentTimeMillis()
                ),
                SetOptions.merge()
            )

            transaction.set(
                userRef,
                mapOf(
                    "uid" to user.uid,
                    "name" to currentDisplayName(),
                    "email" to (user.email ?: ""),
                    "basePoints" to basePoints,
                    "missionXp" to existingMissionXp,
                    "dailyPlanXp" to updatedDailyXp,
                    "points" to totalPoints,
                    "updatedAt" to System.currentTimeMillis()
                ),
                SetOptions.merge()
            )

            DailyPlanTaskResult(
                newlyCompleted = true,
                completedTasks = completedTasks.toList(),
                earnedXp = updatedDailyXp
            )
        }.await()
    }

    data class DailyPlanTaskResult(
        val newlyCompleted: Boolean,
        val completedTasks: List<Boolean>,
        val earnedXp: Int
    )

    // ----------------------------------------------------
    // DAILY MISSIONS — LOAD OR CREATE
    // ----------------------------------------------------

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun loadOrCreateDailyMissions(
        date: String
    ): DailyMissionsResult {

        val parsedDate = LocalDate.parse(date).toString()

        val user = auth.currentUser
            ?: throw IllegalStateException("User login nahi hai.")

        val dailyMissionsRef = firestore.collection("users")
            .document(user.uid)
            .collection("dailyMissions")
            .document(parsedDate)

        return firestore.runTransaction { transaction ->

            val snapshot = transaction.get(dailyMissionsRef)

            if (snapshot.exists()) {

                val savedIds =
                    snapshot.get("missionIds") as? List<*>

                val savedCompletedIds =
                    snapshot.get("completedIds") as? List<*>

                val ids = savedIds
                    ?.filterIsInstance<String>()
                    ?: emptyList()

                val completedIds = savedCompletedIds
                    ?.filterIsInstance<String>()
                    ?.toSet()
                    ?: emptySet()

                require(
                    ids.size == 4 &&
                            ids.distinct().size == 4 &&
                            ids.all {
                                MissionLibrary.findById(it) != null
                            }
                ) {
                    "Saved daily mission assignment is invalid."
                }

                return@runTransaction DailyMissionsResult(
                    date = parsedDate,
                    missionIds = ids,
                    completedIds = completedIds
                )
            }

            val selectedMissions =
                MissionLibrary.allMissions.shuffled().take(4)

            require(selectedMissions.size == 4) {
                "MissionLibrary must contain at least four missions."
            }

            val selectedIds = selectedMissions.map { it.id }

            transaction.set(
                dailyMissionsRef,
                mapOf(
                    "date" to parsedDate,
                    "missionIds" to selectedIds,
                    "completedIds" to emptyList<String>(),
                    "createdAt" to System.currentTimeMillis()
                )
            )

            DailyMissionsResult(
                date = parsedDate,
                missionIds = selectedIds,
                completedIds = emptySet()
            )
        }.await()
    }

    // ----------------------------------------------------
    // DAILY MISSIONS — COMPLETE ONCE
    // ----------------------------------------------------

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun completeDailyMission(
        date: String,
        missionId: String
    ): DailyMissionCompletionResult {

        val parsedDate = LocalDate.parse(date).toString()

        val mission = MissionLibrary.findById(missionId)
            ?: throw IllegalArgumentException("Invalid mission ID.")

        val user = auth.currentUser
            ?: throw IllegalStateException("User login nahi hai.")

        val userRef = firestore.collection("users")
            .document(user.uid)

        val dailyMissionsRef = userRef
            .collection("dailyMissions")
            .document(parsedDate)

        return firestore.runTransaction { transaction ->

            val missionSnapshot = transaction.get(dailyMissionsRef)
            val userSnapshot = transaction.get(userRef)

            require(missionSnapshot.exists()) {
                "Daily missions have not been assigned."
            }

            val assignedIds =
                (missionSnapshot.get("missionIds") as? List<*>)
                    ?.filterIsInstance<String>()
                    ?: emptyList()

            require(
                assignedIds.size == 4 &&
                        missionId in assignedIds
            ) {
                "This mission is not assigned for this date."
            }

            val previousCompletedIds =
                (missionSnapshot.get("completedIds") as? List<*>)
                    ?.filterIsInstance<String>()
                    ?.toSet()
                    ?: emptySet()

            val previousMissionXp =
                userSnapshot.getLong("missionXp")?.toInt() ?: 0

            if (missionId in previousCompletedIds) {
                return@runTransaction DailyMissionCompletionResult(
                    newlyCompleted = false,
                    completedIds = previousCompletedIds,
                    missionXp = previousMissionXp,
                    totalPoints =
                        userSnapshot.getLong("points")?.toInt() ?: 0
                )
            }

            val updatedCompletedIds =
                previousCompletedIds + missionId

            val updatedMissionXp =
                previousMissionXp + mission.points

            val dailyPlanXp =
                userSnapshot.getLong("dailyPlanXp")?.toInt() ?: 0

            val existingPoints =
                userSnapshot.getLong("points")?.toInt() ?: 0

            val basePoints =
                userSnapshot.getLong("basePoints")?.toInt()
                    ?: (
                            existingPoints -
                                    previousMissionXp -
                                    dailyPlanXp
                            ).coerceAtLeast(0)

            val totalPoints = calculateTotalPoints(
                basePoints = basePoints,
                missionXp = updatedMissionXp,
                dailyPlanXp = dailyPlanXp
            )

            transaction.set(
                dailyMissionsRef,
                mapOf(
                    "completedIds" to updatedCompletedIds.toList(),
                    "updatedAt" to System.currentTimeMillis()
                ),
                SetOptions.merge()
            )

            transaction.set(
                userRef,
                mapOf(
                    "uid" to user.uid,
                    "name" to currentDisplayName(),
                    "email" to (user.email ?: ""),
                    "basePoints" to basePoints,
                    "missionXp" to updatedMissionXp,
                    "dailyPlanXp" to dailyPlanXp,
                    "points" to totalPoints,
                    "updatedAt" to System.currentTimeMillis()
                ),
                SetOptions.merge()
            )

            DailyMissionCompletionResult(
                newlyCompleted = true,
                completedIds = updatedCompletedIds,
                missionXp = updatedMissionXp,
                totalPoints = totalPoints
            )
        }.await()
    }

    data class DailyMissionsResult(
        val date: String,
        val missionIds: List<String>,
        val completedIds: Set<String>
    )

    data class DailyMissionCompletionResult(
        val newlyCompleted: Boolean,
        val completedIds: Set<String>,
        val missionXp: Int,
        val totalPoints: Int
    )

    // ----------------------------------------------------
    // LEADERBOARD — PROJECT XP REMOVED
    // ----------------------------------------------------

    suspend fun saveLeaderboardStats(
        points: Int,
        currentStreak: Int,
        completedMissions: Int
    ) {

        val user = auth.currentUser
            ?: throw IllegalStateException("User login nahi hai.")

        val userRef = firestore.collection("users")
            .document(user.uid)

        firestore.runTransaction { transaction ->

            val snapshot = transaction.get(userRef)

            val existingMissionXp =
                snapshot.getLong("missionXp")?.toInt() ?: 0

            val existingDailyPlanXp =
                snapshot.getLong("dailyPlanXp")?.toInt() ?: 0

            val existingPoints =
                snapshot.getLong("points")?.toInt() ?: points

            val basePoints =
                snapshot.getLong("basePoints")?.toInt()
                    ?: (
                            existingPoints -
                                    existingMissionXp -
                                    existingDailyPlanXp
                            ).coerceAtLeast(0)

            val totalPoints = calculateTotalPoints(
                basePoints = basePoints,
                missionXp = existingMissionXp,
                dailyPlanXp = existingDailyPlanXp
            )

            transaction.set(
                userRef,
                mapOf(
                    "uid" to user.uid,
                    "name" to currentDisplayName(),
                    "email" to (user.email ?: ""),
                    "basePoints" to basePoints,
                    "missionXp" to existingMissionXp,
                    "dailyPlanXp" to existingDailyPlanXp,
                    "points" to totalPoints,
                    "currentStreak" to currentStreak,
                    "completedMissions" to completedMissions,
                    "updatedAt" to System.currentTimeMillis()
                ),
                SetOptions.merge()
            )
        }.await()
    }

    // ----------------------------------------------------
    // LOAD LEADERBOARD
    // ----------------------------------------------------

    suspend fun loadLeaderboard(
        limit: Long = 50
    ): List<LeaderboardUser> {

        val snapshot = firestore.collection("users")
            .orderBy("points", Query.Direction.DESCENDING)
            .limit(limit)
            .get()
            .await()

        return snapshot.documents.map { document ->

            LeaderboardUser(
                uid = document.getString("uid") ?: document.id,
                name = document.getString("name") ?: "SkillSprint User",
                points = document.getLong("points")?.toInt() ?: 0,
                currentStreak =
                    document.getLong("currentStreak")?.toInt() ?: 0,
                completedMissions =
                    document.getLong("completedMissions")?.toInt() ?: 0
            )
        }
    }

    // ----------------------------------------------------
    // SAVE DAILY NOTE
    // ----------------------------------------------------

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun saveDailyNote(
        date: String,
        note: String
    ) {

        val parsedDate = LocalDate.parse(date).toString()

        val data = mapOf(
            "date" to parsedDate,
            "note" to note,
            "updatedAt" to System.currentTimeMillis()
        )

        userProgressDocument()
            .collection("dailyNotes")
            .document(parsedDate)
            .set(data, SetOptions.merge())
            .await()
    }

    // ----------------------------------------------------
    // LOAD DAILY NOTE
    // ----------------------------------------------------

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun loadDailyNote(
        date: String
    ): String {

        val parsedDate = LocalDate.parse(date).toString()

        val snapshot = userProgressDocument()
            .collection("dailyNotes")
            .document(parsedDate)
            .get()
            .await()

        return snapshot.getString("note") ?: ""
    }
}

// --------------------------------------------------------
// DATA CLASSES
// --------------------------------------------------------

data class CloudStreak(
    val currentStreak: Int,
    val lastActiveDate: String,
    val longestStreak: Int = 0
)

data class LeaderboardUser(
    val uid: String,
    val name: String,
    val points: Int,
    val currentStreak: Int,
    val completedMissions: Int
)