function initMap() {
    try {
        new google.maps.Map(document.getElementById("map"), {
            center: { lat: 52.2297, lng: 21.0122 },
            zoom: 12,
            renderingType: google.maps.RenderingType.RASTER,
            mapTypeId: google.maps.MapTypeId.ROADMAP
        });
    } catch (e) {
        document.body.innerHTML = "ERR: " + e;
    }
}
