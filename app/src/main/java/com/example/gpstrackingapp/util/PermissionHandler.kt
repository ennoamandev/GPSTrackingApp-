package com.example.gpstrackingapp.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles location permissions for the GPS tracking app
 */
class PermissionHandler(
    private val context: Context
) {
    
    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1001
        const val BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE = 1002
    }
    
    /**
     * Check if location permission is granted
     */
    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    /**
     * Check if background location permission is granted (Android 10+)
     */
    fun hasBackgroundLocationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Not required on older versions
        }
    }
    
    /**
     * Check if all required permissions are granted
     */
    fun hasAllRequiredPermissions(): Boolean {
        return hasLocationPermission() && hasBackgroundLocationPermission()
    }
    
    /**
     * Get required permissions list
     */
    fun getRequiredPermissions(): List<String> {
        val permissions = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }
        
        return permissions
    }
    
    /**
     * Get missing permissions
     */
    fun getMissingPermissions(): List<String> {
        return getRequiredPermissions().filter { permission ->
            ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED
        }
    }
    
    /**
     * Check if permission should show rationale
     */
    fun shouldShowPermissionRationale(activity: FragmentActivity, permission: String): Boolean {
        return activity.shouldShowRequestPermissionRationale(permission)
    }
    
    /**
     * Check if any permissions need to be requested
     */
    fun needsPermissionRequest(): Boolean {
        return getMissingPermissions().isNotEmpty()
    }
    
    /**
     * Get permission explanation text
     */
    fun getPermissionExplanationText(permission: String): String {
        return when (permission) {
            Manifest.permission.ACCESS_FINE_LOCATION -> 
                "Location permission is required to track your GPS coordinates during trips."
            Manifest.permission.ACCESS_BACKGROUND_LOCATION -> 
                "Background location permission allows tracking to continue when the app is not in use."
            else -> "This permission is required for the app to function properly."
        }
    }
}
