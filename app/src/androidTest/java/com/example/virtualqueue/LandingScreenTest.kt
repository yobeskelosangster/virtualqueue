
package com.example.virtualqueue

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LandingScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun landingScreenDisplaysMainContent() {
        composeTestRule.setContent {
            QueueTheme {
                LandingScreen(
                    onGetStarted = {},
                    onLogin = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText("Virtual Queue")
            .assertExists()

        composeTestRule
            .onNodeWithText("Why Virtual Queue?")
            .assertExists()

        composeTestRule
            .onNodeWithText("How it works")
            .assertExists()
    }

    @Test
    fun clickingGetStartedCallsCallback() {
        var clickCount = 0

        composeTestRule.setContent {
            QueueTheme {
                LandingScreen(
                    onGetStarted = { clickCount++ },
                    onLogin = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText("Get started")
            .performClick()

        composeTestRule.runOnIdle {
            assertEquals(1, clickCount)
        }
    }

    @Test
    fun clickingLoginCallsCallback() {
        var loginClicked = false

        composeTestRule.setContent {
            QueueTheme {
                LandingScreen(
                    onGetStarted = {},
                    onLogin = { loginClicked = true }
                )
            }
        }

        composeTestRule
            .onNodeWithText("I already have an account")
            .performClick()

        composeTestRule.runOnIdle {
            assertEquals(true, loginClicked)
        }
    }
}
