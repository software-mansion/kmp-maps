## 📦 Installation

### ✅ Recommended: Using Gradle Version Catalogs

First, add the library to your `gradle/libs.versions.toml` file:

```toml
[versions]
kmpMaps = "0.3.0"

[libraries]
swmansion-kmpMaps = { module = "com.swmansion.kmpmaps:kmp-maps", version.ref = "kmpMaps" }
```

Then add it to your `build.gradle.kts`:

```kotlin
dependencies {
    implementation(libs.swmansion.kmpMaps)
}
```

### 🔧 Alternative: Direct Dependency Declaration

If you're not using Gradle version catalogs, you can add the library directly to your `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.swmansion.kmpmaps:kmp-maps:0.3.0")
}
```

## ☁️ Google Cloud API Setup

For using Google Maps you have to generate your API Key and setup Google Cloud API.
Visit our [dedicated document](https://github.com/software-mansion/kmp-maps/blob/main/docs/GOOGLE_CLOUD_API_SETUP.md) for more info.

## 🤖 Android Setup

To use Google Maps on Android, you need to configure your API key in `AndroidManifest.xml`:

```xml
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="YOUR_API_KEY" />
```

### 🔐 Permissions

To display the user's location on the map, you need to declare and request location permissions.
Add the following permissions to your `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

## 🍎 iOS Setup

### Apple Maps

No additional configuration is required for Apple Maps on iOS.

### Google Maps

For Google Maps setup, visit our [dedicated document](https://github.com/software-mansion/kmp-maps/blob/main/docs/GOOGLE_MAPS_IOS_SETUP.md)

### 🔐 Permissions

To display the user's location on the map, you need to declare location permissions:
Add the following key to your `Info.plist`:

```xml
<key>NSLocationWhenInUseUsageDescription</key>
<string>Allow this app to use your location</string>
```
