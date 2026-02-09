package com.book.ensureu.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperType;
import com.book.ensureu.dto.PaperInfoDataDto;
import com.book.ensureu.dto.PaperMetaDataModelDto;
import com.book.ensureu.exception.GenericException;
import com.book.ensureu.model.PaperMetaDataModel;
import com.book.ensureu.service.PaperInfoDataService;
import com.book.ensureu.service.PaperMetaDataService;

@RestController
@RequestMapping("/papermetadata")
public class PaperMetaDataApi {

	private static final Logger LOGGER = LoggerFactory.getLogger(PaperMetaDataApi.class.getName());

	@Autowired
	PaperMetaDataService paperMetaDataService;

	@Deprecated
	@CrossOrigin
	@RequestMapping(value = "/save/depre", method = RequestMethod.POST)
	public void savePaperMetaData(@RequestBody PaperMetaDataModel paperMetaDataModel) {
		paperMetaDataService.savePaperMetaDataDetails(paperMetaDataModel);
	}

	@CrossOrigin
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public void savePaperMetaDataDto(@RequestBody PaperMetaDataModelDto paperMetaDataModelDto) {
		paperMetaDataService.savePaperMetaDataDetails(paperMetaDataModelDto);
	}

	@CrossOrigin
	@RequestMapping(value = "/save/list", method = RequestMethod.POST)
	public void savePaperMetaDataDto(@RequestBody List<PaperMetaDataModelDto> paperMetaDataModelDtoList) {
		paperMetaDataService.savePaperMetaDataDtoDetails(paperMetaDataModelDtoList);
	}

	@CrossOrigin
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public List<PaperMetaDataModel> getPaperMetaDataList() {
		return null;
	}

	@CrossOrigin
	@RequestMapping(value = "/byPaperType/{paperType}", method = RequestMethod.GET)
	public PaperMetaDataModel getPaperMetaDataByPaperType(@PathVariable(value = "paperType") String paperType) {
		return paperMetaDataService.getPaperMetaDataByPaperType(paperType);
	}

	@CrossOrigin
	@RequestMapping(value = "/paperType/{paperType}", method = RequestMethod.GET)
	public PaperMetaDataModelDto getPaperMetaDataDtoByPaperType(@PathVariable(value = "paperType") String paperType) {
		return paperMetaDataService.getPaperMetaDataDto(paperType);
	}

	@Autowired
	PaperInfoDataService paperInfoDataService;

	// for paperInfoMetadata for creating paper and paper information.

	@CrossOrigin
	@GetMapping("/info/{id}")
	public PaperInfoDataDto getPaperInfoById(@PathVariable(value = "id") String id) {
		try {
			return paperInfoDataService.getPaperInfoDataById(id);
		} catch (GenericException e) {
			LOGGER.error("Error while fetching paperInfo.. Data ", e.getMessage());
		}
		return null;

	}

	@CrossOrigin
	@GetMapping("/info/paperType")
	public List<PaperInfoDataDto> getPaperInfoByPaperTypeAndPaperCategory(
			@RequestParam(value = "paperType") PaperType paperType,
			@RequestParam(value = "paperCategory") PaperCategory paperCategory) {
		try {
			return paperInfoDataService.getPaperInfoDataByPaperTypeAndPaperCategory(paperType, paperCategory);
		} catch (GenericException e) {
			LOGGER.error("Error while fetching paperInfo.. Data ", e.getMessage());
		}
		return null;

	}

	@CrossOrigin
	@PostMapping("/info")
	public void savePaperInfoData(@RequestBody PaperInfoDataDto paperInfoDataDto) {
		try {
			paperInfoDataService.savePaperInfoData(paperInfoDataDto);
		} catch (GenericException e) {
			LOGGER.error("Error while fetching paperInfo.. Data ", e.getMessage());
		}
	}

	@CrossOrigin
	@PostMapping("/info/batch")
	public void savePaperInfoDataBulk(@RequestBody List<PaperInfoDataDto> listPaperInfoDataDto) {
		try {
			paperInfoDataService.savePaperInfoData(listPaperInfoDataDto);
		} catch (GenericException e) {
			LOGGER.error("Error while fetching paperInfo.. Data ", e.getMessage());
		}

	}

}
