## 🗺️ Google Maps iOS Setup Guide

### ⚙️ Step 1 — Link Google Maps SDK

`google-maps` declares Google Maps SDK for iOS (11.2.0) and Google Maps iOS Utils (7.1.2) with
Kotlin's [SwiftPM import](https://kotl.in/cocoapods-to-swiftpm-migration). Your module gets both
packages transitively, so you don't add them yourself. This requires:

- Kotlin 2.4.0 or later
- iOS deployment target 16.0 or later
- No `kotlin("native.cocoapods")` plugin in the module that builds your iOS framework. The Kotlin
  Gradle plugin can't combine CocoaPods with SwiftPM dependencies, including transitive ones. If you
  still use it, migrate first; see the
  [migration guide](https://kotl.in/cocoapods-to-swiftpm-migration).

**🔗 Integrate the Xcode project (one time)**

Run the integration tasks for the module that builds your iOS framework (e.g. `:shared`):

```bash
XCODEPROJ_PATH="$PWD/iosApp/iosApp.xcodeproj" GRADLE_PROJECT_PATH=":shared" \
  ./gradlew :shared:integrateEmbedAndSign :shared:integrateLinkagePackage
```

This adds a build phase that runs `embedAndSignAppleFrameworkForXcode` and a local
`KotlinMultiplatformLinkedPackage` that links the Swift packages into your app. If you skip it,
building in Xcode fails with an error that prints this command for your project. Also set
_User Script Sandboxing_ (`ENABLE_USER_SCRIPT_SANDBOXING`) to `NO` for your app target.

### 🔑 Step 2 — Configure your API Key

### Version 0.8.0 and above (Recommended)

To use Google Maps on iOS, you need to initialize a valid API key globally before rendering the map:

```kotlin
MapConfiguration.initialize(googleMapsApiKey = "YOUR_API_KEY")
```

### Legacy Versions (Below 0.8.0)
Add your API key to `Info.plist` of the iOS app target:

```xml
<key>GoogleMapsAPIKey</key>
<string>YOUR_API_KEY_HERE</string>
```
