# GPS Tracking & Data Collection App

A modern Android application built with Jetpack Compose and MVVM architecture for real-time GPS tracking and trip data collection.

## Features

### 🗺️ Tracking Screen
- **Real-time GPS tracking** with Google Maps integration
- **Live metrics display**: current speed, distance traveled, elapsed time
- **Tracking controls**: start, pause/resume, and stop tracking
- **Continuous location collection** with configurable update intervals
- **Current location marker** on the map

### 📊 Trip History Screen
- **Complete trip history** with detailed information
- **Trip statistics**: start time, duration, distance, average/max speed
- **Trip management**: view details, delete trips
- **Overall statistics**: total trips, total distance, total time, average speed

### ⚙️ Settings Screen
- **Location update interval** configuration (1s, 5s, 10s)
- **Background tracking** toggle for continuous operation
- **Auto-stop tracking** when no movement detected
- **Configurable auto-stop delay** (1-15 minutes)
- **Reset to defaults** functionality

### 🔧 Technical Features
- **MVVM architecture** with clean separation of concerns
- **Room database** for local trip and location data storage
- **Hilt dependency injection** for clean dependency management
- **Kotlin Coroutines & Flow** for reactive data streams
- **Material Design 3** UI components
- **Background location tracking** with foreground service
- **Permission handling** for location access

## Architecture

The app follows the **MVVM (Model-View-ViewModel)** architecture pattern:

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│      View       │    │    ViewModel     │    │     Model       │
│   (Compose UI)  │◄──►│   (Business      │◄──►│  (Data Layer)   │
│                 │    │     Logic)       │    │                 │
└─────────────────┘    └──────────────────┘    └─────────────────┘
                                │
                                ▼
                       ┌──────────────────┐
                       │   Repository     │
                       │  (Data Access)   │
                       └──────────────────┘
                                │
                                ▼
                       ┌──────────────────┐
                       │   Room Database  │
                       │  (Local Storage) │
                       └──────────────────┘
```

### Package Structure

```
com.example.gpstrackingapp/
├── data/
│   ├── local/           # Room database, DAOs, entities
│   ├── model/           # Data classes and models
│   └── repository/      # Data access layer
├── di/                  # Hilt dependency injection modules
├── location/            # GPS location management
├── service/             # Background location tracking service
├── ui/
│   ├── screen/          # Compose UI screens
│   ├── theme/           # Material Design theme
│   └── viewmodel/       # ViewModels for each screen
├── util/                # Utility classes (permissions, preferences)
├── GPSTrackingApplication.kt
└── MainActivity.kt
```

## Dependencies

### Core Dependencies
- **AndroidX Core KTX**: 1.12.0
- **Jetpack Compose**: 2023.10.01
- **Navigation Compose**: 2.7.5
- **Lifecycle ViewModel**: 2.7.0

### Database & Storage
- **Room**: 2.6.1 (local database)
- **DataStore**: 1.0.0 (preferences)
- **Work Manager**: 2.9.0 (background tasks)

### Location & Maps
- **Google Play Services Maps**: 18.2.0
- **Google Play Services Location**: 21.0.1

### Dependency Injection
- **Hilt**: 2.48 (dependency injection)
- **Hilt Navigation Compose**: 1.1.0

### Asynchronous Programming
- **Kotlin Coroutines**: 1.7.3
- **Coroutines Play Services**: 1.7.3

## Setup Instructions

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK 24+ (API level 24)
- Google Maps API key

### 1. Clone the Repository
```bash
git clone <repository-url>
cd GPSTrackingApp
```

### 2. Configure Google Maps API
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing one
3. Enable Maps SDK for Android
4. Create API credentials
5. Add your API key to `local.properties`:
   ```properties
   MAPS_API_KEY=your_google_maps_api_key_here
   ```

### 3. Build and Run
1. Open the project in Android Studio
2. Sync Gradle files
3. Build the project
4. Run on device or emulator

### 4. Grant Permissions
The app requires the following permissions:
- **Location Access**: For GPS tracking
- **Background Location**: For continuous tracking when app is in background
- **Internet**: For Google Maps functionality

## Usage

### Starting a Trip
1. Navigate to the **Tracking** screen
2. Grant location permissions when prompted
3. Tap **Start Tracking** to begin GPS recording
4. View real-time metrics and location on the map

### Pausing/Resuming
- Tap **Pause** to temporarily stop location updates
- Tap **Resume** to continue tracking
- Trip data is preserved during pauses

### Stopping a Trip
1. Tap **Stop Tracking** to end the current trip
2. Trip data is automatically saved to the database
3. View completed trips in the **History** screen

### Viewing Trip History
1. Navigate to the **History** screen
2. View list of completed trips
3. Tap on a trip to see detailed information
4. Delete unwanted trips using the delete button

### Configuring Settings
1. Navigate to the **Settings** screen
2. Adjust location update frequency
3. Enable/disable background tracking
4. Configure auto-stop behavior
5. Reset to default values if needed

## Data Models

### Trip Entity
```kotlin
data class Trip(
    val id: Long = 0,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime? = null,
    val duration: Long = 0,
    val totalDistance: Double = 0.0,
    val averageSpeed: Double = 0.0,
    val maxSpeed: Double = 0.0,
    val isCompleted: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now()
)
```

### Location Point Entity
```kotlin
data class LocationPoint(
    val id: Long = 0,
    val tripId: Long,
    val latitude: Double,
    val longitude: Double,
    val altitude: Double? = null,
    val speed: Float,
    val accuracy: Float? = null,
    val bearing: Float? = null,
    val timestamp: LocalDateTime,
    val isMoving: Boolean = true
)
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- **Google Maps Platform** for mapping services
- **Jetpack Compose** team for modern UI toolkit
- **Android Architecture Components** for robust app architecture
- **Material Design** for design guidelines and components

## Support

For questions or issues:
1. Check existing issues in the repository
2. Create a new issue with detailed description
3. Include device information and steps to reproduce

---

**Note**: This app is designed for educational and development purposes. Ensure compliance with local privacy laws and regulations when collecting location data.
