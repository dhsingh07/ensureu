package com.book.ensureu.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.constant.PaperType;
import com.book.ensureu.constant.TestType;
import com.book.ensureu.dto.PaperInfoDataDto;
import com.book.ensureu.exception.GenericException;
import com.book.ensureu.model.PaperInfoDataModel;
import com.book.ensureu.repository.PaperInfoDataRepository;
import com.book.ensureu.security.UserPrincipalService;
import com.book.ensureu.service.PaperInfoDataService;
import com.book.ensureu.util.HashUtil;

@Service
public class PaperInfoDataServiceImpl implements PaperInfoDataService {

	private final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(PaperInfoDataServiceImpl.class.getName());

	@Autowired
	PaperInfoDataRepository paperInfoDataRepository;
	
	@Autowired
	UserPrincipalService userPrincipalService;

	@Override
	public void savePaperInfoData(PaperInfoDataDto paperInfoDto) throws GenericException {
		LOGGER.info("Save the paperInfoData...");
		PaperInfoDataModel paperInfoDataModel = convertDtoToModel(paperInfoDto);
		paperInfoDataRepository.save(paperInfoDataModel);
	}

	@Override
	public void savePaperInfoData(List<PaperInfoDataDto> listPaperInfoDto) throws GenericException {
		LOGGER.info("Save the paperInfoData list...");
		List<PaperInfoDataModel> listPaperInfoData = new ArrayList<PaperInfoDataModel>();
		listPaperInfoDto.forEach(paperInfoDto -> listPaperInfoData.add(convertDtoToModel(paperInfoDto)));
		paperInfoDataRepository.saveAll(listPaperInfoData);
	}

	@Override
	public PaperInfoDataDto getPaperInfoDataById(String id) throws GenericException {
		Optional<PaperInfoDataModel> optinalPaperInfoDataModel = paperInfoDataRepository.findById(id);
		optinalPaperInfoDataModel.orElseThrow(() -> new GenericException("PaperInfoData not found"));
		return convertModelToDto(optinalPaperInfoDataModel.get());
	}

	@Override
	public List<PaperInfoDataDto> getPaperInfoDataByPaperTypeAndPaperCategory(PaperType paperType,
			PaperCategory paperCategory) throws GenericException {
		Optional<List<PaperInfoDataModel>> optnalListPaperInfoDataModel = paperInfoDataRepository
				.findByPaperTypeAndPaperCategory(paperType, paperCategory);

		optnalListPaperInfoDataModel.orElseThrow(() -> new GenericException("PaperInfoDataModel not found"));
		List<PaperInfoDataDto> listPaperInfoDataDto = new ArrayList<PaperInfoDataDto>();
		optnalListPaperInfoDataModel.get()
				.forEach(paperInfoDataModel -> listPaperInfoDataDto.add(convertModelToDto(paperInfoDataModel)));
		return listPaperInfoDataDto;
	}

	@Override
	public List<PaperInfoDataDto> getPaperInfoDataByPaperTypeAndPaperCategoryAndPaperSubCategory(PaperType paperType,
			PaperCategory paperCategory, PaperSubCategory paperSubCategory) throws GenericException {
		Optional<List<PaperInfoDataModel>> optnalListPaperInfoDataModel = paperInfoDataRepository
				.findByPaperTypeAndPaperCategoryAndPaperSubCategory(paperType, paperCategory, paperSubCategory);
		optnalListPaperInfoDataModel.orElseThrow(() -> new GenericException("PaperInfoDataModel not found"));
		List<PaperInfoDataDto> listPaperInfoDataDto = new ArrayList<PaperInfoDataDto>();
		optnalListPaperInfoDataModel.get()
				.forEach(paperInfoDataModel -> listPaperInfoDataDto.add(convertModelToDto(paperInfoDataModel)));
		return listPaperInfoDataDto;
	}

	private PaperInfoDataModel convertDtoToModel(PaperInfoDataDto paperInfoDto) {
		PaperInfoDataModel paperInfoDataModel = null;
		if (paperInfoDto != null) {
			String hashKey=null;
			if(!paperInfoDto.getPaperSubCategory().equals(PaperSubCategory.SSC_CGL_TIER2)) {
			 hashKey = HashUtil.hashByMD5(paperInfoDto.getPaperType().toString(),
					paperInfoDto.getPaperCategory().toString(), paperInfoDto.getPaperSubCategory()
					!=null?paperInfoDto.getPaperSubCategory().toString():null,
					paperInfoDto.getTestType().toString());
			}
			else {
				 hashKey = HashUtil.hashByMD5(paperInfoDto.getPaperType().toString(),
							paperInfoDto.getPaperCategory().toString(), paperInfoDto.getPaperSubCategory()
							!=null?paperInfoDto.getPaperSubCategory().toString():null,
							paperInfoDto.getTestType().toString(),paperInfoDto.getSectionTypeList().get(0).toString());
			}
			paperInfoDataModel = new PaperInfoDataModel(hashKey, paperInfoDto.getPaperType(),
					paperInfoDto.getPaperCategory(), paperInfoDto.getPaperSubCategory(), paperInfoDto.getTestType(),
					null, paperInfoDto.getPaperName(), paperInfoDto.getScore(), paperInfoDto.getNegativeMarks(),
					paperInfoDto.getPerQuestionScore(), paperInfoDto.getTotalTime(),
					paperInfoDto.getTotalQuestionCount(), paperInfoDto.getSectionTypeList(),
					paperInfoDto.getSections(),paperInfoDto.isEnable(),paperInfoDto.getPriority());
			paperInfoDataModel.setCreateDate(new Date().getTime());
			paperInfoDataModel.setModifiedDate(new Date().getTime());
			paperInfoDataModel.setCreatedBy(userPrincipalService.getCurrentUserDetails().getUsername());
			paperInfoDataModel.setModifiedBy(userPrincipalService.getCurrentUserDetails().getUsername());
		}
		return paperInfoDataModel;
	}

	private PaperInfoDataDto convertModelToDto(PaperInfoDataModel paperInfoModel) {
		PaperInfoDataDto paperInfoDataDto = null;
		if (paperInfoModel != null) {
			paperInfoDataDto = new PaperInfoDataDto(paperInfoModel.getId(), paperInfoModel.getPaperType(),
					paperInfoModel.getPaperCategory(), paperInfoModel.getPaperSubCategory(),
					paperInfoModel.getTestType(), null, paperInfoModel.getPaperName(), paperInfoModel.getScore(),
					paperInfoModel.getNegativeMarks(), paperInfoModel.getPerQuestionScore(),
					paperInfoModel.getTotalTime(), paperInfoModel.getTotalQuestionCount(),
					paperInfoModel.getSectionTypeList(), paperInfoModel.getSections(),paperInfoModel.isEnable(),paperInfoModel.getPriority());

		}
		return paperInfoDataDto;
	}


	@Override
	public Map<PaperSubCategory,List<String>> getPaperInfoByTestTypeAndPaperSubCategoryAndEnable(TestType testType,List<PaperSubCategory> listOfSubCatogory,
			 boolean enable) {
		Map<PaperSubCategory,List<String>> paperSubCategoryVsPaperSubCategoryTypePaper=new LinkedHashMap<PaperSubCategory, List<String>>();
		Optional<List<PaperInfoDataModel>> paperInfoAvalable=paperInfoDataRepository.findByPaperSubCategoryInAndTestTypeAndEnable(listOfSubCatogory,testType,true);
        if(paperInfoAvalable.isPresent()){
        	paperInfoAvalable.get().forEach(paperInfo->{ 
        		if(paperSubCategoryVsPaperSubCategoryTypePaper.get(paperInfo.getPaperSubCategory())==null) {
        			paperSubCategoryVsPaperSubCategoryTypePaper.put(paperInfo.getPaperSubCategory(), paperInfo.getPaperSubCategory().getPaperSubCategoryTypes());
        		}
        	});
        }
		return paperSubCategoryVsPaperSubCategoryTypePaper;
	}

}
