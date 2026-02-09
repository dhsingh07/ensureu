package com.book.ensureu.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.book.ensureu.constant.PaperType;
import com.book.ensureu.dto.QuestionVaultDto;
import com.book.ensureu.model.VaultModel;
import com.book.ensureu.repository.VaultRespository;
import com.book.ensureu.service.VaultService;
import com.book.ensureu.util.HashUtil;

@Service
public class VaultServiceImpl implements VaultService {

	public static final Logger LOGGER = LoggerFactory.getLogger(VaultServiceImpl.class.getName());

	@Lazy
	@Autowired
	VaultRespository vaultRespository;

	@Override
	public void saveQuestion(QuestionVaultDto question, String userName) {
		if (question != null) {
			LOGGER.info("Saved Question...");
			VaultModel vaultModel = convertQuestionVaultToModel(question, userName);
			vaultRespository.save(vaultModel);
		}

	}

	@Override
	public List<QuestionVaultDto> getQuestionByUserName(String userName) {
		LOGGER.info("getQuestionByUserId Question...user " + userName);
		List<VaultModel> vaultModelList = vaultRespository.findByUserNameOrderByCreatedDateDesc(userName);
		List<QuestionVaultDto> listQuestionVaultDto = new ArrayList<QuestionVaultDto>();
		vaultModelList.forEach(valutModel -> listQuestionVaultDto.add(convertVaultModelToDto(valutModel, userName)));
		return listQuestionVaultDto;
	}

	@Override
	public void deleteQuestionByUserNameAndQuestionId(String questionId, String userName) {
		LOGGER.info("delete Question...user " + userName + " questionId : " + questionId);
		vaultRespository.deleteByUserNameAndQuestionId(userName, questionId);
	}

	private VaultModel convertQuestionVaultToModel(QuestionVaultDto question, String userName) {
		VaultModel vaultModel = null;
		if (question != null) {
			vaultModel = new VaultModel();
			vaultModel.setPaperType(question.getPaperType());
			vaultModel.setPaperCategory(question.getPaperCategory());
			vaultModel.setPaperSubCategory(question.getPaperSubCategory());
			vaultModel.setTestType(question.getTestType());
			vaultModel.setPaperId(question.getPaperId());
			vaultModel.setQuestionId(question.getQuestionId());
			vaultModel.setUserName(userName);
			String id = HashUtil.hashByMD5(question.getPaperType().toString(), question.getPaperId(),
					question.getQuestionId(), userName);
			vaultModel.setId(id);
			vaultModel.setSectionName(question.getSectionName());
			vaultModel.setSectionType(question.getSectionType());
			vaultModel.setQuestion(question.getQuestion());

			vaultModel.setQuestionType(question.getQuestionType());
			vaultModel.setReasone(question.getReasone());
			vaultModel.setCreatedDate(System.currentTimeMillis());

		}
		return vaultModel;
	}

	private QuestionVaultDto convertVaultModelToDto(VaultModel vaultModel, String userName) {
		QuestionVaultDto questionVaultDto = null;
		if (vaultModel != null) {
			questionVaultDto = new QuestionVaultDto(vaultModel.getId(), vaultModel.getPaperType(),
					vaultModel.getPaperCategory(), vaultModel.getPaperSubCategory(), vaultModel.getTestType(),
					vaultModel.getPaperId(), vaultModel.getQuestionId(), vaultModel.getSectionName(),
					vaultModel.getSectionType(), vaultModel.getQuestion(), vaultModel.getQuestionType(),
					vaultModel.getReasone(), userName,vaultModel.getCreatedDate());
		}
		return questionVaultDto;
	}

	@Override
	public List<QuestionVaultDto> getQuestionByUserNameAndPaperType(String userName, PaperType paperType) {
		LOGGER.info("getQuestionByUserNameAndPaperType Question...user " + userName);
		List<VaultModel> vaultModelList = vaultRespository.findByUserNameAndPaperType(userName, paperType);
		List<QuestionVaultDto> listQuestionVaultDto = new ArrayList<QuestionVaultDto>();
		vaultModelList.forEach(valutModel -> listQuestionVaultDto.add(convertVaultModelToDto(valutModel, userName)));
		return listQuestionVaultDto;
	}

}
