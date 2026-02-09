package com.book.ensureu.service;

import java.util.List;

import com.book.ensureu.dto.PaperMetaDataModelDto;
import com.book.ensureu.dto.PaperMetaDataPaperTypeVsPaperDto;
import com.book.ensureu.model.PaperMetaDataModel;

public interface PaperMetaDataService {

	public void savePaperMetaDataDetails(PaperMetaDataModel paperMetaDataModel);
	public void savePaperMetaDataDetails(PaperMetaDataModelDto paperMetaDataModelDto);
	public void savePaperMetaDataDtoDetails(List<PaperMetaDataModelDto> paperMetaDataModelDtoList);
	public void savePaperMetaDataDetails(List<PaperMetaDataModel> paperMetaDataModelList);

	public PaperMetaDataModel getPaperMetaDataByPaperType(String paperType);

	public List<PaperMetaDataModel> getPaperMetaDataByPaperType(String paperType, String paperCategory);
	
	
	public PaperMetaDataModelDto getPaperMetaDataDto(String paperType);


}
