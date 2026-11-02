package com.krishna.skillsprint

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val ProfileBackground = Color(0xFFF5F8F6)
private val ProfilePrimary = Color(0xFF173B2F)
private val ProfileSecondary = Color(0xFF718078)
private val ProfileGreen = Color(0xFF16845B)
private val ProfileBlue = Color(0xFF4779E8)
private val ProfilePurple = Color(0xFF7866E8)
private val ProfileOrange = Color(0xFFE88919)
private val ProfileRed = Color(0xFFB42318)

private enum class ProfileDialog {
    PERSONAL_INFORMATION,
    NOTIFICATIONS,
    PRIVACY,
    ABOUT,
    HELP
}

@Composable
fun ProfileScreen(
    padding: PaddingValues,
    totalMissions: Int,
    completedMissions: Int,
    totalProjects: Int = 0,
    completedProjects: Int = 0,
    points: Int,
    onLogout: () -> Unit,
    userName: String,
    userEmail: String,
    onOpenHelp: () -> Unit = {}
) {
    var showContent by remember { mutableStateOf(false) }
    var activeDialog by remember { mutableStateOf<ProfileDialog?>(null) }

    // Editable values are local until connected to Firebase.
    var displayName by remember(userName) {
        mutableStateOf(userName)
    }

    var displayEmail by remember(userEmail) {
        mutableStateOf(userEmail)
    }

    var notificationsEnabled by remember {
        mutableStateOf(true)
    }

    var savedName by remember(userName) {
        mutableStateOf(userName)
    }

    LaunchedEffect(Unit) {
        delay(100)
        showContent = true
    }

    val safePoints = points.coerceAtLeast(0)
    val safeTotal = totalMissions.coerceAtLeast(0)
    val safeCompleted = completedMissions
        .coerceAtLeast(0)
        .coerceAtMost(safeTotal)

    // One level for every 500 XP.
    val currentLevel = safePoints / 500 + 1
    val currentLevelXp = safePoints % 500
    val levelProgress = currentLevelXp / 500f

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = ProfileBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // Header
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn() +
                        slideInVertically(initialOffsetY = { it / 5 })
            ) {
                Column {
                    Text(
                        text = "YOUR ACCOUNT",
                        color = ProfileGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "My Profile",
                        color = ProfilePrimary,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(Modifier.height(5.dp))

                    Text(
                        text = "Your learning journey, all in one place.",
                        color = ProfileSecondary,
                        fontSize = 13.sp
                    )
                }
            }

            // Profile card
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn() +
                        slideInVertically(initialOffsetY = { it / 4 })
            ) {
                ProfileHeaderCard(
                    userName = displayName,
                    userEmail = displayEmail,
                    level = currentLevel,
                    points = safePoints
                )
            }

            // Level progress
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn()
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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "LEVEL $currentLevel",
                                    color = ProfileGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(Modifier.height(5.dp))

                                Text(
                                    text = "Keep moving forward!",
                                    color = ProfilePrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Text(
                                text = "⭐ $safePoints XP",
                                color = ProfileOrange,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        LinearProgressIndicator(
                            progress = { levelProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape),
                            color = ProfileGreen,
                            trackColor = Color(0xFFE5EEE8)
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = "$currentLevelXp / 500 XP to next level",
                            color = ProfileSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Overview
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn()
            ) {
                Column {
                    SectionHeading(
                        title = "Your overview",
                        subtitle = "Track your learning progress"
                    )

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ProfileStatCard(
                            modifier = Modifier.weight(1f),
                            emoji = "🎯",
                            title = "MISSIONS",
                            value = "$safeCompleted/$safeTotal",
                            accent = ProfileBlue
                        )

                        ProfileStatCard(
                            modifier = Modifier.weight(1f),
                            emoji = "⭐",
                            title = "TOTAL XP",
                            value = safePoints.toString(),
                            accent = ProfileOrange
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ProfileStatCard(
                            modifier = Modifier.weight(1f),
                            emoji = "🏆",
                            title = "LEVEL",
                            value = currentLevel.toString(),
                            accent = ProfileGreen
                        )

                        ProfileStatCard(
                            modifier = Modifier.weight(1f),
                            emoji = "📚",
                            title = "COMPLETION",
                            value = if (safeTotal > 0) {
                                "${safeCompleted * 100 / safeTotal}%"
                            } else {
                                "0%"
                            },
                            accent = ProfilePurple
                        )
                    }
                }
            }




            // Dynamic achievements
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn()
            ) {
                Column {
                    SectionHeading(
                        title = "Achievements",
                        subtitle = "Milestones from your learning journey"
                    )

                    Spacer(Modifier.height(12.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(1.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(18.dp)
                        ) {
                            AchievementRow(
                                emoji = "🌱",
                                title = "Getting Started",
                                description = "Earn your first XP",
                                color = ProfileGreen,
                                unlocked = safePoints > 0
                            )

                            AchievementRow(
                                emoji = "🔥",
                                title = "Mission Starter",
                                description = "Complete your first mission",
                                color = ProfileOrange,
                                unlocked = safeCompleted >= 1
                            )

                            AchievementRow(
                                emoji = "🎯",
                                title = "Mission Master",
                                description = "Complete 10 missions",
                                color = ProfileBlue,
                                unlocked = safeCompleted >= 10
                            )

                            AchievementRow(
                                emoji = "⭐",
                                title = "XP Collector",
                                description = "Earn 500 XP",
                                color = ProfilePurple,
                                unlocked = safePoints >= 500
                            )
                        }
                    }

                    Spacer(Modifier.height(7.dp))

                    Text(
                        text = "Achievements are calculated from the values passed to this screen.",
                        color = ProfileSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            // Account settings
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn()
            ) {
                Column {
                    SectionHeading(
                        title = "Account settings",
                        subtitle = "Manage your account preferences"
                    )

                    Spacer(Modifier.height(12.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(1.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            AccountOption(
                                emoji = "👤",
                                title = "Personal information",
                                subtitle = "View and edit your details",
                                onClick = {
                                    activeDialog =
                                        ProfileDialog.PERSONAL_INFORMATION
                                }
                            )

                            AccountDivider()

                            AccountOption(
                                emoji = "🔔",
                                title = "Notifications",
                                subtitle = if (notificationsEnabled) {
                                    "Notifications preference: enabled"
                                } else {
                                    "Notifications preference: disabled"
                                },
                                onClick = {
                                    activeDialog =
                                        ProfileDialog.NOTIFICATIONS
                                }
                            )

                            AccountDivider()

                            AccountOption(
                                emoji = "🔒",
                                title = "Privacy & security",
                                subtitle = "Account security information",
                                onClick = {
                                    activeDialog = ProfileDialog.PRIVACY
                                }
                            )

                            AccountDivider()

                            AccountOption(
                                emoji = "ℹ️",
                                title = "About SkillSprint",
                                subtitle = "Version 1.0 · Keep learning",
                                onClick = {
                                    activeDialog = ProfileDialog.ABOUT
                                }
                            )
                            AccountDivider()

                            AccountOption(
                                emoji = "❓",
                                title = "Help & Guide",
                                subtitle = "Learn how to use SkillSprint",
                                onClick = onOpenHelp
                            )

                        }
                    }
                }
            }

            // Logout
            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ProfileRed
                )
            ) {
                Text(
                    text = "Logout",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "MADE FOR YOUR NEXT BIG ACHIEVEMENT ✨",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                color = ProfileSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }

    // Dialogs
    when (activeDialog) {

        ProfileDialog.PERSONAL_INFORMATION -> {
            AlertDialog(
                onDismissRequest = {
                    activeDialog = null
                },
                title = {
                    Text("Personal information")
                },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = displayName,
                            onValueChange = {
                                displayName = it
                            },
                            modifier = Modifier.fillMaxWidth(),
                            label = {
                                Text("Full name")
                            },
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = displayEmail,
                            onValueChange = {
                                displayEmail = it
                            },
                            modifier = Modifier.fillMaxWidth(),
                            label = {
                                Text("Email")
                            },
                            singleLine = true,
                            readOnly = true
                        )

                        Text(
                            text = "Name changes currently apply only to this screen. Firebase profile saving is not connected yet.",
                            color = ProfileSecondary,
                            fontSize = 12.sp
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            savedName = displayName
                            activeDialog = null
                        }
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            displayName = savedName
                            activeDialog = null
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }

        ProfileDialog.NOTIFICATIONS -> {
            AlertDialog(
                onDismissRequest = {
                    activeDialog = null
                },
                title = {
                    Text("Notification preferences")
                },
                text = {
                    Column {
                        Text(
                            text = "Choose whether you want notifications enabled in your app preferences.",
                            fontSize = 14.sp
                        )

                        Spacer(Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Enable notifications",
                                modifier = Modifier.weight(1f),
                                fontWeight = FontWeight.Medium
                            )

                            Switch(
                                checked = notificationsEnabled,
                                onCheckedChange = {
                                    notificationsEnabled = it
                                }
                            )
                        }

                        Text(
                            text = "This preference is currently stored only while this screen remains in memory. It does not enable or disable system notifications.",
                            color = ProfileSecondary,
                            fontSize = 12.sp
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            activeDialog = null
                        }
                    ) {
                        Text("Done")
                    }
                }
            )
        }

        ProfileDialog.PRIVACY -> {
            AlertDialog(
                onDismissRequest = {
                    activeDialog = null
                },
                title = {
                    Text("Privacy & security")
                },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Keep your account safe:"
                        )

                        Text("• Never share your password or verification codes.")

                        Text("• Use a strong password for your account.")

                        Text("• Log out when using a shared device.")

                        Text(
                            text = "Password changes and account deletion are not configured in this screen.",
                            color = ProfileSecondary,
                            fontSize = 12.sp
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            activeDialog = null
                        }
                    ) {
                        Text("Close")
                    }
                }
            )
        }

        ProfileDialog.ABOUT -> {
            AlertDialog(
                onDismissRequest = {
                    activeDialog = null
                },
                title = {
                    Text("About SkillSprint")
                },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "SkillSprint",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = ProfileGreen
                        )

                        Text(
                            text = "Build skills. Complete missions. Grow every day."
                        )

                        Text("Version 1.0")

                        Text(
                            text = "Your personal learning and skill-development companion.",
                            color = ProfileSecondary,
                            fontSize = 13.sp
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            activeDialog = null
                        }
                    ) {
                        Text("Close")
                    }
                }
            )
        }

        null -> Unit
        else -> {

        }
    }
}

@Composable
private fun ProfileHeaderCard(
    userName: String,
    userEmail: String,
    level: Int,
    points: Int
) {
    val displayName = userName.ifBlank {
        "SkillSprint Learner"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF176B52),
                            Color(0xFF0D4938),
                            Color(0xFF123C32)
                        )
                    )
                )
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(92.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2D8067)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = displayName.firstOrNull()
                        ?.uppercase() ?: "S",
                    color = Color.White,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(Modifier.height(14.dp))

            Text(
                text = displayName,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            if (userEmail.isNotBlank()) {
                Spacer(Modifier.height(6.dp))

                Text(
                    text = userEmail,
                    color = Color(0xFFE0F2E9),
                    fontSize = 13.sp
                )
            }

            Spacer(Modifier.height(12.dp))

            Surface(
                shape = RoundedCornerShape(50.dp),
                color = Color(0xFF28765F)
            ) {
                Text(
                    text = "✦  LEVEL $level  •  $points XP",
                    modifier = Modifier.padding(
                        horizontal = 16.dp,
                        vertical = 9.dp
                    ),
                    color = Color(0xFFB6F4D9),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Small steps. Real skills. Big future.",
                color = Color(0xFFD2E9DF),
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun SectionHeading(
    title: String,
    subtitle: String
) {
    Column {
        Text(
            text = title,
            color = ProfilePrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = subtitle,
            color = ProfileSecondary,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun ProfileStatCard(
    modifier: Modifier,
    emoji: String,
    title: String,
    value: String,
    accent: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(accent.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(emoji, fontSize = 19.sp)
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = title,
                color = ProfileSecondary,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = value,
                color = ProfilePrimary,
                fontSize = if (value.length > 10) 16.sp else 26.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
private fun SkillProgressRow(
    name: String,
    level: String,
    progress: Float,
    color: Color
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                color = ProfilePrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "${(progress * 100).toInt()}%",
                color = color,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(6.dp))

        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(7.dp)
                .clip(CircleShape),
            color = color,
            trackColor = Color(0xFFE1EBE5)
        )

        Spacer(Modifier.height(5.dp))

        Text(
            text = level,
            color = ProfileSecondary,
            fontSize = 11.sp
        )
    }
}

@Composable
private fun AchievementRow(
    emoji: String,
    title: String,
    description: String,
    color: Color,
    unlocked: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(49.dp)
                .clip(RoundedCornerShape(15.dp))
                .background(
                    color.copy(
                        alpha = if (unlocked) 0.13f else 0.06f
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = emoji,
                fontSize = 23.sp
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 13.dp)
        ) {
            Text(
                text = title,
                color = if (unlocked) {
                    ProfilePrimary
                } else {
                    ProfileSecondary
                },
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = description,
                color = ProfileSecondary,
                fontSize = 11.sp
            )
        }

        Text(
            text = if (unlocked) "✓" else "🔒",
            color = if (unlocked) color else ProfileSecondary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun AccountOption(
    emoji: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(43.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(Color(0xFFEDF5F0)),
            contentAlignment = Alignment.Center
        ) {
            Text(emoji, fontSize = 19.sp)
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp)
        ) {
            Text(
                text = title,
                color = ProfilePrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = subtitle,
                color = ProfileSecondary,
                fontSize = 11.sp
            )
        }

        Text(
            text = "›",
            color = ProfileSecondary,
            fontSize = 25.sp
        )
    }
}

@Composable
private fun AccountDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 55.dp),
        thickness = 1.dp,
        color = Color(0xFFF0F2F7)
    )
}