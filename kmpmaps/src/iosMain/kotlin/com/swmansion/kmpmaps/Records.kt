package com.swmansion.kmpmaps

import platform.UIKit.UIColor

public data class Coordinates(val latitude: Double, val longitude: Double)

public data class CameraPosition(
    val coordinates: Coordinates,
    val zoom: Float,
    val bearing: Float = 0f,
    val tilt: Float = 0f,
)

public class AppleMapsAnnotations(
    public var backgroundColor: String? = null,
    public var text: String? = null,
    public var textColor: String? = null,
    coordinates: Coordinates
) : AppleMapsMarker(coordinates)

public open class AppleMapsMarker(
    public val coordinates: Coordinates,
    public val systemImage: String? = null,
    public val tintColor: String? = null,
    public val title: String? = null,
)

public data class AppleMapsCircle(
    val center: Coordinates,
    val radius: Double,
    val width: Float? = 0f,
    val lineColor: UIColor? = null,
    val lineWidth: Float = 0f,
    val color: UIColor? = null,
)

public data class AppleMapsPolygon(
    val coordinates: List<Coordinates>,
    val lineColor: String? = null,
    val lineWidth: Float = 0f,
    val color: String? = null
)

public data class AppleMapsPolyline(
    val coordinates: List<Coordinates>,
    val width: Float? = 0f,
    val contourStyle: AppleMapsContourStyle? = AppleMapsContourStyle.GEODESIC,
)

public enum class AppleMapsContourStyle {
    GEODESIC,
    STRAIGHT
}

public data class AppleMapsProperties(
    val mapType: AppleMapsMapType = AppleMapsMapType.STANDARD,
    val elevation: AppleMapsMapStyleElevation? = AppleMapsMapStyleElevation.AUTOMATIC,
    val emphasis: AppleMapsMapStyleEmphasis? = AppleMapsMapStyleEmphasis.AUTOMATIC,
    val isMyLocationEnabled: Boolean = false,
    val isTrafficEnabled: Boolean = false,
    val pointsOfInterest: AppleMapsPointOfInterestCategories? = null,
    val polylineTapThreshold: Float? = null,
    val selectionEnabled: Boolean = false,
    val showsBuildings: Boolean = true,
)

public enum class AppleMapsMapStyleElevation {
    AUTOMATIC,
    FLAT,
    REALISTIC
}

public enum class AppleMapsMapStyleEmphasis {
    AUTOMATIC,
    MUTED,
}

public data class AppleMapsPointOfInterestCategories(
    val excluding: List<AppleMapPointOfInterestCategory>? = emptyList(),
    val including: List<AppleMapPointOfInterestCategory>? = emptyList(),
)

public enum class AppleMapPointOfInterestCategory {
    AIRPORT,
    AMUSEMENT_PARK,
    ANIMAL_SERVICE,
    AQUARIUM,
    ATM,
    AUTOMOTIVE_REPAIR,
    BAKERY,
    BANK,
    BASEBALL,
    BASKETBALL,
    BEACH,
    BEAUTY,
    BOWLING,
    BREWERY,
    CAFE,
    CAMPGROUND,
    CAR_RENTAL,
    CASTLE,
    CONVENTION_CENTER,
    DISTILLERY,
    EV_CHARGER,
    FAIRGROUND,
    FIRE_STATION,
    FISHING,
    FITNESS_CENTER,
    FOOD_MARKET,
    FORTRESS,
    GAS_STATION,
    GO_KART,
    GOLF,
    HIKING,
    HOSPITAL,
    HOTEL,
    KAYAKING,
    LANDMARK,
    LAUNDRY,
    LIBRARY,
    MAILBOX,
    MARINA,
    MINI_GOLF,
    MOVIE_THEATER,
    MUSEUM,
    MUSIC_VENUE,
    NATIONAL_MONUMENT,
    NATIONAL_PARK,
    NIGHTLIFE,
    PARK,
    PARKING,
    PHARMACY,
    PLANETARIUM,
    POLICE,
    POST_OFFICE,
    PUBLIC_TRANSPORT,
    RESTAURANT,
    RESTROOM,
    ROCK_CLIMBING,
    RV_PARK,
    SCHOOL,
    SKATE_PARK,
    SKATING,
    SKIING,
    SOCCER,
    SPA,
    STADIUM,
    STORE,
    SURFING,
    SWIMMING,
    TENNIS,
    THEATER,
    UNIVERSITY,
    VOLLEYBALL,
    WINERY,
    ZOO
}

public data class AppleMapsUISettings(
    val compassEnabled: Boolean = true,
    val myLocationButtonEnabled: Boolean = true,
    val zoomGesturesEnabled: Boolean = true,
    val scrollGesturesEnabled: Boolean = true,
    val rotateGesturesEnabled: Boolean = true,
    val tiltGesturesEnabled: Boolean = true,
)


public enum class AppleMapsMapType {
    STANDARD,
    SATELLITE,
    HYBRID,
}
