package com.example.gpstrackingapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gpstrackingapp.R
import com.example.gpstrackingapp.data.model.TrackingState
import com.example.gpstrackingapp.ui.viewmodel.TrackingViewModel
import com.example.gpstrackingapp.util.PermissionHandler
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.maps.android.compose.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.platform.LocalLifecycleOwner
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)

/**
 * Main tracking screen with map and controls
 */
@Composable
fun TrackingScreen(
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onRequestPermissions: () -> Unit = {},
    viewModel: TrackingViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val permissionHandler = remember { PermissionHandler(context) }
    
    val trackingState by viewModel.trackingState.collectAsStateWithLifecycle()
    val trackingMetrics by viewModel.trackingMetrics.collectAsStateWithLifecycle()
    val currentLocation by viewModel.currentLocation.collectAsStateWithLifecycle()
    val isTracking by viewModel.isTracking.collectAsStateWithLifecycle()
    
    var showPermissionDialog by remember { mutableStateOf(false) }
    var hasLocationPermission by remember { mutableStateOf(permissionHandler.hasLocationPermission()) }
    var hasShownPermissionDialog by remember { mutableStateOf(false) }
    
    // Check permissions on first load only
    LaunchedEffect(Unit) {
        if (!permissionHandler.hasAllRequiredPermissions() && !hasShownPermissionDialog) {
            showPermissionDialog = true
            hasShownPermissionDialog = true
        } else if (permissionHandler.hasAllRequiredPermissions()) {
            hasLocationPermission = true
            // Try to get initial location immediately
            viewModel.loadInitialLocation()
        }
    }
    
    // Check permissions when the screen is focused (user returns from settings)
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                // Check permissions when app comes back to foreground
                val newPermissionState = permissionHandler.hasLocationPermission()
                if (newPermissionState != hasLocationPermission) {
                    hasLocationPermission = newPermissionState
                    if (newPermissionState && currentLocation == null) {
                        // Try to get initial location if we have permissions but no location
                        viewModel.loadInitialLocation()
                    }
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    
    // Try to get initial location when permissions are granted
    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission && currentLocation == null) {
            viewModel.loadInitialLocation()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.tracking)) },
                actions = {
                    IconButton(onClick = onNavigateToHistory) {
                        Icon(Icons.Default.History, contentDescription = "History")
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Map Section
            MapSection(
                currentLocation = currentLocation,
                hasLocationPermission = hasLocationPermission,
                onRefreshPermission = {
                    hasLocationPermission = permissionHandler.hasLocationPermission()
                    if (hasLocationPermission) {
                        viewModel.loadInitialLocation()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Tracking Status
            TrackingStatusCard(
                trackingState = trackingState,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Metrics Section
            MetricsSection(
                trackingMetrics = trackingMetrics,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Tracking Controls
            TrackingControls(
                trackingState = trackingState,
                onStartTracking = {
                    if (hasLocationPermission) {
                        viewModel.startTracking()
                    } else {
                        showPermissionDialog = true
                    }
                },
                onPauseTracking = { viewModel.pauseTracking() },
                onResumeTracking = { viewModel.resumeTracking() },
                onStopTracking = { viewModel.stopTracking() },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
    
    // Permission Dialog
    if (showPermissionDialog) {
        PermissionDialog(
            onDismiss = { showPermissionDialog = false },
            onGrantPermission = {
                onRequestPermissions()
                showPermissionDialog = false
                // Update permission state after a short delay
                kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
                    delay(1500) // Wait for permission dialog to close and system to process
                    hasLocationPermission = permissionHandler.hasLocationPermission()
                    if (hasLocationPermission) {
                        viewModel.loadInitialLocation()
                    }
                }
            }
        )
    }
}

@Composable
fun MapSection(
    currentLocation: com.example.gpstrackingapp.data.model.LocationPoint?,
    hasLocationPermission: Boolean,
    onRefreshPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    val defaultLocation = LatLng(37.7749, -122.4194) // San Francisco
    
    // Update camera position when current location changes
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            currentLocation?.let { LatLng(it.latitude, it.longitude) } ?: defaultLocation,
            15f
        )
    }
    
    // Auto-center camera when location is available
    LaunchedEffect(currentLocation) {
        currentLocation?.let { location ->
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(
                    LatLng(location.latitude, location.longitude),
                    15f
                ),
                durationMs = 1000
            )
        }
    }
    
    Box(modifier = modifier) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = hasLocationPermission,
                mapType = MapType.NORMAL
            ),
            uiSettings = MapUiSettings(
                myLocationButtonEnabled = hasLocationPermission,
                zoomControlsEnabled = true,
                compassEnabled = true
            )
        ) {
            // Only show marker if we have a location and it's not the same as the map center
            currentLocation?.let { location ->
                val locationLatLng = LatLng(location.latitude, location.longitude)
                // Only add marker if it's significantly different from the map center
                if (locationLatLng != cameraPositionState.position.target) {
                    Marker(
                        state = MarkerState(position = locationLatLng),
                        title = "Current Location",
                        snippet = "Lat: ${String.format("%.6f", location.latitude)}, Lon: ${String.format("%.6f", location.longitude)}"
                    )
                }
            }
        }
        
        // Show permission notice if location permission is not granted
        if (!hasLocationPermission) {
            Card(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Location permission required to show your location on map",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onRefreshPermission,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Refresh Permission State")
                    }
                }
            }
        }
    }
}

@Composable
fun TrackingStatusCard(
    trackingState: TrackingState,
    modifier: Modifier = Modifier
) {
    val (statusText, statusColor, statusIcon) = when (trackingState) {
        TrackingState.Active -> Triple(
            stringResource(R.string.tracking_active),
            MaterialTheme.colorScheme.primary,
            Icons.Default.PlayArrow
        )
        TrackingState.Paused -> Triple(
            stringResource(R.string.tracking_paused),
            MaterialTheme.colorScheme.secondary,
            Icons.Default.Pause
        )
        TrackingState.Stopped -> Triple(
            stringResource(R.string.tracking_stopped),
            MaterialTheme.colorScheme.error,
            Icons.Default.Stop
        )
        TrackingState.Starting -> Triple(
            "Starting...",
            MaterialTheme.colorScheme.primary,
            Icons.Default.Refresh
        )
        TrackingState.Stopping -> Triple(
            "Stopping...",
            MaterialTheme.colorScheme.secondary,
            Icons.Default.Refresh
        )
        TrackingState.Error -> Triple(
            "Error",
            MaterialTheme.colorScheme.error,
            Icons.Default.Error
        )
    }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = statusColor.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = statusIcon,
                contentDescription = null,
                tint = statusColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = statusText,
                style = MaterialTheme.typography.titleMedium,
                color = statusColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun MetricsSection(
    trackingMetrics: com.example.gpstrackingapp.data.model.TrackingMetrics,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Tracking Metrics",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MetricCard(
                title = stringResource(R.string.current_speed),
                value = trackingMetrics.getFormattedCurrentSpeed(),
                icon = Icons.Default.Speed,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            MetricCard(
                title = stringResource(R.string.distance_traveled),
                value = trackingMetrics.getFormattedDistance(),
                icon = Icons.Default.Straighten,
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MetricCard(
                title = stringResource(R.string.elapsed_time),
                value = trackingMetrics.getFormattedElapsedTime(),
                icon = Icons.Default.Timer,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            MetricCard(
                title = "Max Speed",
                value = trackingMetrics.getFormattedMaxSpeed(),
                icon = Icons.Default.Speed,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun TrackingControls(
    trackingState: TrackingState,
    onStartTracking: () -> Unit,
    onPauseTracking: () -> Unit,
    onResumeTracking: () -> Unit,
    onStopTracking: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Tracking Controls",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            when (trackingState) {
                TrackingState.Stopped -> {
                    Button(
                        onClick = onStartTracking,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.start_tracking))
                    }
                }
                TrackingState.Active -> {
                    Button(
                        onClick = onPauseTracking,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Icon(Icons.Default.Pause, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.pause_tracking))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onStopTracking,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(Icons.Default.Stop, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.stop_tracking))
                    }
                }
                TrackingState.Paused -> {
                    Button(
                        onClick = onResumeTracking,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.resume_tracking))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onStopTracking,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(Icons.Default.Stop, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.stop_tracking))
                    }
                }
                else -> {
                    // Loading states
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun PermissionDialog(
    onDismiss: () -> Unit,
    onGrantPermission: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                text = "Location Permission Required",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            ) 
        },
        text = { 
            Column {
                Text(
                    text = "This GPS tracking app needs location permissions to:",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "• Show your current location on the map",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
                )
                Text(
                    text = "• Track your GPS coordinates during trips",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
                )
                Text(
                    text = "• Calculate distance, speed, and route information",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onGrantPermission,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Grant Permission")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
