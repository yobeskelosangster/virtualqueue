
package com.example.virtualqueue

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun getStartedButtonIsDisplayed() {
        composeTestRule.setContent {
            QueueTheme {
                LandingScreen(
                    onGetStarted = {},
                    onLogin = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText("Get Started", useUnmergedTree = true)
            .assertIsDisplayed()
    }
}
