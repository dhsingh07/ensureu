package com.book.ensureu.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;

import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.dto.PaperCollectionDto;
import com.book.ensureu.dto.PaperDto;
import com.book.ensureu.model.FreePaper;
import com.book.ensureu.model.FreePaperCollection;
import com.book.ensureu.model.PaidPaper;
import com.book.ensureu.model.PaidPaperCollection;
import com.book.ensureu.model.PracticePaperCollection;

public class PaperConversionUtil {

	/**
	 * @param testPaperDto
	 * @return
	 */
	public static PaidPaper paidPaperDtoToModel(PaperDto testPaperDto) {

		PaidPaper testPaper = null;
		if (testPaperDto != null) {
			testPaper = new PaidPaper();

			testPaper.setUserId(testPaperDto.getUserId());
			testPaper.setTotalScore(testPaperDto.getTotalScore());
			testPaper.setPaperId(testPaperDto.getPaperId());
			testPaper.setStartTestTime(testPaperDto.getStartTestTime());
			testPaper.setEndTestTime(testPaperDto.getEndTestTime());
			testPaper.setPaperName(testPaperDto.getPaperName());

			testPaper.setPrice(testPaperDto.getPrice());
			testPaper.setPayment(testPaperDto.isPayment());
			testPaper.setPaperStatus(testPaperDto.getPaperStatus());
			testPaper.setPaperValidityStartDate(testPaperDto.getPaperValidityStartDate());
			testPaper.setPaperValidityEndDate(testPaperDto.getPaperValidityEndDate());
			testPaper.setCreateDateTime(testPaperDto.getCreateDateTime());
			testPaper.setId(testPaperDto.getId());

			testPaper.setPaperType(testPaperDto.getPaperType());
			testPaper.setPaperCategory(testPaperDto.getPaperCategory());
			testPaper.setPaperSubCategory(testPaperDto.getPaperSubCategory());
			testPaper.setPaperSubCategoryName(testPaperDto.getPaperSubCategoryName());
			testPaper.setTestType(testPaperDto.getTestType());

			testPaper.setTotalAttemptedQuestionCount(testPaperDto.getTotalAttemptedQuestionCount());
			testPaper.setTotalCorrectCount(testPaperDto.getTotalCorrectCount());
			testPaper.setTotalSkipedCount(testPaperDto.getTotalSkipedCount());
			testPaper.setTotalInCorrectCount(testPaperDto.getTotalInCorrectCount());
			testPaper.setTotalScore(testPaperDto.getTotalScore());
			testPaper.setTotalGetScore(testPaperDto.getTotalGetScore());
			testPaper.setTotalTime(testPaperDto.getTotalTime());
			testPaper.setTotalTimeTaken(testPaperDto.getTotalTimeTaken());
			testPaper.setTotalQuestionCount(testPaperDto.getTotalQuestionCount());
			testPaper.setNegativeMarks(testPaperDto.getNegativeMarks());
			testPaper.setPerQuestionScore(testPaperDto.getPerQuestionScore());

			PaidPaperCollection testPaperCollection = new PaidPaperCollection();
			PaperCollectionDto testPaperCollectionDto = testPaperDto.getPaper();
			if(testPaperCollectionDto!=null) {
			testPaperCollection.setId(testPaperCollectionDto.getId());
			testPaperCollection.setPaperName(testPaperCollectionDto.getPaperName());
			PaperSubCategory paperSubCotag = testPaperCollectionDto.getPaperSubCategory();
			testPaperCollection.setPaperSubCategory(paperSubCotag);
			testPaperCollection.setPaperSubCategoryName(paperSubCotag.toString());
			testPaperCollection.setTestType(testPaperCollectionDto.getTestType());
			testPaperCollection.setPaperType(testPaperCollectionDto.getPaperType());
			
			testPaperCollection.setTotalQuestionCount(testPaperCollectionDto.getTotalQuestionCount());
			testPaperCollection.setNegativeMarks(testPaperCollectionDto.getNegativeMarks());
			testPaperCollection.setPerQuestionScore(testPaperCollectionDto.getPerQuestionScore());
			testPaperCollection.setTotalQuestionCount(testPaperCollectionDto.getTotalQuestionCount());
			testPaperCollection.setTotalScore(testPaperCollectionDto.getTotalScore());
			testPaperCollection.setTotalTime(testPaperCollectionDto.getTotalTime());
			testPaperCollection.setTaken(testPaperCollectionDto.isTaken());
			testPaperCollection.setTotalTakenTime(testPaperCollectionDto.getTotalTakenTime());
			testPaperCollection.setTotalGetScore(testPaperCollectionDto.getTotalGetScore());
			
			testPaperCollection.setPattern(testPaperCollectionDto.getPattern());
			testPaper.setPaper(testPaperCollection);
			}
		}
		return testPaper;

	}

	public static PaperDto paidPaperToDto(PaidPaper testPaper) {

		PaperDto testPaperDto = null;
		if (testPaper != null) {
			testPaperDto = new PaperDto();
			testPaperDto.setUserId(testPaper.getUserId());
			testPaperDto.setTotalScore(testPaper.getTotalScore());
			testPaperDto.setPaperId(testPaper.getPaperId());
			testPaperDto.setStartTestTime(testPaper.getStartTestTime());
			testPaperDto.setEndTestTime(testPaper.getEndTestTime());
			testPaperDto.setPaperName(testPaper.getPaperName());

			testPaperDto.setPrice(testPaper.getPrice());
			testPaperDto.setPayment(testPaper.isPayment());
			testPaperDto.setPaperStatus(testPaper.getPaperStatus());
			testPaperDto.setPaperValidityStartDate(testPaper.getPaperValidityStartDate());
			testPaperDto.setPaperValidityEndDate(testPaper.getPaperValidityEndDate());
			testPaperDto.setCreateDateTime(testPaper.getCreateDateTime());
			testPaperDto.setId(testPaper.getId());

			testPaperDto.setPaperType(testPaper.getPaperType());
			testPaperDto.setPaperCategory(testPaper.getPaperCategory());
			testPaperDto.setPaperSubCategory(testPaper.getPaperSubCategory());
			testPaperDto.setPaperSubCategoryName(testPaper.getPaperSubCategoryName());
			testPaperDto.setTestType(testPaper.getTestType());

			testPaperDto.setTotalAttemptedQuestionCount(testPaper.getTotalAttemptedQuestionCount());
			testPaperDto.setTotalCorrectCount(testPaper.getTotalCorrectCount());
			testPaperDto.setTotalSkipedCount(testPaper.getTotalSkipedCount());
			testPaperDto.setTotalInCorrectCount(testPaper.getTotalInCorrectCount());
			testPaperDto.setTotalScore(testPaper.getTotalScore());
			testPaperDto.setTotalGetScore(testPaper.getTotalGetScore());
			testPaperDto.setTotalTime(testPaper.getTotalTime());
			testPaperDto.setTotalTimeTaken(testPaper.getTotalTimeTaken());
			testPaperDto.setTotalQuestionCount(testPaper.getTotalQuestionCount());
			testPaperDto.setNegativeMarks(testPaper.getNegativeMarks());
			testPaperDto.setPerQuestionScore(testPaper.getPerQuestionScore());

			PaperCollectionDto paperCollectionDto = new PaperCollectionDto();
			PaidPaperCollection testPaperCollection = testPaper.getPaper();
			if(testPaperCollection!=null) {
			paperCollectionDto.setId(testPaperCollection.getId());
			paperCollectionDto.setPaperName(testPaperCollection.getPaperName());
			PaperSubCategory paperSubCotag = testPaperCollection.getPaperSubCategory();
			paperCollectionDto.setPaperSubCategory(paperSubCotag);
			paperCollectionDto.setPaperSubCategoryName(paperSubCotag.toString());
			paperCollectionDto.setTestType(testPaperCollection.getTestType());
			paperCollectionDto.setPaperType(testPaperCollection.getPaperType());
			
			paperCollectionDto.setTotalQuestionCount(testPaperCollection.getTotalQuestionCount());
			paperCollectionDto.setNegativeMarks(testPaperCollection.getNegativeMarks());
			paperCollectionDto.setPerQuestionScore(testPaperCollection.getPerQuestionScore());
			paperCollectionDto.setTotalQuestionCount(testPaperCollection.getTotalQuestionCount());
			paperCollectionDto.setTotalScore(testPaperCollection.getTotalScore());
			paperCollectionDto.setTotalTime(testPaperCollection.getTotalTime());
			paperCollectionDto.setTaken(testPaperCollection.isTaken());
			paperCollectionDto.setTotalTakenTime(testPaperCollection.getTotalTakenTime());
			paperCollectionDto.setTotalGetScore(testPaperCollection.getTotalGetScore());
			
			paperCollectionDto.setPattern(testPaperCollection.getPattern());
			testPaperDto.setPaper(paperCollectionDto);
			}
			
		}
		return testPaperDto;
	}

	
	public static FreePaper freePaperDtoToModel(PaperDto testPaperDto) {

		FreePaper freePaper = null;
		if (testPaperDto != null) {
			freePaper = new FreePaper();

			freePaper.setUserId(testPaperDto.getUserId());
			freePaper.setPaperId(testPaperDto.getPaperId());
			freePaper.setStartTestTime(testPaperDto.getStartTestTime());
			freePaper.setEndTestTime(testPaperDto.getEndTestTime());
			freePaper.setPaperName(testPaperDto.getPaperName());

			//freePaper.setPrice(testPaperDto.getPrice());
			//freePaper.setPayment(testPaperDto.isPayment());
			freePaper.setPaperStatus(testPaperDto.getPaperStatus());
			freePaper.setPaperValidityStartDate(testPaperDto.getPaperValidityStartDate());
			freePaper.setPaperValidityEndDate(testPaperDto.getPaperValidityEndDate());
			freePaper.setCreateDateTime(testPaperDto.getCreateDateTime());
			freePaper.setId(testPaperDto.getId());

			freePaper.setPaperType(testPaperDto.getPaperType());
			freePaper.setPaperCategory(testPaperDto.getPaperCategory());
			freePaper.setPaperSubCategory(testPaperDto.getPaperSubCategory());
			freePaper.setPaperSubCategoryName(testPaperDto.getPaperSubCategoryName());
			freePaper.setTestType(testPaperDto.getTestType());

			freePaper.setTotalAttemptedQuestionCount(testPaperDto.getTotalAttemptedQuestionCount());
			freePaper.setTotalCorrectCount(testPaperDto.getTotalCorrectCount());
			freePaper.setTotalSkipedCount(testPaperDto.getTotalSkipedCount());
			freePaper.setTotalInCorrectCount(testPaperDto.getTotalInCorrectCount());
			freePaper.setTotalGetScore(testPaperDto.getTotalGetScore());
			freePaper.setTotalScore(testPaperDto.getTotalScore());
			freePaper.setTotalTime(testPaperDto.getTotalTime());
			freePaper.setTotalTimeTaken(testPaperDto.getTotalTimeTaken());
			freePaper.setTotalQuestionCount(testPaperDto.getTotalQuestionCount());
			freePaper.setNegativeMarks(testPaperDto.getNegativeMarks());
			freePaper.setPerQuestionScore(testPaperDto.getPerQuestionScore());

			FreePaperCollection freePaperCollection = new FreePaperCollection();
			PaperCollectionDto testPaperCollectionDto = testPaperDto.getPaper();
			if(testPaperCollectionDto!=null) {
			freePaperCollection.setId(testPaperCollectionDto.getId());
			freePaperCollection.setPaperName(testPaperCollectionDto.getPaperName());
			PaperSubCategory paperSubCotag = testPaperCollectionDto.getPaperSubCategory();
			freePaperCollection.setPaperSubCategory(paperSubCotag);
			freePaperCollection.setPaperSubCategoryName(paperSubCotag.toString());
			freePaperCollection.setTestType(testPaperCollectionDto.getTestType());
			freePaperCollection.setPaperType(testPaperCollectionDto.getPaperType());
			
			freePaperCollection.setTotalQuestionCount(testPaperCollectionDto.getTotalQuestionCount());
			freePaperCollection.setNegativeMarks(testPaperCollectionDto.getNegativeMarks());
			freePaperCollection.setPerQuestionScore(testPaperCollectionDto.getPerQuestionScore());
			freePaperCollection.setTotalQuestionCount(testPaperCollectionDto.getTotalQuestionCount());
			freePaperCollection.setTotalScore(testPaperCollectionDto.getTotalScore());
			freePaperCollection.setTotalTime(testPaperCollectionDto.getTotalTime());
			freePaperCollection.setTaken(testPaperCollectionDto.isTaken());
			freePaperCollection.setTotalTakenTime(testPaperCollectionDto.getTotalTakenTime());
			freePaperCollection.setTotalGetScore(testPaperCollectionDto.getTotalGetScore());
			
			freePaperCollection.setPattern(testPaperCollectionDto.getPattern());
			freePaper.setPaper(freePaperCollection);
			}
		}
		return freePaper;

	}
	
	public static PaperDto freePaperToDto(FreePaper freePaper) {

		PaperDto testPaperDto = null;
		if (freePaper != null) {
			testPaperDto = new PaperDto();
			testPaperDto.setUserId(freePaper.getUserId());
			testPaperDto.setPaperId(freePaper.getPaperId());
			testPaperDto.setStartTestTime(freePaper.getStartTestTime());
			testPaperDto.setEndTestTime(freePaper.getEndTestTime());
			testPaperDto.setPaperName(freePaper.getPaperName());

			//testPaperDto.setPrice(freePaper.getPrice());
			//testPaperDto.setPayment(freePaper.isPayment());
			testPaperDto.setPaperStatus(freePaper.getPaperStatus());
			testPaperDto.setPaperValidityStartDate(freePaper.getPaperValidityStartDate());
			testPaperDto.setPaperValidityEndDate(freePaper.getPaperValidityEndDate());
			testPaperDto.setCreateDateTime(freePaper.getCreateDateTime());
			testPaperDto.setId(freePaper.getId());

			testPaperDto.setPaperType(freePaper.getPaperType());
			testPaperDto.setPaperCategory(freePaper.getPaperCategory());
			testPaperDto.setPaperSubCategory(freePaper.getPaperSubCategory());
			testPaperDto.setPaperSubCategoryName(freePaper.getPaperSubCategoryName());
			testPaperDto.setTestType(freePaper.getTestType());

			testPaperDto.setTotalAttemptedQuestionCount(freePaper.getTotalAttemptedQuestionCount());
			testPaperDto.setNegativeMarks(freePaper.getNegativeMarks());
			testPaperDto.setTotalCorrectCount(freePaper.getTotalCorrectCount());
			testPaperDto.setTotalSkipedCount(freePaper.getTotalSkipedCount());
			testPaperDto.setTotalInCorrectCount(freePaper.getTotalInCorrectCount());
			testPaperDto.setTotalScore(freePaper.getTotalScore());
			testPaperDto.setTotalGetScore(freePaper.getTotalGetScore());
			testPaperDto.setTotalTimeTaken(freePaper.getTotalTimeTaken());
			testPaperDto.setTotalTime(freePaper.getTotalTime());
			testPaperDto.setPerQuestionScore(freePaper.getPerQuestionScore());
			testPaperDto.setTotalQuestionCount(freePaper.getTotalQuestionCount());

			PaperCollectionDto paperCollectionDto = new PaperCollectionDto();
			FreePaperCollection freePaperCollection = freePaper.getPaper();
			if(freePaperCollection!=null) {
			paperCollectionDto.setId(freePaperCollection.getId());
			paperCollectionDto.setPaperName(freePaperCollection.getPaperName());
			PaperSubCategory paperSubCotag = freePaperCollection.getPaperSubCategory();
			paperCollectionDto.setPaperCategory(freePaperCollection.getPaperCategory());
			paperCollectionDto.setPaperSubCategory(paperSubCotag);
			paperCollectionDto.setPaperSubCategoryName(paperSubCotag.toString());
			paperCollectionDto.setTestType(freePaperCollection.getTestType());
			paperCollectionDto.setPaperType(freePaperCollection.getPaperType());
			
			paperCollectionDto.setTotalQuestionCount(freePaperCollection.getTotalQuestionCount());
			paperCollectionDto.setNegativeMarks(freePaperCollection.getNegativeMarks());
			paperCollectionDto.setPerQuestionScore(freePaperCollection.getPerQuestionScore());
			paperCollectionDto.setTotalQuestionCount(freePaperCollection.getTotalQuestionCount());
			paperCollectionDto.setTotalScore(freePaperCollection.getTotalScore());
			paperCollectionDto.setTotalTime(freePaperCollection.getTotalTime());
			paperCollectionDto.setTaken(freePaperCollection.isTaken());
			paperCollectionDto.setTotalTakenTime(freePaperCollection.getTotalTakenTime());
			paperCollectionDto.setTotalGetScore(freePaperCollection.getTotalGetScore());
			
			
			paperCollectionDto.setPattern(freePaperCollection.getPattern());
			testPaperDto.setPaper(paperCollectionDto);
			}
		}
		return testPaperDto;
	}
	
	public static List<PaperDto> paidPaperToDto(List<PaidPaper> testPaperList) {
		return paidPaperToDto(testPaperList,null);
	}
	
	
	public static List<PaperDto> paidPaperToDto(List<PaidPaper> testPaperList, Map<String,Double> paperIdVsPercentile ) {
		List<PaperDto> paperTestDtoList = null;
		if (testPaperList != null && !testPaperList.isEmpty()) {
			paperTestDtoList = new ArrayList<PaperDto>();
			for (PaidPaper testPaper : testPaperList) {
				PaperDto testP = paidPaperToDto(testPaper);
				if(paperIdVsPercentile!=null) {
					testP.setPercentile(paperIdVsPercentile.get(testP.getPaperId()));
				}
				paperTestDtoList.add(testP);
			}
		}
		return paperTestDtoList;

	}
	
	
	public static List<PaperDto> freePaperToDto(List<FreePaper> freePaperList) {
		return freePaperToDto(freePaperList, null);
	}
	
	public static List<PaperDto> freePaperToDto(List<FreePaper> freePaperList,Map<String,Double> paperIdVsPercentile ) {
		List<PaperDto> paperTestDtoList = null;
		if (freePaperList != null && !freePaperList.isEmpty()) {
			paperTestDtoList = new ArrayList<PaperDto>();
			for (FreePaper freePaper : freePaperList) {
				PaperDto testP = freePaperToDto(freePaper);
				if(paperIdVsPercentile!=null) {
					testP.setPercentile(paperIdVsPercentile.get(testP.getPaperId()));
				}
				paperTestDtoList.add(testP);
			}
		}
		return paperTestDtoList;
	}
	
	public static List<PaperDto> practicePaperToDtos(List<PracticePaperCollection> collectionList){
		List<PaperDto> paperTestDtoList = null;
		if(collectionList!=null && !collectionList.isEmpty()){
			paperTestDtoList = new ArrayList<PaperDto>();
			for(PracticePaperCollection obj :collectionList){
				PaperDto testP = practicePaperToDto(obj);
				paperTestDtoList.add(testP);
			}
		}
		return paperTestDtoList;
	}
	
	public static PaperDto practicePaperToDto(PracticePaperCollection practicePaper) {
		PaperDto practicePaperDto = null;
		if (practicePaper != null) {
			practicePaperDto = new PaperDto();
			PaperCollectionDto paperCollectionDto = new PaperCollectionDto();
			if(paperCollectionDto!=null) {
				BeanUtils.copyProperties(practicePaper, paperCollectionDto);
				practicePaperDto.setPaper(paperCollectionDto);
			}
		}
		return practicePaperDto;
	}
	
	public static PracticePaperCollection practicePaperDtoToModel(PaperDto practiceDto) {
		PracticePaperCollection practicePaperColl = null;
		if (practiceDto != null) {
			practicePaperColl = new PracticePaperCollection();
			if (practiceDto != null) {
				BeanUtils.copyProperties(practiceDto.getPaper(),
						practicePaperColl);
			}
		}
		return practicePaperColl;
	}
}
