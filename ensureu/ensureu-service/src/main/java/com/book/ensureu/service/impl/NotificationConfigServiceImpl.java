package com.book.ensureu.service.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.book.ensureu.dto.NotificationConfigDTO;
import com.book.ensureu.model.NotificationConfigModel;
import com.book.ensureu.repository.NotificationConfigRepository;
import com.book.ensureu.service.NotificationConfigService;
import com.book.ensureu.util.HashUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class NotificationConfigServiceImpl implements NotificationConfigService {

	@Autowired
	NotificationConfigRepository notificationConfigRepo;

	@Autowired
	ObjectMapper mapper;

	@Override
	public NotificationConfigDTO save(NotificationConfigDTO payload) {
		NotificationConfigModel model = null;
		if (payload != null) {
			payload.setId(HashUtil.hashByMD5(payload.getCategory(), payload.getSubCategory()));
			model = mapper.convertValue(payload, NotificationConfigModel.class);
			model = notificationConfigRepo.save(model);
		}
		return model != null ? mapper.convertValue(model, NotificationConfigDTO.class) : null;
	}

	@Override
	public NotificationConfigDTO findBySubCategory(String subCategory) throws Exception {
		NotificationConfigModel model = notificationConfigRepo.findBySubCategory(subCategory);
		return model != null ? mapper.convertValue(model, NotificationConfigDTO.class) : null;
	}

	@Override
	public List<NotificationConfigDTO> findAll() throws Exception {
		List<NotificationConfigModel> models = notificationConfigRepo.findAll();
		return models != null ? mapper.convertValue(models, new TypeReference<List<NotificationConfigDTO>>() {
		}) : Collections.emptyList();
	}

}
