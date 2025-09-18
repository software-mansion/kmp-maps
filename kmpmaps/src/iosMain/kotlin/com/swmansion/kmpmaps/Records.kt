package com.swmansion.kmpmaps

data class AppleMapsProperties(
    override val mapType: MapType = MapType.STANDARD,
    val elevation: AppleMapsMapStyleElevation? = AppleMapsMapStyleElevation.AUTOMATIC,
    val emphasis: AppleMapsMapStyleEmphasis? = AppleMapsMapStyleEmphasis.AUTOMATIC,
    override val isMyLocationEnabled: Boolean = false,
    override val isTrafficEnabled: Boolean = false,
    val pointsOfInterest: AppleMapsPointOfInterestCategories? = null,
    val polylineTapThreshold: Float? = null,
    val selectionEnabled: Boolean = false,
    override val showsBuildings: Boolean = true,
) : MapProperties


data class AppleMapsUISettings(
    override val compassEnabled: Boolean = true,
    override val myLocationButtonEnabled: Boolean = true,
    override val zoomGesturesEnabled: Boolean = true,
    override val scrollGesturesEnabled: Boolean = true,
    override val rotateGesturesEnabled: Boolean = true,
    override val tiltGesturesEnabled: Boolean = true,
) : MapUISettings


open class AppleMapsMarker(
    override val coordinates: Coordinates,
    val systemImage: String? = null,
    val tintColor: String? = null,
    override val title: String? = null,
) : MapMarker {
    override val subtitle: String? = null
}

class AppleMapsAnnotations(
    public var backgroundColor: String? = null,
    public var text: String? = null,
    public var textColor: String? = null,
    coordinates: Coordinates
) : AppleMapsMarker(coordinates)

fun MapType.toAppleMapsMapType(): AppleMapsMapType {
    return when (this) {
        MapType.STANDARD -> AppleMapsMapType.STANDARD
        MapType.SATELLITE -> AppleMapsMapType.SATELLITE
        MapType.HYBRID -> AppleMapsMapType.HYBRID
        MapType.TERRAIN -> AppleMapsMapType.STANDARD // Apple Maps doesn't have terrain
    }
}

enum class AppleMapsMapStyleElevation {
    AUTOMATIC,
    FLAT,
    REALISTIC
}

enum class AppleMapsMapStyleEmphasis {
    AUTOMATIC,
    MUTED,
}

data class AppleMapsPointOfInterestCategories(
    val excluding: List<AppleMapPointOfInterestCategory>? = emptyList(),
    val including: List<AppleMapPointOfInterestCategory>? = emptyList(),
)

enum class AppleMapPointOfInterestCategory {
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

enum class AppleMapsMapType {
    STANDARD,
    SATELLITE,
    HYBRID,
}
