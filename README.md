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

### 📤 Data Export & Analysis
- **CSV Export**: Export trip data in Excel-compatible format
- **JSON Export**: Structured data format for API integration
- **Bulk Export**: Export all trips or individual trip data
- **Comprehensive Data**: Includes location points, metrics, and statistics

### ⚙️ User Experience
- **Intuitive Material Design 3** interface with custom blue color scheme
- **Permission handling** with user-friendly dialogs
- **Settings customization** for tracking preferences
- **Responsive UI** with smooth animations
- **Custom App Icon** with location pin design

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

## 🆕 Recent Updates

### Version 3.0 - Comprehensive Testing & Latest SDK
- ✅ **Comprehensive Test Suite**: 41 unit and integration tests covering all components
- ✅ **Latest Android SDK**: Updated to API level 35 with latest dependencies
- ✅ **Test Coverage**: Data models, utilities, repositories, database, and UI components
- ✅ **API Compatibility**: Fixed java.time to java.util.Date for API 24+ compatibility
- ✅ **Modern Dependencies**: Updated to latest versions of Compose, Room, Hilt, and more
- ✅ **Test Documentation**: Complete testing guide with examples and best practices
- ✅ **Android 13+ Support**: Added POST_NOTIFICATIONS permission for latest Android versions

### Version 2.0 - Enhanced Features
- ✅ **Data Export System**: Complete CSV/JSON export functionality
- ✅ **Custom Color Scheme**: Professional blue theme (#307ec6, #41c7fa)
- ✅ **Custom App Icon**: Location pin design with gradient background
- ✅ **Permission Optimization**: Improved location permission handling
- ✅ **UI Enhancements**: Better user experience and visual consistency
- ✅ **Code Quality**: Fixed compilation errors and improved architecture

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog or later
- Android SDK 35 (API level 35) - Latest version
- Google Maps API key
- Java 17+ for development

### Setup Instructions

1. **Clone the Repository**
   ```bash
   git clone https://github.com/ennoanandev/GPSTrackingApp-.git
   cd GPSTrackingApp-
   ```

2. **Configure Google Maps API**
   - Get API key from [Google Cloud Console](https://console.cloud.google.com/)
   - Enable Maps SDK for Android
   - Add to `secrets.properties`:
     ```properties
     MAPS_API_KEY=your_api_key_here
     ```

3. **Build and Run**
   - Open in Android Studio
   - Sync Gradle files
   - Build and run on device/emulator

4. **Run Tests** (Optional)
   ```bash
   # Run all tests
   .\gradlew.bat test
   
   # Run unit tests only
   .\gradlew.bat testDebugUnitTest
   
   # Use test runner script
   .\run_tests.bat
   ```

### Required Permissions
- **Location Access** - For GPS tracking
- **Background Location** - For continuous tracking
- **Internet** - For Google Maps functionality
- **Notifications** - For Android 13+ compatibility

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

## 🧪 Testing

### Test Suite Overview
The project includes a comprehensive test suite with **41 tests** covering all major components:

#### Unit Tests (`app/src/test/`)
- **Data Models**: Trip, LocationPoint, TrackingMetrics formatting and validation
- **Utilities**: Converters, DataExporter functionality
- **Repositories**: TripRepository with proper mocking
- **Location Manager**: Distance calculations, bearing, movement detection

#### Integration Tests (`app/src/androidTest/`)
- **Database**: Room database CRUD operations and relationships
- **UI**: Compose screen testing and user interactions
- **Services**: Background service testing

### Test Dependencies
- **JUnit 4**: Core testing framework
- **Mockito**: Mocking framework for dependencies
- **Room Testing**: In-memory database testing
- **Compose Testing**: UI component testing
- **Coroutines Testing**: Async code testing

### Running Tests
```bash
# Run all tests
.\gradlew.bat test

# Run unit tests only
.\gradlew.bat testDebugUnitTest

# Run Android tests (requires device/emulator)
.\gradlew.bat connectedAndroidTest

# Generate test coverage report
.\gradlew.bat jacocoTestReport
```

### Test Documentation
See `TESTING.md` for detailed testing guide, best practices, and troubleshooting.

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
- **Comprehensive test coverage** with 41 tests
- **Modern Android SDK** (API 35) compatibility


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
**Technologies**: Android, Kotlin, Jetpack Compose, Room, MVVM, Testing  
**SDK Version**: API 35 (Latest)  
**Test Coverage**: 41 comprehensive tests

*This project demonstrates my expertise in modern Android development, clean architecture principles, and comprehensive testing practices.*
