
package com.krishna.skillsprint

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import com.krishna.skillsprint.auth.AuthRepository
import com.krishna.skillsprint.auth.LoginScreen
import com.krishna.skillsprint.auth.SignupScreen

@Composable
fun AppNavigation() {
    val authRepository = remember { AuthRepository() }

    var currentScreen by rememberSaveable {
        mutableStateOf(
            if (authRepository.currentUser != null) {
                "dashboard"
            } else {
                "login"
            }
        )
    }

    when (currentScreen) {
        "signup" -> {
            SignupScreen(
                onSignupSuccess = {
                    currentScreen = "dashboard"
                },
                onNavigateToLogin = {
                    currentScreen = "login"
                }
            )
        }

        "dashboard" -> {
            SkillSprintApp(
                onLogout = {
                    authRepository.logout()
                    currentScreen = "login"
                }
            )
        }

        else -> {
            LoginScreen(
                onLoginSuccess = {
                    currentScreen = "dashboard"
                },
                onNavigateToSignup = {
                    currentScreen = "signup"
                }
            )
        }
    }
}

