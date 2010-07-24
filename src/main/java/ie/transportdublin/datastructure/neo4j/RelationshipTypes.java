package ie.transportdublin.datastructure.neo4j;

import org.neo4j.graphdb.RelationshipType;

public enum RelationshipTypes implements RelationshipType {
	ROAD, BUS, TRAIN, WALKING, CONNECTION, REFERENCE
}
