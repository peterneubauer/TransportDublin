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
<script type="text/javascript" src='<c:url value="/scripts/json.min.js" /> '></script>
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
var bounds;
var geocoder;

function initialize() {

	
  geocoder = new google.maps.Geocoder();
  var myLatlng = new google.maps.LatLng(53.350012,-6.260653 );
  var myOptions = {
    zoom:12,
    center: myLatlng,
    mapTypeId: google.maps.MapTypeId.ROADMAP
  }
  	map = new google.maps.Map(document.getElementById("map"), myOptions);
  	bounds = new google.maps.LatLngBounds();


  	first=new Boolean(true);
  	google.maps.event.addListener(map, 'click', function(event) {
   	placeMarker(event.latLng);
	// addLatLng(event.latLng);
   
  });
}

function codeAddress() {
	 
	    var address = document.getElementById("startAddress").value;
	    geocoder.geocode( { 'address': address}, function(results, status) {
	      if (status == google.maps.GeocoderStatus.OK) {
	        map.setCenter(results[0].geometry.location);
	        var marker = new google.maps.Marker({
	            map: map, 
	            position: results[0].geometry.location
	        });
	      } else {
	        alert("Geocode was not successful for the following reason: " + status);
	      }
	    });
	  }

</script>
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