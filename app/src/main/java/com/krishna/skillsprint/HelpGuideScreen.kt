package com.krishna.skillsprint

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val HelpGreen = Color(0xFF16845B)
private val HelpDark = Color(0xFF173B2F)
private val HelpMuted = Color(0xFF718078)
private val HelpBackground = Color(0xFFF5F8F6)

@Composable
fun HelpGuideScreen(
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = HelpBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Header
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFF176B52),
                                    Color(0xFF123C32)
                                )
                            )
                        )
                        .padding(22.dp)
                ) {
                    Text(
                        text = "❓ HELP & GUIDE",
                        color = Color(0xFFB6F4D9),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = "Welcome to SkillSprint!",
                        color = Color.White,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "Learn how to use the app, complete missions and improve your skills step by step.",
                        color = Color(0xFFE0F2E9),
                        fontSize = 14.sp,
                        lineHeight = 21.sp
                    )
                }
            }

            Text(
                text = "HOW TO USE THE APP",
                color = HelpGreen,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )

            HelpSection(
                emoji = "🏠",
                title = "1. Home Dashboard",
                description = """
                    The Home screen is your starting point.

                    • Open Home to see your learning dashboard.
                    • Check your XP and current learning progress.
                    • Look for your available missions.
                    • Complete learning activities regularly.

                    Tip: Visit Home every day to stay focused on your goals.
                """.trimIndent()
            )

            HelpSection(
                emoji = "🎯",
                title = "2. Missions",
                description = """
                    Missions help you practise and build skills.

                    • Open the Missions tab.
                    • Read the mission title and instructions.
                    • Complete the task described in the mission.
                    • Mark it completed using the available checkbox or completion control.
                    • Check your updated progress and XP.

                    Important: Only mark a mission complete when you have actually completed it. The exact XP reward depends on the app's configured mission logic.
                """.trimIndent()
            )

            HelpSection(
                emoji = "⭐",
                title = "3. XP and Levels",
                description = """
                    XP means experience points.

                    • Earn XP through supported learning activities.
                    • Open your Profile to see your total XP.
                    • Your level represents your progress in the app.
                    • Keep completing missions to improve your score.

                    More XP means more progress. Your level-up rules depend on the app's configured system.
                """.trimIndent()
            )

            HelpSection(
                emoji = "🔥",
                title = "4. Daily Streak",
                description = """
                    A streak tracks consistency in your learning routine.

                    • Practise regularly.
                    • Complete the activities required by the app.
                    • Check your streak on the Home screen or wherever it appears.
                    • Build a regular daily learning habit.

                    Note: Streak updates depend on the app's configured rules. Simply opening the app may not be enough.
                """.trimIndent()
            )

            HelpSection(
                emoji = "📊",
                title = "5. Analytics",
                description = """
                    Analytics helps you understand your progress.

                    • Open the Analytics tab.
                    • Review the progress information displayed.
                    • Check your completed missions and available statistics.
                    • Use the information to identify where you can improve.

                    Tip: Review your progress regularly instead of focusing only on XP.
                """.trimIndent()
            )

            HelpSection(
                emoji = "🏆",
                title = "6. Leaderboard",
                description = """
                    The Leaderboard shows learners ranked by their points.

                    • Open the Leaderboard screen.
                    • Find your name and current rank.
                    • Compare your XP with other learners.
                    • Complete more missions to improve your ranking.

                    Your rank depends on the leaderboard data and the points recorded for each learner.
                """.trimIndent()
            )

            HelpSection(
                emoji = "👤",
                title = "7. My Profile",
                description = """
                    The Profile section contains your account and learning information.

                    • Check your name and email.
                    • Review your XP, level and mission progress.
                    • View the achievements available in your profile.
                    • Open Account settings for available preferences.
                    • Select Help & Guide whenever you need instructions.

                    Some profile details may be read-only or may require an internet connection.
                """.trimIndent()
            )

            HelpSection(
                emoji = "🤖",
                title = "8. AI Chat and Learning Tools",
                description = """
                    If AI Chat or other learning tools are enabled in your version of SkillSprint:

                    • Open the relevant feature.
                    • Enter your question or learning request.
                    • Read the response and practise what you learn.
                    • Use clear questions for better results.

                    Availability depends on whether the feature has been fully configured in the app.
                """.trimIndent()
            )

            HelpSection(
                emoji = "🔒",
                title = "9. Account Safety",
                description = """
                    Keep your account safe.

                    • Never share your password or verification codes.
                    • Use a strong password.
                    • Avoid logging in on untrusted devices.
                    • Log out when using a shared device.
                    • Do not share private information in learning conversations.
                """.trimIndent()
            )

            HelpSection(
                emoji = "🚪",
                title = "10. How to Logout",
                description = """
                    When you want to leave your account:

                    • Open Profile.
                    • Scroll down to the Logout button.
                    • Tap Logout.
                    • Confirm any prompt if the app displays one.

                    Log back in using your configured sign-in method when you want to use the app again.
                """.trimIndent()
            )

            // Quick tips
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE5F4EC)
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Text(
                        text = "💡 QUICK TIPS",
                        color = HelpDark,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(10.dp))

                    Text(
                        text = """
                            • Practise consistently.
                            • Read mission instructions carefully.
                            • Check your progress regularly.
                            • Keep your account secure.
                            • Focus on learning, not only ranking.
                        """.trimIndent(),
                        color = HelpDark,
                        fontSize = 13.sp,
                        lineHeight = 23.sp
                    )
                }
            }

            // Troubleshooting
            HelpSection(
                emoji = "🛠️",
                title = "11. Common Problems",
                description = """
                    App is not loading:
                    Check your internet connection and try again.

                    XP or progress looks incorrect:
                    Reopen the screen and check whether the latest data has loaded.

                    Login problem:
                    Check your sign-in details and internet connection.

                    A feature does not respond:
                    Return to Home and open it again. If the issue continues, contact the app developer.

                    Important: Never share your password when asking for technical help.
                """.trimIndent()
            )

            HorizontalDivider(color = Color(0xFFDDE8E1))

            Text(
                text = "Need help? Contact the SkillSprint support team when a support contact is available.",
                color = HelpMuted,
                fontSize = 12.sp,
                lineHeight = 19.sp
            )

            Text(
                text = "Keep learning. Keep growing. Keep sprinting! 🚀",
                color = HelpGreen,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = HelpGreen
                )
            ) {
                Text(
                    text = "← Back to Profile",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun HelpSection(
    emoji: String,
    title: String,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row {
                Text(
                    text = emoji,
                    fontSize = 24.sp
                )

                Spacer(Modifier.padding(start = 5.dp))

                Text(
                    text = title,
                    color = HelpDark,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = description,
                color = HelpMuted,
                fontSize = 13.sp,
                lineHeight = 21.sp
            )
        }
    }
}