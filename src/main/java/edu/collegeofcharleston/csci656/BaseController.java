package edu.collegeofcharleston.csci656;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class BaseController {

	private static final String VIEW_INDEX = "index";
	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(BaseController.class);

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String getIndex(ModelMap model) {		
		// Spring uses InternalResourceViewResolver and return back index.jsp
		return VIEW_INDEX;
	}

	@RequestMapping(value = "/calculate", method = RequestMethod.PUT)
	public @ResponseBody String calculateMovieRating(ModelMap model) {
		logger.debug("calculating movie rating");
		String[] actors = {"Seth Rogan", "Zac Efron", "Jonah Hill", "Aubrey Plaza"};
		return String.valueOf(Brain.calculateMovieRating("Quentin Tarantino", actors, 180000000));
	}
}