// swift-tools-version: 5.9
import PackageDescription
let package = Package(
  name: "_kmp_maps_google_maps",
  platforms: [
    .iOS("16.0")
  ],
  products: [
    .library(
      name: "_kmp_maps_google_maps",
      type: .none,
      targets: ["_kmp_maps_google_maps"]
    )
  ],
  dependencies: [
    .package(
      url: "https://github.com/googlemaps/ios-maps-sdk.git",
      exact: "11.2.0"
    ),
    .package(
      url: "https://github.com/googlemaps/google-maps-ios-utils.git",
      from: "7.1.2"
    )
  ],
  targets: [
    .target(
      name: "_kmp_maps_google_maps",
      dependencies: [
        .product(
          name: "GoogleMaps",
          package: "ios-maps-sdk"
        ),
        .product(
          name: "GoogleMapsUtils",
          package: "google-maps-ios-utils"
        )
      ]
    )
  ]
)
