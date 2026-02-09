package com.book.ensureu.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;

import com.book.ensureu.dto.BlogTagsDTO;
import com.book.ensureu.model.BlogTagsModel;
import com.book.ensureu.repository.BlogTagsRepository;
import com.book.ensureu.service.BlogTagsService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class BlogTagsServiceImpl implements BlogTagsService {

	@Autowired
	BlogTagsRepository blogTagsRepo;

	@Autowired
	ObjectMapper mapper;

	@Override
	public BlogTagsDTO saveBlogTags(BlogTagsDTO payload) throws Exception {
		BlogTagsModel model = null;
		if (payload != null) {
			model = blogTagsRepo.save(mapper.convertValue(payload, BlogTagsModel.class));
		}

		return model != null ? mapper.convertValue(model, BlogTagsDTO.class) : null;
	}

	@Override
	public List<BlogTagsDTO> saveBlogTagsList(List<BlogTagsDTO> payloadList) throws Exception {
		List<BlogTagsModel> modelList = null;
		if (payloadList != null) {
			modelList = blogTagsRepo
					.saveAll(mapper.convertValue(payloadList, new TypeReference<List<BlogTagsModel>>() {
					}));
		}

		return modelList != null ? mapper.convertValue(modelList, new TypeReference<List<BlogTagsDTO>>() {
		}) : null;
	}

	@Override
	public List<BlogTagsDTO> fetchBlogTags(int page, int size) throws Exception {
		List<BlogTagsModel> modelList = null;
		if (size > 0) {
			PageRequest pagination = PageRequest.of(page, size, Sort.by(Order.desc("createdDate")));
			Page<BlogTagsModel> modelPage = blogTagsRepo.findAll(pagination);
			modelList = modelPage != null && modelPage.getContent() != null ? modelPage.getContent() : null;
		} else {
			modelList = blogTagsRepo.findAll();
		}
		return modelList != null ? mapper.convertValue(modelList, new TypeReference<List<BlogTagsDTO>>() {
		}) : null;
	}

}
