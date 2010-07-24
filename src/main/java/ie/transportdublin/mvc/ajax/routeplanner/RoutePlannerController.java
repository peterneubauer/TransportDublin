package ie.transportdublin.mvc.ajax.routeplanner;

import ie.transportdublin.mvc.ajax.pathfinder.GraphPathFinder;
import ie.transportdublin.mvc.ajax.pathfinder.SetupDirections;

import javax.validation.Validator;

import org.neo4j.graphalgo.WeightedPath;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.index.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/routeplanner")
public class RoutePlannerController {

	public enum WaypointType {
		START, END
	}

	private WeightedPath path;

	private Validator validator;

	@Autowired
	private GraphDatabaseService graphDbService;

	@Autowired
	private IndexService indexService;

	public RoutePlannerController() {
		super();
	}

	@Autowired
	public RoutePlannerController(Validator validator) {
		this.validator = validator;
	}

	@RequestMapping(value = "/availability", method = RequestMethod.GET)
	public @ResponseBody
	String getAvailability(@RequestParam Double lat1,
			@RequestParam Double lng1, @RequestParam Double lat2,
			@RequestParam Double lng2) {
	
		RoutePlanner routePlanner = new RoutePlanner(lat1, lng1, lat2, lng2);
		GraphPathFinder pathfinder = new GraphPathFinder(graphDbService,
				indexService);
		WeightedPath path = pathfinder.setupStartAndEndPoints(routePlanner);
		SetupDirections directions = new SetupDirections(path, indexService);
		String json = directions.getJson();
		return json;
	}

	@RequestMapping(method = RequestMethod.GET)
	public String getCreateForm(Model model) {
		System.out.println(" redirect to routeplanner/createForm ");
		return "routeplanner/createForm";
	}
}
