package com.example.gpstrackingapp.data.model

/**
 * Represents the current state of GPS tracking
 */
sealed class TrackingState {
    object Stopped : TrackingState()
    object Active : TrackingState()
    object Paused : TrackingState()
    object Starting : TrackingState()
    object Stopping : TrackingState()
    object Error : TrackingState()
}
