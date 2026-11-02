
package com.krishna.skillsprint

import android.Manifest
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DailyLearningPlanScreen() {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val manager = remember { FirestoreProgressManager() }

    val today = remember { LocalDate.now().toString() }

    val formattedDate = remember(today) {
        LocalDate.parse(today).format(
            DateTimeFormatter.ofPattern(
                "EEEE, dd MMMM yyyy",
                Locale.getDefault()
            )
        )
    }

    var note by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }
    var saveMessage by remember { mutableStateOf("") }
    var showPermissionMessage by remember { mutableStateOf(false) }

    var reminderEnabled by remember {
        mutableStateOf(
            DailyReminderScheduler.isReminderEnabled(context)
        )
    }

    var reminderHour by remember {
        mutableStateOf(
            DailyReminderScheduler.getReminderHour(context)
        )
    }

    var reminderMinute by remember {
        mutableStateOf(
            DailyReminderScheduler.getReminderMinute(context)
        )
    }

    val notificationPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->

            if (isGranted) {
                reminderEnabled = true
                showPermissionMessage = false

                DailyReminderScheduler.saveReminderSettings(
                    context = context,
                    enabled = true,
                    hour = reminderHour,
                    minute = reminderMinute
                )

                saveMessage = "Daily reminder enabled!"
            } else {
                reminderEnabled = false
                showPermissionMessage = true
                saveMessage = ""
            }
        }

    // Load today's note from Firebase.
    LaunchedEffect(today) {
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser == null) {
            saveMessage = "Please log in to save your notes."
            isLoading = false
        } else {
            try {
                note = manager.loadDailyNote(today)
            } catch (e: Exception) {
                saveMessage = "Could not load note. Please try again."
            } finally {
                isLoading = false
            }
        }
    }

    val formattedTime = remember(reminderHour, reminderMinute) {
        val hour12 = if (reminderHour % 12 == 0) {
            12
        } else {
            reminderHour % 12
        }

        val amPm = if (reminderHour < 12) "AM" else "PM"

        String.format(
            Locale.getDefault(),
            "%02d:%02d %s",
            hour12,
            reminderMinute,
            amPm
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            // Header
            Text(
                text = "Daily Notes",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "What's the plan for today?",
                fontSize = 21.sp,
                fontWeight = FontWeight.SemiBold
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor =
                        MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Today's date"
                    )

                    Spacer(modifier = Modifier.padding(horizontal = 6.dp))

                    Column {
                        Text(
                            text = "TODAY",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = formattedDate,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Note editor
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "My Plan",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = note,
                        onValueChange = {
                            note = it
                            saveMessage = ""
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        placeholder = {
                            Text(
                                "Write anything you want to achieve today...\n\n" +
                                        "Example:\n" +
                                        "• Learn Kotlin\n" +
                                        "• Practise English\n" +
                                        "• Complete one project"
                            )
                        },
                        enabled = !isLoading && !isSaving,
                        shape = RoundedCornerShape(12.dp),
                        minLines = 5,
                        maxLines = 10
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            val currentUser =
                                FirebaseAuth.getInstance().currentUser

                            if (currentUser == null) {
                                saveMessage =
                                    "Please log in before saving your note."
                            } else {
                                scope.launch {
                                    isSaving = true
                                    saveMessage = ""

                                    try {
                                        manager.saveDailyNote(
                                            date = today,
                                            note = note
                                        )

                                        saveMessage =
                                            "Note saved successfully!"
                                    } catch (e: Exception) {
                                        saveMessage =
                                            "Unable to save note. Check your internet and try again."
                                    } finally {
                                        isSaving = false
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading && !isSaving
                    ) {
                        androidx.compose.material3.Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Save note"
                        )

                        Spacer(modifier = Modifier.padding(horizontal = 4.dp))

                        Text(
                            text = when {
                                isSaving -> "Saving..."
                                isLoading -> "Loading..."
                                else -> "Save Today's Note"
                            }
                        )
                    }

                    if (saveMessage.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = saveMessage,
                            color = if (
                                saveMessage.contains(
                                    "successfully",
                                    ignoreCase = true
                                ) ||
                                saveMessage.contains(
                                    "enabled",
                                    ignoreCase = true
                                )
                            ) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.error
                            },
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Reminder settings
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        androidx.compose.material3.Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Reminder"
                        )

                        Spacer(modifier = Modifier.padding(horizontal = 5.dp))

                        Text(
                            text = "Daily Reminder",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )

                        Switch(
                            checked = reminderEnabled,
                            onCheckedChange = { enabled ->

                                if (enabled) {
                                    val permissionGranted =
                                        Build.VERSION.SDK_INT <
                                                Build.VERSION_CODES.TIRAMISU ||
                                                ContextCompat.checkSelfPermission(
                                                    context,
                                                    Manifest.permission.POST_NOTIFICATIONS
                                                ) == PackageManager.PERMISSION_GRANTED

                                    if (permissionGranted) {
                                        reminderEnabled = true
                                        showPermissionMessage = false

                                        DailyReminderScheduler.saveReminderSettings(
                                            context,
                                            true,
                                            reminderHour,
                                            reminderMinute
                                        )

                                        saveMessage =
                                            "Daily reminder enabled!"
                                    } else {
                                        notificationPermissionLauncher.launch(
                                            Manifest.permission.POST_NOTIFICATIONS
                                        )
                                    }
                                } else {
                                    reminderEnabled = false
                                    showPermissionMessage = false

                                    DailyReminderScheduler.saveReminderSettings(
                                        context,
                                        false,
                                        reminderHour,
                                        reminderMinute
                                    )

                                    saveMessage =
                                        "Daily reminder turned off."
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Get a notification every day to review your plan.",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Reminder time: $formattedTime",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextButton(
                            onClick = {
                                reminderHour = 20
                                reminderMinute = 0

                                if (reminderEnabled) {
                                    DailyReminderScheduler.saveReminderSettings(
                                        context,
                                        true,
                                        reminderHour,
                                        reminderMinute
                                    )
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Default 8 PM")
                        }

                        Button(
                            onClick = {
                                TimePickerDialog(
                                    context,
                                    { _, selectedHour, selectedMinute ->

                                        reminderHour = selectedHour
                                        reminderMinute = selectedMinute

                                        if (reminderEnabled) {
                                            DailyReminderScheduler.saveReminderSettings(
                                                context,
                                                true,
                                                selectedHour,
                                                selectedMinute
                                            )
                                        }

                                        saveMessage = if (reminderEnabled) {
                                            "Reminder time updated!"
                                        } else {
                                            "Time selected. Turn on the reminder to activate it."
                                        }
                                    },
                                    reminderHour,
                                    reminderMinute,
                                    false
                                ).show()
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Custom Time")
                        }
                    }

                    if (showPermissionMessage) {
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Notifications are not allowed. Enable notifications for SkillSprint in your phone's app settings, then turn the reminder on again.",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Text(
                text = "Your notes are saved separately for each date. Saving notes does not award XP or leaderboard points.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
