![KMP Maps by Software Mansion](https://github.com/software-mansion/kmp-maps/blob/main/docs/images/cover_image.png?raw=true)

[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.20-blue.svg)](https://kotlinlang.org)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](./LICENSE)
![Maven Central](https://img.shields.io/maven-central/v/com.swmansion.kmpmaps/core)

`kmp-maps` provides a unified map component for Compose Multiplatform applications, offering seamless integration with native map APIs on both Android and iOS platforms.

## 🎯 See It in Action

Check out the sample project in the `/sample` directory for complete usage examples.

|                                                                  iOS (Apple Maps)                                                                   |                                                                  Android (Google Maps)                                                                  |                                                                  Desktop (Google Maps)                                                                  |
| :-------------------------------------------------------------------------------------------------------------------------------------------------: | :-----------------------------------------------------------------------------------------------------------------------------------------------------: |:-------------------------------------------------------------------------------------------------------------------------------------------------------:|
| <img src="https://github.com/software-mansion/kmp-maps/blob/main/docs/images/ios_preview.png" height="600" style="display: block; margin: 0 auto;"> | <img src="https://github.com/software-mansion/kmp-maps/blob/main/docs/images/android_preview.png" height="600" style="display: block; margin: 0 auto;"> | <img src="https://github.com/software-mansion/kmp-maps/blob/main/docs/images/desktop_preview.png" height="600" style="display: block; margin: 0 auto;"> |
|                                           _Map view with circles, polygon, polylines and markers on iOS_                                            |                                           _Map view with circles, polygon, polylines and markers on Android_                                            |                                           _Map view with circles, polygon, polylines and markers on desktop_                                            |

## ✨ Features

- **Multi-Provider Support:** Android (Google Maps), iOS (Apple Maps or Google Maps), and JVM/desktop (Google Maps JS).
- **Fully Customizable Markers:** Create markers using standard Compose Multiplatform code.
- **GeoJSON Support:** Easily render GeoJSON layers with customizable styling capabilities.
- **Native Rendering:** Powered by underlying native SDKs, ensuring smooth performance, correct gesture handling, and native look and feel.
- **Rich Geometry Support:** Draw interactive circles, polygons, and polylines with full control over colors, strokes, and fills.
- **Location Services:** Built-in location permission handling and user location display.
- **Interactive Callbacks:** Comprehensive event handling for map clicks, camera movements, POI interactions, and marker events.
- **Compose Multiplatform:** Built with Compose Multiplatform for modern UI development.

## 🚀 Usage

### Import

Choose the appropriate import based on your needs:

- **Native Implementation:** Uses native Google Maps on Android, native Apple Maps on iOS, and Google Maps JS API (via WebView) on desktop.

  ```kotlin
  import com.swmansion.kmpmaps.core.Map
  ```

- **Universal Google Maps:** Uses Google Maps SDK on Android and iOS, and Google Maps JS API on desktop.

  ```kotlin
  import com.swmansion.kmpmaps.googlemaps.Map
  ```

### Example

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

For installation instructions, platform setup, and configuration details, visit our [dedicated document](https://github.com/software-mansion/kmp-maps/blob/main/docs/INSTALLATION_SETUP.md).

## 📚 API Reference

Check out our [dedicated documentation page](https://docs.swmansion.com/kmp-maps/) for API reference.

## 🤝 Contributing

We welcome contributions! Please feel free to submit a Pull Request.

## KMP Maps is created by Software Mansion

[![swm](https://logo.swmansion.com/logo?color=white&variant=desktop&width=150&tag=kmp-maps-github "Software Mansion")](https://swmansion.com)

Since 2012 [Software Mansion](https://swmansion.com) is a software agency with
experience in building web and mobile apps. We are Core React Native
Contributors and experts in dealing with all kinds of React Native issues. We
can help you build your next dream product –
[Hire us](https://swmansion.com/contact/projects?utm_source=typegpu&utm_medium=readme).

Made by [@software-mansion](https://github.com/software-mansion) and
[community](https://github.com/software-mansion/kmp-maps/graphs/contributors) 💛
<br><br>
<a href="https://github.com/software-mansion/kmp-maps/graphs/contributors">
<img src="https://contrib.rocks/image?repo=software-mansion/kmp-maps" />
</a>
