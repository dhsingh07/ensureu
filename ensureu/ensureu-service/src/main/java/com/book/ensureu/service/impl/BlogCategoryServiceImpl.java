package com.book.ensureu.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.stereotype.Service;

import com.book.ensureu.dto.BlogCategoryDTO;
import com.book.ensureu.model.BlogCategoryModel;
import com.book.ensureu.repository.BlogCategoryRepository;
import com.book.ensureu.service.BlogCategoryService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DBObject;

@Service
public class BlogCategoryServiceImpl implements BlogCategoryService {

	@Autowired
	BlogCategoryRepository blogCategoryRepo;

	@Autowired
	ObjectMapper mapper;
	
	@Autowired
	@Lazy
	MongoOperations mongoOperations;

	@Override
	public BlogCategoryDTO saveBlogCategory(BlogCategoryDTO payload) throws Exception {
		BlogCategoryModel model = null;
		if (payload != null) {
			model = blogCategoryRepo.save(mapper.convertValue(payload, BlogCategoryModel.class));
		}

		return model != null ? mapper.convertValue(model, BlogCategoryDTO.class) : null;
	}

	@Override
	public List<BlogCategoryDTO> saveBlogCategoryList(List<BlogCategoryDTO> payloadList) throws Exception {
		List<BlogCategoryModel> modelList = null;
		if (payloadList != null) {
			modelList = blogCategoryRepo
					.saveAll(mapper.convertValue(payloadList, new TypeReference<List<BlogCategoryModel>>() {
					}));
		}

		return modelList != null ? mapper.convertValue(modelList, new TypeReference<List<BlogCategoryDTO>>() {
		}) : null;
	}

	@Override
	public List<BlogCategoryDTO> fetchBlogCategory(int page, int size) throws Exception {
		List<BlogCategoryModel> modelList = null;
		if (size > 0) {
			PageRequest pagination = PageRequest.of(page, size, Sort.by(Order.desc("createdDate")));
			Page<BlogCategoryModel> modelPage = blogCategoryRepo.findAll(pagination);
			modelList = modelPage != null && modelPage.getContent() != null ? modelPage.getContent() : null;
		} else {
			modelList = blogCategoryRepo.findAll();
		}
		return modelList != null ? mapper.convertValue(modelList, new TypeReference<List<BlogCategoryDTO>>() {
		}) : null;
	}
	
	@Override
	public List<DBObject> fetchCountCategoryWise() throws Exception{
		LookupOperation lookUp = Aggregation.lookup("blogs", "_id", "category._id", "cat_info");
		Fields fields = Fields.from(Fields.field("name"));
		ProjectionOperation project = Aggregation.project(fields).and("cat_info").size().as("count");
		Aggregation aggreagation = Aggregation.newAggregation(lookUp,project);
		AggregationResults<DBObject> result = mongoOperations.aggregate(aggreagation,BlogCategoryModel.class, DBObject.class);
		if(result!=null){
			return result.getMappedResults();
		}else{
			return null;
		}
	}

}
