package com.book.ensureu.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.constant.PaperType;
import com.book.ensureu.constant.TestType;
import com.book.ensureu.model.PaperMetaDataModel;

public interface PaperMetaDataRepository extends MongoRepository<PaperMetaDataModel, Long> {

	public PaperMetaDataModel findByPaperType(PaperType paperType);

	public List<PaperMetaDataModel> findByPaperTypeAndPaperCategory(PaperType paperType, PaperCategory paperCategory);

}
