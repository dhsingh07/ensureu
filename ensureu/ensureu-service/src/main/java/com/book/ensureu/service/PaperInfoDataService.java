package com.book.ensureu.service;

import java.util.List;
import java.util.Map;

import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.constant.PaperType;
import com.book.ensureu.constant.TestType;
import com.book.ensureu.dto.PaperInfoDataDto;
import com.book.ensureu.exception.GenericException;

public interface PaperInfoDataService {

	public void savePaperInfoData(PaperInfoDataDto paperInfoDto) throws GenericException;

	public void savePaperInfoData(List<PaperInfoDataDto> listPaperInfoDto)throws GenericException;

	public PaperInfoDataDto getPaperInfoDataById(String id) throws GenericException;

	public List<PaperInfoDataDto> getPaperInfoDataByPaperTypeAndPaperCategory(PaperType paperType,
			PaperCategory paperCategory) throws GenericException;

	public List<PaperInfoDataDto> getPaperInfoDataByPaperTypeAndPaperCategoryAndPaperSubCategory(PaperType paperType,
			PaperCategory paperCategory, PaperSubCategory paperSubCategory) throws GenericException;
	
	public Map<PaperSubCategory,List<String>> getPaperInfoByTestTypeAndPaperSubCategoryAndEnable(TestType testType,List<PaperSubCategory> listOfSubCatogory, boolean enable);
}
