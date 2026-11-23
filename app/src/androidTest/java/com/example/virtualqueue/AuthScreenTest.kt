
package com.example.virtualqueue

import androidx.compose.ui.test.performScrollTo

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class AuthScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loginScreenDisplaysCorrectFields() {
        composeTestRule.setContent {
            QueueTheme {
                AuthScreen(
                    state = AuthState(),
                    onLogin = { _, _ -> },
                    onRegister = { _, _, _, _ -> },
                    startAsRegister = false
                )
            }
        }

        composeTestRule.onNodeWithText("Welcome back")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Email")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Password")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("No account? Register")
            .assertIsDisplayed()
    }

    @Test
    fun registerScreenDisplaysCorrectFields() {
        composeTestRule.setContent {
            QueueTheme {
                AuthScreen(
                    state = AuthState(),
                    onLogin = { _, _ -> },
                    onRegister = { _, _, _, _ -> },
                    startAsRegister = true
                )
            }
        }

        composeTestRule.onNodeWithText("Create your account")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Full name")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Email")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Password")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("I'm a staff member")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Have an account? Login")
            .assertIsDisplayed()
    }

    @Test
    fun switchingFromRegisterToLoginDisplaysLoginFields() {
        composeTestRule.setContent {
            QueueTheme {
                AuthScreen(
                    state = AuthState(),
                    onLogin = { _, _ -> },
                    onRegister = { _, _, _, _ -> },
                    startAsRegister = true
                )
            }
        }

        composeTestRule.onNodeWithText("Have an account? Login")
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Welcome back")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("No account? Register")
            .assertIsDisplayed()
    }

    @Test
    fun switchingFromLoginToRegisterDisplaysRegistrationFields() {
        composeTestRule.setContent {
            QueueTheme {
                AuthScreen(
                    state = AuthState(),
                    onLogin = { _, _ -> },
                    onRegister = { _, _, _, _ -> },
                    startAsRegister = false
                )
            }
        }

        composeTestRule.onNodeWithText("No account? Register")
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Create your account")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Full name")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("I'm a staff member")
            .assertIsDisplayed()
    }

    @Test
    fun passwordVisibilityButtonTogglesLabel() {
        composeTestRule.setContent {
            QueueTheme {
                AuthScreen(
                    state = AuthState(),
                    onLogin = { _, _ -> },
                    onRegister = { _, _, _, _ -> },
                    startAsRegister = false
                )
            }
        }

        composeTestRule.onNodeWithText("Show")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Show")
            .performClick()

        composeTestRule.onNodeWithText("Hide")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Hide")
            .performClick()

        composeTestRule.onNodeWithText("Show")
            .assertIsDisplayed()
    }

    @Test
    fun staffAccessCodeAppearsWhenStaffOptionIsSelected() {
        composeTestRule.setContent {
            QueueTheme {
                AuthScreen(
                    state = AuthState(),
                    onLogin = { _, _ -> },
                    onRegister = { _, _, _, _ -> },
                    startAsRegister = true
                )
            }
        }

        composeTestRule.waitForIdle()

        composeTestRule
            .onNode(
                hasText("I'm a staff member") and isToggleable()
            )
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Staff access code")
            .assertIsDisplayed()
    }




    @Test
    fun registrationSubmitsUserDetails() {
        var submittedName = ""
        var submittedEmail = ""
        var submittedPassword = ""
        var submittedStaffCode = ""

        composeTestRule.setContent {
            QueueTheme {
                AuthScreen(
                    state = AuthState(),
                    onLogin = { _, _ -> },
                    onRegister = { name, email, password, staffCode ->
                        submittedName = name
                        submittedEmail = email
                        submittedPassword = password
                        submittedStaffCode = staffCode
                    },
                    startAsRegister = true
                )
            }
        }

        composeTestRule.onNodeWithText("Full name")
            .performTextInput("Test Student")

        composeTestRule.onNodeWithTag("emailField")
            .performTextInput("student@example.com")

        composeTestRule.onNodeWithTag("passwordField")
            .performTextInput("Password123")

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Register")
            .performScrollTo()
            .performClick()

        composeTestRule.runOnIdle {
            assertEquals("Test Student", submittedName)
            assertEquals("student@example.com", submittedEmail)
            assertEquals("Password123", submittedPassword)
            assertEquals("", submittedStaffCode)
        }
    }

    @Test
    fun loginSubmitsEmailAndPassword() {
        var submittedEmail = ""
        var submittedPassword = ""

        composeTestRule.setContent {
            QueueTheme {
                AuthScreen(
                    state = AuthState(),
                    onLogin = { email, password ->
                        submittedEmail = email
                        submittedPassword = password
                    },
                    onRegister = { _, _, _, _ -> },
                    startAsRegister = false
                )
            }
        }

        composeTestRule.onNodeWithTag("emailField")
            .performTextInput("student@example.com")

        composeTestRule.onNodeWithTag("passwordField")
            .performTextInput("Password123")

        composeTestRule.onNodeWithText("Login")
            .performClick()

        composeTestRule.runOnIdle {
            assertEquals("student@example.com", submittedEmail)
            assertEquals("Password123", submittedPassword)
        }
    }
}
