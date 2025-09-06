package com.example.gpstrackingapp.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.gpstrackingapp.data.model.TrackingState
import com.example.gpstrackingapp.data.model.TrackingMetrics
import com.example.gpstrackingapp.ui.screen.TrackingScreen
import com.example.gpstrackingapp.ui.viewmodel.TrackingViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * UI tests for TrackingScreen
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class TrackingScreenTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `tracking screen should display start button when stopped`() {
        // Given
        val trackingState = MutableStateFlow(TrackingState.Stopped)
        val trackingMetrics = MutableStateFlow(TrackingMetrics())

        // When
        composeTestRule.setContent {
            TrackingScreen(
                onNavigateBack = {},
                onNavigateToHistory = {},
                onNavigateToSettings = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText("Start Tracking").assertIsDisplayed()
    }

    @Test
    fun `tracking screen should display pause button when active`() {
        // Given
        val trackingState = MutableStateFlow(TrackingState.Active)
        val trackingMetrics = MutableStateFlow(TrackingMetrics())

        // When
        composeTestRule.setContent {
            TrackingScreen(
                onNavigateBack = {},
                onNavigateToHistory = {},
                onNavigateToSettings = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText("Pause").assertIsDisplayed()
    }

    @Test
    fun `tracking screen should display metrics when tracking`() {
        // Given
        val trackingState = MutableStateFlow(TrackingState.Active)
        val trackingMetrics = MutableStateFlow(
            TrackingMetrics(
                currentSpeed = 10.0f,
                averageSpeed = 8.0f,
                maxSpeed = 15.0f,
                distance = 1000.0,
                elapsedTime = 60000L,
                locationCount = 10
            )
        )

        // When
        composeTestRule.setContent {
            TrackingScreen(
                onNavigateBack = {},
                onNavigateToHistory = {},
                onNavigateToSettings = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText("Speed").assertIsDisplayed()
        composeTestRule.onNodeWithText("Distance").assertIsDisplayed()
        composeTestRule.onNodeWithText("Time").assertIsDisplayed()
    }

    @Test
    fun `tracking screen should display navigation buttons`() {
        // Given
        val trackingState = MutableStateFlow(TrackingState.Stopped)
        val trackingMetrics = MutableStateFlow(TrackingMetrics())

        // When
        composeTestRule.setContent {
            TrackingScreen(
                onNavigateBack = {},
                onNavigateToHistory = {},
                onNavigateToSettings = {}
            )
        }

        // Then
        composeTestRule.onNodeWithContentDescription("History").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Settings").assertIsDisplayed()
    }
}
