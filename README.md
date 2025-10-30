![KMP Maps by Software Mansion](https://github.com/software-mansion/kmp-maps/blob/main/docs/images/cover_image.png?raw=true)

[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.20-blue.svg)](https://kotlinlang.org)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](./LICENSE)
![Maven Central](https://img.shields.io/maven-central/v/com.swmansion.kmpmaps/core)

`kmp-maps` provides a unified map component for Compose Multiplatform applications, offering seamless integration with native map APIs on both Android and iOS platforms.

## 🎯 See It in Action

Check out the sample project in the `/sample` directory for complete usage examples.

|                                                                       iOS (Apple Maps)                                                                       |                                                                      Android (Google Maps)                                                                       |
| :----------------------------------------------------------------------------------------------------------------------------------------------------------: | :--------------------------------------------------------------------------------------------------------------------------------------------------------------: |
| <img src="https://github.com/software-mansion/kmp-maps/blob/main/docs/images/ios_preview.png?raw=true" height="600" style="display: block; margin: 0 auto;"> | <img src="https://github.com/software-mansion/kmp-maps/blob/main/docs/images/android_preview.png?raw=true" height="600" style="display: block; margin: 0 auto;"> |
|                                                _Map view with circles, polygon, polylines and markers on iOS_                                                |                                                _Map view with circles, polygon, polylines and markers on Android_                                                |

## ✨ Features

-   **Cross-platform compatibility** - Single API for both Android and iOS
-   **Native performance** - Uses Google Maps SDK on Android and Apple Maps (MapKit) on iOS
-   **Compose Multiplatform** - Built with Compose Multiplatform for modern UI development
-   **Rich functionality** - Support for markers, circles, polygons, polylines, and custom styling
-   **Interactive callbacks** - Handle user interactions like clicks, camera movements, and gestures
-   **Location services** - Built-in location permission handling and user location display

## 🚀 Usage

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
