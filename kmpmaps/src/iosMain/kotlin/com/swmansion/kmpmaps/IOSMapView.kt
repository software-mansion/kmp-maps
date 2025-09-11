package com.swmansion.kmpmaps

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.MapKit.*
import platform.UIKit.NSLayoutConstraint
import platform.UIKit.UIView

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun MapView(
    region: MapRegion?,
    mapType: MapType,
    annotations: List<MapAnnotation>,
    showUserLocation: Boolean,
    onRegionChange: (MapRegion) -> Unit,
    onAnnotationPress: (MapAnnotation) -> Unit,
    modifier: Modifier,
) {
    var mapView by remember { mutableStateOf<MKMapView?>(null) }

    UIKitView(
        factory = {
            val view = UIView()
            val mkMapView = MKMapView()
            mkMapView.translatesAutoresizingMaskIntoConstraints = false
            view.addSubview(mkMapView)

            val constraints =
                listOf(
                    mkMapView.topAnchor.constraintEqualToAnchor(view.topAnchor),
                    mkMapView.leadingAnchor.constraintEqualToAnchor(view.leadingAnchor),
                    mkMapView.trailingAnchor.constraintEqualToAnchor(view.trailingAnchor),
                    mkMapView.bottomAnchor.constraintEqualToAnchor(view.bottomAnchor),
                )
            NSLayoutConstraint.activateConstraints(constraints)

            mkMapView.showsUserLocation = showUserLocation

            when (mapType) {
                MapType.STANDARD -> mkMapView.mapType = MKMapTypeStandard
                MapType.SATELLITE -> mkMapView.mapType = MKMapTypeSatellite
                MapType.HYBRID -> mkMapView.mapType = MKMapTypeHybrid
            }

            region?.let { reg ->
                val coordinate = CLLocationCoordinate2DMake(reg.latitude, reg.longitude)
                val span = MKCoordinateSpanMake(reg.latitudeDelta, reg.longitudeDelta)
                val mapRegion = MKCoordinateRegionMake(coordinate, span)
                mkMapView.setRegion(mapRegion, animated = false)
            }

            annotations.forEach { annotation ->
                val pointAnnotation =
                    MKPointAnnotation().apply {
                        setCoordinate(
                            CLLocationCoordinate2DMake(annotation.latitude, annotation.longitude)
                        )
                        setTitle(annotation.title)
                        setSubtitle(annotation.subtitle)
                    }
                mkMapView.addAnnotation(pointAnnotation)
            }

            mapView = mkMapView
            view
        },
        modifier = modifier,
        update = { view ->
            mapView?.let { mkMapView ->
                when (mapType) {
                    MapType.STANDARD -> mkMapView.mapType = MKMapTypeStandard
                    MapType.SATELLITE -> mkMapView.mapType = MKMapTypeSatellite
                    MapType.HYBRID -> mkMapView.mapType = MKMapTypeHybrid
                }

                mkMapView.showsUserLocation = showUserLocation

                mkMapView.removeAnnotations(mkMapView.annotations)
                annotations.forEach { annotation ->
                    val pointAnnotation =
                        MKPointAnnotation().apply {
                            setCoordinate(
                                CLLocationCoordinate2DMake(
                                    annotation.latitude,
                                    annotation.longitude,
                                )
                            )
                            setTitle(annotation.title)
                            setSubtitle(annotation.subtitle)
                        }
                    mkMapView.addAnnotation(pointAnnotation)
                }
            }
        },
        properties =
            UIKitInteropProperties(isInteractive = true, isNativeAccessibilityEnabled = true),
    )
}
