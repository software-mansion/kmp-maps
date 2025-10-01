# Module KMP Maps

KMP Maps provides a unified map component for Compose Multiplatform applications, offering seamless integration with native map APIs on both Android and iOS platforms.


## 🎯 Usage

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
            Marker(
                coordinates = Coordinates(latitude = 50.0486, longitude = 19.9654),
                title = "Software Mansion",
                androidSnippet = "Software house"
            )
        ),
        onMarkerClick = { marker ->
            println("Marker clicked: ${marker.title}")
        },
        onMapClick = { coordinates ->
            println("Map clicked at: ${coordinates.latitude}, ${coordinates.longitude}")
        }
    )
}
```

## 📦 Installation

### ✅ Recommended: Using Gradle Version Catalogs

First, add the library to your `gradle/libs.versions.toml` file:

```toml
[versions]
kmpMaps = "0.1.0"

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
    implementation("com.swmansion.kmpmaps:kmp-maps:0.1.0")
}
```

