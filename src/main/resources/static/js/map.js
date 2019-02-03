var mymap = L.map('mapid').setView([-22.2315237,-45.9386988], 2000);
L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png?{foo}', {foo: 'bar', attribution: 'Map data &copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors, <a href="https://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>'}).addTo(mymap);

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
		
		var polygonsCoordinate = [];
		var polygonsIndex = 0;
		for (var coordinatesMapKey in coordinatesMap) {
			polygonsCoordinate[polygonsIndex] = coordinatesMap[coordinatesMapKey];
			polygonsIndex++;
		}
		
		var polygon = L.polygon(polygonsCoordinate).addTo(mymap);
		//polygon.bindPopup("I am a polygon.");
	}
});

