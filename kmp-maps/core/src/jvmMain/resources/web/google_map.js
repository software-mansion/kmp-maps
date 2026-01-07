let map;
let markers = [];
let markerCluster;
let AdvancedMarkerElement;
let PinElement;

async function initMap() {
    const { Map } = await google.maps.importLibrary("maps");
    const { AdvancedMarkerElement: MarkerClass, PinElement: PinClass } = await google.maps.importLibrary("marker");
    
    AdvancedMarkerElement = MarkerClass;
    PinElement = PinClass;

    map = new Map(document.getElementById("map"), {
        mapId: "KMP_MAPS",
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
            e.stop();

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
