package com.krishna.skillsprint

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay


private val MissionBg = Color(0xFFF5F8F6)
private val MissionWhite = Color(0xFFFFFFFF)
private val MissionText = Color(0xFF172B24)
private val MissionMuted = Color(0xFF718078)
private val MissionGreen = Color(0xFF16845B)
private val MissionPurple = Color(0xFF16845B)
private val MissionBorder = Color(0xFFE1EBE5)



data class MissionItem(
    val title: String,
    val description: String,
    val category: String,
    val points: Int,
    val icon: String,
    val done: Boolean = false
)

@Composable
fun MissionsScreen(
    padding: PaddingValues,
    missions: List<MissionItem>,
    onToggle: (Int, Boolean) -> Unit
) {
    var selectedFilter by remember { mutableIntStateOf(0) }


    val completed = missions.count { it.done }
    val pending = missions.size - completed
    val earnedPoints = missions.filter { it.done }.sumOf { it.points }

    val totalPoints = missions.sumOf { it.points }

    val progress = if (missions.isEmpty()) {
        0f
    } else {
        completed.toFloat() / missions.size
    }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(700),
        label = "mission_progress"
    )

    val visibleMissions = missions.mapIndexedNotNull { index, mission ->
        when (selectedFilter) {
            1 -> if (!mission.done) index to mission else null
            2 -> if (mission.done) index to mission else null
            else -> index to mission
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MissionBg
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(
                horizontal = 20.dp,
                vertical = 22.dp
            ),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // Premium header
            item {
                Column {
                    Surface(
                        shape = RoundedCornerShape(50.dp),
                        color = Color(0xFFE0F4E9)
                    ) {
                        Text(
                            text = "  ✦  YOUR DAILY GROWTH  ",
                            modifier = Modifier.padding(
                                horizontal = 8.dp,
                                vertical = 7.dp
                            ),
                            color = MissionGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(Modifier.height(14.dp))

                    Text(
                        text = "Daily missions",
                        color = MissionText,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(Modifier.height(7.dp))

                    Text(
                        text = "Small actions. Bigger achievements.",
                        color = MissionMuted,
                        fontSize = 14.sp
                    )
                }
            }

            // Progress and XP card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF176B52)
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 3.dp
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "TODAY'S PROGRESS",
                                    color = Color(0xFFBCEBD7),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.2.sp
                                )

                                Spacer(Modifier.height(10.dp))

                                Text(
                                    text = "$completed/${missions.size} completed",
                                    color = Color.White,
                                    fontSize = 23.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )

                                Spacer(Modifier.height(6.dp))

                                Text(
                                    text = if (completed == missions.size && missions.isNotEmpty()) {
                                        "Amazing! All missions completed."
                                    } else {
                                        "$pending missions left. Keep going!"
                                    },
                                    color = Color(0xFFD2EDE1),
                                    fontSize = 12.sp
                                )
                            }

                            Surface(
                                shape = CircleShape,
                                color = Color(0xFF34896F),
                                modifier = Modifier.size(72.dp)
                            ) {
                                Box(
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
                        }

                        Spacer(Modifier.height(22.dp))

                        LinearProgressIndicator(
                            progress = { animatedProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(9.dp)
                                .clip(CircleShape),
                            color = Color(0xFF9AE6C5),
                            trackColor = Color(0xFF3B806A)
                        )

                        Spacer(Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "XP EARNED",
                                    color = Color(0xFFBCEBD7),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(Modifier.height(4.dp))

                                Text(
                                    text = "⚡ $earnedPoints XP",
                                    color = Color.White,
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }

                            Column(
                                horizontalAlignment = Alignment.End
                            ) {
                                Text(
                                    text = "AVAILABLE XP",
                                    color = Color(0xFFBCEBD7),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(Modifier.height(4.dp))

                                Text(
                                    text = "+${totalPoints - earnedPoints} XP",
                                    color = Color.White,
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Summary cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        border = BorderStroke(1.dp, MissionBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(15.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.CheckCircle,
                                contentDescription = null,
                                tint = MissionGreen,
                                modifier = Modifier.size(25.dp)
                            )

                            Spacer(Modifier.height(12.dp))

                            Text(
                                text = "$completed",
                                color = MissionText,
                                fontSize = 25.sp,
                                fontWeight = FontWeight.ExtraBold
                            )

                            Text(
                                text = "Completed",
                                color = MissionMuted,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        border = BorderStroke(1.dp, MissionBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(15.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.WorkspacePremium,
                                contentDescription = null,
                                tint = Color(0xFFD99A20),
                                modifier = Modifier.size(25.dp)
                            )

                            Spacer(Modifier.height(12.dp))

                            Text(
                                text = "$pending",
                                color = MissionText,
                                fontSize = 25.sp,
                                fontWeight = FontWeight.ExtraBold
                            )

                            Text(
                                text = "Remaining",
                                color = MissionMuted,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // Filters
            item {
                Column {
                    Text(
                        text = "Explore missions",
                        color = MissionText,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(Modifier.height(5.dp))

                    Text(
                        text = "Choose a task and earn your XP.",
                        color = MissionMuted,
                        fontSize = 13.sp
                    )

                    Spacer(Modifier.height(15.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("All", "Pending", "Completed")
                            .forEachIndexed { index, label ->

                                val isSelected = selectedFilter == index

                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(14.dp))
                                        .then(
                                            Modifier
                                        ),
                                    shape = RoundedCornerShape(14.dp),
                                    color = if (isSelected) {
                                        MissionGreen
                                    } else {
                                        Color.White
                                    },
                                    border = if (isSelected) {
                                        null
                                    } else {
                                        BorderStroke(1.dp, MissionBorder)
                                    },
                                    onClick = {
                                        selectedFilter = index
                                    }
                                ) {
                                    Box(
                                        modifier = Modifier.padding(
                                            horizontal = 4.dp,
                                            vertical = 12.dp
                                        ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = label,
                                            color = if (isSelected) {
                                                Color.White
                                            } else {
                                                MissionMuted
                                            },
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) {
                                                FontWeight.Bold
                                            } else {
                                                FontWeight.Medium
                                            },
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                    }
                }
            }

            // Mission list heading
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when (selectedFilter) {
                            1 -> "Pending tasks"
                            2 -> "Completed tasks"
                            else -> "Your missions"
                        },
                        color = MissionText,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Surface(
                        shape = RoundedCornerShape(50.dp),
                        color = Color(0xFFE5F3EB)
                    ) {
                        Text(
                            text = "${visibleMissions.size} tasks",
                            modifier = Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 6.dp
                            ),
                            color = MissionGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Empty state or mission cards
            if (visibleMissions.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        border = BorderStroke(1.dp, MissionBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.CheckCircle,
                                contentDescription = null,
                                tint = MissionGreen,
                                modifier = Modifier.size(48.dp)
                            )

                            Spacer(Modifier.height(14.dp))

                            Text(
                                text = if (selectedFilter == 1) {
                                    "You're all caught up!"
                                } else {
                                    "No completed missions yet"
                                },
                                color = MissionText,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(Modifier.height(6.dp))

                            Text(
                                text = if (selectedFilter == 1) {
                                    "Enjoy your progress or check another filter."
                                } else {
                                    "Complete a mission to see it here."
                                },
                                color = MissionMuted,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            } else {
                items(
                    items = visibleMissions,
                    key = { entry -> entry.first }
                ) { entry ->
                    val (originalIndex, mission) = entry

                    var visible by remember(originalIndex) {
                        androidx.compose.runtime.mutableStateOf(false)
                    }

                    LaunchedEffect(originalIndex) {
                        delay(originalIndex * 60L)
                        visible = true
                    }

                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(tween(300)) +
                                slideInVertically(
                                    animationSpec = tween(300),
                                    initialOffsetY = { it / 8 }
                                )
                    ) {
                        MissionTaskCard(
                            mission = mission,
                            onCheckedChange = { checked ->
                                onToggle(originalIndex, checked)
                            }
                        )
                    }
                }
            }

            // Bottom motivational card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE5F4EC)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Consistency wins.",
                                color = MissionText,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.ExtraBold
                            )

                            Spacer(Modifier.height(6.dp))

                            Text(
                                text = "Every mission brings you one step closer to your goals.",
                                color = MissionMuted,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(Modifier.size(10.dp))

                        Icon(
                            imageVector = Icons.Rounded.WorkspacePremium,
                            contentDescription = "Achievement",
                            tint = Color(0xFFD99A20),
                            modifier = Modifier.size(38.dp)
                        )
                    }
                }
            }
        }
    }


}

@Composable
private fun MissionTaskCard(
    mission: MissionItem,
    onCheckedChange: (Boolean) -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (mission.done) {
            Color(0xFFEAF7F0)
        } else {
            Color.White
        },
        animationSpec = tween(300),
        label = "mission_card_color"
    )


    val (icon, iconBackground) = when (mission.category) {
        "CODING" -> Icons.Rounded.Code to Color(0xFFECE9FF)
        "COMMUNICATION" -> Icons.Rounded.MenuBook to Color(0xFFE6F0FF)
        "PROJECT" -> Icons.Rounded.WorkspacePremium to Color(0xFFFFF1D9)
        else -> Icons.Rounded.School to Color(0xFFE3F6EE)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(23.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        border = BorderStroke(
            1.dp,
            if (mission.done) {
                Color(0xFFC7E8D6)
            } else {
                MissionBorder
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (mission.done) 0.dp else 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 17.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(50.dp),
                shape = RoundedCornerShape(16.dp),
                color = if (mission.done) {
                    Color(0xFFD0F1E3)
                } else {
                    iconBackground
                }
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (mission.done) {
                            Icons.Rounded.CheckCircle
                        } else {
                            icon
                        },
                        contentDescription = null,
                        tint = if (mission.done) {
                            MissionGreen
                        } else {
                            MissionGreen
                        },
                        modifier = Modifier.size(25.dp)
                    )
                }
            }

            Spacer(Modifier.size(13.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = mission.title,
                    color = MissionText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                Spacer(Modifier.height(5.dp))

                Text(
                    text = mission.description,
                    color = MissionMuted,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )

                Spacer(Modifier.height(9.dp))

                Surface(
                    shape = RoundedCornerShape(7.dp),
                    color = if (mission.done) {
                        Color(0xFFD0F1E3)
                    } else {
                        Color(0xFFF0F4F2)
                    }
                ) {
                    Text(
                        text = mission.category,
                        modifier = Modifier.padding(
                            horizontal = 8.dp,
                            vertical = 5.dp
                        ),
                        color = MissionGreen,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(Modifier.size(8.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (mission.done) {
                        Color(0xFFD0F1E3)
                    } else {
                        Color(0xFFFFF2D7)
                    }
                ) {
                    Text(
                        text = if (mission.done) {
                            "EARNED"
                        } else {
                            "+${mission.points} XP"
                        },
                        modifier = Modifier.padding(
                            horizontal = 8.dp,
                            vertical = 6.dp
                        ),
                        color = if (mission.done) {
                            MissionGreen
                        } else {
                            Color(0xFF96640A)
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1
                    )
                }

                Spacer(Modifier.height(8.dp))

                Checkbox(
                    checked = mission.done,
                    onCheckedChange = onCheckedChange,
                    colors = CheckboxDefaults.colors(
                        checkedColor = MissionGreen,
                        uncheckedColor = Color(0xFFADB4C4),
                        checkmarkColor = Color.White
                    )
                )
            }
        }
    }


}
