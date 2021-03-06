var mymap;
initMap();

function onEachFeature(feature, layer) {
        layer.on('click', function (e) {

            if (feature.properties && feature.properties.farmId) {
            	console.log("You clicked the map at " + e.latlng);
            	$.ajax({
            		method: "GET",
            		url: "/farms/farmById",
            		data: { 
            			id: feature.properties.farmId
            		},
            	}).done(function (farm, status) {
        		
            		if (status == "success") {
            			var payment = parseFloat(farm.paymentAmount).toFixed(2);
            			layer.bindPopup(farm.farmName + "<br>R$ " + payment).addTo(mymap).openPopup();
            		}
            	});
            }
        });
    
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
		
		var myStyle = {
			    "color": "#50c878"
			};
		for (var coordinatesMapKey in coordinatesMap) {
			var polygonsCoordinate = coordinatesMap[coordinatesMapKey];
			var polygon = L.polygon(polygonsCoordinate);
			var geoJson = polygon.toGeoJSON();
			geoJson.properties.farmId = coordinatesMapKey;
			L.geoJSON(geoJson, {onEachFeature: onEachFeature, style: myStyle}).addTo(mymap);
		}
	}
});