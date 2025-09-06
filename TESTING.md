# GPS Tracking App - Testing Guide

## Overview
This document describes the comprehensive testing setup for the GPS Tracking App, including unit tests, integration tests, and UI tests.

## Test Structure

### Unit Tests (`app/src/test/`)
Located in `app/src/test/java/com/example/gpstrackingapp/`

#### Data Model Tests
- **`TripTest.kt`** - Tests for Trip data class
  - Duration formatting (hours, minutes, seconds)
  - Distance formatting (kilometers, meters)
  - Speed formatting (km/h)
  - Default value initialization

- **`LocationPointTest.kt`** - Tests for LocationPoint data class
  - Speed formatting
  - Coordinate formatting
  - Default value initialization
  - Zero speed handling

- **`TrackingMetricsTest.kt`** - Tests for TrackingMetrics data class
  - Default value initialization
  - Copy functionality
  - Edge case handling

#### Utility Tests
- **`ConvertersTest.kt`** - Tests for Room database type converters
  - Date to timestamp conversion
  - Timestamp to date parsing
  - Null handling
  - Round-trip conversion accuracy

- **`DataExporterTest.kt`** - Tests for data export functionality
  - CSV export format
  - JSON export format
  - Empty data handling
  - Null value handling

#### Repository Tests
- **`TripRepositoryTest.kt`** - Tests for TripRepository
  - CRUD operations
  - Flow data handling
  - Mock verification
  - Error handling

#### Location Manager Tests
- **`LocationManagerTest.kt`** - Tests for LocationManager utility functions
  - Distance calculations
  - Bearing calculations
  - Movement detection
  - Metrics updates

### Integration Tests (`app/src/androidTest/`)
Located in `app/src/androidTest/java/com/example/gpstrackingapp/`

#### Database Tests
- **`DatabaseTest.kt`** - Room database integration tests
  - Trip CRUD operations
  - Location point CRUD operations
  - Database relationships
  - Transaction handling

#### UI Tests
- **`TrackingScreenTest.kt`** - Compose UI tests
  - Screen state display
  - Button interactions
  - Metrics display
  - Navigation elements

## Test Dependencies

### Unit Test Dependencies
```kotlin
testImplementation("junit:junit:4.13.2")
testImplementation("org.mockito:mockito-core:5.1.1")
testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")
testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
testImplementation("androidx.arch.core:core-testing:2.2.0")
testImplementation("androidx.test:core:1.5.0")
testImplementation("androidx.test:rules:1.5.0")
testImplementation("androidx.test:runner:1.5.2")
```

### Android Test Dependencies
```kotlin
androidTestImplementation("androidx.test.ext:junit:1.1.5")
androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
androidTestImplementation("androidx.room:room-testing:2.6.1")
androidTestImplementation("androidx.arch.core:core-testing:2.2.0")
androidTestImplementation("com.google.dagger:hilt-android-testing:2.48")
androidTestImplementation("androidx.compose.ui:ui-test-junit4")
androidTestImplementation("androidx.navigation:navigation-testing:2.7.6")
```

## Running Tests

### Run All Tests
```bash
.\gradlew.bat test
```

### Run Unit Tests Only
```bash
.\gradlew.bat testDebugUnitTest
```

### Run Android Tests Only
```bash
.\gradlew.bat connectedAndroidTest
```

### Run Tests with Coverage
```bash
.\gradlew.bat testDebugUnitTest jacocoTestReport
```

### Using the Test Runner Script
```bash
.\run_tests.bat
```

## Test Configuration

### Test Application
- **`TestApplication.kt`** - Hilt-enabled test application for dependency injection

### Test Manifest
- **`AndroidManifest.xml`** - Test-specific manifest with test API keys

### Test Properties
- **`test.properties`** - Test configuration properties

## Test Coverage

The test suite covers:
- ✅ Data models and their utility functions
- ✅ Database operations and type converters
- ✅ Repository pattern implementation
- ✅ Location calculations and metrics
- ✅ Data export functionality
- ✅ UI components and interactions
- ✅ Database integration

## Test Best Practices

### Unit Tests
- Use mocking for external dependencies
- Test edge cases and error conditions
- Verify method calls and return values
- Use descriptive test names

### Integration Tests
- Use in-memory database for testing
- Test real data flow
- Verify database constraints
- Test transaction handling

### UI Tests
- Test user interactions
- Verify screen states
- Test navigation flows
- Use Compose testing utilities

## Continuous Integration

Tests are designed to run in CI/CD pipelines:
- No external dependencies
- Deterministic test data
- Proper cleanup after tests
- Clear test reporting

## Troubleshooting

### Common Issues
1. **Mock Context Issues** - Ensure proper mocking setup
2. **Database Tests** - Use in-memory database
3. **UI Tests** - Set up proper Compose test rules
4. **Coroutine Tests** - Use `runBlocking` for suspend functions

### Test Reports
Test reports are generated in:
- `app/build/reports/tests/testDebugUnitTest/index.html`
- `app/build/reports/tests/connectedAndroidTest/index.html`

## Future Enhancements

- Add performance tests
- Add accessibility tests
- Add end-to-end tests
- Add visual regression tests
- Add stress tests for location tracking
