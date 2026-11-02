
package com.krishna.skillsprint
import androidx.compose.material3.FloatingActionButton
import android.Manifest
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.rounded.Leaderboard
import  androidx.compose.material3.ButtonDefaults

import androidx.compose.material3.Button
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.navigation.compose.rememberNavController
import com.krishna.skillsprint.DailyLearningPlanScreen
import java.time.LocalDate


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button

import androidx.compose.ui.graphics.graphicsLayer



private val AppBg = Color(0xFFF5F7FC)
private val AppWhite = Color(0xFFFFFFFF)
private val AppText = Color(0xFF182230)
private val AppMuted = Color(0xFF697586)
private val AppGreen = Color(0xFF159B75)
private val AppPurple = Color(0xFF7866E8)
private val AppBlue = Color(0xFF4779E8)
private val AppBorder = Color(0xFFE8ECF4)


data class SkillMission(
    val title: String,
    val description: String,
    val category: String,
    val points: Int,
    val done: Boolean = false,
    val id: String = ""
)




private data class BottomDestination(
    val title: String,
    val icon: ImageVector
)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = android.graphics.Color.rgb(245, 247, 252)
        window.navigationBarColor = android.graphics.Color.WHITE

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = AppBg
                ) {
                    AppNavigation()
                }
            }
        }
        // Schedule daily reminders
        DailyReminderReceiver.scheduleDailyReminders(this)

// Request notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (
                checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) !=
                android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }
    }
}



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SkillSprintApp(onLogout: () -> Unit = {}) {

    var selectedTab by remember { mutableIntStateOf(0) }

    var showHelpGuide by remember { mutableStateOf(false) }

    val firebaseUser = FirebaseAuth.getInstance().currentUser

    if (firebaseUser == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Please login to continue.")
        }
        return
    }

    val userId = firebaseUser.uid
    val userName = firebaseUser.displayName.orEmpty()
    val userEmail = firebaseUser.email.orEmpty()

    val context = LocalContext.current

    val firestoreProgressManager = remember(userId) {
        FirestoreProgressManager()
    }

    val coroutineScope = rememberCoroutineScope()

    var dataLoaded by remember(userId) {
        mutableStateOf(false)
    }

    var currentStreak by remember(userId) {
        mutableIntStateOf(0)
    }

    var totalXp by remember(userId) {
        mutableIntStateOf(0)
    }

    var lastActiveDate by remember(userId) {
        mutableStateOf("")
    }

    var unlockedAchievements by remember(userId) {
        mutableStateOf<List<String>>(emptyList())
    }

    var achievementPopupId by remember(userId) {
        mutableStateOf<String?>(null)
    }



    val missions = remember(userId) {
        mutableStateListOf<SkillMission>()
    }

    // Load today's missions and the user's cumulative progress.
    LaunchedEffect(userId) {

        dataLoaded = false

        try {
            val today = LocalDate.now().toString()

            // STEP 1: Load or create today's four missions.
            val dailyMissionResult =
                firestoreProgressManager.loadOrCreateDailyMissions(today)

            // STEP 2: Convert mission definitions into UI models.
            val todayMissions =
                dailyMissionResult.missionIds.mapNotNull { missionId ->

                    MissionLibrary.findById(missionId)?.let { definition ->

                        SkillMission(
                            id = definition.id,
                            title = definition.title,
                            description = definition.description,
                            category = definition.category,
                            points = definition.points,
                            done = missionId in dailyMissionResult.completedIds
                        )
                    }
                }

            // STEP 3: Validate the four assigned missions.
            if (todayMissions.size != 4) {
                throw IllegalStateException(
                    "Expected 4 missions, but loaded ${todayMissions.size}."
                )
            }

            missions.clear()
            missions.addAll(todayMissions)

            // STEP 4: Load streak, achievements and cumulative XP.
            try {
                val cloudStreak =
                    firestoreProgressManager.loadStreak()

                currentStreak = cloudStreak.currentStreak
                lastActiveDate = cloudStreak.lastActiveDate

                unlockedAchievements =
                    firestoreProgressManager.loadAchievements()

                totalXp =
                    firestoreProgressManager.loadTotalPoints()

            } catch (exception: Exception) {

                Toast.makeText(
                    context,
                    "Some progress data could not be loaded.",
                    Toast.LENGTH_SHORT
                ).show()
            }

        } catch (exception: Exception) {

            Toast.makeText(
                context,
                "Could not load daily missions: ${exception.message}",
                Toast.LENGTH_LONG
            ).show()

        } finally {
            dataLoaded = true
        }
    }

    val completed = missions.count { it.done }
    val points = totalXp

    // Projects have been removed from bottom navigation.
    val destinations = listOf(
        BottomDestination("Home", Icons.Rounded.Dashboard),
        BottomDestination("Missions", Icons.Rounded.CheckCircle),
        BottomDestination("Leaderboard", Icons.Rounded.Leaderboard),
        BottomDestination("Analytics", Icons.Rounded.TrendingUp),
        BottomDestination("Daily Plan", Icons.Rounded.EmojiEvents),
        BottomDestination("Profile", Icons.Rounded.Person)
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),

        containerColor = AppBg,

        bottomBar = {
            if (!showHelpGuide) {
                NavigationBar(
                    containerColor = AppWhite,
                    contentColor = AppGreen,
                    tonalElevation = 6.dp
                ) {
                    destinations.forEachIndexed { index, destination ->

                        val isSelected = selectedTab == index

                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                selectedTab = index
                            },
                            icon = {
                                Icon(
                                    imageVector = destination.icon,
                                    contentDescription = destination.title,
                                    modifier = Modifier.size(
                                        if (isSelected) 25.dp else 22.dp
                                    )
                                )
                            },
                            label = {
                                Text(
                                    text = destination.title,
                                    fontSize = 9.sp,
                                    maxLines = 1,
                                    fontWeight = if (isSelected) {
                                        FontWeight.Bold
                                    } else {
                                        FontWeight.Medium
                                    }
                                )
                            },
                            alwaysShowLabel = true,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = AppGreen,
                                selectedTextColor = AppGreen,
                                indicatorColor = Color(0xFFE1F5ED),
                                unselectedIconColor = AppMuted,
                                unselectedTextColor = AppMuted
                            )
                        )
                    }
                }
            }
        }

    ) { innerPadding ->

        if (showHelpGuide) {

            HelpGuideScreen(
                onBack = {
                    showHelpGuide = false
                    selectedTab = 5
                }
            )

        } else if (!dataLoaded) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AppGreen)
            }

        } else {

            AnimatedContent(
                targetState = selectedTab,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                transitionSpec = {
                    fadeIn(animationSpec = tween(220)) togetherWith
                            fadeOut(animationSpec = tween(160))
                },
                label = "screen_transition"
            ) { tab ->

                when (tab) {

                    0 -> Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(AppBg)
                    ) {
                        HomeScreen(
                            missions = missions,
                            completed = completed,
                            points = points,
                            currentStreak = currentStreak,
                            unlockedAchievements = unlockedAchievements,
                            onSeeMissions = {
                                selectedTab = 1
                            },
                            onSeeAnalytics = {
                                selectedTab = 3
                            },
                            onSeeDailyPlan = {
                                selectedTab = 4
                            }
                        )





                    }


                    1 -> MissionsScreen(
                        padding = PaddingValues(0.dp),
                        missions = missions.map { mission ->
                            MissionItem(
                                title = mission.title,
                                description = mission.description,
                                category = mission.category,
                                points = mission.points,
                                icon = when (mission.category) {
                                    "CODING" -> "</>"
                                    "COMMUNICATION" -> "Aa"
                                    else -> "✦"
                                },
                                done = mission.done
                            )
                        },
                        onToggle = { index, checked ->

                            if (
                                checked &&
                                index in missions.indices &&
                                !missions[index].done
                            ) {

                                val missionId = missions[index].id

                                if (missionId.isBlank()) {
                                    Toast.makeText(
                                        context,
                                        "Mission ID missing. Please reload the app.",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    return@MissionsScreen
                                }

                                coroutineScope.launch {
                                    try {

                                        val result =
                                            firestoreProgressManager.completeDailyMission(
                                                date = LocalDate.now().toString(),
                                                missionId = missionId
                                            )

                                        missions[index] = missions[index].copy(
                                            done = missionId in result.completedIds
                                        )

                                        totalXp = result.totalPoints

                                        if (result.newlyCompleted) {

                                            try {
                                                val streakResult =
                                                    firestoreProgressManager.recordDailyPractice()

                                                currentStreak =
                                                    streakResult.currentStreak

                                                lastActiveDate =
                                                    streakResult.lastActiveDate

                                            } catch (streakError: Exception) {
                                                Toast.makeText(
                                                    context,
                                                    "Mission completed, but streak sync failed.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }

                                            try {
                                                firestoreProgressManager.saveLeaderboardStats(
                                                    points = result.totalPoints,
                                                    currentStreak = currentStreak,
                                                    completedMissions = missions.count { it.done }
                                                )
                                            } catch (leaderboardError: Exception) {
                                                Toast.makeText(
                                                    context,
                                                    "XP saved, but leaderboard sync failed.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }

                                            Toast.makeText(
                                                context,
                                                "+${missions[index].points} XP earned!",
                                                Toast.LENGTH_SHORT
                                            ).show()

// UPDATE ACHIEVEMENTS AFTER MISSION COMPLETION

// UPDATE ACHIEVEMENTS AND SHOW POPUP
                                            try {
                                                val totalCompleted =
                                                    firestoreProgressManager.loadTotalCompletedMissions()

                                                // Purani achievements ko save rakho
                                                val previousAchievements = unlockedAchievements.toSet()

                                                // Firestore mein achievements update karo
                                                val updatedAchievements =
                                                    firestoreProgressManager.updateAchievements(
                                                        totalCompletedMissions = totalCompleted,
                                                        todayCompletedMissions = result.completedIds.size,
                                                        currentStreak = currentStreak
                                                    )

                                                // UI state update karo
                                                unlockedAchievements = updatedAchievements

                                                // Sirf nayi achievement identify karo
                                                val newlyUnlocked = updatedAchievements.firstOrNull { id ->
                                                    id !in previousAchievements
                                                }

                                                // Nayi achievement par popup trigger karo
                                                if (newlyUnlocked != null) {
                                                    achievementPopupId = newlyUnlocked
                                                }

                                            } catch (achievementError: Exception) {
                                                android.util.Log.e(
                                                    "SkillSprint",
                                                    "Achievement update failed",
                                                    achievementError
                                                )
                                            }




                                        }

                                    } catch (exception: Exception) {
                                        Toast.makeText(
                                            context,
                                            "Mission completion failed: ${exception.message}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            }
                        }
                    )

                    2 -> LeaderboardScreen(
                        navController = rememberNavController()
                    )

                    3 -> AnalyticsScreen(
                        totalMissions = missions.size,
                        completedMissions = completed,
                        points = points,
                        currentStreak = currentStreak,
                        unlockedAchievements = unlockedAchievements
                    )

                    4 -> DailyLearningPlanScreen()

                    5 -> ProfileScreen(
                        padding = PaddingValues(0.dp),
                        totalMissions = missions.size,
                        completedMissions = completed,
                        points = points,
                        onLogout = onLogout,
                        userName = userName,
                        userEmail = userEmail,
                        onOpenHelp = {
                            showHelpGuide = true
                        }
                    )
                }
            }
        }
    }
    // ACHIEVEMENT POPUP
    achievementPopupId?.let { achievementId ->
        AchievementUnlockedDialog(
            achievementId = achievementId,
            onDismiss = {
                achievementPopupId = null
            }
        )
    }
}

@Composable
fun AchievementUnlockedDialog(
    achievementId: String,
    onDismiss: () -> Unit
) {
    var showAnimation by remember {
        mutableStateOf(false)
    }

    val achievements = mapOf(
        "first_mission" to Triple(
            "First Step",
            "You completed your first mission!",
            "🎯"
        ),
        "five_missions" to Triple(
            "Mission Master",
            "You have completed five missions!",
            "🏆"
        ),
        "all_missions" to Triple(
            "Perfect Day",
            "Amazing! You completed all four missions today!",
            "⭐"
        ),
        "five_day_streak" to Triple(
            "On Fire",
            "You maintained a five-day learning streak!",
            "🔥"
        ),
        "seven_day_streak" to Triple(
            "Consistency Champion",
            "Seven days of consistency. Keep going!",
            "👑"
        )
    )

    val achievement = achievements[achievementId]
        ?: Triple(
            "Achievement Unlocked",
            "You reached a new milestone!",
            "🏆"
        )

    LaunchedEffect(achievementId) {
        showAnimation = true
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(30.dp),
        title = null,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedVisibility(
                    visible = showAnimation,
                    enter = scaleIn(
                        animationSpec = spring(
                            dampingRatio = 0.45f,
                            stiffness = 180f
                        )
                    ) + fadeIn()
                ) {
                    Box(
                        modifier = Modifier
                            .size(112.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFF1CC)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = achievement.third,
                            fontSize = 55.sp
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = "ACHIEVEMENT UNLOCKED!",
                    color = Color(0xFF159B75),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.2.sp
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = achievement.first,
                    color = Color(0xFF182230),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(Modifier.height(10.dp))

                Text(
                    text = achievement.second,
                    color = Color(0xFF697586),
                    fontSize = 14.sp,
                    lineHeight = 21.sp
                )

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF159B75)
                    )
                ) {
                    Text(
                        text = "Awesome! 🎉",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {}
    )
}

@Composable
private fun HomeScreen(
    missions: List<SkillMission>,
    completed: Int,
    points: Int,
    currentStreak: Int,
    unlockedAchievements: List<String>,
    onSeeMissions: () -> Unit,
    onSeeAnalytics: () -> Unit,
    onSeeDailyPlan: () -> Unit
) {
    val progress = if (missions.isEmpty()) {
        0f
    } else {
        (completed.toFloat() / missions.size).coerceIn(0f, 1f)
    }

    val achievementList = listOf(
        listOf("first_mission", "First Step", "Complete your first mission.", "🎯"),
        listOf("five_missions", "Mission Master", "Complete five missions.", "🏆"),
        listOf("all_missions", "Perfect Day", "Complete all four missions.", "⭐"),
        listOf("five_day_streak", "On Fire", "Maintain a five-day streak.", "🔥"),
        listOf("seven_day_streak", "Consistency Champion", "Maintain a seven-day streak.", "👑")
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 22.dp,
            bottom = 28.dp
        ),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {

        // PREMIUM HEADER
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "SKILLSPRINT",
                        color = AppGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.sp
                    )

                    Spacer(Modifier.height(7.dp))

                    Text(
                        text = "Let's grow today.",
                        color = AppText,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(Modifier.height(5.dp))

                    Text(
                        text = "Small steps. Big achievements.",
                        color = AppMuted,
                        fontSize = 13.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE1F5ED)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.TrendingUp,
                        contentDescription = "Your growth",
                        tint = AppGreen,
                        modifier = Modifier.size(29.dp)
                    )
                }
            }
        }

        // XP HERO CARD
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF176B52)
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 5.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "YOUR LEARNING JOURNEY",
                                color = Color(0xFFB9EBD6),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.1.sp
                            )

                            Spacer(Modifier.height(12.dp))

                            Text(
                                text = "Keep moving forward!",
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold
                            )

                            Spacer(Modifier.height(6.dp))

                            Text(
                                text = "$completed of ${missions.size} missions completed",
                                color = Color(0xFFE0F4EA),
                                fontSize = 13.sp
                            )
                        }

                        Spacer(Modifier.width(8.dp))

                        Box(
                            modifier = Modifier
                                .size(66.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF2C8067)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${(progress * 100).toInt()}%",
                                color = Color.White,
                                fontSize = 19.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    Spacer(Modifier.height(22.dp))

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(9.dp)
                            .clip(CircleShape),
                        color = Color(0xFFB7F3D4),
                        trackColor = Color(0xFF3B8B72)
                    )

                    Spacer(Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.EmojiEvents,
                                contentDescription = null,
                                tint = Color(0xFFFFD166),
                                modifier = Modifier.size(23.dp)
                            )

                            Spacer(Modifier.width(8.dp))

                            Column {
                                Text(
                                    text = "$points XP",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )

                                Text(
                                    text = "Total experience",
                                    color = Color(0xFFB9EBD6),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        TextButton(
                            onClick = onSeeAnalytics,
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = "View stats →",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // QUICK STATS
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DashboardStatCard(
                    modifier = Modifier.weight(1f),
                    title = "MISSIONS",
                    value = "$completed/${missions.size}",
                    icon = Icons.Rounded.CheckCircle,
                    accent = AppGreen,
                    background = Color(0xFFE1F5ED)
                )

                DashboardStatCard(
                    modifier = Modifier.weight(1f),
                    title = "TOTAL XP",
                    value = points.toString(),
                    icon = Icons.Rounded.EmojiEvents,
                    accent = AppPurple,
                    background = Color(0xFFF0EDFF)
                )
            }
        }

        // STREAK CARD
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFF5E8)
                ),
                border = BorderStroke(
                    1.dp,
                    Color(0xFFFFE3BB)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(17.dp))
                            .background(Color(0xFFFFE5BD)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.LocalFireDepartment,
                            contentDescription = "Daily streak",
                            tint = Color(0xFFE88919),
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Spacer(Modifier.width(14.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Daily Streak",
                            color = AppText,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Spacer(Modifier.height(4.dp))

                        Text(
                            text = "Consistency is your superpower.",
                            color = AppMuted,
                            fontSize = 12.sp
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "$currentStreak",
                            color = Color(0xFFE88919),
                            fontSize = 27.sp,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Text(
                            text = if (currentStreak == 1) "DAY" else "DAYS",
                            color = AppMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // TODAY'S PLAN
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFEEEAFE)
                ),
                border = BorderStroke(
                    1.dp,
                    Color(0xFFDAD2FF)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFDDD5FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "📚",
                                fontSize = 27.sp
                            )
                        }

                        Spacer(Modifier.width(14.dp))

                        Column {
                            Text(
                                text = "Today's Plan",
                                color = AppText,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold
                            )

                            Spacer(Modifier.height(4.dp))

                            Text(
                                text = "Your roadmap to success.",
                                color = AppMuted,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(Modifier.height(18.dp))

                    Text(
                        text = "What will you achieve today?",
                        color = AppText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = "Coding • English • Skills • Personal Growth",
                        color = AppMuted,
                        fontSize = 12.sp
                    )

                    Spacer(Modifier.height(18.dp))

                    Button(
                        onClick = onSeeDailyPlan,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(15.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppPurple
                        )
                    ) {
                        Text(
                            text = "Open Today's Plan  →",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // MISSION COMPLETION PROGRESS
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = AppWhite
                ),
                border = BorderStroke(1.dp, AppBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Your Daily Progress",
                        color = AppText,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = if (completed == missions.size && missions.isNotEmpty()) {
                            "Amazing! You completed all today's missions."
                        } else {
                            "Every completed mission takes you forward."
                        },
                        color = AppMuted,
                        fontSize = 12.sp
                    )

                    Spacer(Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Mission completion",
                            color = AppMuted,
                            fontSize = 12.sp
                        )

                        Text(
                            text = "${(progress * 100).toInt()}%",
                            color = AppGreen,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Spacer(Modifier.height(9.dp))

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(9.dp)
                            .clip(CircleShape),
                        color = AppGreen,
                        trackColor = AppBorder
                    )
                }
            }
        }

        // ACHIEVEMENTS
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = AppWhite
                ),
                border = BorderStroke(1.dp, AppBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.EmojiEvents,
                            contentDescription = "Achievements",
                            tint = AppPurple,
                            modifier = Modifier.size(26.dp)
                        )

                        Spacer(Modifier.width(10.dp))

                        Column {
                            Text(
                                text = "Your Achievements",
                                color = AppText,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold
                            )

                            Text(
                                text = "${achievementList.count { it[0] in unlockedAchievements }} of ${achievementList.size} unlocked",
                                color = AppMuted,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    achievementList.forEach { achievement ->
                        val isUnlocked =
                            achievement[0] in unlockedAchievements

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        if (isUnlocked) {
                                            Color(0xFFFFF1CC)
                                        } else {
                                            Color(0xFFF0F2F6)
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = achievement[3],
                                    fontSize = 21.sp
                                )
                            }

                            Spacer(Modifier.width(12.dp))

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = achievement[1],
                                    color = AppText,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(Modifier.height(3.dp))

                                Text(
                                    text = achievement[2],
                                    color = AppMuted,
                                    fontSize = 11.sp
                                )
                            }

                            Text(
                                text = if (isUnlocked) "✓" else "🔒",
                                color = if (isUnlocked) AppGreen else AppMuted,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // TODAY'S MISSIONS HEADER
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Today's Missions",
                        color = AppText,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = "Your next step starts here.",
                        color = AppMuted,
                        fontSize = 12.sp
                    )
                }

                TextButton(
                    onClick = onSeeMissions
                ) {
                    Text(
                        text = "View all →",
                        color = AppGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // MISSION LIST
        if (missions.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = AppWhite
                    )
                ) {
                    Text(
                        text = "No missions available yet. Check back soon!",
                        modifier = Modifier.padding(20.dp),
                        color = AppMuted,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            items(missions.take(3)) { mission ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = AppWhite
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (mission.done) {
                            Color(0xFFC9EBDD)
                        } else {
                            AppBorder
                        }
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(15.dp))
                                .background(
                                    if (mission.done) {
                                        Color(0xFFE1F5ED)
                                    } else {
                                        Color(0xFFF0EDFF)
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (mission.done) {
                                    Icons.Rounded.CheckCircle
                                } else {
                                    Icons.Rounded.LocalFireDepartment
                                },
                                contentDescription = null,
                                tint = if (mission.done) {
                                    AppGreen
                                } else {
                                    AppPurple
                                },
                                modifier = Modifier.size(25.dp)
                            )
                        }

                        Spacer(Modifier.width(13.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = mission.title,
                                color = AppText,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(Modifier.height(5.dp))

                            Text(
                                text = mission.description,
                                color = AppMuted,
                                fontSize = 12.sp
                            )

                            Spacer(Modifier.height(8.dp))

                            Text(
                                text = mission.category,
                                color = AppGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }

                        Spacer(Modifier.width(8.dp))

                        Text(
                            text = if (mission.done) {
                                "DONE"
                            } else {
                                "+${mission.points} XP"
                            },
                            color = if (mission.done) {
                                AppGreen
                            } else {
                                AppPurple
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }

        // MOTIVATION CARD
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFEAF7F0)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Consistency beats intensity.",
                            color = Color(0xFF176B52),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Spacer(Modifier.height(7.dp))

                        Text(
                            text = "Learn a little every day. Your future self will thank you.",
                            color = AppMuted,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    Icon(
                        imageVector = Icons.Rounded.EmojiEvents,
                        contentDescription = "Achievement",
                        tint = Color(0xFFD99B18),
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }
    }
}


@Composable
private fun DashboardStatCard(
    modifier: Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    accent: Color,
    background: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(23.dp),
        colors = CardDefaults.cardColors(
            containerColor = AppWhite
        ),
        border = BorderStroke(
            1.dp,
            AppBorder
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(17.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(background),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accent,
                    modifier = Modifier.size(23.dp)
                )
            }

            Spacer(Modifier.height(15.dp))

            Text(
                text = title,
                color = AppMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )

            Spacer(Modifier.height(5.dp))

            Text(
                text = value,
                color = AppText,
                fontSize = 25.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}
