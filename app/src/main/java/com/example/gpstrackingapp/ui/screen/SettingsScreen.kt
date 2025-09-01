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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gpstrackingapp.R
import com.example.gpstrackingapp.ui.viewmodel.SettingsViewModel
import com.example.gpstrackingapp.ui.viewmodel.LocationUpdateInterval
import com.example.gpstrackingapp.ui.viewmodel.AutoStopDelay

@OptIn(ExperimentalMaterial3Api::class)

/**
 * Settings screen for app configuration
 */
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val locationUpdateInterval by viewModel.locationUpdateInterval.collectAsStateWithLifecycle()
    val backgroundTracking by viewModel.backgroundTracking.collectAsStateWithLifecycle()
    val autoStopTracking by viewModel.autoStopTracking.collectAsStateWithLifecycle()
    val autoStopDelay by viewModel.autoStopDelay.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    
    var showResetDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
            // Location Update Interval Section
            SettingsSection(
                title = stringResource(R.string.location_update_interval),
                modifier = Modifier.padding(16.dp)
            ) {
                LocationUpdateIntervalSetting(
                    currentInterval = locationUpdateInterval,
                    availableIntervals = viewModel.getAvailableIntervals(),
                    onIntervalSelected = { interval ->
                        viewModel.updateLocationUpdateInterval(interval.intervalMs)
                    }
                )
            }
            
            Divider(modifier = Modifier.padding(horizontal = 16.dp))
            
            // Background Tracking Section
            SettingsSection(
                title = stringResource(R.string.background_tracking),
                description = stringResource(R.string.background_tracking_description),
                modifier = Modifier.padding(16.dp)
            ) {
                SwitchSetting(
                    checked = backgroundTracking,
                    onCheckedChange = { enabled ->
                        viewModel.updateBackgroundTracking(enabled)
                    }
                )
            }
            
            Divider(modifier = Modifier.padding(horizontal = 16.dp))
            
            // Auto-stop Tracking Section
            SettingsSection(
                title = stringResource(R.string.auto_stop_tracking),
                description = stringResource(R.string.auto_stop_tracking_description),
                modifier = Modifier.padding(16.dp)
            ) {
                SwitchSetting(
                    checked = autoStopTracking,
                    onCheckedChange = { enabled ->
                        viewModel.updateAutoStopTracking(enabled)
                    }
                )
            }
            
            // Auto-stop Delay Section (only show if auto-stop is enabled)
            if (autoStopTracking) {
                Divider(modifier = Modifier.padding(horizontal = 16.dp))
                
                SettingsSection(
                    title = stringResource(R.string.auto_stop_delay),
                    description = stringResource(R.string.auto_stop_delay_description),
                    modifier = Modifier.padding(16.dp)
                ) {
                    AutoStopDelaySetting(
                        currentDelay = autoStopDelay,
                        availableDelays = viewModel.getAvailableAutoStopDelays(),
                        onDelaySelected = { delay ->
                            viewModel.updateAutoStopDelay(delay.minutes)
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Reset to Defaults Button
            Button(
                onClick = { showResetDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(Icons.Default.Restore, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reset to Defaults")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
    
    // Reset Confirmation Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Settings") },
            text = { Text("Are you sure you want to reset all settings to their default values?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetToDefaults()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun SettingsSection(
    title: String,
    description: String? = null,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        
        if (description != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        content()
    }
}

@Composable
fun LocationUpdateIntervalSetting(
    currentInterval: Long,
    availableIntervals: List<LocationUpdateInterval>,
    onIntervalSelected: (LocationUpdateInterval) -> Unit
) {
    Column {
        availableIntervals.forEach { interval ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = currentInterval == interval.intervalMs,
                    onClick = { onIntervalSelected(interval) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = interval.displayName,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun AutoStopDelaySetting(
    currentDelay: Int,
    availableDelays: List<AutoStopDelay>,
    onDelaySelected: (AutoStopDelay) -> Unit
) {
    Column {
        availableDelays.forEach { delay ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = currentDelay == delay.minutes,
                    onClick = { onDelaySelected(delay) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = delay.displayName,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun SwitchSetting(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (checked) "Enabled" else "Disabled",
            style = MaterialTheme.typography.bodyMedium
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}
