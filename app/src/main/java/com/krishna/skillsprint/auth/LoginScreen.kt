
package com.krishna.skillsprint.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private val AuthGreen = Color(0xFF159B75)
private val AuthBackground = Color(0xFFF5F7FC)

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToSignup: () -> Unit
) {
    val repository = remember { AuthRepository() }
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }

    var showForgotPassword by remember { mutableStateOf(false) }
    var resetLoading by remember { mutableStateOf(false) }

    if (showForgotPassword) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AuthBackground)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Reset Password",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = AuthGreen
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Enter your registered email to receive a password reset link.",
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(28.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    errorMessage = ""
                    successMessage = ""
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Email address") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Mail,
                        contentDescription = "Email"
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (errorMessage.isNotBlank()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (successMessage.isNotBlank()) {
                Text(
                    text = successMessage,
                    color = AuthGreen,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = {
                    when {
                        email.isBlank() -> {
                            errorMessage = "Please enter your email."
                        }

                        !android.util.Patterns.EMAIL_ADDRESS
                            .matcher(email.trim())
                            .matches() -> {
                            errorMessage = "Please enter a valid email address."
                        }

                        else -> {
                            resetLoading = true
                            errorMessage = ""
                            successMessage = ""

                            scope.launch {
                                try {
                                    FirebaseAuth.getInstance()
                                        .sendPasswordResetEmail(email.trim())
                                        .await()

                                    successMessage =
                                        "If an account matches this email, a password reset email will be sent. Check your inbox and spam folder."
                                } catch (e: Exception) {
                                    // Avoid exposing whether an account exists.
                                    successMessage =
                                        "If an account matches this email, a password reset email will be sent. Check your inbox and spam folder."
                                } finally {
                                    resetLoading = false
                                }
                            }
                        }
                    }
                },
                enabled = !resetLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AuthGreen
                )
            ) {
                if (resetLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.height(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Send Reset Link",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = {
                    showForgotPassword = false
                    errorMessage = ""
                    successMessage = ""
                }
            ) {
                Text(
                    text = "Back to Login",
                    color = AuthGreen
                )
            }
        }

        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AuthBackground)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "SkillSprint",
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            color = AuthGreen
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Learn. Build. Grow.",
            color = Color.Gray,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(36.dp))

        Text(
            text = "Welcome Back!",
            fontSize = 27.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF182230)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Login to continue your journey",
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(28.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                errorMessage = ""
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Email address") },
            leadingIcon = {
                Icon(Icons.Default.Mail, contentDescription = "Email")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            ),
            singleLine = true,
            shape = RoundedCornerShape(14.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                errorMessage = ""
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Password") },
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = "Password")
            },
            trailingIcon = {
                IconButton(
                    onClick = { passwordVisible = !passwordVisible }
                ) {
                    Icon(
                        imageVector = if (passwordVisible) {
                            Icons.Default.VisibilityOff
                        } else {
                            Icons.Default.Visibility
                        },
                        contentDescription = "Show or hide password"
                    )
                }
            },
            visualTransformation = if (passwordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp)
        )

        // Forgot Password option
        TextButton(
            onClick = {
                showForgotPassword = true
                errorMessage = ""
                successMessage = ""
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(
                text = "Forgot Password?",
                color = AuthGreen
            )
        }

        if (errorMessage.isNotBlank()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                when {
                    email.isBlank() ->
                        errorMessage = "Please enter your email."

                    password.isBlank() ->
                        errorMessage = "Please enter your password."

                    else -> {
                        loading = true
                        errorMessage = ""

                        scope.launch {
                            val result = repository.login(
                                email = email.trim(),
                                password = password
                            )

                            loading = false

                            result.onSuccess {
                                onLoginSuccess()
                            }.onFailure { error ->
                                errorMessage = when {
                                    error.message?.contains(
                                        "network",
                                        ignoreCase = true
                                    ) == true ->
                                        "Network issue. Please try again."

                                    else ->
                                        error.localizedMessage
                                            ?: "Login failed. Please try again."
                                }
                            }
                        }
                    }
                }
            },
            enabled = !loading,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AuthGreen
            )
        ) {
            if (loading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.height(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Login",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        TextButton(onClick = onNavigateToSignup) {
            Text(
                text = "New to SkillSprint? Create Account",
                color = AuthGreen
            )
        }
    }
}
