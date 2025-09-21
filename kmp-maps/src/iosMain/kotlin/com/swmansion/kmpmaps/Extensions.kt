package com.swmansion.kmpmaps

import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.MapKit.MKCoordinateRegion
import platform.MapKit.MKCoordinateRegionMake
import platform.MapKit.MKCoordinateSpanMake
import platform.MapKit.MKMapTypeHybrid
import platform.MapKit.MKMapTypeSatellite
import platform.MapKit.MKMapTypeStandard
import platform.MapKit.MKMapView
import platform.MapKit.MKPointAnnotation
import platform.UIKit.NSLayoutConstraint
import platform.UIKit.UIView

@OptIn(ExperimentalForeignApi::class)
internal fun MapRegion.toMKCoordinateRegion(): CValue<MKCoordinateRegion> {
    val coordinate = CLLocationCoordinate2DMake(coordinates.latitude, coordinates.longitude)
    val span =
        MKCoordinateSpanMake(
            calculateLatitudeDelta(zoom),
            calculateLongitudeDelta(zoom, coordinates.latitude),
        )
    return MKCoordinateRegionMake(coordinate, span)
}

@OptIn(ExperimentalForeignApi::class)
internal fun MapAnnotation.toMKPointAnnotation(): MKPointAnnotation {
    return MKPointAnnotation().apply {
        setCoordinate(CLLocationCoordinate2DMake(coordinates.latitude, coordinates.longitude))
        setTitle(title)
        setSubtitle(subtitle)
    }
}

internal fun MapType.toMKMapType(): platform.MapKit.MKMapType {
    return when (this) {
        MapType.STANDARD -> MKMapTypeStandard
        MapType.SATELLITE -> MKMapTypeSatellite
        MapType.HYBRID -> MKMapTypeHybrid
    }
}

internal fun MKMapView.setupMapConstraints(parentView: UIView) {
    val constraints =
        listOf(
            topAnchor.constraintEqualToAnchor(parentView.topAnchor),
            leadingAnchor.constraintEqualToAnchor(parentView.leadingAnchor),
            trailingAnchor.constraintEqualToAnchor(parentView.trailingAnchor),
            bottomAnchor.constraintEqualToAnchor(parentView.bottomAnchor),
        )
    NSLayoutConstraint.activateConstraints(constraints)
}

internal fun MKMapView.updateAnnotations(annotations: List<MapAnnotation>) {
    removeAnnotations(this.annotations)
    annotations.forEach { annotation -> addAnnotation(annotation.toMKPointAnnotation()) }
}
