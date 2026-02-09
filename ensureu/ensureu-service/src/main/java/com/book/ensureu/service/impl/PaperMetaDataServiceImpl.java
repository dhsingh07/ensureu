package com.book.ensureu.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.book.ensureu.constant.CounterEnum;
import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.constant.PaperType;
import com.book.ensureu.dto.PaperCategoryMetaDataDto;
import com.book.ensureu.dto.PaperMetaDataModelDto;
import com.book.ensureu.dto.PaperSubCategoryMetaDataDto;
import com.book.ensureu.dto.PaperSubCatogoryDto;
import com.book.ensureu.model.PaperInfoDataModel;
import com.book.ensureu.model.PaperMetaDataModel;
import com.book.ensureu.repository.PaperInfoDataRepository;
import com.book.ensureu.repository.PaperMetaDataRepository;
import com.book.ensureu.service.CounterService;
import com.book.ensureu.service.PaperInfoDataService;
import com.book.ensureu.service.PaperMetaDataService;

@Service
public class PaperMetaDataServiceImpl implements PaperMetaDataService {

	@Autowired
	private PaperMetaDataRepository paperMetaDataRepository;

	@Autowired
	CounterService counterService;
	
	@Autowired
	private PaperInfoDataRepository paperInfoDataRepository;

	@Override
	public void savePaperMetaDataDetails(PaperMetaDataModel paperMetaDataModel) {
		PaperMetaDataModel paperMetada = paperMetaDataRepository.findByPaperType(paperMetaDataModel.getPaperType());
		if (paperMetada != null) {
			paperMetaDataModel.setId(paperMetada.getId());
		} else {
			paperMetaDataModel.setId(counterService.increment(CounterEnum.PAPERMETADATA));
		}
		paperMetaDataRepository.save(paperMetaDataModel);

	}

	@Override
	public void savePaperMetaDataDetails(PaperMetaDataModelDto paperMetaDataModelDto) {

		PaperMetaDataModel paperMetaDataModel = convertPaperMetaDataModelFromDto(paperMetaDataModelDto);
		PaperMetaDataModel paperMetada = paperMetaDataRepository.findByPaperType(paperMetaDataModel.getPaperType());
		if (paperMetada != null) {
			paperMetaDataModel.setId(paperMetada.getId());
		} else {
			paperMetaDataModel.setId(counterService.increment(CounterEnum.PAPERMETADATA));
		}
		paperMetaDataRepository.save(paperMetaDataModel);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.book.assessu.service.PaperMetaDataService#savePaperMetaDataDtoDetails(
	 * java.util.List) save list of metadata...
	 */
	@Override
	public void savePaperMetaDataDtoDetails(List<PaperMetaDataModelDto> paperMetaDataModelDtoList) {

		List<PaperMetaDataModel> paperMetaDataModelList = convertPaperMetaDataModelListFromDto(
				paperMetaDataModelDtoList);

		if (paperMetaDataModelList != null && !paperMetaDataModelList.isEmpty()) {
			for (PaperMetaDataModel paperMetaModel : paperMetaDataModelList) {
				PaperMetaDataModel paperMetada = paperMetaDataRepository.findByPaperType(paperMetaModel.getPaperType());
				if (paperMetada != null) {
					paperMetaModel.setId(paperMetada.getId());
				} else {
					paperMetaModel.setId(counterService.increment(CounterEnum.PAPERMETADATA));
				}
			}
		}
		paperMetaDataRepository.saveAll(paperMetaDataModelList);

	}

	private PaperMetaDataModel convertPaperMetaDataModelFromDto(PaperMetaDataModelDto paperMetaDataModelDto) {
		PaperMetaDataModel paperMetaDataModel = null;
		if (paperMetaDataModelDto != null)
			paperMetaDataModel = new PaperMetaDataModel(paperMetaDataModelDto.getId(),
					paperMetaDataModelDto.getPaperType(), paperMetaDataModelDto.getPaperCategory());
		return paperMetaDataModel;
	}

	private List<PaperMetaDataModel> convertPaperMetaDataModelListFromDto(
			List<PaperMetaDataModelDto> paperMetaDataModelDtoList) {
		List<PaperMetaDataModel> paperMetaDataModelList = new ArrayList<>();

		if (paperMetaDataModelDtoList != null && !paperMetaDataModelDtoList.isEmpty()) {
			paperMetaDataModelDtoList.forEach(paperMetaDataModelDto -> paperMetaDataModelList
					.add(convertPaperMetaDataModelFromDto(paperMetaDataModelDto)));
		}
		return paperMetaDataModelList;
	}

	@Override
	public void savePaperMetaDataDetails(List<PaperMetaDataModel> listPaperMetaDataModel) {

		if (listPaperMetaDataModel != null && !listPaperMetaDataModel.isEmpty()) {
			for (PaperMetaDataModel paperMetaDataModel : listPaperMetaDataModel) {
				paperMetaDataModel.setId(counterService.increment(CounterEnum.PAPERMETADATA));
			}
		}
		paperMetaDataRepository.saveAll(listPaperMetaDataModel);
	}

	@Override
	public PaperMetaDataModel getPaperMetaDataByPaperType(String paperType) {
		return paperMetaDataRepository.findByPaperType(PaperType.valueOf(paperType));
	}

	@Override
	public List<PaperMetaDataModel> getPaperMetaDataByPaperType(String paperType, String paperCategory) {
		return paperMetaDataRepository.findByPaperTypeAndPaperCategory(PaperType.valueOf(paperType),
				PaperCategory.valueOf(paperCategory));
	}


	@Override
	public PaperMetaDataModelDto getPaperMetaDataDto(String paperType) {
		PaperMetaDataModel paperMetaDataModel = paperMetaDataRepository.findByPaperType(PaperType.valueOf(paperType));

		Optional<List<PaperInfoDataModel>> optionalPaperInfoData = paperInfoDataRepository
				.findByPaperTypeAndEnable(PaperType.valueOf(paperType), true);
		return convertModelToPaperMetaDataDto(paperMetaDataModel);
	}

	public PaperMetaDataModelDto convertModelToPaperMetaDataDto(PaperMetaDataModel paperMetaDataModel) {
		if (paperMetaDataModel != null) {
			return new PaperMetaDataModelDto(paperMetaDataModel.getId(), paperMetaDataModel.getPaperType(),
					paperMetaDataModel.getPaperCategory());
		}
		return null;
	}
	
	public PaperMetaDataModelDto convertModelToPaperMetaDataDto(PaperMetaDataModel paperMetaDataModel,List<PaperInfoDataModel> paperInfoMetaDataList) {
		if (paperMetaDataModel != null && paperInfoMetaDataList!=null && !paperInfoMetaDataList.isEmpty()) {
			
			Map<PaperCategory,Integer> paperSubCategoryVsPriority=paperInfoMetaDataList.stream().collect(Collectors.toMap(PaperInfoDataModel::getPaperCategory, PaperInfoDataModel::getPriority));
			
			List<PaperCategoryMetaDataDto> metaDataDto=(List<PaperCategoryMetaDataDto>)paperMetaDataModel.getPaperCategory();
			
			Map<String,List<PaperSubCategoryMetaDataDto>> paperCateVsListPaperSubCategoryMetaData=metaDataDto.stream().collect(Collectors.toMap(PaperCategoryMetaDataDto::getPaperCategory, PaperCategoryMetaDataDto::getpaperSubCategory));

			paperInfoMetaDataList.forEach(paperinfo->{
				if(paperinfo.getPaperCategory()!=null && paperCateVsListPaperSubCategoryMetaData.get(paperinfo.getPaperCategory())!=null) {
					
				}
			});
			return new PaperMetaDataModelDto(paperMetaDataModel.getId(), paperMetaDataModel.getPaperType(),
					paperMetaDataModel.getPaperCategory());
		}
		return null;
	}

	/*
	 * private PaperMetaDataPaperTypeVsPaperDto
	 * getPaperMeateDataDto(List<PaperMetaDataModel> listPaperMetaDataModel, String
	 * paperType) {
	 * 
	 * List<PaperMetaDataPaperCategoryDto> paperCategoryList = new ArrayList<>(); if
	 * (listPaperMetaDataModel != null && !listPaperMetaDataModel.isEmpty()) { for
	 * (PaperMetaDataModel paperMetaDataModel : listPaperMetaDataModel) {
	 * PaperMetaDataPaperCategoryDto paperCategoryDto = new
	 * PaperMetaDataPaperCategoryDto(
	 * paperMetaDataModel.getPaperCategory().getCategory(),
	 * paperMetaDataModel.getPaperSubCategory().toString(),
	 * paperMetaDataModel.getPaperCount(),
	 * paperMetaDataModel.getPaperCountBypaperCategory(),
	 * paperMetaDataModel.getTestType()); paperCategoryList.add(paperCategoryDto); }
	 * } return new PaperMetaDataPaperTypeVsPaperDto(PaperType.valueOf(paperType),
	 * paperCategoryList);
	 * 
	 * }
	 */

}
