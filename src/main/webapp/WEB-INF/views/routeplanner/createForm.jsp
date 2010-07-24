<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
<style type="text/css">

@import "scripts/all.css"; /* just some basic formatting, no layout stuff */
/* @import "form.css"; css3 form */

</style>
<title>Create Account</title>


<script type="text/javascript"
	src='<c:url value="http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js" /> '></script>
<script type="text/javascript"
	src='<c:url value="/scripts/json.min.js" /> '></script>
<script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=false"></script>
<script>



var map;
var first;
var lat1;
var lng1;
var lat2;
var lng2;
var overlay;
var poly;
var geocoder;
var bounds;
function initialize() {


  var myLatlng = new google.maps.LatLng(53.350012,-6.260653 );
  var myOptions = {
    zoom:12,
    center: myLatlng,
    mapTypeId: google.maps.MapTypeId.ROADMAP
  }
  map = new google.maps.Map(document.getElementById("map"), myOptions);
  geocoder = new google.maps.Geocoder(); 
  bounds = new google.maps.LatLngBounds();


  first=new Boolean(true);
  google.maps.event.addListener(map, 'click', function(event) {
   placeMarker(event.latLng);
// addLatLng(event.latLng);
   
  });
}

function placeMarker(location) {

	  var clickedLocation = new google.maps.LatLng(location);
	
	if(first)
	{
		var image = 'images/start.png';
		var marker = new google.maps.Marker({
		      position: location, 
		      map: map,
		      icon: image
		      
		  });
			
	     lat1 = location.lat();
	     lng1 = location.lng();

		first = false;
	}
	else if (!first)
	{
		//var image = 'images/end.png';		
		var marker = new google.maps.Marker({
		      position: location, 
		      map: map
		      ,icon: image
		      
		  });
		first = true;
		 // initial load points

		 lat2 = location.lat();
	     lng2 = location.lng();
	     checkAvailability();
	   

	}


//map.setCenter(location);
}


function addLocation(location) {
	 //var location = new google.maps.LatLng(latlng);
	 var latlng = new  google.maps.LatLng(location.latlng.lat, location.latlng.lng); 
	 bounds.extend(latlng);
	 map.fitBounds(bounds);
    //map.setCenter(location2);
    var infohtml= '<br><b>Bus:</b>' + location.name + '<br><b>Address:</b>' + location.address + '<br><b>Time:</b>' + location.time;
    
    var infowindow = new google.maps.InfoWindow({
    content: infohtml
	});


    var busIcon = 'images/bus.png';
    var marker = new google.maps.Marker({
        map: map, 
        position: latlng,
        title:"Hello World!",
        icon: busIcon          
    });

    google.maps.event.addListener(marker, 'click', function() {
    	  infowindow.open(map,marker);
    	});
	
    var sidebarhtml= '<b>Bus:</b>' + location.name + '<br><b>Time:</b>' + location.time;


    $("<li />")
	.html(sidebarhtml)
	.click(function(){
	    google.maps.event.trigger(marker, 'click');
		
	})
	.appendTo("#list");
}

function checkAvailability() {
	// initial load points
  
	$.getJSON("routeplanner/availability", { "lat1": lat1 , "lng1": lng1 , "lat2": lat2 , "lng2": lng2 }, function(json) {
		  $("#list").empty();
		processJson(json);
	});		 
}
function processJson(json)
{
	bounds = new google.maps.LatLngBounds();
	if(json.markers!=null)
	{
	if (json.markers.markerList.length > 0) {
		for (i=0; i<json.markers.markerList.length; i++) {
			var location = json.markers.markerList[i];		
			addLocation(location);
		}	
		//zoomToBounds();
	} 
		if (json.polylines.polylineList.length > 0) {
		for (i=0; i<json.polylines.polylineList.length; i++) {

			//each polyline
			var polyline = json.polylines.polylineList[i];

			var pathCoordinates = new google.maps.MVCArray();
			for (x=0; x<polyline.latlngList.length; x++) {
			     // each coordinate is put into a LatLng. 
			     var latlng = new  google.maps.LatLng(polyline.latlngList[x]['lat'],polyline.latlngList[x]['lng']); 
			     pathCoordinates.insertAt(x,latlng); 
				 bounds.extend(latlng);
				 map.fitBounds(bounds);
			}

			   var polyOptions = { 
				     path: pathCoordinates, 
				     strokeColor: polyline.strokeColor, 
				     strokeOpacity: polyline.strokeOpacity, 
				     strokeWeight: polyline.strokeWeight
				   };
			 	poly= new google.maps.Polyline(polyOptions); 
			   poly.setMap(map); 
			

		
		//zoomToBounds();
	}


	}
	}
	else
	{
		alert("No Path Found");
	}
}




	function zoomToBounds() {
		map.setCenter(bounds.getCenter());
		map.setZoom(map.getBoundsZoomLevel(bounds)-1);
	}

/**
 * Handles click events on a map, and adds a new point to the Polyline.
 * @param {MouseEvent} mouseEvent
 */
function addLatLng(event) {

  var path = poly.getPath();

  // Because path is an MVCArray, we can simply append a new coordinate
  // and it will automatically appear
  path.push(event.latLng);

  // Add a new marker at the new plotted point on the polyline.
  var marker = new google.maps.Marker({
    position: event.latLng,
    title: '#' + path.getLength(),
    map: map
  });
}

// $("#add-point").submit(function(){
//		geoEncode();
//		return false;
//	});

 function codeAddress() {
    var startAddress = document.getElementById("startAddress").value + ", Ireland";
	var endAddress = document.getElementById("endAddress").value+ ", Ireland";

	if (geocoder) {

      geocoder.geocode( { 'address': startAddress}, function(results, status) {
        if (status == google.maps.GeocoderStatus.OK) {
            
			var myLatlng = new google.maps.LatLng(results[0].geometry.location.lat(),results[0].geometry.location.lng() );
			alert("myLatlng" + myLatlng);
			placeMarker(myLatlng);
  			lat1= results[0].geometry.location.lat();
  			lng1= results[0].geometry.location.lng();


             } else {
          alert("Geocode was not successful for the following reason: " + status);
        }
      });

      geocoder.geocode( { 'address': endAddress}, function(results, status) {
          if (status == google.maps.GeocoderStatus.OK) {

  			placeMarker(results[0].geometry.location);
  			lat2= results[0].geometry.location.lat();
  			lng2= results[0].geometry.location.lng();

               } else {
            alert("Geocode was not successful for the following reason: " + status);
          }
        });

		//alert("getJSON lat1: " + lat1 + "lng1" +lng1 + "lat2: " + lat2 + "lng2" +lng2);
		// checkAvailability();
    }
	else{	alert("else geocoder");}
  }


--></script>
</head>
<body onload="initialize()">

<div id="frame">
  <div id="contentheader">
    <h1><a href="www.transportdublin.ie"> TransportDublin.ie</a> <i>Beta</i></h1>
   <h3> Dublin Public Transport Route Planner Powered by <a href="http://www.neo4j.org" style="color:#0000cc">neo4j</a> and <a href="http://maps.google.com" style="color:#0000cc">Google Maps</a></h3>
  </div>
  <div id="contentleft">
    <h1>links</h1>
    <pre>&nbsp;</pre>
  </div>
  <div id="contentcenter">
  
  
  
    
    <p></p>

    <form>
	  <fieldset>
				<legend>
					 <b>Type</b> a start and end address below or <b>click</b> any two points on the map to generate a route 
		</legend>
				<label for="startAddress">
					Start
				  <input id="startAddress" name="startAddress" type="text" value="castleknock" />
				</label>
				<label for="endAddress">
					End
				  <input id="endAddress" name="endAddress" type="text" value="Glasnevin" />

				</label>
				<label for="time">
					Time
				  <input id="time" name="time" type="text" value="9:00am" READONLY/>

				</label>
		<input type="submit" value="Go" onClick="codeAddress()"/>
			</fieldset>
            </form>
    <div id="map" ></div>
    <pre>&nbsp;</pre>
  </div>
  <div id="contentright">
    <h1>Directions</h1>
      
   <ul id="list"></ul>
    <pre>&nbsp;</pre>
  </div>
  <br clear="all" />
  <!-- without this little <br /> NS6 and IE5PC do not stretch the frame div down to encopass the content DIVs -->
</div>
</body>
</html>