package com.book.ensureu.service.impl;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.book.ensureu.constant.ApplicationConstant;
import com.book.ensureu.constant.BlogsConstant;
import com.book.ensureu.dto.BlogCommentsDTO;
import com.book.ensureu.dto.BlogsDTO;
import com.book.ensureu.model.BlogCommentsModel;
import com.book.ensureu.model.BlogsModel;
import com.book.ensureu.model.JwtUser;
import com.book.ensureu.repository.BlogCommentsRepository;
import com.book.ensureu.repository.BlogsRepository;
import com.book.ensureu.security.UserPrincipalService;
import com.book.ensureu.service.BlogsService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class BlogsServiceImpl implements BlogsService {

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(BlogsServiceImpl.class);

	@Autowired
	ObjectMapper mapper;

	@Autowired
	BlogsRepository blogsRepo;

	@Autowired
	@Lazy
	BlogCommentsRepository blogCommentsRepo;

	@Autowired
	UserPrincipalService userPrincipal;

	// GCS removed - will use S3 for file storage
	// @Autowired
	// @Lazy
	// GoogleCloudStorageUtil gcpUtil;

	@Autowired
	@Lazy
	MongoTemplate mongoTemplate;

	@Override
	public BlogsDTO saveBlog(BlogsDTO blogsPayload) throws Exception {
		BlogsModel model = null;
		if (blogsPayload != null) {
			blogsPayload.setCreatedDate(System.currentTimeMillis());
			model = mapper.convertValue(blogsPayload, BlogsModel.class);
			JwtUser currentUser = userPrincipal.getCurrentUserDetails();
			if (currentUser != null) {
				model.setUserId(currentUser.getUsername());
				model.setAuthor(currentUser.getFirstname() + " " + currentUser.getLastname());
			} else {
				throw new IllegalAccessError("Unauthorized Access");
			}
			model = blogsRepo.save(model);
		}
		return model != null ? mapper.convertValue(model, BlogsDTO.class) : null;
	}

	@Override
	public BlogsDTO partialUpdateBlog(BlogsDTO blogsPayload,boolean isDislike) throws Exception {
		BlogsModel model = null;
		if (blogsPayload != null) {
			JwtUser currentUser = userPrincipal.getCurrentUserDetails();
			model = mapper.convertValue(blogsPayload, BlogsModel.class);
			if (currentUser != null) {
				model.setUserId(currentUser.getUsername());
				model.setAuthor(currentUser.getFirstname() + " " + currentUser.getLastname());
			} else {
				throw new IllegalAccessError("Unauthorized Access");
			}

			model.setUpdatedDate(System.currentTimeMillis());
			Query query = new Query(Criteria.where("id").is(blogsPayload.getId()));
			Update update = null;
			for (Field field : model.getClass().getDeclaredFields()) {
				try {
					field.setAccessible(true);
					if (!field.getName().equalsIgnoreCase("serialVersionUID") && field.get(model) != null) {
						if (update == null)
							update = new Update();
						if (field.getName().equalsIgnoreCase("likes")) {
							if(isDislike) {
								update.pullAll(field.getName(), blogsPayload.getLikes().toArray());
							}else {
								update.addToSet(field.getName()).each(field.get(model));
							}
						} else if(field.getName().equalsIgnoreCase("views")){
							update.inc(field.getName(), blogsPayload.getViews());
						}else {
							update.set(field.getName(), field.get(model));
						}
					}
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			if (update != null) {
				FindAndModifyOptions options = new FindAndModifyOptions();
				options.returnNew(true);
				model = mongoTemplate.findAndModify(query, update, options, BlogsModel.class);
			}
		}
		return model != null ? mapper.convertValue(model, BlogsDTO.class) : null;
	}

	@Override
	public List<BlogsDTO> fetchAll(int size, int page, String sortBy, String direction) throws Exception {
		Page<BlogsModel> modelPage = null;
		sortBy = BlogsConstant.SortBy.valueOf(sortBy.toUpperCase().trim()) != null ? BlogsConstant.SortBy.valueOf(sortBy.toUpperCase().trim()).getSortByCode()
				: BlogsConstant.SortBy.CREATEDDATE.getSortByCode();
		Sort sort = direction != null
				&& (direction.equalsIgnoreCase(BlogsConstant.Direction.ASCENDING.getDirectionCode()))
						? Sort.by(Order.asc(sortBy))
						: Sort.by(Order.desc(sortBy));
		PageRequest pagination = PageRequest.of(page, size, sort);
		modelPage = blogsRepo.findAll(pagination);
		return modelPage != null ? mapper.convertValue(modelPage.getContent(), new TypeReference<List<BlogsDTO>>() {
		}) : null;
	}

	@Override
	public BlogCommentsDTO saveBlogComments(BlogCommentsDTO blogCommentsPayload) throws Exception {
		BlogCommentsModel model = null;
		if (blogCommentsPayload != null && blogCommentsPayload.getBlogId() != null) {
			// Check if comments document already exists for this blog
			Optional<BlogCommentsModel> existingModel = blogCommentsRepo.findByBlogId(blogCommentsPayload.getBlogId());

			if (existingModel.isPresent()) {
				// Append new comments to existing document
				model = existingModel.get();
				if (blogCommentsPayload.getComments() != null) {
					if (model.getComments() == null) {
						model.setComments(blogCommentsPayload.getComments());
					} else {
						model.getComments().addAll(blogCommentsPayload.getComments());
					}
				}
			} else {
				// Create new comments document
				model = mapper.convertValue(blogCommentsPayload, BlogCommentsModel.class);
			}
			model = blogCommentsRepo.save(model);
		}
		return model != null ? mapper.convertValue(model, BlogCommentsDTO.class) : null;
	}

	@Override
	public BlogCommentsDTO fetchCommentsById(String blogId) throws Exception {
		if (blogId == null)
			return null;
		// Use findByBlogId instead of findById - blogId is a field, not the document id
		Optional<BlogCommentsModel> model = blogCommentsRepo.findByBlogId(blogId);
		return model != null && model.isPresent() ? mapper.convertValue(model.get(), BlogCommentsDTO.class) : null;
	}

	@Override
	public List<BlogsDTO> fetchByCategory(int size, int page, String sortBy, String categoryId) throws Exception {
		Page<BlogsModel> modelPage = null;
		if (size <= 0) {
			size = 10;
			page = 0;
		}
		sortBy = BlogsConstant.SortBy.valueOf(sortBy.toUpperCase().trim()) != null ? BlogsConstant.SortBy.valueOf(sortBy.toUpperCase().trim()).getSortByCode()
				: BlogsConstant.SortBy.CREATEDDATE.getSortByCode();
		PageRequest pagination = PageRequest.of(page, size, Sort.by(Order.desc(sortBy)));
		modelPage = blogsRepo.findByCategory(new ObjectId(categoryId), pagination);
		return modelPage != null ? mapper.convertValue(modelPage.getContent(), new TypeReference<List<BlogsDTO>>() {
		}) : null;
	}

	@Override
	public List<BlogsDTO> fetchByUser(int size, int page, String sortBy, String userId) throws Exception {
		Page<BlogsModel> modelPage = null;
		JwtUser currentUser = userPrincipal.getCurrentUserDetails();
		if (currentUser != null && currentUser.getUsername().equalsIgnoreCase(userId)) {
			if (size <= 0) {
				size = 10;
				page = 0;
			}
			sortBy = BlogsConstant.SortBy.valueOf(sortBy.toUpperCase().trim()) != null ? BlogsConstant.SortBy.valueOf(sortBy.toUpperCase().trim()).getSortByCode()
					: BlogsConstant.SortBy.CREATEDDATE.getSortByCode();
			PageRequest pagination = PageRequest.of(page, size, Sort.by(Order.desc(sortBy)));
			modelPage = blogsRepo.findByUserId(currentUser.getUsername(), pagination);
		}
		return modelPage != null ? mapper.convertValue(modelPage.getContent(), new TypeReference<List<BlogsDTO>>() {
		}) : null;
	}

	@Override
	public void deleteBlog(String blogId) throws Exception {
		blogsRepo.deleteById(blogId);
	}

	@Override
	public ResponseEntity<?> uploadImage(MultipartFile file) throws Exception {
		// TODO: Implement S3 file upload
		// GCS has been removed - S3 integration pending
		LOGGER.warn("Image upload attempted but S3 is not yet configured");
		Map<String, String> errorMap = new HashMap<String, String>();
		errorMap.put("error", "File upload is temporarily unavailable. S3 storage not configured.");
		return ResponseEntity.status(503).body(errorMap);
	}

	@Override
	public List<BlogsDTO> searchByTitle(String phrase) throws Exception {
		TextCriteria criteria = TextCriteria.forDefaultLanguage().matchingPhrase(phrase);
		List<BlogsModel> modelList = blogsRepo.findBy(criteria);
		return modelList != null ? mapper.convertValue(modelList, new TypeReference<List<BlogsDTO>>() {
		}) : null;
	}
	
	@Override
	public BlogsDTO fetchById(String blogId) throws Exception {
		// TODO Auto-generated method stub
		Optional<BlogsModel> model = blogsRepo.findById(blogId);
		return model != null && model.isPresent() ? mapper.convertValue(model.get(), BlogsDTO.class) : null;
	}

}
