package com.swmansion.kmpmaps

internal fun generateGoogleMapsHTML(
    cameraPosition: CameraPosition?,
    properties: MapProperties,
    uiSettings: MapUISettings,
    markers: List<Marker>,
    circles: List<Circle>,
    polygons: List<Polygon>,
    polylines: List<Polyline>,
): String {
    val lat = cameraPosition?.coordinates?.latitude ?: 50.0619
    val lng = cameraPosition?.coordinates?.longitude ?: 19.9373
    val zoom = cameraPosition?.zoom ?: 13f

    val mapType =
        when (properties.mapType) {
            MapType.SATELLITE -> "satellite"
            MapType.HYBRID -> "hybrid"
            MapType.TERRAIN -> "terrain"
            else -> "roadmap"
        }

    val markersJS = generateMarkersJS(markers)
    val circlesJS = generateCirclesJS(circles)
    val polygonsJS = generatePolygonsJS(polygons)
    val polylinesJS = generatePolylinesJS(polylines)

    return """
    <!DOCTYPE html>
    <html>
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <script src="https://maps.googleapis.com/maps/api/js?key=YOUR_API_KEY"></script>
        <style>
            html, body, #map { 
                height: 100%; 
                margin: 0; 
                padding: 0; 
                font-family: Arial, sans-serif;
            }
        </style>
    </head>
    <body>
        <div id="map"></div>
        <script>
            let map;
            let mapMarkers = [];
            let mapCircles = [];
            let mapPolygons = [];
            let mapPolylines = [];
            
            function initMap() {
                map = new google.maps.Map(document.getElementById('map'), {
                    center: { lat: $lat, lng: $lng },
                    zoom: $zoom,
                    mapTypeId: '$mapType',
                    disableDefaultUI: ${!uiSettings.myLocationButtonEnabled},
                    zoomControl: ${uiSettings.zoomEnabled},
                    scrollwheel: ${uiSettings.scrollEnabled},
                    disableDoubleClickZoom: false,
                    draggable: ${uiSettings.scrollEnabled}
                });

                // Add markers
                $markersJS

                // Add circles
                $circlesJS

                // Add polygons
                $polygonsJS

                // Add polylines
                $polylinesJS

                // Add map click listeners
                map.addListener('click', function(event) {
                    console.log('Map clicked at:', event.latLng.lat(), event.latLng.lng());
                });

                map.addListener('rightclick', function(event) {
                    console.log('Map right-clicked at:', event.latLng.lat(), event.latLng.lng());
                });

                console.log('Google Maps initialized with ${markers.size} markers, ${circles.size} circles, ${polygons.size} polygons, ${polylines.size} polylines');
            }

            // Initialize map when page loads
            google.maps.event.addDomListener(window, 'load', initMap);
        </script>
    </body>
    </html>
    """
        .trimIndent()
}

internal fun generateMarkersJS(markers: List<Marker>): String {
    if (markers.isEmpty()) return ""

    return markers.mapIndexed { index, marker ->
        """
        // Marker $index
        const marker$index = new google.maps.Marker({
            position: { lat: ${marker.coordinates.latitude}, lng: ${marker.coordinates.longitude} },
            map: map,
            title: "${marker.title ?: ""}",
            ${if (marker.androidDraggable) "draggable: true," else ""}
        });
        
        ${if (marker.androidSnippet != null) """
        const infoWindow$index = new google.maps.InfoWindow({
            content: "<div><strong>${marker.title ?: ""}</strong><br/>${marker.androidSnippet}</div>"
        });
        
        marker$index.addListener('click', function() {
            infoWindow$index.open(map, marker$index);
        });
        """ else """
        marker$index.addListener('click', function() {
            console.log('Marker $index clicked');
        });
        """}
        
        mapMarkers.push(marker$index);
        """.trimIndent()
    }.joinToString("\n\n")
}

internal fun generateCirclesJS(circles: List<Circle>): String {
    if (circles.isEmpty()) return ""

    return circles.mapIndexed { index, circle ->
        val fillColor = circle.color?.let { colorToHex(it) } ?: "#FF0000"
        val strokeColor = circle.lineColor?.let { colorToHex(it) } ?: "#FF0000"
        val strokeWeight = circle.lineWidth ?: 2f

        """
        // Circle $index
        const circle$index = new google.maps.Circle({
            strokeColor: "$strokeColor",
            strokeOpacity: 0.8,
            strokeWeight: $strokeWeight,
            fillColor: "$fillColor",
            fillOpacity: 0.35,
            map: map,
            center: { lat: ${circle.center.latitude}, lng: ${circle.center.longitude} },
            radius: ${circle.radius}
        });
        
        circle$index.addListener('click', function() {
            console.log('Circle $index clicked');
        });
        
        mapCircles.push(circle$index);
        """.trimIndent()
    }.joinToString("\n\n")
}

internal fun generatePolygonsJS(polygons: List<Polygon>): String {
    if (polygons.isEmpty()) return ""

    return polygons.mapIndexed { index, polygon ->
        val fillColor = polygon.color?.let { colorToHex(it) } ?: "#FF0000"
        val strokeColor = polygon.lineColor?.let { colorToHex(it) } ?: "#FF0000"
        val strokeWeight = polygon.lineWidth

        val path = polygon.coordinates.joinToString(", ") {
            "{ lat: ${it.latitude}, lng: ${it.longitude} }"
        }

        """
        // Polygon $index
        const polygon$index = new google.maps.Polygon({
            paths: [$path],
            strokeColor: "$strokeColor",
            strokeOpacity: 0.8,
            strokeWeight: $strokeWeight,
            fillColor: "$fillColor",
            fillOpacity: 0.35,
            map: map
        });
        
        polygon$index.addListener('click', function() {
            console.log('Polygon $index clicked');
        });
        
        mapPolygons.push(polygon$index);
        """.trimIndent()
    }.joinToString("\n\n")
}

internal fun generatePolylinesJS(polylines: List<Polyline>): String {
    if (polylines.isEmpty()) return ""

    return polylines.mapIndexed { index, polyline ->
        val strokeColor = polyline.lineColor?.let { colorToHex(it) } ?: "#FF0000"
        val strokeWeight = polyline.width

        val path = polyline.coordinates.joinToString(", ") {
            "{ lat: ${it.latitude}, lng: ${it.longitude} }"
        }

        """
        // Polyline $index
        const polyline$index = new google.maps.Polyline({
            path: [$path],
            geodesic: true,
            strokeColor: "$strokeColor",
            strokeOpacity: 1.0,
            strokeWeight: $strokeWeight,
            map: map
        });
        
        polyline$index.addListener('click', function() {
            console.log('Polyline $index clicked');
        });
        
        mapPolylines.push(polyline$index);
        """.trimIndent()
    }.joinToString("\n\n")
}

private fun colorToHex(color: androidx.compose.ui.graphics.Color): String {
    val red = (color.red * 255).toInt()
    val green = (color.green * 255).toInt()
    val blue = (color.blue * 255).toInt()
    return String.format("#%02X%02X%02X", red, green, blue)
}
