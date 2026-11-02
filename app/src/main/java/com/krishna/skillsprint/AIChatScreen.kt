
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material.icons.rounded.TipsAndUpdates
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

private val ChatBackground = Color(0xFFF5F7FC)
private val ChatPurple = Color(0xFF7866E8)
private val ChatText = Color(0xFF182230)
private val ChatMuted = Color(0xFF697586)

private data class ChatMessage(
    val text: String,
    val isUser: Boolean
)

@Composable
fun AIChatScreen(navController: NavController) {

    var input by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val messages = remember {
        mutableStateListOf(
            ChatMessage(
                text = "Hi! 👋 I'm your English practice assistant.\n\nYou can practise conversations, ask grammar questions, or learn new vocabulary. What would you like to practise today?",
                isUser = false
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ChatBackground)
    ) {

        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 12.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = { navController.popBackStack() }
            ) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = "Go back",
                    tint = ChatText
                )
            }

            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = ChatPurple.copy(alpha = 0.12f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Rounded.AutoAwesome,
                        contentDescription = "AI assistant",
                        tint = ChatPurple
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Text(
                    text = "English AI Coach",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = ChatText
                )

                Text(
                    text = "Practise • Learn • Improve",
                    style = MaterialTheme.typography.bodySmall,
                    color = ChatMuted
                )
            }
        }

        // Conversation
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            reverseLayout = false
        ) {
            items(messages) { message ->

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (message.isUser) {
                        Arrangement.End
                    } else {
                        Arrangement.Start
                    }
                ) {
                    Surface(
                        shape = RoundedCornerShape(
                            topStart = 18.dp,
                            topEnd = 18.dp,
                            bottomStart = if (message.isUser) 18.dp else 4.dp,
                            bottomEnd = if (message.isUser) 4.dp else 18.dp
                        ),
                        color = if (message.isUser) {
                            ChatPurple
                        } else {
                            Color.White
                        },
                        shadowElevation = 1.dp,
                        modifier = Modifier.fillMaxWidth(0.88f)
                    ) {
                        Text(
                            text = message.text,
                            modifier = Modifier.padding(14.dp),
                            color = if (message.isUser) {
                                Color.White
                            } else {
                                ChatText
                            },
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            if (isLoading) {
                item {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = ChatPurple,
                            strokeWidth = 2.dp
                        )

                        Text(
                            text = "Preparing your response...",
                            modifier = Modifier.padding(start = 10.dp),
                            color = ChatMuted
                        )
                    }
                }
            }
        }

        // Practice suggestions
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.TipsAndUpdates,
                    contentDescription = null,
                    tint = ChatPurple,
                    modifier = Modifier.size(18.dp)
                )

                Text(
                    text = "Try practising",
                    modifier = Modifier.padding(start = 6.dp),
                    color = ChatMuted,
                    style = MaterialTheme.typography.labelMedium
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                PracticeSuggestion(
                    text = "Correct my grammar",
                    onClick = {
                        input = "Please correct my English grammar and explain my mistakes."
                    },
                    modifier = Modifier.weight(1f)
                )

                PracticeSuggestion(
                    text = "Daily conversation",
                    onClick = {
                        input = "Let's practise a daily English conversation. Ask me one question at a time."
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Message input
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text("Write in English or Hindi...")
                },
                shape = RoundedCornerShape(24.dp),
                maxLines = 4,
                enabled = !isLoading
            )

            IconButton(
                onClick = {
                    val question = input.trim()

                    if (question.isNotEmpty() && !isLoading) {
                        messages.add(
                            ChatMessage(
                                text = question,
                                isUser = true
                            )
                        )

                        input = ""

                        // Real AI connection will be added next.
                        messages.add(
                            ChatMessage(
                                text = "Your message has been added. The AI service is not connected yet. We'll connect the secure backend in the next step.",
                                isUser = false
                            )
                        )
                    }
                },
                enabled = input.isNotBlank() && !isLoading
            ) {
                Icon(
                    imageVector = Icons.Rounded.Send,
                    contentDescription = "Send message",
                    tint = ChatPurple
                )
            }
        }
    }
}

@Composable
private fun PracticeSuggestion(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.textButtonColors(
            contentColor = ChatPurple
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

