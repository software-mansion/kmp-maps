let map;

function waitForMapKit() {
    if (typeof mapkit !== "undefined") {
        initMap();
    } else {
        setTimeout(waitForMapKit, 50);
    }
}

waitForMapKit();

function initMap() {
    mapkit.init({
        authorizationCallback: function (done) {
            done("{{API_KEY}}");
        }
    });

    map = new mapkit.Map("map");
}
