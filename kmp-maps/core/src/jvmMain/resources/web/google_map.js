let map;
let markers = [];

let jsCircles = [];
let jsPolygons = [];
let jsPolylines = [];

let markerCluster;
let trafficLayer = null;
let AdvancedMarkerElement;
let PinElement;

const clusterCache = new Map();

async function initMap() {
    const { Map } = await google.maps.importLibrary("maps");
    const { AdvancedMarkerElement: MarkerClass, PinElement: PinClass } = await google.maps.importLibrary("marker");
    
    AdvancedMarkerElement = MarkerClass;
    PinElement = PinClass;

    map = new Map(document.getElementById("map"), {
        mapId: "{{INITIAL_MAP_ID}}",
        center: {
            lat: {{INITIAL_LAT}},
            lng: {{INITIAL_LNG}}
        },
        zoom: {{INITIAL_ZOOM}},
        disableDefaultUI: true,
    });

    if (window.markerClusterer) {
        markerCluster = new markerClusterer.MarkerClusterer({ map, markers: [] });
    }

    google.maps.event.addListenerOnce(map, 'tilesloaded', function() {
        window.kmpJsBridge.callNative("onMapLoaded", "{}");
    });

    map.addListener("click", (e) => {
        if (e.placeId) {
            window.kmpJsBridge.callNative("onPOIClick", JSON.stringify({
                latitude: e.latLng.lat(),
                longitude: e.latLng.lng()
            }));
        } else {
            window.kmpJsBridge.callNative("onMapClick", JSON.stringify({
                latitude: e.latLng.lat(),
                longitude: e.latLng.lng()
            }));
        }
    });

    map.addListener("idle", () => {
        const center = map.getCenter();
        const zoom = map.getZoom();

        if (center) {
            const cameraState = {
                coordinates: {
                    latitude: center.lat(),
                    longitude: center.lng(),
                },
                zoom
            };

            if (window.kmpJsBridge) {
                window.kmpJsBridge.callNative(
                    "onCameraMove",
                    JSON.stringify(cameraState)
                );
            }
        }
    });
}

function updateMarkers(data, clusterEnabled, hasCustomClusterContent) {
    if (!map || !AdvancedMarkerElement) {
        console.warn("Map not ready yet.");
        return;
    }

    clearMarkers();
    clusterCache.clear();

    try {
        const newMarkers = [];

        data.forEach(item => {
            let contentNode = null;
            if (item.renderedHtml) {
                const div = document.createElement('div');
                div.innerHTML = item.renderedHtml;
                div.className = `kmp-marker-wrapper ${item.contentId || ''}`;

                contentNode = div;
            }

            const marker = new AdvancedMarkerElement({
                map,
                position: item.position,
                title: item.title || "",
                content: contentNode
            });

            marker._kmpData = {
                id: item.id,
                title: item.title,
                coordinates: {
                    latitude: item.position.lat,
                    longitude: item.position.lng
                },
                contentId: item.contentId,
            };

            marker.addListener("click", () => {
                sendToKotlin("onMarkerClick", String(marker._kmpData.id));
            });

            newMarkers.push(marker);
        });

        markers = newMarkers;

        if (clusterEnabled && markerCluster) {
            markerCluster.clearMarkers();

            if (hasCustomClusterContent) {
                markerCluster.renderer = {
                    render: (cluster) => {
                        const clusterId = `cluster-${cluster.position.lat()}-${cluster.position.lng()}-${cluster.count}`;
                        const container = document.createElement("div");
                        container.id = clusterId;

                        if (clusterCache.has(clusterId)) {
                            container.innerHTML = clusterCache.get(clusterId);
                        } else {
                            container.innerHTML = `<div style="background: #4285F4; color: white; border-radius: 50%; width: 30px; height: 30px; display: flex; align-items: center; justify-content: center;">${cluster.count}</div>`;

                            const clusterParams = {
                                id: clusterId,
                                coordinates: { latitude: cluster.position.lat(), longitude: cluster.position.lng() },
                                size: cluster.count,
                                items: cluster.markers.map(m => m._kmpData)
                            };
                            sendToKotlin("renderCluster", JSON.stringify(clusterParams));
                        }

                        return new AdvancedMarkerElement({
                            position: cluster.position,
                            content: container
                        });
                    }
                };
            }

            markerCluster.addMarkers(markers);

            google.maps.event.clearListeners(markerCluster, "click");

            google.maps.event.addListener(markerCluster, "click", (cluster) => {
                const mappedItems = cluster.markers.map(m => m._kmpData);

                const clusterInfo = {
                    coordinates: {
                        latitude: cluster.position.lat(),
                        longitude: cluster.position.lng()
                    },
                    size: cluster.count,
                    items: mappedItems
                };

                sendToKotlin("onClusterClick", JSON.stringify(clusterInfo));
            });
        }
    } catch (e) {
        console.error("Error during updateMarkers:", e);
    }
}

function applyClusterHtml(clusterId, html) {
    clusterCache.set(clusterId, html);
    const element = document.getElementById(clusterId);
    if (element) {
        element.innerHTML = html;
    }
}

function updateMapProperties(props) {
    if (!map) return;

    if (props.isTrafficEnabled) {
        if (!trafficLayer) {
            trafficLayer = new google.maps.TrafficLayer();
        }
        trafficLayer.setMap(map);
    } else {
        if (trafficLayer) {
            trafficLayer.setMap(null);
        }
    }

    let typeId;
    switch (props.mapType) {
        case "SATELLITE": typeId = google.maps.MapTypeId.SATELLITE; break;
        case "TERRAIN": typeId = google.maps.MapTypeId.TERRAIN; break;
        case "HYBRID": typeId = google.maps.MapTypeId.HYBRID; break;
        case "NORMAL": default: typeId = google.maps.MapTypeId.ROADMAP; break;
    }
    map.setMapTypeId(typeId);

    const web = props.web;
    if (web) {
        const options = {
            gestureHandling: web.gestureHandling,
            disableDoubleClickZoom: web.disableDoubleClickZoom,
            keyboardShortcuts: web.keyboardShortcuts,
            clickableIcons: web.clickableIcons,
        };

        if (web.mapId !== null) options.mapId = web.mapId;
        if (web.minZoom !== null) options.minZoom = web.minZoom;
        if (web.maxZoom !== null) options.maxZoom = web.maxZoom;
        if (web.restriction !== null) options.restriction = web.restriction;
        if (web.backgroundColor !== null) options.backgroundColor = web.backgroundColor;
        if (web.styles !== null) options.styles = web.styles;

        map.setOptions(options);
    }
}

function updateMapUISettings(settings) {
    if (!map) return;

    const web = settings.web;

    const options = {
        disableDefaultUI: web.disableDefaultUI,
        draggable: settings.scrollEnabled,
        scrollwheel: settings.zoomEnabled,
    };

    if (web) {
        options.disableDefaultUI = web.disableDefaultUI;

        if (!web.disableDefaultUI) {
            const getPos = (posName) => {
                if (!posName) return null;
                return { position: google.maps.ControlPosition[posName] };
            };

            options.zoomControl = web.zoomControl;
            if (web.zoomControl && web.zoomControlPosition) {
                options.zoomControlOptions = getPos(web.zoomControlPosition);
            }

            options.mapTypeControl = web.mapTypeControl;
            if (web.mapTypeControl && web.mapTypeControlPosition) {
                options.mapTypeControlOptions = getPos(web.mapTypeControlPosition);
            }

            options.streetViewControl = web.streetViewControl;
            if (web.streetViewControl && web.streetViewControlPosition) {
                options.streetViewControlOptions = getPos(web.streetViewControlPosition);
            }

            options.rotateControl = web.rotateControl;
            if (web.rotateControl && web.rotateControlPosition) {
                options.rotateControlOptions = getPos(web.rotateControlPosition);
            }

            options.fullscreenControl = web.fullscreenControl;
        }
    } else {
        options.zoomControl = settings.zoomEnabled;
    }

    map.setOptions(options);
}

function updateCircles(jsonString) {
    if (!map) return;

    jsCircles.forEach(c => c.setMap(null));
    jsCircles = [];

    try {
        const data = JSON.parse(jsonString);
        data.forEach((item) => {
            const circle = new google.maps.Circle({
                map: map,
                center: item.center,
                radius: item.radius,
                fillColor: item.fillColor || "#000000",
                fillOpacity: item.fillOpacity !== undefined ? item.fillOpacity : 0.0,
                strokeColor: item.strokeColor || "#000000",
                strokeOpacity: item.strokeOpacity !== undefined ? item.strokeOpacity : 1.0,
                strokeWeight: item.strokeWeight || 1,
                clickable: true,
                zIndex: 2
            });

            circle.addListener("click", () => {
                window.kmpJsBridge.callNative("onCircleClick", String(item.id));
            });

            jsCircles.push(circle);
        });
    } catch (e) { console.error("Error updating circles:", e); }
}

function updatePolygons(jsonString) {
    if (!map) return;

    jsPolygons.forEach(p => p.setMap(null));
    jsPolygons = [];

    try {
        const data = JSON.parse(jsonString);
        data.forEach((item) => {
            const polygon = new google.maps.Polygon({
                map: map,
                paths: item.paths,
                fillColor: item.fillColor || "#000000",
                fillOpacity: item.fillOpacity !== undefined ? item.fillOpacity : 0.0,
                strokeColor: item.strokeColor || "#000000",
                strokeOpacity: item.strokeOpacity !== undefined ? item.strokeOpacity : 1.0,
                strokeWeight: item.strokeWeight || 1,
                clickable: true,
                zIndex: 2
            });

            polygon.addListener("click", () => {
                window.kmpJsBridge.callNative("onPolygonClick", String(item.id));
            });

            jsPolygons.push(polygon);
        });
    } catch (e) { console.error("Error updating polygons:", e); }
}


function updatePolylines(jsonString) {
    if (!map) return;

    jsPolylines.forEach(p => p.setMap(null));
    jsPolylines = [];

    try {
        const data = JSON.parse(jsonString);
        data.forEach((item) => {
            const polyline = new google.maps.Polyline({
                map: map,
                path: item.path,
                strokeColor: item.strokeColor || "#000000",
                strokeOpacity: item.strokeOpacity !== undefined ? item.strokeOpacity : 1.0,
                strokeWeight: item.strokeWeight || 1,
                clickable: true,
                zIndex: 3
            });

            polyline.addListener("click", () => {
                window.kmpJsBridge.callNative("onPolylineClick", String(item.id));
            });

            jsPolylines.push(polyline);
        });
    } catch (e) { console.error("Error updating polylines:", e); }
}

function clearMarkers() {
    if (markerCluster) {
        markerCluster.clearMarkers();
    }
    markers.forEach(m => m.setMap(null));
    markers = [];
}

function sendToKotlin(method, data) {
    if (window.kmpJsBridge?.callNative) {
        window.kmpJsBridge.callNative(method, data);
    } else {
        console.error(`Bridge not found for method: ${method}`);
    }
}
