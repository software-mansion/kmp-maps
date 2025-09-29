package com.swmansion.kmpmaps

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
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
import platform.UIKit.UIColor
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
    markers: List<MapMarker>
): MutableMap<MKPointAnnotation, MapMarker> {
    removeAnnotations(this.annotations)
    val markerMapping = mutableMapOf<MKPointAnnotation, MapMarker>()
    markers.forEach { marker ->
        val mkAnnotation =
            MKPointAnnotation().apply {
                marker.coordinates.let { coords ->
                    setCoordinate(CLLocationCoordinate2DMake(coords.latitude, coords.longitude))
                }
                setTintColor(marker.appleTintColor?.toAppleColor())
                setTitle(marker.title)
            }
        markerMapping[mkAnnotation] = marker
        addAnnotation(mkAnnotation)
    }
    return markerMapping
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
 * Updates Apple Maps circles by creating MKCircle overlays and storing style mappings.
 *
 * @param circles List of MapCircle objects to display
 * @param circleStyles MutableMap to store MKCircle to MapCircle mappings for styling
 */
@OptIn(ExperimentalForeignApi::class)
internal fun MKMapView.updateAppleMapsCircles(
    circles: List<MapCircle>,
    circleStyles: MutableMap<MKCircle, MapCircle>,
) {
    circles.forEach { circle ->
        val coordinate = CLLocationCoordinate2DMake(circle.center.latitude, circle.center.longitude)
        val mkCircle =
            MKCircle.circleWithCenterCoordinate(coordinate, radius = circle.radius.toDouble())
        circleStyles[mkCircle] = circle
        addOverlay(mkCircle)
    }
}

/**
 * Updates Apple Maps polygons by creating MKPolygon overlays and storing style mappings.
 *
 * @param polygons List of MapPolygon objects to display
 * @param polygonStyles MutableMap to store MKPolygon to MapPolygon mappings for styling
 */
@OptIn(ExperimentalForeignApi::class)
internal fun MKMapView.updateAppleMapsPolygons(
    polygons: List<MapPolygon>,
    polygonStyles: MutableMap<MKPolygon, MapPolygon>,
) {
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

/**
 * Updates Apple Maps polylines by creating MKPolyline overlays and storing style mappings.
 *
 * @param polylines List of MapPolyline objects to display
 * @param polylineStyles MutableMap to store MKPolyline to MapPolyline mappings for styling
 */
@OptIn(ExperimentalForeignApi::class)
internal fun MKMapView.updateAppleMapsPolylines(
    polylines: List<MapPolyline>,
    polylineStyles: MutableMap<MKPolyline, MapPolyline>,
) {
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

/**
 * Converts AppleMapPointOfInterestCategory enum to Apple MapKit's MKPointOfInterestCategory.
 *
 * @return MKPointOfInterestCategory constant corresponding to the enum value
 */
@OptIn(ExperimentalForeignApi::class)
internal fun AppleMapPointOfInterestCategory.toMKPointOfInterestCategory():
    MKPointOfInterestCategory =
    when (this) {
        AppleMapPointOfInterestCategory.AIRPORT -> MKPointOfInterestCategoryAirport
        AppleMapPointOfInterestCategory.AMUSEMENT_PARK -> MKPointOfInterestCategoryAmusementPark
        AppleMapPointOfInterestCategory.ANIMAL_SERVICE -> MKPointOfInterestCategoryAnimalService
        AppleMapPointOfInterestCategory.AQUARIUM -> MKPointOfInterestCategoryAquarium
        AppleMapPointOfInterestCategory.ATM -> MKPointOfInterestCategoryATM
        AppleMapPointOfInterestCategory.AUTOMOTIVE_REPAIR ->
            MKPointOfInterestCategoryAutomotiveRepair
        AppleMapPointOfInterestCategory.BAKERY -> MKPointOfInterestCategoryBakery
        AppleMapPointOfInterestCategory.BANK -> MKPointOfInterestCategoryBank
        AppleMapPointOfInterestCategory.BASEBALL -> MKPointOfInterestCategoryBaseball
        AppleMapPointOfInterestCategory.BASKETBALL -> MKPointOfInterestCategoryBasketball
        AppleMapPointOfInterestCategory.BEACH -> MKPointOfInterestCategoryBeach
        AppleMapPointOfInterestCategory.BEAUTY -> MKPointOfInterestCategoryBeauty
        AppleMapPointOfInterestCategory.BOWLING -> MKPointOfInterestCategoryBowling
        AppleMapPointOfInterestCategory.BREWERY -> MKPointOfInterestCategoryBrewery
        AppleMapPointOfInterestCategory.CAFE -> MKPointOfInterestCategoryCafe
        AppleMapPointOfInterestCategory.CAMPGROUND -> MKPointOfInterestCategoryCampground
        AppleMapPointOfInterestCategory.CAR_RENTAL -> MKPointOfInterestCategoryCarRental
        AppleMapPointOfInterestCategory.CASTLE -> MKPointOfInterestCategoryCastle
        AppleMapPointOfInterestCategory.CONVENTION_CENTER ->
            MKPointOfInterestCategoryConventionCenter
        AppleMapPointOfInterestCategory.DISTILLERY -> MKPointOfInterestCategoryDistillery
        AppleMapPointOfInterestCategory.EV_CHARGER -> MKPointOfInterestCategoryEVCharger
        AppleMapPointOfInterestCategory.FAIRGROUND -> MKPointOfInterestCategoryFairground
        AppleMapPointOfInterestCategory.FIRE_STATION -> MKPointOfInterestCategoryFireStation
        AppleMapPointOfInterestCategory.FISHING -> MKPointOfInterestCategoryFishing
        AppleMapPointOfInterestCategory.FITNESS_CENTER -> MKPointOfInterestCategoryFitnessCenter
        AppleMapPointOfInterestCategory.FOOD_MARKET -> MKPointOfInterestCategoryFoodMarket
        AppleMapPointOfInterestCategory.FORTRESS -> MKPointOfInterestCategoryFortress
        AppleMapPointOfInterestCategory.GAS_STATION -> MKPointOfInterestCategoryGasStation
        AppleMapPointOfInterestCategory.GO_KART -> MKPointOfInterestCategoryGoKart
        AppleMapPointOfInterestCategory.GOLF -> MKPointOfInterestCategoryGolf
        AppleMapPointOfInterestCategory.HIKING -> MKPointOfInterestCategoryHiking
        AppleMapPointOfInterestCategory.HOSPITAL -> MKPointOfInterestCategoryHospital
        AppleMapPointOfInterestCategory.HOTEL -> MKPointOfInterestCategoryHotel
        AppleMapPointOfInterestCategory.KAYAKING -> MKPointOfInterestCategoryKayaking
        AppleMapPointOfInterestCategory.LANDMARK -> MKPointOfInterestCategoryLandmark
        AppleMapPointOfInterestCategory.LAUNDRY -> MKPointOfInterestCategoryLaundry
        AppleMapPointOfInterestCategory.LIBRARY -> MKPointOfInterestCategoryLibrary
        AppleMapPointOfInterestCategory.MAILBOX -> MKPointOfInterestCategoryMailbox
        AppleMapPointOfInterestCategory.MARINA -> MKPointOfInterestCategoryMarina
        AppleMapPointOfInterestCategory.MINI_GOLF -> MKPointOfInterestCategoryMiniGolf
        AppleMapPointOfInterestCategory.MOVIE_THEATER -> MKPointOfInterestCategoryMovieTheater
        AppleMapPointOfInterestCategory.MUSEUM -> MKPointOfInterestCategoryMuseum
        AppleMapPointOfInterestCategory.MUSIC_VENUE -> MKPointOfInterestCategoryMusicVenue
        AppleMapPointOfInterestCategory.NATIONAL_MONUMENT ->
            MKPointOfInterestCategoryNationalMonument
        AppleMapPointOfInterestCategory.NATIONAL_PARK -> MKPointOfInterestCategoryNationalPark
        AppleMapPointOfInterestCategory.NIGHTLIFE -> MKPointOfInterestCategoryNightlife
        AppleMapPointOfInterestCategory.PARK -> MKPointOfInterestCategoryPark
        AppleMapPointOfInterestCategory.PARKING -> MKPointOfInterestCategoryParking
        AppleMapPointOfInterestCategory.PHARMACY -> MKPointOfInterestCategoryPharmacy
        AppleMapPointOfInterestCategory.PLANETARIUM -> MKPointOfInterestCategoryPlanetarium
        AppleMapPointOfInterestCategory.POLICE -> MKPointOfInterestCategoryPolice
        AppleMapPointOfInterestCategory.POST_OFFICE -> MKPointOfInterestCategoryPostOffice
        AppleMapPointOfInterestCategory.PUBLIC_TRANSPORT -> MKPointOfInterestCategoryPublicTransport
        AppleMapPointOfInterestCategory.RESTAURANT -> MKPointOfInterestCategoryRestaurant
        AppleMapPointOfInterestCategory.RESTROOM -> MKPointOfInterestCategoryRestroom
        AppleMapPointOfInterestCategory.ROCK_CLIMBING -> MKPointOfInterestCategoryRockClimbing
        AppleMapPointOfInterestCategory.RV_PARK -> MKPointOfInterestCategoryRVPark
        AppleMapPointOfInterestCategory.SCHOOL -> MKPointOfInterestCategorySchool
        AppleMapPointOfInterestCategory.SKATE_PARK -> MKPointOfInterestCategorySkatePark
        AppleMapPointOfInterestCategory.SKATING -> MKPointOfInterestCategorySkating
        AppleMapPointOfInterestCategory.SKIING -> MKPointOfInterestCategorySkiing
        AppleMapPointOfInterestCategory.SOCCER -> MKPointOfInterestCategorySoccer
        AppleMapPointOfInterestCategory.SPA -> MKPointOfInterestCategorySpa
        AppleMapPointOfInterestCategory.STADIUM -> MKPointOfInterestCategoryStadium
        AppleMapPointOfInterestCategory.STORE -> MKPointOfInterestCategoryStore
        AppleMapPointOfInterestCategory.SURFING -> MKPointOfInterestCategorySurfing
        AppleMapPointOfInterestCategory.SWIMMING -> MKPointOfInterestCategorySwimming
        AppleMapPointOfInterestCategory.TENNIS -> MKPointOfInterestCategoryTennis
        AppleMapPointOfInterestCategory.THEATER -> MKPointOfInterestCategoryTheater
        AppleMapPointOfInterestCategory.UNIVERSITY -> MKPointOfInterestCategoryUniversity
        AppleMapPointOfInterestCategory.VOLLEYBALL -> MKPointOfInterestCategoryVolleyball
        AppleMapPointOfInterestCategory.WINERY -> MKPointOfInterestCategoryWinery
        AppleMapPointOfInterestCategory.ZOO -> MKPointOfInterestCategoryZoo
    }

/**
 * Converts androidx Color to Apple UIKit's UIColor.
 *
 * @return UIColor corresponding to the androidx Color object
 */
@OptIn(ExperimentalForeignApi::class)
internal fun Color.toAppleColor(): UIColor {
    val argb = this.toArgb()
    return UIColor.colorWithRed(
        red = ((argb shr 16) and 0xFF) / 255.0,
        green = ((argb shr 8) and 0xFF) / 255.0,
        blue = (argb and 0xFF) / 255.0,
        alpha = ((argb shr 24) and 0xFF) / 255.0,
    )
}
