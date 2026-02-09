package com.book.ensureu.service.impl;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.book.ensureu.model.CoursesModel;
import com.book.ensureu.repository.CoursesRepository;
import com.book.ensureu.service.CoursesService;

/**
 * @author dharmendra.singh
 *
 */
@Service
public class CoursesServiceImpl implements CoursesService {

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(CoursesServiceImpl.class);
	@Autowired
	CoursesRepository coursesRepository;

	@Override
	public void saveCourses(List<CoursesModel> coursesModel) {
		coursesRepository.saveAll(coursesModel);
	}

	@Override
	public void saveCourses(CoursesModel coursesModel) {
		LOGGER.info("saveCourses coursed " + coursesModel.getName());
		coursesRepository.save(coursesModel);
	}

	@Override
	public CoursesModel getCorsesByName(String name) {
		return (CoursesModel) coursesRepository.findByName(name);
	}

	@Override
	public Optional<CoursesModel> getCorsesById(Long id) {
		return coursesRepository.findById(id);
	}

	@Override
	public List<CoursesModel> getAllCourses() {
		return coursesRepository.findAll();
	}

}
