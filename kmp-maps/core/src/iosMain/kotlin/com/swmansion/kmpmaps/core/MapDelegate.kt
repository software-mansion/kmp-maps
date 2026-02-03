package com.swmansion.kmpmaps.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCAction
import kotlinx.cinterop.useContents
import platform.MapKit.MKAnnotationProtocol
import platform.MapKit.MKAnnotationView
import platform.MapKit.MKCircle
import platform.MapKit.MKCircleRenderer
import platform.MapKit.MKClusterAnnotation
import platform.MapKit.MKMapView
import platform.MapKit.MKMapViewDelegateProtocol
import platform.MapKit.MKMarkerAnnotationView
import platform.MapKit.MKMultiPolygon
import platform.MapKit.MKMultiPolygonRenderer
import platform.MapKit.MKMultiPolyline
import platform.MapKit.MKMultiPolylineRenderer
import platform.MapKit.MKOverlayProtocol
import platform.MapKit.MKOverlayRenderer
import platform.MapKit.MKPointAnnotation
import platform.MapKit.MKPolygon
import platform.MapKit.MKPolygonRenderer
import platform.MapKit.MKPolyline
import platform.MapKit.MKPolylineRenderer
import platform.MapKit.MKUserLocation
import platform.UIKit.UIColor
import platform.UIKit.UIGestureRecognizerStateBegan
import platform.UIKit.UIImage
import platform.UIKit.UILongPressGestureRecognizer
import platform.UIKit.UITapGestureRecognizer
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

/** iOS map delegate for handling Apple Maps interactions and rendering. */
@OptIn(ExperimentalForeignApi::class)
internal class MapDelegate(
    private val mapView: MKMapView,
    private val imageCache: MutableMap<String, UIImage> = mutableMapOf(),
    val clustersToRender: MutableMap<String, Cluster> = mutableStateMapOf(),
    private val properties: MapProperties,
    private val circleStyles: MutableMap<MKCircle, Circle>,
    private val polygonStyles: MutableMap<MKPolygon, Polygon>,
    private val polylineStyles: MutableMap<MKPolyline, Polyline>,
    private val markerMapping: MutableMap<MKAnnotationProtocol, Marker>,
    private var onMarkerClick: ((Marker) -> Unit)?,
    private var onCircleClick: ((Circle) -> Unit)?,
    private var onPolygonClick: ((Polygon) -> Unit)?,
    private var onPolylineClick: ((Polyline) -> Unit)?,
    private var onMapClick: ((Coordinates) -> Unit)?,
    private var onMapLongClick: ((Coordinates) -> Unit)?,
    private var onPOIClick: ((Coordinates) -> Unit)?,
    private var onCameraMove: ((CameraPosition) -> Unit)?,
    private val geoJsonPolygonStyles: MutableMap<MKOverlayProtocol, AppleMapsGeoJsonPolygonStyle>,
    private val geoJsonPolylineStyles: MutableMap<MKOverlayProtocol, AppleMapsGeoJsonLineStyle>,
    private val geoJsonPointStyles: MutableMap<MKPointAnnotation, AppleMapsGeoJsonPointStyle>,
    private val customMarkerContent: Map<String, @Composable (Marker) -> Unit>,
    private val clusterSettings: ClusterSettings,
) : NSObject(), MKMapViewDelegateProtocol {

    /**
     * Provides renderers for map overlays (circles, polygons, polylines).
     *
     * @param mapView The map view requesting the renderer
     * @param rendererForOverlay The overlay that needs a renderer
     * @return Appropriate renderer with styling applied
     */
    override fun mapView(mapView: MKMapView, rendererForOverlay: MKOverlayProtocol) =
        when (rendererForOverlay) {
            is MKCircle -> {
                val circleStyle = circleStyles[rendererForOverlay]
                val renderer = MKCircleRenderer(rendererForOverlay)
                renderer.strokeColor =
                    circleStyle?.lineColor?.toAppleMapsColor() ?: UIColor.blackColor
                renderer.lineWidth = (circleStyle?.lineWidth ?: DEFAULT_STROKE_WIDTH).toDouble()
                renderer.fillColor = circleStyle?.color?.toAppleMapsColor()
                renderer
            }
            is MKPolygon,
            is MKMultiPolygon -> {
                val core = (rendererForOverlay as? MKPolygon)?.let { polygonStyles[it] }
                val renderer =
                    if (rendererForOverlay is MKMultiPolygon) {
                        MKMultiPolygonRenderer(rendererForOverlay)
                    } else {
                        MKPolygonRenderer(rendererForOverlay as MKPolygon)
                    }

                if (core != null) {
                    renderer.strokeColor = core.lineColor?.toAppleMapsColor() ?: UIColor.blackColor
                    renderer.lineWidth = core.lineWidth.toDouble()
                    renderer.fillColor = core.color?.toAppleMapsColor()
                } else {
                    val gj = geoJsonPolygonStyles[rendererForOverlay]
                    if (gj != null) {
                        renderer.strokeColor = gj.strokeColor
                        renderer.lineWidth = gj.strokeWidth
                        renderer.fillColor = gj.fillColor
                    } else {
                        renderer.strokeColor = UIColor.blackColor
                        renderer.lineWidth = DEFAULT_STROKE_WIDTH.toDouble()
                        renderer.fillColor = null
                    }
                }
                renderer
            }
            is MKPolyline,
            is MKMultiPolyline -> {
                val core = (rendererForOverlay as? MKPolyline)?.let { polylineStyles[it] }
                val renderer =
                    if (rendererForOverlay is MKMultiPolyline) {
                        MKMultiPolylineRenderer(rendererForOverlay)
                    } else {
                        MKPolylineRenderer(rendererForOverlay as MKPolyline)
                    }

                if (core != null) {
                    renderer.strokeColor = core.lineColor?.toAppleMapsColor() ?: UIColor.blackColor
                    renderer.lineWidth = core.width.toDouble()
                } else {
                    val gj = geoJsonPolylineStyles[rendererForOverlay]
                    if (gj != null) {
                        renderer.strokeColor = gj.color
                        renderer.lineWidth = gj.width
                    } else {
                        renderer.strokeColor = UIColor.blackColor
                        renderer.lineWidth = DEFAULT_STROKE_WIDTH.toDouble()
                    }
                }
                renderer
            }
            else -> MKOverlayRenderer(rendererForOverlay)
        }

    /**
     * Handles marker selection events when user taps on annotations.
     *
     * @param mapView The map view containing the annotation
     * @param didSelectAnnotationView The annotation view that was selected
     */
    override fun mapView(mapView: MKMapView, didSelectAnnotationView: MKAnnotationView) {
        val annotation = didSelectAnnotationView.annotation ?: return

        when (annotation) {
            is MKClusterAnnotation -> {
                val memberAnnotations = annotation.memberAnnotations

                val markers =
                    memberAnnotations
                        .filterIsInstance<MKPointAnnotation>()
                        .mapNotNull(markerMapping::get)

                if (markers.isNotEmpty()) {
                    val clusterCoordinate =
                        annotation.coordinate.useContents { Coordinates(latitude, longitude) }

                    val cluster =
                        Cluster(
                            coordinates = clusterCoordinate,
                            size = markers.size,
                            items = markers,
                        )

                    val consumed = clusterSettings.onClusterClick?.invoke(cluster) ?: false

                    if (!consumed) mapView.showAnnotations(memberAnnotations, animated = true)
                }
                mapView.deselectAnnotation(annotation, animated = false)
            }
            is MKPointAnnotation ->
                markerMapping[annotation]?.let { marker -> onMarkerClick?.invoke(marker) }
        }
    }

    /**
     * Handles camera movement events when the map region changes.
     *
     * @param mapView The map view whose region changed
     * @param regionDidChangeAnimated Whether the change was animated
     */
    override fun mapView(mapView: MKMapView, regionDidChangeAnimated: Boolean) {
        val region = mapView.region
        val cameraPosition = region.toCameraPosition()
        onCameraMove?.invoke(cameraPosition)
    }

    /**
     * Handles GeoJSON annotations and custom markers.
     *
     * @param mapView The map view whose region changed
     * @param viewForAnnotation The annotation view that was selected
     */
    override fun mapView(mapView: MKMapView, viewForAnnotation: MKAnnotationProtocol) =
        when (viewForAnnotation) {
            is MKUserLocation -> null
            is MKClusterAnnotation -> createClusterView(mapView, viewForAnnotation)
            is MKPointAnnotation -> {
                val marker = markerMapping[viewForAnnotation]
                when {
                    marker?.contentId != null &&
                        customMarkerContent.containsKey(marker.contentId) ->
                        createCustomMarkerView(mapView, viewForAnnotation, marker)
                    marker != null -> createStandardMarkerView(mapView, viewForAnnotation, marker)
                    else -> createGeoJsonMarkerView(mapView, viewForAnnotation)
                }
            }
            else -> null
        }

    /**
     * Handles tap gestures on the map to detect clicks on overlays and map.
     *
     * @param gestureRecognizer The tap gesture recognizer
     */
    @ObjCAction
    @OptIn(BetaInteropApi::class)
    fun handleMapTap(gestureRecognizer: UITapGestureRecognizer) {
        val mapView = gestureRecognizer.view as? MKMapView ?: return
        val tapPoint = gestureRecognizer.locationInView(mapView)
        val coordinate = mapView.convertPoint(tapPoint, toCoordinateFromView = mapView)

        coordinate.useContents {
            val tapLat = latitude
            val tapLon = longitude
            val tapCoord = Coordinates(tapLat, tapLon)

            for (mapCircle in circleStyles.values) {
                if (isPointInCircle(tapLat, tapLon, mapCircle)) {
                    onCircleClick?.invoke(mapCircle)
                    return@useContents
                }
            }

            for (mapPolygon in polygonStyles.values) {
                if (isPointInPolygon(tapLat, tapLon, mapPolygon)) {
                    onPolygonClick?.invoke(mapPolygon)
                    return@useContents
                }
            }

            for (mapPolyline in polylineStyles.values) {
                if (
                    isPointNearPolyline(
                        tapLat,
                        tapLon,
                        properties.iosMapProperties.polylineTapThreshold,
                        mapPolyline,
                    )
                ) {
                    onPolylineClick?.invoke(mapPolyline)
                    return@useContents
                }
            }

            onMapClick?.invoke(tapCoord)
        }
    }

    /**
     * Handles long press gestures on the map.
     *
     * @param gestureRecognizer The long press gesture recognizer
     */
    @ObjCAction
    @OptIn(BetaInteropApi::class)
    fun handleMapLongPress(gestureRecognizer: UILongPressGestureRecognizer) {
        if (gestureRecognizer.state == UIGestureRecognizerStateBegan) {
            val mapView = gestureRecognizer.view as? MKMapView ?: return
            val longPressPoint = gestureRecognizer.locationInView(mapView)
            val coordinate = mapView.convertPoint(longPressPoint, toCoordinateFromView = mapView)

            coordinate.useContents {
                val coordinates = Coordinates(latitude, longitude)
                onMapLongClick?.invoke(coordinates)
            }
        }
    }

    fun onBitmapReady(id: String, image: UIImage) {
        dispatch_async(dispatch_get_main_queue()) {
            imageCache[id] = image
            clustersToRender.remove(id)

            val annotation = findAnnotationById(id)
            if (annotation != null) {
                (mapView.viewForAnnotation(annotation) as? CustomMarkers)?.setMarkerImage(image)
            }
        }
    }

    fun pruneCache(activeIds: Set<String>) {
        val keysToRemove = imageCache.keys.filter { it !in activeIds }
        keysToRemove.forEach { imageCache.remove(it) }
    }

    fun getCachedImage(id: String?): UIImage? = imageCache[id ?: ""]

    private fun queueClusterRender(id: String, cluster: Cluster) {
        if (!imageCache.containsKey(id) && !clustersToRender.containsKey(id)) {
            clustersToRender[id] = cluster
        }
    }

    private fun findAnnotationById(id: String): MKAnnotationProtocol? {
        val markerAnn = markerMapping.entries.find { it.value.id == id }?.key
        if (markerAnn != null) return markerAnn

        return mapView.annotations.filterIsInstance<MKClusterAnnotation>().find { clusterAnn ->
            val markers =
                clusterAnn.memberAnnotations.filterIsInstance<MKPointAnnotation>().mapNotNull {
                    markerMapping[it]
                }
            generateClusterId(markers) == id
        }
    }

    private fun createClusterView(
        mapView: MKMapView,
        annotation: MKClusterAnnotation,
    ): MKAnnotationView? {
        if (clusterSettings.clusterContent == null) return null
        val reuseId = "kmp_cluster_view"
        val view =
            mapView.dequeueReusableAnnotationViewWithIdentifier(reuseId) as? CustomMarkers
                ?: CustomMarkers(annotation = annotation, reuseIdentifier = reuseId)

        view.setMarkerImage(null)
        view.annotation = annotation

        val markers =
            annotation.memberAnnotations.filterIsInstance<MKPointAnnotation>().mapNotNull {
                markerMapping[it]
            }

        val clusterId = generateClusterId(markers)
        val cluster =
            Cluster(
                coordinates =
                    annotation.coordinate.useContents { Coordinates(latitude, longitude) },
                size = markers.size,
                items = markers,
            )

        val cached = imageCache[clusterId]
        if (cached != null) {
            view.setMarkerImage(cached)
        } else {
            queueClusterRender(clusterId, cluster)
        }
        return view
    }

    private fun createCustomMarkerView(
        mapView: MKMapView,
        annotation: MKPointAnnotation,
        marker: Marker,
    ): MKAnnotationView {
        val reuseId = "kmp_custom_marker_${marker.contentId}"
        val view =
            mapView.dequeueReusableAnnotationViewWithIdentifier(reuseId) as? CustomMarkers
                ?: CustomMarkers(annotation = annotation, reuseIdentifier = reuseId)

        view.annotation = annotation
        view.clusteringIdentifier =
            if (clusterSettings.enabled) "kmp_marker_cluster_group" else null
        view.setMarkerImage(imageCache[marker.id])

        return view
    }

    private fun createStandardMarkerView(
        mapView: MKMapView,
        annotation: MKPointAnnotation,
        marker: Marker,
    ): MKAnnotationView {
        val reuseId = "kmp_standard_marker"
        val view =
            (mapView.dequeueReusableAnnotationViewWithIdentifier(reuseId)
                    as? MKMarkerAnnotationView)
                ?.apply { this.annotation = annotation }
                ?: MKMarkerAnnotationView(annotation = annotation, reuseIdentifier = reuseId)

        view.canShowCallout = true
        view.markerTintColor = marker.iosMarkerOptions?.tintColor?.toAppleMapsColor()
        view.clusteringIdentifier =
            if (clusterSettings.enabled) "kmp_marker_cluster_group" else null

        return view
    }

    private fun createGeoJsonMarkerView(
        mapView: MKMapView,
        annotation: MKPointAnnotation,
    ): MKAnnotationView? {
        val style = geoJsonPointStyles[annotation] ?: return null
        val reuseId = "kmp_geojson_marker"
        val view =
            (mapView.dequeueReusableAnnotationViewWithIdentifier(reuseId)
                as? MKMarkerAnnotationView)
                ?: MKMarkerAnnotationView(annotation = annotation, reuseIdentifier = reuseId)

        view.annotation = annotation
        view.canShowCallout = true
        view.hidden = !style.visible
        return view
    }
}
