package com.example.gpstrackingapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gpstrackingapp.ui.screen.HistoryScreen
import com.example.gpstrackingapp.ui.screen.SettingsScreen
import com.example.gpstrackingapp.ui.screen.TrackingScreen
import com.example.gpstrackingapp.ui.theme.GPSTrackingAppTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main activity for the GPS Tracking App
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        if (allGranted) {
            // Permissions granted, you can now use location features
            println("Location permissions granted")
            // You could trigger a recomposition here if needed
        } else {
            // Some permissions denied
            println("Some location permissions denied")
            // Show a message to the user about the importance of permissions
            val deniedPermissions = permissions.filter { !it.value }.keys
            println("Denied permissions: $deniedPermissions")
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GPSTrackingAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GPSTrackingApp(
                        onRequestPermissions = { requestLocationPermissions() }
                    )
                }
            }
        }
    }
    
    private fun requestLocationPermissions() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        
        // Check if permissions are already granted
        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()
        
        if (permissionsToRequest.isNotEmpty()) {
            locationPermissionRequest.launch(permissionsToRequest)
        } else {
            println("All location permissions are already granted")
        }
    }
}

@Composable
fun GPSTrackingApp(
    onRequestPermissions: () -> Unit = {}
) {
    val navController = rememberNavController()
    
    NavHost(navController = navController, startDestination = "tracking") {
        composable("tracking") {
            TrackingScreen(
                onNavigateToHistory = { navController.navigate("history") },
                onNavigateToSettings = { navController.navigate("settings") },
                onRequestPermissions = onRequestPermissions
            )
        }
        composable("history") {
            HistoryScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToSettings = { navController.navigate("settings") }
            )
        }
        composable("settings") {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
