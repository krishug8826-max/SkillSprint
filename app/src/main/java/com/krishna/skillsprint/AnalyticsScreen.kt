
package com.krishna.skillsprint

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AnalyticsScreen(
    totalMissions: Int,
    completedMissions: Int,
    totalProjects: Int = 0,
    completedProjects: Int = 0,
    points: Int,
    currentStreak: Int,
    unlockedAchievements: List<String>,
    modifier: Modifier = Modifier
) {
    val missionProgress = if (totalMissions > 0) {
        (completedMissions.toFloat() / totalMissions)
            .coerceIn(0f, 1f)
    } else {
        0f
    }

    val achievementCount = unlockedAchievements.distinct().size
    val totalAchievements = 5

    val achievementProgress = if (totalAchievements > 0) {
        (achievementCount.toFloat() / totalAchievements)
            .coerceIn(0f, 1f)
    } else {
        0f
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FC))
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Text(
            text = "Your Analytics",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF182230)
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = "Track your learning journey and progress.",
            fontSize = 13.sp,
            color = Color(0xFF697586)
        )

        Spacer(Modifier.height(22.dp))

        // Statistics
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AnalyticsStatCard(
                modifier = Modifier.weight(1f),
                title = "MISSIONS",
                value = "$completedMissions/$totalMissions",
                subtitle = "${(missionProgress * 100).toInt()}% completed",
                icon = Icons.Rounded.CheckCircle,
                accent = Color(0xFF159B75),
                iconBackground = Color(0xFFE1F5ED)
            )

            AnalyticsStatCard(
                modifier = Modifier.weight(1f),
                title = "TOTAL XP",
                value = points.coerceAtLeast(0).toString(),
                subtitle = "XP earned",
                icon = Icons.Rounded.EmojiEvents,
                accent = Color(0xFF7866E8),
                iconBackground = Color(0xFFF0EDFF)
            )
        }

        Spacer(Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AnalyticsStatCard(
                modifier = Modifier.weight(1f),
                title = "STREAK",
                value = currentStreak.coerceAtLeast(0).toString(),
                subtitle = if (currentStreak == 1) {
                    "day active"
                } else {
                    "days active"
                },
                icon = Icons.Rounded.LocalFireDepartment,
                accent = Color(0xFFE88919),
                iconBackground = Color(0xFFFFF1DC)
            )

            AnalyticsStatCard(
                modifier = Modifier.weight(1f),
                title = "ACHIEVEMENTS",
                value = "$achievementCount",
                subtitle = "unlocked",
                icon = Icons.Rounded.EmojiEvents,
                accent = Color(0xFF4779E8),
                iconBackground = Color(0xFFE8EEFF)
            )
        }

        Spacer(Modifier.height(20.dp))

        // Mission Progress
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Mission Progress",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF182230)
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "$completedMissions out of $totalMissions missions completed",
                    fontSize = 13.sp,
                    color = Color(0xFF697586)
                )

                Spacer(Modifier.height(16.dp))

                LinearProgressIndicator(
                    progress = { missionProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(CircleShape),
                    color = Color(0xFF159B75),
                    trackColor = Color(0xFFE8ECF4)
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "${(missionProgress * 100).toInt()}% complete",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF159B75)
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        // Achievement Progress
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(15.dp))
                            .background(Color(0xFFF0EDFF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.EmojiEvents,
                            contentDescription = "Achievements",
                            tint = Color(0xFF7866E8),
                            modifier = Modifier.size(27.dp)
                        )
                    }

                    Spacer(Modifier.size(14.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Achievements",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF182230)
                        )

                        Spacer(Modifier.height(4.dp))

                        Text(
                            text = "$achievementCount of $totalAchievements unlocked",
                            fontSize = 13.sp,
                            color = Color(0xFF697586)
                        )
                    }

                    Text(
                        text = "${(achievementProgress * 100).toInt()}%",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF7866E8)
                    )
                }

                Spacer(Modifier.height(16.dp))

                LinearProgressIndicator(
                    progress = { achievementProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape),
                    color = Color(0xFF7866E8),
                    trackColor = Color(0xFFE8ECF4)
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        // Encouragement Card
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
                Icon(
                    imageVector = Icons.Rounded.TrendingUp,
                    contentDescription = "Keep growing",
                    tint = Color(0xFF176B52),
                    modifier = Modifier.size(32.dp)
                )

                Spacer(Modifier.size(12.dp))

                Column {
                    Text(
                        text = "Keep moving forward!",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF176B52)
                    )

                    Spacer(Modifier.height(5.dp))

                    Text(
                        text = "Complete your missions regularly to improve your progress.",
                        fontSize = 12.sp,
                        color = Color(0xFF697586)
                    )
                }
            }
        }
    }
}

@Composable
private fun AnalyticsStatCard(
    modifier: Modifier,
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    accent: Color,
    iconBackground: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(15.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(iconBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(Modifier.height(13.dp))

            Text(
                text = title,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF697586)
            )

            Spacer(Modifier.height(5.dp))

            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF182230)
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = Color(0xFF697586)
            )
        }
    }
}
