package com.swmansion.kmpmaps

import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.MapKit.MKCircle
import platform.MapKit.MKCoordinateRegion
import platform.MapKit.MKCoordinateRegionMake
import platform.MapKit.MKCoordinateSpanMake
import platform.MapKit.MKMapType
import platform.MapKit.MKMapTypeHybrid
import platform.MapKit.MKMapTypeSatellite
import platform.MapKit.MKMapTypeStandard
import platform.MapKit.MKMapView
import platform.MapKit.MKPointAnnotation
import platform.MapKit.addOverlay
import platform.UIKit.NSLayoutConstraint
import platform.UIKit.UIView


@OptIn(ExperimentalForeignApi::class)
public fun CameraPosition.toMKCoordinateRegion(): CValue<MKCoordinateRegion> {
    val coordinate = CLLocationCoordinate2DMake(coordinates.latitude, coordinates.longitude)
    val span =
        MKCoordinateSpanMake(
            calculateLatitudeDelta(zoom),
            calculateLongitudeDelta(zoom, coordinates.latitude),
        )
    return MKCoordinateRegionMake(coordinate, span)
}

@OptIn(ExperimentalForeignApi::class)
public fun AppleMapsAnnotations.toMKPointAnnotation(): MKPointAnnotation {
    return MKPointAnnotation().apply {
        setCoordinate(CLLocationCoordinate2DMake(coordinates.latitude, coordinates.longitude))
        setTitle(title)
        setSubtitle(subtitle)
    }
}

public fun AppleMapsMapType.toMKMapType(): MKMapType {
    return when (this) {
        AppleMapsMapType.STANDARD -> MKMapTypeStandard
        AppleMapsMapType.SATELLITE -> MKMapTypeSatellite
        AppleMapsMapType.HYBRID -> MKMapTypeHybrid
    }
}

public fun MKMapView.setupMapConstraints(parentView: UIView) {
    val constraints =
        listOf(
            topAnchor.constraintEqualToAnchor(parentView.topAnchor),
            leadingAnchor.constraintEqualToAnchor(parentView.leadingAnchor),
            trailingAnchor.constraintEqualToAnchor(parentView.trailingAnchor),
            bottomAnchor.constraintEqualToAnchor(parentView.bottomAnchor),
        )
    NSLayoutConstraint.activateConstraints(constraints)
}

@OptIn(ExperimentalForeignApi::class)
public fun MKMapView.updateAppleMapsAnnotations(annotations: List<AppleMapsAnnotations>) {
    removeAnnotations(this.annotations)
    annotations.forEach { annotation ->
        val mkAnnotation = MKPointAnnotation().apply {
            annotation.coordinates.let { coords ->
                setCoordinate(CLLocationCoordinate2DMake(coords.latitude, coords.longitude))
            }
            setTitle(annotation.title)
        }
        addAnnotation(mkAnnotation)
    }
}

@OptIn(ExperimentalForeignApi::class)
public fun MKMapView.updateAppleMapsMarkers(markers: List<AppleMapsMarker>) {
    removeAnnotations(this.annotations)
    markers.forEach { marker ->
        val mkAnnotation = MKPointAnnotation().apply {
            marker.coordinates.let { coords ->
                setCoordinate(CLLocationCoordinate2DMake(coords.latitude, coords.longitude))
            }
            setTitle(marker.title)
        }
        addAnnotation(mkAnnotation)
    }
}

@OptIn(ExperimentalForeignApi::class)
public fun MKMapView.updateAppleMapsCircles(circles: List<AppleMapsCircle>) {
    circles.forEach { circle ->
        val coordinate = CLLocationCoordinate2DMake(circle.center.latitude, circle.center.longitude)
        val mkCircle = MKCircle.circleWithCenterCoordinate(coordinate, radius = circle.radius)
        addOverlay(mkCircle)
    }
}
