# Module KMP Maps

## 🍎 iOS Setup - Apple Maps

No additional configuration is required for Apple Maps on iOS.

### 🔐 Permissions

To display the user's location on the map, you need to declare location permissions:

Add the following key to your `Info.plist`:
```xml
<key>NSLocationWhenInUseUsageDescription</key>
<string>Allow this app to use your location</string>
```
