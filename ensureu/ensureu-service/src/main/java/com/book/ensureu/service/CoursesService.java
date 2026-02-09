package com.book.ensureu.service;

import java.util.List;
import java.util.Optional;

import com.book.ensureu.model.CoursesModel;

public interface CoursesService {
	public void saveCourses(CoursesModel coursesModel);

	public CoursesModel getCorsesByName(String name);
	
	public Optional<CoursesModel> getCorsesById(Long id);
	
	public List<CoursesModel> getAllCourses();

	void saveCourses(List<CoursesModel> coursesModel);
	
}
