var mymap;
initMap();

function onEachFeature(feature, layer) {
    if (feature.properties && feature.properties.farmId) {
        layer.on('click', function (e) {
        	console.log("You clicked the map at " + e.latlng);
          });
    }
}

function initMap() {
	mymap = L.map('mapid').setView([-51.271984, -14.690660], 5);
	L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png?{foo}', {foo: 'bar', attribution: 'Map data &copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors, <a href="https://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>'}).addTo(mymap);

}

$.ajax({
    method: "GET",
    url: "/coordinates"
}).done(function (coordinates, status) {
	
	if (status == "success") {
		var coordinatesMap = [];
		var coordinatesLength = coordinates.length;
		for (var coordinatesIndex = 0; coordinatesIndex < coordinatesLength; coordinatesIndex++) {	
			var coordinatesInfo = coordinates[coordinatesIndex];
			var latitude = coordinatesInfo[0];
			var longitude = coordinatesInfo[1];
			var farmId = coordinatesInfo[2];
			var coordinatesPair = [];
			coordinatesPair.push(latitude);
			coordinatesPair.push(longitude);
			
			if (coordinatesMap[farmId] == null)
				coordinatesMap[farmId] = [];
			coordinatesMap[farmId].push(coordinatesPair);
		}
		
		var polygonsIndex = 0;
		for (var coordinatesMapKey in coordinatesMap) {
			var polygonsCoordinate = coordinatesMap[coordinatesMapKey];
			var polygon = L.polygon(polygonsCoordinate);
			var geoJson = polygon.toGeoJSON();
			geoJson.properties.farmId = coordinatesMapKey;
			L.geoJSON(geoJson, {onEachFeature: onEachFeature}).addTo(mymap);
		}
		
		//var polygon = L.polygon(polygonsCoordinate).addTo(mymap);
		//polygon.bindPopup("I am a polygon.");
	}
});

