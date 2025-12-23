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
        center: { lat: 50.0619, lng: 19.9373 },
        zoom: 13,
        disableDefaultUI: true,
    });

    if (window.markerClusterer) {
        markerCluster = new markerClusterer.MarkerClusterer({ map, markers: [] });
    }

    map.addListener("click", (e) => {
        sendToKotlin("onMapClick", JSON.stringify({latitude: e.latLng.lat(), longitude: e.latLng.lng()}));
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
                map: map,
                position: item.position,
                title: item.title || "",
            });

            marker.addListener("click", () => {
                sendToKotlin("onMarkerClick", "{}");
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
    if (window.kmpJsBridge && window.kmpJsBridge.callNative) {
            window.kmpJsBridge.callNative(method, data);
    } else {
        console.error("Bridge not found for method: " + method);
    }
}
