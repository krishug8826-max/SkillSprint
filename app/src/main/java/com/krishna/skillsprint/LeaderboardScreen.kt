
package com.krishna.skillsprint

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import androidx.compose.ui.unit.sp
private val BoardBackground = Color(0xFFF5F7FC)
private val BoardPurple = Color(0xFF7866E8)
private val BoardDarkPurple = Color(0xFF5140B5)
private val BoardText = Color(0xFF182230)
private val BoardMuted = Color(0xFF697586)
private val BoardGold = Color(0xFFFFD76A)
private val BoardSilver = Color(0xFFDCE3EE)
private val BoardBronze = Color(0xFFE8B18A)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    navController: NavController
) {
    val scope = rememberCoroutineScope()
    val manager = remember { FirestoreProgressManager() }

    var users by remember { mutableStateOf<List<LeaderboardUser>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    val currentUid = FirebaseAuth.getInstance().currentUser?.uid

    fun refreshLeaderboard() {
        scope.launch {
            loading = true
            error = null

            try {
                users = manager.loadLeaderboard()
                    .sortedByDescending { it.points }
            } catch (e: Exception) {
                error = "Leaderboard load nahi ho paaya. Internet aur Firebase security rules check karo."
            } finally {
                loading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        refreshLeaderboard()
    }

    val currentUserRank = users.indexOfFirst {
        it.uid == currentUid
    }.let {
        if (it >= 0) it + 1 else null
    }

    val currentUser = users.firstOrNull {
        it.uid == currentUid
    }

    Scaffold(
        containerColor = BoardBackground,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Leaderboard",
                            fontWeight = FontWeight.Bold,
                            color = BoardText
                        )
                        Text(
                            text = "Compete. Improve. Rise.",
                            style = MaterialTheme.typography.bodySmall,
                            color = BoardMuted
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = BoardText
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { refreshLeaderboard() },
                        enabled = !loading
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = "Refresh",
                            tint = BoardPurple
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BoardBackground
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            LeaderboardHeroCard(
                rank = currentUserRank,
                points = currentUser?.points ?: 0
            )

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Top learners",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = BoardText
                    )
                    Text(
                        text = "Your effort earns your position",
                        style = MaterialTheme.typography.bodySmall,
                        color = BoardMuted
                    )
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color(0xFFEDE9FF)
                ) {
                    Text(
                        text = "${users.size} learners",
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 7.dp
                        ),
                        color = BoardDarkPurple,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            when {
                loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                color = BoardPurple
                            )
                            Spacer(Modifier.height(12.dp))
                            Text(
                                "Loading rankings...",
                                color = BoardMuted
                            )
                        }
                    }
                }

                error != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = error.orEmpty(),
                                color = BoardMuted
                            )

                            Spacer(Modifier.height(12.dp))

                            Button(
                                onClick = { refreshLeaderboard() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BoardPurple
                                )
                            ) {
                                Text("Try again")
                            }
                        }
                    }
                }

                users.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.EmojiEvents,
                                contentDescription = null,
                                tint = BoardPurple,
                                modifier = Modifier.size(56.dp)
                            )

                            Spacer(Modifier.height(12.dp))

                            Text(
                                "Your leaderboard starts here!",
                                fontWeight = FontWeight.Bold,
                                color = BoardText
                            )

                            Spacer(Modifier.height(6.dp))

                            Text(
                                "Complete missions and projects to earn XP.",
                                color = BoardMuted
                            )

                            Spacer(Modifier.height(12.dp))

                            OutlinedButton(
                                onClick = { refreshLeaderboard() }
                            ) {
                                Text("Refresh leaderboard")
                            }
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        if (users.size >= 3) {
                            item {
                                TopThreeSection(
                                    users = users.take(3),
                                    currentUid = currentUid
                                )
                            }

                            item {
                                Text(
                                    text = "All rankings",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = BoardText,
                                    modifier = Modifier.padding(
                                        top = 4.dp,
                                        bottom = 2.dp
                                    )
                                )
                            }
                        }

                        itemsIndexed(
                            items = users,
                            key = { _, user -> user.uid }
                        ) { index, user ->
                            LeaderboardUserCard(
                                rank = index + 1,
                                user = user,
                                isCurrentUser = user.uid == currentUid
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LeaderboardHeroCard(
    rank: Int?,
    points: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF176B52),
                            Color(0xFF0D4938),
                            Color(0xFF123C32)
                        )
                    )
                )
                .padding(22.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(50.dp),
                            color = Color.White.copy(alpha = 0.14f)
                        ) {
                            Text(
                                text = "  ✦ YOUR JOURNEY  ",
                                modifier = Modifier.padding(
                                    horizontal = 7.dp,
                                    vertical = 7.dp
                                ),
                                color = Color(0xFFD1F5E4),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            )
                        }


                        Spacer(Modifier.height(14.dp))

                        Text(
                            text = if (rank != null) {
                                "You're ranked #$rank!"
                            } else {
                                "Your journey starts here!"
                            },
                            color = Color.White,
                            fontSize = 25.sp,
                            lineHeight = 31.sp,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = "Keep learning. Keep growing. Keep winning.",
                            color = Color.White.copy(alpha = 0.82f),
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    }

                    Spacer(Modifier.width(10.dp))

                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .background(
                                color = Color.White.copy(alpha = 0.12f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.EmojiEvents,
                            contentDescription = "Leaderboard trophy",
                            tint = Color(0xFFFFD76A),
                            modifier = Modifier.size(42.dp)
                        )
                    }
                }

                Spacer(Modifier.height(22.dp))

                HorizontalDivider(
                    color = Color.White.copy(alpha = 0.18f)
                )

                Spacer(Modifier.height(17.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color.White.copy(alpha = 0.13f)
                    ) {
                        Row(
                            modifier = Modifier.padding(
                                horizontal = 13.dp,
                                vertical = 11.dp
                            ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFD76A),
                                modifier = Modifier.size(21.dp)
                            )

                            Spacer(Modifier.width(7.dp))

                            Column {
                                Text(
                                    text = "TOTAL XP",
                                    color = Color.White.copy(alpha = 0.72f),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp
                                )

                                Text(
                                    text = "$points XP",
                                    color = Color.White,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }

                    Spacer(Modifier.weight(1f))

                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "YOUR NEXT GOAL",
                            color = Color.White.copy(alpha = 0.72f),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )

                        Spacer(Modifier.height(5.dp))

                        Text(
                            text = if (rank == 1) {
                                "Stay on top! 👑"
                            } else {
                                "Rise higher! 🚀"
                            },
                            color = Color(0xFFD1F5E4),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }


}


@Composable
private fun TopThreeSection(
    users: List<LeaderboardUser>,
    currentUid: String?
) {
    val first = users.getOrNull(0)
    val second = users.getOrNull(1)
    val third = users.getOrNull(2)


    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(
            1.dp,
            Color(0xFFE7ECEF)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 20.dp
            )
        ) {
            Text(
                text = "🏆  CHAMPIONS PODIUM",
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                color = BoardText,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )

            Spacer(Modifier.height(5.dp))

            Text(
                text = "Celebrating the top learners",
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                color = BoardMuted,
                fontSize = 12.sp
            )

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                if (second != null) {
                    TopLearnerCard(
                        user = second,
                        rank = 2,
                        isCurrentUser = second.uid == currentUid,
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = 24.dp)
                    )
                }

                if (first != null) {
                    TopLearnerCard(
                        user = first,
                        rank = 1,
                        isCurrentUser = first.uid == currentUid,
                        modifier = Modifier.weight(1.12f)
                    )
                }

                if (third != null) {
                    TopLearnerCard(
                        user = third,
                        rank = 3,
                        isCurrentUser = third.uid == currentUid,
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = 34.dp)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            HorizontalDivider(
                color = Color(0xFFEEF0F5)
            )

            Spacer(Modifier.height(13.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.Star,
                    contentDescription = null,
                    tint = Color(0xFFD99A20),
                    modifier = Modifier.size(17.dp)
                )

                Spacer(Modifier.width(6.dp))

                Text(
                    text = "Every mission takes you one step higher.",
                    color = BoardMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }


}


@Composable
private fun TopLearnerCard(
    user: LeaderboardUser,
    rank: Int,
    isCurrentUser: Boolean,
    modifier: Modifier = Modifier
) {
    val medalColor = when (rank) {
        1 -> BoardGold
        2 -> BoardSilver
        else -> BoardBronze
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(if (rank == 1) 62.dp else 52.dp)
                .background(
                    color = medalColor.copy(alpha = 0.30f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = when (rank) {
                    1 -> "🥇"
                    2 -> "🥈"
                    else -> "🥉"
                },
                style = MaterialTheme.typography.headlineMedium
            )
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = user.name.ifBlank { "Learner" },
            color = if (isCurrentUser) BoardDarkPurple else BoardText,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        if (isCurrentUser) {
            Text(
                text = "YOU",
                color = BoardPurple,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Spacer(Modifier.height(5.dp))

        Text(
            text = "${user.points} XP",
            color = BoardDarkPurple,
            fontWeight = FontWeight.ExtraBold,
            style = MaterialTheme.typography.titleSmall
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = "${user.completedMissions} missions",
            color = BoardMuted,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1
        )
    }
}

@Composable
private fun LeaderboardUserCard(
    rank: Int,
    user: LeaderboardUser,
    isCurrentUser: Boolean
) {
    val rankColor = when (rank) {
        1 -> BoardGold
        2 -> BoardSilver
        3 -> BoardBronze
        else -> Color(0xFFF0F1F6)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrentUser) {
                Color(0xFFEDE9FF)
            } else {
                Color.White
            }
        ),
        border = if (isCurrentUser) {
            BorderStroke(1.5.dp, BoardPurple)
        } else {
            null
        },
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isCurrentUser) 2.dp else 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(rankColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (rank) {
                        1 -> "🥇"
                        2 -> "🥈"
                        3 -> "🥉"
                        else -> "#$rank"
                    },
                    color = BoardText,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Text(
                    text = user.name.ifBlank { "Learner" } +
                            if (isCurrentUser) " (You)" else "",
                    fontWeight = FontWeight.Bold,
                    color = BoardText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(5.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.LocalFireDepartment,
                        contentDescription = null,
                        tint = Color(0xFFE77B37),
                        modifier = Modifier.size(17.dp)
                    )

                    Spacer(Modifier.width(3.dp))

                    Text(
                        text = "${user.currentStreak} day streak",
                        color = BoardMuted,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "${user.points} XP",
                    color = BoardDarkPurple,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(Modifier.height(3.dp))

                Text(
                    text = "${user.completedMissions} missions",
                    color = BoardMuted,
                    style = MaterialTheme.typography.labelSmall
                )


            }
        }
    }
}

