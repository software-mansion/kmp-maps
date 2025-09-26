# Module kmp-maps

KMP Maps is a cross-platform map component for Kotlin Multiplatform Mobile that provides a unified interface for map functionality across Android and iOS platforms.

## Platform Support

- **Android**: Uses Google Maps SDK
- **iOS**: Uses Apple Maps (MapKit)

## Key Components

The library provides several main components:

- `Map` - The main composable component that renders maps
- `MapProperties` - Configuration for map behavior and appearance  
- `MapUISettings` - Settings for interactive elements and gestures
- `MapMarker` - Markers to display on the map
- `MapCircle` - Circular shapes on the map
- `MapPolygon` - Polygon shapes on the map
- `MapPolyline` - Line shapes connecting multiple points

## Basic Usage

```kotlin
@Composable
fun MyMapScreen() {
    Map(
        modifier = Modifier.fillMaxSize(),
        properties = MapProperties(
            isMyLocationEnabled = true,
            mapType = MapType.NORMAL,
        ),
        uiSettings = MapUISettings(
            myLocationButtonEnabled = true,
            compassEnabled = true
        ),
        cameraPosition = CameraPosition(
            coordinates = Coordinates(latitude = 50.0619, longitude = 19.9373),
            zoom = 13f
        ),
        markers = listOf(
            MapMarker(
                coordinates = Coordinates(latitude = 50.0486, longitude = 19.9654),
                title = "Software Mansion",
                androidSnippet = "Software house"
            )
        )
    )
}
```

## Configuration

### Android - Google Maps API Key

To use Google Maps on Android, you need to configure your API key in `AndroidManifest.xml`:

```xml
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="${MAPS_API_KEY}" />
```

### iOS - Apple Maps

No additional configuration is required for Apple Maps on iOS.

## Permissions

### Android
```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

### iOS
```xml
<key>NSLocationWhenInUseUsageDescription</key>
<string>Allow this app to use your location</string>
```