/*
 * Copyright (c) 2010
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ie.transportdublin.setupgraph;

import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;

import org.neo4j.gis.spatial.Layer;
import org.neo4j.gis.spatial.SpatialDatabaseService;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import com.vividsolutions.jts.geom.Coordinate;

public class ImportSpatialStopDataStep2 {

	private static final String DB_PATH = "data/neo4j-db";
	private static final String LAYER_NAME = "stops_layer";

	public static void main(String[] args) throws Exception {
		String citiesPath = "resources/data/stopsListForNeo4jSpatial.txt";
		GraphDatabaseService database = new EmbeddedGraphDatabase(DB_PATH);
		try {
			SpatialDatabaseService spatialService = new SpatialDatabaseService(
					database);

			Transaction tx = database.beginTx();
			try {
				spatialService.createLayer(LAYER_NAME);
				tx.success();
			} finally {
				tx.finish();
			}

			LineNumberReader reader = new LineNumberReader(new FileReader(
					new File(citiesPath)));
			try {
				int lines = 0;
				int brokenLines = 0;
				int counter = 0;
				String[] fieldsName = new String[] { "RouteNum", "StopID",
						"Address", "Area", "lat", "lng" };

				String line = reader.readLine(); // skip first line
				// while (line != null && !neededCountryFinished) {
				tx = database.beginTx();
				try {
					Layer layer = spatialService.getLayer(LAYER_NAME);
					com.vividsolutions.jts.geom.GeometryFactory gf = layer
							.getGeometryFactory();

					while (null != (line = reader.readLine())) {
						System.out.println(lines + "  - " + line);

						// 0 route Number, 1 Stopid ,2 Address ,3 Area, 4 lat, 5
						// lng
						String[] tokens = line.split(",");
						if (tokens.length != 6) {
							brokenLines++;
							System.out.println(lines + "  - " + line);
						} else {
							// if ("it".equals(tokens[0])) {
							try {
								// (country,city)
								Object[] fields = new Object[] { tokens[0],
										tokens[1], tokens[2], tokens[3],
										tokens[4], tokens[5] };
								// lat
								double y = Double.parseDouble(tokens[4]);
								// lon
								double x = Double.parseDouble(tokens[5]);
								layer.add(gf.createPoint(new Coordinate(x, y)),
										fieldsName, fields);

								lines++;
							} catch (Throwable e) {
								brokenLines++;
								System.out.println(lines + "  - " + line);
							}

						}

						if (++counter % 1000 == 0) {
							// Commit the transaction every now and then
							tx.success();
							tx.finish();
							tx = database.beginTx();
						}
					}

					tx.success();

				} finally {
					tx.finish();
				}

				System.out.println("Inserted stops: " + lines);
				System.out.println("Broken lines: " + brokenLines);
			} finally {
				reader.close();
				tx.success();
				tx.finish();
			}
		} finally {

			database.shutdown();
		}
	}
}
