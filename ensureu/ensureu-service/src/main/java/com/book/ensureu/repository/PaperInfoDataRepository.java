package com.book.ensureu.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.constant.PaperType;
import com.book.ensureu.constant.TestType;
import com.book.ensureu.model.PaperInfoDataModel;

@Repository
public interface PaperInfoDataRepository extends MongoRepository<PaperInfoDataModel, String> {

	public Optional<List<PaperInfoDataModel>> findByPaperTypeAndEnable(PaperType paperType,boolean enable);
	
	public Optional<List<PaperInfoDataModel>> findByPaperTypeAndPaperCategoryAndPaperSubCategory(PaperType paperType,
			PaperCategory paperCategory, PaperSubCategory paperSubCategory);
	
	public Optional<List<PaperInfoDataModel>> findByPaperTypeAndPaperCategory(PaperType paperType,
			PaperCategory paperCategory);
	
	public Optional<List<PaperInfoDataModel>> findByPaperTypeAndPaperSubCategory(PaperType paperType,
			PaperSubCategory paperSubCategory);
	
	
	  public Optional<List<PaperInfoDataModel>>
	  findByPaperTypeAndPaperSubCategoryAndEnable(PaperType paperType,
	  PaperSubCategory paperSubCategory,boolean enable);
	 
	
	public Optional<List<PaperInfoDataModel>> findByPaperSubCategoryInAndTestTypeAndEnable(List<PaperSubCategory> listOfSubCatogory,TestType testType,boolean enable);
	

}
