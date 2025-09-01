# GPS Tracking & Data Collection App

A modern Android application built with **Jetpack Compose** and **MVVM architecture** for real-time GPS tracking and trip data collection. This project demonstrates my expertise in modern Android development practices and clean architecture principles.

## 🎯 Project Overview

This GPS tracking app was developed as a technical assessment showcasing:
- **Modern Android Architecture** with MVVM pattern
- **Jetpack Compose** for declarative UI development
- **Room Database** for efficient local data storage
- **Clean Code Principles** with proper separation of concerns
- **Background Services** for continuous location tracking
- **Material Design 3** implementation

## ✨ Key Features

### 🗺️ Real-time GPS Tracking
- **Live location updates** with configurable intervals (1s, 5s, 10s)
- **Google Maps integration** for visual tracking display
- **Background tracking** with foreground service
- **Auto-stop functionality** when no movement detected

### 📊 Comprehensive Trip Management
- **Trip recording** with start/pause/resume/stop controls
- **Real-time metrics**: speed, distance, elapsed time
- **Trip history** with detailed statistics and analytics
- **Data persistence** using Room database

### ⚙️ User Experience
- **Intuitive Material Design 3** interface
- **Permission handling** with user-friendly dialogs
- **Settings customization** for tracking preferences
- **Responsive UI** with smooth animations

## 🏗️ Architecture & Design Patterns

### MVVM Architecture
The app follows the **Model-View-ViewModel** pattern for clean separation of concerns:

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

## 🛠️ Technical Implementation

### Core Technologies
- **Jetpack Compose** (2023.10.01) - Modern declarative UI toolkit
- **Room Database** (2.6.1) - Local data persistence
- **Hilt** (2.48) - Dependency injection
- **Kotlin Coroutines & Flow** - Asynchronous programming
- **Material Design 3** - Modern design system

### Key Dependencies
```kotlin
// Core Android
implementation("androidx.core:core-ktx:1.12.0")
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

// Compose
implementation(platform("androidx.compose:compose-bom:2023.10.01"))
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.material3:material3")

// Navigation
implementation("androidx.navigation:navigation-compose:2.7.5")

// Database
implementation("androidx.room:room-runtime:2.6.1")
implementation("androidx.room:room-ktx:2.6.1")

// Dependency Injection
implementation("com.google.dagger:hilt-android:2.48")
implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

// Location Services
implementation("com.google.android.gms:play-services-maps:18.2.0")
implementation("com.google.android.gms:play-services-location:21.0.1")
```

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog or later
- Android SDK 24+ (API level 24)
- Google Maps API key

### Setup Instructions

1. **Clone the Repository**
   ```bash
   git clone https://github.com/ennoanandev/GPSTrackingApp-.git
   cd GPSTrackingApp-
   ```

2. **Configure Google Maps API**
   - Get API key from [Google Cloud Console](https://console.cloud.google.com/)
   - Enable Maps SDK for Android
   - Add to `local.properties`:
     ```properties
     MAPS_API_KEY=your_api_key_here
     ```

3. **Build and Run**
   - Open in Android Studio
   - Sync Gradle files
   - Build and run on device/emulator

### Required Permissions
- **Location Access** - For GPS tracking
- **Background Location** - For continuous tracking
- **Internet** - For Google Maps functionality

## 📱 App Usage

### Starting a Trip
1. Navigate to **Tracking** screen
2. Grant location permissions
3. Tap **Start Tracking**
4. View real-time metrics and map

### Trip Management
- **Pause/Resume** - Temporary stop/continue
- **Stop** - Complete trip and save data
- **History** - View completed trips and statistics

### Settings Configuration
- Location update frequency
- Background tracking toggle
- Auto-stop behavior
- Reset to defaults

## 🗄️ Data Models

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

## 🔧 Development Highlights

### Clean Architecture
- **Separation of concerns** with clear layer boundaries
- **Repository pattern** for data access abstraction
- **Dependency injection** with Hilt for testability
- **Single responsibility principle** in all components

### Performance Optimizations
- **Efficient database queries** with Room
- **Background processing** with WorkManager
- **Memory management** with proper lifecycle handling
- **Smooth UI updates** with Compose recomposition

### Code Quality
- **Kotlin best practices** and idioms
- **Comprehensive error handling**
- **User-friendly permission flows**
- **Accessibility considerations**

## 📊 Technical Assessment Details

**Time Spent**: [You can add your actual time here]
**Complexity Level**: Intermediate to Advanced
**Focus Areas**: Modern Android Architecture, UI/UX, Data Management

## 🤝 Contributing

This is a technical assessment project, but contributions are welcome:
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Submit a pull request

## 📄 License

This project is licensed under the MIT License.

## 🙏 Acknowledgments

- **Google Maps Platform** for mapping services
- **Jetpack Compose** team for modern UI toolkit
- **Android Architecture Components** for robust patterns
- **Material Design** for design guidelines

---

**Developer**: Ennoaman  
**Project**: GPS Tracking App Technical Assessment  
**Technologies**: Android, Kotlin, Jetpack Compose, Room, MVVM

*This project demonstrates my expertise in modern Android development and clean architecture principles.*
