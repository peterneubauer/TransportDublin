<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<html>
	<head>
		<title>PHP, jQuery and Google Maps</title>
		<script type="text/javascript" src="http://www.google.com/jsapi?key=ABQIAAAAtZGzDIdFJqnl4cqEnOX0vhQHI-HVXygL19UYwv4GXiOQ40xaRRRaPN1yzf5IGf8M7yJuKXBH4aD-SA"></script>
		<script type="text/javascript">
			google.load("jquery", '1.2.6');
			google.load("maps", "2.x");
		</script>
		<style type="text/css" media="screen">
			#map { float:left; width:500px; height:500px; }
			#list { float:left; width:200px; background:#eee; list-style:none; padding:0; }
			#list li { padding:10px; }
			#list li:hover { background:#555; color:#fff; cursor:pointer; cursor:hand; }
			#message { background:#555; color:#fff; position:absolute; display:none; width:100px; padding:5px; }
			#add-point { float:left; }
			div.input { padding:3px 0; }
			label { display:block; font-size:80%; }
			input, select { width:150px; }
			button { float:right; }
			div.error { color:red; font-weight:bold; }
		</style>
		<script type="text/javascript" charset="utf-8">
			$(function(){
				var map = new GMap2(document.getElementById('map'));
				var burnsvilleMN = new GLatLng(53.367544,-6.359711);
				map.setCenter(burnsvilleMN, 8);
				var bounds = new GLatLngBounds();
				var geo = new GClientGeocoder(); 
				
				var reasons=[];
				reasons[G_GEO_SUCCESS]            = "Success";
				reasons[G_GEO_MISSING_ADDRESS]    = "Missing Address";
				reasons[G_GEO_UNKNOWN_ADDRESS]    = "Unknown Address.";
				reasons[G_GEO_UNAVAILABLE_ADDRESS]= "Unavailable Address";
				reasons[G_GEO_BAD_KEY]            = "Bad API Key";
				reasons[G_GEO_TOO_MANY_QUERIES]   = "Too Many Queries";
				reasons[G_GEO_SERVER_ERROR]       = "Server error";
				
				// initial load points
				$.getJSON("account/availability", { "lat1": lat1 , "lng1": lng1 , "lat2": lat2 , "lng2": lng2 }, function(json) {
					if (json.Locations.length > 0) {
						for (i=0; i<json.Locations.length; i++) {
							var location = json.Locations[i];
							addLocation(location);
						}
						zoomToBounds();
					}
				});

				
				function addLocation(location) {
					var point = new GLatLng(location.lat, location.lng);		
					var marker = new GMarker(point);
					map.addOverlay(marker);
					bounds.extend(marker.getPoint());
					
					$("<li />")
						.html(location.name)
						.click(function(){
							showMessage(marker, location.name);
						})
						.appendTo("#list");
					
					GEvent.addListener(marker, "click", function(){
						showMessage(this, location.name);
					});
				}
				
				function zoomToBounds() {
					map.setCenter(bounds.getCenter());
					map.setZoom(map.getBoundsZoomLevel(bounds)-1);
				}
				
				$("#message").appendTo( map.getPane(G_MAP_FLOAT_SHADOW_PANE) );
				
				function showMessage(marker, text){
					var markerOffset = map.fromLatLngToDivPixel(marker.getPoint());
					$("#message").hide().fadeIn()
						.css({ top:markerOffset.y, left:markerOffset.x })
						.html(text);
				}
			});
		</script>

	</head>
	<body>
		<form id="add-point" action="php/map-service.php" method="POST">
			<input type="hidden" name="action" value="savepoint" id="action">
			<fieldset>
				<legend>Add a Point to the Map</legend>
				<div class="error" style="display:none;"></div>
				<div class="input">

					<label for="name">Location Name</label>
					<input type="text" name="name" id="name" value="">
				</div>
				<div class="input">
					<label for="address">Address</label>
					<input type="text" name="address" id="address" value="">
				</div>
				<button type="submit">Add Point</button>

			</fieldset>
		</form>
		<div id="map"></div>
		<ul id="list"></ul>
		<div id="message"></div>
	</body>
</html>