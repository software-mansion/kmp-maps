let map;
let markers = [];
let clusterManager = null;

function initMap() {
    map = new google.maps.Map(document.getElementById("map"), {
        center: { lat: 50.0619, lng: 19.9373 },
        zoom: 13,
        disableDefaultUI: true,
    });

    map.addListener("click", (e) => {
        sendToKotlin("onMapClick", JSON.stringify({latitude: e.latLng.lat(), longitude: e.latLng.lng()}));
    });
}

function updateMarkers(json, clusteringEnabled) {
    clearMarkers();

    const data = JSON.parse(json);

    markers = data.map(m => {
        const marker = new google.maps.Marker({
            position: m.position,
            title: m.title,
            map: clusteringEnabled ? null : map
        });

        return marker;
    });

    if (clusteringEnabled && typeof markerClusterer !== 'undefined') {
        clusterManager = new markerClusterer.MarkerClusterer({ map, markers });
    }
}

function clearMarkers() {
    if (clusterManager) {
        clusterManager.clearMarkers();
        clusterManager = null;
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
