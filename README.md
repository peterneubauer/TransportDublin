TransportDublin.ie Beta - Public Transport Route Planner using Neo4j
====================================================================

Installation
------------

![Routing](http://img838.imageshack.us/img838/8676/websitescreenshot.jpg "Routing")

To run it, do:

1. Install http://maven.apache.org
2. Clone this project
3. Import the data:

    mvn -P import install
  
4. Run the server:

    mvn jetty:run
  
5. Browse the application at [http://localhost:8080/transportdublin/routeplanner](http://localhost:8080/transportdublin/routeplanner)

More Info on the [wiki page](http://wiki.github.com/paddydub/TransportDublin/ "Documentation")