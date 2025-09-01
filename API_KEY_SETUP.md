# Google Maps API Key Setup Guide

## Step 1: Get Your Google Maps API Key

1. Go to the [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select an existing one
3. Enable the following APIs:
   - **Maps SDK for Android**
   - **Places API** (optional, for place search features)
4. Go to "Credentials" → "Create Credentials" → "API Key"
5. Copy the generated API key

## Step 2: Configure Your API Key

1. Open the `secrets.properties` file in your project root
2. Replace `your_actual_google_maps_api_key_here` with your actual API key:

```properties
# Google Maps API Key
# Replace this with your actual Google Maps API key
MAPS_API_KEY=AIzaSyYourActualApiKeyHere
```

## Step 3: Secure Your API Key (Important!)

1. **Never commit your API key to version control**
2. Add `secrets.properties` to your `.gitignore` file if it's not already there
3. Set up API key restrictions in Google Cloud Console:
   - Go to "Credentials" → Select your API key
   - Under "Application restrictions", select "Android apps"
   - Add your app's package name and SHA-1 fingerprint
   - Under "API restrictions", select "Restrict key" and choose only the APIs you need

## Step 4: Build and Test

1. Clean and rebuild your project:
   ```bash
   ./gradlew clean build
   ```
2. Run your app - the maps should now work properly

## Troubleshooting

- **Maps not loading**: Check that your API key is correct and has the right permissions
- **Build errors**: Make sure `secrets.properties` exists and has the correct format
- **API key restrictions**: If you set up restrictions, make sure your app's package name and SHA-1 fingerprint are correct

## SHA-1 Fingerprint

To get your app's SHA-1 fingerprint for API key restrictions:

```bash
# For debug builds
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android

# For release builds (if you have a release keystore)
keytool -list -v -keystore your-release-keystore.jks -alias your-alias
```

## Package Name

Your app's package name is: `com.example.gpstrackingapp`
