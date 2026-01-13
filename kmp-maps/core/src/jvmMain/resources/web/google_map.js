let map;
let markers = [];
let markerCluster;
let trafficLayer = null;
let AdvancedMarkerElement;
let PinElement;

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

function updateMarkers(jsonString, clusterEnabled) {
    if (!map || !AdvancedMarkerElement) {
        console.warn("Map not ready yet.");
        return;
    }

    clearMarkers();

    try {
        const data = JSON.parse(jsonString);
        const newMarkers = [];

        data.forEach(item => {
            const marker = new AdvancedMarkerElement({
                map,
                position: item.position,
                title: item.title || "",
            });

            marker.addListener("click", () => {
                sendToKotlin("onMarkerClick", String(item.id));
            });

            newMarkers.push(marker);
        });

        markers = newMarkers;

        if (clusterEnabled && markerCluster) {
            markerCluster.clearMarkers();
            markerCluster.addMarkers(markers);
        }
    } catch (e) {
        console.error("Error during updateMarkers:", e);
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
