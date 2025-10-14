package com.swmansion.kmpmaps

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import cocoapods.GoogleMaps.GMSCircle
import cocoapods.GoogleMaps.GMSMapStyle
import cocoapods.GoogleMaps.GMSMapView
import cocoapods.GoogleMaps.GMSMarker
import cocoapods.GoogleMaps.GMSMutablePath
import cocoapods.GoogleMaps.GMSPolygon
import cocoapods.GoogleMaps.GMSPolyline
import cocoapods.GoogleMaps.kGMSTypeHybrid
import cocoapods.GoogleMaps.kGMSTypeNormal
import cocoapods.GoogleMaps.kGMSTypeSatellite
import cocoapods.GoogleMaps.kGMSTypeTerrain
import kotlin.collections.set
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.interpretCPointer
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.sizeOf
import kotlinx.cinterop.useContents
import platform.CoreLocation.CLLocationCoordinate2D
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.MapKit.MKCircle
import platform.MapKit.MKCoordinateRegion
import platform.MapKit.MKCoordinateRegionMake
import platform.MapKit.MKCoordinateSpanMake
import platform.MapKit.MKMapTypeHybrid
import platform.MapKit.MKMapTypeSatellite
import platform.MapKit.MKMapTypeStandard
import platform.MapKit.MKMapView
import platform.MapKit.MKPointAnnotation
import platform.MapKit.MKPointOfInterestCategory
import platform.MapKit.MKPointOfInterestCategoryATM
import platform.MapKit.MKPointOfInterestCategoryAirport
import platform.MapKit.MKPointOfInterestCategoryAmusementPark
import platform.MapKit.MKPointOfInterestCategoryAnimalService
import platform.MapKit.MKPointOfInterestCategoryAquarium
import platform.MapKit.MKPointOfInterestCategoryAutomotiveRepair
import platform.MapKit.MKPointOfInterestCategoryBakery
import platform.MapKit.MKPointOfInterestCategoryBank
import platform.MapKit.MKPointOfInterestCategoryBaseball
import platform.MapKit.MKPointOfInterestCategoryBasketball
import platform.MapKit.MKPointOfInterestCategoryBeach
import platform.MapKit.MKPointOfInterestCategoryBeauty
import platform.MapKit.MKPointOfInterestCategoryBowling
import platform.MapKit.MKPointOfInterestCategoryBrewery
import platform.MapKit.MKPointOfInterestCategoryCafe
import platform.MapKit.MKPointOfInterestCategoryCampground
import platform.MapKit.MKPointOfInterestCategoryCarRental
import platform.MapKit.MKPointOfInterestCategoryCastle
import platform.MapKit.MKPointOfInterestCategoryConventionCenter
import platform.MapKit.MKPointOfInterestCategoryDistillery
import platform.MapKit.MKPointOfInterestCategoryEVCharger
import platform.MapKit.MKPointOfInterestCategoryFairground
import platform.MapKit.MKPointOfInterestCategoryFireStation
import platform.MapKit.MKPointOfInterestCategoryFishing
import platform.MapKit.MKPointOfInterestCategoryFitnessCenter
import platform.MapKit.MKPointOfInterestCategoryFoodMarket
import platform.MapKit.MKPointOfInterestCategoryFortress
import platform.MapKit.MKPointOfInterestCategoryGasStation
import platform.MapKit.MKPointOfInterestCategoryGoKart
import platform.MapKit.MKPointOfInterestCategoryGolf
import platform.MapKit.MKPointOfInterestCategoryHiking
import platform.MapKit.MKPointOfInterestCategoryHospital
import platform.MapKit.MKPointOfInterestCategoryHotel
import platform.MapKit.MKPointOfInterestCategoryKayaking
import platform.MapKit.MKPointOfInterestCategoryLandmark
import platform.MapKit.MKPointOfInterestCategoryLaundry
import platform.MapKit.MKPointOfInterestCategoryLibrary
import platform.MapKit.MKPointOfInterestCategoryMailbox
import platform.MapKit.MKPointOfInterestCategoryMarina
import platform.MapKit.MKPointOfInterestCategoryMiniGolf
import platform.MapKit.MKPointOfInterestCategoryMovieTheater
import platform.MapKit.MKPointOfInterestCategoryMuseum
import platform.MapKit.MKPointOfInterestCategoryMusicVenue
import platform.MapKit.MKPointOfInterestCategoryNationalMonument
import platform.MapKit.MKPointOfInterestCategoryNationalPark
import platform.MapKit.MKPointOfInterestCategoryNightlife
import platform.MapKit.MKPointOfInterestCategoryPark
import platform.MapKit.MKPointOfInterestCategoryParking
import platform.MapKit.MKPointOfInterestCategoryPharmacy
import platform.MapKit.MKPointOfInterestCategoryPlanetarium
import platform.MapKit.MKPointOfInterestCategoryPolice
import platform.MapKit.MKPointOfInterestCategoryPostOffice
import platform.MapKit.MKPointOfInterestCategoryPublicTransport
import platform.MapKit.MKPointOfInterestCategoryRVPark
import platform.MapKit.MKPointOfInterestCategoryRestaurant
import platform.MapKit.MKPointOfInterestCategoryRestroom
import platform.MapKit.MKPointOfInterestCategoryRockClimbing
import platform.MapKit.MKPointOfInterestCategorySchool
import platform.MapKit.MKPointOfInterestCategorySkatePark
import platform.MapKit.MKPointOfInterestCategorySkating
import platform.MapKit.MKPointOfInterestCategorySkiing
import platform.MapKit.MKPointOfInterestCategorySoccer
import platform.MapKit.MKPointOfInterestCategorySpa
import platform.MapKit.MKPointOfInterestCategoryStadium
import platform.MapKit.MKPointOfInterestCategoryStore
import platform.MapKit.MKPointOfInterestCategorySurfing
import platform.MapKit.MKPointOfInterestCategorySwimming
import platform.MapKit.MKPointOfInterestCategoryTennis
import platform.MapKit.MKPointOfInterestCategoryTheater
import platform.MapKit.MKPointOfInterestCategoryUniversity
import platform.MapKit.MKPointOfInterestCategoryVolleyball
import platform.MapKit.MKPointOfInterestCategoryWinery
import platform.MapKit.MKPointOfInterestCategoryZoo
import platform.MapKit.MKPointOfInterestFilter
import platform.MapKit.MKPolygon
import platform.MapKit.MKPolyline
import platform.MapKit.addOverlay
import platform.MapKit.removeOverlays
import platform.UIKit.UIColor
import platform.UIKit.UIUserInterfaceStyle
import platform.posix.memcpy

/**
 * Converts a CameraPosition to Apple MapKit's MKCoordinateRegion.
 *
 * @return MKCoordinateRegion representing the camera's view area
 */
@OptIn(ExperimentalForeignApi::class)
internal fun CameraPosition.toMKCoordinateRegion(): CValue<MKCoordinateRegion> {
    val coordinate = CLLocationCoordinate2DMake(coordinates.latitude, coordinates.longitude)
    val span =
        MKCoordinateSpanMake(
            calculateLatitudeDelta(zoom),
            calculateLongitudeDelta(zoom, coordinates.latitude),
        )
    return MKCoordinateRegionMake(coordinate, span)
}

/**
 * Converts Apple MapKit's MKCoordinateRegion back to CameraPosition.
 *
 * @return CameraPosition with calculated zoom level and coordinates
 */
@OptIn(ExperimentalForeignApi::class)
internal fun CValue<MKCoordinateRegion>.toCameraPosition() = useContents {
    val latZoom = kotlin.math.ln(360.0 / span.latitudeDelta) / kotlin.math.ln(2.0)
    val lngZoom = kotlin.math.ln(360.0 / span.longitudeDelta) / kotlin.math.ln(2.0)
    val zoom = kotlin.math.min(latZoom, lngZoom).toFloat()

    CameraPosition(coordinates = Coordinates(center.latitude, center.longitude), zoom = zoom)
}

/**
 * Updates Apple Maps markers by removing existing annotations and adding new ones.
 *
 * @param markers List of MapMarker objects to display
 * @return MutableMap mapping MKPointAnnotation to MapMarker for click handling
 */
@OptIn(ExperimentalForeignApi::class)
internal fun MKMapView.updateAppleMapsMarkers(
    markers: List<Marker>
): MutableMap<MKPointAnnotation, Marker> {
    removeAnnotations(this.annotations)
    val markerMapping = mutableMapOf<MKPointAnnotation, Marker>()
    markers.forEach { marker ->
        val mkAnnotation =
            MKPointAnnotation().apply {
                marker.coordinates.let { coords ->
                    setCoordinate(CLLocationCoordinate2DMake(coords.latitude, coords.longitude))
                }
                setTintColor(marker.iosTintColor?.toAppleMapsColor())
                setTitle(marker.title)
            }
        markerMapping[mkAnnotation] = marker
        addAnnotation(mkAnnotation)
    }
    return markerMapping
}

@OptIn(ExperimentalForeignApi::class)
internal fun updateGoogleMapsMarkers(
    mapView: GMSMapView,
    markers: List<Marker>,
    markerMapping: MutableMap<GMSMarker, Marker>,
) {
    markerMapping.keys.forEach { marker -> marker.map = null }
    markerMapping.clear()

    markers.forEach { marker ->
        val gmsMarker = GMSMarker()
        gmsMarker.position =
            CLLocationCoordinate2DMake(marker.coordinates.latitude, marker.coordinates.longitude)
        gmsMarker.title = marker.title
        gmsMarker.map = mapView
        markerMapping[gmsMarker] = marker
    }
}

/**
 * Converts AppleMapsPointOfInterestCategories to Apple MapKit's MKPointOfInterestFilter.
 *
 * @return MKPointOfInterestFilter for including/excluding POI categories, or null if no filtering
 */
@OptIn(ExperimentalForeignApi::class)
internal fun AppleMapsPointOfInterestCategories.toMKPointOfInterestFilter():
    MKPointOfInterestFilter? {
    val includingCategories = including?.map { it.toMKPointOfInterestCategory() }
    val excludingCategories = excluding?.map { it.toMKPointOfInterestCategory() }

    return when {
        !includingCategories.isNullOrEmpty() -> {
            MKPointOfInterestFilter(includingCategories = includingCategories)
        }
        !excludingCategories.isNullOrEmpty() -> {
            MKPointOfInterestFilter(excludingCategories = excludingCategories)
        }
        else -> null
    }
}

/**
 * Updates Apple Maps circles by removing existing overlays and adding new ones.
 *
 * @param circles List of MapCircle objects to display
 * @param circleStyles MutableMap to store MKCircle to MapCircle mappings for styling
 */
@OptIn(ExperimentalForeignApi::class)
internal fun MKMapView.updateAppleMapsCircles(
    circles: List<Circle>,
    circleStyles: MutableMap<MKCircle, Circle>,
) {
    removeOverlays(circleStyles.keys.toList())
    circleStyles.clear()

    circles.forEach { circle ->
        val coordinate = CLLocationCoordinate2DMake(circle.center.latitude, circle.center.longitude)
        val mkCircle =
            MKCircle.circleWithCenterCoordinate(coordinate, radius = circle.radius.toDouble())
        circleStyles[mkCircle] = circle
        addOverlay(mkCircle)
    }
}

@OptIn(ExperimentalForeignApi::class)
internal fun updateGoogleMapsCircles(
    mapView: GMSMapView,
    circles: List<Circle>,
    circleMapping: MutableMap<GMSCircle, Circle>,
) {
    circleMapping.keys.forEach { circle -> circle.map = null }
    circleMapping.clear()

    circles.forEach { circle ->
        val gmsCircle = GMSCircle()
        gmsCircle.position =
            CLLocationCoordinate2DMake(circle.center.latitude, circle.center.longitude)
        gmsCircle.radius = circle.radius.toDouble()
        gmsCircle.fillColor = circle.color?.toAppleMapsColor()
        gmsCircle.strokeColor = circle.lineColor?.toAppleMapsColor()
        gmsCircle.strokeWidth = (circle.lineWidth ?: 1).toDouble()
        gmsCircle.map = mapView
        circleMapping[gmsCircle] = circle
    }
}

/**
 * Updates Apple Maps polygons by removing existing overlays and adding new ones.
 *
 * @param polygons List of MapPolygon objects to display
 * @param polygonStyles MutableMap to store MKPolygon to MapPolygon mappings for styling
 */
@OptIn(ExperimentalForeignApi::class)
internal fun MKMapView.updateAppleMapsPolygons(
    polygons: List<Polygon>,
    polygonStyles: MutableMap<MKPolygon, Polygon>,
) {
    removeOverlays(polygonStyles.keys.toList())
    polygonStyles.clear()

    polygons.forEach { polygon ->
        memScoped {
            val coordinates =
                polygon.coordinates.map { coord ->
                    CLLocationCoordinate2DMake(coord.latitude, coord.longitude)
                }

            val nativeArray = allocArray<CLLocationCoordinate2D>(coordinates.size)
            for ((index, coord) in coordinates.withIndex()) {
                val elementPtr =
                    interpretCPointer<CLLocationCoordinate2D>(
                        nativeArray.rawValue + (index * sizeOf<CLLocationCoordinate2D>())
                    )
                memcpy(elementPtr, coord.ptr, sizeOf<CLLocationCoordinate2D>().toULong())
            }

            val mkPolygon =
                MKPolygon.polygonWithCoordinates(nativeArray, count = coordinates.size.toULong())
            polygonStyles[mkPolygon] = polygon
            addOverlay(mkPolygon)
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
internal fun updateGoogleMapsPolygons(
    mapView: GMSMapView,
    polygons: List<Polygon>,
    polygonMapping: MutableMap<GMSPolygon, Polygon>,
) {
    polygonMapping.keys.forEach { polygon -> polygon.map = null }
    polygonMapping.clear()

    polygons.forEach { polygon ->
        val gmsPolygon = GMSPolygon()
        val path = GMSMutablePath()
        polygon.coordinates.forEach { coord ->
            path.addCoordinate(CLLocationCoordinate2DMake(coord.latitude, coord.longitude))
        }
        gmsPolygon.path = path
        gmsPolygon.fillColor = polygon.color?.toAppleMapsColor()
        gmsPolygon.strokeColor = polygon.lineColor?.toAppleMapsColor()
        gmsPolygon.strokeWidth = polygon.lineWidth.toDouble()
        gmsPolygon.map = mapView
        polygonMapping[gmsPolygon] = polygon
    }
}

/**
 * Updates Apple Maps polylines by removing existing overlays and adding new ones.
 *
 * @param polylines List of MapPolyline objects to display
 * @param polylineStyles MutableMap to store MKPolyline to MapPolyline mappings for styling
 */
@OptIn(ExperimentalForeignApi::class)
internal fun MKMapView.updateAppleMapsPolylines(
    polylines: List<Polyline>,
    polylineStyles: MutableMap<MKPolyline, Polyline>,
) {
    removeOverlays(polylineStyles.keys.toList())
    polylineStyles.clear()

    polylines.forEach { polyline ->
        memScoped {
            val coordinates =
                polyline.coordinates.map { coord ->
                    CLLocationCoordinate2DMake(coord.latitude, coord.longitude)
                }

            val nativeArray = allocArray<CLLocationCoordinate2D>(coordinates.size)
            for ((index, coord) in coordinates.withIndex()) {
                val elementPtr =
                    interpretCPointer<CLLocationCoordinate2D>(
                        nativeArray.rawValue + (index * sizeOf<CLLocationCoordinate2D>())
                    )
                memcpy(elementPtr, coord.ptr, sizeOf<CLLocationCoordinate2D>().toULong())
            }

            val mkPolyline =
                MKPolyline.polylineWithCoordinates(nativeArray, count = coordinates.size.toULong())
            polylineStyles[mkPolyline] = polyline
            addOverlay(mkPolyline)
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
internal fun updateGoogleMapsPolylines(
    mapView: GMSMapView,
    polylines: List<Polyline>,
    polylineMapping: MutableMap<GMSPolyline, Polyline>,
) {
    polylineMapping.keys.forEach { polyline -> polyline.map = null }
    polylineMapping.clear()

    polylines.forEach { polyline ->
        val gmsPolyline = GMSPolyline()
        val path = GMSMutablePath()
        polyline.coordinates.forEach { coord ->
            path.addCoordinate(CLLocationCoordinate2DMake(coord.latitude, coord.longitude))
        }
        gmsPolyline.path = path
        gmsPolyline.strokeWidth = polyline.width.toDouble()
        gmsPolyline.map = mapView
        polylineMapping[gmsPolyline] = polyline
    }
}

/**
 * Converts MapType enum to Apple MapKit's map type constant.
 *
 * @return MKMapType constant corresponding to the MapType enum value
 */
internal fun MapType?.toAppleMapsMapType() =
    when (this) {
        MapType.HYBRID -> MKMapTypeHybrid
        MapType.NORMAL -> MKMapTypeStandard
        MapType.SATELLITE -> MKMapTypeSatellite
        else -> MKMapTypeStandard
    }

@OptIn(ExperimentalForeignApi::class)
internal fun MapType?.toGoogleMapsMapType() =
    when (this) {
        MapType.HYBRID -> kGMSTypeHybrid
        MapType.NORMAL -> kGMSTypeNormal
        MapType.SATELLITE -> kGMSTypeSatellite
        MapType.TERRAIN -> kGMSTypeTerrain
        else -> kGMSTypeNormal
    }

/**
 * Converts AppleMapPointOfInterestCategory enum to Apple MapKit's MKPointOfInterestCategory.
 *
 * @return MKPointOfInterestCategory constant corresponding to the enum value
 */
@OptIn(ExperimentalForeignApi::class)
internal fun AppleMapsPointOfInterestCategory.toMKPointOfInterestCategory():
    MKPointOfInterestCategory =
    when (this) {
        AppleMapsPointOfInterestCategory.AIRPORT -> MKPointOfInterestCategoryAirport
        AppleMapsPointOfInterestCategory.AMUSEMENT_PARK -> MKPointOfInterestCategoryAmusementPark
        AppleMapsPointOfInterestCategory.ANIMAL_SERVICE -> MKPointOfInterestCategoryAnimalService
        AppleMapsPointOfInterestCategory.AQUARIUM -> MKPointOfInterestCategoryAquarium
        AppleMapsPointOfInterestCategory.ATM -> MKPointOfInterestCategoryATM
        AppleMapsPointOfInterestCategory.AUTOMOTIVE_REPAIR ->
            MKPointOfInterestCategoryAutomotiveRepair
        AppleMapsPointOfInterestCategory.BAKERY -> MKPointOfInterestCategoryBakery
        AppleMapsPointOfInterestCategory.BANK -> MKPointOfInterestCategoryBank
        AppleMapsPointOfInterestCategory.BASEBALL -> MKPointOfInterestCategoryBaseball
        AppleMapsPointOfInterestCategory.BASKETBALL -> MKPointOfInterestCategoryBasketball
        AppleMapsPointOfInterestCategory.BEACH -> MKPointOfInterestCategoryBeach
        AppleMapsPointOfInterestCategory.BEAUTY -> MKPointOfInterestCategoryBeauty
        AppleMapsPointOfInterestCategory.BOWLING -> MKPointOfInterestCategoryBowling
        AppleMapsPointOfInterestCategory.BREWERY -> MKPointOfInterestCategoryBrewery
        AppleMapsPointOfInterestCategory.CAFE -> MKPointOfInterestCategoryCafe
        AppleMapsPointOfInterestCategory.CAMPGROUND -> MKPointOfInterestCategoryCampground
        AppleMapsPointOfInterestCategory.CAR_RENTAL -> MKPointOfInterestCategoryCarRental
        AppleMapsPointOfInterestCategory.CASTLE -> MKPointOfInterestCategoryCastle
        AppleMapsPointOfInterestCategory.CONVENTION_CENTER ->
            MKPointOfInterestCategoryConventionCenter
        AppleMapsPointOfInterestCategory.DISTILLERY -> MKPointOfInterestCategoryDistillery
        AppleMapsPointOfInterestCategory.EV_CHARGER -> MKPointOfInterestCategoryEVCharger
        AppleMapsPointOfInterestCategory.FAIRGROUND -> MKPointOfInterestCategoryFairground
        AppleMapsPointOfInterestCategory.FIRE_STATION -> MKPointOfInterestCategoryFireStation
        AppleMapsPointOfInterestCategory.FISHING -> MKPointOfInterestCategoryFishing
        AppleMapsPointOfInterestCategory.FITNESS_CENTER -> MKPointOfInterestCategoryFitnessCenter
        AppleMapsPointOfInterestCategory.FOOD_MARKET -> MKPointOfInterestCategoryFoodMarket
        AppleMapsPointOfInterestCategory.FORTRESS -> MKPointOfInterestCategoryFortress
        AppleMapsPointOfInterestCategory.GAS_STATION -> MKPointOfInterestCategoryGasStation
        AppleMapsPointOfInterestCategory.GO_KART -> MKPointOfInterestCategoryGoKart
        AppleMapsPointOfInterestCategory.GOLF -> MKPointOfInterestCategoryGolf
        AppleMapsPointOfInterestCategory.HIKING -> MKPointOfInterestCategoryHiking
        AppleMapsPointOfInterestCategory.HOSPITAL -> MKPointOfInterestCategoryHospital
        AppleMapsPointOfInterestCategory.HOTEL -> MKPointOfInterestCategoryHotel
        AppleMapsPointOfInterestCategory.KAYAKING -> MKPointOfInterestCategoryKayaking
        AppleMapsPointOfInterestCategory.LANDMARK -> MKPointOfInterestCategoryLandmark
        AppleMapsPointOfInterestCategory.LAUNDRY -> MKPointOfInterestCategoryLaundry
        AppleMapsPointOfInterestCategory.LIBRARY -> MKPointOfInterestCategoryLibrary
        AppleMapsPointOfInterestCategory.MAILBOX -> MKPointOfInterestCategoryMailbox
        AppleMapsPointOfInterestCategory.MARINA -> MKPointOfInterestCategoryMarina
        AppleMapsPointOfInterestCategory.MINI_GOLF -> MKPointOfInterestCategoryMiniGolf
        AppleMapsPointOfInterestCategory.MOVIE_THEATER -> MKPointOfInterestCategoryMovieTheater
        AppleMapsPointOfInterestCategory.MUSEUM -> MKPointOfInterestCategoryMuseum
        AppleMapsPointOfInterestCategory.MUSIC_VENUE -> MKPointOfInterestCategoryMusicVenue
        AppleMapsPointOfInterestCategory.NATIONAL_MONUMENT ->
            MKPointOfInterestCategoryNationalMonument
        AppleMapsPointOfInterestCategory.NATIONAL_PARK -> MKPointOfInterestCategoryNationalPark
        AppleMapsPointOfInterestCategory.NIGHTLIFE -> MKPointOfInterestCategoryNightlife
        AppleMapsPointOfInterestCategory.PARK -> MKPointOfInterestCategoryPark
        AppleMapsPointOfInterestCategory.PARKING -> MKPointOfInterestCategoryParking
        AppleMapsPointOfInterestCategory.PHARMACY -> MKPointOfInterestCategoryPharmacy
        AppleMapsPointOfInterestCategory.PLANETARIUM -> MKPointOfInterestCategoryPlanetarium
        AppleMapsPointOfInterestCategory.POLICE -> MKPointOfInterestCategoryPolice
        AppleMapsPointOfInterestCategory.POST_OFFICE -> MKPointOfInterestCategoryPostOffice
        AppleMapsPointOfInterestCategory.PUBLIC_TRANSPORT ->
            MKPointOfInterestCategoryPublicTransport
        AppleMapsPointOfInterestCategory.RESTAURANT -> MKPointOfInterestCategoryRestaurant
        AppleMapsPointOfInterestCategory.RESTROOM -> MKPointOfInterestCategoryRestroom
        AppleMapsPointOfInterestCategory.ROCK_CLIMBING -> MKPointOfInterestCategoryRockClimbing
        AppleMapsPointOfInterestCategory.RV_PARK -> MKPointOfInterestCategoryRVPark
        AppleMapsPointOfInterestCategory.SCHOOL -> MKPointOfInterestCategorySchool
        AppleMapsPointOfInterestCategory.SKATE_PARK -> MKPointOfInterestCategorySkatePark
        AppleMapsPointOfInterestCategory.SKATING -> MKPointOfInterestCategorySkating
        AppleMapsPointOfInterestCategory.SKIING -> MKPointOfInterestCategorySkiing
        AppleMapsPointOfInterestCategory.SOCCER -> MKPointOfInterestCategorySoccer
        AppleMapsPointOfInterestCategory.SPA -> MKPointOfInterestCategorySpa
        AppleMapsPointOfInterestCategory.STADIUM -> MKPointOfInterestCategoryStadium
        AppleMapsPointOfInterestCategory.STORE -> MKPointOfInterestCategoryStore
        AppleMapsPointOfInterestCategory.SURFING -> MKPointOfInterestCategorySurfing
        AppleMapsPointOfInterestCategory.SWIMMING -> MKPointOfInterestCategorySwimming
        AppleMapsPointOfInterestCategory.TENNIS -> MKPointOfInterestCategoryTennis
        AppleMapsPointOfInterestCategory.THEATER -> MKPointOfInterestCategoryTheater
        AppleMapsPointOfInterestCategory.UNIVERSITY -> MKPointOfInterestCategoryUniversity
        AppleMapsPointOfInterestCategory.VOLLEYBALL -> MKPointOfInterestCategoryVolleyball
        AppleMapsPointOfInterestCategory.WINERY -> MKPointOfInterestCategoryWinery
        AppleMapsPointOfInterestCategory.ZOO -> MKPointOfInterestCategoryZoo
    }

/**
 * Converts androidx Color to Apple UIKit's UIColor.
 *
 * @return UIColor corresponding to the androidx Color object
 */
@OptIn(ExperimentalForeignApi::class)
internal fun Color.toAppleMapsColor(): UIColor {
    val argb = this.toArgb()
    return UIColor.colorWithRed(
        red = ((argb shr 16) and 0xFF) / 255.0,
        green = ((argb shr 8) and 0xFF) / 255.0,
        blue = (argb and 0xFF) / 255.0,
        alpha = ((argb shr 24) and 0xFF) / 255.0,
    )
}

/**
 * Switches between light and dark mode for the map.
 *
 * @param isDarkModeEnabled true for dark mode, false for light mode
 */
internal fun MKMapView.switchTheme(isDarkModeEnabled: Boolean) {
    overrideUserInterfaceStyle =
        if (isDarkModeEnabled) {
            UIUserInterfaceStyle.UIUserInterfaceStyleDark
        } else {
            UIUserInterfaceStyle.UIUserInterfaceStyleLight
        }
}

@OptIn(ExperimentalForeignApi::class)
internal fun GMSMapView.switchTheme(isDarkModeEnabled: Boolean) {
    overrideUserInterfaceStyle =
        if (isDarkModeEnabled) {
            UIUserInterfaceStyle.UIUserInterfaceStyleDark
        } else {
            UIUserInterfaceStyle.UIUserInterfaceStyleLight
        }
}

@OptIn(ExperimentalForeignApi::class)
internal fun MapUISettings.toGoogleMapsSettings(mapView: GMSMapView) {
    mapView.settings.scrollGestures = scrollEnabled
    mapView.settings.zoomGestures = zoomEnabled
    mapView.settings.tiltGestures = iosGmsTiltGesturesEnabled
    mapView.settings.rotateGestures = rotateEnabled
    mapView.settings.compassButton = compassEnabled
    mapView.settings.myLocationButton = myLocationButtonEnabled
    mapView.settings.indoorPicker = iosGmsIndoorPicker
    mapView.settings.allowScrollGesturesDuringRotateOrZoom =
        iosGmsScrollGesturesEnabledDuringRotateOrZoom
    mapView.settings.consumesGesturesInView = iosGmsConsumesGesturesInView
}

@OptIn(ExperimentalForeignApi::class)
internal fun GoogleMapsMapStyleOptions?.toNativeStyleOptions() =
    this?.json?.let { GMSMapStyle.styleWithJSONString(it, error = null) }
