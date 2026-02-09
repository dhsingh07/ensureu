package com.book.ensureu.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.book.ensureu.constant.CounterEnum;
import com.book.ensureu.model.CoursesModel;
import com.book.ensureu.service.CounterService;
import com.book.ensureu.service.CoursesService;

/**
 * @author dharmendra.singh
 *
 */
@CrossOrigin
@RequestMapping("/course")
@RestController
public class CoursesMetaDataApi {

	public static final Logger LOGGER=LoggerFactory.getLogger(CoursesMetaDataApi.class);
	@Autowired
	CoursesService coursesService;
	
	@Autowired
	CounterService counterService;

	@RequestMapping(value = "/save/list", method = RequestMethod.POST)
	public void saveCourses(@RequestBody final List<CoursesModel> coursesModel) {
		if (coursesModel != null && !coursesModel.isEmpty()) {
			for(CoursesModel coursesModel2:coursesModel) {
				coursesModel2.setId(counterService.increment(CounterEnum.COURSES));
			}
			coursesService.saveCourses(coursesModel);
		}
	}

	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public List<CoursesModel> saveCourse() {
			return coursesService.getAllCourses();
	}
	
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public void saveCourse(@RequestBody CoursesModel coursesModel) {
		if (coursesModel != null) {
			coursesService.saveCourses(coursesModel);
		}
	}
	
	
	@RequestMapping(value = "/getcourses/{id}", method = RequestMethod.GET)
	public CoursesModel getCorsesById(@PathVariable(value = "id") final Long id) {
		LOGGER.info("get courses by id");
		return coursesService.getCorsesById(id).get();
	}
	
	@RequestMapping(value = "/getcoursesbyname", method = RequestMethod.GET)
	public CoursesModel getCorsesById(@RequestParam(value = "name") final String name) {
		LOGGER.info("get courses by id");
		return coursesService.getCorsesByName(name);
	}
}
