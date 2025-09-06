@echo off
echo Running GPS Tracking App Tests...
echo.

echo Running Unit Tests...
.\gradlew.bat test

echo.
echo Running Android Tests...
.\gradlew.bat connectedAndroidTest

echo.
echo Test execution completed!
pause
